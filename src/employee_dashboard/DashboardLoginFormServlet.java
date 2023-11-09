package employee_dashboard;

import com.google.gson.JsonObject;

import employee_dashboard.services.DashboardLoginFormService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main_fablix.services.LoginFormService;
import utils.RecaptchaVerifyUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "dashboard.DashboardLoginFormServlet", urlPatterns = "/_dashboard/api/login")
public class DashboardLoginFormServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // return if user is logged in

        try {
            response.setContentType("application/json"); // Response mime type
            PrintWriter out = response.getWriter();
            HttpSession session = request.getSession(true);
            String sessionEmployeeEmail = (String) session.getAttribute("employeeEmail");

            if (sessionEmployeeEmail != null) {
                JsonObject responseJsonObject = new JsonObject();

                responseJsonObject.addProperty("isLoggedIn", true);
                responseJsonObject.addProperty("employeeEmail", sessionEmployeeEmail);
                responseJsonObject.addProperty("message", "Employee Already Login");
                out.println(responseJsonObject.toString());
                response.setStatus(200);
            } else {
                JsonObject responseJsonObject = new JsonObject();

                responseJsonObject.addProperty("isLoggedIn", false);
                responseJsonObject.addProperty("employeeEmail", (String) null);
                responseJsonObject.addProperty("message", "Employee not logged in");
                out.println(responseJsonObject.toString());
                response.setStatus(200);
            }
        } catch (Exception e) {
            System.out.println("Got error: " + e);
            response.setStatus(500);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type
        String gRecaptchaResponse = request.getParameter("reCaptchaToken");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        PrintWriter out = response.getWriter();

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {

            System.out.println("Got recaptcha error: " + e);

            JsonObject responseJsonObject = new JsonObject();

            responseJsonObject.addProperty("success", false);
            responseJsonObject.addProperty("employeeEmail", (String) null);
            responseJsonObject.addProperty("reason", "recaptcha verification error");
            responseJsonObject.addProperty("message", "Got error: " + e);
            out.println(responseJsonObject.toString());
            response.setStatus(498);

            out.close();
            return;
        }

        // get email and password
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // check if username and password are valid from mysql

        try {
            HttpSession session = request.getSession(true);
            String sessionEmployeeEmail = (String) session.getAttribute("employeeEmail");

            if (sessionEmployeeEmail == null) {
                String employeeEmail = DashboardLoginFormService.verifyCredentials(email, password, dataSource);

                System.out.println("employeeEmail: " + employeeEmail);

                if ("-1".equals(employeeEmail)) {
                    // invalid login
                    // for now just print out
                    System.out.println("Invalid email");
                    JsonObject responseJsonObject = new JsonObject();

                    responseJsonObject.addProperty("success", false);
                    responseJsonObject.addProperty("employeeEmail", (Number) null);
                    responseJsonObject.addProperty("reason", "email");
                    responseJsonObject.addProperty("message", "Email does not exist");
                    out.println(responseJsonObject.toString());
                    response.setStatus(401);

                } else if ("-2".equals(employeeEmail)) {
                    System.out.println("Invalid Password");
                    JsonObject responseJsonObject = new JsonObject();

                    responseJsonObject.addProperty("success", false);
                    responseJsonObject.addProperty("employeeEmail", (Number) null);
                    responseJsonObject.addProperty("reason", "password");
                    responseJsonObject.addProperty("message", "Password does not exist");
                    out.println(responseJsonObject.toString());
                    response.setStatus(401);
                } else {

                    System.out.println("Valid login");
                    JsonObject responseJsonObject = new JsonObject();

                    responseJsonObject.addProperty("success", true);
                    responseJsonObject.addProperty("employeeEmail", employeeEmail);
                    responseJsonObject.addProperty("message", "Sucessfully Login");
                    out.println(responseJsonObject.toString());
                    response.setStatus(200);
                    session.setAttribute("employeeEmail", employeeEmail);
                    session.setAttribute("customerId", -999);

                }

            } else {
                // already login
                // for now just print out
                System.out.println("Employee already login");

                JsonObject responseJsonObject = new JsonObject();

                responseJsonObject.addProperty("success", false);
                responseJsonObject.addProperty("employeeEmail", (String) null);
                responseJsonObject.addProperty("reason", "already");
                responseJsonObject.addProperty("message", "User Already Login");
                out.println(responseJsonObject.toString());
                response.setStatus(200);

            }
        } catch (Exception e) {
            // for now just print out
            // return a fail response
            System.out.println("Got error: " + e);

            JsonObject responseJsonObject = new JsonObject();

            responseJsonObject.addProperty("success", false);
            responseJsonObject.addProperty("employeeEmail", (String) null);
            responseJsonObject.addProperty("reason", "error");
            responseJsonObject.addProperty("message", "Got error: " + e);
            out.println(responseJsonObject.toString());
            response.setStatus(500);

        } finally {
            out.close();
        }
    }
}
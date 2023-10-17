import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.LoginFormService;
import services.MoviesService;
import utils.ServletUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "LoginFormServlet", urlPatterns = "/api/login")
public class LoginFormServlet extends HttpServlet {
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
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String email = request.getParameter("email");
//        String password = request.getParameter("password");
//        System.out.println("email: " + email);
//        System.out.println("password: " + password);
//        response.setContentType("application/json"); // Response mime type
//        PrintWriter out = response.getWriter();
//
//        try {
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("email", email);
//            jsonObject.addProperty("password", password);
//
////            JsonObject jsonObject = service.login(dataSource, username, password);
//            out.write(jsonObject.toString());
//            response.setStatus(200);
//        } catch (Exception e) {
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("errorMessage", e.getMessage());
//            out.write(jsonObject.toString());
//            response.setStatus(500);
//        } finally {
//            out.close();
//        }

        response.setContentType("application/json"); // Response mime type
        PrintWriter out = response.getWriter();

        //get email and password
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        //check if username and password are valid from mysql


        try{
            HttpSession session = request.getSession(true);
            Integer sessionCustomerId = (Integer) session.getAttribute("customerId");

            if (sessionCustomerId == null) {
                Integer customerId = LoginFormService.validateEmailPassword(dataSource, email, password);

                System.out.println("customer: " + customerId);

                if (customerId == null) {
                    //invalid login
                    //for now just print out
                    System.out.println("Invalid login");
                    JsonObject responseJsonObject = new JsonObject();

                    responseJsonObject.addProperty("success", false);
                    responseJsonObject.addProperty("customerId", customerId);
                    responseJsonObject.addProperty("message", "Invalid email or password");
                    out.println(responseJsonObject.toString());
                    response.setStatus(401);

                }
                else{
                    //valid login
                    //for now just print out



                    System.out.println("Valid login");
                    JsonObject responseJsonObject = new JsonObject();

                    responseJsonObject.addProperty("success", true);
                    responseJsonObject.addProperty("customerId", customerId);
                    responseJsonObject.addProperty("message", "Sucessfully Login");
                    out.println(responseJsonObject.toString());
                    response.setStatus(200);

                }

                session.setAttribute("customerId", customerId);

                return;
                //set up new session for customer
//            request.getSession().setAttribute("customer", customer);


            }

            else{
                //already login
                //for now just print out
                System.out.println("Customer already login");

                JsonObject responseJsonObject = new JsonObject();

                responseJsonObject.addProperty("success", false);
                responseJsonObject.addProperty("customerId", sessionCustomerId);
                responseJsonObject.addProperty("message", "User Already Login");
                out.println(responseJsonObject.toString());
                response.setStatus(200);

            }
        } catch (Exception e) {
            //for now just print out
            System.out.println("Got error: " + e);

        }


        response.sendRedirect(ServletUtils.getBaseUrl(request) + "/index.html");

    }
}

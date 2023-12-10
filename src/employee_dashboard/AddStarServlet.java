package employee_dashboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import employee_dashboard.services.AddStarService;
import employee_dashboard.services.MetaDataService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

import static utils.ServletUtils.checkLoginEmployee;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "AddStarServlet", urlPatterns = "/_dashboard/api/addStar")
public class AddStarServlet extends HttpServlet {
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
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!checkLoginEmployee(request, response)) {
            return;
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("name");
        String birthYear = request.getParameter("birthYear");

        try {
            String result = AddStarService.addStar( name, birthYear);

            if (!result.isEmpty()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("success", true);
                jsonObject.addProperty("message", "Successfully added star " + name);
                jsonObject.addProperty("starId", result);
                out.write(jsonObject.toString());
                response.setStatus(201);
            } else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("success", false);
                jsonObject.addProperty("errorMessage", "Failed to add star " + name);
                out.write(jsonObject.toString());
                response.setStatus(202);
            }
        } catch (Exception e) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}

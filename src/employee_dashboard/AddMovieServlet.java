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
import employee_dashboard.services.AddMovieService;
import employee_dashboard.services.AddStarService;
import employee_dashboard.services.MetaDataService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

import static utils.ServletUtils.checkLogin;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/addMovie")
public class AddMovieServlet extends HttpServlet {
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

        if (!checkLogin(request, response)) {
            return;
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String movieTitle = request.getParameter("title");
        String movieYear = request.getParameter("year");
        String movieDirector = request.getParameter("director");
        String starName = request.getParameter("starName");
        String starBirthYear = request.getParameter("starBirthYear");
        String genreName = request.getParameter("genreName");

        System.out.println("movieTitle: " + movieTitle);
        System.out.println("movieYear: " + movieYear);
        System.out.println("movieDirector: " + movieDirector);
        System.out.println("starName: " + starName);
        System.out.println("starBirthYear: " + starBirthYear);
        System.out.println("genreName: " + genreName);

        try {
            int result = AddMovieService.addMovie(dataSource, movieTitle, movieYear, movieDirector, starName,
                    starBirthYear, genreName);

            if (result == 0) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("success", true);
                jsonObject.addProperty("message", "Successfully added movie " + movieTitle);
                out.write(jsonObject.toString());
                response.setStatus(201);
            } else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("success", false);
                jsonObject.addProperty("errorMessage", "Failed to add movie " + movieTitle);
                out.write(jsonObject.toString());
                response.setStatus(202);
            }
        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}

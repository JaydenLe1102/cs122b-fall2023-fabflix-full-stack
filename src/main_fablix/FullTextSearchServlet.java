package main_fablix;

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
import main_fablix.services.FullTextSearchService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;

import static utils.ServletUtils.checkLogin;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "FullTextSearchServlet", urlPatterns = "/api/full-search")
public class FullTextSearchServlet extends HttpServlet {
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
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!checkLogin(request, response)) {
            return;
        }

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        long servletStartTime = System.nanoTime(); // Start time for the servlet

        // Get a connection from dataSource and let resource manager close the
        // connection after usage.
        try {

            String movie_query = request.getParameter("movie_query");
            System.out.println(movie_query);
            Integer page_number = Integer.parseInt(request.getParameter("page_number"));
            Integer page_size = Integer.parseInt(request.getParameter("page_size"));
            Integer sort_option = Integer.parseInt(request.getParameter("sort_option"));

            long jdbcStartTime = System.nanoTime(); // Start time for JDBC execution
            JsonArray jsonArray = FullTextSearchService.getMovieListByQuery(movie_query, page_number, page_size, sort_option);

            long jdbcEndTime = System.nanoTime(); // End time for JDBC execution
            long jdbcElapsedTime = jdbcEndTime - jdbcStartTime;

            // Log JDBC execution time
            logToConsole("JDBC execution time: " + jdbcElapsedTime + " ns");

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            HttpSession session = request.getSession(true);

            session.setAttribute("page_number", page_number);
            session.setAttribute("page_size", page_size);
            session.setAttribute("sort_option", sort_option);
            session.setAttribute("movie_query", movie_query);
            session.setAttribute("isSearch", false);
            session.setAttribute("isBrowsed", false);
            session.setAttribute("isFullSearch", true);

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            long servletEndTime = System.nanoTime(); // End time for the servlet
            long servletElapsedTime = servletEndTime - servletStartTime;

            // Log total execution time
            logToConsole("Search servlet total execution time: " + servletElapsedTime + " ns");
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by
        // try-with-resources

    }

    private void logToConsole(String message) {
        // Log message to console
        System.out.println(message);

        // Log message to file (optional)
        logToFile(message);
    }

    private void logToFile(String message) {
        try {
            // Specify the file path
            String filePath = "~/code/2023-fall-cs122b-bobaholic/src/logs/logfile.txt";

            // Create or append to the log file
            FileWriter fileWriter = new FileWriter(filePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Write the message to the file
            bufferedWriter.write(message);
            bufferedWriter.newLine();

            // Close the file writer
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


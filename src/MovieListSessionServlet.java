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
import services.BrowseByGenreService;
import services.BrowseByTitleService;
import services.MoviesService;
import services.SearchService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieListSessionServlet", urlPatterns = "/api/movielist")
public class MovieListSessionServlet extends HttpServlet {
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

		response.setContentType("application/json"); // Response mime type

		PrintWriter out = response.getWriter();

		try {

			HttpSession session = request.getSession(true);

			Boolean isBrowsed = (Boolean) session.getAttribute("isBrowsed");
			Boolean isSearch = (Boolean) session.getAttribute("isSearch");

			Integer page_number = (Integer) session.getAttribute("page_number") != null
					? (Integer) session.getAttribute("page_number")
					: 1;
			Integer page_size = (Integer) session.getAttribute("page_size") != null
					? (Integer) session.getAttribute("page_size")
					: 10;

			Integer sort_option = (Integer) session.getAttribute("sort_option") != null
					? (Integer) session.getAttribute("sort_option")
					: 0;

			System.out.println("page_number: " + page_number);
			System.out.println("page_size: " + page_size);
			System.out.println("isBrowsed: " + isBrowsed);
			System.out.println("isSearch: " + isSearch);

			JsonObject responseJsonObject = new JsonObject();

			responseJsonObject.addProperty("page_number", page_number);
			responseJsonObject.addProperty("page_size", page_size);
			responseJsonObject.addProperty("sort_option", sort_option);
			responseJsonObject.addProperty("isBrowsed", isBrowsed);
			responseJsonObject.addProperty("isSearch", isSearch);

			responseJsonObject.addProperty("browse_genre", (String) session.getAttribute("browse_genre"));
			responseJsonObject.addProperty("browse_title", (String) session.getAttribute("browse_title"));

			responseJsonObject.addProperty("search_title", (String) session.getAttribute("search_title"));
			responseJsonObject.addProperty("search_year", (String) session.getAttribute("search_year"));
			responseJsonObject.addProperty("search_director", (String) session.getAttribute("search_director"));
			responseJsonObject.addProperty("search_star", (String) session.getAttribute("search_star"));

			out.write(responseJsonObject.toString());
			response.setStatus(200);

		} catch (Exception e) {

			// Write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// Set response status to 500 (Internal Server Error)
			response.setStatus(500);
		} finally {
			out.close();
		}

		// Always remember to close db connection after usage. Here it's done by
		// try-with-resources

	}
}

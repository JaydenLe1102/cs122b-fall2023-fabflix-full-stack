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
import services.MoviesService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "BrowseByGenreServlet", urlPatterns = "/api/browse/genre")
public class BrowseByGenreServlet extends HttpServlet {
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

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		// Get a connection from dataSource and let resource manager close the
		// connection after usage.
		try {

			String browse_genre = request.getParameter("genre");
			Integer page_number = Integer.parseInt(request.getParameter("page_number"));
			Integer page_size = Integer.parseInt(request.getParameter("page_size"));
			Integer sort_option = Integer.parseInt(request.getParameter("sort_option"));

			JsonArray jsonArray = BrowseByGenreService.getMovieListByGenre(dataSource, browse_genre, page_number, page_size,
					sort_option);

			request.getServletContext().log("getting " + jsonArray.size() + " results");

			HttpSession session = request.getSession(true);

			session.setAttribute("page_number", page_number);
			session.setAttribute("page_size", page_size);
			session.setAttribute("sort_option", sort_option);
			session.setAttribute("browse_genre", browse_genre);
			session.setAttribute("isBrowsed", true);
			session.setAttribute("isSearch", false);

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
			out.close();
		}

		// Always remember to close db connection after usage. Here it's done by
		// try-with-resources

	}
}

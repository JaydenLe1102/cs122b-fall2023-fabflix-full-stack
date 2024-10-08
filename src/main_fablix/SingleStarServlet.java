package main_fablix;

import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main_fablix.services.SingleStarService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import static utils.ServletUtils.checkLogin;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
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

		// Retrieve parameter id from url request.
		String id = request.getParameter("id");

		// The log message can be found in localhost log
		request.getServletContext().log("getting id: " + id);

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		// Get a connection from dataSource and let resource manager close the
		// connection after usage.
		try  {
			// Get a connection from dataSource

			JsonObject starObject = SingleStarService.getSingleStarById(id);

			// Write JSON string to output
			out.write(starObject.toString());
			// Set response status to 200 (OK)
			response.setStatus(200);

		} catch (Exception e) {
			// Write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// Log error to localhost log
			request.getServletContext().log("Error:", e);
			// Set response status to 500 (Internal Server Error)
			response.setStatus(500);
		} finally {
			out.close();
		}

		// Always remember to close db connection after usage. Here it's done by
		// try-with-resources

	}

}

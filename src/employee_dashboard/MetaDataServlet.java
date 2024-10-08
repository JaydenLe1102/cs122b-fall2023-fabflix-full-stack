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
import employee_dashboard.services.MetaDataService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

import static utils.ServletUtils.checkLoginEmployee;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MetaDataServlet", urlPatterns = "/_dashboard/api/metadata")
public class MetaDataServlet extends HttpServlet {
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
		if (!checkLoginEmployee(request, response)) {
			return;
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		try {
			JsonArray jsonArray = MetaDataService.getMetaData();

			request.getServletContext().log("getting " + jsonArray.size() + " results");

			out.write(jsonArray.toString());
			response.setStatus(200);

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

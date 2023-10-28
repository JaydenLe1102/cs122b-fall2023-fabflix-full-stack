import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.SingleMovieService;
import services.PaymentService;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
	private DataSource dataSource;

	public void init(ServletConfig config) {
		try {
			dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");

		String customerId = request.getParameter("customerID");
		String ccId = request.getParameter("creditCardNumber");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String expiration = request.getParameter("expirationDate");
		System.out.println(customerId);
		System.out.println(ccId);
		System.out.println(firstName);
		System.out.println(lastName);
		System.out.println(expiration);
		PrintWriter out = response.getWriter();

		try (Connection conn = dataSource.getConnection()) {
			System.out.println("In payment servlet");
			JsonObject paymentResponse = PaymentService.processPayment(dataSource, request, customerId, ccId, firstName,
					lastName, expiration);
			out.write(paymentResponse.toString());
			response.setStatus(200);
		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			request.getServletContext().log("Error:", e);
			response.setStatus(500);
		} finally {
			out.close();
		}
	}
}
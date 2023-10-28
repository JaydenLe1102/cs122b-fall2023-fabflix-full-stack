package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PaymentService {

    public static JsonObject processPayment(DataSource dataSource, HttpServletRequest request, String customerId, String ccId, String firstName, String lastName, String expiration) {
        JsonObject response = new JsonObject();
        boolean isValid = false;

        try (Connection connection = dataSource.getConnection()) {
            isValid = validateCreditCardDetails(connection, ccId, firstName, lastName, expiration);
            System.out.println(isValid);
        } catch (SQLException e) {
            response.addProperty("error", "Database connection error.");
            return response;
        }

        if (isValid) {
            try (Connection connection = dataSource.getConnection()) {
                ArrayList<String> previousItems = (ArrayList<String>) request.getSession().getAttribute("previousItems");
                System.out.println(previousItems);
                if (previousItems != null && previousItems.size() > 0) {
                    boolean allSalesRecorded = true;

                    for (String movie : previousItems) {
                        System.out.println("In for loop");
                        String movieId = Random3Service.getMovieIdByMovieTitle(dataSource, movie);
                        System.out.println(movieId);
                        boolean saleRecorded = recordSale(connection, customerId, movieId);

                        if (!saleRecorded) {
                            allSalesRecorded = false;
                            break; // Exit the loop if any sale recording fails
                        }
                    }

                    if (allSalesRecorded) {
                        System.out.println("All sales have been recorded");
                        response.addProperty("success", true);
                        response.addProperty("message", "Transaction successful!");
                    } else {
                        response.addProperty("error", "Failed to record sale(s). Please try again.");
                    }
                } else {
                    response.addProperty("error", "No movies in the shopping list.");
                }
            } catch (Exception e) {
                response.addProperty("error", "Error recording sale(s).");
                return response;
            }
        } else {
            response.addProperty("error", "Invalid credit card information.");
        }
        return response;
    }

    private static boolean validateCreditCardDetails(Connection conn, String ccId, String firstName, String lastName, String expiration) {
        String query = "SELECT * FROM creditcards WHERE id = ? AND firstName = ? AND lastName = ? AND expiration = ?";

        System.out.println(ccId);
        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(expiration);
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, ccId);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expiration);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean recordSale(Connection conn, String customerId, String movieId) {
        String insertQuery = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, CURDATE())";
        try (PreparedStatement statement = conn.prepareStatement(insertQuery)) {
            statement.setString(1, customerId);
            statement.setString(2, movieId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}




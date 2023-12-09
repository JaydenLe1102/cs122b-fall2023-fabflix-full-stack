package main_fablix.services;

import com.google.gson.JsonObject;

import constant.SQLStatements;
import jakarta.servlet.http.HttpServletRequest;
import utils.DatabaseUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PaymentService {

    public static JsonObject processPayment(HttpServletRequest request, String customerId,
                                            String ccId, String firstName, String lastName, String expiration) throws SQLException {
        JsonObject response = new JsonObject();
        boolean isValid = false;

        DataSource dataSource = DatabaseUtil.getDataSource(true);

        try (Connection connection = dataSource.getConnection()) {
            isValid = validateCreditCardDetails(connection, ccId, firstName, lastName, expiration);
            System.out.println(isValid);
        } catch (SQLException e) {
            response.addProperty("error", "Database connection error.");
            return response;
        }

        if (isValid) {

            dataSource = DatabaseUtil.getDataSource(false);

            try (Connection connection = dataSource.getConnection()) {
                ArrayList<String> previousItems = (ArrayList<String>) request.getSession().getAttribute("previousItems");
                ArrayList<String> quantities = (ArrayList<String>) request.getSession().getAttribute("quantities");

                if (previousItems != null && previousItems.size() > 0) {
                    JsonObject salesObject = new JsonObject(); // Create a JSON object to store sales

                    boolean allSalesRecorded = true;

                    for (int i = 0; i < previousItems.size(); i++) {
                        String movie = previousItems.get(i);
                        String movieId = Random3Service.getMovieIdByMovieTitle(movie);
                        int quantitySold = Integer.parseInt(quantities.get(i));

                        // Call the recordSale method and capture the sale ID returned
                        int saleId = recordSale(connection, customerId, movieId, quantitySold);

                        if (saleId != -1) {
                            // Construct a JSON object for each sale
                            JsonObject saleDetails = new JsonObject();
                            saleDetails.addProperty("saleId", saleId);
                            saleDetails.addProperty("movieTitle", movie);
                            saleDetails.addProperty("quantity", quantitySold);

                            // Add this sale to the sales object
                            salesObject.add("sale" + i, saleDetails);
                        } else {
                            allSalesRecorded = false;
                            break; // Exit the loop if sale recording fails
                        }
                    }

                    if (allSalesRecorded) {
                        System.out.println("All sales have been recorded");
                        response.addProperty("success", true);
                        response.addProperty("message", "Transaction successful!");
                    } else {
                        response.addProperty("error", "Failed to record sale(s). Please try again.");
                    }

                    // Add the 'sales' object to the main response
                    response.add("sales", salesObject);
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

    private static boolean validateCreditCardDetails(Connection conn, String ccId, String firstName, String lastName,
                                                     String expiration) {

        System.out.println(ccId);
        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(expiration);

        try (PreparedStatement statement = conn.prepareStatement(SQLStatements.VALIDATE_CREDITCARDS)) {
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

    private static int recordSale(Connection conn, String customerId, String movieId, Integer quantitySold) {

        try (PreparedStatement statement = conn.prepareStatement(SQLStatements.INSERT_NEW_SALES,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, customerId);
            statement.setString(2, movieId);
            statement.setInt(3, quantitySold);

            System.out.println("Insert statement");
            System.out.println(statement);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating sale failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Retrieving the auto-generated sale ID
                } else {
                    throw new SQLException("Creating sale failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Or any other error code or specific value to denote a failure
        }
    }

}

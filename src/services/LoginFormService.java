package services;

import com.google.gson.JsonObject;
import constant.SQLStatements;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormService {

    public static JsonObject validateEmailPassword (DataSource dataSource, String email, String password) throws  Exception{

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(SQLStatements.VALICATE_EMAIL_PASSWORD);
            statement.setString(1, email);
            statement.setString(2, password);


            ResultSet result = statement.executeQuery();

            if (result.next()) {
                // Valid login credentials

                JsonObject customerObject = new JsonObject();

                customerObject.addProperty("id", result.getInt("id"));
                customerObject.addProperty("firstName", result.getString("firstName"));
                customerObject.addProperty("lastName", result.getString("lastName"));
                customerObject.addProperty("ccId", result.getString("ccId"));
                customerObject.addProperty("address", result.getString("address"));
                customerObject.addProperty("email", email);
                customerObject.addProperty("password",password);

                return customerObject;
                // Perform the login and redirection logic here
            } else {
                return null;
                // Invalid login credentials, show an error or redirect to a login page
            }
        } catch (SQLException e) {
            // Handle any database-related exceptions
            throw e;
        }

    }

}

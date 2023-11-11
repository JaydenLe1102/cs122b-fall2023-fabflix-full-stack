package employee_dashboard.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import constant.SQLStatements;
import org.jasypt.util.password.StrongPasswordEncryptor;

public class DashboardLoginFormService {

	public static String verifyCredentials(String email, String password, DataSource dataSource) throws Exception {

		String re;

		try (Connection conn = dataSource.getConnection()) {
			try (PreparedStatement statement = conn.prepareStatement(SQLStatements.VALIDATE_EMPLOYEE_EMAIL_PASSWORD)) {
				statement.setString(1, email);
				try (ResultSet rs = statement.executeQuery()) {
					boolean success = false;
					if (rs.next()) {
						// get the encrypted password from the database
						String encryptedPassword = rs.getString("password");

						System.out.println("encryptedPassword: " + encryptedPassword);

						// use the same encryptor to compare the user input password with encrypted
						// password stored in DB
						success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

						if (!success) {
							// return -2 if password is wrong
							re = "-2";
						} else {
							System.out.println("verify " + email + " - " + password);

							String employeeEmail = rs.getString("email");

							re = employeeEmail;
						}

					} else {
						// return -1 if no email exist
						re = "-1";
					}
				}
			}
		} catch (SQLException e) {
			// Handle any database-related exceptions
			throw e;
		}

		return re;
	}

}
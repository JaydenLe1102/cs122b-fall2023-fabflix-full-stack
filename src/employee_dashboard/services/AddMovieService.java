package employee_dashboard.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import constant.SQLStatements;

public class AddMovieService {

	public static int addMovie(DataSource dataSource, String movieTitle, String movieYear, String movieDirector,
			String starName, String starBirthYear, String genreName) {

		try (Connection conn = dataSource.getConnection()) {
			try (PreparedStatement statement = conn.prepareStatement(SQLStatements.INSERT_NEW_MOVIE)) {

				statement.setString(1, movieTitle);
				statement.setInt(2, Integer.parseInt(movieYear));
				statement.setString(3, movieDirector);
				statement.setString(4, starName);

				if ("".equals(starBirthYear)) {
					statement.setInt(5, Integer.parseInt(starBirthYear));
				} else {
					statement.setInt(5, -1);
				}

				statement.setString(6, genreName);

				try (ResultSet rs = statement.executeQuery()) {
					if (rs.next()) {
						int affectedRows = rs.getInt(0);

						System.out.println("add-movie Sp Affected rows: " + Integer.toString(affectedRows));

						if (affectedRows < 3 || affectedRows > 5) {
							return -1;
						}
						return 0;
					} else {
						return -1;
					}
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1; // Or any other error code or specific value to denote a failure
		}
	}
}

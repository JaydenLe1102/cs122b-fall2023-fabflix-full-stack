package employee_dashboard.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import constant.SQLStatements;

public class AddStarService {

	public static int addStar(DataSource dataSource, String name, String birthYear) {

		String query;

		if ("".equals(birthYear)) {
			query = SQLStatements.INSERT_NEW_STAR_WITHOUT_BIRTHYEAR;
		} else {
			query = SQLStatements.INSERT_NEW_STAR_WITH_BIRTHYEAR;
		}
		try (Connection conn = dataSource.getConnection()) {
			try (PreparedStatement statement = conn.prepareStatement(query)) {

				int biggestStarId = getBiggestStarId(conn);
				int newStarId = biggestStarId + 1;
				String newStarIdString = "nm" + newStarId;

				if ("".equals(birthYear)) {
					statement.setString(1, newStarIdString);
					statement.setString(2, name);
				} else {
					statement.setString(1, newStarIdString);
					statement.setString(2, name);
					statement.setInt(3, Integer.parseInt(birthYear));
				}

				System.out.println("Insert statement");
				System.out.println(statement);

				int affectedRows = statement.executeUpdate();

				System.out.println("Affected rows: " + Integer.toString(affectedRows));

				if (affectedRows == 0) {
					throw new SQLException("Creating sale failed, no rows affected.");
				}

				return 0; // Success
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1; // Or any other error code or specific value to denote a failure
		}
	}

	private static int getBiggestStarId(Connection conn) throws SQLException {
		try (PreparedStatement statement = conn.prepareStatement(SQLStatements.GET_BIGGEST_STAR_ID)) {
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				String id = rs.getString(1);
				return Integer.parseInt(id.substring(2));
			}

			throw new SQLException("No stars found");
		}
	}
}

package employee_dashboard.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import java.sql.CallableStatement;

import com.google.gson.JsonObject;
import constant.SQLStatements;
import utils.DatabaseUtil;

public class AddMovieService {

    public static String[] addMovie(String movieTitle, String movieYear, String movieDirector,
                                    String starName, String starBirthYear, String genreName) throws SQLException {

        String[] result = new String[4];
        DataSource dataSource = DatabaseUtil.getDataSource(false);
        try (Connection conn = dataSource.getConnection()) {
            try (CallableStatement statement = conn.prepareCall(SQLStatements.INSERT_NEW_MOVIE)) {

                statement.setString(1, movieTitle);
                statement.setInt(2, Integer.parseInt(movieYear));
                statement.setString(3, movieDirector);
                statement.setString(4, starName);

                if ("".equals(starBirthYear)) {
                    statement.setInt(5, -1);
                } else {
                    statement.setInt(5, Integer.parseInt(starBirthYear));
                }

                statement.setString(6, genreName);

                System.out.println("add-movie Sp: " + statement);

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        int affectedRows = rs.getInt("result");
                        String movieId = rs.getString("movieId");
                        String starId = rs.getString("starId");
                        String genreId = rs.getString("genreId");

                        System.out.println("add-movie Sp Affected rows: " + Integer.toString(affectedRows));

                        if (affectedRows < 3 || affectedRows > 5) {

                            result[0] = "-1";

                            //return -1;
                            return result;
                        }

                        result[0] = "0";

                        result[1] = movieId;

                        result[2] = starId;
                        result[3] = genreId;

                        return result;
                    } else {
                        result[0] = "-1";

                        //return -1;
                        return result;
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            result[0] = "-1";

            //return -1;
            return result;
        }
    }
}

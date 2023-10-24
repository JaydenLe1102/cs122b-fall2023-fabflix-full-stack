package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import constant.SQLStatements;

public class BrowseByTitleService {

    public static JsonArray getMovieListByTitle(DataSource dataSource, String title) throws Exception {

        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            String sql;

            if (title.equals("*")) {
                sql = SQLStatements.BROWSE_BY_TITLE_NON_ALPHANUMERIC;
            } else {
                sql = SQLStatements.BROWSE_BY_TITLE_ALPHANUMERIC;
            }

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(sql);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            if (!title.equals("*")) {
                statement.setString(1, title + "%");
            }

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();


            // Iterate through each row of rs
            while (rs.next()) {
                // Create a JsonObject based on the data we retrieve from rs

                JsonObject movieObject = new JsonObject();
                String movieId = rs.getString("id");
                movieObject.addProperty("movie_id", movieId);
                movieObject.addProperty("movie_title", rs.getString("title"));
                movieObject.addProperty("year", rs.getString("year"));
                movieObject.addProperty("director", rs.getString("director"));
                movieObject.add("genres", Random3Service.getRandom3GenreByMovieId(dataSource, movieId));
                movieObject.add("stars", Random3Service.getRandom3StarsByMovieId(dataSource, movieId));

                jsonArray.add(movieObject);
            }


            rs.close();
            statement.close();

            return jsonArray;

        }
    }

}

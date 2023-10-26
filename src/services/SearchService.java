package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import constant.SQLStatements;

public class SearchService {

    public static JsonArray getMovieListByTitleYearDirectorStar(DataSource dataSource, String title, String year, String director, String star) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("Connection established"); // Add this print statement

            // Get a connection from dataSource
            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(SQLStatements.SEARCH);
            System.out.println("PreparedStatement created"); // Add this print statement

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, title);
            statement.setString(2, title);
            statement.setString(3, director);
            statement.setString(4, director);
            statement.setString(5, star);
            statement.setString(6, star);
            statement.setString(7, year);
            statement.setString(8, year);

            System.out.println("Parameters set for the statement"); // Add this print statement
            System.out.println(statement); // Add this print statement

            // Perform the query
            ResultSet rs = statement.executeQuery();
            System.out.println("Query executed"); // Add this print statement
            System.out.println(rs); // Add this print statement

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                // Create a JsonObject based on the data we retrieve from rs

                JsonObject movieObject = new JsonObject();
                String movieId = rs.getString("id");
                movieObject.addProperty("movie_id", movieId);
                movieObject.addProperty("title", rs.getString("title"));
                movieObject.addProperty("year", rs.getString("year"));
                movieObject.addProperty("director", rs.getString("director"));
                movieObject.addProperty("rating", rs.getString("rating"));
                movieObject.add("genres", Random3Service.getRandom3GenreByMovieId(dataSource, movieId));
                movieObject.add("stars", Random3Service.getRandom3StarsByMovieId(dataSource, movieId));

                jsonArray.add(movieObject);
            }
            System.out.println("Query result processed"); // Add this print statement

            rs.close();
            statement.close();
            System.out.println("Resources closed"); // Add this print statement
            System.out.println(jsonArray);
            return jsonArray;
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage()); // Add this print statement
            throw e;
        }
    }
}

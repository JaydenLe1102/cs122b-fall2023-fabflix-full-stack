package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import constant.SQLStatements;

public class SingleStarService {

    public static JsonObject getSingleStarById (DataSource dataSource, String id) throws  Exception{

        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(SQLStatements.SINGLE_STAR_BY_STARID);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            JsonObject starObject = new JsonObject();

            // Iterate through each row of rs
            while (rs.next()) {
                // Create a JsonObject based on the data we retrieve from rs

                starObject.addProperty("star_id", rs.getString("starId"));
                starObject.addProperty("star_name", rs.getString("name"));
                starObject.addProperty("star_dob", rs.getString("birthYear"));

                JsonObject movieObject = new JsonObject();

                movieObject.addProperty("movie_id", rs.getString("movieId"));
                movieObject.addProperty("movie_title", rs.getString("title"));
                movieObject.addProperty("movie_year", rs.getString("year"));
                movieObject.addProperty("movie_director", rs.getString("director"));

                jsonArray.add(movieObject);
            }

            starObject.add("movies", jsonArray);
            rs.close();
            statement.close();

            return starObject;

        }
    }

}

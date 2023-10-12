package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SingleStarService {

    public static JsonObject getSingleStarById (DataSource dataSource, String id) throws  Exception{

        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
                    "where m.id = sim.movieId and sim.starId = s.id and s.id = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            JsonObject starObject = new JsonObject();

            // Iterate through each row of rs
            while (rs.next()) {

                String starId = rs.getString("starId");
                String starName = rs.getString("name");
                String starDob = rs.getString("birthYear");

                String movieId = rs.getString("movieId");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");

                // Create a JsonObject based on the data we retrieve from rs

                starObject.addProperty("star_id", starId);
                starObject.addProperty("star_name", starName);
                starObject.addProperty("star_dob", starDob);

                JsonObject movieObject = new JsonObject();

                movieObject.addProperty("movie_id", movieId);
                movieObject.addProperty("movie_title", movieTitle);
                movieObject.addProperty("movie_year", movieYear);
                movieObject.addProperty("movie_director", movieDirector);

                jsonArray.add(movieObject);
            }

            starObject.add("movies", jsonArray);
            rs.close();
            statement.close();

            return starObject;

        }
    }

}

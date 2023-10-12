package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SingleMovieService {

    public static JsonObject getSingleMovieById (DataSource dataSource, String id) throws  Exception{

        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT m.id, m.title, m.year, m.director, s.id, s.name, s.birthYear, g.id, g.name, r.rating from stars as s, stars_in_movies as sim, movies as m, genres as g, genres_in_movies as gim, ratings as r " +
                    "where g.id = gim.genreId and gim.movieId = m.id and m.id = sim.movieId and sim.starId = s.id and r.movieId = m.id and m.id = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray1 = new JsonArray();

            JsonArray jsonArray2 = new JsonArray();

            JsonObject movieObject = new JsonObject();

            // Iterate through each row of rs
            while (rs.next()) {

                String movieId = rs.getString("m.id");
                String movieTitle = rs.getString("m.title");
                String movieYear = rs.getString("m.year");
                String movieDirector = rs.getString("m.director");
                String movieRating = rs.getString("r.rating");

                String starId = rs.getString("s.id");
                String starName = rs.getString("s.name");
                String starDob = rs.getString("s.birthYear");

                String genreId = rs.getString("g.id");
                String genreName = rs.getString("g.name");

                // Create a JsonObject based on the data we retrieve from rs

                movieObject.addProperty("movie_id", movieId);
                movieObject.addProperty("movie_title", movieTitle);
                movieObject.addProperty("movie_year", movieYear);
                movieObject.addProperty("movie_director", movieDirector);
                movieObject.addProperty("movie_rating", movieRating);

                JsonObject starObject = new JsonObject();

                starObject.addProperty("star_id", starId);
                starObject.addProperty("star_name", starName);
                starObject.addProperty("star_dob", starDob);

                jsonArray1.add(starObject);

                JsonObject genreObject = new JsonObject();

                genreObject.addProperty("genre_id", genreId);
                genreObject.addProperty("genre_name", genreName);

                jsonArray2.add(genreObject);
            }

            movieObject.add("stars", jsonArray1);
            movieObject.add("genres", jsonArray2);
            rs.close();
            statement.close();

            return movieObject;

        }
    }

}

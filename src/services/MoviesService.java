package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import constant.SQLStatements;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MoviesService {

    public static JsonArray getMoviesArray(DataSource dataSource) throws Exception{

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement movieStatement = conn.createStatement();

            // Perform the query
            ResultSet rsMovies = movieStatement.executeQuery(SQLStatements.TOP20MOVIES);


            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rsMovies.next()) {
                String movie_id = rsMovies.getString("id");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("title", rsMovies.getString("title"));
                jsonObject.addProperty("year", rsMovies.getString("year"));
                jsonObject.addProperty("director", rsMovies.getString("director"));
                jsonObject.addProperty("rating", rsMovies.getString("rating"));

                // call db for genres of each movie_id
                PreparedStatement genreStatement = conn.prepareStatement(SQLStatements.RANDOM3GENREBYMOVIEID);

                genreStatement.setString(1, movie_id);

                // Perform the query
                ResultSet rsGenres = genreStatement.executeQuery();

                JsonArray genresList = new JsonArray();

                while(rsGenres.next()){
                    genresList.add(rsGenres.getString("genre"));
                }

                jsonObject.add("genres", genresList);

                rsGenres.close();
                genreStatement.close();


                // end: call db for genres of each movie_id

                // call db for stars of each movie_id

                PreparedStatement starStatement = conn.prepareStatement(SQLStatements.RANDOM3STARBYMOVIEID);

                starStatement.setString(1, movie_id);

                // Perform the query
                ResultSet rsStars = starStatement.executeQuery();

                JsonArray starsList = new JsonArray();

                while(rsStars.next()){
                    JsonObject starObject = new JsonObject();
                    starObject.addProperty("name", rsStars.getString("star"));
                    starObject.addProperty("star_id", rsStars.getString("star_id"));
                    starsList.add(starObject);
                }

                jsonObject.add("stars", starsList);

                rsStars.close();
                starStatement.close();

                // end: call db for stars of each movie_id

                jsonArray.add(jsonObject);
            }
            rsMovies.close();
            movieStatement.close();

            return jsonArray;
        }

    }
}

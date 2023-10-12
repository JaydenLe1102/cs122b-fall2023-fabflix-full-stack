package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MoviesService {

    public static JsonArray getMoviesArray(DataSource dataSource) throws Exception{

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement movieStatement = conn.createStatement();

            String movieQuery = "SELECT\n" +
                    "    id,\n" +
                    "    title,\n" +
                    "    year,\n" +
                    "    director,\n" +
                    "    rating\n" +
                    "FROM movies AS m\n" +
                    "JOIN ratings AS r ON m.id = r.movieId\n" +
                    "ORDER BY rating DESC\n" +
                    "LIMIT 20;";

            // Perform the query
            ResultSet rsMovies = movieStatement.executeQuery(movieQuery);


            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rsMovies.next()) {
                String movie_id = rsMovies.getString("id");
                String movie_title = rsMovies.getString("title");
                String year = rsMovies.getString("year");
                String director = rsMovies.getString("director");
                String rating = rsMovies.getString("rating");


                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("title", movie_title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("rating", rating);

                // call db for genres of each movie_id
                Statement genreStatement = conn.createStatement();

                String genreQuery = "SELECT g.name AS genre \n" +
                        "FROM genres AS g \n" +
                        "JOIN genres_in_movies AS gim ON g.id = gim.genreId \n" +
                        "WHERE gim.movieId = '" + movie_id + "' \n" +
                        "LIMIT 3;";



                // Perform the query
                ResultSet rsGenres = genreStatement.executeQuery(genreQuery);

                JsonArray genresList = new JsonArray();

                while(rsGenres.next()){
                    genresList.add(rsGenres.getString("genre"));
                }

                jsonObject.add("genres", genresList);

                rsGenres.close();
                genreStatement.close();


                // end: call db for genres of each movie_id

                // call db for stars of each movie_id

                Statement starStatement = conn.createStatement();

                String starQuery = "SELECT s.name AS star, s.id as star_id\n" +
                        "FROM stars AS s \n" +
                        "JOIN stars_in_movies AS sim ON s.id = sim.starId \n" +
                        "WHERE sim.movieId = '" + movie_id + "' \n" +
                        "LIMIT 3;";

                // Perform the query
                ResultSet rsStars = starStatement.executeQuery(starQuery);

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

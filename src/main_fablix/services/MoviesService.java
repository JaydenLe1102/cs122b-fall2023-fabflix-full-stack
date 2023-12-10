package main_fablix.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import constant.SQLStatements;
import utils.DatabaseUtil;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MoviesService {

    public static JsonArray getMoviesArray() throws Exception {

        // Get a connection from dataSource and let resource manager close the connection after usage.

        DataSource dataSource = DatabaseUtil.getDataSource(true);

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


                jsonObject.add("genres", Random3Service.getRandom3GenreByMovieId(movie_id));


                jsonObject.add("stars", Random3Service.getRandom3StarsByMovieId(movie_id));


                jsonArray.add(jsonObject);
            }
            rsMovies.close();
            movieStatement.close();

            return jsonArray;
        }

    }
}

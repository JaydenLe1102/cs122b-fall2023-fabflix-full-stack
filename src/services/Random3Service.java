package services;

import com.google.gson.JsonArray;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.gson.JsonObject;
import constant.SQLStatements;

public class Random3Service {

    public static JsonArray getRandom3GenreByMovieId(DataSource dataSource, String movieId) throws Exception {

        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            PreparedStatement genreStatement = conn.prepareStatement(SQLStatements.RANDOM3GENREBYMOVIEID);

            genreStatement.setString(1, movieId);

            // Perform the query
            ResultSet rsGenres = genreStatement.executeQuery();

            JsonArray genresList = new JsonArray();

            while (rsGenres.next()) {
                genresList.add(rsGenres.getString("genre"));
            }

            rsGenres.close();
            genreStatement.close();

            return genresList;

        }
    }

    public static JsonArray getRandom3StarsByMovieId(DataSource dataSource, String movieId) throws Exception {

        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            PreparedStatement starStatement = conn.prepareStatement(SQLStatements.RANDOM3STARBYMOVIEID);

            starStatement.setString(1, movieId);

            // Perform the query
            ResultSet rsStars = starStatement.executeQuery();

            JsonArray starsList = new JsonArray();

            while (rsStars.next()) {
                JsonObject starObject = new JsonObject();
                starObject.addProperty("name", rsStars.getString("star"));
                starObject.addProperty("star_id", rsStars.getString("star_id"));
                starsList.add(starObject);
            }

            rsStars.close();
            starStatement.close();

            return starsList;

        }
    }

    public static String getMovieIdByMovieTitle(DataSource dataSource, String movieTitle) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement movieStatement = conn.prepareStatement(SQLStatements.GETMOVIEID);
            movieStatement.setString(1, movieTitle);

            ResultSet rsId = movieStatement.executeQuery();
            if (rsId.next()) {
                String id = rsId.getString("movieId");
                System.out.println(id);
                return id;
            } else {
                throw new Exception("No movie ID found for the title: " + movieTitle);
            }
        }
    }


}

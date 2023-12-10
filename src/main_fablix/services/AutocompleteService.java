package main_fablix.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import constant.SQLStatements;
import utils.DatabaseUtil;

public class AutocompleteService {

    public static JsonArray getMovieListByQuery(String movie_query) throws Exception {

        DataSource dataSource = DatabaseUtil.getDataSource(true);

        try (Connection conn = dataSource.getConnection()) {
            // Tokenize the input query
            String[] tokens = movie_query.split("\\s+");

            // Build the SQL query dynamically based on the number of tokens
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(SQLStatements.FULLTEXTSEARCH);
            // Add the dynamically generated conditions
            for (int i = 0; i < tokens.length; i++) {
                if (i == 0) {
                    queryBuilder.append("(m.title LIKE ? OR m.title LIKE ?)");
                } else {
                    queryBuilder.append(" AND (m.title LIKE ? OR m.title LIKE ?)");
                }
            }
            // Prepare the statement with dynamically generated query
            PreparedStatement statement = conn.prepareStatement(queryBuilder.toString());

            // Set parameters for each token
            int index = 1;
            for (int i = 0; i < tokens.length; i++) {
                statement.setString(index,  "% " + tokens[i] + "%");
                index++;
                statement.setString(index,  tokens[i] + "%");
                index++;
            }

            // Execute the query
            ResultSet rs = statement.executeQuery();

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
                movieObject.add("genres", Random3Service.getRandom3GenreByMovieId(movieId));
                movieObject.add("stars", Random3Service.getRandom3StarsByMovieId(movieId));

                jsonArray.add(movieObject);
            }

            rs.close();
            statement.close();
            return jsonArray;
        } catch (Exception e) {
            throw e;
        }
    }
}

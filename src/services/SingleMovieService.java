package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import constant.SQLStatements;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SingleMovieService {

    public static JsonObject getSingleMovieById(DataSource dataSource, String id) throws Exception {

        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource
            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(SQLStatements.SINGLEMOVIEBYMOVIEID);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray1 = new JsonArray();

            JsonArray jsonArray2 = new JsonArray();

            Set<String> starIds = new HashSet<>();
            Set<String> genreIds = new HashSet<>();

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

                if (!starIds.contains(starId)) {
                    starIds.add(starId);
                    PreparedStatement starMovieStatement = conn.prepareStatement(SQLStatements.STAR_COUNT_MOVIE);
                    starMovieStatement.setString(1, starId);

                    ResultSet starMovieRs = starMovieStatement.executeQuery();
                    JsonObject starObject = new JsonObject();

                    while (starMovieRs.next()) {
                        String starMovieCount = starMovieRs.getString("movieCount");
                        starObject.addProperty("movie_count", starMovieCount);
                    }

                    starMovieStatement.close();
                    starMovieRs.close();

                    starObject.addProperty("star_id", starId);
                    starObject.addProperty("star_name", starName);
                    starObject.addProperty("star_dob", starDob);
                    jsonArray1.add(starObject);
                }

                if (!genreIds.contains(genreId)) {
                    JsonObject genreObject = new JsonObject();

                    genreObject.addProperty("genre_id", genreId);
                    genreObject.addProperty("genre_name", genreName);
                    genreIds.add(genreId);
                    jsonArray2.add(genreObject);
                }

            }

            JsonArray sortedJsonArray1 = sortStarArray(jsonArray1);

            // Convert the JSON objects to a list for sorting
            JsonArray sortedJsonArray = sortGenreArray(jsonArray2);

            movieObject.add("stars", sortedJsonArray1);
            movieObject.add("genres", sortedJsonArray);

            rs.close();
            statement.close();

            return movieObject;

        }
    }

    private static JsonArray sortGenreArray(JsonArray jsonArray2) {
        List<JsonObject> jsonObjectList = new ArrayList<>();
        for (int i = 0; i < jsonArray2.size(); i++) {
            jsonObjectList.add(jsonArray2.get(i).getAsJsonObject());
        }

        // Sort the list by "genre_name" in alphabetical order
        Collections.sort(jsonObjectList, Comparator.comparing(obj -> obj.get("genre_name").getAsString()));

        // Create a new JsonArray with sorted JSON objects
        JsonArray sortedJsonArray = new JsonArray();
        for (JsonObject jsonObject : jsonObjectList) {
            sortedJsonArray.add(jsonObject);
        }
        return sortedJsonArray;
    }

    private static JsonArray sortStarArray(JsonArray jsonArray1) {
        List<JsonObject> jsonObjectList = new ArrayList<>();
        for (int i = 0; i < jsonArray1.size(); i++) {
            jsonObjectList.add(jsonArray1.get(i).getAsJsonObject());
        }

        // Sort the list in decreasing order of "movie_count" and use alphabetical order
        // for ties
        Collections.sort(jsonObjectList, (obj1, obj2) -> {
            int movieCount1 = obj2.get("movie_count").getAsInt();
            int movieCount2 = obj1.get("movie_count").getAsInt(); // Reverse order
            int nameComparison = obj1.get("star_name").getAsString().compareTo(obj2.get("star_name").getAsString());

            if (movieCount1 != movieCount2) {
                return movieCount1 - movieCount2;
            }
            return nameComparison;
        });

        // Create a new JsonArray with sorted JSON objects
        JsonArray sortedJsonArray1 = new JsonArray();
        for (JsonObject jsonObject : jsonObjectList) {
            sortedJsonArray1.add(jsonObject);
        }
        return sortedJsonArray1;
    }

}



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.List;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/moviedb";
    private static final String DB_USER = "mytestuser";
    private static final String DB_PASSWORD = "My6$Password";

    private static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database.");
        }
    }

    public static void insertMovies(List<Movie> movies) {
        try (Connection conn = getConnection()) {
            String insertMovieQuery = "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertMovieQuery)) {
                for (Movie movie : movies) {
                    String movieId = movie.getId();
                    String movieTitle = movie.getTitle();
                    Integer movieYear = movie.getYear();
                    String movieDirector = movie.getDirector();

                    // If any attribute is null, skip inserting the record
                    if (movieId == null || movieTitle == null || movieYear == null || movieDirector == null) {
                        System.out.println("Skipping movie with null attributes: " + movie.getTitle());
                        continue;
                    }

                    // Check if the film ID already exists
                    if (!filmIdExists(conn, movieId)) {
                        pstmt.setString(1, movieId);
                        pstmt.setString(2, movieTitle);
                        pstmt.setInt(3, movieYear);
                        pstmt.setString(4, movieDirector);

                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertMoviesBatch(List<Movie> movies) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)")) {
            conn.setAutoCommit(false);

            for (Movie movie : movies) {
                String movieId = movie.getId();
                String movieTitle = movie.getTitle();
                Integer movieYear = movie.getYear();
                String movieDirector = movie.getDirector();

                if (movieId == null || movieTitle == null || movieYear == null || movieDirector == null) {
                    System.out.println("Skipping movie with null attributes: " + movie.getTitle());
                    continue;
                }

                if (!filmIdExists(conn, movieId)) {
                    pstmt.setString(1, movieId);
                    pstmt.setString(2, movieTitle);
                    pstmt.setInt(3, movieYear);
                    pstmt.setString(4, movieDirector);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean filmIdExists(Connection conn, String filmId) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM movies WHERE id = ?")) {
            pstmt.setString(1, filmId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                return resultSet.next(); // Returns true if the film ID exists in the database
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void insertGenres(List<Genre> genres) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO genres (name) VALUES (?)")) {

            for (Genre genre : genres) {
                pstmt.setString(1, genre.getName());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertGenresBatch(List<Genre> genres) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO genres (name) VALUES (?)")) {
            conn.setAutoCommit(false);

            for (Genre genre : genres) {
                pstmt.setString(1, genre.getName());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertGenresInMovies(List<GenresInMovie> genresInMovies) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE genreId = VALUES(genreId)")) {

            for (GenresInMovie gim : genresInMovies) {
                // Get the movieId from the GenresInMovie object
                String movieId = gim.getMovieId();

                // Check if the movieId exists in the movies table
                if (movieIdExists(conn, movieId)) {
                    // Insert into genres_in_movies only if the movieId is valid
                    pstmt.setString(1, getGenreId(gim.getGenreName()));  // Get the genreId
                    pstmt.setString(2, movieId);  // Insert the movieId
                    pstmt.executeUpdate();
                } else {
                    System.out.println("Skipping invalid movieId: " + movieId);
                    // Handle invalid movieId (e.g., log, skip, or other handling)
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertGenresInMoviesBatch(List<GenresInMovie> genresInMovies) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE genreId = VALUES(genreId)")) {
            conn.setAutoCommit(false);

            for (GenresInMovie gim : genresInMovies) {
                String movieId = gim.getMovieId();
                if (movieIdExists(conn, movieId)) {
                    pstmt.setString(1, getGenreId(gim.getGenreName()));
                    pstmt.setString(2, movieId);
                    pstmt.addBatch();
                } else {
                    System.out.println("Skipping invalid movieId: " + movieId);
                }
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean movieIdExists(Connection conn, String movieId) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM movies WHERE id = ?")) {
            pstmt.setString(1, movieId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                return resultSet.next(); // Returns true if the movieId exists in the movies table
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getGenreId(String genreName) {
        String genreId = null;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM genres WHERE name = ?")) {
            pstmt.setString(1, genreName);

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                genreId = resultSet.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genreId;
    }

    public void insertStars(List<Star> stars) {
        try (Connection conn = getConnection()) {
            String insertStarQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertStarQuery)) {
                for (Star star : stars) {
                    String stagename = star.getName();
                    Integer birthYear = star.getBirthYear();
                    int dob = (birthYear != null) ? star.getBirthYear() : 0;

                    // Check if the actor name (stagename) is unique before inserting
                    if (isActorNameUnique(conn, stagename)) {
                        pstmt.setString(1, star.getId());
                        pstmt.setString(2, stagename);
                        pstmt.setInt(3, dob);

                        pstmt.executeUpdate();
                    } else {
                        System.out.println("Skipping insertion: Actor name already exists - " + stagename);
                        // Handle the situation where the actor name is not unique
                        // You might want to log or handle it according to your needs
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertStarsBatch(List<Star> stars) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)")) {
            conn.setAutoCommit(false);

            for (Star star : stars) {
                String stagename = star.getName();
                Integer birthYear = star.getBirthYear();
                int dob = (birthYear != null) ? star.getBirthYear() : 0;

                if (isActorNameUnique(conn, stagename)) {
                    pstmt.setString(1, star.getId());
                    pstmt.setString(2, stagename);
                    pstmt.setInt(3, dob);
                    pstmt.addBatch();
                } else {
                    System.out.println("Skipping insertion: Actor name already exists - " + stagename);
                }
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isActorNameUnique(Connection conn, String stagename) {
        String query = "SELECT COUNT(*) FROM stars WHERE name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, stagename);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count == 0;  // If count is 0, the name is unique
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void insertStarsInMovies(List<StarsInMovie> starsInMovies) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)")) {

            for (StarsInMovie starsInMovie : starsInMovies) {
                String movieId = starsInMovie.getMovieId();
                String starName = starsInMovie.getStarName();

                if (movieIdExists(conn, movieId) && starName != null && !starName.isEmpty()) {
                    String starId = getStarId(starName);

                    if (starId != null) {
                        if (!starsInMovieExists(conn, starId, movieId)) {
                            pstmt.setString(1, starId);
                            pstmt.setString(2, movieId);
                            pstmt.executeUpdate();
                        } else {
                            System.out.println("Stars in Movies entry already exists for star ID: " + starId + " and movie ID: " + movieId);
                            // Handle the existing entry (e.g., log, skip, or other handling)
                        }
                    } else {
                        System.out.println("Star ID not found for star name: " + starName);
                        // Handle the missing starId (e.g., log, skip, or other handling)
                    }
                } else {
                    System.out.println("Skipping invalid movieId or starName");
                    // Handle invalid movieId or starName (e.g., log, skip, or other handling)
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertStarsInMoviesBatch(List<StarsInMovie> starsInMovies) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)")) {
            conn.setAutoCommit(false);

            for (StarsInMovie starsInMovie : starsInMovies) {
                String movieId = starsInMovie.getMovieId();
                String starName = starsInMovie.getStarName();

                if (movieIdExists(conn, movieId) && starName != null && !starName.isEmpty()) {
                    String starId = getStarId(starName);

                    if (starId != null && !starsInMovieExists(conn, starId, movieId)) {
                        pstmt.setString(1, starId);
                        pstmt.setString(2, movieId);
                        pstmt.addBatch();
                    } else {
                        System.out.println("Skipping insertion: Stars in Movies entry already exists or star ID not found for star name - " + starName);
                    }
                } else {
                    System.out.println("Skipping invalid movieId or starName");
                }
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getStarId(String starName) {
        String starId = null;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM stars WHERE name = ?")) {
            pstmt.setString(1, starName);

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                starId = resultSet.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return starId;
    }

    public static boolean starsInMovieExists(Connection conn, String starId, String movieId) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM stars_in_movies WHERE starId = ? AND movieId = ?")) {
            pstmt.setString(1, starId);
            pstmt.setString(2, movieId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                return resultSet.next(); // Returns true if the stars_in_movies entry exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}



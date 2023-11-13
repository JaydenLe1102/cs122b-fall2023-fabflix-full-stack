;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseHandler {
    private ConnectionPool connectionPool;
    public DatabaseHandler(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

public void insertMoviesBatch(List<Movie> movies, Mains243SAXParser parser) {
    try (Connection conn = connectionPool.getConnection();
         PreparedStatement pstmt = conn.prepareStatement("INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)")) {
        conn.setAutoCommit(false);

        Set<String> uniqueMovieIds = new HashSet<>();

        for (Movie movie : movies) {
            String movieId = movie.getId();
            String movieTitle = movie.getTitle();
            Integer movieYear = movie.getYear();
            String movieDirector = movie.getDirector();

            if (movieId == null || movieTitle == null || movieYear == null || movieDirector == null) {
                parser.moviesWithNullAttributesCount++;
                continue;
            }

            if (!filmIdExists(conn, movieId) && uniqueMovieIds.add(movieId)) {
                pstmt.setString(1, movieId);
                pstmt.setString(2, movieTitle);
                pstmt.setInt(3, movieYear);
                pstmt.setString(4, movieDirector);
                pstmt.addBatch();
                parser.insertedMoviesCount++;
            } else if (filmIdExists(conn, movieId)) {
                parser.moviesWithInvalidIdCount++;
            } else {
                // Handle the case where movieId is already in the batch
                // You can skip, log, or handle it according to your application's logic
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

    public void insertGenresBatch(List<Genre> genres, Mains243SAXParser parser) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO genres (name) VALUES (?)")) {
            conn.setAutoCommit(false);
    
            Set<String> uniqueGenreNames = new HashSet<>();
    
            for (Genre genre : genres) {
                String genreName = genre.getName();
    
                if (genreName != null && uniqueGenreNames.add(genreName)) {
                    pstmt.setString(1, genreName);
                    pstmt.addBatch();
                    parser.insertedGenresCount++;
                }
                // You can optionally log or handle the case where genreName is null or already in the batch
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public void insertGenresInMoviesBatch(List<GenresInMovie> genresInMovies, Mains243SAXParser parser) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE genreId = VALUES(genreId)")) {
            conn.setAutoCommit(false);
    
            Set<String> uniqueGenreMovieCombos = new HashSet<>();
    
            for (GenresInMovie gim : genresInMovies) {
                String movieId = gim.getMovieId();
                String genreName = gim.getGenreName();
    
                if (movieIdExists(conn, movieId) && uniqueGenreMovieCombos.add(genreName + movieId)) {
                    parser.insertedGenresInMoviesCount++;
                    pstmt.setString(1, getGenreId(genreName));
                    pstmt.setString(2, movieId);
                    pstmt.addBatch();
                } else {
                    parser.moviesWithInvalidIdCount++;
                    // You can optionally log or handle the case where movieId doesn't exist or the combo is a duplicate
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

    public String getGenreId(String genreName) {
        String genreId = null;
        try (Connection conn = connectionPool.getConnection();
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

    public void insertStarsBatch(List<Star> stars, Actors63SAXParser parser) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)")) {
            conn.setAutoCommit(false);
    
            Set<String> uniqueStarId = new HashSet<>();
    
            for (Star star : stars) {
                String stagename = star.getName();
                Integer birthYear = star.getBirthYear();
                int dob = (birthYear != null) ? star.getBirthYear() : 0;
    
                if (stagename != null && uniqueStarId.add(star.getId())) {
                    pstmt.setString(1, star.getId());
                    pstmt.setString(2, stagename);
                    pstmt.setInt(3, dob);
                    pstmt.addBatch();
                    parser.insertedStarsCount++;
                } else {
                    parser.duplicateActorCount++;
                    // You can optionally log or handle the case where the star name is a duplicate or null
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


    public void insertStarsInMoviesBatch(List<StarsInMovie> starsInMovies, Casts124SAXParser parser) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)")) {
            conn.setAutoCommit(false);
    
            Set<String> uniqueStarMoviePairs = new HashSet<>();
    
            for (StarsInMovie starsInMovie : starsInMovies) {
                String movieId = starsInMovie.getMovieId();
                String starName = starsInMovie.getStarName();
    
                if (movieIdExists(conn, movieId) && starName != null && !starName.isEmpty()) {
                    String starId = getStarId(starName);
    
                    if (starId != null && uniqueStarMoviePairs.add(starId + movieId)) {
                        pstmt.setString(1, starId);
                        pstmt.setString(2, movieId);
                        pstmt.addBatch();
                        parser.insertedStarsInMoviesCount++;
                    } else {
                        parser.duplicateStarsInMoviesCount++;
                        // You can optionally log or handle the case where the starId-movieId pair is a duplicate
                    }
                } else {
                    parser.inconsistentValuesCount++;
                    // You can optionally log or handle the case where movieId doesn't exist or starName is null/empty
                }
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public String getStarId(String starName) {
        String starId = null;
        try (Connection conn = connectionPool.getConnection();
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



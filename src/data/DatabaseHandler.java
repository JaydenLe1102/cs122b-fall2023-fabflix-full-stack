package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.List;

public class DatabaseHandler {
    private ConnectionPool connectionPool;
    public DatabaseHandler(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void insertMoviesBatch(List<Movie> movies, Mains243SAXParser parser) {
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)")) {
            conn.setAutoCommit(false);

            for (Movie movie : movies) {
                String movieId = movie.getId();
                String movieTitle = movie.getTitle();
                Integer movieYear = movie.getYear();
                String movieDirector = movie.getDirector();

                if (movieId == null || movieTitle == null || movieYear == null || movieDirector == null) {
                    parser.moviesWithNullAttributesCount++;
                    continue;
                }

                if (!filmIdExists(conn, movieId)) {
                    pstmt.setString(1, movieId);
                    pstmt.setString(2, movieTitle);
                    pstmt.setInt(3, movieYear);
                    pstmt.setString(4, movieDirector);
                    pstmt.addBatch();
                    parser.insertedMoviesCount++;
                } else {
                    parser.moviesWithInvalidIdCount++;
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

            for (Genre genre : genres) {
                parser.insertedGenresCount++;
                pstmt.setString(1, genre.getName());
                pstmt.addBatch();
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

            for (GenresInMovie gim : genresInMovies) {
                String movieId = gim.getMovieId();
                if (movieIdExists(conn, movieId)) {
                    parser.insertedGenresInMoviesCount++;
                    pstmt.setString(1, getGenreId(gim.getGenreName()));
                    pstmt.setString(2, movieId);
                    pstmt.addBatch();
                } else {
                    parser.moviesWithInvalidIdCount++;
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

            for (Star star : stars) {
                String stagename = star.getName();
                Integer birthYear = star.getBirthYear();
                int dob = (birthYear != null) ? star.getBirthYear() : 0;

                if (isActorNameUnique(conn, stagename)) {
                    pstmt.setString(1, star.getId());
                    pstmt.setString(2, stagename);
                    pstmt.setInt(3, dob);
                    pstmt.addBatch();
                    parser.insertedStarsCount++;
                } else {
                    parser.duplicateActorCount++;
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

            for (StarsInMovie starsInMovie : starsInMovies) {
                String movieId = starsInMovie.getMovieId();
                String starName = starsInMovie.getStarName();

                if (movieIdExists(conn, movieId) && starName != null && !starName.isEmpty()) {
                    String starId = getStarId(starName);

                    if (starId != null && !starsInMovieExists(conn, starId, movieId)) {
                        pstmt.setString(1, starId);
                        pstmt.setString(2, movieId);
                        pstmt.addBatch();
                        parser.insertedStarsInMoviesCount++;
                    } else {
                        parser.duplicateStarsInMoviesCount++;
                    }
                } else {
                    parser.inconsistentValuesCount++;
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



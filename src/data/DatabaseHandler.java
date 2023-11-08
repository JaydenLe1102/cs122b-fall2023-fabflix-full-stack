package data;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.sql.ResultSet;

public class DatabaseHandler {
    private static final String INSERT_MOVIE_QUERY = "INSERT INTO movies (film_id, film_title, year, director) VALUES (?, ?, ?, ?)";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO genres (name) VALUES (?)";
    private static final String INSERT_GENRES_IN_MOVIE_QUERY = "INSERT INTO genres_in_movies (genre_id, movie_id) VALUES (?, ?)";
    private static final String SELECT_GENRE_ID_BY_NAME = "SELECT id FROM genres WHERE name = ?";
    private static DataSource dataSource;

    public DatabaseHandler() {
        try {
            InitialContext initContext = new InitialContext();
            dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public static void insertMovies(List<Movie> movies) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_MOVIE_QUERY)) {

            for (Movie movie : movies) {
                pstmt.setString(1, movie.getId());
                pstmt.setString(2, movie.getTitle());
                pstmt.setInt(3, movie.getYear());
                pstmt.setString(4, movie.getDirector());

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertGenres(List<Genre> genres) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_GENRE_QUERY)) {

            for (Genre genre : genres) {
                pstmt.setString(1, genre.getName());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertGenresInMovies(List<GenresInMovie> genresInMovies) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_GENRES_IN_MOVIE_QUERY)) {

            for (GenresInMovie gim : genresInMovies) {
                pstmt.setString(1, getGenreId(gim.getGenreName()));
                pstmt.setString(2, gim.getMovieId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getGenreId(String genreName) {
        String genreId = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_GENRE_ID_BY_NAME)) {
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
}


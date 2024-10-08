package constant;

public class SQLStatements {

    // main_fablix package

    // MoviesService
    public static final String TOP20MOVIES = "SELECT\n" +
            "    id,\n" +
            "    title,\n" +
            "    year,\n" +
            "    director,\n" +
            "    rating\n" +
            "FROM movies AS m\n" +
            "JOIN ratings AS r ON m.id = r.movieId\n" +
            "ORDER BY rating DESC\n" +
            "LIMIT 20;";
    public static final String RANDOM3GENREBYMOVIEID = "SELECT g.name AS genre\n" +
            "FROM genres AS g\n" +
            "JOIN genres_in_movies AS gim ON g.id = gim.genreId\n" +
            "WHERE gim.movieId = ?\n" +
            "ORDER BY g.name\n" +
            "LIMIT 3;\n";

    public static final String RANDOM3STARBYMOVIEID = "SELECT s.name AS star, s.id AS star_id\n" +
            "FROM stars AS s\n" +
            "JOIN stars_in_movies AS sim ON s.id = sim.starId\n" +
            "WHERE sim.movieId = ?\n" +
            "ORDER BY (\n" +
            "  SELECT COUNT(*)\n" +
            "  FROM stars_in_movies AS sim_count\n" +
            "  WHERE sim_count.starId = s.id\n" +
            ") DESC, s.name\n" +
            "LIMIT 3;\n";

    // end: MoviesService

    // SingleMovieService
    public static final String SINGLEMOVIEBYMOVIEID = "SELECT m.id, m.title, m.year, m.director, s.id, s.name, s.birthYear, g.id, g.name, r.rating\n" + //
            "FROM stars AS s\n" + //
            "JOIN stars_in_movies AS sim ON s.id = sim.starId\n" + //
            "JOIN movies AS m ON m.id = sim.movieId\n" + //
            "JOIN genres_in_movies AS gim ON m.id = gim.movieId\n" + //
            "JOIN genres AS g ON g.id = gim.genreId\n" + //
            "LEFT JOIN ratings AS r ON m.id = r.movieId\n" + //
            "WHERE m.id = ?;";

    // end: SingleMovieService

    // SingleStarService
    public static final String SINGLE_STAR_BY_STARID = "SELECT *\n" +
            "FROM stars AS s\n" +
            "JOIN stars_in_movies AS sim ON s.id = sim.starId\n" +
            "JOIN movies AS m ON sim.movieId = m.id\n" +
            "WHERE s.id = ?\n" +
            "ORDER BY m.year DESC, m.title ASC;";

    public static final String STAR_COUNT_MOVIE = "SELECT starId, COUNT(*) AS movieCount\n" +
            "FROM stars_in_movies\n" +
            "WHERE starId = ?;";

    public static final String VALIDATE_EMAIL_PASSWORD = "SELECT * FROM customers WHERE email = ?";

    public static final String VALIDATE_EMPLOYEE_EMAIL_PASSWORD = "SELECT * FROM employees WHERE email = ?";

    public static final String BROWSE_BY_GENRE = "SELECT *\n" +
            "FROM movies as m\n" +
            "INNER JOIN genres_in_movies as gim ON m.id = gim.movieId\n" +
            "INNER JOIN genres g ON gim.genreId = g.id\n" +
            "LEFT JOIN ratings AS r ON m.id = r.movieId\n" +
            "WHERE LOWER(g.name) = LOWER(?)\n";

    public static final String BROWSE_BY_TITLE_NON_ALPHANUMERIC = "SELECT *\n" +
            "FROM movies AS m\n" +
            "LEFT JOIN ratings AS r ON m.id = r.movieId\n" +
            "WHERE title REGEXP '^[^a-zA-Z0-9]'\n";

    public static final String BROWSE_BY_TITLE_ALPHANUMERIC = "SELECT *\n" +
            "FROM movies AS m\n" +
            "LEFT JOIN ratings AS r ON m.id = r.movieId\n" +
            "WHERE LOWER(title) LIKE ?\n";

    public static final String ALL_GENRES = "SELECT name FROM genres;";

    public static final String[] SORTING = {
            "ORDER BY Title ASC, Rating ASC\n",
            "ORDER BY Title ASC, Rating DESC\n",
            "ORDER BY Title DESC, Rating ASC\n",
            "ORDER BY Title DESC, Rating DESC\n",
            "ORDER BY Rating ASC, Title ASC\n",
            "ORDER BY Rating ASC, Title DESC\n",
            "ORDER BY Rating DESC, Title ASC\n",
            "ORDER BY Rating DESC, Title DESC\n"
    };

    public static final String PAGINATION = "LIMIT ?\n" +
            "OFFSET ?\n;";
    public static final String SEARCH = "SELECT DISTINCT m.id AS id, m.title AS title, m.director AS director, m.year AS year, r.rating AS rating\n"
            +
            "FROM movies m\n" +
            "JOIN stars_in_movies sm ON m.id = sm.movieId\n" +
            "JOIN stars s ON sm.starId = s.id\n" +
            "LEFT JOIN ratings r ON m.id = r.movieId\n" +
            "WHERE\n" +
            "    ((? = '' OR m.title LIKE CONCAT('%', ?, '%'))\n" +
            "     AND (? = '' OR m.director LIKE CONCAT('%', ?, '%'))\n" +
            "     AND (? = '' OR s.name LIKE CONCAT('%', ?, '%'))\n" +
            "     AND (? = '' OR m.year = ?))\n";

    public static final String GETMOVIEID = "SELECT id AS movieId\n" +
            "FROM movies\n" +
            "WHERE title = ?;";

    public static final String VALIDATE_CREDITCARDS = "SELECT * FROM creditcards WHERE id = ? AND firstName = ? AND lastName = ? AND expiration = ?";
    public static final String INSERT_NEW_SALES = "INSERT INTO sales (customerId, movieId, saleDate, quantity) VALUES (?, ?, CURDATE(), ?)";

    // employee_dashboard package
    public static final String GET_MOVIEDB_METADATA = "SELECT table_name as table_name, column_name as column_name, data_type as data_type\n"
            +
            "FROM information_schema.columns\n" +
            "WHERE table_schema = 'moviedb'" +
            "ORDER BY table_name, ordinal_position;";

    public static final String GET_BIGGEST_STAR_ID = "select id\n" +
            "FROM moviedb.stars WHERE id LIKE 'nm%' AND id REGEXP '^nm[0-9]+$'" +
            "order by id DESC\n" +
            "LIMIT 1;";

    public static final String INSERT_NEW_STAR_WITH_BIRTHYEAR = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?);";
    public static final String INSERT_NEW_STAR_WITHOUT_BIRTHYEAR = "INSERT INTO stars (id, name) VALUES (?, ?);";

    public static final String INSERT_NEW_MOVIE = "CALL add_movie(?, ?, ?, ?, ?, ?);";

    public static final String FULLTEXTSEARCH =
            "SELECT DISTINCT m.id AS id, m.title AS title, m.director AS director, m.year AS year, r.rating AS rating\n"
                    +
                    "FROM movies m\n" +
                    "JOIN stars_in_movies sm ON m.id = sm.movieId\n" +
                    "JOIN stars s ON sm.starId = s.id\n" +
                    "LEFT JOIN ratings r ON m.id = r.movieId\n" +
                    "WHERE\n";
}
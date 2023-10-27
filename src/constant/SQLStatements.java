package constant;

public class SQLStatements {

	// MoviesService
	public static String TOP20MOVIES = "SELECT\n" +
			"    id,\n" +
			"    title,\n" +
			"    year,\n" +
			"    director,\n" +
			"    rating\n" +
			"FROM movies AS m\n" +
			"JOIN ratings AS r ON m.id = r.movieId\n" +
			"ORDER BY rating DESC\n" +
			"LIMIT 20;";
	public static String RANDOM3GENREBYMOVIEID = "SELECT g.name AS genre\n" +
			"FROM genres AS g\n" +
			"JOIN genres_in_movies AS gim ON g.id = gim.genreId\n" +
			"WHERE gim.movieId = ?\n" +
			"ORDER BY g.name\n" +
			"LIMIT 3;\n";

	public static String RANDOM3STARBYMOVIEID = "SELECT s.name AS star, s.id AS star_id\n" +
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
	public static String SINGLEMOVIEBYMOVIEID = "SELECT m.id, m.title, m.year, m.director, s.id, s.name, s.birthYear, g.id, g.name, r.rating from stars as s, stars_in_movies as sim, movies as m, genres as g, genres_in_movies as gim, ratings as r "
			+
			"where g.id = gim.genreId and gim.movieId = m.id and m.id = sim.movieId and sim.starId = s.id and r.movieId = m.id and m.id = ?";

	// end: SingleMovieService

	// SingleStarService
	public static String SINGLE_STAR_BY_STARID = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
			"where m.id = sim.movieId and sim.starId = s.id and s.id = ?";

	public static String VALICATE_EMAIL_PASSWORD = "SELECT * FROM customers WHERE email = ?";

	public static String BROWSE_BY_GENRE = "SELECT *\n" +
			"FROM movies as mv\n" +
			"INNER JOIN genres_in_movies as gim ON mv.id = gim.movieId\n" +
			"INNER JOIN genres g ON gim.genreId = g.id\n" +
			"JOIN ratings AS r ON mv.id = r.movieId\n" +
			"WHERE LOWER(g.name) = LOWER(?)\n";
	public static String BROWSE_BY_TITLE_NON_ALPHANUMERIC = "SELECT *\n" +
			"FROM movies\n" +
			"JOIN ratings AS r ON movies.id = r.movieId\n" +
			"WHERE title REGEXP '^[^a-zA-Z0-9]'\n";

	public static String BROWSE_BY_TITLE_ALPHANUMERIC = "SELECT *\n" +
			"FROM movies\n" +
			"JOIN ratings AS r ON movies.id = r.movieId\n" +
			"WHERE LOWER(title) LIKE ?\n";

	public static String ALL_GENRES = "SELECT name FROM genres;";

	public static String PAGINATION = "LIMIT ?\n" +
			"OFFSET ?\n;";
	public static String SEARCH = "SELECT DISTINCT m.id AS id, m.title AS title, m.director AS director, m.year AS year, r.rating AS rating\n"
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
}

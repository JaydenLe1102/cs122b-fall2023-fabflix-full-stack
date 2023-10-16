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
    public static String RANDOM3GENREBYMOVIEID = "SELECT g.name AS genre \n" +
            "FROM genres AS g \n" +
            "JOIN genres_in_movies AS gim ON g.id = gim.genreId \n" +
            "WHERE gim.movieId = ? \n" +
            "LIMIT 3;";

    public static String RANDOM3STARBYMOVIEID = "SELECT s.name AS star, s.id as star_id\n" +
            "FROM stars AS s \n" +
            "JOIN stars_in_movies AS sim ON s.id = sim.starId \n" +
            "WHERE sim.movieId = ? \n" +
            "LIMIT 3;";

    // end: MoviesService

    //SingleMovieService
    public static String SINGLEMOVIEBYMOVIEID = "SELECT m.id, m.title, m.year, m.director, s.id, s.name, s.birthYear, g.id, g.name, r.rating from stars as s, stars_in_movies as sim, movies as m, genres as g, genres_in_movies as gim, ratings as r " +
            "where g.id = gim.genreId and gim.movieId = m.id and m.id = sim.movieId and sim.starId = s.id and r.movieId = m.id and m.id = ?";

    //end: SingleMovieService

    //SingleStarService
    public static String SINGLE_STAR_BY_STARID = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
            "where m.id = sim.movieId and sim.starId = s.id and s.id = ?";

}

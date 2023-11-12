package data;

import com.zaxxer.hikari.HikariConfig;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainParser {
    public static void main(String[] args) {

        // Create parsers
        Mains243SAXParser mains243Parser = new Mains243SAXParser();
        Actors63SAXParser actors63Parser = new Actors63SAXParser();
        Casts124SAXParser casts124Parser = new Casts124SAXParser();


        // Create an ExecutorService for concurrent execution
        ExecutorService executorService = Executors.newFixedThreadPool(3);

// Run parsers concurrently
        executorService.submit(() -> mains243Parser.parseDocument());
        executorService.submit(() -> actors63Parser.parseDocument());
        executorService.submit(() -> casts124Parser.parseDocument());

        // Shutdown the executorService when all tasks are complete
        executorService.shutdown();

        // Wait for all tasks to finish
        while (!executorService.isTerminated()) {
            // Optionally, you can add some delay or perform other tasks
        }

        // Handle database operations
        handleDatabaseOperations(mains243Parser, actors63Parser, casts124Parser);

        // Optionally, print summaries or perform additional tasks
        mains243Parser.printCountSummary();
        actors63Parser.printCountSummary();
        casts124Parser.printCountSummary();
    }

    private static ConnectionPool createConnectionPool() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/moviedb";
        String username = "mytestuser";
        String password = "My6$Password";
        int maxConnections = 10; // Adjust as needed

        return new HikariConnectionPool(jdbcUrl, username, password, maxConnections);
    }

    private static void handleDatabaseOperations(Mains243SAXParser mainsParser, Actors63SAXParser actorsParser, Casts124SAXParser castsParser) {
        ConnectionPool connectionPoolMains = createConnectionPool();
        ConnectionPool connectionPoolActors = createConnectionPool();
        ConnectionPool connectionPoolCasts = createConnectionPool();

        try {
            List<Movie> movies = mainsParser.getMovies();
            List<Genre> genres = mainsParser.getGenres();
            List<GenresInMovie> genresInMovies = mainsParser.getGenresInMovies();

            DatabaseHandler databaseHandlerMains = new DatabaseHandler(connectionPoolMains);
            databaseHandlerMains.insertMoviesBatch(movies, mainsParser);
            databaseHandlerMains.insertGenresBatch(genres, mainsParser);
            databaseHandlerMains.insertGenresInMoviesBatch(genresInMovies, mainsParser);

            List<Star> stars = actorsParser.getStars();
            DatabaseHandler databaseHandlerActors = new DatabaseHandler(connectionPoolActors);
            databaseHandlerActors.insertStarsBatch(stars, actorsParser);

            List<StarsInMovie> starsInMovies = castsParser.getStarsInMovies();
            DatabaseHandler databaseHandlerCasts = new DatabaseHandler(connectionPoolCasts);
            databaseHandlerCasts.insertStarsInMoviesBatch(starsInMovies, castsParser);
        } finally {
            // Close the connection pools in a finally block to ensure they are always closed,
            // even if an exception occurs
            connectionPoolMains.close();
            connectionPoolActors.close();
            connectionPoolCasts.close();
        }
    }
}

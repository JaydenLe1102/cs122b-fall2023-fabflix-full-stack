

import com.zaxxer.hikari.HikariConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainParser {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

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
        
        try {

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
    } finally {
        // Record the end time
        long endTime = System.currentTimeMillis();

        // Calculate total execution time
        long totalTime = endTime - startTime;

        // Print the ending local time and total execution time
        printEndTimeAndTotalTime(startTime, endTime, totalTime);
    }
    }

    private static ConnectionPool createConnectionPool() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/moviedb";
        String username = "mytestuser";
        String password = "12345";
        int maxConnections = 20; // Adjust as needed

        return new HikariConnectionPool(jdbcUrl, username, password, maxConnections);
    }

    private static void handleDatabaseOperations(Mains243SAXParser mainsParser, Actors63SAXParser actorsParser, Casts124SAXParser castsParser) {
        ConnectionPool connectionPoolMains = createConnectionPool();
        ConnectionPool connectionPoolActors = createConnectionPool();
        ConnectionPool connectionPoolCasts = createConnectionPool();

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(2);

            // Block 1
            executorService.submit(() -> {
                try {
                    List<Movie> movies = mainsParser.getMovies();
                    List<Genre> genres = mainsParser.getGenres();
                    List<GenresInMovie> genresInMovies = mainsParser.getGenresInMovies();

                    DatabaseHandler databaseHandlerMains = new DatabaseHandler(connectionPoolMains);
                    databaseHandlerMains.insertMoviesBatch(movies, mainsParser);
                    databaseHandlerMains.insertGenresBatch(genres, mainsParser);
                    databaseHandlerMains.insertGenresInMoviesBatch(genresInMovies, mainsParser);
                } catch (Exception e) {
                    e.printStackTrace(); // Handle the exception according to your needs
                }
            });

            // Block 2
            executorService.submit(() -> {
                try {
                    List<Star> stars = actorsParser.getStars();
                    DatabaseHandler databaseHandlerActors = new DatabaseHandler(connectionPoolActors);
                    databaseHandlerActors.insertStarsBatch(stars, actorsParser);
                } catch (Exception e) {
                    e.printStackTrace(); // Handle the exception according to your needs
                }
            });

            // Wait for Block 1 and Block 2 to complete
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            // Block 3
            List<StarsInMovie> starsInMovies = castsParser.getStarsInMovies();
            DatabaseHandler databaseHandlerCasts = new DatabaseHandler(connectionPoolCasts);
            databaseHandlerCasts.insertStarsInMoviesBatch(starsInMovies, castsParser);
        } catch (InterruptedException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            connectionPoolMains.close();
            connectionPoolActors.close();
            connectionPoolCasts.close();
        }
    }


    private static void printEndTimeAndTotalTime(long startTime, long endTime, long totalTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startLocalTime = sdf.format(new Date(startTime));
        String endLocalTime = sdf.format(new Date(endTime));
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println();

        System.out.println("Execution started at: " + startLocalTime);
        System.out.println("Execution ended at: " + endLocalTime);
        System.out.println("Total execution time: " + totalTime + " milliseconds");
    }
}

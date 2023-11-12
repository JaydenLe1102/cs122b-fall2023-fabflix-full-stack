import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class Demo {

    private static ConnectionPool createConnectionPool() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/moviedb";
        String username = "mytestuser";
        String password = "12345";
        int maxConnections = 20; // Adjust as needed

        return new HikariConnectionPool(jdbcUrl, username, password, maxConnections);
    }

    public static void main(String[] args) {
        ConnectionPool connectionPool = createConnectionPool();


        try (Connection conn = connectionPool.getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{CALL InsertStarInMovieProcedure(?, ?)}");
            conn.setAutoCommit(false);

            //movieid
            //AA13
            //AA13
            //Jean Cadell
            //Wilfrid Lawson

            // Set the input parameters
            cstmt.setString(1, "AA13");
            cstmt.setString(2, "Jean Cadell");

            // Register the output parameter
//            cstmt.registerOutParameter(1, Types.INTEGER);

            // Add the statement to the batch
            cstmt.addBatch();


            cstmt.setString(1, "AA13");
            cstmt.setString(2, "JWilfrid Lawson");

//            cstmt.registerOutParameter(1, Types.INTEGER);

            // Add the statement to the batch
            cstmt.addBatch();

            // Execute the batch
            int[] updateCounts = cstmt.executeBatch();


            // Retrieve the return values
            for (int i = 0; i < updateCounts.length; i++) {
                
                // Process the return value as needed
                System.out.println("This is return value: ");
                System.out.println(updateCounts[i]);
            }

            conn.commit();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }


    }
}

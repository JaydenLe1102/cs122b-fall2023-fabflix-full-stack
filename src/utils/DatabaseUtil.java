package utils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Random;

public class DatabaseUtil {
    private static DataSource masterDataSource;
    private static DataSource slaveDataSource;
    private static Random random = new Random();

    static {
        try {
            Context initialContext = new InitialContext();
            masterDataSource = (DataSource) initialContext.lookup("java:comp/env/jdbc/moviedbMaster");
            slaveDataSource = (DataSource) initialContext.lookup("java:comp/env/jdbc/moviedbSlave");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource(boolean isReadOperation) throws SQLException {
        if (isReadOperation) {
            if (random.nextBoolean()) {
                System.out.println("Get master");
                return masterDataSource;
            } else {
                System.out.println("Get slave");

                return slaveDataSource;
            }
        } else {
            System.out.println("Get master");

            return masterDataSource;
        }
    }
}

package main_fablix.services;

import com.google.gson.JsonArray;
import constant.SQLStatements;
import utils.DatabaseUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AllGenresService {

    public static JsonArray getAllGenres() throws Exception {
        JsonArray jsonArray = new JsonArray();

        DataSource dataSource = DatabaseUtil.getDataSource(true);

        try (Connection conn = dataSource.getConnection()) {
            Statement statement = conn.createStatement();

            ResultSet result = statement.executeQuery(SQLStatements.ALL_GENRES);

            while (result.next()) {
                jsonArray.add(result.getString("name"));
            }

            result.close();
            statement.close();

            return jsonArray;
        }
    }
}

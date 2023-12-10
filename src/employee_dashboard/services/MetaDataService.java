package employee_dashboard.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import constant.SQLStatements;
import utils.DatabaseUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MetaDataService {

	public static JsonArray getMetaData() throws SQLException {
		JsonArray jsonArray = new JsonArray();
		DataSource dataSource = DatabaseUtil.getDataSource(true);
		try (Connection conn = dataSource.getConnection()) {
			try (Statement statement = conn.createStatement()) {
				try (ResultSet result = statement.executeQuery(SQLStatements.GET_MOVIEDB_METADATA)) {
					while (result.next()) {
						JsonObject jsonObject = new JsonObject();

						jsonObject.addProperty("table_name", result.getString("table_name"));
						jsonObject.addProperty("column_name", result.getString("column_name"));
						jsonObject.addProperty("data_type", result.getString("data_type"));

						jsonArray.add(jsonObject);
					}
				}
			}
		}

		return jsonArray;
	}

}

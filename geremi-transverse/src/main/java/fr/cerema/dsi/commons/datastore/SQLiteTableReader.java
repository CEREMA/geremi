package fr.cerema.dsi.commons.datastore;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class SQLiteTableReader {

	/**
	 * Connection au fichier Gpkg sous forme d'une base Sqlite
	 *
	 * @param gpkgPath
	 * @param tableName
	 * @throws IOException
	 */

	public List<Map<String, Object>> getNonSpatialTableData(String gpkgPath, String tableName) throws IOException {
	    String jdbcUrl = "jdbc:sqlite:" + gpkgPath;

	    // Liste pour stocker toutes les lignes de la table
	    List<Map<String, Object>> tableData = new ArrayList<>();

	    try (
	        Connection connection = DriverManager.getConnection(jdbcUrl);
	        Statement statement = connection.createStatement();
	        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName)
	    ) {
	        // Obtenir les métadonnées du ResultSet
	        ResultSetMetaData metadata = resultSet.getMetaData();
	        int columnCount = metadata.getColumnCount();

	        // Pour chaque ligne du ResultSet
	        while (resultSet.next()) {
	            // Utilisation de l'API Stream pour collecter les résultats
	            Map<String, Object> row = IntStream.rangeClosed(1, columnCount)
	                .boxed()
                  .collect( HashMap::new,
                            (m,v) -> {
                              String key ;
                              Object value;
                              try {
                                key = metadata.getColumnName(v);
                                value = resultSet.getObject(v);
                              } catch (SQLException e) {
                                throw new RuntimeException(e);
                              }
                               m.put(key,value);
                            },
                            HashMap::putAll
                  );

	            // Ajouter la map (représentant une ligne) à la liste
	            tableData.add(row);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return tableData;
	}
}

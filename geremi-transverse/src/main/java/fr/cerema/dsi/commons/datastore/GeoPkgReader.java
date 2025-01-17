package fr.cerema.dsi.commons.datastore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.jdbc.JDBCDataStore;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Value;

import fr.cerema.dsi.commons.exceptions.ImportZonageGeometryException;
import fr.cerema.dsi.commons.exceptions.ImportZonageLayerException;

public class GeoPkgReader {

	@Value("${geremi.config.message.erreur.import.layer}")
	private String messageLayer;
	@Value("${geremi.config.message.erreur.import.donnees.introuvables}")
	private String messageDonneesIntrouvables;
	@Value("${geremi.config.message.erreur.import.attributs.geometry}")
	private String messageGeometry;

	public DataStore read(String gpkgPath)
			throws IOException, ImportZonageLayerException, ImportZonageGeometryException {
		DataStore dataStore = new DataStore();
		HashMap<String, List<Attribut>> hashMapResult = new HashMap<>();
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("dbtype", "geopkg");
		params.put("database", gpkgPath);
		params.put("read-only", true);

    JDBCDataStore jdbcDataStore = null;
    try {
      jdbcDataStore = (JDBCDataStore) DataStoreFinder.getDataStore(params);
      if (jdbcDataStore.getTypeNames().length != 1) {
        throw new ImportZonageLayerException(messageLayer);
      }
      String typeName = jdbcDataStore.getTypeNames()[0];
      SimpleFeatureSource source = jdbcDataStore.getAbsoluteFeatureSource(typeName);
      SimpleFeatureType schema = source.getSchema();

      CoordinateReferenceSystem sourceCRS = schema.getCoordinateReferenceSystem();
      dataStore.setReferenceSystem(sourceCRS.getName().toString());

      Query query = new Query(schema.getTypeName());

      FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(query);
      try (FeatureIterator<SimpleFeature> features = collection.features()) {
        while (features.hasNext()) {
          SimpleFeature feature = features.next();
          List<Attribut> attributList = new ArrayList<>();
          hashMapResult.put(feature.getID(), attributList);
          for (Property attribute : feature.getProperties()) {
            Attribut attribut = new Attribut();
            attribut.setBinding(attribute.getType().getBinding());
            if (attribute.getName().equals(schema.getGeometryDescriptor().getName())) {
              attribut.setName("the_geom");
            } else {
              attribut.setName(attribute.getName().toString().toLowerCase());
            }
            attribut.setValue(attribute.getValue());
            attributList.add(attribut);
          }
          long count = attributList.stream().filter(a -> a.getName().equals("the_geom")).count();
          if (count == 0) {
            throw new ImportZonageGeometryException(messageGeometry);
          }
        }
      } catch (Exception e) {
        throw new ImportZonageGeometryException(messageGeometry);
      }

      if (hashMapResult.size() == 0) {
        throw new ImportZonageLayerException(messageDonneesIntrouvables);
      }
      dataStore.setElements(hashMapResult);
      return dataStore;
    } finally {
      if (jdbcDataStore != null) {
        jdbcDataStore.dispose();
      }
    }

	}

	/**
	 * Extraction du nom de la Table du fichier Gpkg.
	 *
	 * @param gpkgPath chemin d'accès du fichier gpkg
	 * @return le nom de la table dans le fichier gpkg
	 * @throws IOException erreur lors de la lecture du fichier
	 */
	public String extractTableNameFromGpkg(String gpkgPath) throws IOException {
		Map<String, Object> params = Map.of("dbtype", "geopkg", "database", gpkgPath, "read-only", true);
    JDBCDataStore datastore = null;
    try {
      datastore = (JDBCDataStore) DataStoreFinder.getDataStore(params);
      String[] typeNames = datastore.getTypeNames();

      if (typeNames == null || typeNames.length == 0) {
        throw new IOException("Aucun nom de type trouvé dans la base de données.");
      }
      return typeNames[0];
    } finally {
      if (datastore != null) {
        datastore.dispose();
      }
    }
	}

	/**
	 * RG2 : La géométrie doit être récupérée d'après les attributs "table_name" et
	 * "column_name" de la table "gpkg_geometry_columns". Vérifier qu'une seule
	 * table de géometrie. Vérifier la colonne indiquée de le champ "column_name"
	 * contient bien une géométrie.
	 *
	 * @param gpkgPath
	 * @return
	 * @throws IOException
	 */
	public boolean checkGpkgTableGeometrie(String gpkgPath) throws IOException {
		Map<String, Object> params = Map.of("dbtype", "geopkg", "database", gpkgPath, "read-only", true);

    JDBCDataStore datastore = null;
    try {
      datastore = (JDBCDataStore) DataStoreFinder.getDataStore(params);
      String[] typeNames = datastore.getTypeNames();

      // Vérifier qu'il n'y a qu'une seule table de géométrie.
      if (typeNames.length != 1) {
        throw new IOException("Il y a plus d'une table de géométrie dans la base de données GeoPackage.");
      }

      // Obtenir la source de fonctionnalité pour ce nom de type
      SimpleFeatureSource source = datastore.getFeatureSource(typeNames[0]);

      // Obtenir le schéma pour cette source de fonctionnalité
      SimpleFeatureType schema = source.getSchema();

      // Vérifier que la colonne indiquée par "column_name" contient une géométrie
      GeometryDescriptor geometryDescriptor = schema.getGeometryDescriptor();
      if (geometryDescriptor == null) {
        throw new IOException("La colonne indiquée par \"column_name\" ne contient pas une géométrie.");
      }

      return true;
    } finally {
      if (datastore != null) {
        datastore.dispose();
      }
    }
	}

}

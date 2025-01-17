package fr.cerema.dsi.commons.datastore;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Value;

import fr.cerema.dsi.commons.exceptions.ImportZonageLayerException;

public class ShpReader {

  @Value("${geremi.config.message.erreur.import.layer}")
  private String messageLayer;

  public DataStore read(File shapefile) throws IOException, ImportZonageLayerException {

    DataStore dataStore = new DataStore();
    HashMap<String,List<Attribut>> hashMapResult = new HashMap<>();
    ShapefileDataStore myData = (ShapefileDataStore)FileDataStoreFinder.getDataStore(shapefile);
    SimpleFeatureSource source = myData.getFeatureSource();
    SimpleFeatureType schema = source.getSchema();

    CoordinateReferenceSystem sourceCRS = schema.getCoordinateReferenceSystem();
    dataStore.setReferenceSystem(sourceCRS.getName().toString());

    Query query = new Query(schema.getTypeName());

    FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(query);
    try (FeatureIterator<SimpleFeature> features = collection.features()) {
      while (features.hasNext()) {
        SimpleFeature feature = features.next();
        List<Attribut> attributList = new ArrayList<>();
        hashMapResult.put(feature.getID(),attributList);
        for (Property attribute : feature.getProperties()) {
          Attribut attribut = new Attribut();
          attribut.setBinding(attribute.getType().getBinding());
          attribut.setName(attribute.getName().toString().toLowerCase());
          attribut.setValue(attribute.getValue());
          attributList.add(attribut);
        }
      }
    }
    if(hashMapResult.size() == 0){
      throw new ImportZonageLayerException(messageLayer);
    }
    dataStore.setElements(hashMapResult);
    return dataStore;
  }
}

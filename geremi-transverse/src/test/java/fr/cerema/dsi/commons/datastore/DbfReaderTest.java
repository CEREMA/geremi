package fr.cerema.dsi.commons.datastore;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class DbfReaderTest {

  private ShpReader shpReader = new ShpReader();

  @Test
  public void test() throws Exception{
    File file = new File("src/test/resources/a11/a11.shp");
    DataStore dataStore = shpReader.read(file);
    HashMap<String, List<Attribut>> hashMap = dataStore.getElements();
    Assertions.assertEquals(1,hashMap.size());
    Assertions.assertEquals("GCS_WGS_1984", dataStore.getReferenceSystem());
    Assertions.assertTrue(hashMap.keySet().contains("a11.1"));
    Assertions.assertEquals(9,hashMap.get("a11.1").size());

  }

  @Test
  public void testWithMultipeZone() throws Exception{
    File file = new File("src/test/resources/z_pdl/zones.shp");
    DataStore dataStore = shpReader.read(file);
    HashMap<String, List<Attribut>> hashMap = dataStore.getElements();
    Assertions.assertEquals(16,hashMap.size());
  }
}

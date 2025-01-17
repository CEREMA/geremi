package fr.cerema.dsi.commons.datastore;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeoPkgReaderTest {

  private GeoPkgReader geoPkgReader = new GeoPkgReader();

  @Test
  public void test() throws Exception{
    DataStore dataStore = this.geoPkgReader.read("src/test/resources/plat_stock.gpkg");
    Assertions.assertNotNull(dataStore);
  }
}

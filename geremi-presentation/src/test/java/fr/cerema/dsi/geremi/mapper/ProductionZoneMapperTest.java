package fr.cerema.dsi.geremi.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.cerema.dsi.geremi.dto.ProductionZone;
import fr.cerema.dsi.geremi.services.mapper.ProductionZoneMapper;
import fr.cerema.dsi.geremi.services.mapper.ProductionZoneMapperImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProductionZoneMapperTest {

  @Test
  void testListToMapOfMap() {

    ProductionZoneMapper mapper = new ProductionZoneMapperImpl();

    List<ProductionZone> prods = new ArrayList<>();
    prods.add(new ProductionZone() {
      @Override
      public int getAnnee() {
        return 2019;
      }
      @Override
      public Long getIdZone() {
        return 1L;
      }
      @Override
      public String getTypeProduction() {
        return "Beton";
      }
      @Override
      public BigDecimal getProductionZone() {
        return BigDecimal.valueOf(10L);
      }
    }
    );

    prods.add(new ProductionZone() {
                @Override
                public int getAnnee() {
                  return 2019;
                }
                @Override
                public Long getIdZone() {
                  return 1L;
                }
                @Override
                public String getTypeProduction() {
                  return "Viab";
                }
                @Override
                public BigDecimal getProductionZone() {
                  return BigDecimal.valueOf(20L);
                }
              }
    );

    prods.add(new ProductionZone() {
                @Override
                public int getAnnee() {
                  return 2019;
                }
                @Override
                public Long getIdZone() {
                  return 2L;
                }
                @Override
                public String getTypeProduction() {
                  return "Beton";
                }
                @Override
                public BigDecimal getProductionZone() {
                  return BigDecimal.valueOf(20L);
                }
              }
    );


    prods.add(new ProductionZone() {
                @Override
                public int getAnnee() {
                  return 2020;
                }
                @Override
                public Long getIdZone() {
                  return 1L;
                }
                @Override
                public String getTypeProduction() {
                  return "Beton";
                }
                @Override
                public BigDecimal getProductionZone() {
                  return BigDecimal.valueOf(20L);
                }
              }
    );

    Map<Integer, Map<Long,List<ProductionZone>>> mapProdAnneZone = mapper.toMapAnneeMapZone(prods);

    Assertions.assertEquals(mapProdAnneZone.size(), 2);

    Assertions.assertNotNull(mapProdAnneZone.get(2019));
    Assertions.assertEquals(mapProdAnneZone.get(2019).size(), 2);
    Assertions.assertNotNull(mapProdAnneZone.get(2019).get(1L));
    Assertions.assertEquals(mapProdAnneZone.get(2019).get(1L).size(), 2);
    Assertions.assertNotNull(mapProdAnneZone.get(2019).get(2L));
    Assertions.assertEquals(mapProdAnneZone.get(2019).get(2L).size(), 1);

    Assertions.assertNotNull(mapProdAnneZone.get(2020));
    Assertions.assertEquals(mapProdAnneZone.get(2020).size(), 1);
    Assertions.assertNotNull(mapProdAnneZone.get(2020).get(1L));
    Assertions.assertEquals(mapProdAnneZone.get(2020).get(1L).size(), 1);

  }


}

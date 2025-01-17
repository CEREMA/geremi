package fr.cerema.dsi.geremi.services.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.cerema.dsi.geremi.dto.ProductionZone;
import org.springframework.stereotype.Component;

@Component
public class ProductionZoneMapperImpl implements ProductionZoneMapper{

  public Map<Integer, Map<Long, List<ProductionZone>>>  toMapAnneeMapZone(List<ProductionZone> prods) {
    Map<Integer, Map<Long,List<ProductionZone>>> mapProdAnneZone = new HashMap<>();
    if (prods != null) {
      prods.stream().forEach(p -> mapProdAnneZone.computeIfAbsent(p.getAnnee(), (pa) -> new HashMap<>()).computeIfAbsent(p.getIdZone(), (pz) -> new ArrayList<>()).add(p));
    }
    return mapProdAnneZone;
  }

}

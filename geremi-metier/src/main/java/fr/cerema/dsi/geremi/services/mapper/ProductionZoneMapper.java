package fr.cerema.dsi.geremi.services.mapper;

import java.util.List;
import java.util.Map;

import fr.cerema.dsi.geremi.dto.ProductionZone;

public interface ProductionZoneMapper {


  Map<Integer, Map<Long, List<ProductionZone>>>  toMapAnneeMapZone(List<ProductionZone> prods);
}

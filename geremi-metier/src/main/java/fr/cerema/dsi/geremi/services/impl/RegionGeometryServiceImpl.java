package fr.cerema.dsi.geremi.services.impl;

import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import fr.cerema.dsi.geremi.repositories.RegionGeometryRepository;
import fr.cerema.dsi.geremi.services.RegionGeometryService;

@Service
public class RegionGeometryServiceImpl implements RegionGeometryService {

  @Autowired
  private RegionGeometryRepository regionGeometryRepository;

  @Override
  @Cacheable(value ="REGION", key = "#id+#precision")
  public Geometry getGeometryByRegionId(Long id, double precision) {
    return this.regionGeometryRepository.getGeometry(id,precision).getGeometry();
  }
}

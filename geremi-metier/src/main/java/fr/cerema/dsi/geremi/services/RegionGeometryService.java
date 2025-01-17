package fr.cerema.dsi.geremi.services;

import org.locationtech.jts.geom.Geometry;

public interface RegionGeometryService {

  Geometry getGeometryByRegionId(Long id, double precision);
}

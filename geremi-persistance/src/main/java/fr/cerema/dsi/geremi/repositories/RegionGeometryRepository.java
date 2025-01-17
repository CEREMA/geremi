package fr.cerema.dsi.geremi.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.RegionGeometry;

public interface RegionGeometryRepository extends GenericRepository<RegionGeometry,String> {

  @Query(value="SELECT r.id,public.ST_ReducePrecision(r.the_geom,:precision) as the_geom  FROM ref_region_geometry r WHERE r.id=:id",nativeQuery = true)
  RegionGeometry getGeometry(@Param("id") Long id, @Param("precision") double  precision);

}

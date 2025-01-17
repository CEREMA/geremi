package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.dto.ProductionZone;
import fr.cerema.dsi.geremi.entities.RelScenarioInstallation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RelScenarioInstallationRepository extends GenericRepository<RelScenarioInstallation, Integer> {


  @Query(value = """
                  SELECT z.id_zone as idZone,
                         SUM(CASE WHEN i.ton_tot IS NOT NULL AND i.id_stockage IS NOT NULL THEN i.ton_tot ELSE 0 END) as productionZone
                  FROM {h-schema}scenario s
                  INNER JOIN {h-schema}zone z ON z.id_etude = s.id_etude
                  LEFT OUTER JOIN {h-schema}rel_scenario_installation rsi ON s.id_scenario = rsi.id_scenario
                  LEFT OUTER JOIN {h-schema}installation_stockage i
                                  ON i.id_stockage = rsi.id_installation
                                  AND public.ST_Intersects(public.ST_SetSRID(i.the_geom, 4326), public.ST_SetSRID(z.the_geom, 4326))
                                  AND i.annee_debut <= :anneeRef AND i.annee_fin >= :anneeRef
                  WHERE s.id_scenario = :idScenario
                  group by z.id_zone
                  """
    , nativeQuery = true)
  List<ProductionZone> getTotalStocksInstallationIntersectZone(@Param("idScenario") Long idScenario, @Param("anneeRef") Integer anneeRef);
}

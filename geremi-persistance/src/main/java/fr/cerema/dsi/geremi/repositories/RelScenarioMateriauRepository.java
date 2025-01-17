package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import fr.cerema.dsi.geremi.dto.ProductionZone;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.entities.RelScenarioMateriau;

public interface RelScenarioMateriauRepository extends GenericRepository<RelScenarioMateriau, Integer> {


  @Query(value = """
                SELECT z.id_zone as idZone,
                       SUM(CASE WHEN rsm.tonnage IS NOT NULL AND m.id_materiau IS NOT NULL THEN rsm.tonnage ELSE 0 END) as productionZone
                FROM {h-schema}scenario s
                INNER JOIN {h-schema}zone z ON z.id_etude = s.id_etude
                LEFT OUTER JOIN {h-schema}rel_scenario_materiau rsm on rsm.id_scenario = s.id_scenario and rsm.id_zone = z.id_zone
                LEFT OUTER JOIN {h-schema}materiau m ON rsm.id_materiau = m.id_materiau AND m.type = :type AND m.origine IN :origines
                WHERE s.id_scenario = :idScenario
                GROUP BY z.id_zone
                """, nativeQuery = true)
  List<ProductionZone> getMaterialsForScenarioAndZoneByTypeAndOrigin(@Param("idScenario") Long idScenario, @Param("type") String type, @Param("origines") List<String> origines);
}

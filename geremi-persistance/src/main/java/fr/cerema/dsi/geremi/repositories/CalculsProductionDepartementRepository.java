package fr.cerema.dsi.geremi.repositories;

import java.util.List;

import fr.cerema.dsi.commons.repositories.GenericRepository;
import fr.cerema.dsi.geremi.dto.RepartitionDepartementPartielEtude;
import fr.cerema.dsi.geremi.entities.DepartementIntersectEtude;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CalculsProductionDepartementRepository extends GenericRepository<DepartementIntersectEtude, Long> {

  @Query(value = "SELECT * FROM {h-schema}v_departement_intersect_etude d WHERE id_etude = :idEtude " +
    "AND public.ST_AREA(public.ST_REDUCEPRECISION(public.ST_DIFFERENCE(d.dept_geom, (SELECT the_geom FROM territoire t WHERE t.id_etude = :idEtude)),0.000001)) > 0", nativeQuery = true)
  List<DepartementIntersectEtude> getDepartementPartielEtude(Long idEtude);


  @Query(value = """
                SELECT d.nom_departement as nom, d.id_departement as idDepartement,
                       public.ST_AREA(public.st_transform(public.ST_REDUCEPRECISION(public.st_intersection(d.dept_geom,t.the_geom),0.000001),d.dept_srid)) / public.ST_AREA(public.st_transform(public.ST_REDUCEPRECISION(d.dept_geom,0.000001),d.dept_srid)) * 100 as repartitionDepartement
                FROM {h-schema}territoire t
                join {h-schema}v_departement_intersect_etude d on t.id_etude = d.id_etude
                WHERE t.id_etude = :idEtude
                AND public.ST_AREA(public.ST_REDUCEPRECISION(public.ST_DIFFERENCE(d.dept_geom, t.the_geom ),0.000001)) > 0
                """ , nativeQuery = true)
  List<RepartitionDepartementPartielEtude> getRepartionDepartementPartielEtude(@Param("idEtude") Long idEtude);



}

package fr.cerema.dsi.geremi.services.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import fr.cerema.dsi.geremi.entities.Etablissement;
import fr.cerema.dsi.geremi.utils.GeometryUtils;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

 
import org.mapstruct.Mapper;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.Geometry;
 

@Mapper(componentModel = "spring", uses = {})
@Slf4j
public class EtablissementMapper {

	public Feature toFeature(Etablissement entity) {
		Geometry geometry = GeometryUtils.convertJtsGeometryToGeoJson(entity.getGeometry());
		Integer id = entity.getId();
		Map<String, Object> properties = new HashMap<>();

		properties.put("id_etab", entity.getId());
		properties.put("annee", entity.getAnnee());
		properties.put("code_etab", entity.getCode());
		properties.put("nom_etab", entity.getNom());
		properties.put("adresse", entity.getAdresse());
		properties.put("code_postal", String.format("%05d",entity.getCodePostal()));
		properties.put("commune", entity.getCommune());
		properties.put("code_ape", entity.getCodeApe());
		properties.put("activite", entity.getActivite());
		properties.put("type_produits", entity.getTypeProduits());
    properties.put("origin_mat", entity.getOriginMat());
		if (Objects.isNull(entity.getUnite())) {
			properties.put("volume_production", entity.getVolumeProduction());
		} else {
      if(entity.getVolumeProduction() != null){
        properties.put("volume_production", entity.getVolumeProduction() + ' ' + entity.getUnite());
      } else {
        properties.put("volume_production", null);
      }
		}
		properties.put("siret", entity.getSiret());
		properties.put("site_internet", entity.getSiteInternet());
		properties.put("nb_employe", entity.getNbEmploye());
    properties.put("remarques", entity.getRemarques());
    properties.put("commentaire_section", entity.getCommentaireSection());
		properties.put("long", entity.getLongitude());
		properties.put("lat", entity.getLatitude());
		properties.put("date_debut", entity.getDateDebut());
		properties.put("date_fin", entity.getDateFin());
    properties.put("date_fin_autorisation", entity.getDateFinAutorisation());


		log.debug("Converting Etablissement to Feature: {}", entity);

		return new Feature(id, geometry, properties);
	}

  public FeatureCollection toFeatureCollection(List<Etablissement> etabs) {
    List<Feature> featureArrayList = etabs.stream().map(this::toFeature).toList();
    Feature[] features = new Feature[featureArrayList.size()];
    return new FeatureCollection(featureArrayList.toArray(features));
  }

  public FeatureCollection jsonToFeatureCollection(List<Map<String, Object>> etabs) {
    int i = 1;
    List<Feature> featureArrayList = new ArrayList<>();
    for (Map<String, Object> etab : etabs) {
      featureArrayList.add(jsonToFeature(etab,i++));
    }
    Feature[] features = new Feature[featureArrayList.size()];
    return new FeatureCollection(featureArrayList.toArray(features));
  }

  public Feature jsonToFeature(Map<String, Object> etab, int id) {

    Geometry geometry = null;
    if(etab.containsKey("lat") && etab.containsKey("long")) {
      GeometryFactory geometryFactory = new GeometryFactory();
      org.locationtech.jts.geom.Geometry jtsGeometry = geometryFactory.createPoint(new Coordinate(((Number) etab.get("long")).doubleValue(), ((Number) etab.get("lat")).doubleValue()));
      geometry = GeometryUtils.convertJtsGeometryToGeoJson(jtsGeometry);
    }
    return new Feature(id, geometry, etab);
  }
}

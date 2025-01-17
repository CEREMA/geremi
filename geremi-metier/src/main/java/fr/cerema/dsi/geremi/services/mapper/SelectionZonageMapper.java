package fr.cerema.dsi.geremi.services.mapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import fr.cerema.dsi.geremi.dto.SelectionZonage;
import fr.cerema.dsi.geremi.utils.GeometryUtils;
import lombok.extern.slf4j.Slf4j;
import org.geolatte.geom.ByteBuffer;
import org.geolatte.geom.codec.Wkb;
import org.geolatte.geom.jts.JTS;
import org.locationtech.jts.geom.Geometry;
import org.mapstruct.Mapper;
import org.postgresql.util.PGobject;
import org.wololo.geojson.Feature;

@Mapper(componentModel = "spring", uses = {})
@Slf4j
public abstract class SelectionZonageMapper {

  public Feature toFeature(SelectionZonage selectionZonage, boolean exterieur ) {
    Map<String, Object> propertiesInt = new HashMap<>();
    propertiesInt.put("code", selectionZonage.getCode());
    propertiesInt.put("nom", selectionZonage.getNom());
    propertiesInt.put("exterieur", exterieur);

    Serializable geometry = exterieur?selectionZonage.getGeomExt():selectionZonage.getGeomInt();
    Geometry jtsGeom;
    // si la chaine connections JDBC comporte un current schema, hibernate n'arrive pas à déterminer le type
    // et retourne la géométrie sous forme de PGobject contenant le binaire WKB en value. C'est sans doute un bug ???
    // si il n'y a pas de current schema, hibernate ressort une org.geolatte.geom.Geometry
    /// TODO : vérifier dans le futur si on peut se passer de ces différents if pour unifier la solution
    if (geometry instanceof org.geolatte.geom.Geometry) {
      jtsGeom = JTS.to((org.geolatte.geom.Geometry)geometry);
    } else if (geometry instanceof PGobject){
      jtsGeom = JTS.to(Wkb.fromWkb(ByteBuffer.from(((PGobject) geometry).getValue())));
    } else {
      // A priori ca n'arrive jamais, pourtant c'est le type retourné dans les entités... On laisse cette option par defaut au cas où...
      jtsGeom = (Geometry) geometry;
    }

    return new Feature(selectionZonage.getId(), GeometryUtils.convertJtsGeometryToGeoJson(jtsGeom), propertiesInt);
  }

}

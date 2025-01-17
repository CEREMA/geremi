package fr.cerema.dsi.geremi.mapper;

import fr.cerema.dsi.geremi.entities.Etablissement;
import fr.cerema.dsi.geremi.services.mapper.EtablissementMapper;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.wololo.geojson.Feature;
import org.wololo.geojson.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EtablissementMapperTest {

    @Test
    void testToFeature() {
        Etablissement entity = new Etablissement();
        entity.setId(1);
        entity.setCode("code");
        entity.setNom("nom");
        entity.setAdresse("adresse");
        entity.setCodePostal(5698);
        entity.setCommune("commune");
        entity.setActivite("activite");
        entity.setSiret(1L);
        entity.setSiteInternet("siteInternet");
        GeometryFactory geometryFactory = new GeometryFactory();
        entity.setGeometry(geometryFactory.createPoint(new Coordinate(1, 2)));

        EtablissementMapper mapper = new EtablissementMapper();
        Feature feature = mapper.toFeature(entity);

        assertNotNull(feature);
        assertEquals(1, feature.getId());
        assertNotNull(feature.getGeometry());
        assertEquals(Point.class, feature.getGeometry().getClass());
        Point point = (Point) feature.getGeometry();
        assertEquals(1, point.getCoordinates()[0], 1.0);
        assertEquals(2, point.getCoordinates()[1], 2.0);
        assertNotNull(feature.getProperties());
        assertEquals("code", feature.getProperties().get("code_etab"));
        assertEquals("nom", feature.getProperties().get("nom_etab"));
        assertEquals("05698", feature.getProperties().get("code_postal"));


    }
}

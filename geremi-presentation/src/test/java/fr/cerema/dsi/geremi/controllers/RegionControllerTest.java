package fr.cerema.dsi.geremi.controllers;

import fr.cerema.dsi.geremi.mapper.RegionMapper;
import fr.cerema.dsi.geremi.services.RegionService;
import fr.cerema.dsi.geremi.services.dto.RegionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RegionControllerTest {

	@InjectMocks
	private RegionController regionController;

	@Mock
	private RegionService regionService;

	@Mock
	private RegionMapper regionMapper;

	private List<Feature> featureList = null;

	private RegionDTO entity1 = new RegionDTO();

	private RegionDTO entity2 = new RegionDTO();

	private Feature feature1;

	private Feature feature2;

	private List<String> bbox;

	private FeatureCollection featureCollection;

	@BeforeEach
	void setUp() {
		featureList = new ArrayList<>();
		GeometryFactory geometryFactory = new GeometryFactory();
		RegionMapper mapper = new RegionMapper();

		entity1.setId(1L);
		entity1.setCode("code1");
		entity1.setNom("nom1");
		entity1.setGeometry(geometryFactory.createPoint(new Coordinate(1, 2)));

		entity2.setId(2L);
		entity2.setCode("code2");
		entity2.setNom("nom2");
		entity2.setGeometry(geometryFactory.createPoint(new Coordinate(11, 22)));

		feature1 = mapper.toFeature(entity1);
		feature2 = mapper.toFeature(entity2);

		featureList.add(feature1);
		featureList.add(feature2);

		bbox = Arrays.asList("1.0", "2.0", "3.0", "4.0");

		Feature[] features = { feature1, feature2 };

		featureCollection = new FeatureCollection(features);
	}


	@Test
	void testGetRegion() {
		when(regionService.findInBox(1.0, 2.0, 3.0, 4.0, new BigDecimal("0.1"))).thenReturn(Arrays.asList(feature1, feature2));

		ResponseEntity<FeatureCollection> response = regionController.getRegion(bbox,"0.1");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(isEqual(featureCollection, response.getBody()));
	}

	private boolean isEqual(FeatureCollection actual, FeatureCollection expected) {
		if (actual.getFeatures().length != expected.getFeatures().length) {
			return false;
		}
		for (int i = 0; i < actual.getFeatures().length; i++) {
			if (!actual.getFeatures()[i].equals(expected.getFeatures()[i])) {
				return false;
			}
		}
		return true;
	}

}

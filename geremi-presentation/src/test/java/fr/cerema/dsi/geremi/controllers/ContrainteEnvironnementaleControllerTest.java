package fr.cerema.dsi.geremi.controllers;

import fr.cerema.dsi.geremi.entities.ContrainteEnvironnementale;
import fr.cerema.dsi.geremi.services.ContrainteEnvironnementaleService;
import fr.cerema.dsi.geremi.services.mapper.ContrainteEnvironnementaleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ContrainteEnvironnementaleControllerTest {

	@Mock
	private ContrainteEnvironnementaleService contrainteEnvironnementaleService;

	@InjectMocks
	private ContrainteEnvironnementaleController contrainteEnvironnementaleController;

	@Mock
	private ContrainteEnvironnementaleMapper contrainteEnvironnementaleMapper;

	private MockMvc mockMvc;

	private ContrainteEnvironnementale entity1 = new ContrainteEnvironnementale();

	private ContrainteEnvironnementale entity2 = new ContrainteEnvironnementale();

	private Feature feature1;

	private Feature feature2;

	private FeatureCollection featureCollection;

	@BeforeEach
	public void setup() {

		mockMvc = MockMvcBuilders.standaloneSetup(contrainteEnvironnementaleController).build();
		when(contrainteEnvironnementaleService.getContrainteEnvironnementaleByIdTerritoire(any(Long.class)))
				.thenReturn(Optional.of(Collections.emptyList()));

		entity1.setIdContrEnv(1);
		entity2.setIdContrEnv(2);

		feature1 = contrainteEnvironnementaleMapper.toFeature(entity1);
		feature2 = contrainteEnvironnementaleMapper.toFeature(entity2);

		Feature[] features = { feature1, feature2 };

		featureCollection = new FeatureCollection(features);
	}

	@Test
	public void testGetContrainteEnvironnementaleByIdTerritoire() throws Exception {
		// Given
		Long idTerritoire = 1L;
		ArrayList<Feature> features = new ArrayList<>();
		Optional<List<Feature>> optionalFeatures = Optional.of(features);

		// When
		when(contrainteEnvironnementaleService.getContrainteEnvironnementaleByIdTerritoire(idTerritoire))
				.thenReturn(optionalFeatures);

		// Then
		mockMvc.perform(get("/api/contrainteEnvironnementale/" + idTerritoire)).andExpect(status().isOk()).andExpect(
				content().xml("<FeatureCollection><type>FeatureCollection</type><features/></FeatureCollection>"));

		verify(contrainteEnvironnementaleService, times(1)).getContrainteEnvironnementaleByIdTerritoire(idTerritoire);
	}

	@Test
	void testGetContrainteEnvironnementaleByIdTerritoireReturnsEmptyList() {
		ResponseEntity<FeatureCollection> responseEntity = contrainteEnvironnementaleController
				.getContrainteEnvironnementaleByIdTerritoire(1L);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());
		assertEquals(0, responseEntity.getBody().getFeatures().length);
	}



}

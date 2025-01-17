package fr.cerema.dsi.geremi.config.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.repositories.ZoneRepository;
import fr.cerema.dsi.geremi.services.EtudeService;
import fr.cerema.dsi.geremi.services.ZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.springframework.core.io.Resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OdsFileServiceTest {

	@Mock
	private ZoneRepository zoneRepository;

	@Mock
	private EtudeService etudeService;

	@Mock
	private ZoneService zoneService;

	@InjectMocks
	private OdsFileService odsFileService;

	private Zone zone1;

	private Zone zone2;

	private Zone zone3;

	@BeforeEach
	public void setUp() {
		zone1 = new Zone();
		zone1.setId(1L);
		zone1.setType("type1");
		zone1.setNom("nom1");
		zone1.setDescription("description1");
		zone1.setCode("code1");

		zone2 = new Zone();
		zone2.setId(2L);
		zone2.setType("type2");
		zone2.setNom("nom2");
		zone2.setDescription("description2");
		zone2.setCode("code2");

		zone3 = new Zone();
		zone3.setId(3L);
		zone3.setType("type3");
		zone3.setNom("nom3");
		zone3.setDescription("description3");
		zone3.setCode("code3");
	}

	@Test
	public void testGetOdsForEtude() throws Exception {
	    // Given
	    Long id = 1L;
	    List<Zone> zones = Arrays.asList(zone1, zone2, zone3);
	    Etude etude = new Etude();
	    etude.setId(id);
	    etude.setAnneeRef(2020);
	    etude.setAnneeFin(2022);
	    when(etudeService.findById(anyLong())).thenReturn(etude);
	    when(zoneRepository.findZoneByEtude(id)).thenReturn(zones);

	    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("modele_population.ods");
	    assertNotNull(inputStream);

	    // When
	    Resource resource = odsFileService.getOdsForEtude(id);

	    // Then
	    verify(etudeService).findById(id);
	    verify(zoneRepository).findZoneByEtude(id);
	    verifyNoMoreInteractions(etudeService, zoneRepository, zoneService);

	    assertNotNull(resource);

	    SpreadsheetDocument spreadSheet = SpreadsheetDocument.loadDocument(resource.getInputStream());
	    assertNotNull(spreadSheet);

	    Table sheet = spreadSheet.getTableByName("Export");
	    assertNotNull(sheet);
	    assertEquals("Export", sheet.getTableName());

	    Row headerRow = sheet.getRowByIndex(0);
	    assertNotNull(headerRow);
	    assertEquals("code_zone", headerRow.getCellByIndex(0).getDisplayText());
	    assertEquals("nom_zone", headerRow.getCellByIndex(1).getDisplayText());
	    assertEquals("annee", headerRow.getCellByIndex(2).getDisplayText());
	    assertEquals("population_basse", headerRow.getCellByIndex(3).getDisplayText());
	    assertEquals("population_centrale", headerRow.getCellByIndex(4).getDisplayText());
	    assertEquals("population_haute", headerRow.getCellByIndex(5).getDisplayText());

	    Row row1 = sheet.getRowByIndex(1);
	    assertNotNull(row1);
	    assertEquals("code1", row1.getCellByIndex(0).getDisplayText());
	    assertEquals("nom1", row1.getCellByIndex(1).getDisplayText());
	    assertEquals("2020", row1.getCellByIndex(2).getDisplayText());
	    assertEquals("", row1.getCellByIndex(3).getDisplayText());
	    assertEquals("", row1.getCellByIndex(4).getDisplayText());
	    assertEquals("", row1.getCellByIndex(5).getDisplayText());

	    Row row2 = sheet.getRowByIndex(2);
	    assertNotNull(row2);
	    assertEquals("code1", row2.getCellByIndex(0).getDisplayText());
	    assertEquals("nom1", row2.getCellByIndex(1).getDisplayText());
	    assertEquals("2021", row2.getCellByIndex(2).getDisplayText());
	    assertEquals("", row2.getCellByIndex(3).getDisplayText());
	    assertEquals("", row2.getCellByIndex(4).getDisplayText());
	    assertEquals("", row2.getCellByIndex(5).getDisplayText());

	    Row row3 = sheet.getRowByIndex(3);
	    assertNotNull(row3);
	    assertEquals("code1", row3.getCellByIndex(0).getDisplayText());
	    assertEquals("nom1", row3.getCellByIndex(1).getDisplayText());
	    assertEquals("2022", row3.getCellByIndex(2).getDisplayText());
	    assertEquals("", row3.getCellByIndex(3).getDisplayText());
	    assertEquals("", row3.getCellByIndex(4).getDisplayText());
	    assertEquals("", row3.getCellByIndex(5).getDisplayText());

	}




}

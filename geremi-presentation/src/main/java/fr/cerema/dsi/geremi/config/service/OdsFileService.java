package fr.cerema.dsi.geremi.config.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.services.dto.PopulationDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OdsFileService extends FileService implements IFileService {

  @Transactional(propagation = Propagation.REQUIRED)
  public Resource getOdsForEtude(Long id) {

		List<Zone> zones = zoneRepository.findZoneByEtude(id);
		Etude etude = etudeService.findById(id);
		int anneeRef = etude.getAnneeRef();
		int anneeFin = etude.getAnneeFin();
        SpreadsheetDocument spreadSheet = null;

		try {
			File templateFile = new File(TEMPLATE_FILENAME_ODS);
			spreadSheet = SpreadsheetDocument.newSpreadsheetDocument();
			Table sheet = spreadSheet.getTableByName("Sheet1");
			sheet.setTableName(SHEET_NAME);

			// Ajoutez le header à la feuille de calcul
			for (int i = 0; i < columns.length; i++) {
				Cell cell = sheet.getCellByPosition(i, 0);
				cell.setStringValue(columns[i]);
			}

			// Ajoutez les données à la feuille de calcul
			for (Zone zone : zones) {
				for (int annee = anneeRef; annee <= anneeFin; annee++) {
					Row row = sheet.appendRow();
					row.getCellByIndex(0).setStringValue(zone.getCode());
					row.getCellByIndex(1).setStringValue(zone.getNom());
					row.getCellByIndex(2).setStringValue(String.valueOf(annee));
					row.getCellByIndex(3).setStringValue("");
					row.getCellByIndex(4).setStringValue("");
					row.getCellByIndex(5).setStringValue("");
				}
			}

			// Enregistrez le fichier ODS
			spreadSheet.save(templateFile);

			// Créez une ressource FileSystemResource pour le fichier ODS
			FileSystemResource resource = new FileSystemResource(templateFile);

			return resource;
		} catch (Exception e) {
			log.error("Erreur lors de la génération du fichier ODS pour l'étude {}", id, e);
		}finally {
      if(Objects.nonNull(spreadSheet)){
        spreadSheet.close();
      }
    }
    return null;
	}


	@Override
	public List<PopulationDTO> checkFileColumns(Long id, MultipartFile file, Map<String, Zone> zones) throws ImportTypesException {
		Map<Integer, Integer> yearRowCounts = new HashMap<Integer, Integer>();
		return checkODSFileColumns(id, file, yearRowCounts, zones);
	}

	private List<PopulationDTO> checkODSFileColumns(Long id, MultipartFile file, Map<Integer, Integer> yearRowCounts,
			Map<String, Zone> zones) throws ImportTypesException {

		Set<String> foundZoneCodes = new HashSet<>();
		Map<String, Set<Integer>> zoneYears = new HashMap<>();
		List<PopulationDTO> populationDataList = new ArrayList<>();
		try {
			// Charger le fichier ODS
			SpreadsheetDocument document;
			Table sheet = null;
			try {
				document = SpreadsheetDocument.loadDocument(file.getInputStream());
				sheet = document.getSheetByIndex(0);
			} catch (Exception e1) {
				log.error("Erreur lors de la lecture du fichier ODS", e1);
			}
			// Vérification du Header.
			Row headerRow = sheet.getRowByIndex(0);
			Cell[] headers = new Cell[columns.length];
			String[] headerValues = new String[columns.length];

			for (int i = 0; i < columns.length; i++) {
				headers[i] = headerRow.getCellByIndex(i);
				headerValues[i] = headers[i] != null ? headers[i].getStringValue().trim() : null;
				if (headerValues[i] == null || !headerValues[i].equalsIgnoreCase(columns[i])) {
					// Le Header n'est pas valide.
					throw new ImportTypesException(String.format(ENTETE, columns[i]));
				}
			}
			// Récupérer les années de la période d'étude
			Etude etude = etudeService.findById(id);
			int anneeRef = etude.getAnneeRef();
			int anneeFin = etude.getAnneeFin();
			int rowCount = sheet.getRowCount();
			// SMART-532: l'import ne vérifie pas le nombre de lignes du fichier
			  int expectedRowCount = zones.size() * (anneeFin - anneeRef + 1);
		        if (rowCount != expectedRowCount + 1) { // On ajoute 1 pour le header
		            throw new ImportTypesException(String.format(LIGNEATTENDUE, expectedRowCount, rowCount-1));
		        }

			// Vérification du Contenue.
			Set<String> processedRows = new HashSet<>();
			Set<String> processedZonneAnneRows = new HashSet<>();
			for (int i = 1; i < rowCount; i++) {
				Row row = sheet.getRowByIndex(i);

				// SMART-532: Vérification des Doublons du couple zone/annee
				StringBuilder sbZonneAnne = new StringBuilder();
				Cell cellCodeZone = row.getCellByIndex(0);
				Cell cellAnnee = row.getCellByIndex(2);
				sbZonneAnne.append(cellCodeZone == null ? "" : cellCodeZone.getStringValue().trim()).append("|");
				sbZonneAnne.append(cellAnnee == null ? "" : cellAnnee.getStringValue().trim());
				String rowStringZonneAnne = sbZonneAnne.toString();
				// Vérification si la ligne a déjà été traitée.
				if (processedZonneAnneRows.contains(rowStringZonneAnne)) {
				    throw new ImportTypesException(String.format(LIGNEDUPLIQUE, row.getCellByIndex(0).getStringValue().trim(), row.getCellByIndex(2).getStringValue().trim()));
				} else {
				    // ligne non dupliquée.
					processedZonneAnneRows.add(rowStringZonneAnne);
				}

				// Vérification des Doublons de ligne
				StringBuilder sbLigne = new StringBuilder();
				for (int j = 0; j < columns.length; j++) {
					Cell cell = row.getCellByIndex(j);
					sbLigne.append(cell == null ? "" : cell.getStringValue().trim()).append("|");
				}
				String rowString = sbLigne.toString();
				// Vérification si la ligne a déjà été traité.
				if (processedRows.contains(rowString)) {
					log.info("Ligne dupliqué: {}", rowString);
					continue;
				} else {
					// ligne non dupliqué.
					processedRows.add(rowString);
				}

				for (int j = 0; j < columns.length; j++) {
					Cell cell = row.getCellByIndex(j);
					String cellValue = cell == null ? null : cell.getStringValue();
					if (cellValue == null || cellValue.trim().isEmpty()) {
						// La ligne contient une valeur manquante, donc elle n'est pas valide
						throw new ImportTypesException(String.format(FORMAT, getColumnName(j), i + 1));
					}
				}

				// SMART-517 : Chaque zone de l'étude doit être dans le fichier
				foundZoneCodes.add(row.getCellByIndex(0).getStringValue().trim());

				int populationBasseInt;
				int populationCentraleInt;
				int populationHauteInt;
				int parsedYear;

				Cell code_zone = row.getCellByIndex(0);
				Cell nom_zone = row.getCellByIndex(1);
				Cell annee = row.getCellByIndex(2);
				Cell population_basse = row.getCellByIndex(3);
				Cell population_centrale = row.getCellByIndex(4);
				Cell population_haute = row.getCellByIndex(5);

				// Vérification Code Zone
				final int rowIndex = i; // Création d'une variable final pour l'index de la ligne, pour l'optionnal
				Zone zone = zones.get(code_zone.getStringValue().trim());
				Optional<Zone> optionalZone = Optional.ofNullable(zone);
				optionalZone.orElseThrow(() -> new ImportTypesException(String.format(CODEZONE, code_zone.getStringValue().trim(), rowIndex)));

				// Vérification des formats
				try {
					parsedYear = Integer.parseInt(annee.getStringValue().trim());
				} catch (NumberFormatException e) {
					// L'année n'est pas un entier valide
					throw new ImportTypesException(String.format(ANNEEINC, annee.getStringValue().trim()));
				}

				if (parsedYear < anneeRef || parsedYear > anneeFin) {
					// L'année est hors de la période d'étude, donc la ligne est ignorée
					log.warn("L'année {} est hors de la période d'étude ({}-{}). Elle sera ignorée.", parsedYear,
							anneeRef, anneeFin);
					continue;
				}

				yearRowCounts.put(parsedYear, yearRowCounts.getOrDefault(parsedYear, 0) + 1);

				// SMART-517 : Chaque année de l'étude doit être dans le fichier
				String zoneCode = row.getCellByIndex(0).getStringValue().trim();
				Set<Integer> years = zoneYears.getOrDefault(zoneCode, new HashSet<>());
				years.add(parsedYear);
				zoneYears.put(zoneCode, years);

				try {
					populationBasseInt = Math.round(Float.parseFloat(population_basse.getStringValue().trim()));
				} catch (NumberFormatException e) {
					// L'une des populations n'est pas un nombre valide (entier ou décimal)
					throw new ImportTypesException(String.format(POPULATIONINC, BASSE, population_basse.getStringValue().trim(), i));
				}
				try {
					populationCentraleInt = Math.round(Float.parseFloat(population_centrale.getStringValue().trim()));
				} catch (NumberFormatException e) {
					// L'une des populations n'est pas un nombre valide (entier ou décimal)
					throw new ImportTypesException(String.format(POPULATIONINC, CENTRALE, population_centrale.getStringValue().trim(), i));
				}
				try {
					populationHauteInt = Math.round(Float.parseFloat(population_haute.getStringValue().trim()));
				} catch (NumberFormatException e) {
					// L'une des populations n'est pas un nombre valide (entier ou décimal)
					throw new ImportTypesException(String.format(POPULATIONINC, HAUTE, population_haute.getStringValue().trim(), i));
				}

				// Vérification que pour chaque zone et chaque année: ${population_basse} <=
				// ${population_centrale} <= ${population_haute}
				if (!(populationBasseInt <= populationCentraleInt && populationCentraleInt <= populationHauteInt)) {
					throw new ImportTypesException(COMPARPOPULATION);
				}

		        if(Objects.nonNull(zone)) {
		          PopulationDTO populationData = new PopulationDTO(code_zone.getStringValue().trim(),
		            nom_zone.getStringValue().trim(), parsedYear, populationBasseInt, populationCentraleInt,
		            populationHauteInt, zone.getId(), id);
		          populationDataList.add(populationData);
		        }
			}

			  // SMART-517 : Chaque zone de l'étude doit être dans le fichier
			 for (String zoneCode : zones.keySet()) {
			        if (!foundZoneCodes.contains(zoneCode)) {
			            throw new ImportTypesException(String.format(POPULATION,anneeRef, anneeFin, zones.size() * ((anneeFin - anneeRef) + 1)));
			        }
			    }
			// SMART-517 : Chaque année de l'étude doit être dans le fichier
			 for (int year = anneeRef; year <= anneeFin; year++) {
				    for (String zoneCode : zones.keySet()) {
				        Set<Integer> years = zoneYears.get(zoneCode);
				        if (years == null || !years.contains(year)) {
				            throw new ImportTypesException(String.format(POPULATION,anneeRef, anneeFin, zones.size() * ((anneeFin - anneeRef) + 1)));
				        }
				    }
				}


			if (yearRowCounts.isEmpty()) {
				throw new ImportTypesException(String.format(TABLEAUVIDE));
			}
			// Vérification de la répartition égale des lignes par année
			int expectedRowCountAnne = Collections.max(yearRowCounts.values());
			for (Map.Entry<Integer, Integer> entry : yearRowCounts.entrySet()) {
				if (!entry.getValue().equals(expectedRowCountAnne)) {
					throw new ImportTypesException(String.format(LIGNEANNEE, entry.getKey()));
				}
			}

		} catch (NullPointerException e) {
			log.error("Erreur lors de la lecture du fichier ODS", e);
			throw new ImportTypesException(FICHIERCORROMPU);
		}

		return populationDataList;
	}
}

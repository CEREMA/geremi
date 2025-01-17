package fr.cerema.dsi.geremi.config.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.services.dto.PopulationDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExcelFileService extends FileService implements IFileService {

  private static final Logger logger = LoggerFactory.getLogger(ExcelFileService.class);

	@Override
	public List<PopulationDTO> checkFileColumns(Long id, MultipartFile file, Map<String, Zone> zones) throws ImportTypesException {
		Map<Integer, Integer> yearRowCounts = new HashMap<Integer, Integer>();
		return checkExcelFileColumns(id, file, yearRowCounts, zones);
	}

	private List<PopulationDTO> checkExcelFileColumns(Long id, MultipartFile file, Map<Integer, Integer> yearRowCounts,
			Map<String, Zone> zones) throws ImportTypesException {

		Set<String> foundZoneCodes = new HashSet<>();
		Map<String, Set<Integer>> zoneYears = new HashMap<>();
		List<PopulationDTO> populationDataList = new ArrayList<>();
		Workbook workbook = null;
		try {
			// Charger le fichier Excel
			workbook = WorkbookFactory.create(file.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);

			// Vérification du Header.
			Row headerRow = sheet.getRow(0);
			Cell[] cells = new Cell[columns.length];
			boolean isHeaderValid = true;
			StringBuilder invalidColumns = new StringBuilder();

			for (int i = 0; i < columns.length; i++) {
			    cells[i] = headerRow.getCell(i);
			    if (cells[i] == null || !cells[i].getStringCellValue().trim().equalsIgnoreCase(columns[i])) {
			        isHeaderValid = false;
			        invalidColumns.append(columns[i]).append(" ");
			    }
			}

			if (!isHeaderValid) {
			    // Le Header n'est pas valide.
			    throw new ImportTypesException(String.format(ENTETE, invalidColumns.toString().trim()));
			}

			// Récupérer les années de la période d'étude
			Etude etude = etudeService.findById(id);
			int anneeRef = etude.getAnneeRef();
			int anneeFin = etude.getAnneeFin();
			int rowCount = sheet.getLastRowNum();

			// SMART-532: l'import ne vérifie pas le nombre de lignes du fichier
			  int expectedRowCount = zones.size() * (anneeFin - anneeRef + 1);
		        if (rowCount != expectedRowCount) { // On ajoute 1 pour le header
		            throw new ImportTypesException(String.format(LIGNEATTENDUE, expectedRowCount, rowCount-1));
		        }

			// Vérification du Contenue.
			Set<String> processedRows = new HashSet<>();
			Set<String> processedZonneAnneRows = new HashSet<>();
			for (int i = 1; i <= rowCount; i++) {
				Row row = sheet.getRow(i);

				//SMART-532:  Vérification des Doublons du couple zone/annee
				StringBuilder sbZonneAnne = new StringBuilder();
				Cell cellCodeZone = row.getCell(0);
				Cell cellAnnee = row.getCell(2);
				sbZonneAnne.append(cellCodeZone == null ? "" : cellCodeZone.toString().trim()).append("|");
				sbZonneAnne.append(cellAnnee == null ? "" : cellAnnee.toString().trim());
				String rowStringZonneAnne = sbZonneAnne.toString();
				// Vérification si la ligne a déjà été traitée.
				if (processedZonneAnneRows.contains(rowStringZonneAnne)) {
				    throw new ImportTypesException(String.format(LIGNEDUPLIQUE, row.getCell(0).toString().trim(), row.getCell(2).toString().trim()));
				} else {
				    // ligne non dupliquée.
					processedZonneAnneRows.add(rowStringZonneAnne);
				}

				// Vérification des Doublons ligne
				StringBuilder sbLigne = new StringBuilder();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					sbLigne.append(cell.toString().trim()).append("|");
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
					Cell cell = row.getCell(j);
					String cellValue = cell == null ? "" : cell.toString().trim();
					if (cellValue.isEmpty()) {
						// La ligne contient une valeur manquante, donc elle n'est pas valide
						throw new ImportTypesException(String.format(FORMAT, getColumnName(j), i + 1));
					}
				}

				// SMART-517 : Chaque zone de l'étude doit être dans le fichier
				foundZoneCodes.add(row.getCell(0).toString().trim());

				int populationBasseInt;
				int populationCentraleInt;
				int populationHauteInt;
				int parsedYear;

				Cell code_zone = row.getCell(0);
				Cell nom_zone = row.getCell(1);
				Cell annee = row.getCell(2);
				Cell population_basse = row.getCell(3);
				Cell population_centrale = row.getCell(4);
				Cell population_haute = row.getCell(5);

				// Vérification Code Zone
				final int rowIndex = i; // Création d'une variable final pour l'index de la ligne, pour l'optionnal
				Zone zone = zones.get(code_zone.toString().trim());
				Optional<Zone> optionalZone = Optional.ofNullable(zone);
				optionalZone.orElseThrow(() -> new ImportTypesException(
						String.format(CODEZONE, code_zone.toString().trim(), rowIndex)));

				// Vérification des formats
				try {
					parsedYear = Integer.parseInt(annee.toString().trim());
				} catch (NumberFormatException e) {
					// L'année n'est pas un entier valide
					throw new ImportTypesException(String.format(ANNEEINC, annee.toString().trim()));
				}

				if (parsedYear < anneeRef || parsedYear > anneeFin) {
					// L'année est hors de la période d'étude, donc la ligne est ignorée
					log.warn("L'année {} est hors de la période d'étude ({}-{}). Elle sera ignorée.", parsedYear,
							anneeRef, anneeFin);
					continue;
				}

				yearRowCounts.put(parsedYear, yearRowCounts.getOrDefault(parsedYear, 0) + 1);
				// SMART-517 : Chaque année de l'étude doit être dans le fichier
				String zoneCode = row.getCell(0).toString().trim();
				Set<Integer> years = zoneYears.getOrDefault(zoneCode, new HashSet<>());
				years.add(parsedYear);
				zoneYears.put(zoneCode, years);

				try {
					populationBasseInt = Math.round(Float.parseFloat(population_basse.toString().trim()));
				} catch (NumberFormatException e) {
					// L'une des populations n'est pas un nombre valide (entier ou décimal)
					throw new ImportTypesException(String.format(POPULATIONINC, BASSE, population_basse.toString().trim(), i));
				}
				try {
					populationCentraleInt = Math.round(Float.parseFloat(population_centrale.toString().trim()));
				} catch (NumberFormatException e) {
					// L'une des populations n'est pas un nombre valide (entier ou décimal)
					throw new ImportTypesException(String.format(POPULATIONINC, CENTRALE, population_centrale.toString().trim(), i));
				}
				try {
					populationHauteInt = Math.round(Float.parseFloat(population_haute.toString().trim()));
				} catch (NumberFormatException e) {
					// L'une des populations n'est pas un nombre valide (entier ou décimal)
					throw new ImportTypesException(String.format(POPULATIONINC, HAUTE, population_haute.toString().trim(), i));
				}

				// Vérification que pour chaque zone et chaque année: ${population_basse} <=
				// ${population_centrale} <= ${population_haute}
				if (!(populationBasseInt <= populationCentraleInt && populationCentraleInt <= populationHauteInt)) {
					throw new ImportTypesException(COMPARPOPULATION);
				}

		        if(Objects.nonNull(zone)){
		          PopulationDTO populationData = new PopulationDTO(code_zone.toString().trim(),
		            nom_zone.toString().trim(), parsedYear, populationBasseInt, populationCentraleInt,
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

		} catch (IOException e) {
			log.error("Erreur lors de la lecture du fichier Excel", e);
			throw new ImportTypesException(FICHIERCORROMPU);
		} finally {
      if(workbook != null){
        try {
          workbook.close();
        } catch (IOException e) {
          logger.error("Erreur durant la lectue du fichier excel",e);
        }
      }
    }

		return populationDataList;
	}

}

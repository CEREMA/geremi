package fr.cerema.dsi.geremi.config.service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.repositories.ZoneRepository;
import fr.cerema.dsi.geremi.services.EtudeService;
import fr.cerema.dsi.geremi.services.TerritoireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

	protected final String TEMPLATE_FILENAME_ODS = "modele_population.ods";

	protected final String CODE_ZONE = "code_zone";

	protected final String NOM_ZONE = "nom_zone";

	protected final String ANNEE = "annee";

	protected final String POPULATION_BASSE = "population_basse";

	protected final String POPULATION_CENTRALE = "population_centrale";

	protected final String POPULATION_HAUTE = "population_haute";

	protected final String ODS = ".ods";

	protected final String XLS = ".xls";

	protected final String XLSX = ".xlsx";

	protected final String SHEET_NAME = "Export";

	protected final String BASSE = "basse";

	protected final String CENTRALE = "centrale";

	protected final String HAUTE = "haute";

	@Value("${geremi.config.message.erreur.import.demographie.success}")
	protected String SUCCESS;

	@Value("${geremi.config.message.erreur.import.demographie.entete.invalide}")
	protected String ENTETE;

	@Value("${geremi.config.message.erreur.import.demographie.format.fichier.invalide}")
	protected String FORMATFICHIER;

	@Value("${geremi.config.message.erreur.import.demographie.donnees.absentes}")
	protected String POPULATION;

	@Value("${geremi.config.message.erreur.import.demographie.format.donnee.invalide}")
	protected String FORMAT;

	@Value("${geremi.config.message.erreur.import.demographie.annee.invalide}")
	protected String ANNEEINC;

	@Value("${geremi.config.message.erreur.import.demographie.population.invalide}")
	protected String POPULATIONINC;

	@Value("${geremi.config.message.erreur.import.demographie.populations.incoherentes}")
	protected String COMPARPOPULATION;

	@Value("${geremi.config.message.erreur.import.demographie.fichier.corrompu}")
	protected String FICHIERCORROMPU;

	@Value("${geremi.config.message.erreur.import.demographie.annee.manquante}")
	protected String LIGNEANNEE;

	@Value("${geremi.config.message.erreur.import.demographie.tableau.vide}")
	protected String TABLEAUVIDE;

	@Value("${geremi.config.message.erreur.import.demographie.codezone.invalide}")
	protected String CODEZONE;

	@Value("${geremi.config.message.erreur.import.demographie.ligne.dupliquee}")
	protected String LIGNEDUPLIQUE;

	@Value("${geremi.config.message.erreur.import.demographie.nblignes.invalide}")
	protected String LIGNEATTENDUE;

	@Autowired
	protected TerritoireService territoireService;

	@Autowired
	protected EtudeService etudeService;

	@Autowired
  protected  ZoneRepository zoneRepository;


	protected String[] columns = { CODE_ZONE, NOM_ZONE, ANNEE, POPULATION_BASSE, POPULATION_CENTRALE,
			POPULATION_HAUTE };

	protected String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Code Zone";
		case 1:
			return "Nom Zone";
		case 2:
			return "Année";
		case 3:
			return "Population Basse";
		case 4:
			return "Population Centrale";
		case 5:
			return "Population Haute";
		default:
			return "Colonne Inconnue";
		}

	}

	protected String checkFileExtension(MultipartFile file) throws ImportTypesException {
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null) {
			throw new ImportTypesException(FORMATFICHIER);
		}

		String extension = "";
		if (originalFilename.toLowerCase().endsWith(ODS)) {
			extension = ODS;
		} else if (originalFilename.toLowerCase().endsWith(XLS)) {
			extension = XLS;
		} else if (originalFilename.toLowerCase().endsWith(XLSX)) {
			extension = XLSX;
		} else {
			throw new ImportTypesException(FORMATFICHIER);
		}

		return extension;
	}

	protected Map<String, Zone> findZonesByCode (Long id){

		return  this.zoneRepository.findZonesByCode(id)
				 .stream()
				 .collect(Collectors.toMap(Zone::getCode, Function.identity()));
	}

}

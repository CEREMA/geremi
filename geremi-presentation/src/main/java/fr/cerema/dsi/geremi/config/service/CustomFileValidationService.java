package fr.cerema.dsi.geremi.config.service;

import static fr.cerema.dsi.geremi.enums.AttributImportControl.ANNEE_DEBUT;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.ANNEE_FIN;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.BETON_PREF;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.CODE_ETAB;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.CODE_ZONE;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.CPG;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.DBF;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.DESCRIP;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.EPSG_WGS_84;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.FAIBLE;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.FORTE;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.GCS_WGS_1984;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.GPKG;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.MOYENNE;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.NIVEAU;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.NOM;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.NOM_ETAB;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.NOM_ZONE;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.PRJ;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.SHP;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.SHX;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.THEGEOM;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.TON_TOT;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.UTF8;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.VIAB_AUTRE;
import static fr.cerema.dsi.geremi.enums.AttributImportControl.WGS_84;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fr.cerema.dsi.commons.datastore.Attribut;
import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.datastore.GeoPkgReader;
import fr.cerema.dsi.commons.datastore.SQLiteTableReader;
import fr.cerema.dsi.commons.datastore.ShpReader;
import fr.cerema.dsi.commons.datastore.entities.Resultats;
import fr.cerema.dsi.commons.exceptions.ImportZonageGeometryException;
import fr.cerema.dsi.commons.exceptions.ImportZonageLayerException;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.services.EtudeService;
import fr.cerema.dsi.geremi.services.TerritoireService;
import fr.cerema.dsi.geremi.services.dto.EtudeDTO;
import fr.cerema.dsi.geremi.utils.ValidationImportResult;

@Service
public class CustomFileValidationService {

	private static final Logger logger = LoggerFactory.getLogger(CustomFileValidationService.class);

	private final ShpReader shpReader;

	private final GeoPkgReader geoPkgReader;

	private final SQLiteTableReader sQLiteTableReader;

	private final static String ZONE = "zone";

	private final static String CONTRAINTE = "contrainte";

	private final static String CHANTIER = "chantier";

	private final static String STOCKAGE = "stockage";

	private final static String IMPORTETUDE = "importEtude";

	private final static String T_RESULTATS = "resultats";

	@Value("${geremi.config.message.erreur.import.zip}")
	String messageErreurDeZip;
	@Value("${geremi.config.message.erreur.import.geometrytype}")
	String messageTypesGeometry;
	@Value("${geremi.config.message.erreur.import.types}")
	String messageTypesFiles;
	@Value("${geremi.config.message.erreur.import.noms}")
	String messageNomsFiles;
	@Value("${geremi.config.message.erreur.import.projection}")
	String messageProjection;
	@Value("${geremi.config.message.erreur.import.encodage}")
	String messageEncodage;
  @Value("${geremi.config.message.erreur.import.contraintes.attributs}")
  String messageAttributsContrainte;
	@Value("${geremi.config.message.erreur.import.contraintes.attributs.vides}")
	String messageAttributsVidesContrainte;
	@Value("${geremi.config.message.erreur.import.nomtable.gpkg}")
	String messageNomTableNonCorrespond;
	@Value("${geremi.config.message.erreur.import.contraintes.niveau.doublon}")
	String messageNiveauContrainteDoublon;
	@Value("${geremi.config.message.erreur.import.contraintes.niveau.invalide}")
	String messageInvalidContrainte;
	@Value("${geremi.config.message.erreur.import.chantiers.attributs}")
	String messageAttributsChantier;
	@Value("${geremi.config.message.erreur.import.etude.attributs}")
	String messageAttributsImportEtude;
  @Value("${geremi.config.message.erreur.import.etude.attributs.vides}")
  String messageAttributsVidesImportEtude;
	@Value("${geremi.config.message.erreur.import.chantiers.attributs.vides}")
	String messageAttributsVideChantier;
	@Value("${geremi.config.message.erreur.import.zonage.attributs}")
	String messageAttributsZonage;
	@Value("${geremi.config.message.erreur.import.zonage.attributs.vides}")
	String messageAttributsVideZonage;
  @Value("${geremi.config.message.erreur.import.zonage.codes.doublons}")
  String messageCodesDoublonsZonage;
	@Value("${geremi.config.message.erreur.import.geometryintrouvable}")
	String messageGeometryIntrouvable;
	@Value("${geremi.config.repertoire.temporaire.chemin}")
	String cheminImport;
	@Value("${geremi.config.message.erreur.import.stockages.attributs.vides}")
	String messageAttributsVideStockage;
	@Value("${geremi.config.message.erreur.import.stockages.attributs}")
	String messageAttributsStockage;
	@Value("${geremi.config.message.erreur.import.geometrytype.stockage}")
	String messageTypesGeometryStockage;
	@Value("${geremi.config.message.erreur.import.volumes.incoherents}")
	String messageCoherenceVolumeStockage;
  @Value("${geremi.config.message.erreur.import.volumes.non.entiers}")
  String messageVolumeNonEntier;
	@Value("${geremi.config.message.erreur.import.stockages.tous.hors.periode}")
	String messageInstallationHorsPeriode;
	@Value("${geremi.config.message.erreur.import.etude.hors.periode}")
	String messageImportEtudeHorsPeriode;
	@Value("${geremi.config.message.erreur.import.etude.productions.incoherentes}")
	String messageImportEtudeProductionZoneIncoherente;
  @Value("${geremi.config.message.erreur.import.etude.productions.absentes}")
	String messageImportEtudeProductionZoneAbsente;
  @Value("${geremi.config.message.erreur.import.stockages.hors.periode.list}")
	String messageInstallationHorsPeriodeLists;
	@Value("${geremi.config.message.erreur.import.chantiers.hors.periode.list}")
	String messageChantierHorsPeriodeLists;
	@Value("${geremi.config.message.erreur.import.chantiers.tous.hors.periode}")
	String messageChantierHorsPeriode;
	@Value("${geremi.config.message.erreur.import.stockages.tous.hors.territoire}")
	String messageStockageHorsTerritoire;
	@Value("${geremi.config.message.erreur.import.chantiers.tous.hors.territoire}")
	String messageChantierHorsTerritoire;
	@Value("${geremi.config.message.erreur.import.stockages.hors.territoire.list}")
	String messageInstallationHorsTerritoireList;
	@Value("${geremi.config.message.erreur.import.chantiers.hors.territoire.list}")
	String messageChantierHorsTerritoireList;
  @Value("${geremi.config.message.erreur.import.contraintes.toutes.hors.territoire}")
	String messageContrainteHorsTerritoire;
	@Value("${geremi.config.message.erreur.import.contraintes.hors.territoire.list}")
	String messageContrainteHorsTerritoireList;

	@Autowired
	private EtudeService etudeService;

	@Autowired
	private TerritoireService territoireService;

	public CustomFileValidationService(ShpReader shpReader, GeoPkgReader geoPkgReader, SQLiteTableReader sQLiteTableReader) {

		this.shpReader = shpReader;
		this.geoPkgReader = geoPkgReader;
		this.sQLiteTableReader = sQLiteTableReader;
	}

	/**
	 * Vérifie si tous les contrôles sont respectés et renvoie les données
	 *
	 * @param multipartFiles multipartfiles
	 * @return Renvoie les données (type DataStore)
	 * @throws IOException          renvoie une erreur de lecture du fichier
	 * @throws ImportTypesException renvoie une erreur de contrôle
	 * @throws ImportZonageGeometryException
	 * @throws ImportZonageLayerException
	 */
	public ValidationImportResult validationImportEtude(MultipartFile[] multipartFiles, Integer anneeDeRef, Integer anneeFinEtu)
	        throws IOException, ImportTypesException, ImportZonageLayerException, ImportZonageGeometryException {

	    logger.debug("Début des contrôles de l'import Etude");
	    String type = IMPORTETUDE;
	    MultipartFile file = multipartFiles[0];
	    String originalFileName = file.getOriginalFilename();

	    if (originalFileName == null) {
	        throw new ImportTypesException("Le nom du fichier est nul");
	    }

	    if (!originalFileName.toLowerCase().endsWith(GPKG.getLibelle())) {
	        throw new ImportTypesException("Le type de fichier n'est pas supporté : " + originalFileName);
	    }

	    return this.validationImportEtudeGpkg(file, type, anneeDeRef, anneeFinEtu);
	}


	/**
	 * Créer une zone à partir de l'import Etude.
	 * <p>
	 * Cette méthode effectue d'abord les contrôles de base sur le fichier fourni, tels que la vérification du nom et du type.
	 * Si le fichier est un GPKG valide, elle crée un DataStore à partir de ce fichier. Enfin, elle vérifie la table des résultats du fichier GPKG.
	 * </p>
	 *
	 * @param multipartFiles          Un tableau de MultipartFile, actuellement seule le premier fichier est traité.
	 * @throws IOException                    Si une erreur de lecture du fichier se produit.
	 * @throws ImportTypesException           Si le type ou le nom du fichier n'est pas supporté.
	 * @throws ImportZonageLayerException     Si une erreur se produit lors de la vérification des couches du fichier.
	 * @throws ImportZonageGeometryException  Si une erreur se produit lors de la vérification de la géométrie du fichier.
	 */
	public DataStore createDataStoreFromImportEtude(MultipartFile[] multipartFiles)
	        throws IOException, ImportTypesException, ImportZonageLayerException, ImportZonageGeometryException {

	    logger.debug("Début des contrôles de l'import Etude");
	    String type = IMPORTETUDE;
	    MultipartFile file = multipartFiles[0];
	    String originalFileName = file.getOriginalFilename();

	    if (originalFileName == null) {
	        throw new ImportTypesException("Le nom du fichier est nul");
	    }

	    if (!originalFileName.toLowerCase().endsWith(GPKG.getLibelle())) {
	        throw new ImportTypesException("Le type de fichier n'est pas supporté : " + originalFileName);
	    }
	    Path path = Files.createTempDirectory(Paths.get(cheminImport), UUID.randomUUID().toString()).toAbsolutePath();
      try {
        File fileGpkg = convertMultiPartToFile(file, path);
        return this.dataStoreFromGpkgFile(fileGpkg);
      } finally {
        // delete Temp Directory
        deleteTempDirectory(path);
      }
	}

	/**
	 * Crée une liste de résultats à partir d'un fichier importé pour une étude.
	 * <p>
	 * Cette méthode commence par vérifier le premier fichier fourni (supposé être un GPKG) pour s'assurer de sa validité. Si le fichier est valide,
	 * elle le convertit en un objet File et effectue des vérifications sur la table des résultats du GPKG.
	 * </p>
	 *
	 * @param multipartFiles          Un tableau de MultipartFile, actuellement seule le premier fichier est traité.
	 * @return                        Une liste d'objets Resultats contenant les données extraites du fichier GPKG.
	 * @throws IOException                    En cas d'erreur lors de la manipulation du fichier, par exemple, la conversion du MultipartFile en File.
	 * @throws ImportTypesException           Si le fichier n'est pas reconnu comme un fichier GPKG valide.
	 */
	public List<Resultats> createResultatsFromImportEtude(MultipartFile[] multipartFiles)
	        throws IOException, ImportTypesException {

	    if(multipartFiles == null || multipartFiles.length == 0) {
	        throw new ImportTypesException("Aucun fichier fourni pour l'importation.");
	    }

	    MultipartFile file = multipartFiles[0];
	    if(file.isEmpty()) {
	        throw new ImportTypesException("Le fichier fourni est vide.");
	    }

	    Path path = Files.createTempDirectory(Paths.get(cheminImport), UUID.randomUUID().toString()).toAbsolutePath();
      try {
        File fileGpkg = convertMultiPartToFile(file, path);
        return checkGpkgTableResultat(fileGpkg, T_RESULTATS);
      } finally {
        deleteTempDirectory(path);
      }
	}


	/**
	 * Vérifie si tous les contrôles sont respectés et renvoie les données
	 *
	 * @param multipartFiles multipartfiles
	 * @param type           de l'import
	 * @return Renvoie les données (type DataStore)
	 * @throws IOException          renvoie une erreur de lecture du fichier
	 * @throws ImportTypesException renvoie une erreur de contrôle
	 */
	public ValidationImportResult validationImport(MultipartFile[] multipartFiles, Long idEtude, String type)
			throws IOException, ImportTypesException, ImportZonageLayerException, ImportZonageGeometryException {
		logger.debug("Début des contrôles de l'import " + type);
		if (multipartFiles.length > 1) {
			return this.validationImportMultiFile(multipartFiles, idEtude, type);
		}
		if (Objects.nonNull(multipartFiles[0])) {
			String originalFileName = multipartFiles[0].getOriginalFilename();
			if (Objects.nonNull(originalFileName)) {
				if (originalFileName.toLowerCase().endsWith(GPKG.getLibelle())) {
					return this.validationImportGpkg(multipartFiles[0], idEtude, type);
				}
			}
		}

		return this.validationImportZip(multipartFiles[0], idEtude, type);
	}

	/**
	 * Vérifie si tous les contrôles sont respectés et renvoie les données
	 *
	 * @param multipartFile multipartfile ZIP
	 * @return Renvoie les données (type DataStore)
	 * @throws IOException          renvoie une erreur de lecture du fichier
	 * @throws ImportTypesException renvoie une erreur de contrôle
	 */
	public ValidationImportResult validationImportZip(MultipartFile multipartFile, Long idEtude, String type)
			throws IOException, ImportTypesException, ImportZonageLayerException {

		logger.debug("Début de la validation du fichier ZIP...");
		DataStore dataStore;
		Path path = Files.createTempDirectory(Paths.get(cheminImport), UUID.randomUUID().toString()).toAbsolutePath();
    try {
      List<File> files = unzipMultipartFileToTempDir(multipartFile, path);
      // RG1 pour .Zip Extension 5 Files
      checkFilesSize(files);
      // RG1 pour .Zip Extension Files
      checkFilesExtension(files);
      // RG3 Encodage fichier .CPG avec UTF-8
      checkFilesEncodage(files);
      // RG4 Format du fichier .PRJ type GCS_WGS_1984
      checkFilePRJFormat(files);
      // From shapeFile File to DataStore
      dataStore = filesToDataStore(files);

      // validation de la cohérence des données du datastore
      ValidationImportResult validationImportResult = validationFonctionelleDataStore(dataStore, idEtude, type);
      logger.debug("Fin de la validation du fichier ZIP.");
      return validationImportResult;
    } finally {
      // delete Temp Directory
      deleteTempDirectory(path);
    }

	}


	/**
	 * Vérifie si tous les contrôles sont respectés et renvoie les données
	 *
	 * @param multipartFile multipartfile GPKG
	 * @return Renvoie les données (type DataStore)
	 * @throws IOException          renvoie une erreur de lecture du fichier
	 * @throws ImportTypesException renvoie une erreur de contrôle
	 */
	public ValidationImportResult validationImportEtudeGpkg(MultipartFile multipartFile,String type, Integer anneeRefEtude, Integer anneeFinEtude)
			throws IOException, ImportTypesException, ImportZonageLayerException, ImportZonageGeometryException {

		logger.debug("Début de la validation du fichier GPKG...");
		DataStore dataStore = null;
		ValidationImportResult validationImportResult = null;
		Path path = Files.createTempDirectory(Paths.get(cheminImport), UUID.randomUUID().toString()).toAbsolutePath();
    try {
      File fileGpkg = convertMultiPartToFile(multipartFile, path);
      // Format du fichier : le fichier doit être au format .gpkg
      if (getFileExtension(fileGpkg).equals(GPKG.getLibelle())) {
        //Le nom de la table dans le fichier doit être le même que celui du
        // fichier.
        checkGpkgTableGeometrie(fileGpkg);
        dataStore = this.dataStoreFromGpkgFile(fileGpkg);
        //Le fichier de projection (.gpkg) doit être au format 4326
        checkFileGPKGFormat(dataStore);
        // Verification de la présence des colonnes obligatoires
        checkGeometryAttributes(dataStore, type);
        //  Si un des attributs "Non-nullable" est vide : Import refusé, affichage
        // du message d'erreur : "Import impossible, les attributs suivants ne peuvent
        // pas être vide : <liste des attributs>
        checkNonNullableAttributs(dataStore, type);
        // vérifie l'unicité des codes des zones
        checKCodeZoneUnicity(dataStore, type);
        // Faire la vérification de la zone et année
        List<Resultats> resultats = checkGpkgTableResultat(fileGpkg, T_RESULTATS);
        verifyZoneYearAndZoneProd(dataStore, resultats, anneeRefEtude, anneeFinEtude);
        // Créer une liste pour stocker les messages informatifs.
        List<String> informativeMessages = new ArrayList<>();
        validationImportResult = new ValidationImportResult(dataStore, new ArrayList<>(resultats), informativeMessages);
      }
      logger.debug("Retour du DataStore depuis le fichier multipartFile...");
      return validationImportResult;
    } finally {
      // delete Temp Directory
      deleteTempDirectory(path);
    }
	}

	/**
	 * Vérifie si tous les contrôles sont respectés et renvoie les données
	 *
	 * @param multipartFile multipartfile GPKG
	 * @return Renvoie les données (type DataStore)
	 * @throws IOException          renvoie une erreur de lecture du fichier
	 * @throws ImportTypesException renvoie une erreur de contrôle
	 */
	public ValidationImportResult validationImportGpkg(MultipartFile multipartFile, Long idEtude, String type)
			throws IOException, ImportTypesException, ImportZonageLayerException, ImportZonageGeometryException {

		logger.debug("Début de la validation du fichier multipartFile...");
		DataStore dataStore = null;
		ValidationImportResult validationImportResult = null;
		Path path = Files.createTempDirectory(Paths.get(cheminImport), UUID.randomUUID().toString()).toAbsolutePath();
    try {
      File fileGpkg = convertMultiPartToFile(multipartFile, path);

      // RG1 : Vérifie si l'extension du fichier donné est .gpkg
      checkFileExtension(fileGpkg);

      // RG2 : Le nom de la table dans le fichier doit être le même que celui du
      // fichier.
      checkGpkgTableGeometrie(fileGpkg);

      if (getFileExtension(fileGpkg).equals(GPKG.getLibelle())) {
        dataStore = this.dataStoreFromGpkgFile(fileGpkg);

        // RG4 : Le fichier de projection (.gpkg) doit être au format 4326
        checkFileGPKGFormat(dataStore);

        // validation de la cohérence fonctionelle des données du datastore
        validationImportResult = validationFonctionelleDataStore(dataStore, idEtude, type);
      }

      logger.debug("Retour du DataStore depuis le fichier multipartFile...");
      return validationImportResult;
    } finally {
      // delete Temp Directory
      deleteTempDirectory(path);
    }
	}

	/**
	 * Vérifie si tous les contrôles sont respectés et renvoie les données
	 *
	 * @param multipartFiles liste des fichiers lors d'un import SHP
	 * @return Renvoie les données (type DataStore)
	 * @throws IOException          renvoie une erreur de lecture du fichier
	 * @throws ImportTypesException renvoie une erreur de contrôle
	 */
	public ValidationImportResult validationImportMultiFile(MultipartFile[] multipartFiles, Long idEtude, String type)
			throws IOException, ImportTypesException, ImportZonageLayerException {

		logger.debug("Début de la validation des fichier multipartFiles...");
		Path path = Files.createTempDirectory(Paths.get(cheminImport), UUID.randomUUID().toString()).toAbsolutePath();
    try {
      List<File> files = convertMultiPartFileArrayToList(multipartFiles, path);
      // RG1 5 Files
      checkFilesSize(files);
      // RG1 extension CPG, DBF, PRJ, SHP, SHX existe.
      checkFilesExtension(files);
      // RG3 Encodage fichier .CPG avec UTF-8
      checkFilesEncodage(files);
      // RG4 Format du fichier .PRJ type GCS_WGS_1984
      checkFilePRJFormat(files);
      // From sheapFile File to DataStore
      DataStore dataStore = filesToDataStore(files);

      // validation de la cohérence fonctionelle des données du datastore
      ValidationImportResult validationImportResult = validationFonctionelleDataStore(dataStore, idEtude, type);

      logger.debug("Retour du DataStore depuis les fichier multipartFiles...");
      return validationImportResult;
    } finally {
      // delete Temp Directory
      deleteTempDirectory(path);
    }
	}

  public ValidationImportResult validationFonctionelleDataStore(DataStore dataStore, Long idEtude, String type)
    throws IOException, ImportTypesException, ImportZonageLayerException {

    // Verification de la présence des colonnes obligatoires
    checkGeometryAttributes(dataStore, type);
    // RG5 La/les géométrie(s) du dataStore doi(ven)t être du type "Polygon".
    checkTypeGeom(dataStore, type);
    //  Si un des attributs "Non-nullable" est vide : Import refusé, affichage
    // du message d'erreur : "Import impossible, les attributs suivants ne peuvent
    // pas être vide : <liste des attributs>
    checkNonNullableAttributs(dataStore, type);
    // vérifie l'unicité des codes des zones
    checKCodeZoneUnicity(dataStore, type);
    // RG6 : Le fichier ne pourra contenir qu'une entité maximum par niveau de
    // contrainte (Faible, Moyenne ou Forte), soit 3 entités maximum.
    checkEntityWithContraint(dataStore, type);
    // RG6 - Volumes cohérents
    checkVolumeConsistency(dataStore, type);
    // RG8 : Cette méthode vérifie si les données de chantiers ou d'installations de
    // stockage sont dans la période de l'étude.
    Optional<String> msgListElementsHorsPeriode = checkHorsPeriode(dataStore, idEtude, type);
    // RG9 : Vérifie si les géométries dans un DataStore est dans (ou intersecte) le
    // territoire de l'étude.
    Optional<String> msgListElementsHorsGeometry = verifyGeometryInTerritory(dataStore, idEtude, type);

    // Créer une liste pour stocker les messages informatifs.
    List<String> informativeMessages = new ArrayList<>();
    // Ajouter les messages à la liste si présents.
    msgListElementsHorsPeriode.ifPresent(informativeMessages::add);
    msgListElementsHorsGeometry.ifPresent(informativeMessages::add);

    return new ValidationImportResult(dataStore, informativeMessages);
  }

	/**
	 * @param fileGpkg fichier gpkg
	 * @return return le nom de la table d'un gpkg
	 * @throws IOException renvoie une erreur de lecture du fichier
	 */
	public String readTableName(File fileGpkg) throws IOException {
		logger.debug("Début de la lecture du nom de la table pour le fichier GPKG...");
		String tableName = this.geoPkgReader.extractTableNameFromGpkg(fileGpkg.getAbsolutePath());
		logger.debug("Nom de la table extrait : {}", tableName);
		return tableName;
	}

	/**
	 * Permet de renvoyer les données d'un GPKG
	 *
	 * @param fileGpkg fichier GPKG
	 * @return renvoie les données (type DataStore)
	 * @throws IOException renvoie une erreur de lecture du fichier
	 */
	public DataStore dataStoreFromGpkgFile(File fileGpkg)
			throws IOException, ImportZonageLayerException, ImportZonageGeometryException {
		logger.debug("Début de la création du DataStore à partir du fichier GPKG...");
		DataStore dataStore = this.geoPkgReader.read(fileGpkg.getAbsolutePath());
		logger.debug("DataStore créé avec succès à partir du fichier GPKG.");
		return dataStore;
	}

	/**
	 * Convertir un MultipartFile vers un fichier.
	 *
	 * @param multipartFile fichier multiparte
	 * @return return le file
	 * @throws IOException renvoie une erreur de lecture du fichier
	 */
	public File convertMultiPartToFile(MultipartFile multipartFile, Path tempDir) throws IOException {
		String originalFileName = multipartFile.getOriginalFilename().toLowerCase();
		Path tempFile = Paths.get(tempDir.toString(), originalFileName);
		if (!Files.exists(tempFile)) {
			Files.createFile(tempFile);
		}
		Files.write(tempFile, multipartFile.getBytes(), StandardOpenOption.WRITE);
		File convFile = tempFile.toFile();
		convFile.deleteOnExit();
		return convFile;
	}

	/**
	 * @param files liste des fichiers
	 * @return liste des files
	 * @throws IOException renvoie une erreur de lecture du fichier
	 */
	public List<File> convertMultiPartFileArrayToList(MultipartFile[] files, Path tempFile)
			throws IOException, ImportTypesException {
		List<File> fileList = new ArrayList<>();
		List<String> fileName = new ArrayList<>();

		for (MultipartFile multipartFile : files) {
			String originalFileName = multipartFile.getOriginalFilename().toLowerCase();
			File file = new File(tempFile.toFile(), originalFileName);
			fileName.add(getFileNameWithoutExtension(file));
			multipartFile.transferTo(file);
			fileList.add(file);
		}
		this.checkFilesName(fileName);

		return fileList;
	}

	/**
	 * @param files liste des fichiers
	 * @return liste des files
	 * @throws IOException renvoie une erreur de lecture du fichier
	 */
	public DataStore filesToDataStore(List<File> files) throws IOException, ImportZonageLayerException {
		DataStore dataStore = null;
		for (File file : files) {
			String actualExtension = getFileExtension(file);
			if (SHP.getLibelle().equals(actualExtension)) {
				dataStore = this.shpReader.read(file);
			}
		}

		return dataStore;
	}

	/**
	 * Crée un nouveau répertoire temporaire avec le préfixe spécifié.
	 *
	 * @param prefix préfix du chemin
	 * @return chemin du fichier temporaire
	 * @throws IOException renvoie une erreur de lecture du fichier
	 */
	public Path createTempDirectory(String prefix) throws IOException {
		return Files.createTempDirectory(prefix);
	}

	/**
	 * Supprime un répertoire temporaire et tous ses fichiers/dossiers contenus
	 * Files.walkFileTree permet de traverser récursivement un répertoire et
	 * Supprimer les fichiers et répertoire
	 *
	 * @param tempDir chemin temporaire
	 * @throws IOException renvoie une erreur de lecture du fichier
	 */
	public void deleteTempDirectory(Path tempDir) throws IOException {

		Files.walkFileTree(tempDir, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Décompresse un MultipartFile dans un répertoire spécifié (généralement un
	 * répertoire temporaire). Ouvrire les flux d'entrée pour lire le fichier ZIP.
	 * Parcourire chaque entrée du fichier ZIP. Crée un Path pour chaque entrée dans
	 * le répertoire temporaire. Vérifie si l'entrée est un fichier (et non un
	 * répertoire) S'assure que les répertoires parents existent, copie le fichier
	 * dans le répertoire temporaire, ajouter le fichier à la liste des fichiers
	 * décompressés. Renvoier la liste des fichiers décompressés
	 *
	 * @param multipartFile multipart file ZIP
	 * @param tempDir       chemin temporaire
	 * @return liste des fichiers dézippés
	 * @throws IOException renvoie une erreur de lecture du fichier
	 */
	public List<File> unzipMultipartFileToTempDir(MultipartFile multipartFile, Path tempDir)
			throws IOException, ImportTypesException {
		try (InputStream is = multipartFile.getInputStream(); ZipInputStream zis = new ZipInputStream(is)) {
			try {
				ZipEntry zipEntry = zis.getNextEntry();
				List<File> unzippedFiles = new ArrayList<>();
				while (zipEntry != null) {
					Path path = tempDir.resolve(zipEntry.getName().toLowerCase());
					if (!zipEntry.isDirectory()) {
						Files.createDirectories(path.getParent());
						Files.copy(zis, path, StandardCopyOption.REPLACE_EXISTING);
						unzippedFiles.add(path.toFile());
					}
					zipEntry = zis.getNextEntry();
				}
				return unzippedFiles;
			} catch (Exception e) {
				throw new ImportTypesException(this.messageErreurDeZip);
			}
		}
	}

	/**
	 * RG1 : Un .zip, contenant 5 fichiers. sinon message d'erreur : Import
	 * impossible, les extensions des fichiers selectionné ne correspondent pas aux
	 * formats attendus.
	 *
	 * @param files liste files
	 * @throws ImportTypesException renvoie une erreur de contrôle d'import
	 */
	public void checkFilesSize(List<File> files) throws ImportTypesException {
		if (files.size() != 5) {
			throw new ImportTypesException(this.messageTypesFiles);
		}
	}

	/**
	 * RG1 : Extension des 5 fichiers. sinon message d'erreur : Import impossible,
	 * les extensions des fichiers selectionné ne correspondent pas aux formats
	 * attendus.
	 *
	 * @param files liste files
	 * @throws ImportTypesException renvoie une erreur de contrôle d'import
	 */
	public void checkFilesExtension(List<File> files) throws ImportTypesException {
		Set<String> expectedFileTypes = Set.of(CPG.getLibelle(), DBF.getLibelle(), PRJ.getLibelle(), SHP.getLibelle(),
				SHX.getLibelle());
		Set<String> actualFileTypes = files.stream().map(this::getFileExtension).collect(Collectors.toSet());

		if (!expectedFileTypes.equals(actualFileTypes)) {
			throw new ImportTypesException(this.messageTypesFiles);
		}
	}

	/**
	 * Vérification que le nom des fichiers soient identiques
	 *
	 * @param filesNames liste files Names
	 * @throws ImportTypesException renvoie une erreur de contrôle d'import
	 */
	public void checkFilesName(List<String> filesNames) throws ImportTypesException {
		String name = filesNames.get(0);
		for (String n : filesNames) {
			if (!name.equals(n)) {
				throw new ImportTypesException(this.messageNomsFiles);
			}
		}
	}

	/**
	 * RG1 : Vérifie si l'extension du fichier donné est .gpkg
	 *
	 * @param file le fichier à vérifier
	 * @throws ImportTypesException si l'extension du fichier n'est pas .gpkg
	 */
	public void checkFileExtension(File file) throws ImportTypesException {

		String actualExtension = getFileExtension(file);
		if (!GPKG.getLibelle().equals(actualExtension)) {
			throw new ImportTypesException(this.messageTypesFiles);
		}
	}

	/**
	 * RG2 : Dans le cas où un fichier .gpkg est importé, le nom de la table dans le
	 * fichier doit être le même que celui du fichier. Cette méthode est obsolète et
	 * pourrait être supprimée dans les versions futures. suite à la nouvelle regle
	 * RG2 : Il doit y avoir une unique géométrie qui devra être récupérée d'après
	 * les attributs "table_name" et "column_name" de la table
	 * "gpkg_geometry_columns".
	 *
	 * @param file     le fichier à vérifier
	 * @param typeName nom table
	 * @throws ImportTypesException si l'extension du fichier n'est pas .gpkg
	 */
	public void checkGpkgFileAndTableName(File file, String typeName) throws ImportTypesException {

		String fileName = getFileNameWithoutExtension(file);
		if (!fileName.equals(typeName)) {
			throw new ImportTypesException(this.messageNomTableNonCorrespond);
		}
	}

	/**
	 * RG2 :Il doit y avoir une unique géométrie qui devra être récupérée d'après
	 * les attributs "table_name" et "column_name" de la table
	 * "gpkg_geometry_columns".
	 *
	 * @param gpkgPath
	 * @throws IOException
	 * @throws ImportTypesException
	 */
	public void checkGpkgTableGeometrie(File gpkgPath) throws IOException, ImportTypesException {

		if (!geoPkgReader.checkGpkgTableGeometrie(gpkgPath.getAbsolutePath())) {
			throw new ImportTypesException(this.messageGeometryIntrouvable);
		}
	}

	/**
	 * Lit les données de la table "Resultat" depuis un fichier gpkg (Geopackage) et les convertit en une liste d'objets "Resultats".
	 *
	 * <p> Cette méthode extrait les données spécifiées par le paramètre tableName du fichier Geopackage fourni et mappe
	 * chaque enregistrement à un objet Resultats. Elle renvoie ensuite une liste de ces objets. </p>
	 *
	 * @param gpkgPath Le chemin d'accès complet du fichier Geopackage à lire.
	 * @param tableName Le nom de la table à lire depuis le fichier Geopackage.
	 * @return Une liste d'objets Resultats correspondant aux enregistrements de la table spécifiée.
	 * @throws IOException Si une erreur se produit lors de la lecture du fichier ou de l'extraction des données.
	 */
	public List<Resultats> checkGpkgTableResultat(File gpkgPath, String tableName)  throws IOException {

		List<Map<String, Object>> data = sQLiteTableReader.getNonSpatialTableData(gpkgPath.getAbsolutePath(),tableName);

		List<Resultats> resultatsList = data.stream().map(map -> {
		    Resultats resultats = new Resultats();

		    if(map.containsKey("code_zone") && map.get("code_zone") != null)
		        resultats.setCodeZone((String) map.get("code_zone"));

		    if(map.containsKey("annee") && map.get("annee") != null)
		        resultats.setAnnee((Integer) map.get("annee"));

		    if(map.containsKey("prodprim") && map.get("prodprim") != null)
		        resultats.setProdprim((Double) map.get("prodprim"));

		    if(map.containsKey("prodtot") && map.get("prodtot") != null)
		        resultats.setProdtot((Double) map.get("prodtot"));

		    if(map.containsKey("bestot") && map.get("bestot") != null)
		        resultats.setBestot((Double) map.get("bestot"));

		    if(map.containsKey("pprimintra") && map.get("pprimintra") != null)
		        resultats.setPprimintra((Double) map.get("pprimintra"));

		    if(map.containsKey("pprimbrute") && map.get("pprimbrute") != null)
		        resultats.setPprimbrute((Double) map.get("pprimbrute"));

		    if(map.containsKey("besprim") && map.get("besprim") != null)
		        resultats.setBesprim((Double) map.get("besprim"));

		    if(map.containsKey("beschant") && map.get("beschant") != null)
		        resultats.setBeschant((Double) map.get("beschant"));

		    return resultats;
		}).collect(Collectors.toList());

		return resultatsList;
	}

	/**
	 * RG3 : Vérifie si les fichiers sont au format UTF-8 en lisant la première
	 * ligne du fichier avec l'extension .cpg et en vérifiant si elle contient la
	 * chaîne "UTF-8"
	 *
	 * @param files liste des fichiers
	 * @throws IOException          renvoie une erreur de lecture du fichier
	 * @throws ImportTypesException renvoie une erreur de contrôle d'import
	 */
	public void checkFilesEncodage(List<File> files) throws IOException, ImportTypesException {
		for (File file : files) {
			if (getFileExtension(file).equals(CPG.getLibelle())) {
				try (var br = new BufferedReader(new FileReader(file))) {
					String firstLine = br.readLine();
					if (firstLine == null || !firstLine.contains(UTF8.getLibelle())) {
						throw new ImportTypesException(this.messageEncodage);
					}
				}
			}
		}
	}

	/**
	 * RG4 : Si utilisation du format shapefile : Alors Le fichier de projection
	 * (.prj) doit être au format 4326
	 *
	 * @param files liste des fichiers
	 * @throws IOException          renvoie une erreur de lecture des fichiers
	 * @throws ImportTypesException renvoie une erreur de contrôle d'import
	 */
	public void checkFilePRJFormat(List<File> files) throws IOException, ImportTypesException {
		for (File file : files) {
			if (getFileExtension(file).equals(PRJ.getLibelle())) {
				try (var br = new BufferedReader(new FileReader(file))) {
					String firstLine = br.readLine();
					if (firstLine == null || (!firstLine.contains(GCS_WGS_1984.getLibelle())
							&& !firstLine.contains(WGS_84.getLibelle()))) {
						throw new ImportTypesException(this.messageProjection);
					}
				}
			}
		}
	}

	/**
	 * RG4 : Si utilisation du format Gpkg : Alors la dataStore de projection
	 * (.gpkg) doit être au format 4326
	 *
	 * @param dataStore les données à contrôler
	 * @throws ImportTypesException renvoie une erreur de contrôle d'import
	 */
	public void checkFileGPKGFormat(DataStore dataStore) throws ImportTypesException {

		if (!EPSG_WGS_84.getLibelle().equals(dataStore.getReferenceSystem())) {
			throw new ImportTypesException(this.messageProjection);
		}
	}

	/**
	 * RG5 : La/les géométrie(s) du dataStore doi(ven)t être du type "Polygon".
	 *
	 * @param dataStore les données à contrôler
	 * @throws ImportTypesException renvoie une erreur de contrôle d'import
	 */
	public void checkTypeGeom(DataStore dataStore, String type) throws ImportTypesException {

		for (List<Attribut> elements : dataStore.getElements().values()) {
			for (Attribut attribut : elements) {
				if (THEGEOM.getLibelle().equals(attribut.getName())) {

					if (CHANTIER.equals(type)) {
						if (!(attribut.getValue() instanceof org.locationtech.jts.geom.MultiPolygon)
								&& !(attribut.getValue() instanceof org.locationtech.jts.geom.Polygon)
								&& !(attribut.getValue() instanceof org.locationtech.jts.geom.MultiLineString)
								&& !(attribut.getValue() instanceof org.locationtech.jts.geom.LineString)
								&& !(attribut.getValue() instanceof org.locationtech.jts.geom.Point)) {
							throw new ImportTypesException(messageTypesGeometry);
						}

					} else if (STOCKAGE.equals(type)) {
						if (!(attribut.getValue() instanceof org.locationtech.jts.geom.Point)) {
							throw new ImportTypesException(messageTypesGeometryStockage);
						}

					} else if (!(attribut.getValue() instanceof org.locationtech.jts.geom.MultiPolygon)) {
						throw new ImportTypesException(messageTypesGeometry);
					}
				}
			}
		}

	}

	/**
	 * RG6 : Le DataStore ne pourra contenir qu'une entité maximum par niveau de
	 * contrainte (Faible, Moyenne ou Forte), soit 3 entités maximum.
	 *
	 * @param dataStore les données à contrôler.
	 * @throws ImportTypesException renvoie une erreur de contrôle d'import - Si le
	 *                              niveau de contrainte d'une entité est absent ou
	 *                              nul. - Si le niveau de contrainte d'une entité
	 *                              n'est pas 'Faible', 'Moyenne' ou 'Forte'.
	 *
	 */
	public void checkEntityWithContraint(DataStore dataStore, String type) throws ImportTypesException {

		HashMap<String, Integer> contraintLevelCount = new HashMap<>();
		if (CONTRAINTE.equals(type)) {
			List<String> acceptableContraintLevels = Arrays.asList(FAIBLE.getLibelle(), MOYENNE.getLibelle(),
					FORTE.getLibelle());
			for (List<Attribut> elements : dataStore.getElements().values()) {
				String constraintLevel = null;
				for (Attribut attribut : elements) {
					if (NIVEAU.getLibelle().equals(attribut.getName())) {
						constraintLevel = (String) attribut.getValue();
            if (constraintLevel != null && !constraintLevel.isEmpty()) {
              constraintLevel = constraintLevel.substring(0, 1).toUpperCase() + constraintLevel.substring(1).toLowerCase();
            }
					}
				}
				if (!acceptableContraintLevels.contains(constraintLevel)) {
					throw new ImportTypesException(messageInvalidContrainte);
				}
				contraintLevelCount.put(constraintLevel, contraintLevelCount.getOrDefault(constraintLevel, 0) + 1);
			}

			for (String level : contraintLevelCount.keySet()) {
				if (contraintLevelCount.get(level) > 1) {
					throw new ImportTypesException(messageNiveauContrainteDoublon);
				}
			}
		}
	}

	/**
	 * RG6 : Installation de stockage, volumes cohérents Si une entité contient un
	 * des attributs suivants de valorisé, vérifier que celui-ci est > 0.
	 *
	 * @param dataStore les données à contrôler
	 * @throws ImportTypesException renvoie une erreur de contrôle d'import
	 */
	public void checkVolumeConsistency(DataStore dataStore, String type) throws ImportTypesException {
		if (STOCKAGE.equals(type) || CHANTIER.equals(type)) {
			for (List<Attribut> elements : dataStore.getElements().values()) {
				Long betonPref = null;
				Long viabAutre = null;
				Long tonTot = null;

				for (Attribut attribut : elements) {
					if (BETON_PREF.getLibelle().equals(attribut.getName()) && attribut.getValue() != null) {
            if (!attribut.getValue().toString().matches("[0-9]*")) {
              throw new ImportTypesException(messageVolumeNonEntier);
            }
						betonPref = (Long) attribut.getValue();
						if (betonPref <= 0) {
							throw new ImportTypesException(messageCoherenceVolumeStockage);
						}
					}

					if (VIAB_AUTRE.getLibelle().equals(attribut.getName()) && attribut.getValue() != null) {
            if (!attribut.getValue().toString().matches("[0-9]*")) {
              throw new ImportTypesException(messageVolumeNonEntier);
            }
						viabAutre = (Long) attribut.getValue();
						if (viabAutre <= 0) {
							throw new ImportTypesException(messageCoherenceVolumeStockage);
						}
					}

					if (TON_TOT.getLibelle().equals(attribut.getName()) && attribut.getValue() != null) {
            if (!attribut.getValue().toString().matches("[0-9]*")) {
              throw new ImportTypesException(messageVolumeNonEntier);
            }
						tonTot = (Long) attribut.getValue();
						if (tonTot <= 0) {
							throw new ImportTypesException(messageCoherenceVolumeStockage);
						}
					}
				}

				if (tonTot != null && (betonPref != null || viabAutre != null)) {
					if (tonTot != ((betonPref == null ? 0 : betonPref) + (viabAutre == null ? 0 : viabAutre))) {
						throw new ImportTypesException(messageCoherenceVolumeStockage);
					}
				}
			}
		}
	}



	/**
	 * RG7 : Le DataStore doit contenir la géométrie des contraintes avec au minimum
	 * les attributs suivants : nom et descrip et niveau (s'il existe d'autres
	 * attributs, ils seront ignorés).
	 * <p>
	 * Le DataStore doit contenir la géométrie des Chantiers avec au minimum les
	 * attributs suivants : nom (nonNull) et descrip et an_deb (nonNull) et an_fin
	 * (nonNull) et beton_pref et viab_autre et ton_tot (s'il existe d'autres
	 * attributs, ils seront ignorés).
	 * <p>
	 * Le DataStore doit contenir la géométrie des installations de stockage avec au
	 * minimum les attributs suivants : nom (nonNull) et descrip et an_deb (nonNull)
	 * et an_fin (nonNull) et beton_pref et viab_autre et ton_tot (s'il existe
	 * d'autres attributs, ils seront ignorés).
	 *
	 * @param data les données à contrôler
	 * @param type type de l'import
	 * @throws ImportTypesException renvoie une erreur de contrôle d'import
	 */
	public void checkGeometryAttributes(DataStore data, String type) throws ImportTypesException {
		Set<String> requiredAttributes = null;
		String messageErreur = null;
		if (ZONE.equals(type)) {
			requiredAttributes = Set.of(NOM_ZONE.getLibelle(), CODE_ZONE.getLibelle(), DESCRIP.getLibelle(),
					THEGEOM.getLibelle());
			messageErreur = this.messageAttributsZonage;
		}
		if (CONTRAINTE.equals(type)) {
			requiredAttributes = Set.of(NOM.getLibelle(), DESCRIP.getLibelle(), NIVEAU.getLibelle(),
					THEGEOM.getLibelle());
			messageErreur = this.messageAttributsContrainte;
		}
		if (CHANTIER.equals(type)) {
			requiredAttributes = Set.of(NOM.getLibelle(), DESCRIP.getLibelle(), ANNEE_DEBUT.getLibelle(),
					ANNEE_FIN.getLibelle(), BETON_PREF.getLibelle(), VIAB_AUTRE.getLibelle(), TON_TOT.getLibelle(),
					THEGEOM.getLibelle());
			messageErreur = this.messageAttributsChantier;
		}
		if (STOCKAGE.equals(type)) {
			requiredAttributes = Set.of(NOM_ETAB.getLibelle(), CODE_ETAB.getLibelle(), DESCRIP.getLibelle(),
					ANNEE_FIN.getLibelle(), BETON_PREF.getLibelle(), VIAB_AUTRE.getLibelle(),
					TON_TOT.getLibelle(), THEGEOM.getLibelle());
			messageErreur = this.messageAttributsStockage;
		}
		if (IMPORTETUDE.equals(type)) {
			requiredAttributes = Set.of(NOM_ZONE.getLibelle(), CODE_ZONE.getLibelle(), DESCRIP.getLibelle(), THEGEOM.getLibelle());
			messageErreur = this.messageAttributsImportEtude;
		}

		for (List<Attribut> elements : data.getElements().values()) {
    	  List<String> dataAttr = elements.stream().map(Attribut::getName).toList();
    	  for (String attr : requiredAttributes) {
    	    if (!dataAttr.contains(attr)) {
    	      throw new ImportTypesException(messageErreur);
    	    }
    	  }
    	}
	}

  /**
   * Si un code de zone est en doublon : Import refusé, affichage
   * du message d'erreur : "Import impossible, des codes de zones sont en doublon3
   *
   * @param data les données à controler
   * @param type de l'import
   * @throws ImportTypesException renvoie une erreur de contrôle d'import
   */

  private void checKCodeZoneUnicity(DataStore data, String type) throws ImportTypesException {
    if (ZONE.equals(type)||IMPORTETUDE.equals(type)) {
      String messageErreur = this.messageCodesDoublonsZonage;
      Set<String> codes = new HashSet<>();
      for (List<Attribut> elements : data.getElements().values()) {
        for (Attribut attribut : elements) {
          if (Objects.equals(attribut.getName(), "code_zone")) {
            codes.add((String) attribut.getValue());
          }
        }
      }
      if (codes.size() != data.getElements().size()) {
        throw new ImportTypesException(messageErreur);
      }
    }
  }

	/**
	 * RG7 : Si un des attributs "Non-nullable" est vide : Import refusé, affichage
	 * du message d'erreur : "Import impossible, les attributs suivants ne peuvent
	 * pas être vide : nom niveau
	 *
	 * @param data les données à controler
	 * @param type de l'import
	 * @throws ImportTypesException renvoie une erreur de contrôle d'import
	 */
	public void checkNonNullableAttributs(DataStore data, String type) throws ImportTypesException {
		Set<String> nonNullableAttributes = null;

		String messageErreur = null;

		if (ZONE.equals(type)) {
			nonNullableAttributes = Set.of(NOM_ZONE.getLibelle(), CODE_ZONE.getLibelle());
			messageErreur = this.messageAttributsVideZonage;
		}
		if (CONTRAINTE.equals(type)) {
			nonNullableAttributes = Set.of(NOM.getLibelle(), NIVEAU.getLibelle());
			messageErreur = this.messageAttributsVidesContrainte;
		}
		if (CHANTIER.equals(type)) {
			nonNullableAttributes = Set.of(NOM.getLibelle(), ANNEE_DEBUT.getLibelle(), ANNEE_FIN.getLibelle());
			messageErreur = this.messageAttributsVideChantier;

      //au moins un des attributs suivants doit être renseigné
      Set<String> oneNullableAttributes = Set.of(BETON_PREF.getLibelle(), VIAB_AUTRE.getLibelle(), TON_TOT.getLibelle());
			verifOneNullableAttribute(data, oneNullableAttributes, messageErreur);
		}
		if (STOCKAGE.equals(type)) {
			nonNullableAttributes = Set.of(NOM_ETAB.getLibelle(), ANNEE_FIN.getLibelle());
			messageErreur = this.messageAttributsVideStockage;

      //au moins un des attributs suivants doit être renseigné
      Set<String> oneNullableAttributes = Set.of(BETON_PREF.getLibelle(), VIAB_AUTRE.getLibelle(), TON_TOT.getLibelle());
			verifOneNullableAttribute(data, oneNullableAttributes, messageErreur);
		}
		if (IMPORTETUDE.equals(type)) {
			nonNullableAttributes = Set.of(NOM_ZONE.getLibelle(), CODE_ZONE.getLibelle());
			messageErreur = this.messageAttributsVidesImportEtude;
    }

		for (List<Attribut> elements : data.getElements().values()) {
			for (Attribut attribut : elements) {
				if (nonNullableAttributes.contains(attribut.getName())
						&& (Objects.isNull(attribut.getValue()) || (attribut.getBinding().equals(String.class) && ((String) attribut.getValue()).trim().isEmpty()))
        ) {
					throw new ImportTypesException(messageErreur);
				}
			}
		}
	}

  /**
   *  Cette méthode vérifie, pour chaque ligne de la table, qu'au moins un des attributs
   *  dont le nom est spécifié dans le set contient une valeur non nulle
   *
   * @param data Le DataStore contenant les données à vérifier.
   * @param oneNullableAttributes Liste des noms de champs
   * @param messageErreur message d'erreur de l'exception si la règle n'est pas vérifiée
   * @throws ImportTypesException  Exception lancée lorsque les données ne
   *                               respectent pas la règle.
   */
	private void verifOneNullableAttribute(DataStore data, Set<String> oneNullableAttributes, String messageErreur)
			throws ImportTypesException {
		for (List<Attribut> elements : data.getElements().values()) {
      if (elements.stream().noneMatch(a ->oneNullableAttributes.contains(a.getName()) && Objects.nonNull(a.getValue()))) {
        throw new ImportTypesException(messageErreur);
      }
		}
	}

	/**
	 * RG8 : Cette méthode vérifie si les données de chantiers ou d'installations de
	 * stockage sont dans la période de l'étude. Elle lance une exception si les
	 * données ne respectent pas certaines conditions concernant la période d'étude.
	 *
	 * @param data    Le DataStore contenant les données à vérifier.
	 * @param idEtude L'identifiant de l'étude en cours.
	 * @param type    Le type des données à vérifier (chantier ou stockage).
	 * @throws ImportTypesException Exception lancée lorsque les données ne
	 *                              respectent pas les règles concernant la période
	 *                              de l'étude.
	 */
	public Optional<String> checkHorsPeriode(DataStore data, Long idEtude, String type) throws ImportTypesException {

		logger.info("Début de la vérification des périodes pour le type : {}", type);
		Optional<EtudeDTO> etudeDTO = etudeService.getEtudeById(idEtude);
		if (!etudeDTO.isPresent()) {
			logger.warn("Etude avec l'ID {} non trouvée", idEtude);
			return Optional.empty();
		}

		Integer anneeRefEtude = etudeDTO.get().getAnneeRef();
		Integer anneeFinEtude = etudeDTO.get().getAnneeFin();
		logger.debug("Année de référence de l'étude : {}, Année de fin de l'étude : {}", anneeRefEtude, anneeFinEtude);

		if (CHANTIER.equals(type)) {
			return verifyElements(data, anneeRefEtude, anneeFinEtude, messageChantierHorsPeriode,
					messageChantierHorsPeriodeLists, NOM.getLibelle());
		} else if (STOCKAGE.equals(type)) {
			return verifyElementsInstallations(data, anneeRefEtude, anneeFinEtude, messageInstallationHorsPeriode,
					messageInstallationHorsPeriodeLists, NOM_ETAB.getLibelle());
		}
		logger.info("Fin de la vérification des périodes pour le type : {}", type);
		return Optional.empty();
	}


	/**
	 * Vérifie que pour chaque zone, toutes les années comprises entre `anneeRefEtude` et `anneeFinEtude` sont présentes.
	 *
	 * @param data Les données contenant les zones à vérifier.
	 * @param resultats La liste des résultats contenant les années et les zones.
	 * @param anneeRefEtude L'année de début de la période à vérifier.
	 * @param anneeFinEtude L'année de fin de la période à vérifier.
	 * @return Un Optional contenant un message d'erreur si des années sont manquantes pour une zone, sinon Optional.empty().
	 * @throws ImportTypesException Si une zone ne contient pas toutes les années attendues.
	 * @throws IOException
	 */
	private void verifyZoneYearAndZoneProd(DataStore data, List<Resultats> resultats, Integer anneeRefEtude, Integer anneeFinEtude) throws ImportTypesException, IOException {

	    logger.info("Début de la vérification des éléments pour la période d'étude");


	    HashMap<String, List<Attribut>> elements = data.getElements();
	    // 1. Création de la liste des années attendues
	    List<Integer> expectedYears = IntStream.rangeClosed(anneeRefEtude, anneeFinEtude).boxed().collect(Collectors.toList());
	    // 2. Récupération de la liste des zones distinctes
	    Set<String> distinctZones = elements.values().stream()
	        .flatMap(elementList -> elementList.stream()
	            .filter(e -> CODE_ZONE.getLibelle().equals(e.getName()))
	            .map(Attribut::getValue)
	            .map(String::valueOf))
	        .collect(Collectors.toSet());
	    // 3. Vérification pour chaque zone
	    for (String zone : distinctZones) {
	        List<Resultats> resultsForZone = resultats.stream()
	            .filter(r -> zone.equals(r.getCodeZone()))
	            .collect(Collectors.toList());
	        // Vérification des années pour la zone
	        List<Integer> yearsForZone = resultsForZone.stream()
	            .map(Resultats::getAnnee)
	            .collect(Collectors.toList());
	        if (!yearsForZone.containsAll(expectedYears)) {
	            logger.error("Erreur : La zone {} n'a pas toutes les années attendues.", zone);
	            throw new ImportTypesException(messageImportEtudeHorsPeriode);
	        }
          //verification de
          if (resultsForZone.stream().anyMatch(r -> Objects.isNull(r.getProdtot()) || Objects.isNull(r.getProdprim()) ||
                                                    Objects.isNull(r.getBestot()) || Objects.isNull(r.getCodeZone())  ||
                                                    Objects.isNull(r.getAnnee()) ) ) {
            logger.error("Erreur : La zone {} comporte au moins un champ obligatoire non renseigné.", zone);
            throw new ImportTypesException(messageImportEtudeProductionZoneAbsente);
          }
	        // Vérification que prodtot >= prodprim pour la zone
	        if (resultsForZone.stream().anyMatch(r -> r.getProdtot().compareTo(r.getProdprim()) < 0)) {
	            logger.error("Erreur : La zone {} a une production totale inférieure à la production primaire.", zone);
	            throw new ImportTypesException(messageImportEtudeProductionZoneIncoherente);
	        }
	    }
	    logger.info("Fin de la vérification des éléments pour la période d'étude");
	}


	/**
	 * Vérifie si les éléments sont dans une période d'étude spécifiée.
	 *
	 * @param data                    Les données contenant les éléments à vérifier.
	 * @param anneeRefEtude           L'année de référence de l'étude.
	 * @param anneeFinEtude           L'année de fin de l'étude.
	 * @param messageHorsPeriode      Le message à afficher si un élément est en
	 *                                dehors de la période.
	 * @param messageHorsPeriodeLists Le message à afficher si une liste d'éléments
	 *                                est en dehors de la période.
	 * @throws ImportTypesException Si les éléments sont en dehors de la période
	 *                              d'étude.
	 */
	private Optional<String> verifyElements(DataStore data, Integer anneeRefEtude, Integer anneeFinEtude,
			String messageHorsPeriode, String messageHorsPeriodeLists, String nomLibelle) throws ImportTypesException {

		logger.info("Début de la vérification des éléments");
		HashMap<String, List<Attribut>> elements = data.getElements();

    boolean allOutsideStudyPeriod = elements.values().stream()
  		.allMatch(elementList -> elementList.stream()
  				.allMatch(element ->
              !ANNEE_DEBUT.getLibelle().equals(element.getName()) && !ANNEE_FIN.getLibelle().equals(element.getName())
                || (ANNEE_DEBUT.getLibelle().equals(element.getName()) && ((Integer) element.getValue() > anneeFinEtude || (Integer) element.getValue() < anneeRefEtude))
                || ANNEE_FIN.getLibelle().equals(element.getName()) && ((Integer) element.getValue() < anneeRefEtude || (Integer) element.getValue() > anneeFinEtude)
          ));

		boolean allInsideStudyPeriod = elements.values().stream()
				.allMatch(elementList -> elementList.stream()
						.allMatch(element -> ANNEE_FIN.getLibelle().equals(element.getName())
								&& (Integer) element.getValue() >= anneeRefEtude
								|| ANNEE_DEBUT.getLibelle().equals(element.getName())
										&& (Integer) element.getValue() <= anneeFinEtude

								|| !ANNEE_DEBUT.getLibelle().equals(element.getName())
										&& !ANNEE_FIN.getLibelle().equals(element.getName())));

		if (!allOutsideStudyPeriod && !allInsideStudyPeriod) {
			Set<String> nomsElementsSet = elements.values().stream()
					.filter(elementList -> elementList.stream()
							.anyMatch(element -> (ANNEE_FIN.getLibelle().equals(element.getName())
									&& (Integer) element.getValue() <= anneeRefEtude)
									|| (ANNEE_DEBUT.getLibelle().equals(element.getName())
											&& (Integer) element.getValue() >= anneeFinEtude)))
					.map(elementList -> elementList.stream().filter(e -> nomLibelle.equals(e.getName())).findFirst()
							.map(Attribut::getValue).orElse("").toString())
					.collect(Collectors.toSet());
			String listNomsElements = String.join(", ", nomsElementsSet);
			logger.debug("Erreur de vérification des éléments : {}", listNomsElements);

			return Optional.of(String.format(messageHorsPeriodeLists, listNomsElements));
		} else if (!allInsideStudyPeriod) {
			logger.warn("Erreur de vérification des éléments : {}", messageHorsPeriode);
			throw new ImportTypesException(messageHorsPeriode);
		}
		logger.info("Fin de la vérification des éléments pour la période d'étude");
		return Optional.empty();
	}

  private Optional<String> verifyElementsInstallations(DataStore data, Integer anneeRefEtude, Integer anneeFinEtude,
                                          String messageHorsPeriode, String messageHorsPeriodeLists, String nomLibelle) throws ImportTypesException {

    logger.info("Début de la vérification des éléments");
    HashMap<String, List<Attribut>> elements = data.getElements();

    boolean allOutsideStudyPeriod = elements.values().stream()
      .allMatch(elementList -> elementList.stream()
        .allMatch(element -> !ANNEE_FIN.getLibelle().equals(element.getName()) || ((Integer) element.getValue() <= anneeRefEtude)));

    boolean allInsideStudyPeriod = elements.values().stream()
      .allMatch(elementList -> elementList.stream()
        .allMatch(element -> !ANNEE_FIN.getLibelle().equals(element.getName()) || (Integer) element.getValue() >= anneeRefEtude));

    if (!allOutsideStudyPeriod && !allInsideStudyPeriod) {
      Set<String> nomsElementsSet = elements.values().stream()
        .filter(elementList -> elementList.stream()
          .anyMatch(element -> (ANNEE_FIN.getLibelle().equals(element.getName())
            && (Integer) element.getValue() <= anneeRefEtude)))
        .map(elementList -> elementList.stream().filter(e -> nomLibelle.equals(e.getName())).findFirst()
          .map(Attribut::getValue).orElse("").toString())
        .collect(Collectors.toSet());
      String listNomsElements = String.join(", ", nomsElementsSet);
      logger.debug("Erreur de vérification des éléments : {}", listNomsElements);

      return Optional.of(String.format(messageHorsPeriodeLists, listNomsElements));
    } else if (!allInsideStudyPeriod) {
      logger.warn("Erreur de vérification des éléments : {}", messageHorsPeriode);
      throw new ImportTypesException(messageHorsPeriode);
    }
    logger.info("Fin de la vérification des éléments pour la période d'étude");
    return Optional.empty();
  }

	/**
	 * RG9 : Vérifie si les géométries dans un DataStore est dans (ou intersecte) le
	 * territoire de l'étude. Les géométries peuvent être de type "CHANTIER",
	 * "CONTRAINTE" ou "STOCKAGE".
	 *
	 * @param data    Le DataStore contenant les géométries à vérifier.
	 * @param etudeId L'identifiant de l'étude en cours.
	 * @param type    Le type des géométries à vérifier (CHANTIER, CONTRAINTE ou
	 *                STOCKAGE).
	 * @throws ImportTypesException Lorsque les géométries ne respectent pas les
	 *                              règles du territoire.
	 */
	public Optional<String> verifyGeometryInTerritory(DataStore data, Long etudeId, String type)
			throws ImportTypesException {
		logger.info("Début de la vérification des géométries pour le type: {}", type);
		String messageHorsTerritoire;
		String messageHorsGeometry;
		if (CHANTIER.equals(type)) {
			messageHorsTerritoire = messageChantierHorsTerritoire;
			messageHorsGeometry = messageChantierHorsTerritoireList;
			return verifyGeometriesForType(data, etudeId, messageHorsTerritoire, messageHorsGeometry);
		} else if (CONTRAINTE.equals(type)) {
			messageHorsTerritoire = messageContrainteHorsTerritoire;
			messageHorsGeometry = messageContrainteHorsTerritoireList;
			return verifyGeometriesForType(data, etudeId, messageHorsTerritoire, messageHorsGeometry);
		} else if (STOCKAGE.equals(type)) {
			messageHorsTerritoire = messageStockageHorsTerritoire;
			messageHorsGeometry = messageInstallationHorsTerritoireList;
			return verifyGeometriesForType(data, etudeId, messageHorsTerritoire, messageHorsGeometry);
		}
		logger.info("Fin de la vérification des géométries pour le type: {}", type);
		return Optional.empty();
	}

	/**
	 * Vérifie si les géométries est dans (ou intersecte) d'un territoire spécifié
	 * pour une étude donnée.
	 *
	 * @param data                  Le DataStore contenant les géométries à
	 *                              vérifier.
	 * @param etudeId               L'identifiant de l'étude en cours.
	 * @param messageHorsTerritoire Le message d'erreur à afficher si aucune
	 *                              géométrie n'est dans le territoire.
	 * @param messageHorsGeometry   Le message d'erreur à afficher si certaines
	 *                              géométries ne sont pas dans le territoire.
	 * @throws ImportTypesException Lorsque les géométries ne respectent pas les
	 *                              règles du territoire.
	 */
	private Optional<String> verifyGeometriesForType(DataStore data, Long etudeId, String messageHorsTerritoire,
			String messageHorsGeometry) throws ImportTypesException {

		logger.debug("Début de la vérification des géométries pour l'étude ID: {}", etudeId);
		HashMap<String, List<Attribut>> elements = data.getElements();

		List<String> outsideTerritory = new ArrayList<>();
		boolean anyInside = false;

		for (List<Attribut> attributs : elements.values()) {
			Geometry geom = null;
			String nom = null;
			for (Attribut attribut : attributs) {
				if (THEGEOM.getLibelle().equals(attribut.getName())) {
					geom = (Geometry) attribut.getValue();
				} else if (NOM_ETAB.getLibelle().equals(attribut.getName())) {
					nom = (String) attribut.getValue();
				} else if (NOM_ZONE.getLibelle().equals(attribut.getName())) {
					nom = (String) attribut.getValue();
				} else if (NOM.getLibelle().equals(attribut.getName())) {
					nom = (String) attribut.getValue();
				}
			}
			if (geom != null && nom != null) {
				boolean isInTerritory = territoireService.geometryIntersectsTerritoire(geom, etudeId);
				if (!isInTerritory) {
					outsideTerritory.add(nom);
				} else {
					anyInside = true;
				}
			}
		}
		if (!anyInside) {
			logger.warn("Aucune géométrie trouvée dans le territoire pour l'étude ID: {}", etudeId);
			throw new ImportTypesException(messageHorsTerritoire);
		} else if (!outsideTerritory.isEmpty()) {
			String listNomsInstallations = String.join(", ", outsideTerritory);
			logger.warn("Certaines géométries ne sont pas dans le territoire pour l'étude ID: {}", etudeId);
			return Optional.of(String.format(messageHorsGeometry, listNomsInstallations));
		}

		logger.debug("Fin de la vérification des géométries pour l'étude ID: {}", etudeId);
		return Optional.empty();
	}

	/**
	 * @param file le fichier type File
	 * @return retourne l'extension du fichier
	 */
	private String getFileExtension(File file) {
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
	}

	/**
	 * @param file le fichier type File
	 * @return retourne le nom du fichier sans l'extension
	 */
	private String getFileNameWithoutExtension(File file) {
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
	}

}

package fr.cerema.dsi.geremi.config.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import fr.cerema.dsi.commons.datastore.GeoPkgReader;
import fr.cerema.dsi.commons.datastore.SQLiteTableReader;
import fr.cerema.dsi.commons.datastore.ShpReader;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomFileValidationServiceTest {

	@InjectMocks
	private CustomFileValidationService customFileValidationService;

	@Mock
	private GeoPkgReader geoPkgReader;

	@Mock
	private ShpReader shpReader;
	
	@Mock
	private SQLiteTableReader sQLiteTableReader;

	Path sharedTempDirNOK;

	Path sharedTempDirOK;

	List<File> files_nok_5;

	String prefixNotOK = "tempNotOK";

	List<File> files_ok_5;

	String prefixOK = "tempOK";

	File erdreGpkgFile;

	File erdreNotGpkgFile;

	@BeforeAll
	public void setUp() throws IOException, ImportTypesException {

		shpReader = new ShpReader();
		geoPkgReader = new GeoPkgReader();
		sQLiteTableReader = new SQLiteTableReader();
		customFileValidationService = new CustomFileValidationService(shpReader, geoPkgReader, sQLiteTableReader);

		File testFile_not_5 = new File("src/test/resources/data/shp_not_5.zip");
		FileInputStream input_not_5 = new FileInputStream(testFile_not_5);
		MockMultipartFile multipartFile_not_5 = new MockMultipartFile("shp_not_5.zip", "shp_not_5.zip",
				"application/zip", FileCopyUtils.copyToByteArray(input_not_5));

		sharedTempDirNOK = customFileValidationService.createTempDirectory(prefixNotOK);
		files_nok_5 = customFileValidationService.unzipMultipartFileToTempDir(multipartFile_not_5, sharedTempDirNOK);

		File testFile_ok_5 = new File("src/test/resources/data/shp.zip");
		FileInputStream input_ok_5 = new FileInputStream(testFile_ok_5);
		MockMultipartFile multipartFile_ok_5 = new MockMultipartFile("shp.zip", "shp.zip", "application/zip",
				FileCopyUtils.copyToByteArray(input_ok_5));

		sharedTempDirOK = customFileValidationService.createTempDirectory(prefixOK);
		files_ok_5 = customFileValidationService.unzipMultipartFileToTempDir(multipartFile_ok_5, sharedTempDirOK);

		erdreGpkgFile = new File("src/test/resources/gpkg/erdre-gpkg/erdre.gpkg");
		erdreNotGpkgFile = new File("src/test/resources/gpkg/erdre-gpkg/erdre.gpk");

	}

	@AfterAll
	public void tearDown() throws IOException {

		customFileValidationService.deleteTempDirectory(sharedTempDirNOK);
		assertTrue(Files.notExists(sharedTempDirNOK));

		customFileValidationService.deleteTempDirectory(sharedTempDirOK);
		assertTrue(Files.notExists(sharedTempDirOK));
	}

	@Test
	public void testCreateAndDeleteTempDirectoryTest() throws IOException {
		String prefix = "temp";
		Path tempDir = customFileValidationService.createTempDirectory(prefix);
		assertTrue(Files.exists(tempDir));
		assertTrue(tempDir.getFileName().toString().startsWith(prefix));
		customFileValidationService.deleteTempDirectory(tempDir);
		assertTrue(Files.notExists(tempDir));
	}

	@Test
	void testUnzipMultipartFileToTempDir() throws Exception {
		// Setup
		String prefix = "temp";
		File testFile = new File("src/test/resources/data/shp.zip");
		FileInputStream input = new FileInputStream(testFile);
		MockMultipartFile multipartFile = new MockMultipartFile("shp.zip", "shp.zip", "application/zip",
				FileCopyUtils.copyToByteArray(input));

		Path tempDir = customFileValidationService.createTempDirectory(prefix);
		List<File> files = customFileValidationService.unzipMultipartFileToTempDir(multipartFile, tempDir);

		// Verify
		assertNotNull(files);
		assertTrue(Files.exists(tempDir));
		customFileValidationService.deleteTempDirectory(tempDir);
		assertTrue(Files.notExists(tempDir));

	}

	@Test
	void testCheckFilesSize_ThrowsException_IfListSizeIsNot5() throws Exception {
		// Exercise and Verify
		Exception exception = assertThrows(ImportTypesException.class, () -> {
			customFileValidationService.checkFilesSize(files_nok_5);
		});
		assertNotNull(exception);
	}

	@Test
	void testCheckFilesSize_DoesNotThrowException_IfListSizeIs5() throws Exception {
		// Exercise and Verify
		try {
			customFileValidationService.checkFilesSize(files_ok_5);
		} catch (ImportTypesException e) {
			fail("L’exception n’aurait pas dû être levée");
		}
	}

	@Test
	void testCheckFilesExtensionOK() throws Exception {

		// Exercise and Verify
		try {
			customFileValidationService.checkFilesExtension(files_ok_5);
		} catch (ImportTypesException e) {
			fail("L’exception n’aurait pas dû être levée");
		}
	}

	@Test
	void testCheckFilesExtensionNOK() throws Exception {

		// Exercise and Verify
		Exception exception = assertThrows(ImportTypesException.class, () -> {
			customFileValidationService.checkFilesExtension(files_nok_5);
		});
		assertNotNull(exception);
	}

	@Test
	void testCheckFileExtensionNOK() throws Exception {

		Exception exception = assertThrows(ImportTypesException.class, () -> {
			customFileValidationService.checkFileExtension(erdreNotGpkgFile);
		});
		assertNotNull(exception);
	}

	@Test
	void testCheckFileExtensionOK() throws Exception {

		customFileValidationService.checkFileExtension(erdreGpkgFile);
	}

	@Test
	void checkGpkgFileAndTableNameOK() throws Exception {

		customFileValidationService.checkGpkgFileAndTableName(erdreGpkgFile, "erdre");
	}

	@Test
	void checkGpkgFileAndTableNameNOK() throws Exception {
		Exception exception = assertThrows(ImportTypesException.class, () -> {
			customFileValidationService.checkGpkgFileAndTableName(erdreGpkgFile, "erdree");
		});
		assertNotNull(exception);
	}

	@Test
	void testCheckFilesEncodage_CpgFileWithUtf8Encoding() throws Exception {

		// Assurez-vous que le fichier testfile.cpg existe et contient l'encodage UTF-8
		customFileValidationService.checkFilesEncodage(files_ok_5);
	}

	@Test
	void testCheckFilesEncodage_CpgFileWithNoUtf8Encoding() throws Exception {

		Exception exception = assertThrows(ImportTypesException.class, () -> {
			customFileValidationService.checkFilesEncodage(files_nok_5);
		});
		assertNotNull(exception);
	}

	@Test
	void testCheckFilePRJFormat() throws Exception {

		// Exercise and Verify
		try {
			customFileValidationService.checkFilePRJFormat(files_ok_5);
		} catch (ImportTypesException e) {
			fail("L’exception n’aurait pas dû être levée");
		}
	}

//	@Test
//	public void checkFileGPKGFormat_OK() throws IOException, ImportTypesException {
//		// Arrange
//		File validGPKGFile = new File("src/test/resources/gpkg/erdre-gpkg/erdre.gpkg");
//
//		DataStore mockDataStore = mock(DataStore.class);
//		when(mockDataStore.getReferenceSystem()).thenReturn("EPSG:WGS 84");
//
//		when(geoPkgReader.read(validGPKGFile.getAbsolutePath()).getLeft()).thenReturn(mockDataStore);
//
//		List<File> files = List.of(validGPKGFile);
//
//		// Act
//		customFileValidationService.checkFileGPKGFormat(files);
//
//		// Assert
//		verify(geoPkgReader).read(validGPKGFile.getAbsolutePath());
//	}

//	@Test
//	public void checkFileGPKGFormat_Fail() throws IOException {
//		// Arrange
//		File mockFile = mock(File.class);
//		when(mockFile.getName()).thenReturn("test.gpkg");
//
//		DataStore mockDataStore = mock(DataStore.class);
//		when(mockDataStore.getReferenceSystem()).thenReturn("EPSG:OTHER");
//
//		when(geoPkgReader.read(anyString()).getLeft()).thenReturn(mockDataStore);
//
//		List<File> files = List.of(mockFile);
//
//		// Act & Assert
//		try {
//			customFileValidationService.checkFileGPKGFormat(files);
//		} catch (ImportTypesException e) {
//			// Assert
//			verify(geoPkgReader).read(anyString());
//		}
//	}

//	@Test
//	public void test() throws Exception {
//		DataStore dataStore = this.geoPkgReader.read("src/test/resources/gpkg/erdre-gpkg/erdre.gpkg").getLeft();
//		System.out.println(dataStore);
//	}
}

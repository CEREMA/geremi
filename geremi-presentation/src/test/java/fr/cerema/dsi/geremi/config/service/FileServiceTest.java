package fr.cerema.dsi.geremi.config.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

	@InjectMocks
	private FileService fileService;

	@Test
	void checkFileExtension_shouldReturnOdsExtension() throws IOException, ImportTypesException {
		// given
		byte[] content = "content".getBytes(StandardCharsets.UTF_8);
		MultipartFile file = new MockMultipartFile("file.ods", "original_filename.ods",
				"application/vnd.oasis.opendocument.spreadsheet", content);

		// when
		String result = fileService.checkFileExtension(file);

		// then
		assertEquals(".ods", result);
	}

	@Test
	void checkFileExtension_shouldReturnXlsExtension() throws IOException, ImportTypesException {
		// given
		byte[] content = "content".getBytes(StandardCharsets.UTF_8);
		MultipartFile file = new MockMultipartFile("file.xls", "original_filename.xls", "application/vnd.ms-excel",
				content);

		// when
		String result = fileService.checkFileExtension(file);

		// then
		assertEquals(".xls", result);
	}

	@Test
	void checkFileExtension_shouldReturnXlsxExtension() throws IOException, ImportTypesException {
		// given
		byte[] content = "content".getBytes(StandardCharsets.UTF_8);
		MultipartFile file = new MockMultipartFile("file.xlsx", "original_filename.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);

		// when
		String result = fileService.checkFileExtension(file);

		// then
		assertEquals(".xlsx", result);
	}


	@Test
	public void testGetColumnName() {
		FileService fileService = new FileService();
		assertEquals("Code Zone", fileService.getColumnName(0));
		assertEquals("Nom Zone", fileService.getColumnName(1));
		assertEquals("Année", fileService.getColumnName(2));
		assertEquals("Population Basse", fileService.getColumnName(3));
		assertEquals("Population Centrale", fileService.getColumnName(4));
		assertEquals("Population Haute", fileService.getColumnName(5));
		assertEquals("Colonne Inconnue", fileService.getColumnName(6));
	}


}

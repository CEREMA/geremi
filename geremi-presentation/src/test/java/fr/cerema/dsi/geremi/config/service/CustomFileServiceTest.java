package fr.cerema.dsi.geremi.config.service;

import java.util.Map;

import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomFileServiceTest {

    @Mock
    private OdsFileService odsFileService;

    @Mock
    private ExcelFileService excelFileService;

    @Mock
    private FileService fileService;

    @InjectMocks
    private CustomFileService customFileService;

    // mock zones map
    @Mock
    private Map<String, Zone> zones;

    @Test
    void checkFileColumns_veriy_xls() throws ImportTypesException {
        // given
        Long id = 1L;
        MultipartFile file = new MockMultipartFile("file", new byte[0]);
        when(fileService.checkFileExtension(any())).thenReturn(".xls");


        // when
        customFileService.checkFileColumns(id, file, zones);

        // then
        verify(fileService).checkFileExtension(file);
        verify(excelFileService).checkFileColumns(id, file, zones);
        verifyNoMoreInteractions(fileService, odsFileService, excelFileService);
    }

    @Test
    void checkFileColumns_veriy_ods() throws ImportTypesException {
        // given
        Long id = 1L;
        MultipartFile file = new MockMultipartFile("file", new byte[0]);
        when(fileService.checkFileExtension(any())).thenReturn(".ods");


        // when
        customFileService.checkFileColumns(id, file, zones);

        // then
        verify(fileService).checkFileExtension(file);
        verify(odsFileService).checkFileColumns(id, file, zones);
        verifyNoMoreInteractions(fileService, odsFileService, excelFileService);
    }

    @Test
    void getOdsForEtude_shouldReturnResource() {
        // given
        Long id = 1L;
        Resource expectedResource = new ByteArrayResource(new byte[0]);
        when(odsFileService.getOdsForEtude(any())).thenReturn(expectedResource);

        // when
        Resource actualResource = customFileService.getOdsForEtude(id);

        // then
        assertEquals(expectedResource, actualResource);
        verify(odsFileService).getOdsForEtude(id);
        verifyNoMoreInteractions(fileService, odsFileService, excelFileService);
    }

}

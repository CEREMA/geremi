package fr.cerema.dsi.geremi.config.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.services.dto.PopulationDTO;


// pattern facade
@Service
public class CustomFileService {

	@Autowired
	private OdsFileService odsFileService;

	@Autowired
	private ExcelFileService excelFileService;

	@Autowired
	private FileService fileService;

	public List<PopulationDTO> checkFileColumns(Long id, MultipartFile file, Map<String, Zone> zones) throws ImportTypesException {
		String extension = fileService.checkFileExtension(file);
		// thread safe
		IFileService iFileService = getIFileService(extension);
		return iFileService.checkFileColumns(id, file, zones);
	}

	public Resource getOdsForEtude(Long id) {
		return odsFileService.getOdsForEtude(id);
	}

  @Transactional(propagation = Propagation.REQUIRED)
	public Map<String, Zone> findZonesByCode(Long id) {
		return fileService.findZonesByCode(id);
	}

	// pattern strategy
	private IFileService getIFileService(String extension) {
		if (extension.equals(fileService.ODS)) {
			return odsFileService;
		} else {
			return excelFileService;
		}
	}
}

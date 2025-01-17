package fr.cerema.dsi.geremi.config.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.exceptions.ImportTypesException;
import fr.cerema.dsi.geremi.services.dto.PopulationDTO;

public interface IFileService {

	List<PopulationDTO> checkFileColumns(Long id, MultipartFile file, Map<String, Zone> zones) throws ImportTypesException;

	//List<PopulationDTO> processFileUpload(Long id, MultipartFile file, Map<String, Zone> zones);
}

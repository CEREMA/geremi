package fr.cerema.dsi.geremi.services;

import fr.cerema.dsi.commons.datastore.DataStore;
import fr.cerema.dsi.commons.datastore.entities.Resultats;
import fr.cerema.dsi.geremi.services.dto.ImportEtudeDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;

import java.util.List;
import java.util.Optional;

public interface ImportEtudeService {

  Optional<ScenarioDTO> importEtudeFromFiles(ImportEtudeDTO importEtudeDTO, DataStore data, List<Resultats> resultats);
}

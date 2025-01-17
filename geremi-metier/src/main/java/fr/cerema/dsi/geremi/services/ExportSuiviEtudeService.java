package fr.cerema.dsi.geremi.services;

import java.io.File;
import java.io.IOException;

public interface ExportSuiviEtudeService {

  File exportSuiviEtudeByIdEtude(Long idEtude) throws IOException;

}

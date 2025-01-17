package fr.cerema.dsi.geremi.services.impl;

import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.services.EtudeService;
import fr.cerema.dsi.geremi.services.ExportSuiviEtudeService;
import fr.cerema.dsi.geremi.services.ScenarioService;
import fr.cerema.dsi.geremi.services.dto.ResultatCalculDTO;
import fr.cerema.dsi.geremi.services.dto.ResultatZoneDTO;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service("exportSuiviEtudeService")
public class ExportSuiviEtudeServiceImpl implements ExportSuiviEtudeService {

  private final EtudeService etudeService;
  private final ScenarioService scenarioService;
  private final ZoneServiceImpl zoneService;

  public ExportSuiviEtudeServiceImpl(EtudeService etudeService, ScenarioService scenarioService, ZoneServiceImpl zoneService) {
    this.etudeService = etudeService;
    this.scenarioService = scenarioService;
    this.zoneService = zoneService;
  }

  @Override
  public File exportSuiviEtudeByIdEtude(Long idEtude) throws IOException {
    Etude etude = this.etudeService.findById(idEtude);
    ScenarioDTO scenarioDTO = this.scenarioService.suiviScenarioEtude(idEtude);
    scenarioDTO.getResultatsCalculs().get(scenarioDTO.getEtudeDTO().getAnneeRef()).getResultatZones().values()
      .forEach( res -> {
        res.setProductionZonePrimaireBruteReelle(res.getProductionZonePrimaireBrute());
        res.setProductionZonePrimaireTotalReelle(res.getProductionZonePrimaireTotal());
      });
    Map<Long, String> nomZoneById = this.zoneService.getZoneNamesEtude(idEtude);
    int anneeRef = etude.getAnneeRef();
    File csvFile = File.createTempFile("SuiviGlobal_"+etude.getNom()+".csv","");

    try (FileWriter writer = new FileWriter(csvFile)) {
      // Entêtes du fichier CSV
      writeLine(writer, List.of("Année", "Zone",
        "Production annuelle estimée de la zone en matériaux primaires (kt)",
        "Production annuelle réelle de la zone en matériaux primaires (kt)",
        "Production annuelle brute estimée de la zone en matériaux primaires (kt)",
        "Production annuelle brute réelle de la zone en matériaux primaires (kt)",
        "Ratio production/besoin estimé de la zone (%)",
        "Ratio production/besoin ajusté de la zone (%)"));

      // Données
      List<ResultatZoneDTO> resultatZoneDTOS = scenarioDTO.getResultatsCalculs().values().stream()
        .map(ResultatCalculDTO::getResultatZones)
        .flatMap(list -> list.values().stream())
        .toList();

      for(ResultatZoneDTO resultatZoneDTO : resultatZoneDTOS){
        writeLine(writer, List.of(String.valueOf(resultatZoneDTO.getAnnee()),
          nomZoneById.get(resultatZoneDTO.getIdZone()),
          anneeRef == resultatZoneDTO.getAnnee() ? "" : this.ifNullReturnEmpty(resultatZoneDTO.getProductionZonePrimaireTotal()),
          this.ifNullReturnEmpty(resultatZoneDTO.getProductionZonePrimaireTotalReelle()),

          anneeRef == resultatZoneDTO.getAnnee() ? "" : this.ifNullReturnEmpty(resultatZoneDTO.getProductionZonePrimaireBrute()),
          this.ifNullReturnEmpty(resultatZoneDTO.getProductionZonePrimaireBruteReelle()),

          // Tension estimée
          anneeRef == resultatZoneDTO.getAnnee() ? "" : this.ifNullReturnEmptyCalculTensionEstime(resultatZoneDTO.getProductionZoneTotal(), resultatZoneDTO.getBesoinZoneTotal()),
          // Tension ajustée
          this.ifNullReturnEmptyCalculTensionAjustee(resultatZoneDTO.getProductionZonePrimaireTotalReelle(),
            resultatZoneDTO.getProductionZoneSecondaireTotal(),
            resultatZoneDTO.getBesoinZoneTotal())
        ));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return csvFile;
  }

  private String ifNullReturnEmpty(Double data) {
    if (Objects.isNull(data)) {
      return "";
    } else {
      return data.toString();
    }
  }

  private String ifNullReturnEmptyCalculTensionAjustee(Double prodPrim, Double prodSec, Double besoin) {
    if (Objects.isNull(prodPrim) || Objects.isNull(prodSec) || Objects.isNull(besoin)) {
      return "";
    } else {
      return (BigDecimal.valueOf(prodPrim + prodSec).multiply(BigDecimal.valueOf(100))
        .divide(BigDecimal.valueOf(besoin), 2, RoundingMode.HALF_UP))
        .toString();
    }
  }

  private String ifNullReturnEmptyCalculTensionEstime(Double prodPrim, Double besoin) {
    if (Objects.isNull(prodPrim) || Objects.isNull(besoin)) {
      return "";
    } else {
      return (BigDecimal.valueOf(prodPrim).multiply(BigDecimal.valueOf(100))
        .divide(BigDecimal.valueOf(besoin), 2, RoundingMode.HALF_UP))
        .toString();
    }
  }

  private static void writeLine(FileWriter writer, List<String> values) throws IOException {
    String line = String.join(";", values);
    writer.write(line + System.lineSeparator());
  }
}

package fr.cerema.dsi.geremi.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.cerema.dsi.geremi.services.CarriereAffichageConfigService;
import jakarta.annotation.PostConstruct;

@Service
public class CarriereAffichageConfigServiceImpl implements CarriereAffichageConfigService{
  private final Logger logger = LoggerFactory.getLogger(CarriereAffichageConfigServiceImpl.class);

  @Value("#{'${geremi.config.carriere.affichage.principal}'.split(',')}")
  private List<String> detailPrincipal = new ArrayList<>();
  @Value("#{'${geremi.config.carriere.affichage.activite}'.split(',')}")
  private List<String> detailActivite = new ArrayList<>();
  @Value("#{'${geremi.config.carriere.affichage.destination}'.split(',')}")
  private List<String> detailDestination = new ArrayList<>();
  @Value("#{'${geremi.config.carriere.affichage.traitement-dechet}'.split(',')}")
  private List<String> detailTraitementDechets = new ArrayList<>();
  @Value("#{'${geremi.config.carriere.affichage.tooltip}'.split(',')}")
  private List<String> detailTooltip = new ArrayList<>();

  @PostConstruct
  public void init() {
    if(this.logger.isDebugEnabled()){
      this.logger.debug("Affichage détail trouvées :");
      this.logger.debug(String.valueOf(this.detailPrincipal));
      this.logger.debug(String.valueOf(this.detailActivite));
      this.logger.debug(String.valueOf(this.detailDestination));
      this.logger.debug(String.valueOf(this.detailTraitementDechets));
      this.logger.debug(String.valueOf(this.detailTooltip));
    }
  }

  @Override
  public List<String> getCarriereAffichagePrincipalConfig() {
    return this.detailPrincipal;
  }

  @Override
  public List<String> getCarriereAffichageActiviteConfig() {
    return this.detailActivite;
  }

  @Override
  public List<String> getCarriereAffichageDestinationConfig() {
    return this.detailDestination;
  }

  @Override
  public List<String> getCarriereAffichageTraitementDechetConfig() {
    return this.detailTraitementDechets;
  }

  @Override
  public List<String> getCarriereAffichageTooltipConfig() {
    return this.detailTooltip;
  }
}

package fr.cerema.dsi.geremi.services;

import java.util.List;

public interface CarriereAffichageConfigService {
  public List<String> getCarriereAffichagePrincipalConfig();
  public List<String> getCarriereAffichageActiviteConfig();
  public List<String> getCarriereAffichageDestinationConfig();
  public List<String> getCarriereAffichageTraitementDechetConfig();
  public List<String> getCarriereAffichageTooltipConfig();
}

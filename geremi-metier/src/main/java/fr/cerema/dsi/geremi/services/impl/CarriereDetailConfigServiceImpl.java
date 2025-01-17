package fr.cerema.dsi.geremi.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

import fr.cerema.dsi.geremi.services.CarriereDetailConfigService;
import fr.cerema.dsi.geremi.services.dto.CarriereDetailDTO;
import jakarta.annotation.PostConstruct;

@Service
public class CarriereDetailConfigServiceImpl implements CarriereDetailConfigService {

  private final Logger logger = LoggerFactory.getLogger(CarriereDetailConfigServiceImpl.class);

  @Autowired
  private Environment environment;

  private List<CarriereDetailDTO> carriereDetailDTOList = new ArrayList<>();

  private String prefix = "geremi.config.carriere.detail";
  private String attrConfig = "attr";
  private String attrListConfig = "attr.list";
  private String attrListSeparatorConfig = "attr.list.separator";
  private String labelConfig = "label";

  @PostConstruct
  public void init() {
    this.logger.info("Recherche des libellés d'affichage pour les carrières");
    if (environment instanceof ConfigurableEnvironment) {
      for (PropertySource<?> propertySource : ((ConfigurableEnvironment) environment).getPropertySources()) {
        if (propertySource instanceof EnumerablePropertySource) {
          EnumerablePropertySource enumerablePropertySource = (EnumerablePropertySource) propertySource;
          int i = 1;
          Arrays.stream(enumerablePropertySource.getPropertyNames()).forEach(this.logger::debug);
          while(enumerablePropertySource.containsProperty(this.prefix+"["+i+"]"+"."+attrConfig)) {
            CarriereDetailDTO carriereDetailDTO = new CarriereDetailDTO();
            if (enumerablePropertySource.containsProperty(this.prefix+"["+i+"]."+attrListConfig) && enumerablePropertySource.containsProperty(this.prefix+"["+i+"]."+attrListSeparatorConfig)) {
              carriereDetailDTO.setAttrList(Arrays.stream(String.valueOf(enumerablePropertySource.getProperty(this.prefix+"["+i+"]."+attrListConfig)).split(" *, *")).toList());
              carriereDetailDTO.setAttrListSeparator(String.valueOf(enumerablePropertySource.getProperty(this.prefix+"["+i+"]."+attrListSeparatorConfig)));
            }
            carriereDetailDTO.setAttr(String.valueOf(enumerablePropertySource.getProperty(this.prefix+"["+i+"]."+attrConfig)));
            carriereDetailDTO.setLabel(String.valueOf(enumerablePropertySource.getProperty(this.prefix+"["+i+"]."+labelConfig)));
            this.carriereDetailDTOList.add(carriereDetailDTO);
            i++;
          }
        }
      }
    }
    if(this.logger.isDebugEnabled()){
      this.logger.debug("Libellé détail trouvées :");
      this.logger.debug(String.valueOf(this.carriereDetailDTOList));
    }
  }


  @Override
  public List<CarriereDetailDTO> getCarriereDetailConfig() {
    return this.carriereDetailDTOList;
  }
}

package fr.cerema.dsi.geremi.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@EnableCaching
public class WebConfiguration implements WebMvcConfigurer {

  private static Logger logger = LoggerFactory.getLogger(WebConfiguration.class);

  @Value("${url.externe:}")
  private String urlExterne;

  @Value("${info.app.version:unknown}")
  private String version;

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

    logger.info("Initialisation de la configuration de la négociation de contenu.");

    configurer.
      favorParameter(true).
      parameterName("mediaType").
      ignoreAcceptHeader(false).
      defaultContentType(MediaType.APPLICATION_JSON).
      mediaType("xml", MediaType.APPLICATION_XML).
      mediaType("json", MediaType.APPLICATION_JSON);
  }

  @Bean
  public OpenAPI infoOpenAPI() {
    // TODO ADAPTE
    OpenAPI openAPI = new OpenAPI()
      .info(new Info().title("GEREMI API")
        .description("<b>Gestion des ressources minérales</b>.")
        .version(version)
        .license(new License().name("Cecill 2.0")
          .url("http://www.cecill.info/licences/Licence_CeCILL_V2-fr.html")));

    if (StringUtils.isNotBlank(urlExterne)) {
      openAPI.addServersItem(new Server().url(urlExterne));
    }
    return openAPI;
  }

}

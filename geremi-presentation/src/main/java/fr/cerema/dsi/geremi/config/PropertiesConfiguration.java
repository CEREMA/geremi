package fr.cerema.dsi.geremi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:etablissement.properties","classpath:message-erreur.properties","classpath:territoire.properties"},encoding = "UTF-8")
public class PropertiesConfiguration {
}

package fr.cerema.dsi.commons.controllers;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fr.cerema.dsi.commons.beans.errors.JsonApiError;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Permet d'afficher les erreurs en les présentant à l'utilisateur sous forme de json
 * Si cela échoue, on affiche une version de secours en utilisant un toString()
 * Le but est de limiter le plus possible la survenue d'erreur pendant ce processus : risque de boucle infinie.
 */
@Component
public abstract class AbstractErrorHandler {
  private static final Logger logger = LoggerFactory.getLogger(AbstractErrorHandler.class);

  private final ObjectMapper mapper;

  @Autowired
  public AbstractErrorHandler(ObjectMapper mapper) {
    this.mapper = mapper;
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public void sendError(JsonApiError error, HttpServletResponse response) {
    response.setStatus(getStatusFromError(error));
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    try {
      PrintWriter writer = response.getWriter();
      try {
        writer.write(mapper.writeValueAsString(error));
      } catch (Exception e) {
        logger.warn("Problème lors de l'envoi formaté en json de l'erreur : {}", error);
        logger.warn(" Le problème était : {}", e.getLocalizedMessage());
        logger.warn(" On envoie l'erreur en mode simple string");
        writer.write(error.toString());
      } finally {
        writer.close();
      }
    } catch (Exception ex) {
      logger.error("Impossible d'envoyer en simple mode texte l'erreur suivante : " + error, ex);
    }
  }

  /**
   * Si le statut n'est pas défini, on retourne par défaut une simple erreur 500 générique
   */
  private int getStatusFromError(JsonApiError error) {
    if (error.getStatus() != null) {
      return error.getStatus().value();
    } else {
      return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
  }

}

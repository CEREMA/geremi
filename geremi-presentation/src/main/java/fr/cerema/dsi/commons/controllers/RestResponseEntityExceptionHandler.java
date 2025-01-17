package fr.cerema.dsi.commons.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cerema.dsi.commons.beans.errors.JsonApiError;
import fr.cerema.dsi.commons.exceptions.EntityNotFoundException;
import fr.cerema.dsi.commons.exceptions.UsersNotFoundException;
import fr.cerema.dsi.commons.security.exceptions.AuthenticationServiceException;
import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends AbstractErrorHandler {

  @Autowired
  public RestResponseEntityExceptionHandler(ObjectMapper mapper) {
    super(mapper);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public void handleNoHandlerFound(NoHandlerFoundException ex, HttpServletResponse response) {
    JsonApiError error = new JsonApiError(HttpStatus.NOT_FOUND,
      "La requête ne correspond pas à un service existant.", ex);
    sendError(error, response);
  }

  @ExceptionHandler({AccessDeniedException.class})
  public void handleAccessDeniedException(AccessDeniedException ex, HttpServletResponse response) {
    JsonApiError error = new JsonApiError(HttpStatus.FORBIDDEN,
      "Accès refusé, vous n'avez pas les droits suffisants", ex);
    sendError(error, response);
  }

  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public void handleError(HttpMediaTypeNotAcceptableException ex, HttpServletResponse response) {
    JsonApiError error = new JsonApiError(HttpStatus.NOT_ACCEPTABLE,
      "Le serveur ne sait pas générer une réponse avec le mediaType demandé.", ex);
    sendError(error, response);
  }

  @ExceptionHandler(value = EntityNotFoundException.class)
  public void handleNotFound(EntityNotFoundException ex, HttpServletResponse response) {
    JsonApiError error = new JsonApiError(HttpStatus.NOT_FOUND,
      "Entité recherchée non trouvée", ex);
    sendError(error, response);
  }

  @ExceptionHandler(value = UsersNotFoundException.class)
  public void handleUserNotFound(UsersNotFoundException ex, HttpServletResponse response) {
    JsonApiError error = new JsonApiError(HttpStatus.UNAUTHORIZED,
      "Utilisateur recherché non trouvé", ex);
    sendError(error, response);
  }

  @ExceptionHandler(value = IllegalArgumentException.class)
  public void handleIllegalArgument(IllegalArgumentException ex, HttpServletResponse response) {
    JsonApiError error = new JsonApiError(HttpStatus.BAD_REQUEST,
      "Un argument de la requête est incorrect", ex);
    sendError(error, response);
  }

  @ExceptionHandler(value = AuthenticationServiceException.class)
  public void handleAuthenticationServiceException(AuthenticationServiceException ex,
                                                   HttpServletResponse response) {
    JsonApiError error = new JsonApiError(HttpStatus.UNAUTHORIZED,
      "Authentification incorrecte.", ex);
    sendError(error, response);
  }

}

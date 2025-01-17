package fr.cerema.dsi.commons.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cerema.dsi.commons.beans.errors.JsonApiError;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;

// TODO Verifier API qui génére une erreur pour les tests.
// surement redirection d'erreur

@Hidden
@RestController
public class CustomErrorController extends AbstractErrorHandler implements ErrorController {

  private static final String PATH = "/error";

  private final ErrorAttributes errorAttributes;

  @Autowired
  public CustomErrorController(ObjectMapper mapper, ErrorAttributes errorAttributes) {
    super(mapper);
    this.errorAttributes = errorAttributes;
  }

  @GetMapping(value = PATH, produces = MediaType.ALL_VALUE)
  public void getHandleError(WebRequest request, HttpServletResponse response) {
    JsonApiError error = new JsonApiError(HttpStatus.resolve(response.getStatus()), errorAttributes.getError(request));

    sendError(error, response);
  }

  @PostMapping(value = PATH, produces = MediaType.ALL_VALUE)
  public void postHandleError(WebRequest request, HttpServletResponse response) {
    JsonApiError error = new JsonApiError(HttpStatus.resolve(response.getStatus()), errorAttributes.getError(request));

    sendError(error, response);
  }

  @PutMapping(value = PATH, produces = MediaType.ALL_VALUE)
  public void putHandleError(WebRequest request, HttpServletResponse response) {
    JsonApiError error = new JsonApiError(HttpStatus.resolve(response.getStatus()), errorAttributes.getError(request));

    sendError(error, response);
  }

}


package fr.cerema.dsi.commons.beans.errors;

public class ApiValidationSubError extends ApiSubError {
  private String object;
  private String field;
  private Object rejectedValue;
  private String message;

  ApiValidationSubError(String object, String message) {
    this.object = object;
    this.message = message;
  }
}

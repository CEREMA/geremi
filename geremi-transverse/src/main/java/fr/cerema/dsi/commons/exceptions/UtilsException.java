package fr.cerema.dsi.commons.exceptions;

public class UtilsException extends RuntimeException {

  public UtilsException(String msg) {
    super(msg);
  }

  public UtilsException(String message, Throwable cause) {
    super(message, cause);
  }

}

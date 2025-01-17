package fr.cerema.dsi.geremi.exceptions;

public class JwtConfMalformedException extends RuntimeException {

  public JwtConfMalformedException(String msg) {
    super(msg);
  }

}

package fr.cerema.dsi.commons.exceptions;

/**
 * Exception à lever lorsque le workflow de création/modification n'est pas respecté (ordre métier des étapes)
 */
public class WorkflowSecurityException extends RuntimeException {

  public WorkflowSecurityException(String message) {
    super(message);
  }
}

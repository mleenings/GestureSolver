package com.github.mleenings.gesture.solver.dao;

/** Exception thrown by {@link DAO}. */
public class ProjectDAOException extends RuntimeException {

  /**
   * Creates a new instance of {@code ProjectDAOException} with detailed message and cause.
   *
   * @param detailMessage
   * @param throwable
   */
  public ProjectDAOException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }
}

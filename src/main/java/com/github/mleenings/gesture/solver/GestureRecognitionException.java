package com.github.mleenings.gesture.solver;

/** Gesture recognition exception */
public class GestureRecognitionException extends RuntimeException {
  /**
   * Creates a new instance of {@code GestureRecognitionException} with detailed message and cause.
   *
   * @param detailMessage
   * @param throwable
   */
  public GestureRecognitionException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  /**
   * constructor
   *
   * @param detailMessage
   */
  public GestureRecognitionException(String detailMessage) {
    super(detailMessage);
  }
}

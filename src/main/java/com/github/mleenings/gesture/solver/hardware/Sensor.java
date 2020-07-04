package com.github.mleenings.gesture.solver.hardware;

/** dummy code for hardware stuff (should be for example the smartphone sensor) */
public abstract class Sensor {
  public static final int TYPE_ACCELEROMETER = 0;
  public static final int TYPE_GYROSCOPE = 1;
  public static final int TYPE_MAGNETIC_FIELD = 2;
  public static final int TYPE_STEP_DETECTOR = 3;
  public static final int TYPE_GYROSCOPE_UNCALIBRATED = 4;

  public abstract int getType();
}

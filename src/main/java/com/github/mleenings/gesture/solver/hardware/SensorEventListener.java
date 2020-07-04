package com.github.mleenings.gesture.solver.hardware;

/** dummy code for hardware stuff (should be for example the smartphone sensor event listener) */
public interface SensorEventListener {

  void onSensorChanged(SensorEvent event);

  void onAccuracyChanged(Sensor sensor, int accuracy);
}

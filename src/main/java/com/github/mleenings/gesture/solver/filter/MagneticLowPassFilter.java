package com.github.mleenings.gesture.solver.filter;

import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;

/** Low pass filter for the magnetic sensor */
public class MagneticLowPassFilter implements SensorFilter {
  private SimpleLowPassFilter simpleLowPassFilter = new SimpleLowPassFilter();

  @Override
  public SensorMotionData filter(SensorMotionData smd) {
    SensorMotionData filtered = new SensorMotionData(smd);
    filtered.setOrientationData(simpleLowPassFilter.filter(smd.getMagneticFieldData()));
    return filtered;
  }
}

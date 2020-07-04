package com.github.mleenings.gesture.solver.filter;

import com.github.mleenings.gesture.solver.data.objects.sensor.SensorData;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;

/** low pass filter for the gyroscope sensor */
public class OrientationLowPassFilter implements SensorFilter {
  private SimpleLowPassFilter simpleLowPassFilter = new SimpleLowPassFilter();

  @Override
  public SensorMotionData filter(SensorMotionData smd) {
    SensorMotionData filtered = new SensorMotionData(smd);
    SensorData orientationData = smd.getOrientationData();
    filtered.setOrientationData(simpleLowPassFilter.filter(orientationData));
    return filtered;
  }
}

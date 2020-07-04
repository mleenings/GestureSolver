package com.github.mleenings.gesture.solver.filter;

import com.github.mleenings.gesture.solver.data.objects.sensor.SensorData;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;

/** mean filter for the orientation sensor */
public class MeanOrientationFilter implements SensorFilter {
  private MeanFilter meanFilter;

  /** @param k number of elements for smoothing (should be odd value) */
  public MeanOrientationFilter(int k) {
    meanFilter = new MeanFilter(k);
  }

  @Override
  public SensorMotionData filter(SensorMotionData smd) {
    final SensorMotionData newSmd = new SensorMotionData(smd);
    final SensorData orientationData = smd.getOrientationData();
    newSmd.setOrientationData(
        new SensorData(
            meanFilter.filter(orientationData.getValues()), orientationData.getEventTime()));
    return newSmd;
  }
}

package com.github.mleenings.gesture.solver.filter;

import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;

/** Butterworth low-pass filter for the gyroscope to reduce the noise */
public abstract class ButterworthFilterGyroscope extends ButterworthFilter implements SensorFilter {

  /** constructor */
  public ButterworthFilterGyroscope() {
    super();
  }

  /**
   * filter the Gyroscope SensorMotionData
   *
   * @param smd
   * @return
   */
  public SensorMotionData filter(SensorMotionData smd) {
    smd.getGyroscopeData().setValues(filter(smd.getGyroscopeData().getValues()));
    return smd;
  }
}

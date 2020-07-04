package com.github.mleenings.gesture.solver.filter;

import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import com.github.mleenings.gesture.solver.filter.hardware.gyroscope.ImuOCfQuaternion;
import com.github.mleenings.gesture.solver.filter.hardware.gyroscope.Orientation;
import com.github.mleenings.gesture.solver.hardware.SensorManager;

/** Kalman Filter to reduce the noise. */
public class KalmanFilterGyroscope implements SensorFilter {
  private static Orientation orientation = new ImuOCfQuaternion();
  private SensorManager sensorManager;

  /**
   * constructor
   *
   * @param sensorManager
   */
  public KalmanFilterGyroscope(SensorManager sensorManager) {
    this.sensorManager = sensorManager;
  }

  @Override
  public SensorMotionData filter(SensorMotionData smd) {
    orientation.setMagneticField(smd.getMagneticFieldData().getValues());
    orientation.setAccelerometer(smd.getAccelerometerData().getValues());
    orientation.setGyroscope(
        smd.getGyroscopeData().getValues(), smd.getGyroscopeData().getEventTime());
    final SensorMotionData filteredSmd = new SensorMotionData(smd);
    filteredSmd.getGyroscopeData().setValues(orientation.getOrientation());
    return filteredSmd;
  }
}

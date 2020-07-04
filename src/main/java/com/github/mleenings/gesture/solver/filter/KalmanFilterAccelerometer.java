package com.github.mleenings.gesture.solver.filter;

import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import com.github.mleenings.gesture.solver.filter.hardware.acceleration.ImuLaKfQuaternion;
import com.github.mleenings.gesture.solver.filter.hardware.acceleration.ImuLinearAccelerationInterface;

/** Kalman Filter for the Accelerometer to reduce the noise. */
public class KalmanFilterAccelerometer implements SensorFilter {
  private static ImuLinearAccelerationInterface imuLinearAcceleration = new ImuLaKfQuaternion();

  @Override
  public SensorMotionData filter(SensorMotionData smd) {
    float[] linearAcceleration;
    float[] rotation = new float[3];
    System.arraycopy(
        smd.getGyroscopeData().getValues(),
        0,
        rotation,
        0,
        smd.getGyroscopeData().getValues().length);
    imuLinearAcceleration.setAcceleration(smd.getAccelerometerData().getValues());
    imuLinearAcceleration.setMagnetic(smd.getMagneticFieldData().getValues());
    imuLinearAcceleration.setGyroscope(rotation, System.nanoTime());
    linearAcceleration = imuLinearAcceleration.getLinearAcceleration();
    smd.getAccelerometerData().setValues(linearAcceleration);
    return smd;
  }
}

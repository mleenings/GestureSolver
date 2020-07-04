package com.github.mleenings.gesture.solver.gesturizer;

import com.github.mleenings.gesture.solver.data.objects.sensor.SensorData;
import com.github.mleenings.gesture.solver.hardware.SensorManager;

/** compass North : 360°/0° East : 90° South : 180° West : 270° */
public class OrientationCalculation {
  private final float[] mAccelerometerReading = new float[3];
  private final float[] mMagnetometerReading = new float[3];
  private final float[] mRotationMatrix = new float[9];
  private final float[] mInclinationMatrix = new float[9];
  private final float[] mOrientationAngles = new float[3];
  private SensorManager mSensorManager;
  private long timestamp;

  /**
   * constructor
   *
   * @param sensorManager
   */
  public OrientationCalculation(SensorManager sensorManager) {
    this.mSensorManager = sensorManager;
  }

  private void setAccelerometerData(final SensorData accelerometerData) {
    System.arraycopy(
        accelerometerData.getValues(), 0, mAccelerometerReading, 0, mAccelerometerReading.length);
    timestamp = accelerometerData.getEventTime();
  }

  private void setMagnetometerData(final SensorData magnetometerData) {
    System.arraycopy(
        magnetometerData.getValues(), 0, mMagnetometerReading, 0, mMagnetometerReading.length);
  }

  private void updateOrientationAngles() {
    mSensorManager.getRotationMatrix(
        mRotationMatrix, null, mAccelerometerReading, mMagnetometerReading);
    mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
  }

  /** @return the orientation */
  public SensorData calcOrientation() {
    return calcOrientation(
        new SensorData(mAccelerometerReading, timestamp),
        new SensorData(mMagnetometerReading, timestamp));
  }

  /**
   * @param accelerometerData
   * @param magnetometerData
   * @return the orientation
   */
  public SensorData calcOrientation(
      final SensorData accelerometerData, final SensorData magnetometerData) {
    setAccelerometerData(accelerometerData);
    setMagnetometerData(magnetometerData);
    updateOrientationAngles();
    return new SensorData(mOrientationAngles, timestamp);
  }

  /**
   * @param accelerometerData
   * @param magnetometerData
   * @return the azimuth as degrees
   */
  public int calcAzimuthInDegrees(
      final SensorData accelerometerData, final SensorData magnetometerData) {
    setAccelerometerData(accelerometerData);
    setMagnetometerData(magnetometerData);
    SensorManager.getRotationMatrix(
        mRotationMatrix, mInclinationMatrix, mAccelerometerReading, mMagnetometerReading);
    return (int)
            (Math.toDegrees(SensorManager.getOrientation(mRotationMatrix, mOrientationAngles)[0])
                + 360)
        % 360;
  }

  /**
   * @param orientation
   * @return the azimuth as degrees
   */
  public int calcAzimuthInDegrees(final SensorData orientation) {
    return (int) (Math.toDegrees(orientation.getValues()[0]) + 360) % 360;
  }
}

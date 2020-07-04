package com.github.mleenings.gesture.solver.gesturizer;

import com.github.mleenings.gesture.solver.dao.CalibrationSensorDAO;
import com.github.mleenings.gesture.solver.data.objects.sensor.CalibrationData;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorData;
import com.github.mleenings.gesture.solver.data.objects.sensor.StatisticSensorData;

/** Utility class for the Acceleromation */
public class AcceleromationDerivationCalculation {
  // nanoseconds to seconds
  public static final float NS2S = 1.0f / 1000000000.0f; // 1e-9
  private CalibrationSensorDAO calibrationSensorDAO;
  private float[] accelExpectedValue = new float[3];
  private float[] lastVelocity = new float[3];
  private float[] lastDisplacement = new float[3];
  private float lastTimestampVelo = 0.0f;
  private float lastTimestampDispl = 0.0f;

  /** constructor */
  public AcceleromationDerivationCalculation() {
    this(new CalibrationSensorDAO());
  }

  /**
   * constructor
   *
   * @param calibrationSensorDAO
   */
  public AcceleromationDerivationCalculation(CalibrationSensorDAO calibrationSensorDAO) {
    this.calibrationSensorDAO = calibrationSensorDAO;
    loadCalibrationData();
  }

  /**
   * set the CalibrationData
   *
   * @param calibrationData
   */
  public void setCalibrationData(final CalibrationData calibrationData) {
    if (calibrationData != null) {
      final StatisticSensorData statAccel = calibrationData.getAccelerometerStatistic();
      accelExpectedValue = statAccel.getExpectedValue();
    }
  }

  /** load CalibrationData from external storage */
  public void loadCalibrationData() {
    final CalibrationData calibrationData = calibrationSensorDAO.loadData();
    setCalibrationData(calibrationData);
  }

  /** reset all values */
  public void reset() {
    lastVelocity = new float[3];
    lastDisplacement = new float[3];
    lastTimestampVelo = 0.0f;
    lastTimestampDispl = 0.0f;
  }

  /**
   * calculate the velocity
   *
   * @param accel
   * @return the SensorData
   */
  public SensorData calcVelocity(final SensorData accel) {
    final SensorData accelToUse = accel;
    final float[] vAccel = accelToUse.getValues();
    final float[] actualVelocity = new float[vAccel.length];
    final float dT = calcDifferenceOfTime(lastTimestampVelo, accelToUse.getEventTime());
    for (int i = 0; i < actualVelocity.length; i++) {
      actualVelocity[i] = lastVelocity[i] + dT * (vAccel[i] - accelExpectedValue[i]);
    }
    lastVelocity = actualVelocity;
    lastTimestampVelo = accelToUse.getEventTime();
    return new SensorData(actualVelocity, accelToUse.getEventTime());
  }

  protected float calcDifferenceOfTime(float lastTimestamp, float actualTimestamp) {
    return Float.compare(lastTimestamp, 0.0f) == 0
        ? lastTimestamp
        : (actualTimestamp - lastTimestamp) * NS2S;
  }

  /**
   * calculate the displacement
   *
   * @param velo
   * @return the Sensordata
   */
  public SensorData calcDisplacement(final SensorData velo) {
    final float[] vVelo = velo.getValues();
    final float[] actualDisplacement = new float[3];
    final float dT = calcDifferenceOfTime(lastTimestampDispl, velo.getEventTime());
    for (int i = 0; i < vVelo.length; i++) {
      actualDisplacement[i] =
          vVelo[i] > 0 ? lastDisplacement[i] + dT * vVelo[i] : lastDisplacement[i];
    }
    lastDisplacement = actualDisplacement;
    lastTimestampDispl = velo.getEventTime();
    return new SensorData(actualDisplacement, velo.getEventTime());
  }
}

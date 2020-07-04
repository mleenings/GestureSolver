package com.github.mleenings.gesture.solver.gesturizer;

import com.github.mleenings.gesture.solver.dao.CalibrationSensorDAO;
import com.github.mleenings.gesture.solver.data.objects.sensor.CalibrationData;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import com.github.mleenings.gesture.solver.data.objects.sensor.StatisticSensorData;

import java.util.ArrayList;
import java.util.List;

/** Calibration of the sensor data */
public class CalibrationCalculation {
  private static CalibrationSensorDAO dao;

  /** private constructor */
  private CalibrationCalculation() {
    // private constructor
  }

  /**
   * calculate the calibration data
   *
   * @param list
   */
  public static void calibrate(List<SensorMotionData> list) {
    dao = new CalibrationSensorDAO();
    final CalibrationData calibrationData = calcMinMaxEV(list);
    saveCalibrationData(calibrationData);
  }

  protected static CalibrationData calcMinMaxEV(List<SensorMotionData> list) {
    List<float[]> accelVectors = new ArrayList<>();
    List<float[]> gyroVectors = new ArrayList<>();
    for (SensorMotionData smd : list) {
      accelVectors.add(smd.getAccelerometerData().getValues());
      gyroVectors.add(smd.getGyroscopeData().getValues());
    }

    return new CalibrationData(calculateData(accelVectors), calculateData(gyroVectors));
  }

  private static StatisticSensorData calculateData(List<float[]> vectors) {
    float[] max = minFloat();
    float[] min = maxFloat();
    float[] expectedVal = new float[3];
    for (float[] v : vectors) {
      for (int i = 0; i < v.length; i++) {
        max[i] = Math.max(max[i], v[i]);
        min[i] = Math.min(min[i], v[i]);
        expectedVal[i] += v[i];
      }
    }
    for (int i = 0; i < expectedVal.length; i++) {
      expectedVal[i] = expectedVal[i] / vectors.size();
    }

    return new StatisticSensorData(min, max, expectedVal);
  }

  private static float[] maxFloat() {
    float[] max = new float[3];
    for (int i = 0; i < max.length; i++) {
      max[i] = Float.MAX_VALUE;
    }
    return max;
  }

  private static float[] minFloat() {
    float[] min = new float[3];
    for (int i = 0; i < min.length; i++) {
      min[i] = Float.MAX_VALUE * -1.0f;
    }
    return min;
  }

  private static void saveCalibrationData(final CalibrationData calibrationData) {
    dao.saveData(calibrationData);
  }
}

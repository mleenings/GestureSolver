package com.github.mleenings.gesture.solver.classifier;

import com.github.mleenings.gesture.solver.data.objects.sensor.*;

import java.util.List;

/** Classifier for the gesture 'stand' */
public class StandClassifier extends ClassifierBase implements Classifier {

  private static final double DEFAULT_RANGE = 0.05;
  private StatisticSensorData accelerometerStat;
  private StatisticSensorData gyroscopeStat;

  /**
   * constructor
   *
   * @param calibrationData
   */
  public StandClassifier(CalibrationData calibrationData) {
    if (calibrationData != null) {
      accelerometerStat = calibrationData.getAccelerometerStatistic();
      gyroscopeStat = calibrationData.getGyroscopeStatistic();
    }
  }

  @Override
  public GestureInfo classify(List<SensorMotionData> activity, double range) {
    if (accelerometerStat == null || gyroscopeStat == null) {
      return null;
    } else {
      int countStand = 0;
      int len = activity.size();
      for (SensorMotionData smd : activity) {
        if (isInZeroRange(smd.getAccelerometerData()) && isInZeroRange(smd.getGyroscopeData())) {
          countStand++;
        }
      }
      if (countStand > len * 0.5) {
        return new GestureInfo(Gesture.STAND, -1.0);
      }
      return new GestureInfo(Gesture.UNKNOWN, -1.0);
    }
  }

  private boolean isInZeroRange(SensorData sd) {
    float[] v = sd.getValues();
    boolean isZero = true;
    for (int i = 0; i < v.length; i++) {
      isZero = isInZeroRange(v, i, accelerometerStat);
      if (!isZero) {
        break;
      }
    }
    return isZero;
  }

  private boolean isInZeroRange(float[] values, int idx, final StatisticSensorData stat) {
    final float min = stat.getMin()[idx];
    final float max = stat.getMax()[idx];
    return (Float.compare(values[idx], 0.0f) == 0)
        || ((min - min * DEFAULT_RANGE) < values[idx] && values[idx] < (max + max * DEFAULT_RANGE));
  }

  @Override
  public double getDefaultRange() {
    return DEFAULT_RANGE;
  }
}

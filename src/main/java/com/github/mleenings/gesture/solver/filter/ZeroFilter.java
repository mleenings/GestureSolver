package com.github.mleenings.gesture.solver.filter;

import com.github.mleenings.gesture.solver.data.objects.sensor.CalibrationData;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorData;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import com.github.mleenings.gesture.solver.data.objects.sensor.StatisticSensorData;

import java.util.LinkedList;
import java.util.List;

/** zero filter, that set the minimal value (noise) to zero */
public class ZeroFilter {
  private static final double RANGE = 0.02; // in %

  private CalibrationData calibrationData;
  private List<float[]> lastAccel = new LinkedList<>();
  private List<float[]> lastGyro = new LinkedList<>();

  private int k = 3;

  /**
   * constructor
   *
   * @param calibrationData
   */
  public ZeroFilter(CalibrationData calibrationData) {
    this.calibrationData = calibrationData;
  }

  /**
   * @param smd
   * @return the zero filtered SensorMotionData
   */
  public SensorMotionData zeroFilterSimple(final SensorMotionData smd) {
    final SensorMotionData filtered = new SensorMotionData(smd);
    filtered.setAccelerometerData(
        zeroFilterSimple(
            filtered.getAccelerometerData(), calibrationData.getAccelerometerStatistic()));
    filtered.setGyroscopeData(
        zeroFilterSimple(filtered.getGyroscopeData(), calibrationData.getGyroscopeStatistic()));
    return filtered;
  }

  private SensorData zeroFilterSimple(final SensorData sd, final StatisticSensorData stat) {
    final float[] v = sd.getValues();
    final float[] vNoise2Zero = new float[3];
    for (int i = 0; i < v.length; i++) {
      if (isInZeroRange(v, i, stat)) {
        vNoise2Zero[i] = 0;
      } else {
        vNoise2Zero[i] = v[i];
      }
    }
    return new SensorData(vNoise2Zero, sd.getEventTime());
  }

  /**
   * Test
   *
   * @param smd
   * @return
   */
  public SensorMotionData zeroFilter(final SensorMotionData smd) {
    final SensorMotionData filtered = new SensorMotionData(smd);
    filtered.setAccelerometerData(
        zeroFilter(
            filtered.getAccelerometerData(),
            lastAccel,
            calibrationData.getAccelerometerStatistic()));
    filtered.setGyroscopeData(
        zeroFilter(filtered.getGyroscopeData(), lastGyro, calibrationData.getGyroscopeStatistic()));
    return filtered;
  }

  private SensorData zeroFilter(
      final SensorData sd, List<float[]> lastValues, final StatisticSensorData stat) {
    final float[] v = sd.getValues();
    float[] vNoise2Zero = new float[3];

    lastValues.add(v);
    if (lastValues.size() > k) {
      lastValues.remove(0);
    }

    if (k > lastValues.size()) {
      vNoise2Zero = zeroFilterSimple(v, stat);
    } else {
      for (int i = 0; i < v.length; i++) {
        vNoise2Zero[i] = zeroRangeValue(i, lastValues, stat);
      }
    }

    return new SensorData(vNoise2Zero, sd.getEventTime());
  }

  private float zeroRangeValue(int idx, List<float[]> lastValues, final StatisticSensorData stat) {
    final float value;
    int countHowManyInRange = 0;
    for (int i = 0; i < lastValues.size(); i++) {
      countHowManyInRange =
          isInZeroRange(lastValues.get(i), idx, stat)
              ? countHowManyInRange + 1
              : countHowManyInRange;
    }
    if (countHowManyInRange > lastValues.size() / 2
        || isInZeroRange(lastValues.get(lastValues.size() - 1), idx, stat)) {
      value = 0;
    } else {
      value = lastValues.get(lastValues.size() - 1)[idx];
    }
    return value;
  }

  private float[] zeroFilterSimple(float[] values, final StatisticSensorData stat) {
    final float[] vNoise2Zero = new float[values.length];
    for (int i = 0; i < values.length; i++) {
      if (isInZeroRange(values, i, stat)) {
        vNoise2Zero[i] = 0;
      } else {
        vNoise2Zero[i] = values[i];
      }
    }
    return vNoise2Zero;
  }

  private boolean isInZeroRange(float[] values, int idx, final StatisticSensorData stat) {
    final float min = stat.getMin()[idx];
    final float max = stat.getMax()[idx];
    return (min + min * RANGE) < values[idx] && values[idx] < (max + max * RANGE);
  }
}

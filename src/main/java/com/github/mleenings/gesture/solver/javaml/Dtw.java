package com.github.mleenings.gesture.solver.javaml;

import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeriesPoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Dynamic Time Wrapping */
public class Dtw {

  private static final Logger LOG = Logger.getLogger(Dtw.class.getName());

  private Dtw() {
    // private constructor
  }

  /**
   * @param from
   * @param to
   * @return the distance beteween two trainingsdata
   */
  public static double getDistance(List<SensorMotionData> from, List<SensorMotionData> to) {
    return DTW.getWarpDistBetween(convertToTimeseries(from), convertToTimeseries(to));
  }

  /**
   * @param from
   * @param to
   * @return the distance beteween two trainingsdata and ignore the original timestamp
   */
  public static double getDistanceWithNewTime(
      List<SensorMotionData> from, List<SensorMotionData> to) {
    return DTW.getWarpDistBetween(
        convertToTimeseriesWithNewTime(from), convertToTimeseriesWithNewTime(to));
  }

  private static TimeSeries convertToTimeseries(List<SensorMotionData> listOfSmd) {
    if (!listOfSmd.isEmpty()) {
      float[] vec = listOfSmd.get(0).getVector();
      TimeSeries ts = new TimeSeries(vec.length);
      for (SensorMotionData smd : listOfSmd) {
        TimeSeriesPoint point = convertToTimeSeriesPoint(smd);
        ts.addLast(smd.getAccelerometerData().getEventTime(), point);
      }
      return ts;
    }
    return new TimeSeries(6);
  }

  private static TimeSeries convertToTimeseriesWithNewTime(List<SensorMotionData> listOfSmd) {
    if (!listOfSmd.isEmpty()) {
      float[] vec = listOfSmd.get(0).getVector();
      final TimeSeries ts = new TimeSeries(vec.length);
      int time = 0;
      for (SensorMotionData smd : listOfSmd) {
        TimeSeriesPoint point = convertToTimeSeriesPoint(smd);
        ts.addLast(time, point);
        time++;
      }
      return ts;
    }
    return new TimeSeries(6);
  }

  private static TimeSeriesPoint convertToTimeSeriesPoint(SensorMotionData smd) {
    double[] d = new double[smd.getVector().length];
    float[] v = smd.getVector();
    for (int i = 0; i < v.length; i++) {
      d[i] = v[i];
    }
    return new TimeSeriesPoint(d);
  }

  /**
   * @param from
   * @param to
   * @return the costmatrix between two trainingdatas
   */
  public static double[][] getCostMatrix(List<SensorMotionData> from, List<SensorMotionData> to) {
    return getCostMatrix(convertToTimeseriesWithNewTime(from), convertToTimeseriesWithNewTime(to));
  }

  /**
   * part of the method: private static TimeWarpInfo DynamicTimeWarp(TimeSeries tsI, TimeSeries tsJ)
   * in the class: net.sf.javaml.distance.fastdtw.dtw.DTW used for: BDA
   */
  public static double[][] getCostMatrix(TimeSeries tsI, TimeSeries tsJ) {
    double costMatrix[][] = new double[tsI.size()][tsJ.size()];
    int maxI = tsI.size() - 1;
    int maxJ = tsJ.size() - 1;
    costMatrix[0][0] = euclideanDist(tsI.getMeasurementVector(0), tsJ.getMeasurementVector(0));
    for (int j = 1; j <= maxJ; j++)
      costMatrix[0][j] =
          costMatrix[0][j - 1]
              + euclideanDist(tsI.getMeasurementVector(0), tsJ.getMeasurementVector(j));

    for (int i = 1; i <= maxI; i++) {
      costMatrix[i][0] =
          costMatrix[i - 1][0]
              + euclideanDist(tsI.getMeasurementVector(i), tsJ.getMeasurementVector(0));
      for (int j = 1; j <= maxJ; j++) {
        double minGlobalCost =
            Math.min(
                costMatrix[i - 1][j], Math.min(costMatrix[i - 1][j - 1], costMatrix[i][j - 1]));
        costMatrix[i][j] =
            minGlobalCost + euclideanDist(tsI.getMeasurementVector(i), tsJ.getMeasurementVector(j));
      }
    }
    return costMatrix;
  }

  private static double euclideanDist(double[] vector1, double[] vector2) {
    try {
      final DTW dtw = new DTW();
      final Method m =
          dtw.getClass()
              .getDeclaredMethod("euclideanDist", new Class[] {double[].class, double[].class});
      m.setAccessible(true);
      return (double) m.invoke(dtw, vector1, vector2);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      LOG.log(Level.ALL, e.getMessage(), e);
      return Double.MAX_VALUE;
    }
  }
}

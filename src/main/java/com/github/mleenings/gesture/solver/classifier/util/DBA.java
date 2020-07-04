package com.github.mleenings.gesture.solver.classifier.util;

import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import com.github.mleenings.gesture.solver.javaml.Dtw;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/** DTW Barycenter Averaging */
public class DBA {

  private static final Logger LOG = Logger.getLogger(DBA.class.getName());

  private DBA() {
    // private constructor
  }

  /**
   * @param D
   * @return the average trainingsset
   */
  public static List<SensorMotionData> average(final List<List<SensorMotionData>> D) {
    final double epsilon = 0.001;
    double lastDist = Double.MAX_VALUE;
    List<SensorMotionData> T_avg_last = new LinkedList<>();
    int j = 0;

    // get the medoid of the set of sequence D
    List<SensorMotionData> T_avg = medoid(D);

    while (lastDist > epsilon && j < 25) {
      T_avg = D_update(T_avg, D);
      if (j > 0) {
        final double dist = Dtw.getDistanceWithNewTime(T_avg, T_avg_last);
        if (Math.abs(dist / lastDist - 1) < 0.0000001) {
          break;
        }
        lastDist = dist;
      }
      T_avg_last = T_avg;
      j++;
    }
    return T_avg;
  }

  /**
   * @param D
   * @param i
   * @return the average trainingsset
   */
  public static List<SensorMotionData> average(final List<List<SensorMotionData>> D, final int i) {
    // get the medoid of the set of sequence D
    List<SensorMotionData> T_avg = medoid(D);
    int j = 0;
    while (j < i) {
      T_avg = D_update(T_avg, D);
      j++;
    }
    return T_avg;
  }

  /**
   * @param T_avg_init the average sequence to refine
   * @param D the set of sequences to average
   */
  private static List<SensorMotionData> D_update(
      final List<SensorMotionData> T_avg_init, final List<List<SensorMotionData>> D) {
    // Step #1: compute the multiple alignment for T_avg_init
    int L = T_avg_init.size();
    List<SensorMotionData>[] alignment = new ArrayList[L];
    for (int i = 0; i < alignment.length; i++) {
      alignment[i] = new ArrayList<>();
    }
    for (List<SensorMotionData> S : D) {
      List<SensorMotionData>[] alignment_for_S = DTW_multiple_alignment(T_avg_init, S);
      for (int i = 0; i < L; i++) {
        alignment[i].addAll(alignment_for_S[i]);
      }
    }
    List<SensorMotionData> T_avg = new ArrayList<>();
    // Step #2: compute the multiple alignment for the alignment
    for (int i = 0; i < L; i++) {
      // arithmetic mean on the set
      T_avg.add(mean(alignment[i]));
    }
    return T_avg;
  }

  /**
   * @param S_ref the sequence for which the alignment is computed
   * @param S the sequence to align to S_ref using DTW
   */
  private static List<SensorMotionData>[] DTW_multiple_alignment(
      final List<SensorMotionData> S_ref, final List<SensorMotionData> S) {
    // Step #1: compute the accumulated cost matrix of DTW
    double[][] cost = Dtw.getCostMatrix(S_ref, S);
    // Step #2: store the elements associated to S_ref
    int L = S_ref.size();
    List<SensorMotionData>[] alignment = new ArrayList[L];
    for (int i = 0; i < alignment.length; i++) {
      alignment[i] = new ArrayList<SensorMotionData>();
    }
    // i iterates over the elements of S_ref (rows(cumul_cost) -> cost.length)
    int i = S_ref.size() - 1;
    // j iterates over the elements of S (colums(cumul_cost) -> cost[0].length)
    int j = S.size() - 1;
    while (i >= 0 && j >= 0) {
      alignment[i].add(S.get(j));
      if (i == 0) {
        j = i - 1;
      } else if (j == 0) {
        i = i - 1;
      } else {
        double score = min(cost[i - 1][j - 1], cost[i][j - 1], cost[i - 1][j]);
        if (Double.compare(score, cost[i - 1][j - 1]) == 0) {
          i = i - 1;
          j = j - 1;
        } else if (Double.compare(score, cost[i - 1][j]) == 0) {
          i = i - 1;
        } else {
          j = j - 1;
        }
      }
    }
    return alignment;
  }

  private static List<SensorMotionData> medoid(List<List<SensorMotionData>> D) {
    return mendoidMinDistanceToAll(D);
  }

  private static List<SensorMotionData> mendoidRandom(List<List<SensorMotionData>> D) {
    return D.get(randInt(0, D.size() - 1));
  }

  private static List<SensorMotionData> mendoidMinDistanceToAll(List<List<SensorMotionData>> D) {
    double minDist = Double.MAX_VALUE;
    int idxMinDist = 0;
    double[][] distMatrix = getDistMatrix(D);
    double[] avgDist = averageDistances(distMatrix);
    for (int i = 0; i < avgDist.length; i++) {
      if (avgDist[i] < minDist) {
        idxMinDist = i;
        minDist = avgDist[i];
      }
    }
    return D.get(idxMinDist);
  }

  private static double[][] getDistMatrix(List<List<SensorMotionData>> D) {
    final int length = D.size();
    double[][] distMatrix = new double[length][length];
    for (int i = 0; i < length; i++) {
      for (int j = 0; j < length; j++) {
        if (i != j) {
          distMatrix[i][j] = Dtw.getDistanceWithNewTime(D.get(i), D.get(j));
        }
      }
    }
    return distMatrix;
  }

  private static double[] averageDistances(double[][] distanceMatrix) {
    double[] avg = new double[distanceMatrix.length];
    for (int i = 0; i < distanceMatrix.length; i++) {
      double sum = 0.0;
      for (int j = 0; j < distanceMatrix.length; j++) {
        if (i != j) {
          sum += distanceMatrix[i][j];
        }
      }
      avg[i] = sum / (double) distanceMatrix.length;
    }
    return avg;
  }

  private static int randInt(int min, int max) {
    return new Random().nextInt(max - min) + min;
  }

  private static SensorMotionData mean(final List<SensorMotionData> list) {
    SensorMotionData sum = new SensorMotionData();
    for (SensorMotionData smd : list) {
      sum = add(sum, smd);
    }
    return div(sum, list.size());
  }

  private static SensorMotionData add(final SensorMotionData smd1, final SensorMotionData smd2) {
    float[] v1 = smd1.getVector();
    float[] v2 = smd2.getVector();
    float[] sum = new float[v1.length];
    final int minLen = Math.min(v1.length, v2.length);
    for (int i = 0; i < minLen; i++) {
      sum[i] = v1[i] + v2[i];
    }
    return new SensorMotionData(sum);
  }

  private static SensorMotionData div(final SensorMotionData smd, final int div) {
    float[] v = null;
    try {
      v = smd.getVector();
    } catch (NullPointerException e) {
      LOG.log(Level.ALL, e.getMessage(), e);
      return null;
    }
    float[] vRes = new float[v.length];
    float fDiv = (float) div;
    for (int i = 0; i < v.length; i++) {
      vRes[i] = v[i] / fDiv;
    }
    SensorMotionData smdRes = new SensorMotionData(vRes);
    return smdRes;
  }

  private static double min(final double d1, final double d2, final double d3) {
    double min12 = Math.min(d1, d2);
    return Math.min(min12, d3);
  }
}

package com.github.mleenings.gesture.solver.filter;

import java.util.LinkedList;
import java.util.List;

/** mean filter */
public class MeanFilter {
  private List<float[]> values = new LinkedList<>();
  private int k;

  /** @param k number of elements for smoothing (should be odd value) */
  public MeanFilter(int k) {
    this.k = k;
  }

  /**
   * @param value
   * @return the mean of the last k values
   */
  public float[] filter(float[] value) {
    values.add(value);
    if (k < values.size()) {
      values.remove(0);
    }
    float[] mean = new float[value.length];
    for (float[] val : values) {
      for (int i = 0; i < val.length; i++) {
        mean[i] += val[i];
      }
    }
    for (int i = 0; i < mean.length; i++) {
      mean[i] = mean[i] / values.size();
    }

    return mean;
  }
}

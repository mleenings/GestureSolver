package com.github.mleenings.gesture.solver.classifier;

import com.github.mleenings.gesture.solver.data.objects.sensor.GestureInfo;
import com.github.mleenings.gesture.solver.data.objects.sensor.PreGestureClassificationData;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;

import java.util.List;

/**
 * For performance: KNN with only a subset of all trainingsdata (the subset that are the nearest to
 * the centroid)
 */
public class KnnSubsetClassifier extends ClassifierBase implements Classifier {

  private static final double DEFAULT_RANGE = 0.6;

  /** constructor */
  public KnnSubsetClassifier() {
    // defualt constructor
  }

  /**
   * constructor
   *
   * @param preGestureClassificationData
   */
  public KnnSubsetClassifier(final PreGestureClassificationData preGestureClassificationData) {
    super(preGestureClassificationData);
  }

  @Override
  public GestureInfo classify(List<SensorMotionData> actualData, double range) {
    if (preGestureClassificationData == null) {
      return null;
    }
    KnnClassifier knnClassifier = new KnnClassifier(preGestureClassificationData);
    knnClassifier.setTrainingsdata(preGestureClassificationData.getTrainingsSubSet());
    return knnClassifier.classify(actualData, range);
  }

  @Override
  public double getDefaultRange() {
    return DEFAULT_RANGE;
  }
}

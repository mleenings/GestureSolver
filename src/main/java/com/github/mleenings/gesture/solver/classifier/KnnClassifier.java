package com.github.mleenings.gesture.solver.classifier;

import com.github.mleenings.gesture.solver.data.objects.sensor.Gesture;
import com.github.mleenings.gesture.solver.data.objects.sensor.GestureInfo;
import com.github.mleenings.gesture.solver.data.objects.sensor.PreGestureClassificationData;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import com.github.mleenings.gesture.solver.javaml.Dtw;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** k-nearest neighbors algorithm */
public class KnnClassifier extends ClassifierBase implements Classifier {

  private static final double DEFAULT_RANGE = 0.8;

  /** constructor */
  public KnnClassifier() {
    // defualt constructor
  }

  /**
   * constructor
   *
   * @param preGestureClassificationData
   */
  public KnnClassifier(final PreGestureClassificationData preGestureClassificationData) {
    super(preGestureClassificationData);
  }

  /**
   * classified the trainingsset
   *
   * @param actualData
   * @param range
   * @return
   */
  @Override
  public GestureInfo classify(final List<SensorMotionData> actualData, final double range) {
    final ListMultimap<Double, List<SensorMotionData>> distanceTrainingsset =
        calculateDistances(actualData, range);
    return calculateKNN(distanceTrainingsset);
  }

  @Override
  public double getDefaultRange() {
    return DEFAULT_RANGE;
  }

  private ListMultimap<Double, List<SensorMotionData>> calculateDistances(
      final List<SensorMotionData> actualData, final double range) {
    // ListMultimap sorted key (=distances) sort by size
    final ListMultimap<Double, List<SensorMotionData>> distanceTrainingsset =
        MultimapBuilder.treeKeys().arrayListValues().build();
    for (List<SensorMotionData> oneTrainingsset : trainingsdata) {
      final double dist = Dtw.getDistance(actualData, oneTrainingsset);
      if (isDistanceInRange(dist, range)) {
        distanceTrainingsset.put(dist, oneTrainingsset);
      }
    }
    return distanceTrainingsset;
  }

  private GestureInfo calculateKNN(
      final ListMultimap<Double, List<SensorMotionData>> distanceTrainingsset) {
    double sumDist = 0;
    final int k = (int) Math.sqrt(trainingsdata.size());
    Gesture detectedGesture = Gesture.UNKNOWN;
    final Map<Gesture, AtomicInteger> countNearestDistances = new EnumMap<>(Gesture.class);
    final Map<Gesture, Double> gestureDist = new EnumMap<>(Gesture.class);
    for (Map.Entry<Double, List<SensorMotionData>> entry : distanceTrainingsset.entries()) {
      final Gesture g = entry.getValue().get(0).getGesture();
      final double dist = entry.getKey();
      final Double tmpDistOfGesture = gestureDist.get(detectedGesture);
      final double distOfGesture = tmpDistOfGesture == null ? 0 : tmpDistOfGesture.doubleValue();
      gestureDist.put(g, distOfGesture + dist);
      sumDist += dist;
      AtomicInteger numberOfNN = countNearestDistances.get(g);
      if (numberOfNN == null) {
        countNearestDistances.put(g, new AtomicInteger(1));
      } else if (numberOfNN.intValue() == k) {
        detectedGesture = g;
        break;
      } else {
        numberOfNN.incrementAndGet();
      }
    }
    Double tmpMminDist = gestureDist.get(detectedGesture);
    double minDist = tmpMminDist == null ? Double.MAX_VALUE : tmpMminDist.doubleValue();
    return new GestureInfo(
        detectedGesture, calculateProbability(detectedGesture, sumDist, minDist));
  }

  private double calculateProbability(Gesture g, double sumDist, double minDist) {
    final double probability;
    if ((g == Gesture.UNKNOWN && trainingsdata.isEmpty())
        || Double.compare(minDist, Double.MAX_VALUE) == 0) {
      probability = 1.0;
    } else {
      probability = (sumDist - minDist) / sumDist;
    }
    return probability;
  }
}

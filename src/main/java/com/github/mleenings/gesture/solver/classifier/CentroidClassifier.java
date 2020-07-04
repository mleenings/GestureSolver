package com.github.mleenings.gesture.solver.classifier;

import com.github.mleenings.gesture.solver.data.objects.sensor.*;
import com.github.mleenings.gesture.solver.javaml.Dtw;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** Centroid Classifier */
public class CentroidClassifier extends ClassifierBase implements Classifier {

  private static final double DEFAULT_RANGE = 0.7;
  private static final Logger log = Logger.getLogger(CentroidClassifier.class.getName());
  private double avgDistanceFromCentroid = 0.0;
  private double range = 0.0;

  public CentroidClassifier() {
    // default constructor
  }

  /**
   * constructor
   *
   * @param preGestureClassificationData
   */
  public CentroidClassifier(PreGestureClassificationData preGestureClassificationData) {
    super(preGestureClassificationData);
  }

  @Override
  public GestureInfo classify(List<SensorMotionData> actualData, double range) {
    this.range = range;
    Gesture minGesture = Gesture.UNKNOWN;
    double minDist = Double.MAX_VALUE;
    final ListMultimap<Gesture, PreGestureClassificationDataForGesture> map =
        preGestureClassificationData.getHcaCentroidgesturePreClassificationMap();
    final Map<Gesture, Double> gestureDist = new EnumMap<>(Gesture.class);
    for (final Map.Entry<Gesture, PreGestureClassificationDataForGesture> entry : map.entries()) {
      final PreGestureClassificationDataForGesture preClassify4Gesture = entry.getValue();
      final List<SensorMotionData> centroid = preClassify4Gesture.getAvgTrainingsSequence();
      final double dist2Centroid = Dtw.getDistanceWithNewTime(actualData, centroid);
      Double tmpDist = gestureDist.get(entry.getKey());
      if (tmpDist == null || Double.compare(dist2Centroid, tmpDist.doubleValue()) < 0) {
        gestureDist.put(entry.getKey(), dist2Centroid);
      }
      avgDistanceFromCentroid = preClassify4Gesture.getAvgTrainingsDistanceFromCentroid();
      if (isDistanceInRange(dist2Centroid, avgDistanceFromCentroid, range)
          && minDist > dist2Centroid) {
        minDist = dist2Centroid;
        minGesture = preClassify4Gesture.getGesture();
      }
    }
    final double probability = calculateProbability(gestureDist, minDist, minGesture);
    // WIP: to test
    // printAllProbabilities(gestureDist);
    GestureInfo classifyGestureInfo = new GestureInfo(minGesture, probability);
    classifyGestureInfo = updateWithProbability(classifyGestureInfo, gestureDist);
    return classifyGestureInfo;
  }

  private GestureInfo updateWithProbability(
      GestureInfo classifyGestureInfo, Map<Gesture, Double> gestureDist) {
    final ListMultimap<Double, Gesture> distanceTrainingsset = probabilityMap(gestureDist);
    GestureInfo minGestureInfo = classifyGestureInfo;
    if (!distanceTrainingsset.isEmpty()) {
      final Map.Entry<Double, Gesture> firstEntry =
          Iterables.getLast(distanceTrainingsset.entries());
      minGestureInfo = new GestureInfo(firstEntry.getValue(), firstEntry.getKey().doubleValue());
    }

    if (minGestureInfo.getGesture() != classifyGestureInfo.getGesture()
        && minGestureInfo.getProbability() > 5 * classifyGestureInfo.getProbability()) {
      return minGestureInfo;
    } else {
      return classifyGestureInfo;
    }
  }

  private ListMultimap<Double, Gesture> probabilityMap(Map<Gesture, Double> map) {
    final ListMultimap<Double, Gesture> listMultimap =
        MultimapBuilder.treeKeys().arrayListValues().build();
    for (Map.Entry<Gesture, Double> entry : map.entrySet()) {
      Gesture g = entry.getKey();
      double d = entry.getValue().doubleValue();
      double p = calculateProbability(map, d, g);
      listMultimap.put(p, g);
    }
    return listMultimap;
  }

  /**
   * to test
   *
   * @param gestureDist
   */
  private void printAllProbabilities(Map<Gesture, Double> gestureDist) {
    log.info("###############################");
    for (Map.Entry<Gesture, Double> entry : gestureDist.entrySet()) {
      Gesture g = entry.getKey();
      double d = entry.getValue().doubleValue();
      double probability = calculateProbability(gestureDist, d, g);
      log.info("Probability: P(" + g + ")=" + probability);
    }
    log.info("###############################");
  }

  private double calculateProbability(
      Map<Gesture, Double> gestureDist, double minDist, Gesture minGesture) {
    double probability;
    Gesture[] gestures = gestureDist.keySet().toArray(new Gesture[gestureDist.size()]);
    Double[] distances = gestureDist.values().toArray(new Double[gestureDist.size()]);

    if (distances.length == 0) {
      probability = getProbabilityWithNoClasses();
    } else if (distances.length == 1) {
      probability = getProbabilityWithOneClass(distances);
    } else {
      probability = getProbabilityWithMoreClasses(distances, gestures, minDist, minGesture);
    }
    return probability;
  }

  private double getProbabilityWithNoClasses() {
    return 1.0;
  }

  private double getProbabilityWithOneClass(Double[] distances) {
    double maxRange = maxRange(avgDistanceFromCentroid, range);
    double max = Math.max(distances[0], maxRange);
    double min = Math.min(distances[0], maxRange);
    return (max - min) / max;
  }

  private double getProbabilityWithMoreClasses(
      Double[] distances, Gesture[] gestures, double minDist, Gesture minGesture) {
    double empiricalProbability = getEmpiricalProbability(distances, gestures, minGesture);
    double set = getSetForProbability(distances, gestures, minDist, minGesture);
    return Double.compare(set, 0.0) == 0 ? 1.0 : empiricalProbability / set;
  }

  private double getEmpiricalProbability(
      Double[] distances, Gesture[] gestures, Gesture minGesture) {
    double empiricalProbability = 1.0;
    for (int i = 0; i < distances.length; i++) {
      final double di = distances[i];
      final Gesture gi = gestures[i];
      if (minGesture != gi) {
        empiricalProbability *= di;
      }
    }
    return empiricalProbability;
  }

  private double getSetForProbability(
      Double[] distances, Gesture[] gestures, double minDist, Gesture minGesture) {
    double set = 0.0;
    for (int i = 0; i < distances.length; i++) {
      final double di = distances[i];
      final Gesture gi = gestures[i];
      if ((Double.compare(di, minDist) == 0 && gi == minGesture) || gi != minGesture) {
        double mult = 1.0;
        for (int j = 0; j < distances.length; j++) {
          if (i != j) {
            final double dj = distances[j];
            final Gesture gj = gestures[j];
            if ((Double.compare(dj, minDist) == 0 && gj == minGesture) || (gj != minGesture)) {
              mult *= dj;
            }
          }
        }
        set += mult;
      }
    }
    return set;
  }

  @Override
  public double getDefaultRange() {
    return DEFAULT_RANGE;
  }
}

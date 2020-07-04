package com.github.mleenings.gesture.solver.classifier;

import com.github.mleenings.gesture.solver.classifier.util.ClassificationUtility;
import com.github.mleenings.gesture.solver.classifier.util.DBA;
import com.github.mleenings.gesture.solver.classifier.util.hca.HCA;
import com.github.mleenings.gesture.solver.data.objects.sensor.Gesture;
import com.github.mleenings.gesture.solver.data.objects.sensor.PreGestureClassificationData;
import com.github.mleenings.gesture.solver.data.objects.sensor.PreGestureClassificationDataForGesture;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import com.github.mleenings.gesture.solver.javaml.Dtw;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** Pre-Classification: HCA, BDA + Centroid Calculation */
public class PreGestureClassification {

  private static final double DEFAULT_PERCENT_MIN_DATA_FOR_CLASSIFICATION = 0.1;
  private static final double DEFAULT_PERCENT_MAX_DATA_FOR_CLASSIFICATION = 0.2;
  private static final double DEFAULT_PERCENT_FOR_SUBSET = 0.2;
  protected List<List<SensorMotionData>> trainingsset;
  protected PreGestureClassificationData preClassData = new PreGestureClassificationData();
  private double percentMinDataForClassification = -0.1;
  private double percentMaxDataForClassification = -0.1;
  private Map<Gesture, List<List<SensorMotionData>>> sortedTrainingssetByGesture =
      new EnumMap<>(Gesture.class);
  private ListMultimap<Gesture, List<List<SensorMotionData>>>
      hcaCentroidSortedTrainingssetByGesture = MultimapBuilder.treeKeys().arrayListValues().build();
  private double percentForSubset;
  private boolean saveAllTrainingssets;

  /**
   * constructor
   *
   * @param trainingsset
   */
  public PreGestureClassification(List<List<SensorMotionData>> trainingsset) {
    this(trainingsset, DEFAULT_PERCENT_FOR_SUBSET);
  }

  /**
   * constructor
   *
   * @param trainingsset
   * @param percentForSubset
   */
  public PreGestureClassification(
      List<List<SensorMotionData>> trainingsset, double percentForSubset) {
    this.trainingsset = trainingsset;
    this.percentForSubset = percentForSubset;
  }

  /**
   * save all trainingssets? important for knn classification!
   *
   * @param saveAllTrainingssets
   */
  public void setSaveAllTrainingssets(boolean saveAllTrainingssets) {
    this.saveAllTrainingssets = saveAllTrainingssets;
  }

  /**
   * train the trainingsset
   *
   * @return the trainingsset
   */
  public PreGestureClassificationData training() {
    if (saveAllTrainingssets) {
      preClassData.setTrainingsset(trainingsset);
    }
    trainingFromDistanceMatrix();
    trainingEveryGesture();
    trainingMinMaxDataForClassification();
    return preClassData;
  }

  protected void trainingMinMaxDataForClassification() {
    int minAvg = Integer.MAX_VALUE;
    int maxAvg = Integer.MIN_VALUE;
    for (Map.Entry<Gesture, List<List<SensorMotionData>>> entryToGesture :
        hcaCentroidSortedTrainingssetByGesture.entries()) {
      int sum = 0;
      List<List<SensorMotionData>> tmpTrainingsset = entryToGesture.getValue();
      for (List<SensorMotionData> trainingsdata : tmpTrainingsset) {
        sum += trainingsdata.size();
      }
      int avg = sum / tmpTrainingsset.size();
      minAvg = Math.min(avg, minAvg);
      maxAvg = Math.max(avg, maxAvg);
    }
    percentMinDataForClassification =
        Double.compare(percentMinDataForClassification, -1.0) == 0
            ? DEFAULT_PERCENT_MIN_DATA_FOR_CLASSIFICATION
            : percentMinDataForClassification;
    percentMaxDataForClassification =
        Double.compare(percentMaxDataForClassification, -1.0) == 0
            ? DEFAULT_PERCENT_MAX_DATA_FOR_CLASSIFICATION
            : percentMaxDataForClassification;
    final int maxData4Class = (int) (maxAvg - maxAvg * percentMaxDataForClassification);
    final int minData4Class = (int) (minAvg - minAvg * percentMinDataForClassification);
    preClassData.setMinSensordata4Classification(minData4Class);
    preClassData.setMaxSensordata4Classification(maxData4Class);
    preClassData.setDiffMinMaxSensordata4Classification(Math.abs(maxData4Class - minData4Class));
  }

  protected void trainingFromDistanceMatrix() {
    final Statistics stat = calculateDistancesFromDistanceMatrix(trainingsset);
    preClassData.setMaxTrainingsDistanceFromDistanceMatrix(stat.max);
    preClassData.setMinTrainingsDistanceFromDistanceMatrix(stat.min);
    preClassData.setAvgTrainingsDistanceFromDistnaceMatrix(stat.avg);
  }

  protected void trainingEveryGesture() {
    sortedTrainingssetByGesture = ClassificationUtility.sortTrainingssetByGesture(trainingsset);
    if (saveAllTrainingssets) {
      preClassData.setTrainingPreClassificationMap(
          calculateForEveryGesture(sortedTrainingssetByGesture));
    }
    final HCA hca = new HCA(sortedTrainingssetByGesture);
    hcaCentroidSortedTrainingssetByGesture = hca.getCentroidsForGestures();
    preClassData.setHcaCentroidgesturePreClassificationMap(
        calculateForEveryGesture(hcaCentroidSortedTrainingssetByGesture));
  }

  private Map<Gesture, PreGestureClassificationDataForGesture> calculateForEveryGesture(
      final Map<Gesture, List<List<SensorMotionData>>> sortedMap) {
    Map<Gesture, PreGestureClassificationDataForGesture> preClassificationMap =
        new EnumMap<>(Gesture.class);
    for (Map.Entry<Gesture, List<List<SensorMotionData>>> entryToGesture : sortedMap.entrySet()) {
      final Gesture gesture = entryToGesture.getKey();
      preClassificationMap.put(
          gesture, getPreGestureClassificationDataForGesture(gesture, entryToGesture.getValue()));
    }
    return preClassificationMap;
  }

  private ListMultimap<Gesture, PreGestureClassificationDataForGesture> calculateForEveryGesture(
      final ListMultimap<Gesture, List<List<SensorMotionData>>> sortedMap) {
    ListMultimap<Gesture, PreGestureClassificationDataForGesture> preClassificationMap =
        MultimapBuilder.treeKeys().arrayListValues().build();
    for (Map.Entry<Gesture, List<List<SensorMotionData>>> entryToGesture : sortedMap.entries()) {
      final Gesture gesture = entryToGesture.getKey();
      preClassificationMap.put(
          gesture, getPreGestureClassificationDataForGesture(gesture, entryToGesture.getValue()));
    }
    return preClassificationMap;
  }

  private PreGestureClassificationDataForGesture getPreGestureClassificationDataForGesture(
      Gesture gesture, List<List<SensorMotionData>> trainingssetToGesture) {
    final PreGestureClassificationDataForGesture classifyData4Gesture =
        new PreGestureClassificationDataForGesture();
    classifyData4Gesture.setGesture(gesture);
    classifyData4Gesture.setTrainingsSetForGesture(trainingssetToGesture);
    classifyData4Gesture.setAvgTrainingsSequence(DBA.average(trainingssetToGesture));
    calculateDistancesFromCentroid(classifyData4Gesture);
    calculateDistancesFromDistanceMatrix(classifyData4Gesture);
    if (!saveAllTrainingssets) {
      // not save the trainingsset to the sd card!
      classifyData4Gesture.setTrainingsSetForGesture(null);
    }
    return classifyData4Gesture;
  }

  private void calculateDistancesFromCentroid(
      final PreGestureClassificationDataForGesture classifyData4Gesture) {
    final List<List<SensorMotionData>> trainingsSet =
        classifyData4Gesture.getTrainingsSetForGesture();
    final List<SensorMotionData> avgSeq = classifyData4Gesture.getAvgTrainingsSequence();
    final Statistics stat = calculateDistancesFromCentroid(trainingsSet, avgSeq);
    classifyData4Gesture.setAvgTrainingsDistanceFromCentroid(stat.avg);
    classifyData4Gesture.setMaxTrainingsDistanceFromCentroid(stat.max);
    classifyData4Gesture.setMinTrainingsDistanceFromCentroid(stat.min);
    if (saveAllTrainingssets) {
      classifyData4Gesture.setTrainingsSubSetForGesture(stat.subset);
    }
  }

  private void calculateDistancesFromDistanceMatrix(
      final PreGestureClassificationDataForGesture classifyData4Gesture) {
    final Statistics stat =
        calculateDistancesFromDistanceMatrix(classifyData4Gesture.getTrainingsSetForGesture());
    classifyData4Gesture.setAvgTrainingsDistanceFromDistanceMatrix(stat.avg);
    classifyData4Gesture.setMaxTrainingsDistanceFromDistanceMatrix(stat.max);
    classifyData4Gesture.setMinTrainingsDistanceFromDistanceMatrix(stat.min);
  }

  private Statistics calculateDistancesFromCentroid(
      final List<List<SensorMotionData>> trainingsSet, final List<SensorMotionData> avgSeq) {
    double max = Double.MIN_VALUE;
    double min = Double.MAX_VALUE;
    final double[] distances = new double[trainingsSet.size()];
    for (int i = 0; i < trainingsSet.size(); i++) {
      final double distance = Dtw.getDistanceWithNewTime(avgSeq, trainingsSet.get(i));
      distances[i] = distance;
      max = Math.max(max, distance);
      min = Math.min(min, distance);
    }
    List<List<SensorMotionData>> subset = calculateSubset(distances);
    double avg = 0.0;
    for (int i = 0; i < distances.length; i++) {
      avg += distances[i];
    }
    avg /= (double) distances.length;
    Statistics stat = new Statistics();
    stat.max = max;
    stat.min = min;
    stat.avg = avg;
    stat.subset = subset;
    return stat;
  }

  private List<List<SensorMotionData>> calculateSubset(double[] distances) {
    final List<List<SensorMotionData>> subset = new LinkedList<>();
    final ListMultimap<Double, List<SensorMotionData>> distanceTrainingsset =
        MultimapBuilder.treeKeys().arrayListValues().build();
    for (int i = 0; i < distances.length; i++) {
      distanceTrainingsset.put(distances[i], trainingsset.get(i));
    }
    int k = (int) Math.ceil(distances.length * percentForSubset);
    int i = 0;
    for (Map.Entry<Double, List<SensorMotionData>> entry : distanceTrainingsset.entries()) {
      if (i == k) {
        break;
      }
      subset.add(entry.getValue());
      i++;
    }
    return subset;
  }

  private Statistics calculateDistancesFromDistanceMatrix(final List<List<SensorMotionData>> set) {
    double maxDistanceTraining = Double.MIN_VALUE;
    double minDistanceTraining = Double.MAX_VALUE;
    double sum = 0.0;
    int count = 0;
    for (int i = 0; i < set.size(); i++) {
      for (int j = 0; j < set.size(); j++) {
        if (i != j) {
          double distance = Dtw.getDistanceWithNewTime(set.get(i), set.get(j));
          maxDistanceTraining = Math.max(maxDistanceTraining, distance);
          minDistanceTraining = Math.min(minDistanceTraining, distance);
          sum += distance;
          count++;
        }
      }
    }
    Statistics stat = new Statistics();
    stat.max = maxDistanceTraining;
    stat.min = minDistanceTraining;
    stat.avg = count == 0 ? 0 : sum / count;
    return stat;
  }

  /** @return the min data for classification as percent */
  public double getPercentMinDataForClassification() {
    return percentMinDataForClassification;
  }

  /**
   * set the min Data for classification as percent
   *
   * @param percentMinDataForClassification
   */
  public void setPercentMinDataForClassification(double percentMinDataForClassification) {
    this.percentMinDataForClassification = percentMinDataForClassification;
  }

  /** @return the max data for classification as percent */
  public double getPercentMaxDataForClassification() {
    return percentMaxDataForClassification;
  }

  /**
   * set the max Data for classification as percent
   *
   * @param percentMaxDataForClassification
   */
  public void setPercentMaxDataForClassification(double percentMaxDataForClassification) {
    this.percentMaxDataForClassification = percentMaxDataForClassification;
  }

  private class Statistics {
    /** the max */
    public double max;
    /** the min */
    public double min;
    /** the avg */
    public double avg;
    /** the subset */
    public List<List<SensorMotionData>> subset;
  }
}

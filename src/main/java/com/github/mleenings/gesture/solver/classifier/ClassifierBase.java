package com.github.mleenings.gesture.solver.classifier;

import com.github.mleenings.gesture.solver.data.objects.sensor.*;

import java.util.List;
import java.util.Map;

/** Basic functions for Classifier */
public abstract class ClassifierBase {

  protected PreGestureClassificationData preGestureClassificationData;
  protected List<List<SensorMotionData>> trainingsdata;
  protected double maxDistanceTraining;
  protected double minDistanceTraining;

  /** constructor */
  public ClassifierBase() {
    // default constructor
  }

  /**
   * constructor
   *
   * @param preGestureClassificationData
   */
  public ClassifierBase(PreGestureClassificationData preGestureClassificationData) {
    setPreGestureClassificationData(preGestureClassificationData);
  }

  /**
   * set the pre classification data
   *
   * @param preGestureClassificationData
   */
  public void setPreGestureClassificationData(
      PreGestureClassificationData preGestureClassificationData) {
    this.preGestureClassificationData = preGestureClassificationData;
    this.trainingsdata = preGestureClassificationData.getTrainingsset();
    this.maxDistanceTraining =
        preGestureClassificationData.getMaxTrainingsDistanceFromDistanceMatrix();
    this.minDistanceTraining =
        preGestureClassificationData.getMinTrainingsDistanceFromDistanceMatrix();
  }

  protected GestureInfo classify(List<SensorMotionData> actualData) {
    return classify(actualData, 0.0);
  }

  /**
   * classified the data
   *
   * @param actualData
   * @param range
   * @return
   */
  public abstract GestureInfo classify(List<SensorMotionData> actualData, double range);

  protected boolean isDistanceInRange(double distance, double range) {
    for (Map.Entry<Gesture, PreGestureClassificationDataForGesture> entry :
        preGestureClassificationData.getHcaCentroidgesturePreClassificationMap().entries()) {
      final double avgDistFromCentroid = entry.getValue().getAvgTrainingsDistanceFromCentroid();
      if (distance <= avgDistFromCentroid + range * avgDistFromCentroid) {
        return true;
      }
    }
    return false;
  }

  protected boolean isDistanceInRange(double distance, double maxDistance, double range) {
    return (distance <= maxRange(maxDistance, range));
  }

  protected double maxRange(double maxDistance, double range) {
    return maxDistance + range * maxDistance;
  }

  /** @return the trainingsset */
  public List<List<SensorMotionData>> getTrainingsdata() {
    return trainingsdata;
  }

  /**
   * set the trainingsset
   *
   * @param trainingsdata
   */
  public void setTrainingsdata(List<List<SensorMotionData>> trainingsdata) {
    this.trainingsdata = trainingsdata;
  }
}

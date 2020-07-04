package com.github.mleenings.gesture.solver.data.objects.sensor;

import java.io.Serializable;
import java.util.List;

public class PreGestureClassificationDataForGesture implements Serializable {
    private Gesture gesture;
    private List<List<SensorMotionData>> trainingsSetForGesture;
    private List<List<SensorMotionData>> trainingsSubSetForGesture;
    private double maxTrainingsDistanceFromCentroid;
    private double minTrainingsDistanceFromCentroid;
    private double avgTrainingsDistanceFromCentroid;
    private double maxTrainingsDistanceFromDistanceMatrix;
    private double minTrainingsDistanceFromDistanceMatrix;
    private double avgTrainingsDistanceFromDistanceMatrix;
    private List<SensorMotionData> avgTrainingsSequence;

    /**
     *
     * @return the maximum trainings distance to centroid
     */
    public double getMaxTrainingsDistanceFromCentroid() {
        return maxTrainingsDistanceFromCentroid;
    }

    /**
     * set the max trainings distance to centroid
     * @param maxTrainingsDistanceFromCentroid
     */
    public void setMaxTrainingsDistanceFromCentroid(double maxTrainingsDistanceFromCentroid) {
        this.maxTrainingsDistanceFromCentroid = maxTrainingsDistanceFromCentroid;
    }

    /**
     *
     * @return the min trainings distance to centroid
     */
    public double getMinTrainingsDistanceFromCentroid() {
        return minTrainingsDistanceFromCentroid;
    }

    /**
     * set the min trainings distance to centroid
     * @param minTrainingsDistanceFromCentroid
     */
    public void setMinTrainingsDistanceFromCentroid(double minTrainingsDistanceFromCentroid) {
        this.minTrainingsDistanceFromCentroid = minTrainingsDistanceFromCentroid;
    }

    /**
     *
     * @return the average trainings distance to centroid
     */
    public double getAvgTrainingsDistanceFromCentroid() {
        return avgTrainingsDistanceFromCentroid;
    }

    /**
     * set the average trainings distance to centroid
     * @param avgTrainingsDistanceFromCentroid
     */
    public void setAvgTrainingsDistanceFromCentroid(double avgTrainingsDistanceFromCentroid) {
        this.avgTrainingsDistanceFromCentroid = avgTrainingsDistanceFromCentroid;
    }

    /**
     *
     * @return the average trainingssequence
     */
    public List<SensorMotionData> getAvgTrainingsSequence() {
        return avgTrainingsSequence;
    }

    /**
     * set the average trainingssequence
     * @param avgTrainingsSequence
     */
    public void setAvgTrainingsSequence(List<SensorMotionData> avgTrainingsSequence) {
        this.avgTrainingsSequence = avgTrainingsSequence;
    }

    /**
     *
     * @return the gesture
     */
    public Gesture getGesture() {
        return gesture;
    }

    /**
     * set the gesture
     * @param gesture
     */
    public void setGesture(Gesture gesture) {
        this.gesture = gesture;
    }

    /**
     *
     * @return the trainingsset for the member gesture
     */
    public List<List<SensorMotionData>> getTrainingsSetForGesture() {
        return trainingsSetForGesture;
    }

    /**
     * set the trainingsset for the member gesture
     * @param trainingsSetForGesture
     */
    public void setTrainingsSetForGesture(List<List<SensorMotionData>> trainingsSetForGesture) {
        this.trainingsSetForGesture = trainingsSetForGesture;
    }

    /**
     *
     * @return the max trainings distance from distance matrix
     */
    public double getMaxTrainingsDistanceFromDistanceMatrix() {
        return maxTrainingsDistanceFromDistanceMatrix;
    }

    /**
     * set the max trainings distance from distance matrix
     * @param maxTrainingsDistanceFromDistanceMatrix
     */
    public void setMaxTrainingsDistanceFromDistanceMatrix(double maxTrainingsDistanceFromDistanceMatrix) {
        this.maxTrainingsDistanceFromDistanceMatrix = maxTrainingsDistanceFromDistanceMatrix;
    }

    /**
     *
     * @return the min trainings distance from distance matrix
     */
    public double getMinTrainingsDistanceFromDistanceMatrix() {
        return minTrainingsDistanceFromDistanceMatrix;
    }

    /**
     * set the min trainings distance from distance matrix
     * @param minTrainingsDistanceFromDistanceMatrix
     */
    public void setMinTrainingsDistanceFromDistanceMatrix(double minTrainingsDistanceFromDistanceMatrix) {
        this.minTrainingsDistanceFromDistanceMatrix = minTrainingsDistanceFromDistanceMatrix;
    }

    /**
     *
     * @return the average trainings distance from distance matrix
     */
    public double getAvgTrainingsDistanceFromDistanceMatrix() {
        return avgTrainingsDistanceFromDistanceMatrix;
    }

    /**
     * set the average trainings distance from distance matrix
     * @param avgTrainingsDistanceFromDistanceMatrix
     */
    public void setAvgTrainingsDistanceFromDistanceMatrix(double avgTrainingsDistanceFromDistanceMatrix) {
        this.avgTrainingsDistanceFromDistanceMatrix = avgTrainingsDistanceFromDistanceMatrix;
    }

    /**
     *
     * @return the trainings subset
     */
    public List<List<SensorMotionData>> getTrainingsSubSetForGesture() {
        return trainingsSubSetForGesture;
    }

    /**
     * set the trainings subset
     * @param trainingsSubSetForGesture
     */
    public void setTrainingsSubSetForGesture(List<List<SensorMotionData>> trainingsSubSetForGesture) {
        this.trainingsSubSetForGesture = trainingsSubSetForGesture;
    }
}

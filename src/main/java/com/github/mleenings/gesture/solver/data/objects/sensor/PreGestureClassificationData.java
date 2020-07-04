package com.github.mleenings.gesture.solver.data.objects.sensor;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PreGestureClassificationData implements Serializable {
    private double maxTrainingsDistanceFromDistanceMatrix;
    private double minTrainingsDistanceFromDistanceMatrix;
    private double avgTrainingsDistanceFromDistnaceMatrix;
    private int maxSensordata4Classification;
    private int minSensordata4Classification;
    private int diffMinMaxSensordata4Classification;
    private Map<Gesture,PreGestureClassificationDataForGesture> trainingPreClassificationMap = new HashMap<>();
    private ListMultimap<Gesture, PreGestureClassificationDataForGesture> hcaCentroidgesturePreClassificationMap = MultimapBuilder.treeKeys().arrayListValues().build();
    private List<List<SensorMotionData>> trainingsset = new LinkedList<>();
    private List<List<SensorMotionData>> trainingsSubSet = new LinkedList<>();

    /**
     *
     * @return the training pre classification map
     */
    public Map<Gesture, PreGestureClassificationDataForGesture> getTrainingPreClassificationMap() {
        return trainingPreClassificationMap;
    }

    /**
     * set the training pre classification map
     * @param gesturePreClassification
     */
    public void setTrainingPreClassificationMap(Map<Gesture, PreGestureClassificationDataForGesture> gesturePreClassification) {
        this.trainingPreClassificationMap = gesturePreClassification;
    }

    /**
     *
     * @return the max trainingsdistance from distancematrix
     */
    public double getMaxTrainingsDistanceFromDistanceMatrix() {
        return maxTrainingsDistanceFromDistanceMatrix;
    }

    /**
     * set the max trainingsdistance from distancematrix
     * @param maxTrainingsDistanceFromDistanceMatrix
     */
    public void setMaxTrainingsDistanceFromDistanceMatrix(double maxTrainingsDistanceFromDistanceMatrix) {
        this.maxTrainingsDistanceFromDistanceMatrix = maxTrainingsDistanceFromDistanceMatrix;
    }

    /**
     *
     * @return the min trainingsdistance from distancematrix
     */
    public double getMinTrainingsDistanceFromDistanceMatrix() {
        return minTrainingsDistanceFromDistanceMatrix;
    }

    /**
     * set the min trainingsdistance from distancematrix
     * @param minTrainingsDistanceFromDistanceMatrix
     */
    public void setMinTrainingsDistanceFromDistanceMatrix(double minTrainingsDistanceFromDistanceMatrix) {
        this.minTrainingsDistanceFromDistanceMatrix = minTrainingsDistanceFromDistanceMatrix;
    }

    /**
     *
     * @return the average trainingsdistance from distancematrix
     */
    public double getAvgTrainingsDistanceFromDistnaceMatrix() {
        return avgTrainingsDistanceFromDistnaceMatrix;
    }

    /**
     * the average trainingsdistance from distancematrix
     * @param avgTrainingsDistanceFromDistnaceMatrix
     */
    public void setAvgTrainingsDistanceFromDistnaceMatrix(double avgTrainingsDistanceFromDistnaceMatrix) {
        this.avgTrainingsDistanceFromDistnaceMatrix = avgTrainingsDistanceFromDistnaceMatrix;
    }

    /**
     *
     * @return the trainingsset
     */
    public List<List<SensorMotionData>> getTrainingsset() {
        return trainingsset;
    }

    /**
     * set the trainingsset
     * @param trainingsset
     */
    public void setTrainingsset(List<List<SensorMotionData>> trainingsset) {
        this.trainingsset = trainingsset;
    }

    /**
     *
     * @return the trainingssubset
     */
    public List<List<SensorMotionData>> getTrainingsSubSet(){
        trainingsSubSet.clear();
        for(Map.Entry<Gesture, PreGestureClassificationDataForGesture> entry : trainingPreClassificationMap.entrySet()){
            trainingsSubSet.addAll(entry.getValue().getTrainingsSubSetForGesture());
        }
        return trainingsSubSet;
    }

    /**
     *
     * @return the hca centroid preclassification map
     */
    public ListMultimap<Gesture, PreGestureClassificationDataForGesture> getHcaCentroidgesturePreClassificationMap() {
        return hcaCentroidgesturePreClassificationMap;
    }

    /**
     * set the hca centroid preclassification map
     * @param hcaCentroidgesturePreClassificationMap
     */
    public void setHcaCentroidgesturePreClassificationMap(ListMultimap<Gesture, PreGestureClassificationDataForGesture> hcaCentroidgesturePreClassificationMap) {
        this.hcaCentroidgesturePreClassificationMap = hcaCentroidgesturePreClassificationMap;
    }

    /**
     *
     * @return the max number of sensordata for classification
     */
    public int getMaxSensordata4Classification() {
        return maxSensordata4Classification;
    }

    /**
     * set the max number of sensordata for classification
     * @param maxSensordata4Classification
     */
    public void setMaxSensordata4Classification(int maxSensordata4Classification) {
        this.maxSensordata4Classification = maxSensordata4Classification;
    }

    /**
     *
     * @return the min number of sensordata for classification
     */
    public int getMinSensordata4Classification() {
        return minSensordata4Classification;
    }

    /**
     * set the min number of sensordata for classification
     * @param minSensordata4Classification
     */
    public void setMinSensordata4Classification(int minSensordata4Classification) {
        this.minSensordata4Classification = minSensordata4Classification;
    }

    /**
     *
     * @return the difference of max and min number of sensordata for classification
     */
    public int getDiffMinMaxSensordata4Classification() {
        return diffMinMaxSensordata4Classification;
    }

    /**
     * set the difference of max and min number of sensordata for classification
     * @param diffMinMaxSensordata4Classification
     */
    public void setDiffMinMaxSensordata4Classification(int diffMinMaxSensordata4Classification) {
        this.diffMinMaxSensordata4Classification = diffMinMaxSensordata4Classification;
    }
}

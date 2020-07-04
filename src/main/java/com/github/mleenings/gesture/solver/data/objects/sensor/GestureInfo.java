package com.github.mleenings.gesture.solver.data.objects.sensor;

public class GestureInfo {
    private Gesture gesture;
    private double probability;

    /**
     * constructor
     * @param gesture
     * @param probability
     */
    public GestureInfo (final Gesture gesture, final double probability){
        this.gesture = gesture;
        this.probability = probability;
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
     * @return the probability
     */
    public double getProbability() {
        return probability;
    }

    /**
     * set the probability
     * @param probability
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "GestureInfo{" +
                "gesture=" + gesture +
                ", probability=" + probability +
                '}';
    }
}

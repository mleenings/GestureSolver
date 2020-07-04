package com.github.mleenings.gesture.solver.data.objects.sensor;

/**
 * Represent Statistics for the SensorData
 * Created by marcel on 10/20/16.
 */
public class StatisticSensorData {
    private float[] min;
    private float[] max;
    private float[] expectedValue;

    /**
     * constructor
     * @param min
     * @param max
     * @param expectedValue
     */
    public StatisticSensorData(float[] min, float[] max, float[] expectedValue){
        this.min = min;
        this.max = max;
        this.expectedValue = expectedValue;
    }

    /**
     * default constructor
     */
    public StatisticSensorData(){
        //default
    }

    /**
     *
     * @return the minimum
     */
    public float[] getMin() {
        return min;
    }

    /**
     * set the minimum
     * @param min
     */
    public void setMin(float[] min) {
        this.min = min;
    }

    /**
     *
     * @return the maximum
     */
    public float[] getMax() {
        return max;
    }

    /**
     * set the maximum
     * @param max
     */
    public void setMax(float[] max) {
        this.max = max;
    }

    /**
     *
     * @return the expacted value
     */
    public float[] getExpectedValue() {
        return expectedValue;
    }

    /**
     * set the expected value
     * @param expectedValue
     */
    public void setExpectedValue(float[] expectedValue) {
        this.expectedValue = expectedValue;
    }
}

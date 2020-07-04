package com.github.mleenings.gesture.solver.data.objects.sensor;

import java.io.Serializable;

public class SensorData implements Serializable {
    private long eventTime;
    private float[] values = new float[3];

    /**
     * default constructor
     */
    public SensorData(){
        // default constructor
    }

    /**
     * constructor
     * @param data
     * @param eventTime
     */
    public SensorData(float[] data, long eventTime){
        setValues(data);
        setEventTime(eventTime);
    }

    /**
     * Copy constructor
     * @param sensorData
     */
    public SensorData(SensorData sensorData){
        System.arraycopy(sensorData.getValues(), 0, this.values, 0, sensorData.getValues().length);
        eventTime=sensorData.getEventTime();
    }

    /**
     * set the values
     * @param sensorData
     */
    public void setValues(float[] sensorData){
        this.values = sensorData;
    }

    /**
     *
     * @return the values
     */
    public float[] getValues(){
        float[] copy = new float[3];
        System.arraycopy(values, 0, copy, 0, values.length);
        return copy;
    }

    /**
     *
     * @return the event timestamp
     */
    public long getEventTime() {
        return eventTime;
    }

    /**
     * set the event timestamp
     * @param eventTime
     */
    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return String.format("[%.4f,%.4f,%.4f]",values[0], values[1], values[2]);
    }
}

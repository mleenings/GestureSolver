package com.github.mleenings.gesture.solver.data.objects.sensor;

import java.io.Serializable;
/**
 * Represent a set of sensor motion datas
 */
public class SensorMotionData implements Serializable {
    /** only for simple test purpose */
    private boolean withVelo = false;
    /** only for simple test purpose */
    private boolean withL2norm = false;
    private float l2norm;

    private Gesture gesture;
    private SensorData accelerometerData;
    private SensorData velocityData;
    private SensorData gyroscopeData;
    private SensorData magneticFieldData;
    private SensorData orientationData;
    private boolean isStep = false;

    /**
     * default constructor
     */
    public SensorMotionData(){
        // default constructor
    }

    /**
     * copy constructor
     * @param sensorMotionData
     */
    public SensorMotionData(SensorMotionData sensorMotionData){
        accelerometerData = sensorMotionData.getAccelerometerData();
        velocityData = sensorMotionData.getVelocityData();
        gyroscopeData = sensorMotionData.getGyroscopeData();
        magneticFieldData = sensorMotionData.getMagneticFieldData();
        orientationData = sensorMotionData.getOrientationData();
        gesture = sensorMotionData.getGesture();
        isStep = sensorMotionData.isStep();
        l2norm = sensorMotionData.getL2norm();
    }

    /**
     * constructor to initiate only with a vector
     * @param vector
     */
    public SensorMotionData(float[] vector){
        setVector(vector);
    }

    /**
     * constructor to initiate with vector and timestamp
     * @param vector
     * @param timestamp
     */
    public SensorMotionData(float[] vector, long timestamp){
        setVector(vector);
        accelerometerData.setEventTime(timestamp);
        if(withVelo) {
            velocityData.setEventTime(timestamp);
        }
        gyroscopeData.setEventTime(timestamp);
        if(orientationData == null){
            orientationData = new SensorData();
        }
        orientationData.setEventTime(timestamp);
    }

    /**
     * returns the magnetic field
     * @return
     */
    public SensorData getMagneticFieldData() {
        return magneticFieldData;
    }

    /**
     * sets the magnetic field
     * @param magneticFieldData
     */
    public void setMagneticFieldData(SensorData magneticFieldData) {
        this.magneticFieldData = magneticFieldData;
    }

    /**
     * return the accelerometer data
     * @return
     */
    public SensorData getAccelerometerData() {
        return accelerometerData;
    }

    /**
     * sets the accelerometer data
     * @param accelerometerData
     */
    public void setAccelerometerData(SensorData accelerometerData) {
        this.accelerometerData = accelerometerData;
    }

    /**
     * returns the gyroscope data
     * @return
     */
    public SensorData getGyroscopeData() {
        return gyroscopeData;
    }

    /**
     * sets the gyroscope data
     * @param gyroscopeData
     */
    public void setGyroscopeData(SensorData gyroscopeData) {
        this.gyroscopeData = gyroscopeData;
    }

    /**
     * returns the gyroscope data
     * @return
     */
    public SensorData getVelocityData() {
        return velocityData;
    }

    /**
     * sets the gyroscope data
     * @param velocityData
     */
    public void setVelocityData(SensorData velocityData) {
        this.velocityData = velocityData;
    }

    /**
     * returns the orientation data
     * @return
     */
    public SensorData getOrientationData() {
        return orientationData;
    }

    /**
     * sets the orientation data
     * @param orientationData
     */
    public void setOrientationData(SensorData orientationData) {
        this.orientationData = orientationData;
    }

    /**
     * returns true if accelerometer and gyroscope and magnetic field data is set
     * @return
     */
    public boolean areAllSensorDataSet(){
        return accelerometerData!=null && gyroscopeData!=null  && magneticFieldData != null;
    }

    /**
     * @return true if with velo
     */
    public boolean isWithVelo() {
        return withVelo;
    }

    /**
     * set if with velo
     * @param withVelo
     */
    public void setWithVelo(boolean withVelo) {
        this.withVelo = withVelo;
    }

    /**
     *
     * @return true if l2-norm is set
     */
    public boolean isWithL2norm() {
        return withL2norm;
    }

    /**
     * set if with l2-norm
     * @param withL2norm
     */
    public void setWithL2norm(boolean withL2norm) {
        this.withL2norm = withL2norm;
    }

    /**
     *
     * @return the l2-norm
     */
    public float getL2norm() {
        return l2norm;
    }

    /**
     * set the l2-norm
     * @param l2norm
     */
    public void setL2norm(float l2norm) {
        this.l2norm = l2norm;
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
     * @return is a step detected
     */
    public boolean isStep() {
        return isStep;
    }

    /**
     * set is step detected
     * @param step
     */
    public void setStep(boolean step) {
        isStep = step;
    }



    /*
     * WIP: getVector() and setVector()
     * for simple test purpose
     */

    /**
     * @return the vector to calculate with DTW
     */
    public float[] getVector(){
        final int dim = withVelo?9:6;
        float[] v = new float[dim];
        if(accelerometerData == null){
            v[0] = 0;
            v[1] = 0;
            v[2] = 0;
        }else if(gyroscopeData == null){
            v[3] = 0;
            v[4] = 0;
            v[5] = 0;
        }else if(withVelo && velocityData == null){
            v[6] = 0;
            v[7] = 0;
            v[8] = 0;
        }else {
            float[] vAccel = accelerometerData.getValues();
            float[] vgyro = gyroscopeData.getValues();
            v[0] = vAccel[0];
            v[1] = vAccel[1];
            v[2] = vAccel[2];
            v[3] = vgyro[0];
            v[4] = vgyro[1];
            v[5] = vgyro[2];
            if(withVelo) {
                float[] vVelo = velocityData.getValues();
                v[6] = vVelo[0];
                v[7] = vVelo[1];
                v[8] = vVelo[2];
            }
        }
        if(withL2norm){
            float[] v2 = new float[v.length+1];
            System.arraycopy(v,0,v2,0,v.length);
            l2norm = calcL2norm(v);
            v2[v2.length-1] = l2norm;
            return v2;
        }else{
            return v;
        }
    }

    private float calcL2norm(final float[] v){
        float l2norm = 0;
        for(int i = 0; i<v.length; i++){
            l2norm += v[i]*v[i];
        }
        l2norm = (float) Math.sqrt((double)l2norm);
        return l2norm;
    }

    /**
     *
     * @return the vector for DTW calculation as double array
     */
    public double[] getDoubleVector(){
        float[] vFloat = getVector();
        double[] v = new double[vFloat.length];
        for(int i = 0; i<vFloat.length;i++){
            v[i] = vFloat[i];
        }
        return v;
    }

    /**
     * set the vector for DTW calculation as double array
     * @param v
     */
    public void setDoubleVector(double[] v){
        float[] vFloat = new float[v.length];
        for(int i = 0; i<v.length;i++){
            vFloat[i] = (float) v[i];
        }
        setVector(vFloat);
    }

    /**
     * set the vector for DTW calculation
     * @param v
     */
    public void setVector(float[] v){
        if(withL2norm){
            if(v.length % 3 ==0){
                float[] v2 = new float[v.length-1];
                System.arraycopy(v,0,v2,0,v.length-1);
                l2norm = calcL2norm(v2);
            }else{
                l2norm = calcL2norm(v);
            }
        }
        float[] vAccel = new float[3];
        float[] vgyro = new float[3];
        float[] vVelo = new float[3];
        vAccel[0] = v[0];
        vAccel[1] = v[1];
        vAccel[2] = v[2];
        vgyro[0] = v[3];
        vgyro[1] = v[4];
        vgyro[2] = v[5];
        if (accelerometerData == null) {
            setAccelerometerData(new SensorData());
        }
        if (gyroscopeData == null) {
            setGyroscopeData(new SensorData());
        }
        accelerometerData.setValues(vAccel);
        gyroscopeData.setValues(vgyro);
        if (withVelo) {
            vVelo[0] = v[6];
            vVelo[1] = v[7];
            vVelo[2] = v[8];
            if (velocityData == null) {
                setVelocityData(new SensorData());
            }
            velocityData.setValues(vVelo);
        }
    }
}

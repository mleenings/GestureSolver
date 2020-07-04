package com.github.mleenings.gesture.solver.data.objects.sensor;

/**
 * Represent the Calibration Data
 * Created by marcel on 10/12/16.
 */
public class CalibrationData {

    private StatisticSensorData accelerometerStatistic;
    private StatisticSensorData gyroscopeStatistic;

    /**
     * constructor
     * @param accelerometerStatistic
     * @param gyroscopeStatistic
     */
    public CalibrationData(StatisticSensorData accelerometerStatistic, StatisticSensorData gyroscopeStatistic){
        this.accelerometerStatistic=accelerometerStatistic;
        this.gyroscopeStatistic = gyroscopeStatistic;
    }

    /**
     * default constructor
     */
    public CalibrationData(){
        //default
    }

    /**
     *
     * @return the accelerometer statistics
     */
    public StatisticSensorData getAccelerometerStatistic() {
        return accelerometerStatistic;
    }

    /**
     * sets the accelereometer statistics
     * @param accelerometerStatistic
     */
    public void setAccelerometerStatistic(StatisticSensorData accelerometerStatistic) {
        this.accelerometerStatistic = accelerometerStatistic;
    }

    /**
     *
     * @return the gyroscope statistics
     */
    public StatisticSensorData getGyroscopeStatistic() {
        return gyroscopeStatistic;
    }

    /**
     * stes the gyrocsope statistics
     * @param gyroscopeStatistic
     */
    public void setGyroscopeStatistic(StatisticSensorData gyroscopeStatistic) {
        this.gyroscopeStatistic = gyroscopeStatistic;
    }
}

package com.github.mleenings.gesture.solver;

import com.github.mleenings.gesture.solver.data.objects.sensor.CalibrationData;
import com.github.mleenings.gesture.solver.data.objects.sensor.Gesture;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import com.github.mleenings.gesture.solver.gesturizer.GestureSensorEventListener;
import com.github.mleenings.gesture.solver.hardware.Sensor;
import com.github.mleenings.gesture.solver.hardware.SensorManager;

/** The motion sensor scanner */
public class MotionSensorScannner {
  /**
   * SENSOR_DELAY_NORMAL 200,000 microsecond delay; SENSOR_DELAY_UI 60,000 microsecond delay;
   * SENSOR_DELAY_GAME 20,000 microsecond delay; SENSOR_DELAY_FASTEST 0 microsecond delay
   */
  public static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_UI;

  private SensorManager sensorManager;
  private SensorMotionData sensorMotion;
  private boolean shouldScanning = false;
  private GestureSensorEventListener mySensorEventListener;

  /**
   * constructor
   *
   * @param sensorManager
   */
  public MotionSensorScannner(SensorManager sensorManager) {
    this(null, sensorManager);
  }

  /**
   * constructor
   *
   * @param gesture
   * @param sensorManager
   */
  public MotionSensorScannner(Gesture gesture, SensorManager sensorManager) {
    this.sensorManager = sensorManager;
    sensorMotion = new SensorMotionData();
    sensorMotion.setGesture(gesture);
    mySensorEventListener = new GestureSensorEventListener(sensorManager);
    mySensorEventListener.setGesture(gesture);
    mySensorEventListener.setSensorMotion(sensorMotion);
    mySensorEventListener.setShouldScanning(shouldScanning);

    registerSensorTypes();
  }

  /** @return SensorEventListener */
  public GestureSensorEventListener getSensorEventListener() {
    return mySensorEventListener;
  }

  /**
   * set the calibrationData
   *
   * @param calibrationData
   */
  public void setCalibrationData(final CalibrationData calibrationData) {
    mySensorEventListener.setCalibrationData(calibrationData);
  }

  /**
   * set the zeroFilter
   *
   * @param withZeroFilter
   */
  public void setWithZeroFilter(boolean withZeroFilter) {
    mySensorEventListener.setWithZeroFilter(withZeroFilter);
  }

  private void registerSensorTypes() {
    sensorManager.registerListener(
        mySensorEventListener,
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        SENSOR_DELAY);
    sensorManager.registerListener(
        mySensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SENSOR_DELAY);
    sensorManager.registerListener(
        mySensorEventListener,
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
        SENSOR_DELAY);
    sensorManager.registerListener(
        mySensorEventListener,
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
        SENSOR_DELAY);
  }

  /**
   * set the CollectedSensorMotionCallback
   *
   * @param collectedSensorMotionCallback
   */
  public void setCollectedSensorMotionCallback(
      CollectedSensorMotionCallback collectedSensorMotionCallback) {
    mySensorEventListener.setCollectedSensorMotionCallback(collectedSensorMotionCallback);
  }

  /**
   * start the scan
   *
   * @param shouldScanning
   */
  public void scan(boolean shouldScanning) {
    this.shouldScanning = shouldScanning;
    mySensorEventListener.setShouldScanning(shouldScanning);
    if (!shouldScanning) {
      resetAccelCalc();
    }
  }

  /**
   * set the Gesture
   *
   * @param gesture
   */
  public void setGesture(Gesture gesture) {
    mySensorEventListener.setGesture(gesture);
  }

  /** reset the AccelerometerCalculation values */
  public void resetAccelCalc() {
    mySensorEventListener.resetAccelCalc();
  }

  /** interface for CollectedSensorMotionCallback */
  public interface CollectedSensorMotionCallback {
    /**
     * onSensorMotionCollected
     *
     * @param sm
     */
    void onSensorMotionCollected(SensorMotionData sm);
  }
}

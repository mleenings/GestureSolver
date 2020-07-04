package com.github.mleenings.gesture.solver.gesturizer;

import com.github.mleenings.gesture.solver.MotionSensorScannner;
import com.github.mleenings.gesture.solver.data.objects.sensor.CalibrationData;
import com.github.mleenings.gesture.solver.data.objects.sensor.Gesture;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorData;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import com.github.mleenings.gesture.solver.filter.*;
import com.github.mleenings.gesture.solver.hardware.Sensor;
import com.github.mleenings.gesture.solver.hardware.SensorEvent;
import com.github.mleenings.gesture.solver.hardware.SensorEventListener;
import com.github.mleenings.gesture.solver.hardware.SensorManager;
import com.github.mleenings.gesture.solver.util.ObjectCloner;

/** example code for a SensorEventListener */
public class GestureSensorEventListener implements SensorEventListener {
  private boolean shouldScanning;
  private boolean withZeroFilter = true;
  private SensorMotionData sensorMotion;
  private Gesture gesture;
  private ButterworthFilterGyroscope butterworthFilterGyroscope;
  private KalmanFilterAccelerometer kalmanFilterAccelerometer;
  private AcceleromationDerivationCalculation accelCalc = new AcceleromationDerivationCalculation();
  private MagneticLowPassFilter magneticLowPassFilter = new MagneticLowPassFilter();
  private OrientationCalculation orientationCalculation;
  private ZeroFilter zeroFilter;
  private MeanOrientationFilter meanOrientationFilter = new MeanOrientationFilter(3);
  private MotionSensorScannner.CollectedSensorMotionCallback collectedSensorMotionCallback;

  /** constructor */
  public GestureSensorEventListener(SensorManager sensorManager) {
    butterworthFilterGyroscope =
        new ButterworthFilterGyroscope() {
          @Override
          protected int getActualDelay() {
            // TODO meaningfull implementation
            return 1;
          }
        };
    kalmanFilterAccelerometer = new KalmanFilterAccelerometer();
    sensorMotion = new SensorMotionData();
    sensorMotion.setGesture(gesture);
    orientationCalculation = new OrientationCalculation(sensorManager);
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    if (shouldScanning) {
      final SensorData sensorData = new SensorData(sensorEvent.values, sensorEvent.timestamp);
      switch (sensorEvent.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
          sensorMotion.setAccelerometerData(sensorData);
          break;
        case Sensor.TYPE_GYROSCOPE:
          sensorMotion.setGyroscopeData(sensorData);
          break;
        case Sensor.TYPE_MAGNETIC_FIELD:
          sensorMotion.setMagneticFieldData(sensorData);
          break;
        default:
          // do nothing
          break;
      }
      if (sensorMotion.areAllSensorDataSet()) {
        sensorMotion.setGesture(gesture);

        // calc orientation before filter accel!
        sensorMotion = magneticLowPassFilter.filter(sensorMotion);
        final SensorData sdOrientation =
            orientationCalculation.calcOrientation(
                sensorMotion.getAccelerometerData(), sensorMotion.getMagneticFieldData());
        sensorMotion.setOrientationData(sdOrientation);
        sensorMotion = meanOrientationFilter.filter(sensorMotion);

        sensorMotion = kalmanFilterAccelerometer.filter(sensorMotion);
        sensorMotion = butterworthFilterGyroscope.filter(sensorMotion);

        if (withZeroFilter) {
          sensorMotion = zeroFilter.zeroFilter(sensorMotion);
        }

        final SensorMotionData copied = (SensorMotionData) ObjectCloner.deepCopy(sensorMotion);
        collectedSensorMotionCallback.onSensorMotionCollected(copied);
        sensorMotion = new SensorMotionData();
        sensorMotion.setGesture(gesture);
      }
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {
    // not interested
  }

  /** @param shouldScanning */
  public void setShouldScanning(boolean shouldScanning) {
    this.shouldScanning = shouldScanning;
  }

  /**
   * set with zero filter?
   *
   * @param withZeroFilter
   */
  public void setWithZeroFilter(boolean withZeroFilter) {
    this.withZeroFilter = withZeroFilter;
  }

  /**
   * set smd
   *
   * @param sensorMotion
   */
  public void setSensorMotion(SensorMotionData sensorMotion) {
    this.sensorMotion = sensorMotion;
  }

  /**
   * set gesture
   *
   * @param gesture
   */
  public void setGesture(Gesture gesture) {
    this.gesture = gesture;
  }

  /**
   * set callback
   *
   * @param collectedSensorMotionCallback
   */
  public void setCollectedSensorMotionCallback(
      MotionSensorScannner.CollectedSensorMotionCallback collectedSensorMotionCallback) {
    this.collectedSensorMotionCallback = collectedSensorMotionCallback;
  }

  /** reset the AccelerometerCalculation values */
  public void resetAccelCalc() {
    accelCalc.reset();
  }

  /**
   * set the calibrationData
   *
   * @param calibrationData
   */
  public void setCalibrationData(final CalibrationData calibrationData) {
    zeroFilter = new ZeroFilter(calibrationData);
    accelCalc.setCalibrationData(calibrationData);
  }
}

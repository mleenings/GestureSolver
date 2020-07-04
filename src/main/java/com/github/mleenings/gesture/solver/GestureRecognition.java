package com.github.mleenings.gesture.solver;

import com.github.mleenings.gesture.solver.classifier.CentroidClassifier;
import com.github.mleenings.gesture.solver.classifier.Classifier;
import com.github.mleenings.gesture.solver.dao.CalibrationSensorDAO;
import com.github.mleenings.gesture.solver.dao.PreGestureClassificationDataDAO;
import com.github.mleenings.gesture.solver.dao.ProjectDAOException;
import com.github.mleenings.gesture.solver.data.objects.sensor.*;
import com.github.mleenings.gesture.solver.gesturizer.GestureSensorEventListener;
import com.github.mleenings.gesture.solver.hardware.SensorManager;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/** Recognizer for gestures */
public class GestureRecognition extends Observable {
  private static final Logger LOGGER = LoggerFactory.getLogger(GestureRecognition.class);
  private Classifier classifier = new CentroidClassifier();

  private MotionSensorScannner motionSensorScannner;
  private PreGestureClassificationData preGestureClassificationData;
  private int maxSensordataForClassification;
  private int minSensordataForClassification;
  private int diffMinMaxSensordataForClassification;
  private double classifierRange;
  private List<SensorMotionData> actualSensorMotionData = new LinkedList<>();
  private SensorMotionData lastSensorMotionData;

  /**
   * constructor
   *
   * @param sensorManager
   */
  public GestureRecognition(SensorManager sensorManager) {
    this(sensorManager, null);
  }

  /**
   * constructor
   *
   * @param sensorManager
   * @param calibrationData
   */
  public GestureRecognition(SensorManager sensorManager, CalibrationData calibrationData) {
    motionSensorScannner = new MotionSensorScannner(sensorManager);
    motionSensorScannner.setCollectedSensorMotionCallback(new MotionSensorCallback());
    final CalibrationData tmpCalibrationData =
        calibrationData == null ? new CalibrationSensorDAO().loadData() : calibrationData;
    motionSensorScannner.setCalibrationData(tmpCalibrationData);
    classifierRange = classifier.getDefaultRange();
  }

  /** @return SensorEventListener */
  public GestureSensorEventListener getSensorEventListener() {
    if (motionSensorScannner != null) {
      return motionSensorScannner.getSensorEventListener();
    }
    return null;
  }

  /** start the gesture recognition */
  public void start() {
    if (preGestureClassificationData == null) {
      setPreGestureClassificationData(getPreGestureClassificationData());
    }
    motionSensorScannner.scan(true);
    actualSensorMotionData.clear();
  }

  /** stop the gesture recognition */
  public void stop() {
    motionSensorScannner.scan(false);
    motionSensorScannner.resetAccelCalc();
    motionSensorScannner.setCollectedSensorMotionCallback(new MotionSensorCallback());
    lastSensorMotionData = null;
  }

  /**
   * set the classifier
   *
   * @param classifier
   */
  public void setClassifier(Classifier classifier) {
    this.classifier = classifier;
    this.classifier.setPreGestureClassificationData(preGestureClassificationData);
  }

  private PreGestureClassificationData getPreGestureClassificationData() {
    PreGestureClassificationData tmpPreGestureClassificationData = null;
    final PreGestureClassificationDataDAO preGestureClassificationDataDAO =
        new PreGestureClassificationDataDAO();
    try {
      tmpPreGestureClassificationData = preGestureClassificationDataDAO.loadData();
    } catch (ProjectDAOException e) {
      LOGGER.error(e.getMessage(), e);
    }
    if (tmpPreGestureClassificationData == null) {
      throw new GestureRecognitionException("No Training");
    }
    return tmpPreGestureClassificationData;
  }

  /**
   * set the PreGestureClassificationData
   *
   * @param preGestureClassificationData
   */
  public void setPreGestureClassificationData(
      final PreGestureClassificationData preGestureClassificationData) {
    if (preGestureClassificationData == null) {
      this.preGestureClassificationData = getPreGestureClassificationData();
    } else {
      this.preGestureClassificationData = preGestureClassificationData;
    }
    this.classifier.setPreGestureClassificationData(this.preGestureClassificationData);
    maxSensordataForClassification =
        this.preGestureClassificationData.getMaxSensordata4Classification();
    minSensordataForClassification =
        this.preGestureClassificationData.getMinSensordata4Classification();
    diffMinMaxSensordataForClassification =
        this.preGestureClassificationData.getDiffMinMaxSensordata4Classification();
  }

  /** @return the least SensorMotionData */
  public SensorMotionData getLatestSensorMotionData() {
    return lastSensorMotionData;
  }

  private class MotionSensorCallback implements MotionSensorScannner.CollectedSensorMotionCallback {

    private static final int MIN_COUNT_BEFORE_NEW_GESTURE = 3;
    private boolean hasMinDataForClassification = false;
    private boolean hasMaxDataForClassification = false;
    private GestureInfo lastGesture = null;
    private GestureInfo lastRealGesture = null;
    private int countBeforeNewGesture = 0;

    @Override
    public void onSensorMotionCollected(SensorMotionData sm) {
      actualSensorMotionData.add(sm);
      lastSensorMotionData = sm;
      notifyDataAdd();
    }

    private void notifyDataAdd() {
      GestureInfo g = fetchGesture();
      if (g != null) {
        setChanged();
        notifyObservers(g);
      }
    }

    private GestureInfo fetchGesture() {
      GestureInfo realGesture = null;
      if (hasMinDataForClassification
          || actualSensorMotionData.size() >= minSensordataForClassification) {
        realGesture = handleMinDataForClassification();
      }
      realGesture = realGesture == null ? lastRealGesture : realGesture;
      return realGesture;
    }

    private GestureInfo handleMinDataForClassification() {
      GestureInfo realGesture;
      hasMinDataForClassification = true;
      GestureInfo actualGesture = classifier.classify(actualSensorMotionData, classifierRange);
      actualGesture = actualGesture == null ? lastGesture : actualGesture;
      realGesture = detectCorrectGesture(actualGesture);
      realGesture = realGesture == null ? lastRealGesture : realGesture;
      if (realGesture != null && realGesture.getGesture() == Gesture.STAND) {
        motionSensorScannner.resetAccelCalc();
      }
      if (realGesture != null && realGesture.getGesture() != Gesture.UNKNOWN) {
        actualSensorMotionData.clear();
        hasMinDataForClassification = false;
      }
      if (hasMaxDataForClassification
          || actualSensorMotionData.size() == maxSensordataForClassification) {
        handleMaxClassificationSize();
      }
      lastGesture = actualGesture;
      return realGesture;
    }

    private void handleMaxClassificationSize() {
      hasMaxDataForClassification = true;
      if (actualSensorMotionData.size() > minSensordataForClassification) {
        for (int i = 0; i <= diffMinMaxSensordataForClassification; i++) {
          if (!actualSensorMotionData.isEmpty()) {
            actualSensorMotionData.remove(0);
          }
        }
      }
    }

    private GestureInfo detectCorrectGesture(final GestureInfo gesture) {
      if (lastGesture == null || gesture.getGesture() == lastGesture.getGesture()) {
        countBeforeNewGesture++;
      } else if (gesture.getGesture() != lastGesture.getGesture()
          && lastRealGesture != null
          && lastGesture.getGesture() != lastRealGesture.getGesture()) {
        countBeforeNewGesture = 0;
        lastRealGesture =
            new GestureInfo(
                Gesture.UNKNOWN,
                (double) (MIN_COUNT_BEFORE_NEW_GESTURE - 1)
                    / (double) MIN_COUNT_BEFORE_NEW_GESTURE);
      }
      if (countBeforeNewGesture >= MIN_COUNT_BEFORE_NEW_GESTURE) {
        countBeforeNewGesture = 0;
        lastRealGesture = gesture;
      }
      return lastRealGesture;
    }
  }
}

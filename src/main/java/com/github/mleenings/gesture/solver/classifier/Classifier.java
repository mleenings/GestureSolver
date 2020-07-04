package com.github.mleenings.gesture.solver.classifier;

import com.github.mleenings.gesture.solver.data.objects.sensor.GestureInfo;
import com.github.mleenings.gesture.solver.data.objects.sensor.PreGestureClassificationData;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;

import java.util.List;

/** Classifier */
public interface Classifier {
  /**
   * @param activity
   * @param range
   * @return the classified gesture
   */
  GestureInfo classify(List<SensorMotionData> activity, double range);

  /**
   * set the pre classification data
   *
   * @param preGestureClassificationData
   */
  void setPreGestureClassificationData(PreGestureClassificationData preGestureClassificationData);

  /** @return the default range */
  double getDefaultRange();
}

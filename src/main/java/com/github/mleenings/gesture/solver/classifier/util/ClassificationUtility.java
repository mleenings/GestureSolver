package com.github.mleenings.gesture.solver.classifier.util;

import com.github.mleenings.gesture.solver.data.objects.sensor.Gesture;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** Utility for the classification */
public class ClassificationUtility {

  private ClassificationUtility() {
    // private constructor
  }

  /**
   * @param trainingsset
   * @return a map with trainingsset sorted by gesture
   */
  public static Map<Gesture, List<List<SensorMotionData>>> sortTrainingssetByGesture(
      List<List<SensorMotionData>> trainingsset) {
    final Map<Gesture, List<List<SensorMotionData>>> map = new EnumMap<>(Gesture.class);
    for (List<SensorMotionData> seq : trainingsset) {
      final Gesture gesture = seq.get(0).getGesture();
      List<List<SensorMotionData>> setToGesture =
          map.computeIfAbsent(gesture, k -> new LinkedList<>());
      setToGesture.add(seq);
    }
    return map;
  }
}

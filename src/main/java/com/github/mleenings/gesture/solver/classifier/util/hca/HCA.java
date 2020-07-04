package com.github.mleenings.gesture.solver.classifier.util.hca;

import com.github.mleenings.gesture.solver.classifier.util.DBA;
import com.github.mleenings.gesture.solver.data.objects.sensor.Gesture;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import com.github.mleenings.gesture.solver.javaml.Dtw;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import java.util.*;

/** Hierarchical cluster analysis */
public class HCA {

  private static final double PERCENT_DISTANCE_TO_PARENT = 0.5;
  private HCANode root;
  private int maxDepth;
  private Set<HCANode> children = new HashSet<>();
  private ListMultimap<Gesture, List<List<SensorMotionData>>> centroidsForGestures =
      MultimapBuilder.treeKeys().arrayListValues().build();

  /**
   * constructor
   *
   * @param trainingsset
   */
  public HCA(final List<List<SensorMotionData>> trainingsset) {
    root = generateTreeFromSet(trainingsset);
    init();
  }

  /**
   * constructor
   *
   * @param trainingsset
   */
  public HCA(Map<Gesture, List<List<SensorMotionData>>> trainingsset) {
    root = generateTreeFromMap(trainingsset);
    init();
  }

  /** @return centroids for gestures as map */
  public ListMultimap<Gesture, List<List<SensorMotionData>>> getCentroidsForGestures() {
    return centroidsForGestures;
  }

  private void init() {
    // maxDepth = maxDepth(root);
    calculateCentroids(root);
  }

  private void calculateCentroids(HCANode node) {
    if (node.getChildren() != null && !node.getChildren().isEmpty()) {
      calculateCentroids(node, false);
    }
  }

  private void calculateCentroids(HCANode node, boolean isFinish) {
    if (isFinish || node == null) {
      return;
    }
    boolean tmpIsFinish = false;
    if (node.getGestures().size() == 1) {
      double maxDistFromChildrenToParent = getMaxDistanceFromChildrenToParent(node);
      double maxDistanceToFirstNode = getMaxDistanceFromParentFromFirstNode(node);
      if (maxDistFromChildrenToParent
          <= maxDistanceToFirstNode + maxDistanceToFirstNode * PERCENT_DISTANCE_TO_PARENT) {
        final Gesture gesture = node.getGesturesArray()[0];
        final List<List<SensorMotionData>> trainingssetUnderNode = getTrainingssetUnderNode(node);
        centroidsForGestures.put(gesture, trainingssetUnderNode);
        tmpIsFinish = true;
      }
    }
    for (HCANode child : node.getChildren()) {
      calculateCentroids(child, tmpIsFinish);
    }
  }

  private List<List<SensorMotionData>> getTrainingssetUnderNode(HCANode hcaNode) {
    List<List<SensorMotionData>> list = new ArrayList<>();
    if (hcaNode.getChildren() == null || hcaNode.getChildren().isEmpty()) {
      list.add(hcaNode.getCentroid());
    } else {
      for (HCANode child : hcaNode.getChildren()) {
        list.addAll(getTrainingssetUnderNode(child));
      }
    }
    return list;
  }

  private double getMaxDistanceFromChildrenToParent(HCANode node) {
    double max = Double.MIN_VALUE;
    for (HCANode child : node.getChildren()) {
      max = Math.max(child.getDistanceToParent(), max);
    }
    return max;
  }

  private double getMaxDistanceFromParentFromFirstNode(HCANode node) {
    return getMaxDistanceFromParentFromFirstNode(node, Double.MIN_VALUE);
  }

  private double getMaxDistanceFromParentFromFirstNode(HCANode node, double maxRek) {
    double max = Double.MIN_VALUE;
    if (node.getChildren() != null && !node.getChildren().isEmpty()) {
      for (HCANode child : node.getChildren()) {
        max = Math.max(max, getMaxDistanceFromParentFromFirstNode(child, maxRek));
      }
    } else {
      for (HCANode child : node.getParent().getChildren()) {
        max = Math.max(maxRek, child.getDistanceToParent());
      }
    }
    return max;
  }

  private HCANode generateTreeFromMap(Map<Gesture, List<List<SensorMotionData>>> trainingsset) {
    if (trainingsset.size() != 0) {
      final List<HCANode> rootNodesForGesture = new ArrayList<>();
      for (Map.Entry<Gesture, List<List<SensorMotionData>>> entryToGesture :
          trainingsset.entrySet()) {
        rootNodesForGesture.add(generateTreeFromSet(entryToGesture.getValue()));
      }
      return generateTreeFromNodes(rootNodesForGesture).get(0);
    }
    return new HCANode(null);
  }

  // not used at the moment
  private int maxDepth(HCANode node) {
    int deepest = 0;
    if (node.getChildren() != null) {
      for (HCANode child : node.getChildren()) {
        deepest = Math.max(deepest, maxDepth(child));
      }
    }
    return deepest + 1;
  }

  private List<HCANode> convert(final List<List<SensorMotionData>> trainingsset) {
    final List<HCANode> list = new LinkedList<>();
    for (List<SensorMotionData> train : trainingsset) {
      final HCANode node = new HCANode(null, train);
      node.addGesture(train.get(0).getGesture());
      list.add(node);
    }
    children.addAll(list);
    return list;
  }

  private HCANode generateTreeFromSet(final List<List<SensorMotionData>> trainingsset) {
    return generateTreeFromNodes(convert(trainingsset)).get(0);
  }

  private List<HCANode> generateTreeFromNodes(List<HCANode> nodes) {
    if (nodes.size() == 1 || nodes.isEmpty()) {
      return nodes;
    }
    final List<HCANode> bottomUpSet = new ArrayList<>();
    final HashSet<Integer> usedIndexes = new HashSet<>();
    for (int i = 0; i < nodes.size(); i++) {
      if (!usedIndexes.contains(i)) {
        double minDist = Double.MAX_VALUE;
        int minIdx = -1;
        minIdx = getMinIdx(nodes, usedIndexes, i, minDist, minIdx);
        if (minIdx == -1) {
          bottomUpSet.add(nodes.get(i));
        } else {
          usedIndexes.add(minIdx);
          final HCANode childOne = nodes.get(i);
          final HCANode childTwo = nodes.get(minIdx);
          final List<SensorMotionData> centroid =
              DBA.average(convertToSensorMotionDataList(childOne, childTwo));
          final HCANode parent = new HCANode(convertToHCANodeList(childOne, childTwo), centroid);
          childOne.setParent(parent);
          childTwo.setParent(parent);
          bottomUpSet.add(parent);
        }
      }
    }
    return generateTreeFromNodes(bottomUpSet);
  }

  private int getMinIdx(
      List<HCANode> nodes, HashSet<Integer> usedIndexes, int i, double minDist, int minIdx) {
    double tmpMinDist = minDist;
    int tmpMinIdx = minIdx;
    for (int j = i + 1; j < nodes.size(); j++) {
      if (!usedIndexes.contains(j)) {
        double dist =
            Dtw.getDistanceWithNewTime(nodes.get(i).getCentroid(), nodes.get(j).getCentroid());
        if (dist < tmpMinDist) {
          tmpMinDist = dist;
          tmpMinIdx = j;
        }
      }
    }
    return tmpMinIdx;
  }

  private List<HCANode> convertToHCANodeList(HCANode... nodes) {
    List<HCANode> list = new LinkedList<>();
    for (HCANode node : nodes) {
      list.add(node);
    }
    return list;
  }

  private List<List<SensorMotionData>> convertToSensorMotionDataList(HCANode... nodes) {
    List<List<SensorMotionData>> list = new LinkedList<>();
    for (HCANode node : nodes) {
      list.add(node.getCentroid());
    }
    return list;
  }
}

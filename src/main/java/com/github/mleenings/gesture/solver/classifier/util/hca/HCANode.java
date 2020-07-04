package com.github.mleenings.gesture.solver.classifier.util.hca;

import com.github.mleenings.gesture.solver.data.objects.sensor.Gesture;
import com.github.mleenings.gesture.solver.data.objects.sensor.SensorMotionData;
import com.github.mleenings.gesture.solver.javaml.Dtw;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** HCA Node */
public class HCANode {
  private List<HCANode> children = new ArrayList<>();
  private HCANode parent;
  private double distanceToParent = Double.MIN_VALUE;
  private List<SensorMotionData> centroid;
  private Set<Gesture> gestures = new HashSet<>();
  private List<List<SensorMotionData>> trainingssetToLastChild = new ArrayList<>();

  /**
   * constructor
   *
   * @param children
   */
  public HCANode(List<HCANode> children) {
    this(children, null);
  }

  /**
   * constructor
   *
   * @param children
   * @param centroid
   */
  public HCANode(List<HCANode> children, List<SensorMotionData> centroid) {
    setChildren(children);
    setCentroid(centroid);
  }

  /** @return the children */
  public List<HCANode> getChildren() {
    return children;
  }

  /**
   * set the children
   *
   * @param children
   */
  public void setChildren(List<HCANode> children) {
    this.children = children;
    if (children != null) {
      for (HCANode child : children) {
        gestures.addAll(child.getGestures());
      }
    } else {
      this.children = new ArrayList<>();
    }
  }

  /** @return the parent */
  public HCANode getParent() {
    return parent;
  }

  /**
   * set the parent
   *
   * @param parent
   */
  public void setParent(HCANode parent) {
    this.parent = parent;
    calcDistanceToParent();
  }

  private void calcDistanceToParent() {
    if (parent != null && centroid != null) {
      distanceToParent = Dtw.getDistanceWithNewTime(parent.getCentroid(), centroid);
    }
  }

  /** @return the centroid of the Node */
  public List<SensorMotionData> getCentroid() {
    return centroid;
  }

  /**
   * set the centroid of the node
   *
   * @param centroid
   */
  public void setCentroid(List<SensorMotionData> centroid) {
    this.centroid = centroid;
    calcDistanceToParent();
  }

  /**
   * add children to the node
   *
   * @param child
   */
  public void addChild(HCANode child) {
    if (children == null) {
      children = new ArrayList<>();
    }
    children.add(child);
  }

  /** @return the gesture of the node */
  public Set<Gesture> getGestures() {
    return gestures;
  }

  /** @return the gestures of the node */
  public Gesture[] getGesturesArray() {
    return gestures.toArray(new Gesture[gestures.size()]);
  }

  /**
   * add gesture to the node
   *
   * @param gesture
   */
  public void addGesture(Gesture gesture) {
    gestures.add(gesture);
  }

  /** @return the distance to the parent */
  public double getDistanceToParent() {
    return distanceToParent;
  }

  /** @return the trainingsset of the last child under this node */
  public List<List<SensorMotionData>> getTrainingssetToLastChild() {
    return trainingssetToLastChild;
  }

  /**
   * set the trainingsset of the last child unter this node
   *
   * @param trainingssetToLastChild
   */
  public void setTrainingssetToLastChild(List<List<SensorMotionData>> trainingssetToLastChild) {
    this.trainingssetToLastChild = trainingssetToLastChild;
  }
}

package com.github.mleenings.gesture.solver.dao;

import com.github.mleenings.gesture.solver.data.objects.sensor.PreGestureClassificationData;

import java.io.File;

/** The DAO for PreGestureClassificationData */
public class PreGestureClassificationDataDAO extends DAO<PreGestureClassificationData> {
  private static final String FILE_NAME =
      "PreGestureClassificationData" + MACHINE_LEARNING_PROJECT_FILE_EXTENSION;

  /** constructor */
  public PreGestureClassificationDataDAO() {
    super(PreGestureClassificationData.class, "Gesture-Training");
  }

  /** @return the PreGestureClassificationData from external storage */
  public PreGestureClassificationData loadData() {
    return loadDataJson();
  }

  /** @return the PreGestureClassificationData from external storage */
  public PreGestureClassificationData loadDataJson() {
    return super.load(new File(storageLocation, FILE_NAME));
  }

  /**
   * save the PreGestureClassificationData to external storage
   *
   * @param data
   * @throws ProjectDAOException
   */
  public void saveData(PreGestureClassificationData data) {
    saveDataJson(data);
  }

  private void saveDataJson(PreGestureClassificationData data) {
    super.save(data, new File(storageLocation, FILE_NAME));
  }

  /** delete the PreGestureClassificationData from external storage */
  public void deleteData() {
    super.deleteFile(new File(storageLocation, FILE_NAME));
  }
}

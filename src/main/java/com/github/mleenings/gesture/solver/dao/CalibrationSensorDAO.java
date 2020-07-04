package com.github.mleenings.gesture.solver.dao;

import com.github.mleenings.gesture.solver.data.objects.sensor.CalibrationData;

import java.io.File;

/** DAO for the calibration of the sensors */
public class CalibrationSensorDAO extends DAO<CalibrationData> {
  protected static final String FILE_NAME_WITHOUT_EXTENSION = "CallibrationData";
  protected static final String FILE_NAME =
      FILE_NAME_WITHOUT_EXTENSION + MACHINE_LEARNING_PROJECT_FILE_EXTENSION;
  protected static final String SUBDIR = "Gesture-Training";

  /**
   * constructor
   *
   * @param subdir
   */
  public CalibrationSensorDAO(String subdir) {
    super(CalibrationData.class, subdir == null ? SUBDIR : SUBDIR + "/subdir");
  }

  /** constructor */
  public CalibrationSensorDAO(File envidir, String basedir, String subdir) {
    super(CalibrationData.class, envidir, basedir, subdir);
  }

  /** constructor */
  public CalibrationSensorDAO() {
    this(null);
  }

  /**
   * @return the CalibrationData
   * @throws ProjectDAOException
   */
  public CalibrationData loadData() {
    return super.load(new File(storageLocation, FILE_NAME));
  }

  /**
   * save the Calibration Data
   *
   * @param data
   * @throws ProjectDAOException
   */
  public void saveData(CalibrationData data) {
    super.save(data, new File(storageLocation, FILE_NAME));
  }
}

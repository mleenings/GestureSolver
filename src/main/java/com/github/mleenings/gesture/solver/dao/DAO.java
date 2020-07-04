package com.github.mleenings.gesture.solver.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Basis class for the Data Access Objects */
public class DAO<T> {
  protected static final String MACHINE_LEARNING_PROJECT_FILE_EXTENSION = ".ml.json";
  protected static final String ERROR_MSG = "Could not load file from external storage";
  protected static final String BASE_DIRECTORY = "/";
  protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Logger LOGGER = LoggerFactory.getLogger(DAO.class);

  static {
    OBJECT_MAPPER.registerModule(new GuavaModule());
  }

  protected File storageLocation;
  private Class<T> clazz;
  private TypeReference<T> typeReference;

  /**
   * constructor
   *
   * @param clazz
   */
  public DAO(Class<T> clazz) {
    this(clazz, null);
  }

  /**
   * constructor
   *
   * @param clazz
   * @param envidir
   * @param basedir
   * @param subdir
   */
  public DAO(Class<T> clazz, File envidir, String basedir, String subdir) {
    this.clazz = clazz;
    setStorageLocation(envidir, basedir, subdir);
  }

  /**
   * constructor
   *
   * @param clazz
   * @param subdir
   */
  public DAO(Class<T> clazz, String subdir) {
    this(clazz, getStorageDirectory(), BASE_DIRECTORY, subdir);
  }

  /**
   * constructor
   *
   * @param typeReference
   * @param subdir
   */
  public DAO(TypeReference<T> typeReference, String subdir) {
    this(typeReference, getStorageDirectory(), BASE_DIRECTORY, subdir);
  }

  /**
   * constructor
   *
   * @param typeReference
   * @param envidir
   * @param basedir
   * @param subdir
   */
  public DAO(TypeReference<T> typeReference, File envidir, String basedir, String subdir) {
    this.typeReference = typeReference;
    setStorageLocation(envidir, basedir, subdir);
  }

  protected static File getStorageDirectory() {
    return new File(".");
  }

  protected void setStorageLocation(File envidir, String basedir, String subdir) {
    String savedir = basedir;
    if (subdir != null) {
      savedir += "/" + subdir;
    }
    storageLocation = new File(envidir, savedir);
    if (!storageLocation.isDirectory()) {
      storageLocation.mkdirs();
    }
  }

  /**
   * Checks if the external storage is readable and writable (see <a
   * href="http://developer.android.com/training/basics/data-storage/files.html#WriteExternalStorage">here</a>).
   *
   * @return
   */
  public boolean isExternalStorageReadableAndWritable() {
    return getStorageDirectory().exists();
  }

  protected List<T> listAll() {
    File[] files = getAllFilesInStorageLocation();
    List<T> projects = new ArrayList<>();
    if (files != null) {
      for (File f : files) {
        addProject(projects, f);
      }
    }
    return projects;
  }

  private void addProject(List<T> projects, File f) {
    try {
      if (typeReference != null) {
        projects.add((T) OBJECT_MAPPER.readValue(f, typeReference));
      } else {
        projects.add(OBJECT_MAPPER.readValue(f, clazz));
      }
    } catch (IOException ex) {
      LOGGER.error(DAO.class.getName(), ex.getMessage(), ex);
      throw new ProjectDAOException(ERROR_MSG, ex);
    }
  }

  protected File[] getAllFilesInStorageLocation() {
    return storageLocation.listFiles(
        new FileFilter() {
          @Override
          public boolean accept(File pathname) {
            return pathname.getName().endsWith(MACHINE_LEARNING_PROJECT_FILE_EXTENSION);
          }
        });
  }

  protected T load(String filename) {
    return load(getFile(filename));
  }

  protected T load(File file) {
    if (!file.isFile()) {
      return null;
    }
    try {
      if (typeReference != null) {
        return OBJECT_MAPPER.readValue(file, typeReference);
      } else {
        return OBJECT_MAPPER.readValue(file, clazz);
      }
    } catch (IOException ex) {
      LOGGER.error(DAO.class.getName(), ex.getMessage(), ex);
      throw new ProjectDAOException(ERROR_MSG, ex);
    }
  }

  protected void save(T obj, String filename) {
    save(obj, getFile(filename));
  }

  protected void save(T obj, File file) {
    try {
      OBJECT_MAPPER.writeValue(file, obj);
    } catch (IOException ex) {
      LOGGER.error(DAO.class.getName(), ex.getMessage(), ex);
      throw new ProjectDAOException(ERROR_MSG, ex);
    }
  }

  protected File getFile(String filename) {
    return new File(storageLocation, filename + MACHINE_LEARNING_PROJECT_FILE_EXTENSION);
  }

  protected boolean deleteFile(final File file) {
    return file.delete();
  }

  protected boolean deleteFile(final String filename) {
    final File fileToDelete = getFile(filename);
    return fileToDelete.delete();
  }
}

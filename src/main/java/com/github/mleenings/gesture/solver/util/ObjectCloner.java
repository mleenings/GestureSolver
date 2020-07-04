package com.github.mleenings.gesture.solver.util;

import com.github.mleenings.gesture.solver.dao.DAO;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.*;

public class ObjectCloner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectCloner.class);

    private ObjectCloner(){
        // private constcutor
    }

    /**
     * returns a deep copy of an object
     */
    public static Object deepCopy(Object oldObj) {
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(oldObj);
            oos.flush();
            ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bin);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error(ObjectCloner.class.getName(), e.getMessage(), e);
            return null;
        }
    }
}

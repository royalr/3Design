package com.example.roi.a3Design;


import android.util.Log;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

import java.util.Stack;

import static java.lang.Math.toRadians;


public class Undo {
    public enum UndoAction {
        MOVEMENT, ROTATION, DELETE, ADD
    }

    private static class ObjectInfo {
        final String objectName;
        final SimpleVector objectPosition;
        final UndoAction objectAction;

        ObjectInfo(String name, SimpleVector pos, UndoAction action) {
            objectName = name;
            objectPosition = pos;
            objectAction = action;
        }

    }

    private static Stack<ObjectInfo> log;
    private static boolean recordMode = true;

    static void init() {
        log = new Stack<>();
    }

    public static void writeLog(Object3D obj, UndoAction action) {
        if (obj == null || !recordMode) {
            return;
        }
        String objName = obj.getName();
        Log.i("Undo", "Writing log: Object name: " + objName + " Position: " + obj.getTranslation() + " Action: " + action);
        log.push(new ObjectInfo(objName, obj.getTranslation(), action));
    }

    public static void readLog(World w) {
        if (log.empty()) {
            return;
        }
        ObjectInfo logDetails = log.pop();
        Log.i("Undo", "Reading log: Object name: " + logDetails.objectName + " Position: " + logDetails.objectPosition + " Action: " + logDetails.objectAction);

        switch (logDetails.objectAction) {
            case MOVEMENT:
                w.getObjectByName(logDetails.objectName).clearTranslation();

                w.getObjectByName(logDetails.objectName).translate(logDetails.objectPosition);
                break;
            case ROTATION:
                w.getObjectByName(logDetails.objectName).rotateY((float) toRadians(-90));
                break;
            case ADD:
                w.removeObject(w.getObjectByName(logDetails.objectName));
                break;
            case DELETE:
                Object3D obj = ObjectManager.loadObject(logDetails.objectName.replaceAll("\\d", "")); // remove id numbers
                obj.setName(logDetails.objectName);
                obj.translate(logDetails.objectPosition);
                w.addObject(obj);
                break;
        }
    }

    public static void setRecordMode(boolean flag) {
        recordMode = flag;
    }
}

package com.example.roi.a3Design;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.ExtendedPrimitives;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.threed.jpct.Object3D.NO_OBJECT;
import static com.threed.jpct.Object3D.OBJ_VISIBLE;
import static java.lang.Math.toRadians;


public class ObjectManager {
    static Context context;
    static int id;
    private static Map<String, Integer> rotationsRecords = new HashMap<>();
    private static String objToBeCreatedName;
    private static boolean objToBeCreated = false;

    public static void init(Context c) {
        context = c;
        id = 0;
    }
    public static void setId(int num){
        if(num < id){
            return;
        }
        id = num;
    }
    public static Object3D loadObject(String fileName) {

        AssetManager assetManager = context.getAssets();
        try {
            //InputStream mtl = assetManager.open("mtl/***.mtl");
            InputStream obj = assetManager.open("obj/" + fileName + ".obj");
            Object3D[] model = Loader.loadOBJ(obj, null, 0.01f);
            Object3D object = new Object3D(0);
            Object3D temp = null;
            for (int j = 0; j < model.length; j++) {
                temp = model[j];
                temp.setCenter(SimpleVector.ORIGIN);
                temp.rotateZ((float) (-1 * Math.PI));
                temp.rotateMesh();
                temp.setRotationMatrix(new Matrix());
                object = Object3D.mergeObjects(object, temp);
                object.build();
            }
            float[] f = object.getMesh().getBoundingBox();
            if (f[3] - f[2] < 0.1) {
                Log.i("Object Manager", "I'm a tiny object! Do something about me!");
            }
            object.translate(0, -f[3], 0);
            object.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            String objectName = fileName + Integer.toString(id);
            id++;
            object.setName(objectName);

            Log.d("Object Manager", objectName + " has been added successfully!");
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void toggleMenu(Object3D obj, World world, boolean status) {
        if (obj == null) {
            return;
        }
        if (!world.containsObject(obj)) {
            return;
        }
        if (status) {
            obj.setAdditionalColor(RGBColor.BLUE);
            float[] f = obj.getMesh().getBoundingBox();

            Object3D rotationBtn = Primitives.getSphere(0.2f);
            rotationBtn.setAdditionalColor(RGBColor.RED);
            rotationBtn.setLighting(Object3D.LIGHTING_NO_LIGHTS);
            rotationBtn.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            obj.addChild(rotationBtn);
            rotationBtn.translate(obj.getCenter());
            rotationBtn.translate(-0.3f, f[2] - f[3], 0);
            rotationBtn.setName("Rotation Menu");
            rotationBtn.build();
            world.addObject(rotationBtn);

            Object3D verticalMotionBtn = ExtendedPrimitives.createPyramid(0.4f);
            verticalMotionBtn.setAdditionalColor(RGBColor.GREEN);
            verticalMotionBtn.setLighting(Object3D.LIGHTING_NO_LIGHTS);
            verticalMotionBtn.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            verticalMotionBtn.translate(obj.getCenter());
            verticalMotionBtn.translate(0.3f, f[2] - f[3], 0);
            verticalMotionBtn.setName("Vertical Menu");
            verticalMotionBtn.build();
            obj.addChild(verticalMotionBtn);
            world.addObject(verticalMotionBtn);


//            Object3D shadow = ExtendedPrimitives.createDisc(1, 20);
//            shadow.setAdditionalColor(RGBColor.BLUE);
//            shadow.setLighting(Object3D.LIGHTING_NO_LIGHTS);
//            shadow.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
//            shadow.translate(obj.getCenter());
//            shadow.rotateX((float) toRadians(90));
//            shadow.setName("Shadow Menu");
//            shadow.build();
//            obj.addChild(shadow);
//            //shadow.addChild(obj);
//            shadow.translate(0, -shadow.getTransformedCenter().y, 0);
//            world.addObject(shadow);

        } else {
            obj.clearAdditionalColor();
            if (world.containsObject(world.getObjectByName("Rotation Menu"))) {
                world.removeObject(world.getObjectByName("Rotation Menu"));
            }
            if (world.containsObject(world.getObjectByName("Vertical Menu"))) {
                world.removeObject(world.getObjectByName("Vertical Menu"));
            }
            //world.removeObject(world.getObjectByName("Shadow Menu"));
            // obj.removeChild();
        }


    }

    static void panObjectBy(Object3D obj, SimpleVector position) {
        position.y = obj.getTranslation().y;
        obj.clearTranslation();
        position.x -= obj.getCenter().x;
        position.z -= obj.getCenter().z;
        obj.translate(position);

    }

    public static void rotateObj(Object3D obj, boolean direction) {
        if (direction) {
            obj.rotateY((float) toRadians(90));
        } else {
            obj.rotateY((float) toRadians(-90));
        }
        regRotation(obj.getName(), direction);
    }

    private static void regRotation(String objName, boolean direction) {
        //direction = true - cw | false - ccw
        Integer state = 0;
        if (rotationsRecords.containsKey(objName)) {
            state = rotationsRecords.get(objName);
        }
        rotationsRecords.put(objName, direction ? state + 1 % 4 : state - 1 % 4);
    }

    public static Integer getRotation(String objName) {
        return rotationsRecords.get(objName);
    }

    public static boolean isObjToBeCreated() {
        return objToBeCreated;
    }

    public static void setObjToBeCreated(boolean objToBeCreated) {
        ObjectManager.objToBeCreated = objToBeCreated;
    }

    public static String getObjToBeCreatedName() {
        return objToBeCreatedName;
    }

    public static void setObjToBeCreatedName(String objNamet) {
        objToBeCreatedName = objNamet;
    }
}

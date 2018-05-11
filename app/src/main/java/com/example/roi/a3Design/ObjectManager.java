package com.example.roi.a3Design;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.threed.jpct.Object3D.NO_OBJECT;
import static com.threed.jpct.Object3D.OBJ_VISIBLE;


public class ObjectManager {

    private static ArrayList<Object3D> allObjects = new ArrayList<>();

    // getNewObject(String query)
    // deleteObject(obj id)?

    public static Object3D loadObject(String fileName, float scale, Context c) {


        InputStream objectPath = c.getResources().openRawResource(R.raw.couch_two_seats);
        Object3D[] model = Loader.loadOBJ(objectPath, null, scale);
        Object3D o3d = new Object3D(0);
        Object3D temp = null;
        for (int i = 0; i < model.length; i++) {
            temp = model[i];
            temp.setCenter(SimpleVector.ORIGIN);
            temp.rotateZ((float) (-1 * Math.PI));
            temp.rotateMesh();
            temp.setRotationMatrix(new Matrix());
            o3d = Object3D.mergeObjects(o3d, temp);
            o3d.build();
        }
        // should be object size /2
        o3d.translate(0, 0, -5);

        o3d.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
        allObjects.add(o3d);

        return o3d;

    }


    static void toggleMenu(Object3D obj, World world, boolean status) {
        if (obj == null) {
            return;
        }
        if (status) {
            obj.setAdditionalColor(RGBColor.BLUE);
            Object3D menu = Primitives.getSphere(0.2f);
            menu.setAdditionalColor(RGBColor.RED);
            menu.setLighting(Object3D.LIGHTING_NO_LIGHTS);
            menu.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
            float[] f = obj.getMesh().getBoundingBox();
            obj.addChild(menu);
            menu.translate(0, (-(f[3] - f[2]) - 0.2f), 0);
            menu.setName("Object Menu");
            menu.build();
            world.addObject(menu);

            menu.setVisibility(OBJ_VISIBLE);

        } else {
            obj.clearAdditionalColor();
            world.removeObject(world.getObjectByName("Object Menu"));
            // obj.removeChild();
        }


    }

    static void panObjectBy(Object3D obj, SimpleVector position) {
        Log.d("event", "Move object to " + position);
        obj.clearTranslation();
        obj.translate(position);

    }
}

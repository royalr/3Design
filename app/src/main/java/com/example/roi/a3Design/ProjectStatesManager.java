package com.example.roi.a3Design;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.threed.jpct.Camera;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.ExtendedPrimitives;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Scanner;

import static java.lang.Math.toRadians;

public class ProjectStatesManager {

    private static boolean newProject;
    private static Context context;
    private static int loadId;


    public static void init(boolean status) {
        newProject = status;
    }

    public static void regLoadId(int id) {
        loadId = id;
    }

    public static void regContext(Context c) {
        context = c;
    }

    public static boolean getStatus() {
        return newProject;
    }

    public static World loadState(int id) {
        if (id == -1) {
            id = loadId;
        }
        String data = "";
        try {
            FileInputStream fis = context.openFileInput("save" + id);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            data = new String(buffer);
            Log.d("file", new String(buffer));
            ((Activity)context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ((Activity)context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            });
            return null;

        }
        Scanner s = new Scanner(data).useDelimiter(" ");

        SimpleVector cameraPos = new SimpleVector(Float.parseFloat(s.next()), Float.parseFloat(s.next()), Float.parseFloat(s.next()));
        WallManager.registerWalls(new Wall(Float.parseFloat(s.next())), new Wall(Float.parseFloat(s.next())));
        World loadedWorld = getNewWorld();
        loadedWorld.getCamera().setPosition(cameraPos);
        loadedWorld.getCamera().lookAt(new SimpleVector(0, 0, 0));


        while (s.hasNext()) {
            String objName = s.next();
            ObjectManager.setId(Integer.parseInt(objName.replaceAll("\\D", "")));
            Log.d("file2", objName);
            Object3D obj = ObjectManager.loadObject(objName.replaceAll("\\d", "")); // remove id numbers
            obj.setName(objName);
            SimpleVector objPosition = new SimpleVector(Float.parseFloat(s.next()), Float.parseFloat(s.next()), Float.parseFloat(s.next()));
            obj.translate(objPosition);
            int rotations = Integer.parseInt(s.next());
            for (int i = 0; i < rotations; i++) {
                obj.rotateY((float) toRadians(90));
            }
            loadedWorld.addObject(obj);
        }

        return loadedWorld;
    }

    public static void saveState(World world, int id) {
        StringBuilder sb = new StringBuilder();
        SimpleVector cameraPosition = world.getCamera().getPosition();
        sb.append(cameraPosition.x);
        sb.append(" ");
        sb.append(cameraPosition.y);
        sb.append(" ");
        sb.append(cameraPosition.z);
        sb.append(" ");


        float[] walls = WallManager.getRoomDimentions();
        sb.append(walls[0]);
        sb.append(" ");
        sb.append(walls[1]);
        sb.append(" ");


        Enumeration<Object3D> objs = world.getObjects();
        while (objs.hasMoreElements()) {
            Object3D elem = objs.nextElement();
            if (elem.getName().equals("wall") || elem.getName().equals("floor") || elem.getName().equals("Vertical Menu") || elem.getName().equals("Rotation Menu")) {
                continue;
            }
            sb.append(elem.getName());
            sb.append(" ");
            sb.append(elem.getTranslation().x);
            sb.append(" ");
            sb.append(elem.getTranslation().y);
            sb.append(" ");
            sb.append(elem.getTranslation().z);
            sb.append(" ");

            Integer rotations = ObjectManager.getRotation(elem.getName());
            if (rotations == null) {
                sb.append("0");
            } else {
                sb.append(rotations);
            }
            sb.append(" ");

        }

        try {

            FileOutputStream fos = context.openFileOutput("save" + id, Context.MODE_PRIVATE);
            fos.write(sb.toString().getBytes());
            fos.close();
            ((Activity)context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ((Activity)context).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static World getNewWorld() {
        World newWorld = new World();
        Light light = new Light(newWorld);
        light.setIntensity(150, 150, 150);
        light.setPosition(new SimpleVector(30, -50, 20));
        Camera cam = newWorld.getCamera();
        cam.moveCamera(Camera.CAMERA_MOVEOUT, 11);
        cam.moveCamera(Camera.CAMERA_MOVEUP, 10);
        cam.lookAt(new SimpleVector(0,0,0));
        newWorld.addObjects(WallManager.getWallsObjects());
        newWorld.addObject(WallManager.getFloor().getFloorAsObj3D());
        Object3D grass = ExtendedPrimitives.createBox(new SimpleVector(500,500,0.01f));
        grass.setName("floor");
        grass.translate(0,0.03f,0);
        grass.setAdditionalColor(new RGBColor(51,102,0,255));
        grass.rotateX((float) Math.toRadians(90));
        newWorld.addObject(grass);
        return newWorld;
    }
}

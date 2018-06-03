package com.example.roi.a3Design;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.Polyline;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

import java.util.Enumeration;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.threed.jpct.Interact2D.reproject2D3DWS;

/**
 * Created by Roi on 03/04/2018.
 */

public class MyRenderer implements GLSurfaceView.Renderer {
    private FrameBuffer fb = null;
    private static World world = null;
    private WallManager wm = null;
    private float camDistant = 15;
    private final float minHeight = -4;
    private Context context;
    private Object3D tempObjectHolder = null;
    private static Object3D currentObject = null;
    private boolean deleteFlag = false;
    private boolean undoFlag = false;


    public enum TapStatus {
        SAME_OBJECT, NO_OBJECT, NEW_OBJECT, VERTICAL_MENU
    }

    MyRenderer(Context c) {
        context = c;
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        TextureHandler.init(); // load textures

        // re-init when load
        ObjectManager.init(context);
        Undo.init();

        // to see the axis
        // markAxis();

        if (ProjectStatesManager.getStatus()) {
            // get world with default values
            world = ProjectStatesManager.getNewWorld();
        } else {
//             get world with values saved in a file
            ObjectManager.init(context);
            Undo.init();
            world = ProjectStatesManager.loadState(-1);
        }

        // delete & undo buttons handlers
        final Button delete = ((Activity) context).findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteFlag = true;
            }
        });
        final Button undo = ((Activity) context).findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                undoFlag = true;
            }
        });
        final Button newProj = ((Activity) context).findViewById(R.id.newProj);
        newProj.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Caution!")
                        .setMessage("Are you sure you want to start a new project? All unsaved changes will be lost.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(context, RoomSize.class);
                                intent.putExtra("EXTRA_SESSION_ID", "renderer");
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

    }


    public void handleCreatingNewObject(float posX, float posY) {
        if (ObjectManager.isObjToBeCreated()) {
            Object3D obj = ObjectManager.loadObject(ObjectManager.getObjToBeCreatedName());
            ObjectManager.panObjectBy(obj, getWorldPositionYAxis(posX, posY, 0));
            world.addObject(obj);
            currentObject = obj;
            ObjectManager.toggleMenu(obj, world, true);
            Undo.writeLog(obj, Undo.UndoAction.ADD);
            ((Activity)context).runOnUiThread(new Runnable() {
                public void run() {
                    MainMenu.unchooseChild();
                }
            });
        }
    }

    public static String getCurrentObjectName() {
        return currentObject == null ? "" : currentObject.getName();
    }

    private void deleteObject() {
        if (currentObject == null) {
            return;
        }
        if (!world.containsObject(currentObject)) {
            return;
        }
        Undo.writeLog(currentObject, Undo.UndoAction.DELETE);
        ObjectManager.toggleMenu(currentObject, world, false);
        world.removeObject(currentObject);
        currentObject = null;
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        fb = new FrameBuffer(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if (deleteFlag) {
            deleteFlag = false;
            deleteObject();
        }
        if (undoFlag) {
            undoFlag = false;
            Undo.readLog(world);
        }
        fb.clear(new RGBColor(219, 219, 219));
        world.renderScene(fb);
        world.draw(fb);
        fb.display();
    }

    public void panCameraBy(float dx, float dy) {
        // need to add the use of minHeight of camera - V
        Camera cam = world.getCamera();
        cam.setPosition(0, 0, 0);
        cam.rotateCameraY(0.001f * dx);
        cam.rotateCameraX(0.001f * dy);
        cam.moveCamera(Camera.CAMERA_MOVEOUT, camDistant);

        SimpleVector camPosition = cam.getPosition();
        if (camPosition.y > minHeight) {
            camPosition.y = minHeight;
            cam.setPosition(camPosition);
        }
        cam.lookAt(new SimpleVector(0, 0, 0));
    }

    public void zoom(float factor) {
        camDistant += factor;
        if (camDistant < 8) {
            camDistant = 8;
        }
        Camera cam = world.getCamera();
        cam.setPosition(0, 0, 0);
        cam.moveCamera(Camera.CAMERA_MOVEOUT, camDistant);

    }

    public void panObjectBy(float x, float y) {
        if (currentObject == null) {
            Log.e("Error", "Something went horribly wrong!");
            return;
        }
        Undo.writeLog(currentObject, Undo.UndoAction.MOVEMENT);
        float planeLevel = currentObject.getTranslation().y;
        ObjectManager.panObjectBy(currentObject, getWorldPositionYAxis(x, y, planeLevel));
    }

    public void panObjectVerticallyBy(float dy) {
        Undo.writeLog(currentObject, Undo.UndoAction.MOVEMENT);
        currentObject.translate(0, dy / 100, 0);
    }


    public TapStatus tapHandler(float x, float y) {
        Camera cam = world.getCamera();
        SimpleVector collisionVector = Interact2D.reproject2D3DWS(cam, fb, (int) Math.ceil(x), (int) Math.ceil(y)).normalize();
        Object[] f = world.calcMinDistanceAndObject3D(world.getCamera().getPosition(), collisionVector, 1000);
        tempObjectHolder = (Object3D) f[1];

        if (world.getObjectByName("Vertical Menu") != null && (Object3D) f[1] == world.getObjectByName("Vertical Menu")) {
            return TapStatus.VERTICAL_MENU;
        }

        if (currentObject == f[1]) {
            if (currentObject == null) {
                return TapStatus.NO_OBJECT;
            }

            return TapStatus.SAME_OBJECT;
        } else {
            if (currentObject == null) {
                return TapStatus.NEW_OBJECT;
            } else {
                // Object was selected and now another object is selected or null
                if (f[1] == null) {
                    return TapStatus.NO_OBJECT;
                } else {
                    return TapStatus.NEW_OBJECT;
                }
            }
        }
    }

    public void handleObjectMenu(TapStatus status) {
        switch (status) {
            case VERTICAL_MENU:
                return;
            case NEW_OBJECT:
                if (world.getObjectByName("Rotation Menu") == tempObjectHolder) {
                    Undo.writeLog(currentObject, Undo.UndoAction.ROTATION);
                    ObjectManager.rotateObj(currentObject, true);
                    return;
                }
                ObjectManager.toggleMenu(tempObjectHolder, world, true);
            case NO_OBJECT:
                ObjectManager.toggleMenu(currentObject, world, false);
                break;
            case SAME_OBJECT:
                break;
        }
        currentObject = tempObjectHolder;
    }

    private SimpleVector getWorldPositionYAxis(float x, float y, float planeLevel) {
        float Y_PLANE = planeLevel;

        SimpleVector dir = Interact2D.reproject2D3DWS(world.getCamera(), fb, (int) Math.ceil(x), (int) Math.ceil(y)).normalize();
        SimpleVector pos = world.getCamera().getPosition();

        float a = (Y_PLANE - pos.y) / dir.y;

        float xn = pos.x + a * dir.x;
        float zn = pos.z + a * dir.z;

        return new SimpleVector(xn, Y_PLANE, zn);

    }

    public static void saveProject(int slot) {
        ProjectStatesManager.saveState(world, slot);
    }

    public static void loadProject(int slot) {
        world = ProjectStatesManager.loadState(slot);
    }

    private void markAxis() {
        SimpleVector[] lineX = {new SimpleVector(0, 0, 0), new SimpleVector(50, 0, 0)};
        SimpleVector[] lineY = {new SimpleVector(0, 0, 0), new SimpleVector(0, 50, 0)};
        SimpleVector[] lineZ = {new SimpleVector(0, 0, 0), new SimpleVector(0, 0, 50)};

        Polyline x = new Polyline(lineX, RGBColor.BLACK);
        Polyline y = new Polyline(lineY, RGBColor.BLUE);
        Polyline z = new Polyline(lineZ, RGBColor.GREEN);

        x.setWidth(5);
        y.setWidth(5);
        z.setWidth(5);
        world.addPolyline(x);
        world.addPolyline(y);
        world.addPolyline(z);

    }

}

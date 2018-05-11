package com.example.roi.a3Design;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.Polyline;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.threed.jpct.Interact2D.reproject2D3DWS;
import static java.lang.Math.toRadians;

/**
 * Created by Roi on 03/04/2018.
 */

public class MyRenderer implements GLSurfaceView.Renderer {
    private FrameBuffer fb = null;
    private World world = null;
    private Light light = null;
    private WallManager wm = null;
    private float camDistant = 15;
    private final float minHeight = -4;
    private Context context;
    private Object3D tempObjectHolder = null;
    private Object3D currentObject = null;


    public enum TapStatus {
        SAME_OBJECT, NO_OBJECT, NEW_OBJECT
    }

    MyRenderer(Context c) {
        context = c;
    }


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        world = new World();
        light = new Light(world);
        light.setIntensity(150, 150, 150);
        light.setPosition(new SimpleVector(30, -50, 20));
        Camera cam = world.getCamera();
        cam.moveCamera(Camera.CAMERA_MOVEOUT, camDistant);


        // need to get this information from query:
        wm = new WallManager(new Wall(8.5f), new Wall(4.5f));
        Floor floor = wm.createFloor();


        Object3D testObj = ObjectManager.loadObject("couchTwoSeats.obj", 1, context);
        world.addObject(testObj);
        world.addObjects(wm.getWallsObjects());
        world.addObject(floor.getFloor());
        // to see the axis
        markAxis();

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        fb = new FrameBuffer(width, height);
        //Log.d("view", "width: "+width+" height: "+height);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
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

    public void panObjectBy(float x, float y) {
        if (currentObject == null) {
            Log.e("Error", "Something went horribly wrong!");
            return;
        }
        ObjectManager.panObjectBy(currentObject, getWorldPositionYAxis(x, y));
    }


    public TapStatus tapHandler(float x, float y) {
        Camera cam = world.getCamera();
        SimpleVector collisionVector = Interact2D.reproject2D3DWS(cam, fb, (int) Math.ceil(x), (int) Math.ceil(y)).normalize();
        Object[] f = world.calcMinDistanceAndObject3D(world.getCamera().getPosition(), collisionVector, 1000);
        tempObjectHolder = (Object3D) f[1];

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
            case NEW_OBJECT:
                if (world.getObjectByName("Object Menu") == tempObjectHolder) {
                    currentObject.rotateY((float) toRadians(90));
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

    private SimpleVector getWorldPositionYAxis(float x, float y) {
        float Y_PLANE = 0;

        SimpleVector dir = Interact2D.reproject2D3DWS(world.getCamera(), fb, (int) Math.ceil(x), (int) Math.ceil(y)).normalize();
        SimpleVector pos = world.getCamera().getPosition();

        float a = (Y_PLANE - pos.y) / dir.y;

        float xn = pos.x + a * dir.x;
        float zn = pos.z + a * dir.z;

        return new SimpleVector(xn, Y_PLANE, zn);

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

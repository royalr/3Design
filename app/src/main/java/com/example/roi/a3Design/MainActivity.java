package com.example.roi.a3Design;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;


import static com.example.roi.a3Design.MyRenderer.TapStatus.NO_OBJECT;
import static com.example.roi.a3Design.MyRenderer.TapStatus.SAME_OBJECT;
import static com.example.roi.a3Design.MyRenderer.TapStatus.VERTICAL_MENU;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private GLSurfaceView mGLView;
    private MyRenderer renderer = null;

    private float lastX, lastY, originX, originY;
    private MyRenderer.TapStatus status;

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 0.1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE); // (NEW)
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ProjectStatesManager.regContext(this);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        ViewPager viewPager = findViewById(R.id.viewpager);

        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        mGLView = this.findViewById(R.id.glSurface);
        mGLView.setEGLContextClientVersion(2);
        mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                // Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
                // back to Pixelflinger on some device (read: Samsung I7500)
                int[] attributes = new int[]{EGL10.EGL_DEPTH_SIZE, 16,
                        EGL10.EGL_NONE};
                EGLConfig[] configs = new EGLConfig[1];
                int[] result = new int[1];
                egl.eglChooseConfig(display, attributes, configs, 1, result);
                return configs[0];
            }
        });
        renderer = new MyRenderer(this);
        mGLView.setRenderer(renderer);
        mGLView.setOnTouchListener(this);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        // should be initialized in the beginnig query
//        ProjectStatesManager.init(true, -1);

    }


    @Override
    protected void onPause() {
        mGLView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean twoFingerFlag = false;

    @Override
    public boolean onTouch(View view, final MotionEvent motionEvent) {

        if (motionEvent == null) {
            return false;
        }
        mScaleGestureDetector.onTouchEvent(motionEvent);
        if (mScaleGestureDetector.isInProgress()) {
            return false;
        }
        switch (motionEvent.getActionMasked() ) {

            case MotionEvent.ACTION_DOWN:

                originX = lastX = motionEvent.getX();
                originY = lastY = motionEvent.getY();

                mGLView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        status = renderer.tapHandler(originX, originY);
                    }
                });
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:

                twoFingerFlag = true;
//                motionEvent.getPointerCoords(1, );
                return true;
            case MotionEvent.ACTION_MOVE:
                final float dx = motionEvent.getX() - lastX;
                final float dy = motionEvent.getY() - lastY;
                lastX = motionEvent.getX();
                lastY = motionEvent.getY();
                //Log.d("event", "Move event");



                mGLView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if (twoFingerFlag) {
//
//                            if (motionEvent.getPointerCount() >1) {
//
//                            }



                           renderer.slideCamera(dx, dy);
                           return;
                        }
                        if (status == SAME_OBJECT) {
                            renderer.panObjectBy(lastX, lastY);
                            renderer.toggleGrid(true);
                            Undo.setRecordMode(false);
                        } else if (status == VERTICAL_MENU) {
                            renderer.panObjectVerticallyBy(dy);
                            Undo.setRecordMode(false);
                        } else {
                            renderer.panCameraBy(dx, dy);
                        }
                    }
                });
                return true;

            case MotionEvent.ACTION_UP:
                if ((Math.abs(originX - motionEvent.getX()) < 5) && (Math.abs(originY - motionEvent.getY()) < 5)) {
                    mGLView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            renderer.handleObjectMenu(status);
                            if (status == NO_OBJECT) {
                                renderer.handleCreatingNewObject(originX, originY);
                            }
                        }
                    });
                }
                renderer.toggleGrid(false);

                twoFingerFlag = false;

                Undo.setRecordMode(true);
                return true;
        }
        return false;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor = 1;
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            Log.d("zoom", "scale factor: " + mScaleFactor);
            Log.d("zoom", "scale: " + scaleGestureDetector.getScaleFactor());

            if (scaleGestureDetector.getScaleFactor() >= 1) {
                mScaleFactor *= -1;
            }
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            renderer.zoom(mScaleFactor);

            return true;

        }
    }
}

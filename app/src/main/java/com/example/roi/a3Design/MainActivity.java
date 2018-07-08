package com.example.roi.a3Design;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;


import static com.example.roi.a3Design.MyRenderer.TapStatus.NO_OBJECT;
import static com.example.roi.a3Design.MyRenderer.TapStatus.SAME_OBJECT;
import static com.example.roi.a3Design.MyRenderer.TapStatus.VERTICAL_MENU;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private GLSurfaceView mGLView;
    private MyRenderer renderer = null;

    private MyRenderer.TapStatus status;
    private ViewConfiguration viewConfig;
    private float threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE); // (NEW)
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewConfig = ViewConfiguration.get(this);
        threshold = viewConfig.getScaledTouchSlop();

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


    }


    @Override
    protected void onPause() {
        mGLView.setPreserveEGLContextOnPause(true);
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

    private boolean threeFingerDragGesture = false;

    @Override
    public void onBackPressed() {

    }

    private float PrimeOriginX, PrimeOriginY, primePointerX, primePointerY, secondaryPointerX, secondaryPointerY;
    private Float oldDistance = null, pointersDistance = null;

    @Override
    public boolean onTouch(View view, final MotionEvent motionEvent) {

        switch (motionEvent.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                PrimeOriginX = primePointerX = motionEvent.getX();
                PrimeOriginY = primePointerY = motionEvent.getY();

                mGLView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        status = renderer.tapHandler(PrimeOriginX, PrimeOriginY);
                    }
                });
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                // initiate the distance between two fingers
                if (motionEvent.getPointerCount() == 2) {
                    pointersDistance = distance(motionEvent, 0, 1);
                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                return true;
            case MotionEvent.ACTION_MOVE:
                final float dx = motionEvent.getX() - primePointerX;
                final float dy = motionEvent.getY() - primePointerY;

                if (motionEvent.getPointerCount() == 2 && pointersDistance != null) {

                    if (oldDistance == null) {
                        oldDistance = pointersDistance;
                    }

                    pointersDistance = distance(motionEvent, 0, 1);
                    final float distanceDifferences = oldDistance - pointersDistance;

                    final float diffPrimX = primePointerX - motionEvent.getX(0);
                    final float diffPrimY = primePointerY - motionEvent.getY(0);
                    final float diffSecX = secondaryPointerX - motionEvent.getX(1);
                    final float diffSecY = secondaryPointerY - motionEvent.getY(1);


                    if ((Math.abs(distanceDifferences) > threshold &&
                            (diffPrimY * diffSecY) <= 0 && (diffPrimX * diffSecX) <= 0)) {

                        mGLView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                renderer.zoom((distanceDifferences) / 40);
                            }
                        });
                    }
                    oldDistance = pointersDistance;

                    primePointerX = motionEvent.getX();
                    primePointerY = motionEvent.getY();
                    secondaryPointerX = motionEvent.getX(1);
                    secondaryPointerY = motionEvent.getY(1);
                    return true;
                } else if (motionEvent.getPointerCount() == 3) {
                    threeFingerDragGesture = true;
                }

                primePointerX = motionEvent.getX();
                primePointerY = motionEvent.getY();
                mGLView.queueEvent(new Runnable() {
                    @Override
                    public void run() {


                        if (threeFingerDragGesture) {
                            renderer.slideCamera(dx, dy);
                            threeFingerDragGesture = false;
                            return;
                        }
                        if (status == SAME_OBJECT) {
                            renderer.panObjectBy(primePointerX, primePointerY);
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
                oldDistance = null;
                pointersDistance = null;

                if ((Math.abs(PrimeOriginX - motionEvent.getX()) < threshold) && (Math.abs(PrimeOriginY - motionEvent.getY()) < threshold)) {
                    mGLView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            renderer.handleObjectMenu(status);
                            if (status == NO_OBJECT) {
                                renderer.handleCreatingNewObject(PrimeOriginX, PrimeOriginY);
                            }
                        }
                    });
                }

                mGLView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        renderer.toggleGrid(false);
                    }
                });
                Undo.setRecordMode(true);
                return true;
        }
        return false;
    }

    private float distance(MotionEvent event, int first, int second) {
        if (event.getPointerCount() >= 2) {
            final float x = event.getX(first) - event.getX(second);
            final float y = event.getY(first) - event.getY(second);

            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }


}

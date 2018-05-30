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
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;


import static com.example.roi.a3Design.MyRenderer.TapStatus.SAME_OBJECT;
import static com.example.roi.a3Design.MyRenderer.TapStatus.VERTICAL_MENU;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private GLSurfaceView mGLView;
    private MyRenderer renderer = null;

    private float lastX, lastY, originX, originY;
    private MyRenderer.TapStatus status;

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE); // (NEW)
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        ObjectManager.initializeObjects(this);

        setContentView(R.layout.activity_main);





        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);


//        //         get the listview
//        expListView = findViewById(R.id.lvExp);
//        if (expListView == null) {
//            Log.d("list", "it is null..");
//        }
//        // preparing list data
//        prepareListData();
//
//        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
//
//
//        // setting list adapter
//        expListView.setAdapter(listAdapter);

        mGLView = this.findViewById(R.id.glSurface);


//        mGLView = new GLSurfaceView(getApplication());
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
//        setContentView(mGLView);
        mGLView.setOnTouchListener(this);

    }


    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Living Room");
        listDataHeader.add("Bedroom");
        listDataHeader.add("Bathroom");
        listDataHeader.add("Kitchen");

        // Adding child data
        List<String> LivingRoom = new ArrayList<String>();
        LivingRoom.add("The Shawshank Redemption");
        LivingRoom.add("The Godfather");
        LivingRoom.add("The Godfather: Part II");
        LivingRoom.add("Pulp Fiction");
        LivingRoom.add("The Good, the Bad and the Ugly");
        LivingRoom.add("The Dark Knight");
        LivingRoom.add("12 Angry Men");

        List<String> bedroom = new ArrayList<String>();
        bedroom.add("The Conjuring");
        bedroom.add("Despicable Me 2");
        bedroom.add("Turbo");
        bedroom.add("Grown Ups 2");
        bedroom.add("Red 2");
        bedroom.add("The Wolverine");

        List<String> bathroom = new ArrayList<String>();
        bathroom.add("2 Guns");
        bathroom.add("The Smurfs 2");
        bathroom.add("The Spectacular Now");
        bathroom.add("The Canyons");
        bathroom.add("Europa Report");

        listDataChild.put(listDataHeader.get(0), LivingRoom); // Header, Child data
        listDataChild.put(listDataHeader.get(1), bedroom);
        listDataChild.put(listDataHeader.get(2), bathroom);
//        listDataChild.put(listDataHeader.get(3), kitchen);
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
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

            case MotionEvent.ACTION_MOVE:
                final float dx = motionEvent.getX() - lastX;
                final float dy = motionEvent.getY() - lastY;
                lastX = motionEvent.getX();
                lastY = motionEvent.getY();
                //Log.d("event", "Move event");

                mGLView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if (status == SAME_OBJECT) {
                            renderer.panObjectBy(lastX, lastY);
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
                if ((Math.abs(originX - motionEvent.getX()) < 0.3) && (Math.abs(originY - motionEvent.getY()) < 0.3)) {
                    mGLView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            renderer.handleObjectMenu(status);

                        }
                    });
                }
                Undo.setRecordMode(true);
                return true;
        }
        return false;
    }
}

package com.example.roi.a3Design;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.util.ExtendedPrimitives;

/**
 * Created by Roi on 10/04/2018.
 */

public class Floor {

    Object3D floor = null;

    Floor(SimpleVector dimentions) {
        floor = ExtendedPrimitives.createBox(dimentions);
//        floor.setTexture("floor");
        floor.setName("floor");
        floor.rotateX((float) Math.toRadians(90));
        floor.translate(0,0.02f,0);
    }

    Object3D getFloorAsObj3D() {
        return floor;
    }
}

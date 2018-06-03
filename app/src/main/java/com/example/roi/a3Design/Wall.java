package com.example.roi.a3Design;

import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.util.ExtendedPrimitives;

/**
 * Created by Roi on 03/04/2018.
 */

public class Wall {
    private Object3D wall = null;
    private float length;
    private final static float height = 2.6f, depth = 0.2f;

    Wall(float size) {
        length = size;
        wall = ExtendedPrimitives.createBox(new SimpleVector(length, height, depth));
//        wall.setTexture("wall");
        wall.setName("wall");
    }

    Object3D getWall() {
        return this.wall;
    }

     float getLength() {
        return this.length;
    }

    static float getHeight() {
        return height;
    }

}

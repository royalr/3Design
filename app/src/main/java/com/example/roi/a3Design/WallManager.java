package com.example.roi.a3Design;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

import static java.lang.Math.*;

/**
 * Created by Roi on 04/04/2018.
 */

public class WallManager {
    private static Wall wallA, wallB, wallC, wallD;
    private static Floor floor;


    public static void registerWalls(Wall wA, Wall wB) {
        wallA = wA;
        wallB = wB;
        wallC = new Wall((wA.getLength()));
        wallD = new Wall(wB.getLength());
        positionWalls(90, 90);
        floor = createFloor();
    }

    private static void positionWalls(float degA, float degB) {
        float wallsHeight = Wall.getHeight();
        float originalWallNudgeSize = wallA.getLength() / 2,
                rotatedWallNudgeSize = wallB.getLength() / 2;

        wallA.getWall().rotateY((float) toRadians(90));
        wallC.getWall().rotateY((float) toRadians(90));

        // position the walls
        wallA.getWall().translate(-rotatedWallNudgeSize, -wallsHeight / 2, 0);
        wallB.getWall().translate(0, -wallsHeight / 2, -originalWallNudgeSize);
        wallC.getWall().translate(rotatedWallNudgeSize, -wallsHeight / 2, 0);
        wallD.getWall().translate(0, -wallsHeight / 2, originalWallNudgeSize);
    }

    public static float[] getRoomDimentions () {
        return new float[] {wallA.getLength(), wallB.getLength()};
    }

    public static Object3D[] getWallsObjects() {
        return new Object3D[]{wallA.getWall(), wallB.getWall(), wallC.getWall(), wallD.getWall()};

    }

    public static Floor getFloor() {
        return floor;
    }

    private static Floor createFloor() {
        return new Floor(new SimpleVector(wallB.getLength(), wallA.getLength(), 0.02f));
    }

}

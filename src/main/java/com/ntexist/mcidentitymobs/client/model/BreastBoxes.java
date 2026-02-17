package com.ntexist.mcidentitymobs.client.model;

public class BreastBoxes {
    public final ModelBox leftMain;
    public final ModelBox leftOverlay;
    public final ModelBox rightMain;
    public final ModelBox rightOverlay;

    public BreastBoxes() {
        leftMain = new ModelBox(64, 64,
                -2.0F, -1.0F, -2.0F, 4, 5, 3, 0.0f, false,
                new float[][] {
                        {27, 20, 30, 25},  // EAST
                        {22, 20, 25, 25},  // WEST
                        {64, 23, 60, 20},  // DOWN
                        {24, 25, 28, 28},  // UP
                        {24, 20, 28, 25},  // NORTH
                        {60, 23, 64, 28}   // SOUTH
                }
        );
        leftOverlay = new ModelBox(64, 64,
                -2.0F, -1.0F, -2.0F, 4, 5, 3, 0.1f, false,
                new float[][] {
                        {27, 36, 30, 41},  // EAST
                        {22, 36, 25, 41},  // WEST
                        {64, 39, 60, 36},  // DOWN
                        {24, 41, 28, 44},  // UP
                        {24, 36, 28, 41},  // NORTH
                        {60, 39, 64, 44}   // SOUTH
                }
        );
        rightMain = new ModelBox(64, 64,
                -2.0F, -1.0F, -2.0F, 4, 5, 3, 0.0f, false,
                new float[][] {
                        {23, 20, 26, 25},  // EAST
                        {18, 20, 21, 25},  // WEST
                        {64, 23, 60, 20},  // DOWN
                        {20, 25, 24, 28},  // UP
                        {20, 20, 24, 25},  // NORTH
                        {60, 23, 64, 28}   // SOUTH
                }
        );
        rightOverlay = new ModelBox(64, 64,
                -2.0F, -1.0F, -2.0F, 4, 5, 3, 0.1f, false,
                new float[][] {
                        {23, 36, 26, 41},  // EAST
                        {18, 36, 21, 41},  // WEST
                        {64, 39, 60, 36},  // DOWN
                        {20, 41, 28, 44},  // UP
                        {20, 36, 24, 41},  // NORTH
                        {60, 39, 64, 44}   // SOUTH
                }
        );
    }
}
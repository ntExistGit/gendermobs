package com.ntexist.mcidentitymobs.client.model;

import net.minecraft.core.Direction;

public class ModelBox {
    public final TexturedQuad[] quads;

    public static final int EAST    = 0;
    public static final int WEST    = 1;
    public static final int DOWN    = 2;
    public static final int UP      = 3;
    public static final int NORTH   = 4;
    public static final int SOUTH   = 5;

    public ModelBox(int texWidth, int texHeight,
                    float x, float y, float z,
                    int dx, int dy, int dz,
                    float delta, boolean mirror,
                    float[][] uvData) {
        this.quads = new TexturedQuad[6];

        float f = x + dx;
        float f1 = y + dy;
        float f2 = z + dz;
        x -= delta;
        y -= delta;
        z -= delta;
        f += delta;
        f1 += delta;
        f2 += delta;
        if (mirror) {
            float f3 = f;
            f = x;
            x = f3;
        }

        PositionTextureVertex[] vertices = new PositionTextureVertex[8];
        vertices[0] = new PositionTextureVertex(f, y, z, 0, 8);
        vertices[1] = new PositionTextureVertex(f, f1, z, 8, 8);
        vertices[2] = new PositionTextureVertex(x, f1, z, 8, 0);
        vertices[3] = new PositionTextureVertex(x, y, f2, 0, 0);
        vertices[4] = new PositionTextureVertex(f, y, f2, 0, 8);
        vertices[5] = new PositionTextureVertex(f, f1, f2, 8, 8);
        vertices[6] = new PositionTextureVertex(x, f1, f2, 8, 0);
        vertices[7] = new PositionTextureVertex(x, y, z, 0, 0);

        this.quads[EAST] = new TexturedQuad(
                uvData[0][0], uvData[0][1], uvData[0][2], uvData[0][3],
                texWidth, texHeight, mirror, Direction.EAST,
                vertices[4], vertices[0], vertices[1], vertices[5]
        );

        this.quads[WEST] = new TexturedQuad(
                uvData[1][0], uvData[1][1], uvData[1][2], uvData[1][3],
                texWidth, texHeight, mirror, Direction.WEST,
                vertices[7], vertices[3], vertices[6], vertices[2]
        );

        this.quads[DOWN] = new TexturedQuad(
                uvData[2][0], uvData[2][1], uvData[2][2], uvData[2][3],
                texWidth, texHeight, mirror, Direction.DOWN,
                vertices[4], vertices[3], vertices[7], vertices[0]
        );

        this.quads[UP] = new TexturedQuad(
                uvData[3][0], uvData[3][1], uvData[3][2], uvData[3][3],
                texWidth, texHeight, mirror, Direction.UP,
                vertices[1], vertices[2], vertices[6], vertices[5]
        );

        this.quads[NORTH] = new TexturedQuad(
                uvData[4][0], uvData[4][1], uvData[4][2], uvData[4][3],
                texWidth, texHeight, mirror, Direction.NORTH,
                vertices[0], vertices[7], vertices[2], vertices[1]
        );

        this.quads[SOUTH] = new TexturedQuad(
                uvData[5][0], uvData[5][1], uvData[5][2], uvData[5][3],
                texWidth, texHeight, mirror, Direction.SOUTH,
                vertices[5], vertices[6], vertices[3], vertices[4]
        );
    }
}
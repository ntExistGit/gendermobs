package com.ntexist.mcidentitymobs.client.model;

public class PositionTextureVertex {
    public final float x, y, z, u, v;
    public PositionTextureVertex(float x, float y, float z, float u, float v) {
        this.x = x; this.y = y; this.z = z; this.u = u; this.v = v;
    }
    public PositionTextureVertex withTexturePosition(float u, float v) {
        return new PositionTextureVertex(this.x, this.y, this.z, u, v);
    }
}
package com.ntexist.mcidentitymobs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TexturedQuad {
    public final PositionTextureVertex[] vertices;
    public final Vec3i normal;

    public TexturedQuad(float u1, float v1, float u2, float v2, float texWidth, float texHeight,
                        boolean mirror, Direction direction, PositionTextureVertex... vertices) {
        this.vertices = vertices;
        float f = 0.0F / texWidth;
        float f1 = 0.0F / texHeight;
        vertices[0] = vertices[0].withTexturePosition(u2 / texWidth - f, v1 / texHeight + f1);
        vertices[1] = vertices[1].withTexturePosition(u1 / texWidth + f, v1 / texHeight + f1);
        vertices[2] = vertices[2].withTexturePosition(u1 / texWidth + f, v2 / texHeight - f1);
        vertices[3] = vertices[3].withTexturePosition(u2 / texWidth - f, v2 / texHeight - f1);
        if (mirror) {
            int len = vertices.length;
            for (int j = 0; j < len / 2; j++) {
                PositionTextureVertex vertex = vertices[j];
                vertices[j] = vertices[len - 1 - j];
                vertices[len - 1 - j] = vertex;
            }
        }
        this.normal = direction.getNormal();
        if (mirror) this.normal.multiply(-1);
    }

    public void render(PoseStack.Pose pose, VertexConsumer consumer, float red, float green, float blue, float alpha,
                       int packedLight, int packedOverlay) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        Vector3f normalVec = new Vector3f((float) normal.getX(), (float) normal.getY(), (float) normal.getZ());
        normalVec.mul(matrix3f);
        for (PositionTextureVertex vertex : vertices) {
            consumer.vertex(matrix4f, vertex.x / 16.0F, vertex.y / 16.0F, vertex.z / 16.0F);
            consumer.color(red, green, blue, alpha);
            consumer.uv(vertex.u, vertex.v);
            consumer.overlayCoords(packedOverlay);
            consumer.uv2(packedLight);
            consumer.normal(normalVec.x(), normalVec.y(), normalVec.z());
            consumer.endVertex();
        }
    }
}
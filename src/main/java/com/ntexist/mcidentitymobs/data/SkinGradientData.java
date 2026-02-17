package com.ntexist.mcidentitymobs.data;

import net.minecraft.resources.ResourceLocation;

public class SkinGradientData {
    private final ResourceLocation gradientTexture;
    private final int toneCount;

    public SkinGradientData(ResourceLocation gradientTexture, int toneCount) {
        this.gradientTexture = gradientTexture;
        this.toneCount = toneCount;
    }

    public ResourceLocation getGradientTexture() {
        return gradientTexture;
    }

    public int getToneCount() {
        return toneCount;
    }
}
package com.ntexist.mcidentitymobs.data;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Predicate;

public class SkinGradientRule {
    private final Predicate<Holder<Biome>> predicate;
    private final ResourceLocation gradientTexture;
    private final int toneCount;

    public SkinGradientRule(Predicate<Holder<Biome>> predicate, ResourceLocation gradientTexture, int toneCount) {
        this.predicate = predicate;
        this.gradientTexture = gradientTexture;
        this.toneCount = toneCount;
    }

    public boolean matches(Holder<Biome> biomeHolder) {
        return predicate.test(biomeHolder);
    }

    public ResourceLocation getGradientTexture() { return gradientTexture; }
    public int getToneCount() { return toneCount; }
}
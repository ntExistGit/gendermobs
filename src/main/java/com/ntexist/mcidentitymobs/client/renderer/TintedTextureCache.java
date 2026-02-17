package com.ntexist.mcidentitymobs.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TintedTextureCache {

    private static final Map<String, ResourceLocation> CACHE = new HashMap<>();

    public static ResourceLocation getOrCreate(ResourceLocation grayscaleTex,
                                               ResourceLocation gradientTex,
                                               int toneIndex) {

        String key = buildKey(grayscaleTex, gradientTex, toneIndex);
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }

        NativeImage grayscale = loadImage(grayscaleTex);
        NativeImage gradient = loadImage(gradientTex);

        if (grayscale == null || gradient == null) {
            return null;
        }

        if (toneIndex < 0 || toneIndex >= gradient.getHeight()) {
            toneIndex = 0;
        }

        NativeImage tinted = new NativeImage(grayscale.getWidth(), grayscale.getHeight(), false);

        for (int y = 0; y < grayscale.getHeight(); y++) {
            for (int x = 0; x < grayscale.getWidth(); x++) {

                int gray = grayscale.getPixelRGBA(x, y);
                int alpha = (gray >>> 24) & 0xFF;

                if (alpha == 0) {
                    tinted.setPixelRGBA(x, y, 0x00000000);
                    continue;
                }

                int rGray = (gray >>> 16) & 0xFF;
                int gGray = (gray >>> 8) & 0xFF;
                int bGray = gray & 0xFF;

                int brightness = (rGray + gGray + bGray) / 3;
                int gradientX = brightness * (gradient.getWidth() - 1) / 255;

                int gradColor = gradient.getPixelRGBA(gradientX, toneIndex);
                int r = (gradColor >>> 16) & 0xFF;
                int g = (gradColor >>> 8) & 0xFF;
                int b = gradColor & 0xFF;

                int resultColor = (alpha << 24) | (r << 16) | (g << 8) | b;
                tinted.setPixelRGBA(x, y, resultColor);
            }
        }

        ResourceLocation newTex = ResourceLocation.fromNamespaceAndPath(
                "mcidentitymobs",
                "dynamic/tinted/" + key
        );

        DynamicTexture dyn = new DynamicTexture(tinted);
        dyn.upload();
        Minecraft.getInstance().getTextureManager().register(newTex, dyn);

        tinted.close();
        grayscale.close();
        gradient.close();

        CACHE.put(key, newTex);
        return newTex;
    }

    public static ResourceLocation getColoredFromPalette(ResourceLocation grayscaleTex,
                                                         ResourceLocation paletteTex,
                                                         int u, int v) {

        String key = buildKey(grayscaleTex, paletteTex, u, v);
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }

        NativeImage grayscale = loadImage(grayscaleTex);
        NativeImage palette = loadImage(paletteTex);

        if (grayscale == null || palette == null) {
            return null;
        }

        u = Math.min(u, palette.getWidth() - 1);
        v = Math.min(v, palette.getHeight() - 1);

        int color = palette.getPixelRGBA(u, v);
        int r = (color >>> 16) & 0xFF;
        int g = (color >>> 8) & 0xFF;
        int b = color & 0xFF;

        NativeImage tinted = new NativeImage(grayscale.getWidth(), grayscale.getHeight(), false);

        for (int y = 0; y < grayscale.getHeight(); y++) {
            for (int x = 0; x < grayscale.getWidth(); x++) {

                int gray = grayscale.getPixelRGBA(x, y);
                int alpha = (gray >>> 24) & 0xFF;

                if (alpha == 0) {
                    tinted.setPixelRGBA(x, y, 0x00000000);
                    continue;
                }

                int rGray = (gray >>> 16) & 0xFF;
                int gGray = (gray >>> 8) & 0xFF;
                int bGray = gray & 0xFF;

                int brightness = (rGray + gGray + bGray) / 3;

                int newR = (brightness * r) / 255;
                int newG = (brightness * g) / 255;
                int newB = (brightness * b) / 255;

                int result = (alpha << 24) | (newR << 16) | (newG << 8) | newB;
                tinted.setPixelRGBA(x, y, result);
            }
        }

        ResourceLocation newTex = ResourceLocation.fromNamespaceAndPath(
                "mcidentitymobs",
                "dynamic/colored/" + key
        );

        DynamicTexture dyn = new DynamicTexture(tinted);
        dyn.upload();
        Minecraft.getInstance().getTextureManager().register(newTex, dyn);

        tinted.close();
        grayscale.close();
        palette.close();

        CACHE.put(key, newTex);
        return newTex;
    }

    private static NativeImage loadImage(ResourceLocation location) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        Optional<Resource> resource = rm.getResource(location);

        if (resource.isEmpty()) {
            return null;
        }

        try (var input = resource.get().open()) {
            return NativeImage.read(input);
        } catch (IOException e) {
            return null;
        }
    }

    private static String buildKey(ResourceLocation a, ResourceLocation b, int... extra) {
        StringBuilder sb = new StringBuilder();

        sb.append(a.getNamespace())
                .append("_")
                .append(a.getPath().replace('/', '_'))
                .append("_")
                .append(b.getNamespace())
                .append("_")
                .append(b.getPath().replace('/', '_'));

        for (int i : extra) {
            sb.append("_").append(i);
        }

        return sb.toString();
    }

    public static void clearCache() {
        CACHE.clear();
    }
}

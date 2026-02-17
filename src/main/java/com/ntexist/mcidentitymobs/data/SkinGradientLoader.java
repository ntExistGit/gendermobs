package com.ntexist.mcidentitymobs.data;

import com.google.gson.*;
import com.ntexist.mcidentitymobs.client.renderer.TintedTextureCache;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class SkinGradientLoader extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FOLDER = "villager_skin_gradients";

    private static final List<SkinGradientRule> RULES = new ArrayList<>();

    public SkinGradientLoader() {
        super(GSON, FOLDER);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager resourceManager, ProfilerFiller profiler) {
        TintedTextureCache.clearCache();
        RULES.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : data.entrySet()) {
            try {
                JsonObject obj = entry.getValue().getAsJsonObject();

                String gradientStr = obj.get("gradient").getAsString();
                int toneCount = obj.get("toneCount").getAsInt();
                ResourceLocation gradientTex = ResourceLocation.tryParse(gradientStr);

                JsonElement targetElem = obj.get("target");
                List<Predicate<Holder<Biome>>> predicates = new ArrayList<>();

                if (targetElem.isJsonArray()) {
                    JsonArray targets = targetElem.getAsJsonArray();
                    for (JsonElement elem : targets) {
                        parseTarget(elem.getAsString(), predicates);
                    }
                } else {
                    parseTarget(targetElem.getAsString(), predicates);
                }

                if (predicates.isEmpty()) continue;

                Predicate<Holder<Biome>> combined = predicates.stream().reduce(Predicate::or).orElse(b -> false);

                RULES.add(new SkinGradientRule(combined, gradientTex, toneCount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parseTarget(String target, List<Predicate<Holder<Biome>>> predicates) {
        target = target.trim();
        if (target.isEmpty()) return;
        if (target.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.tryParse(target.substring(1));
            TagKey<Biome> tag = TagKey.create(Registries.BIOME, tagId);
            predicates.add(holder -> holder.is(tag));
        } else {
            ResourceLocation biomeId = ResourceLocation.tryParse(target);
            predicates.add(holder -> holder.unwrapKey().map(key -> key.location().equals(biomeId)).orElse(false));
        }
    }

    private static final ResourceLocation DEFAULT_GRADIENT =
            ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "textures/colormap/villager_skin.png");
    private static final int DEFAULT_TONE_COUNT = 32;

    private static final SkinGradientRule DEFAULT_RULE =
            new SkinGradientRule(biome -> true, DEFAULT_GRADIENT, DEFAULT_TONE_COUNT);

    public static SkinGradientRule getDefaultRule() {
        return DEFAULT_RULE;
    }

    public static SkinGradientRule getRuleForBiome(Holder<Biome> biome) {
        for (SkinGradientRule rule : RULES) {
            if (rule.matches(biome)) return rule;
        }
        return null;
    }
}
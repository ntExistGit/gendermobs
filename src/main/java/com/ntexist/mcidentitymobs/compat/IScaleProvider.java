package com.ntexist.mcidentitymobs.compat;

import com.ntexist.mcidentitymobs.config.ConfigManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Unique;

public interface IScaleProvider {

    @Unique
    default float mcidentitymobs$getScaleForEntity(LivingEntity entity, boolean isFemale) {
        if (!ConfigManager.CONFIG.general.femaleScale || !isFemale) {
            return 1.0F;
        }
        String key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
        return getFemaleScale(key);
    }

    @Unique
    default float getFemaleScale(String entityKey) {
        return switch (entityKey) {
            // alexsmobs
            case "alexsmobs:alligator_snapping_turtle" -> 1.08F;
            case "alexsmobs:anaconda" -> 1.2F;
            case "alexsmobs:anteater" -> 0.96F;
            case "alexsmobs:bald_eagle" -> 1.08F;
            case "alexsmobs:banana_slug" -> 1.0F;
            case "alexsmobs:bison" -> 0.88F;
            case "alexsmobs:blobfish" -> 1.0F;
            case "alexsmobs:blue_jay" -> 0.98F;
            case "alexsmobs:bone_serpent" -> 1.04F;
            case "alexsmobs:bunfungus" -> 0.96F;
            case "alexsmobs:cachalot_whale" -> 0.86F;
            case "alexsmobs:caiman" -> 0.92F;
            case "alexsmobs:capuchin_monkey" -> 0.94F;
            case "alexsmobs:catfish" -> 1.08F;
            case "alexsmobs:cockroach" -> 1.04F;
            case "alexsmobs:comb_jelly" -> 1.0F;
            case "alexsmobs:cosmic_cod" -> 1.08F;
            case "alexsmobs:cosmaw" -> 0.96F;
            case "alexsmobs:crimson_mosquito" -> 1.08F;
            case "alexsmobs:crocodile" -> 0.94F;
            case "alexsmobs:crow" -> 0.98F;
            case "alexsmobs:devils_hole_pupfish" -> 1.04F;
            case "alexsmobs:dropbear" -> 0.92F;
            case "alexsmobs:elephant" -> 0.9F;
            case "alexsmobs:emu" -> 1.04F;
            case "alexsmobs:endergrade" -> 0.96F;
            case "alexsmobs:enderiophage" -> 1.0F;
            case "alexsmobs:farseer" -> 0.96F;
            case "alexsmobs:flutter" -> 1.08F;
            case "alexsmobs:fly" -> 0.98F;
            case "alexsmobs:flying_fish" -> 1.0F;
            case "alexsmobs:frilled_shark" -> 1.04F;
            case "alexsmobs:froststalker" -> 0.92F;
            case "alexsmobs:gazelle" -> 0.92F;
            case "alexsmobs:gelada_monkey" -> 0.88F;
            case "alexsmobs:giant_squid" -> 1.12F;
            case "alexsmobs:grizzly_bear" -> 0.9F;
            case "alexsmobs:guster" -> 1.0F;
            case "alexsmobs:hammerhead_shark" -> 1.08F;
            case "alexsmobs:hummingbird" -> 1.04F;
            case "alexsmobs:jerboa" -> 0.96F;
            case "alexsmobs:kangaroo" -> 0.88F;
            case "alexsmobs:komodo_dragon" -> 0.88F;
            case "alexsmobs:laviathan" -> 1.04F;
            case "alexsmobs:leafcutter_ant" -> 1.0F;
            case "alexsmobs:lobster" -> 1.02F;
            case "alexsmobs:maned_wolf" -> 0.94F;
            case "alexsmobs:mantis_shrimp" -> 1.08F;
            case "alexsmobs:mimic_octopus" -> 1.04F;
            case "alexsmobs:mimicube" -> 1.0F;
            case "alexsmobs:moose" -> 0.94F;
            case "alexsmobs:mudskipper" -> 1.04F;
            case "alexsmobs:mungus" -> 0.94F;
            case "alexsmobs:murmur" -> 1.0F;
            case "alexsmobs:orca" -> 0.92F;
            case "alexsmobs:platypus" -> 0.96F;
            case "alexsmobs:potoo" -> 0.96F;
            case "alexsmobs:raccoon" -> 0.96F;
            case "alexsmobs:rain_frog" -> 1.08F;
            case "alexsmobs:rattlesnake" -> 1.04F;
            case "alexsmobs:rhinoceros" -> 0.92F;
            case "alexsmobs:roadrunner" -> 0.96F;
            case "alexsmobs:rocky_roller" -> 1.0F;
            case "alexsmobs:sea_bear" -> 0.88F;
            case "alexsmobs:seagull" -> 0.96F;
            case "alexsmobs:seal" -> 0.92F;
            case "alexsmobs:shoebill" -> 0.96F;
            case "alexsmobs:skelewag" -> 1.0F;
            case "alexsmobs:skreecher" -> 0.96F;
            case "alexsmobs:skunk" -> 0.92F;
            case "alexsmobs:snow_leopard" -> 0.92F;
            case "alexsmobs:soul_vulture" -> 1.04F;
            case "alexsmobs:spectre" -> 1.0F;
            case "alexsmobs:straddler" -> 1.0F;
            case "alexsmobs:stradpole" -> 1.0F;
            case "alexsmobs:sugar_glider" -> 0.96F;
            case "alexsmobs:sunbird" -> 0.96F;
            case "alexsmobs:tarantula_hawk" -> 1.08F;
            case "alexsmobs:tasmanian_devil" -> 0.94F;
            case "alexsmobs:terrapin" -> 1.08F;
            case "alexsmobs:tiger" -> 0.92F;
            case "alexsmobs:toucan" -> 0.96F;
            case "alexsmobs:triops" -> 1.0F;
            case "alexsmobs:tusklin" -> 0.9F;
            case "alexsmobs:underminer" -> 1.0F;
            case "alexsmobs:void_worm" -> 1.12F;
            case "alexsmobs:warped_mosco" -> 1.08F;
            case "alexsmobs:warped_toad" -> 1.08F;
            default -> 1.0F;
        };
    }
}
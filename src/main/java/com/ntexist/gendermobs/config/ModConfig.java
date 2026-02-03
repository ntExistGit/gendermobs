package com.ntexist.gendermobs.config;

import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModConfig {

    public General general = new General();
    public Colors colors = new Colors();

    public Map<String, EntryData> vanillaHumanoid = new HashMap<>();
    public Map<String, EntryData> customHumanoid = new HashMap<>();
    public Map<String, EntryData> vanillaNonHumanoid = new HashMap<>();
    public Map<String, EntryData> customNonHumanoid = new HashMap<>();
    public Set<String> zombies = new HashSet<>();
    public Set<String> canBeInfected = new HashSet<>();

    public static ModConfig defaultConfig() {
        ModConfig cfg = new ModConfig();

        cfg.general.showNames = true;
        cfg.general.showColors = false;

        cfg.general.jadeIcons = true;

        cfg.colors.male = "#5555FF";
        cfg.colors.female = "#FF55FF";

        cfg.general.useDefaultNames = true;

        // Vanilla Humanoid
        cfg.vanillaHumanoid.put("minecraft:villager", new EntryData(0.5f, true));
        cfg.vanillaHumanoid.put("minecraft:wandering_trader", new EntryData(0.5f, true));
        cfg.vanillaHumanoid.put("minecraft:witch", new EntryData(1.0f, true));
        cfg.vanillaHumanoid.put("minecraft:pillager", new EntryData(0.1f, true));
        cfg.vanillaHumanoid.put("minecraft:vindicator", new EntryData(0.1f, true));
        cfg.vanillaHumanoid.put("minecraft:evoker", new EntryData(0.3f, true));
        cfg.vanillaHumanoid.put("minecraft:illusioner", new EntryData(0.5f, true));
        cfg.vanillaHumanoid.put("minecraft:zombie", new EntryData(0.5f, false));
        cfg.vanillaHumanoid.put("minecraft:zombie_villager", new EntryData(0.5f, false));
        cfg.vanillaHumanoid.put("minecraft:husk", new EntryData(0.5f, false));
        cfg.vanillaHumanoid.put("minecraft:drowned", new EntryData(0.5f, false));
        cfg.vanillaHumanoid.put("minecraft:zombified_piglin", new EntryData(0.5f, false));

        // Farm animals (more females for eggs/milk/population)
        cfg.vanillaNonHumanoid.put("minecraft:chicken", new EntryData(0.8f, false));
        cfg.vanillaNonHumanoid.put("minecraft:cow", new EntryData(0.7f, false));
        cfg.vanillaNonHumanoid.put("minecraft:sheep", new EntryData(0.6f, false));
        cfg.vanillaNonHumanoid.put("minecraft:pig", new EntryData(0.6f, false));
        cfg.vanillaNonHumanoid.put("minecraft:goat", new EntryData(0.6f, false));

        // Bees (90% worker females, 10% drones)
        cfg.vanillaNonHumanoid.put("minecraft:bee", new EntryData(0.9f, false));

        // Arachnids (females dominate in nature)
        cfg.vanillaNonHumanoid.put("minecraft:spider", new EntryData(0.7f, false));
        cfg.vanillaNonHumanoid.put("minecraft:cave_spider", new EntryData(0.7f, false));

        // Wild predators and loners (50/50 balance)
        cfg.vanillaNonHumanoid.put("minecraft:wolf", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:fox", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:ocelot", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:cat", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:polar_bear", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:panda", new EntryData(0.5f, false));

        // Horses and camels
        cfg.vanillaNonHumanoid.put("minecraft:horse", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:donkey", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:mule", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:camel", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:llama", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:trader_llama", new EntryData(0.5f, false));

        // Aquatic creatures, fish, and amphibians
        cfg.vanillaNonHumanoid.put("minecraft:axolotl", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:frog", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:tadpole", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:turtle", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:dolphin", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:cod", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:salmon", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:pufferfish", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:tropical_fish", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:squid", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:glow_squid", new EntryData(0.5f, false));

        // Other
        cfg.vanillaNonHumanoid.put("minecraft:rabbit", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:parrot", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:bat", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:hoglin", new EntryData(0.5f, false));
        cfg.vanillaNonHumanoid.put("minecraft:sniffer", new EntryData(0.5f, false));

        // Zombies
        cfg.zombies.add("minecraft:zombie");
        cfg.zombies.add("minecraft:husk");
        cfg.zombies.add("minecraft:drowned");
        cfg.zombies.add("minecraft:zombie_villager");
        cfg.zombies.add("minecraft:zombified_piglin");

        // Can Be Infected
        cfg.canBeInfected.add("minecraft:villager");
        cfg.canBeInfected.add("minecraft:wandering_trader");
        cfg.canBeInfected.add("minecraft:pillager");

        // Guard Villagers
        if (ModList.get().isLoaded("guardvillagers")) {
            cfg.customHumanoid.put("guardvillagers:guard", new EntryData(0.2f, true));
            cfg.canBeInfected.add("guardvillagers:guard");
        }

        // Alex's Mobs
        if (ModList.get().isLoaded("alexsmobs")) {

            // Mammals
            cfg.customNonHumanoid.put("alexsmobs:grizzly_bear", new EntryData(0.45f, false));
            cfg.customNonHumanoid.put("alexsmobs:gorilla", new EntryData(0.65f, false));
            cfg.customNonHumanoid.put("alexsmobs:capuchin_monkey", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("alexsmobs:gelada_monkey", new EntryData(0.70f, false));
            cfg.customNonHumanoid.put("alexsmobs:snow_leopard", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:tiger", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:elephant", new EntryData(0.65f, false));
            cfg.customNonHumanoid.put("alexsmobs:gazelle", new EntryData(0.70f, false));
            cfg.customNonHumanoid.put("alexsmobs:moose", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:bison", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("alexsmobs:rhinoceros", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:kangaroo", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:tasmanian_devil", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:raccoon", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("alexsmobs:jerboa", new EntryData(0.65f, false));
            cfg.customNonHumanoid.put("alexsmobs:skunk", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:platypus", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:orca", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:seal", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:cachalot_whale", new EntryData(0.70f, false));
            cfg.customNonHumanoid.put("alexsmobs:sea_bear", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:dropbear", new EntryData(0.45f, false)); // Mythical Australian predator
            cfg.customNonHumanoid.put("alexsmobs:anteater", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:maned_wolf", new EntryData(0.55f, false));

            // Birds
            cfg.customNonHumanoid.put("alexsmobs:hummingbird", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("alexsmobs:emu", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("alexsmobs:bald_eagle", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:soul_vulture", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:shoebill", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:seagull", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:toucan", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:blue_jay", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:potoo", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:roadrunner", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:crow", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:sunbird", new EntryData(0.60f, false));

            // Reptiles
            cfg.customNonHumanoid.put("alexsmobs:crocodile", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:caiman", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:komodo_dragon", new EntryData(0.45f, false));
            cfg.customNonHumanoid.put("alexsmobs:alligator_snapping_turtle", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:rattlesnake", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:anaconda", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("alexsmobs:terrapin", new EntryData(0.55f, false));

            // Fish
            cfg.customNonHumanoid.put("alexsmobs:hammerhead_shark", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("alexsmobs:catfish", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:blobfish", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:flying_fish", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:devils_hole_pupfish", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:lobster", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:giant_squid", new EntryData(0.45f, false));
            cfg.customNonHumanoid.put("alexsmobs:frilled_shark", new EntryData(0.55f, false)); // Prehistoric shark

            // Amphibians
            cfg.customNonHumanoid.put("alexsmobs:rain_frog", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("alexsmobs:warped_toad", new EntryData(0.50f, false));

            // Arthropods & Insects
            cfg.customNonHumanoid.put("alexsmobs:cockroach", new EntryData(0.65f, false)); // Females produce egg cases
            cfg.customNonHumanoid.put("alexsmobs:fly", new EntryData(0.60f, false)); // Females lay eggs in organic matter
            cfg.customNonHumanoid.put("alexsmobs:leafcutter_ant", new EntryData(0.95f, false)); // Colony: queen + sterile female workers
            cfg.customNonHumanoid.put("alexsmobs:mantis_shrimp", new EntryData(0.50f, false)); // Marine crustaceans, equal ratio
            cfg.customNonHumanoid.put("alexsmobs:tarantula_hawk", new EntryData(0.75f, false));
            cfg.customNonHumanoid.put("alexsmobs:crimson_mosquito", new EntryData(1.00f, false)); // Only females drink blood for eggs

            // Mollusks & Cephalopods
            cfg.customNonHumanoid.put("alexsmobs:mimic_octopus", new EntryData(0.50f, false)); // Males die after mating

            // Other Aquatic
            cfg.customNonHumanoid.put("alexsmobs:mudskipper", new EntryData(0.55f, false)); // Amphibious fish

            // Fantasy With Real-world Analogues
            cfg.customNonHumanoid.put("alexsmobs:laviathan", new EntryData(0.45f, false)); // Lava whale, similar to cetaceans
            cfg.customNonHumanoid.put("alexsmobs:straddler", new EntryData(0.50f, false)); // Lava creature
            cfg.customNonHumanoid.put("alexsmobs:stradpole", new EntryData(0.50f, false)); // Lava tadpole
            cfg.customNonHumanoid.put("alexsmobs:cosmaw", new EntryData(0.50f, false)); // Cosmic creature with terrestrial traits
            cfg.customNonHumanoid.put("alexsmobs:tusklin", new EntryData(0.60f, false)); // Mammoth-like creature
            cfg.customNonHumanoid.put("alexsmobs:froststalker", new EntryData(0.50f, false)); // Cold biome predator
            cfg.customNonHumanoid.put("alexsmobs:mungus", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("alexsmobs:bunfungus", new EntryData(0.50f, false));

            // Additional Creatures
            cfg.customNonHumanoid.put("alexsmobs:sugar_glider", new EntryData(0.55f, false));

            cfg.customHumanoid.put("alexsmobs:underminer", new EntryData(0.50f, false));
            cfg.customHumanoid.put("alexsmobs:murmur", new EntryData(0.50f, false));
        }

        // Naturalist
        if (ModList.get().isLoaded("naturalist")) {
            // Mammals
            cfg.customNonHumanoid.put("naturalist:bear", new EntryData(0.45f, false));
            cfg.customNonHumanoid.put("naturalist:deer", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("naturalist:rhino", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("naturalist:lion", new EntryData(0.75f, false));
            cfg.customNonHumanoid.put("naturalist:elephant", new EntryData(0.65f, false));
            cfg.customNonHumanoid.put("naturalist:zebra", new EntryData(0.65f, false));
            cfg.customNonHumanoid.put("naturalist:giraffe", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("naturalist:hippo", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("naturalist:boar", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("naturalist:moose", new EntryData(0.55f, false));

            // Birds
            cfg.customNonHumanoid.put("naturalist:bluejay", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("naturalist:cardinal", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("naturalist:canary", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("naturalist:robin", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("naturalist:finch", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("naturalist:sparrow", new EntryData(0.60f, false));
            cfg.customNonHumanoid.put("naturalist:vulture", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("naturalist:duck", new EntryData(0.55f, false));

            // Reptiles
            cfg.customNonHumanoid.put("naturalist:snake", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("naturalist:coral_snake", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("naturalist:rattlesnake", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("naturalist:alligator", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("naturalist:lizard", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("naturalist:tortoise", new EntryData(0.55f, false));

            // Fish
            cfg.customNonHumanoid.put("naturalist:catfish", new EntryData(0.50f, false));
            cfg.customNonHumanoid.put("naturalist:bass", new EntryData(0.50f, false));

            // Insects & Arthropods
            cfg.customNonHumanoid.put("naturalist:butterfly", new EntryData(0.55f, false));
            cfg.customNonHumanoid.put("naturalist:firefly", new EntryData(0.10f, false));
            cfg.customNonHumanoid.put("naturalist:dragonfly", new EntryData(0.50f, false));
        }

        // Ecologics
        if (ModList.get().isLoaded("ecologics")) {
            // Mammals
            cfg.customNonHumanoid.put("ecologics:squirrel", new EntryData(0.55f, false));

            // Birds
            cfg.customNonHumanoid.put("ecologics:penguin", new EntryData(0.50f, false));

            // Crustaceans
            cfg.customNonHumanoid.put("ecologics:coconut_crab", new EntryData(0.50f, false));
        }

        return cfg;
    }

    public static class General {
        public boolean showNames = true;
        public boolean showColors = false;
        public boolean jadeIcons = true;
        public boolean useDefaultNames = true;
    }

    public static class Colors {
        public String male = "#5555FF";
        public String female = "#FF55FF";
    }
}

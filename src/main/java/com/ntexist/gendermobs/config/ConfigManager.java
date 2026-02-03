package com.ntexist.gendermobs.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ntexist.gendermobs.GenderAssigner;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;

public class ConfigManager {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Path DIR;
    public static Path FILE;
    public static Path NAMES_DIR;
    public static Path DEFAULT_NAMES_FILE;
    public static Path CUSTOM_NAMES_FILE;

    public static ModConfig CONFIG;
    public static ModConfig WORKING_COPY;

    public static void init() {
        DIR = FMLPaths.CONFIGDIR.get().resolve("gendermobs");
        FILE = DIR.resolve("config.json");

        NAMES_DIR = DIR;
        DEFAULT_NAMES_FILE = NAMES_DIR.resolve("default_names.json");
        CUSTOM_NAMES_FILE = NAMES_DIR.resolve("custom_names.json");

        load();
    }

    public static void load() {
        try {
            if (!Files.exists(DIR)) {
                Files.createDirectories(DIR);
            }

            initNameFiles();

            if (!Files.exists(FILE)) {
                CONFIG = ModConfig.defaultConfig();
                save();
                NameLoader.loadNames();
                return;
            }

            String json = Files.readString(FILE);
            CONFIG = GSON.fromJson(json, ModConfig.class);
            NameLoader.loadNames();

        } catch (Exception e) {
            e.printStackTrace();
            CONFIG = ModConfig.defaultConfig();
            NameLoader.loadNames();
        }
    }

    private static void initNameFiles() {
        try {
            if (!Files.exists(DEFAULT_NAMES_FILE)) {
                try (InputStream in = ConfigManager.class
                        .getResourceAsStream("/assets/gendermobs/names/default_names.json")) {
                    if (in != null) {
                        Files.copy(in, DEFAULT_NAMES_FILE);
                    }
                }
            }

            if (!Files.exists(CUSTOM_NAMES_FILE)) {
                String emptyJson = """
                {
                  "female_names": [],
                  "male_names": []
                }
                """;
                Files.writeString(CUSTOM_NAMES_FILE, emptyJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreDefaultNames() {
        try {
            Files.deleteIfExists(DEFAULT_NAMES_FILE);

            try (InputStream in = ConfigManager.class
                    .getResourceAsStream("/assets/gendermobs/names/default_names.json")) {
                if (in != null) {
                    Files.copy(in, DEFAULT_NAMES_FILE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            String json = GSON.toJson(CONFIG);
            Files.writeString(FILE, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createWorkingCopy() {
        WORKING_COPY = GSON.fromJson(
                GSON.toJson(CONFIG),
                ModConfig.class);
    }

    public static void applyWorkingCopy() {
        CONFIG = WORKING_COPY;
        save();
        NameLoader.loadNames();
        GenderAssigner.updateAllNames();
    }

    public static void resetAllConfig() {
        ModConfig defaultConfig = ModConfig.defaultConfig();

        ConfigManager.WORKING_COPY.general.showNames        = defaultConfig.general.showNames;
        ConfigManager.WORKING_COPY.general.showColors       = defaultConfig.general.showColors;

        ConfigManager.WORKING_COPY.general.jadeIcons        = defaultConfig.general.jadeIcons;
        ConfigManager.WORKING_COPY.general.offsetY          = defaultConfig.general.offsetY;

        ConfigManager.WORKING_COPY.colors.male              = defaultConfig.colors.male;
        ConfigManager.WORKING_COPY.colors.female            = defaultConfig.colors.female;

        ConfigManager.WORKING_COPY.general.useDefaultNames  = defaultConfig.general.useDefaultNames;

        ConfigManager.WORKING_COPY.vanillaHumanoid          = new HashMap<>(defaultConfig.vanillaHumanoid);
        ConfigManager.WORKING_COPY.customHumanoid           = new HashMap<>(defaultConfig.customHumanoid);
        ConfigManager.WORKING_COPY.vanillaNonHumanoid       = new HashMap<>(defaultConfig.vanillaNonHumanoid);
        ConfigManager.WORKING_COPY.customNonHumanoid        = new HashMap<>(defaultConfig.customNonHumanoid);

        ConfigManager.WORKING_COPY.zombies                  = new HashSet<>(defaultConfig.zombies);
        ConfigManager.WORKING_COPY.canBeInfected            = new HashSet<>(defaultConfig.canBeInfected);
    }
}

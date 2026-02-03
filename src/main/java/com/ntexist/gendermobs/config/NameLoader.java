package com.ntexist.gendermobs.config;

import com.google.gson.reflect.TypeToken;
import net.minecraft.util.RandomSource;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NameLoader {
    private static List<String> maleNames = new ArrayList<>();
    private static List<String> femaleNames = new ArrayList<>();

    public static void loadNames() {
        try {
            var path = ConfigManager.CONFIG.general.useDefaultNames ?
                    ConfigManager.DEFAULT_NAMES_FILE : ConfigManager.CUSTOM_NAMES_FILE;

            if (!Files.exists(path)) return;

            String json = Files.readString(path);
            Map<String, List<String>> data = ConfigManager.GSON.fromJson(json,
                    new TypeToken<Map<String, List<String>>>(){}.getType());

            maleNames = data.getOrDefault("male_names", new ArrayList<>());
            femaleNames = data.getOrDefault("female_names", new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRandomName(String gender, RandomSource random) {
        List<String> list = "Male".equals(gender) ? maleNames : femaleNames;
        if (list == null || list.isEmpty()) return "";
        return list.get(random.nextInt(list.size()));
    }
}

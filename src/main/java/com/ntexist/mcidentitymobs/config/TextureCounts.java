package com.ntexist.mcidentitymobs.config;

import java.util.HashMap;
import java.util.Map;

public class TextureCounts {
    public GenderCounts male;
    public GenderCounts female;

    public static class GenderCounts {
        public int skin;
        public int face;
        public int hair;
        public ClothingCounts clothing;
    }

    public static class ClothingCounts {
        public int baby;
        public int none;
        public Map<String, Integer> byProfession = new HashMap<>();
    }
}
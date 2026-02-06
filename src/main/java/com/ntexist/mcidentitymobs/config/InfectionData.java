package com.ntexist.mcidentitymobs.config;

public class InfectionData {
    public String zombie;
    public boolean curable;
    public String effect;
    public String item;
    public int time;

    public InfectionData(String zombie, boolean curable, String effect, String item, int time) {
        this.zombie = zombie;
        this.curable = curable;
        this.effect = effect;
        this.item = item;
        this.time = time;
    }

    public InfectionData(InfectionData other) {
        this.zombie = other.zombie;
        this.curable = other.curable;
        this.effect = other.effect;
        this.item = other.item;
        this.time = other.time;
    }

    public boolean isValid() {
        if (zombie == null || zombie.isEmpty()) {
            return false;
        }

        if (curable) {
            boolean hasEffect = effect != null && !effect.isEmpty();
            boolean hasItem = item != null && !item.isEmpty();
            return hasEffect || hasItem;
        }

        return true;
    }
}
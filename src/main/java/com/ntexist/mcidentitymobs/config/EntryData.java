package com.ntexist.mcidentitymobs.config;

public class EntryData {
    public float chance;
    public boolean force;

    public EntryData(float chance, boolean force) {
        this.chance = chance;
        this.force = force;
    }

    public EntryData(EntryData other) {
        this.chance = other.chance;
        this.force = other.force;
    }
}

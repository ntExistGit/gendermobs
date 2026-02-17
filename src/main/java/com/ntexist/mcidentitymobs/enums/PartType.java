package com.ntexist.mcidentitymobs.enums;

public enum PartType {
    SKIN("skin"),
    FACE("face"),
    CLOTH("cloth"),
    HAIR("hair");

    private final String folder;

    PartType(String folder) { this.folder = folder; }
    public String getFolder() { return folder; }
}

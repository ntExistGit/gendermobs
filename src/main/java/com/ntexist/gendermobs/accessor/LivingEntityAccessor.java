package com.ntexist.gendermobs.accessor;

import net.minecraft.nbt.CompoundTag;

public interface LivingEntityAccessor {
    void setGender(String gender);
    String getGender();
    void setMobName(String name);
    String getMobName();
    String getOriginalId();
    void setOriginalId(String id);

    void setPlayerNamed(boolean playerNamed);
    boolean isPlayerNamed();

    void setZombieSavedName(String name);
    String getZombieSavedName();

    default void saveToNBT(CompoundTag nbt) {
        if (!getGender().isEmpty()) nbt.putString("GM_Gender", getGender());
        if (!getMobName().isEmpty()) nbt.putString("GM_Name", getMobName());
        if (!getOriginalId().isEmpty()) nbt.putString("GM_OriginalId", getOriginalId());

        if (isPlayerNamed()) nbt.putBoolean("GM_PlayerNamed", true);

        String zombieName = getZombieSavedName();
        if (zombieName != null && !zombieName.isEmpty()) {
            nbt.putString("GM_ZombieSavedName", zombieName);
        }
    }

    default void loadFromNBT(CompoundTag nbt) {
        if (nbt.contains("GM_Gender")) setGender(nbt.getString("GM_Gender"));
        if (nbt.contains("GM_Name")) setMobName(nbt.getString("GM_Name"));
        if (nbt.contains("GM_OriginalId")) setOriginalId(nbt.getString("GM_OriginalId"));
        if (nbt.contains("GM_PlayerNamed")) setPlayerNamed(nbt.getBoolean("GM_PlayerNamed"));
        if (nbt.contains("GM_ZombieSavedName")) setZombieSavedName(nbt.getString("GM_ZombieSavedName"));
    }
}
package com.ntexist.mcidentitymobs.accessor;

import net.minecraft.nbt.CompoundTag;

public interface LivingEntityAccessor {
    // gender
    void mcidentitymobs$setGender(String gender);
    String mcidentitymobs$getGender();

    // name
    void mcidentitymobs$setMobName(String name);
    String mcidentitymobs$getMobName();

    // original id
    void mcidentitymobs$setOriginalId(String id);
    String mcidentitymobs$getOriginalId();

    // player named
    void mcidentitymobs$setPlayerNamed(boolean val);
    boolean mcidentitymobs$isPlayerNamed();

    // zombie
    void mcidentitymobs$setZombieSavedName(String name);
    String mcidentitymobs$getZombieSavedName();

    // shaking / conversion
    void mcidentitymobs$setInConversion(boolean inConversion);
    boolean mcidentitymobs$isInConversion();

    // boost
    void mcidentitymobs$setConversionTime(int time);
    int mcidentitymobs$getConversionTime();

    // storage
    void mcidentitymobs$saveToNBT(CompoundTag nbt);
    void mcidentitymobs$loadFromNBT(CompoundTag nbt);
}

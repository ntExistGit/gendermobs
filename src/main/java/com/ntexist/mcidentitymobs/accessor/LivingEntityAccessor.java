package com.ntexist.mcidentitymobs.accessor;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.UUID;

public interface LivingEntityAccessor {

    @Nullable
    UUID mcidentitymobs$getCuringPlayerUUID();
    void mcidentitymobs$setCuringPlayerUUID(@Nullable UUID uuid);

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

    // layers
    CompoundTag mcidentitymobs$getLayerSettings();
    void mcidentitymobs$setLayerSettings(CompoundTag tag);

    int mcidentitymobs$getSkinIndex();
    void mcidentitymobs$setSkinIndex(int value);

    int mcidentitymobs$getFaceIndex();
    void mcidentitymobs$setFaceIndex(int value);

    int mcidentitymobs$getClothIndex();
    void mcidentitymobs$setClothIndex(int value);

    int mcidentitymobs$getHairIndex();
    void mcidentitymobs$setHairIndex(int value);

    byte mcidentitymobs$getSkinToneIndex();
    void mcidentitymobs$setSkinToneIndex(byte value);

    byte mcidentitymobs$getHairColorU();
    void mcidentitymobs$setHairColorU(byte value);

    byte mcidentitymobs$getHairColorV();
    void mcidentitymobs$setHairColorV(byte value);

    // Breast physics
    float mcidentitymobs$getBreastSize();
    void mcidentitymobs$setBreastSize(float size);

    float mcidentitymobs$getBreastOffsetX();
    void mcidentitymobs$setBreastOffsetX(float xOffset);

    float mcidentitymobs$getBreastOffsetY();
    void mcidentitymobs$setBreastOffsetY(float yOffset);

    float mcidentitymobs$getBreastOffsetZ();
    void mcidentitymobs$setBreastOffsetZ(float zOffset);

    float mcidentitymobs$getBreastCleavage();
    void mcidentitymobs$setBreastCleavage(float cleavage);
}

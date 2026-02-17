package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityDataMixin implements LivingEntityAccessor {

    @Unique
    private UUID mcidentitymobs$curingPlayerUUID;

    @Unique
    private static final EntityDataAccessor<String> DATA_GENDER =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.STRING);
    @Unique
    private static final EntityDataAccessor<String> DATA_MOB_NAME =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.STRING);
    @Unique
    private static final EntityDataAccessor<String> DATA_ORIGINAL_ID =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.STRING);
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_PLAYER_NAMED =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<String> DATA_ZOMBIE_SAVED =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.STRING);
    @Unique
    private static final EntityDataAccessor<Integer> DATA_CONVERSION_TIME =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_IN_CONVERSION =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private static final EntityDataAccessor<CompoundTag> DATA_LAYER_SETTINGS =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.COMPOUND_TAG);

    @Unique
    private SynchedEntityData mcidentitymobs$getEntityData() {
        return ((Entity) (Object) this).getEntityData();
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineAllData(CallbackInfo ci) {

        this.mcidentitymobs$getEntityData().define(DATA_GENDER, "");
        this.mcidentitymobs$getEntityData().define(DATA_MOB_NAME, "");
        this.mcidentitymobs$getEntityData().define(DATA_ORIGINAL_ID, "");
        this.mcidentitymobs$getEntityData().define(DATA_PLAYER_NAMED, false);
        this.mcidentitymobs$getEntityData().define(DATA_ZOMBIE_SAVED, "");
        this.mcidentitymobs$getEntityData().define(DATA_CONVERSION_TIME, -1);
        this.mcidentitymobs$getEntityData().define(DATA_IN_CONVERSION, false);

        this.mcidentitymobs$getEntityData().define(DATA_LAYER_SETTINGS, new CompoundTag());
    }

    @Override
    @Nullable
    public UUID mcidentitymobs$getCuringPlayerUUID() {
        return this.mcidentitymobs$curingPlayerUUID;
    }

    @Override
    public void mcidentitymobs$setCuringPlayerUUID(@Nullable UUID uuid) {
        this.mcidentitymobs$curingPlayerUUID = uuid;
    }

    @Override
    public void mcidentitymobs$setGender(String gender) {
        this.mcidentitymobs$getEntityData().set(DATA_GENDER, gender != null ? gender : "");
    }
    @Override
    public String mcidentitymobs$getGender() {
        return this.mcidentitymobs$getEntityData().get(DATA_GENDER);
    }

    @Override
    public void mcidentitymobs$setMobName(String name) {
        this.mcidentitymobs$getEntityData().set(DATA_MOB_NAME, name != null ? name : "");
    }
    @Override
    public String mcidentitymobs$getMobName() {
        return this.mcidentitymobs$getEntityData().get(DATA_MOB_NAME);
    }

    @Override
    public void mcidentitymobs$setOriginalId(String id) {
        this.mcidentitymobs$getEntityData().set(DATA_ORIGINAL_ID, id != null ? id : "");
    }
    @Override
    public String mcidentitymobs$getOriginalId() {
        return this.mcidentitymobs$getEntityData().get(DATA_ORIGINAL_ID);
    }

    @Override
    public void mcidentitymobs$setPlayerNamed(boolean val) {
        this.mcidentitymobs$getEntityData().set(DATA_PLAYER_NAMED, val);
    }
    @Override
    public boolean mcidentitymobs$isPlayerNamed() {
        return this.mcidentitymobs$getEntityData().get(DATA_PLAYER_NAMED);
    }

    @Override
    public void mcidentitymobs$setZombieSavedName(String name) {
        this.mcidentitymobs$getEntityData().set(DATA_ZOMBIE_SAVED, name != null ? name : "");
    }
    @Override
    public String mcidentitymobs$getZombieSavedName() {
        return this.mcidentitymobs$getEntityData().get(DATA_ZOMBIE_SAVED);
    }

    @Override
    public void mcidentitymobs$setConversionTime(int time) {
        this.mcidentitymobs$getEntityData().set(DATA_CONVERSION_TIME, time);
    }
    @Override
    public int mcidentitymobs$getConversionTime() {
        return this.mcidentitymobs$getEntityData().get(DATA_CONVERSION_TIME);
    }

    @Override
    public void mcidentitymobs$setInConversion(boolean inConversion) {
        this.mcidentitymobs$getEntityData().set(DATA_IN_CONVERSION, inConversion);
    }
    @Override
    public boolean mcidentitymobs$isInConversion() {
        return this.mcidentitymobs$getEntityData().get(DATA_IN_CONVERSION);
    }

    @Override
    public CompoundTag mcidentitymobs$getLayerSettings() {
        return this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS).copy();
    }

    @Override
    public void mcidentitymobs$setLayerSettings(CompoundTag tag) {
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, tag.copy());
    }

    @Override
    public int mcidentitymobs$getSkinIndex() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.getInt("skin_index");
    }

    @Override
    public void mcidentitymobs$setSkinIndex(int value) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putInt("skin_index", value);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Override
    public int mcidentitymobs$getFaceIndex() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.getInt("face_index");
    }

    @Override
    public void mcidentitymobs$setFaceIndex(int value) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putInt("face_index", value);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Override
    public int mcidentitymobs$getClothIndex() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.getInt("cloth_index");
    }

    @Override
    public void mcidentitymobs$setClothIndex(int value) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putInt("cloth_index", value);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Override
    public int mcidentitymobs$getHairIndex() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.getInt("hair_index");
    }

    @Override
    public void mcidentitymobs$setHairIndex(int value) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putInt("hair_index", value);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Override
    public byte mcidentitymobs$getSkinToneIndex() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.contains("skin_tone", 1) ? tag.getByte("skin_tone") : (byte) -1;
    }

    @Override
    public void mcidentitymobs$setSkinToneIndex(byte value) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putByte("skin_tone", value);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Override
    public byte mcidentitymobs$getHairColorU() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.contains("hair_u", 1) ? tag.getByte("hair_u") : (byte) -1;
    }

    @Override
    public void mcidentitymobs$setHairColorU(byte value) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putByte("hair_u", value);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Override
    public byte mcidentitymobs$getHairColorV() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.contains("hair_v", 1) ? tag.getByte("hair_v") : (byte) -1;
    }

    @Override
    public void mcidentitymobs$setHairColorV(byte value) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putByte("hair_v", value);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Override
    public float mcidentitymobs$getBreastSize() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.contains("breast_size", Tag.TAG_FLOAT) ? tag.getFloat("breast_size") : 0.0f;
    }

    @Override
    public void mcidentitymobs$setBreastSize(float size) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putFloat("breast_size", size);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Override
    public float mcidentitymobs$getBreastOffsetX() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.contains("breasts_OffsetX", Tag.TAG_FLOAT) ? tag.getFloat("breasts_OffsetX") : 0.0f;
    }

    @Override
    public void mcidentitymobs$setBreastOffsetX(float xOffset) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putFloat("breasts_OffsetX", xOffset);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Override
    public float mcidentitymobs$getBreastOffsetY() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.contains("breasts_OffsetY", Tag.TAG_FLOAT) ? tag.getFloat("breasts_OffsetY") : 0.0f;
    }

    @Override
    public void mcidentitymobs$setBreastOffsetY(float yOffset) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putFloat("breasts_OffsetY", yOffset);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Override
    public float mcidentitymobs$getBreastOffsetZ() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.contains("breasts_OffsetZ", Tag.TAG_FLOAT) ? tag.getFloat("breasts_OffsetZ") : 0.0f;
    }

    @Override
    public void mcidentitymobs$setBreastOffsetZ(float zOffset) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putFloat("breasts_OffsetZ", zOffset);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Override
    public float mcidentitymobs$getBreastCleavage() {
        CompoundTag tag = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        return tag.contains("breasts_cleavage", Tag.TAG_FLOAT) ? tag.getFloat("breasts_cleavage") : 0.0f;
    }

    @Override
    public void mcidentitymobs$setBreastCleavage(float cleavage) {
        CompoundTag old = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        CompoundTag copy = old.copy();
        copy.putFloat("breasts_cleavage", cleavage);
        this.mcidentitymobs$getEntityData().set(DATA_LAYER_SETTINGS, copy);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveAllToNBT(CompoundTag tag, CallbackInfo ci) {

        if (mcidentitymobs$curingPlayerUUID != null)
            tag.putUUID("MI_CuringPlayer", mcidentitymobs$curingPlayerUUID);

        String gender = this.mcidentitymobs$getEntityData().get(DATA_GENDER);
        if (!gender.isEmpty()) tag.putString("MI_Gender", gender);

        String mobName = this.mcidentitymobs$getEntityData().get(DATA_MOB_NAME);
        if (!mobName.isEmpty()) tag.putString("MI_Name", mobName);

        String origId = this.mcidentitymobs$getEntityData().get(DATA_ORIGINAL_ID);
        if (!origId.isEmpty()) tag.putString("MI_OriginalId", origId);

        if (this.mcidentitymobs$getEntityData().get(DATA_PLAYER_NAMED))
            tag.putBoolean("MI_PlayerNamed", true);

        String zombieName = this.mcidentitymobs$getEntityData().get(DATA_ZOMBIE_SAVED);
        if (!zombieName.isEmpty()) tag.putString("MI_ZombieSavedName", zombieName);

        int convTime = this.mcidentitymobs$getEntityData().get(DATA_CONVERSION_TIME);
        if (convTime > -1) tag.putInt("MI_ConversionTime", convTime);

        if (this.mcidentitymobs$getEntityData().get(DATA_IN_CONVERSION))
            tag.putBoolean("MI_InConversion", true);

        CompoundTag layerSettings = this.mcidentitymobs$getEntityData().get(DATA_LAYER_SETTINGS);
        if (!layerSettings.isEmpty()) {
            tag.put("MI_LayerSettings", layerSettings);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadAllFromNBT(CompoundTag tag, CallbackInfo ci) {
        SynchedEntityData data = this.mcidentitymobs$getEntityData();

        if (tag.hasUUID("MI_CuringPlayer"))
            this.mcidentitymobs$curingPlayerUUID = tag.getUUID("MI_CuringPlayer");

        if (tag.contains("MI_Gender"))          data.set(DATA_GENDER,          tag.getString("MI_Gender"));
        if (tag.contains("MI_Name"))            data.set(DATA_MOB_NAME,        tag.getString("MI_Name"));
        if (tag.contains("MI_OriginalId"))      data.set(DATA_ORIGINAL_ID,     tag.getString("MI_OriginalId"));
        if (tag.contains("MI_PlayerNamed"))     data.set(DATA_PLAYER_NAMED,    tag.getBoolean("MI_PlayerNamed"));
        if (tag.contains("MI_ZombieSavedName")) data.set(DATA_ZOMBIE_SAVED,    tag.getString("MI_ZombieSavedName"));
        if (tag.contains("MI_ConversionTime"))  data.set(DATA_CONVERSION_TIME, tag.getInt("MI_ConversionTime"));
        if (tag.contains("MI_InConversion"))    data.set(DATA_IN_CONVERSION,   tag.getBoolean("MI_InConversion"));

        if (tag.contains("MI_LayerSettings", 10)) {
            data.set(DATA_LAYER_SETTINGS, tag.getCompound("MI_LayerSettings"));
        } else {
            CompoundTag oldSettings = new CompoundTag();
            boolean hasOld = false;

            if (tag.contains("MI_SkinIndex")) {
                oldSettings.putInt("skin_index", tag.getInt("MI_SkinIndex"));
                hasOld = true;
            }
            if (tag.contains("MI_FaceIndex")) {
                oldSettings.putInt("face_index", tag.getInt("MI_FaceIndex"));
                hasOld = true;
            }
            if (tag.contains("MI_ClothIndex")) {
                oldSettings.putInt("cloth_index", tag.getInt("MI_ClothIndex"));
                hasOld = true;
            }
            if (tag.contains("MI_HairIndex")) {
                oldSettings.putInt("hair_index", tag.getInt("MI_HairIndex"));
                hasOld = true;
            }
            if (tag.contains("MI_SkinTone", 1)) {
                oldSettings.putByte("skin_tone", tag.getByte("MI_SkinTone"));
                hasOld = true;
            }
            if (tag.contains("MI_HairColorU", 1)) {
                oldSettings.putByte("hair_u", tag.getByte("MI_HairColorU"));
                hasOld = true;
            }
            if (tag.contains("MI_HairColorV", 1)) {
                oldSettings.putByte("hair_v", tag.getByte("MI_HairColorV"));
                hasOld = true;
            }
            if (tag.contains("MI_BreastSize")) {
                oldSettings.putFloat("breast_size", tag.getFloat("MI_BreastSize"));
                hasOld = true;
            }
            if (tag.contains("MI_BreastOffsetX")) {
                oldSettings.putFloat("breasts_OffsetX", tag.getFloat("MI_BreastOffsetX"));
                hasOld = true;
            }
            if (tag.contains("MI_BreastOffsetY")) {
                oldSettings.putFloat("breasts_OffsetY", tag.getFloat("MI_BreastOffsetY"));
                hasOld = true;
            }
            if (tag.contains("MI_BreastOffsetZ")) {
                oldSettings.putFloat("breasts_OffsetZ", tag.getFloat("MI_BreastOffsetZ"));
                hasOld = true;
            }
            if (tag.contains("MI_BreastCleavage")) {
                oldSettings.putFloat("breasts_cleavage", tag.getFloat("MI_BreastCleavage"));
                hasOld = true;
            }

            if (hasOld) {
                data.set(DATA_LAYER_SETTINGS, oldSettings);
            }
        }
    }
}
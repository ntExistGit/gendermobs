package com.ntexist.gendermobs.mixin;

import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityAccessor {

    @Unique
    private static final EntityDataAccessor<String> GENDER = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.STRING);
    @Unique
    private static final EntityDataAccessor<String> MOB_NAME = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.STRING);
    @Unique
    private static final EntityDataAccessor<String> ORIGINAL_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.STRING);

    @Unique
    private static final String NBT_GENDER = "GM_Gender";
    @Unique
    private static final String NBT_NAME = "GM_Name";
    @Unique
    private static final String NBT_ORIGINAL_ID = "GM_OriginalId";
    @Unique
    private static final String NBT_PLAYER_NAMED = "GM_PlayerNamed";
    @Unique
    private static final String NBT_ZOMBIE_SAVED_NAME = "GM_ZombieSavedName";

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    private void initGmData(CallbackInfo ci) {
        SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
        if (tracker != null) {
            tracker.define(GENDER, "");
            tracker.define(MOB_NAME, "");
            tracker.define(ORIGINAL_ID, "");
        }
    }

    @Override
    public String getGender() {
        CompoundTag nbt = ((LivingEntity)(Object)this).getPersistentData();
        if (nbt.contains(NBT_GENDER)) {
            String gender = nbt.getString(NBT_GENDER);
            SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
            if (tracker != null && !gender.equals(tracker.get(GENDER))) {
                tracker.set(GENDER, gender);
            }
            return gender;
        }
        return "";
    }

    @Override
    public void setGender(String gender) {
        SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
        CompoundTag nbt = ((LivingEntity)(Object)this).getPersistentData();
        if (tracker != null) tracker.set(GENDER, gender);
        nbt.putString(NBT_GENDER, gender);
    }

    @Override
    public String getMobName() {
        CompoundTag nbt = ((LivingEntity)(Object)this).getPersistentData();
        if (nbt.contains(NBT_NAME)) {
            String name = nbt.getString(NBT_NAME);
            SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
            if (tracker != null && !name.equals(tracker.get(MOB_NAME))) {
                tracker.set(MOB_NAME, name);
            }
            return name;
        }
        return "";
    }

    @Override
    public void setMobName(String name) {
        SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
        CompoundTag nbt = ((LivingEntity)(Object)this).getPersistentData();
        nbt.putString(NBT_NAME, name);
        if (tracker != null) tracker.set(MOB_NAME, name);
    }

    @Override
    public String getOriginalId() {
        CompoundTag nbt = ((LivingEntity)(Object)this).getPersistentData();
        if (nbt.contains(NBT_ORIGINAL_ID)) {
            String id = nbt.getString(NBT_ORIGINAL_ID);
            SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
            if (tracker != null && !id.equals(tracker.get(ORIGINAL_ID))) {
                tracker.set(ORIGINAL_ID, id);
            }
            return id;
        }
        return "";
    }

    @Override
    public void setOriginalId(String id) {
        SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
        CompoundTag nbt = ((LivingEntity)(Object)this).getPersistentData();
        if (tracker != null) tracker.set(ORIGINAL_ID, id);
        nbt.putString(NBT_ORIGINAL_ID, id);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeGmDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        CompoundTag persistentData = ((LivingEntity)(Object)this).getPersistentData();
        if (persistentData.contains(NBT_GENDER)) {
            nbt.putString(NBT_GENDER, persistentData.getString(NBT_GENDER));
        }
        if (persistentData.contains(NBT_NAME)) {
            nbt.putString(NBT_NAME, persistentData.getString(NBT_NAME));
        }
        if (persistentData.contains(NBT_ORIGINAL_ID)) {
            nbt.putString(NBT_ORIGINAL_ID, persistentData.getString(NBT_ORIGINAL_ID));
        }
        if (persistentData.contains(NBT_PLAYER_NAMED)) {
            nbt.putBoolean(NBT_PLAYER_NAMED, persistentData.getBoolean(NBT_PLAYER_NAMED));
        }
        if (persistentData.contains(NBT_ZOMBIE_SAVED_NAME)) {
            nbt.putString(NBT_ZOMBIE_SAVED_NAME, persistentData.getString(NBT_ZOMBIE_SAVED_NAME));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readGmDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        CompoundTag persistentData = ((LivingEntity)(Object)this).getPersistentData();
        if (nbt.contains(NBT_GENDER)) {
            String gender = nbt.getString(NBT_GENDER);
            persistentData.putString(NBT_GENDER, gender);
            SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
            if (tracker != null) tracker.set(GENDER, gender);
        }
        if (nbt.contains(NBT_NAME)) {
            String name = nbt.getString(NBT_NAME);
            persistentData.putString(NBT_NAME, name);
            SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
            if (tracker != null) tracker.set(MOB_NAME, name);
        }
        if (nbt.contains(NBT_ORIGINAL_ID)) {
            String id = nbt.getString(NBT_ORIGINAL_ID);
            persistentData.putString(NBT_ORIGINAL_ID, id);
            SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
            if (tracker != null) tracker.set(ORIGINAL_ID, id);
        }
        if (nbt.contains(NBT_PLAYER_NAMED)) {
            persistentData.putBoolean(NBT_PLAYER_NAMED, nbt.getBoolean(NBT_PLAYER_NAMED));
        }
        if (nbt.contains(NBT_ZOMBIE_SAVED_NAME)) {
            persistentData.putString(NBT_ZOMBIE_SAVED_NAME, nbt.getString(NBT_ZOMBIE_SAVED_NAME));
        }
    }
}
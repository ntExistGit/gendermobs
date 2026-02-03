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
        SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
        return tracker != null ? tracker.get(GENDER) : "";
    }

    @Override
    public void setGender(String gender) {
        SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
        if (tracker != null) tracker.set(GENDER, gender);
    }

    @Override
    public String getMobName() {
        SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
        return tracker != null ? tracker.get(MOB_NAME) : "";
    }

    @Override
    public void setMobName(String name) {
        SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
        if (tracker != null) tracker.set(MOB_NAME, name);
    }

    @Override
    public String getOriginalId() {
        SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
        return tracker != null ? tracker.get(ORIGINAL_ID) : "";
    }

    @Override
    public void setOriginalId(String id) {
        SynchedEntityData tracker = ((LivingEntity)(Object)this).getEntityData();
        if (tracker != null) tracker.set(ORIGINAL_ID, id);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeGmDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        nbt.putString("MobGender", getGender());
        nbt.putString("MobNameCustom", getMobName());
        nbt.putString("OriginalMobId", getOriginalId());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readGmDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("MobGender")) setGender(nbt.getString("MobGender"));
        if (nbt.contains("MobNameCustom")) setMobName(nbt.getString("MobNameCustom"));
        if (nbt.contains("OriginalMobId")) setOriginalId(nbt.getString("OriginalMobId"));
    }
}
package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import net.minecraft.nbt.CompoundTag;
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

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityDataMixin implements LivingEntityAccessor {

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
    private SynchedEntityData getEntityData() {
        return ((Entity) (Object) this).getEntityData();
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineAllData(CallbackInfo ci) {
        this.getEntityData().define(DATA_GENDER, "");
        this.getEntityData().define(DATA_MOB_NAME, "");
        this.getEntityData().define(DATA_ORIGINAL_ID, "");
        this.getEntityData().define(DATA_PLAYER_NAMED, false);
        this.getEntityData().define(DATA_ZOMBIE_SAVED, "");
        this.getEntityData().define(DATA_CONVERSION_TIME, -1);
        this.getEntityData().define(DATA_IN_CONVERSION, false);
    }

    @Override
    public void mcidentitymobs$setGender(String gender) {
        this.getEntityData().set(DATA_GENDER, gender != null ? gender : "");
    }

    @Override
    public String mcidentitymobs$getGender() {
        return this.getEntityData().get(DATA_GENDER);
    }

    @Override
    public void mcidentitymobs$setMobName(String name) {
        this.getEntityData().set(DATA_MOB_NAME, name != null ? name : "");
    }

    @Override
    public String mcidentitymobs$getMobName() {
        return this.getEntityData().get(DATA_MOB_NAME);
    }

    @Override
    public void mcidentitymobs$setOriginalId(String id) {
        this.getEntityData().set(DATA_ORIGINAL_ID, id != null ? id : "");
    }

    @Override
    public String mcidentitymobs$getOriginalId() {
        return this.getEntityData().get(DATA_ORIGINAL_ID);
    }

    @Override
    public void mcidentitymobs$setPlayerNamed(boolean val) {
        this.getEntityData().set(DATA_PLAYER_NAMED, val);
    }

    @Override
    public boolean mcidentitymobs$isPlayerNamed() {
        return this.getEntityData().get(DATA_PLAYER_NAMED);
    }

    @Override
    public void mcidentitymobs$setZombieSavedName(String name) {
        this.getEntityData().set(DATA_ZOMBIE_SAVED, name != null ? name : "");
    }

    @Override
    public String mcidentitymobs$getZombieSavedName() {
        return this.getEntityData().get(DATA_ZOMBIE_SAVED);
    }

    @Override
    public void mcidentitymobs$setConversionTime(int time) {
        this.getEntityData().set(DATA_CONVERSION_TIME, time);
    }

    @Override
    public int mcidentitymobs$getConversionTime() {
        return this.getEntityData().get(DATA_CONVERSION_TIME);
    }

    @Override
    public void mcidentitymobs$setInConversion(boolean inConversion) {
        this.getEntityData().set(DATA_IN_CONVERSION, inConversion);
    }

    @Override
    public boolean mcidentitymobs$isInConversion() {
        return this.getEntityData().get(DATA_IN_CONVERSION);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveAllToNBT(CompoundTag tag, CallbackInfo ci) {
        String gender = this.getEntityData().get(DATA_GENDER);
        if (!gender.isEmpty()) tag.putString("MI_Gender", gender);

        String mobName = this.getEntityData().get(DATA_MOB_NAME);
        if (!mobName.isEmpty()) tag.putString("MI_Name", mobName);

        String origId = this.getEntityData().get(DATA_ORIGINAL_ID);
        if (!origId.isEmpty()) tag.putString("MI_OriginalId", origId);

        if (this.getEntityData().get(DATA_PLAYER_NAMED)) tag.putBoolean("MI_PlayerNamed", true);

        String zombieName = this.getEntityData().get(DATA_ZOMBIE_SAVED);
        if (!zombieName.isEmpty()) tag.putString("MI_ZombieSavedName", zombieName);

        int convTime = this.getEntityData().get(DATA_CONVERSION_TIME);
        if (convTime > -1) tag.putInt("MI_ConversionTime", convTime);

        if (this.getEntityData().get(DATA_IN_CONVERSION)) tag.putBoolean("MI_InConversion", true);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadAllFromNBT(CompoundTag tag, CallbackInfo ci) {
        SynchedEntityData data = this.getEntityData();

        if (tag.contains("MI_Gender"))          data.set(DATA_GENDER,          tag.getString("MI_Gender"));
        if (tag.contains("MI_Name"))            data.set(DATA_MOB_NAME,        tag.getString("MI_Name"));
        if (tag.contains("MI_OriginalId"))      data.set(DATA_ORIGINAL_ID,     tag.getString("MI_OriginalId"));
        if (tag.contains("MI_PlayerNamed"))     data.set(DATA_PLAYER_NAMED,    tag.getBoolean("MI_PlayerNamed"));
        if (tag.contains("MI_ZombieSavedName")) data.set(DATA_ZOMBIE_SAVED,    tag.getString("MI_ZombieSavedName"));
        if (tag.contains("MI_ConversionTime"))  data.set(DATA_CONVERSION_TIME, tag.getInt("MI_ConversionTime"));
        if (tag.contains("MI_InConversion"))    data.set(DATA_IN_CONVERSION,   tag.getBoolean("MI_InConversion"));
    }
}
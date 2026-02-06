package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.LivingEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityDataMixin implements LivingEntityAccessor {

    @Unique private String mi_gender = "";
    @Unique private String mi_name = "";
    @Unique private String mi_originalId = "";
    @Unique private boolean mi_playerNamed = false;
    @Unique private String mi_zombieSavedName = "";
    @Unique private int mi_conversionTime = -1;
    @Unique private boolean mi_inConversion = false;

    @Override public void mcidentitymobs$setGender(String gender) { this.mi_gender = gender; }
    @Override public String mcidentitymobs$getGender() { return mi_gender; }

    @Override public void mcidentitymobs$setMobName(String name) { this.mi_name = name; }
    @Override public String mcidentitymobs$getMobName() { return mi_name; }

    @Override public void mcidentitymobs$setOriginalId(String id) { this.mi_originalId = id; }
    @Override public String mcidentitymobs$getOriginalId() { return mi_originalId; }

    @Override public void mcidentitymobs$setPlayerNamed(boolean val) { this.mi_playerNamed = val; }
    @Override public boolean mcidentitymobs$isPlayerNamed() { return mi_playerNamed; }

    @Override public void mcidentitymobs$setZombieSavedName(String name) { this.mi_zombieSavedName = name; }
    @Override public String mcidentitymobs$getZombieSavedName() { return mi_zombieSavedName; }

    @Override public void mcidentitymobs$setConversionTime(int time) { this.mi_conversionTime = time; }
    @Override public int mcidentitymobs$getConversionTime() { return mi_conversionTime; }

    @Override public void mcidentitymobs$setInConversion(boolean inConversion) { this.mi_inConversion = inConversion; }
    @Override public boolean mcidentitymobs$isInConversion() { return mi_inConversion; }

    @Override
    public void mcidentitymobs$saveToNBT(CompoundTag nbt) {
        if (!mi_gender.isEmpty()) nbt.putString("MI_Gender", mi_gender);
        if (!mi_name.isEmpty()) nbt.putString("MI_Name", mi_name);
        if (!mi_originalId.isEmpty()) nbt.putString("MI_OriginalId", mi_originalId);
        if (mi_playerNamed) nbt.putBoolean("MI_PlayerNamed", true);
        if (!mi_zombieSavedName.isEmpty()) nbt.putString("MI_ZombieSavedName", mi_zombieSavedName);
        if (mi_conversionTime > -1) nbt.putInt("ConversionTime", mi_conversionTime);
        if (mi_inConversion) nbt.putBoolean("InConversion", true);
    }

    @Override
    public void mcidentitymobs$loadFromNBT(CompoundTag nbt) {
        if (nbt.contains("MI_Gender")) mi_gender = nbt.getString("MI_Gender");
        if (nbt.contains("MI_Name")) mi_name = nbt.getString("MI_Name");
        if (nbt.contains("MI_OriginalId")) mi_originalId = nbt.getString("MI_OriginalId");
        if (nbt.contains("MI_PlayerNamed")) mi_playerNamed = nbt.getBoolean("MI_PlayerNamed");
        if (nbt.contains("MI_ZombieSavedName")) mi_zombieSavedName = nbt.getString("MI_ZombieSavedName");
        if (nbt.contains("ConversionTime")) mi_conversionTime = nbt.getInt("ConversionTime");
        mi_inConversion = nbt.getBoolean("InConversion");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void mi_save(CompoundTag tag, CallbackInfo ci) { mcidentitymobs$saveToNBT(tag); }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadIdentityData(CompoundTag tag, CallbackInfo ci) { mcidentitymobs$loadFromNBT(tag); }
}

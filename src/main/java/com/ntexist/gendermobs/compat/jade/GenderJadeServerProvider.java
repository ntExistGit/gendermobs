package com.ntexist.gendermobs.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum GenderJadeServerProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    private static final String NBT_GENDER = "GM_Gender";
    private static final String NBT_NAME = "GM_Name";

    @Override
    public void appendServerData(CompoundTag nbt, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof LivingEntity living) {
            try {
                CompoundTag temp = new CompoundTag();
                living.addAdditionalSaveData(temp);

                if (temp.contains(NBT_GENDER)) {
                    nbt.putString(NBT_GENDER, temp.getString(NBT_GENDER));
                }

                if (temp.contains(NBT_NAME)) {
                    nbt.putString(NBT_NAME, temp.getString(NBT_NAME));
                }

                CompoundTag persistentData = living.getPersistentData();

                if (persistentData.contains(NBT_GENDER)) {
                    nbt.putString(NBT_GENDER, persistentData.getString(NBT_GENDER));
                }
            } catch (Throwable ignored) {}
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.tryBuild("gendermobs", "gender_server_info");
    }
}
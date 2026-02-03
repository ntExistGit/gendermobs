package com.ntexist.gendermobs.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum GenderJadeServerProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag nbt, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof LivingEntity living) {
            try {
                CompoundTag temp = new CompoundTag();
                living.addAdditionalSaveData(temp);

                if (temp.contains("MobGender")) {
                    nbt.putString("MobGender", temp.getString("MobGender"));
                }
            } catch (Throwable ignored) {}
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.tryBuild("gendermobs", "gender_server_info");
    }
}
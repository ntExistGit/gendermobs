package com.ntexist.mcidentitymobs.compat.jade;

import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.enums.Gender;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum GenderJadeServerProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof LivingEntity living) {
            Gender gender = MobIdentityAPI.getGender(living);
            tag.putString("MI_Gender", gender != null ? gender.name().toLowerCase() : "");
            tag.putInt("MI_ConversionTime", MobIdentityAPI.getConversionTime(living));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "gender_server_info");
    }
}
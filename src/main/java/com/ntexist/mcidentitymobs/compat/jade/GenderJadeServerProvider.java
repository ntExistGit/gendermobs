package com.ntexist.mcidentitymobs.compat.jade;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum GenderJadeServerProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof LivingEntityAccessor acc) {
            tag.putString("MI_Gender", acc.mcidentitymobs$getGender());
            tag.putInt("MI_ConversionTime", acc.mcidentitymobs$getConversionTime());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "gender_server_info");
    }
}
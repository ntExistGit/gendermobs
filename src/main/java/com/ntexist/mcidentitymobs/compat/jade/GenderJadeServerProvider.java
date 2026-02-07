package com.ntexist.mcidentitymobs.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public class GenderJadeServerProvider implements IServerDataProvider<EntityAccessor> {

    public static final GenderJadeServerProvider INSTANCE = new GenderJadeServerProvider();

    private GenderJadeServerProvider() {}

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor accessor) {

    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.tryBuild("mcidentitymobs", "gender_server_data");
    }
}
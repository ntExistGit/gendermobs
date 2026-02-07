package com.ntexist.mcidentitymobs.compat.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;

@WailaPlugin
public class GenderJadePlugin implements IWailaPlugin {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "gender_info");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(GenderJadeServerProvider.INSTANCE, LivingEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(GenderJadeProvider.INSTANCE, LivingEntity.class);
    }
}
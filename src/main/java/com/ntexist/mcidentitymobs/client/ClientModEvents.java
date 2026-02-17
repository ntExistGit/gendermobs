package com.ntexist.mcidentitymobs.client;

import com.ntexist.mcidentitymobs.client.renderer.VillagerAsPlayerRenderer;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "mcidentitymobs", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (ConfigManager.CONFIG.general.usePlayerModel) {
            event.registerEntityRenderer(EntityType.VILLAGER, VillagerAsPlayerRenderer::new);
        }
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {}
}
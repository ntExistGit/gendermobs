package com.ntexist.mcidentitymobs;

import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.ConfigScreen;
import com.ntexist.mcidentitymobs.data.SkinGradientLoader;
import com.ntexist.mcidentitymobs.network.NetworkHandler;
import com.ntexist.mcidentitymobs.pipeline.SpawnPipeline;
import com.ntexist.mcidentitymobs.service.IdentityStorage;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MobIdentity.MODID)
public class MobIdentity {
    public static final String MODID = "mcidentitymobs";

    public MobIdentity() {
        ConfigManager.init();
        MinecraftForge.EVENT_BUS.register(this);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::register);
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) { return; }
        if (!(event.getEntity() instanceof LivingEntity living)) { return; }
        if (!IdentityStorage.hasGender(living)) { SpawnPipeline.onSpawn(living); }
    }

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SkinGradientLoader());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ModLoadingContext.get().registerExtensionPoint(
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory((mc, lastScreen) -> {
                            ConfigManager.createWorkingCopy();
                            return new ConfigScreen(lastScreen);
                        })
                );
            });
        }
    }
}
package com.ntexist.gendermobs;

import com.ntexist.gendermobs.config.ConfigManager;
import com.ntexist.gendermobs.config.ConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("gendermobs")
public class GenderMobs {
    public GenderMobs() {
        ConfigManager.init();
    }

    @Mod.EventBusSubscriber(modid = "gendermobs", bus = Mod.EventBusSubscriber.Bus.MOD)
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
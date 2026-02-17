package com.ntexist.mcidentitymobs.event;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.InfectionData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "mcidentitymobs", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CureHandler {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getLevel().isClientSide) return;

        if (!(event.getTarget() instanceof LivingEntity target)) return;
        if (!(target instanceof LivingEntityAccessor)) return;

        ItemStack heldItem = event.getEntity().getItemInHand(InteractionHand.MAIN_HAND);
        ResourceLocation currentType = EntityType.getKey(target.getType());

        List<Map.Entry<String, InfectionData>> candidates = new ArrayList<>();

        for (Map.Entry<String, InfectionData> entry : ConfigManager.CONFIG.canBeInfected.entrySet()) {
            InfectionData d = entry.getValue();
            if (!d.curable || !d.zombie.equals(currentType.toString())) continue;

            if (!d.item.isEmpty()) {
                ResourceLocation reqItem = ResourceLocation.tryParse(d.item);
                if (reqItem == null) continue;

                Item requiredItem = BuiltInRegistries.ITEM.get(reqItem);
                if (requiredItem == null || requiredItem == Items.AIR) continue;

                if (!heldItem.is(requiredItem)) continue;
            }

            if (!d.effect.isEmpty()) {
                ResourceLocation effLoc = ResourceLocation.tryParse(d.effect);
                if (effLoc == null) continue;

                var effect = BuiltInRegistries.MOB_EFFECT.get(effLoc);
                if (effect == null || !target.hasEffect(effect)) continue;
            }

            candidates.add(entry);
        }

        if (candidates.isEmpty()) return;

        String originalId = MobIdentityAPI.getOriginalId(target);
        Map.Entry<String, InfectionData> selectedEntry;

        if (originalId != null && !originalId.isEmpty()) {
            selectedEntry = candidates.stream()
                    .filter(e -> e.getKey().equals(originalId))
                    .findFirst()
                    .orElse(null);
            if (selectedEntry == null) return;
        } else {
            selectedEntry = candidates.get(target.getRandom().nextInt(candidates.size()));
            MobIdentityAPI.setOriginalId(target, selectedEntry.getKey());
        }

        InfectionData data = selectedEntry.getValue();

        event.setCanceled(true);

        if (!data.item.isEmpty()) {
            heldItem.shrink(1);
        }

        int baseTime = data.time;
        if (baseTime <= 0) baseTime = 3600;

        ServerLevel level = (ServerLevel) event.getLevel();
        float minMultiplier, maxMultiplier;

        switch (level.getDifficulty()) {
            case PEACEFUL, EASY -> {
                minMultiplier = ConfigManager.CONFIG.general.timeMinMult * 0.5f;
                maxMultiplier = ConfigManager.CONFIG.general.timeMaxMult;
            }
            case NORMAL -> {
                minMultiplier = ConfigManager.CONFIG.general.timeMinMult;
                maxMultiplier = ConfigManager.CONFIG.general.timeMaxMult;
            }
            case HARD -> {
                minMultiplier = ConfigManager.CONFIG.general.timeMinMult;
                maxMultiplier = ConfigManager.CONFIG.general.timeMaxMult * 2.0f;
            }
            default -> {
                minMultiplier = 1.0f;
                maxMultiplier = 1.0f;
            }
        }

        int conversionTime = (int) (baseTime * (minMultiplier + level.random.nextFloat() * (maxMultiplier - minMultiplier)));

        if (target instanceof LivingEntityAccessor acc) {
            acc.mcidentitymobs$setConversionTime(conversionTime);
            acc.mcidentitymobs$setInConversion(true);
            acc.mcidentitymobs$setCuringPlayerUUID(event.getEntity().getUUID());
        }

        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }
}
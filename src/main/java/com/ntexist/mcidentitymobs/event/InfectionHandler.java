package com.ntexist.mcidentitymobs.event;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.InfectionData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "mcidentitymobs", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InfectionHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0) return;

        // Only run on server
        if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) return;

        LivingEntity target = event.getEntity();

        // Attacker must be a living entity
        Entity source = event.getSource().getEntity();
        if (!(source instanceof LivingEntity attacker)) return;

        // Check if attacker is in zombies list
        ResourceLocation attackerType = EntityType.getKey(attacker.getType());
        if (!ConfigManager.CONFIG.zombies.contains(attackerType.toString())) {
            return;
        }

        // Check if target can be infected
        ResourceLocation targetType = EntityType.getKey(target.getType());
        InfectionData data = ConfigManager.CONFIG.canBeInfected.get(targetType.toString());
        if (data == null) return;

        // Only infect on lethal damage
        float remainingHealth = target.getHealth() - event.getAmount();
        if (remainingHealth > 0) return;

        // Chance based on difficulty (vanilla-like)
        float chance = switch (serverLevel.getDifficulty()) {
            case PEACEFUL, EASY -> 0.0f;
            case NORMAL -> 0.5f;
            case HARD -> 1.0f;
        };

        if (serverLevel.random.nextFloat() >= chance) return;

        // Parse zombie type from config
        ResourceLocation zombieLoc = ResourceLocation.tryParse(data.zombie);
        if (zombieLoc == null) return;

        EntityType<?> zombieType = EntityType.byString(zombieLoc.toString()).orElse(null);
        if (zombieType == null) return;

        LivingEntity newZombie = (LivingEntity) zombieType.create(serverLevel);
        if (newZombie == null) return;

        // Copy position, rotation, custom name
        newZombie.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());

        if (target.hasCustomName()) {
            newZombie.setCustomName(target.getCustomName());
            newZombie.setCustomNameVisible(target.isCustomNameVisible());
        }

        // Copy mod-specific data (gender, name, etc.)
        if (newZombie instanceof LivingEntityAccessor newAcc && target instanceof LivingEntityAccessor targetAcc) {
            newAcc.mcidentitymobs$setOriginalId(targetType.toString());
            newAcc.mcidentitymobs$setGender(targetAcc.mcidentitymobs$getGender());
            newAcc.mcidentitymobs$setMobName(targetAcc.mcidentitymobs$getMobName());
            newAcc.mcidentitymobs$setPlayerNamed(targetAcc.mcidentitymobs$isPlayerNamed());
        }

        // Special handling for villager -> zombie villager
        if (target instanceof Villager villager && newZombie instanceof ZombieVillager zombieVillager) {
            zombieVillager.setVillagerData(villager.getVillagerData());
            CompoundTag offersTag = villager.getOffers().createTag();
            zombieVillager.setTradeOffers(offersTag);
            zombieVillager.setVillagerXp(villager.getVillagerXp());
            Tag gossipsNbt = villager.getGossips().store(NbtOps.INSTANCE);
            zombieVillager.setGossips(gossipsNbt);
        }

        // Play infection sound
        serverLevel.playSound(null, newZombie.getX(), newZombie.getY(), newZombie.getZ(),
                SoundEvents.ZOMBIE_INFECT,
                SoundSource.HOSTILE, 1.0F, 1.0F);

        // Spawn new entity and remove old one
        serverLevel.addFreshEntity(newZombie);
        target.remove(Entity.RemovalReason.DISCARDED);

        // Cancel damage
        event.setCanceled(true);
    }
}
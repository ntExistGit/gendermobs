package com.ntexist.mcidentitymobs.event;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.InfectionData;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = "mcidentitymobs", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InfectionHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.isCanceled() || event.getAmount() <= 0) return;

        if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) return;

        LivingEntity target = event.getEntity();

        Entity source = event.getSource().getEntity();
        if (!(source instanceof LivingEntity attacker)) return;

        ResourceLocation attackerType = EntityType.getKey(attacker.getType());
        if (!ConfigManager.CONFIG.zombies.contains(attackerType.toString())) {
            return;
        }

        ResourceLocation targetType = EntityType.getKey(target.getType());
        InfectionData data = ConfigManager.CONFIG.canBeInfected.get(targetType.toString());
        if (data == null) return;

        float remainingHealth = target.getHealth() - event.getAmount();
        if (remainingHealth > 0) return;

        float chance = switch (serverLevel.getDifficulty()) {
            case PEACEFUL   -> 0.0f;
            case EASY       -> ConfigManager.CONFIG.general.chanceInf * 0.5f;
            case NORMAL     -> ConfigManager.CONFIG.general.chanceInf;
            case HARD       -> ConfigManager.CONFIG.general.chanceInf * 2.0f;
        };

        chance = Math.max(0.0f, Math.min(1.0f, chance));

        if (serverLevel.random.nextFloat() >= chance) return;

        ResourceLocation zombieLoc = ResourceLocation.tryParse(data.zombie);
        if (zombieLoc == null) return;

        EntityType<?> zombieType = EntityType.byString(zombieLoc.toString()).orElse(null);
        if (zombieType == null) return;

        LivingEntity newZombie = (LivingEntity) zombieType.create(serverLevel);
        if (newZombie == null) return;

        newZombie.moveTo(target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());

        if (target.hasCustomName()) {
            newZombie.setCustomName(target.getCustomName());
            newZombie.setCustomNameVisible(target.isCustomNameVisible());
        }

        if (newZombie instanceof LivingEntityAccessor newAcc && target instanceof LivingEntityAccessor targetAcc) {
            newAcc.mcidentitymobs$setOriginalId(targetType.toString());
            newAcc.mcidentitymobs$setGender(targetAcc.mcidentitymobs$getGender());
            newAcc.mcidentitymobs$setMobName(targetAcc.mcidentitymobs$getMobName());
            newAcc.mcidentitymobs$setPlayerNamed(targetAcc.mcidentitymobs$isPlayerNamed());
            newAcc.mcidentitymobs$setLayerSettings(targetAcc.mcidentitymobs$getLayerSettings());
        }

        if (target instanceof Villager villager && newZombie instanceof ZombieVillager zombieVillager) {
            zombieVillager.setVillagerData(villager.getVillagerData());
            CompoundTag offersTag = villager.getOffers().createTag();
            zombieVillager.setTradeOffers(offersTag);
            zombieVillager.setVillagerXp(villager.getVillagerXp());
            Tag gossipsNbt = villager.getGossips().store(NbtOps.INSTANCE);
            zombieVillager.setGossips(gossipsNbt);
        }

        if (target instanceof WanderingTrader trader && newZombie instanceof ZombieVillager zombie) {
            try {
                Field offersField = AbstractVillager.class.getDeclaredField("offers");
                offersField.setAccessible(true);
                MerchantOffers offers = (MerchantOffers) offersField.get(trader);
                if (offers != null && !offers.isEmpty()) {
                    CompoundTag offersTag = offers.createTag();
                    Field tradeField = ZombieVillager.class.getDeclaredField("tradeOffers");
                    tradeField.setAccessible(true);
                    tradeField.set(zombie, offersTag);
                }

                CompoundTag extraData = new CompoundTag();
                extraData.putInt("DespawnDelay", trader.getDespawnDelay());

                try {
                    Field wanderField = WanderingTrader.class.getDeclaredField("wanderTarget");
                    wanderField.setAccessible(true);
                    BlockPos wanderPos = (BlockPos) wanderField.get(trader);
                    if (wanderPos != null) {
                        extraData.putLong("WanderTarget", wanderPos.asLong());
                    }
                } catch (Exception ignored) {}

                if (!extraData.isEmpty()) {
                    ((LivingEntityAccessor) zombie).mcidentitymobs$setZombieSavedName(extraData.toString());
                }
            } catch (Exception e) {}
        }

        serverLevel.playSound(null, newZombie.getX(), newZombie.getY(), newZombie.getZ(),
                SoundEvents.ZOMBIE_INFECT,
                SoundSource.HOSTILE, 1.0F, 1.0F);

        serverLevel.addFreshEntity(newZombie);
        target.remove(Entity.RemovalReason.DISCARDED);

        event.setCanceled(true);
    }
}
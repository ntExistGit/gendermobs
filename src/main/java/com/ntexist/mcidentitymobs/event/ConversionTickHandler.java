package com.ntexist.mcidentitymobs.event;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "mcidentitymobs", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ConversionTickHandler {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide) return;

        LivingEntity entity = event.getEntity();
        if (!(entity instanceof LivingEntityAccessor acc)) return;

        int time = acc.mcidentitymobs$getConversionTime();
        if (time <= 0) return;

        ServerLevel level = (ServerLevel) entity.level();

        int decrement = 1;
        BlockPos pos = entity.blockPosition();
        for (int x = -4; x <= 4; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos check = pos.offset(x, y, z);
                    BlockState state = level.getBlockState(check);
                    if (state.is(BlockTags.BEDS) || state.is(Blocks.IRON_BARS)) {
                        decrement++;
                    }
                }
            }
        }

        acc.mcidentitymobs$setConversionTime(time - decrement);

        if (acc.mcidentitymobs$isInConversion()) {
            float shake = level.random.nextFloat() * 0.8f - 0.4f;   // 0.4 - 0.4
            entity.yBodyRot += shake * 15;                          // 10
            entity.setXRot(entity.getXRot() + shake * 8);           // 5
        }

        if (level.random.nextInt(5) == 0) {
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    entity.getX() + level.random.nextGaussian() * 0.5,
                    entity.getY() + 1.0 + level.random.nextGaussian() * 0.5,
                    entity.getZ() + level.random.nextGaussian() * 0.5,
                    5, 0.3, 0.4, 0.3, 0.0);
        }

        if (level.random.nextInt(40) == 0) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.ZOMBIE_VILLAGER_AMBIENT, SoundSource.NEUTRAL, 0.8F, 1.0F);
        }

        if (acc.mcidentitymobs$getConversionTime() <= 0) {
            String origId = MobIdentityAPI.getOriginalId(entity);
            if (origId == null || origId.isEmpty()) {
                acc.mcidentitymobs$setInConversion(false);
                return;
            }

            ResourceLocation loc = ResourceLocation.tryParse(origId);
            if (loc == null) return;

            EntityType<?> type = EntityType.byString(loc.toString()).orElse(null);
            if (type == null) return;

            LivingEntity original = (LivingEntity) type.create(level);
            if (original == null) return;

            original.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());

            if (entity.hasCustomName()) {
                original.setCustomName(entity.getCustomName());
                original.setCustomNameVisible(entity.isCustomNameVisible());
            }

            if (original instanceof LivingEntityAccessor oAcc) {
                oAcc.mcidentitymobs$setGender(acc.mcidentitymobs$getGender());
                oAcc.mcidentitymobs$setMobName(acc.mcidentitymobs$getMobName());
                oAcc.mcidentitymobs$setPlayerNamed(acc.mcidentitymobs$isPlayerNamed());
                oAcc.mcidentitymobs$setLayerSettings(acc.mcidentitymobs$getLayerSettings());
            }

            if (original instanceof Villager originalVillager && entity instanceof ZombieVillager zombieVillager) {
                originalVillager.setVillagerData(zombieVillager.getVillagerData());
                originalVillager.setVillagerXp(zombieVillager.getVillagerXp());

                try {
                    Field tradeField = ZombieVillager.class.getDeclaredField("tradeOffers");
                    tradeField.setAccessible(true);
                    CompoundTag offersTag = (CompoundTag) tradeField.get(zombieVillager);
                    if (offersTag != null) {
                        originalVillager.setOffers(new MerchantOffers(offersTag));
                    }
                } catch (Exception ignored) {}

                try {
                    Field gossipsField = ZombieVillager.class.getDeclaredField("gossips");
                    gossipsField.setAccessible(true);
                    Tag gossipsTag = (Tag) gossipsField.get(zombieVillager);
                    if (gossipsTag != null) {
                        originalVillager.setGossips(gossipsTag);
                    }
                } catch (Exception ignored) {}

                UUID playerUUID = acc.mcidentitymobs$getCuringPlayerUUID();
                if (playerUUID != null) {
                    Player player = level.getPlayerByUUID(playerUUID);
                    if (player instanceof ServerPlayer serverPlayer) {
                        CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger(serverPlayer, zombieVillager, originalVillager);
                    }
                }
            }

            if (original instanceof WanderingTrader newTrader && entity instanceof ZombieVillager zombie) {
                try {
                    Field tradeField = ZombieVillager.class.getDeclaredField("tradeOffers");
                    tradeField.setAccessible(true);
                    CompoundTag offersTag = (CompoundTag) tradeField.get(zombie);
                    if (offersTag != null) {
                        MerchantOffers offers = new MerchantOffers(offersTag);
                        Field offersField = AbstractVillager.class.getDeclaredField("offers");
                        offersField.setAccessible(true);
                        offersField.set(newTrader, offers);
                    }
                } catch (Exception ignored) {}

                String saved = ((LivingEntityAccessor) zombie).mcidentitymobs$getZombieSavedName();
                if (saved != null && !saved.isEmpty()) {
                    try {
                        CompoundTag extraData = net.minecraft.nbt.TagParser.parseTag(saved);
                        if (extraData.contains("DespawnDelay")) {
                            newTrader.setDespawnDelay(extraData.getInt("DespawnDelay"));
                        }
                        if (extraData.contains("WanderTarget")) {
                            BlockPos wanderTargetPos = BlockPos.of(extraData.getLong("WanderTarget"));
                            newTrader.setWanderTarget(wanderTargetPos);
                        }
                    } catch (Exception ignored) {}
                }
            }

            level.addFreshEntity(original);
            entity.remove(Entity.RemovalReason.DISCARDED);

            level.playSound(null, original.getX(), original.getY(), original.getZ(),
                    SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.NEUTRAL, 1.0F, 1.0F);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    original.getX(), original.getY() + 1.0, original.getZ(), 30, 0.5, 0.8, 0.5, 0.1);

            if (original instanceof LivingEntityAccessor oAcc) {
                oAcc.mcidentitymobs$setConversionTime(-1);
                oAcc.mcidentitymobs$setInConversion(false);
            }
        }
    }
}
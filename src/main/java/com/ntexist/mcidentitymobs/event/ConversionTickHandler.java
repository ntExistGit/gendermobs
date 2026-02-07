package com.ntexist.mcidentitymobs.event;

import com.ntexist.mcidentitymobs.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.InfectionData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

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

        // Уменьшение времени + ускорение от кроватей и железных решёток
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

        // Тряска (простой способ — поворот тела)
        if (acc.mcidentitymobs$isInConversion()) {
            float shake = level.random.nextFloat() * 0.4f - 0.2f;
            entity.yBodyRot += shake * 10;  // ← используем yBodyRot (публичное поле)
            entity.setXRot(entity.getXRot() + shake * 5);
        }

        // Частицы (каждые 5–10 тиков)
        if (level.random.nextInt(5) == 0) {
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    entity.getX() + level.random.nextGaussian() * 0.5,
                    entity.getY() + 1.0 + level.random.nextGaussian() * 0.5,
                    entity.getZ() + level.random.nextGaussian() * 0.5,
                    5, 0.3, 0.4, 0.3, 0.0);
        }

        // Периодический звук превращения
        if (level.random.nextInt(40) == 0) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.ZOMBIE_VILLAGER_AMBIENT, SoundSource.NEUTRAL, 0.8F, 1.0F);
        }

        // Завершение лечения
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

            // Перенос имени
            if (entity.hasCustomName()) {
                original.setCustomName(entity.getCustomName());
                original.setCustomNameVisible(entity.isCustomNameVisible());
            }

            // Перенос пола, имени, флага
            if (original instanceof LivingEntityAccessor oAcc && entity instanceof LivingEntityAccessor eAcc) {
                oAcc.mcidentitymobs$setGender(eAcc.mcidentitymobs$getGender());
                oAcc.mcidentitymobs$setMobName(eAcc.mcidentitymobs$getMobName());
                oAcc.mcidentitymobs$setPlayerNamed(eAcc.mcidentitymobs$isPlayerNamed());
            }

            // Специально для жителя — полный перенос как в ванили
            if (original instanceof Villager originalVillager && entity instanceof ZombieVillager zombieVillager) {
                originalVillager.setVillagerData(zombieVillager.getVillagerData());
                originalVillager.setVillagerXp(zombieVillager.getVillagerXp());

                // Торговые предложения и Gossips — через рефлексию
                try {
                    // tradeOffers (CompoundTag)
                    java.lang.reflect.Field tradeField = ZombieVillager.class.getDeclaredField("tradeOffers");
                    tradeField.setAccessible(true);
                    CompoundTag offersTag = (CompoundTag) tradeField.get(zombieVillager);
                    if (offersTag != null) {
                        originalVillager.setOffers(new MerchantOffers(offersTag));
                    }

                    // gossips (Tag)
                    java.lang.reflect.Field gossipsField = ZombieVillager.class.getDeclaredField("gossips");
                    gossipsField.setAccessible(true);
                    Tag gossipsTag = (Tag) gossipsField.get(zombieVillager);
                    if (gossipsTag != null) {
                        originalVillager.setGossips(gossipsTag);
                    }
                } catch (Exception e) {
                    // Если рефлексия упала — пропускаем (можно добавить лог)
                    // e.printStackTrace();
                }
            }

            level.addFreshEntity(original);
            entity.remove(Entity.RemovalReason.DISCARDED);

            // Финальный звук и частицы
            level.playSound(null, original.getX(), original.getY(), original.getZ(),
                    SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.NEUTRAL, 1.0F, 1.0F);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    original.getX(), original.getY() + 1.0, original.getZ(), 30, 0.5, 0.8, 0.5, 0.1);

            // Очистка
            if (original instanceof LivingEntityAccessor oAcc) {
                oAcc.mcidentitymobs$setConversionTime(-1);
                oAcc.mcidentitymobs$setInConversion(false);
            }
        }
    }
}
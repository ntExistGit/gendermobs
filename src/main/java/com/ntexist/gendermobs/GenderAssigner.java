package com.ntexist.gendermobs;

import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import com.ntexist.gendermobs.config.ConfigManager;
import com.ntexist.gendermobs.config.EntryData;
import com.ntexist.gendermobs.config.NameLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber
public class GenderAssigner {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (
                ConfigManager.CONFIG == null
                || event.getLevel().isClientSide()
                || !(event.getEntity() instanceof LivingEntity entity)
        ) return;

        updateEntityVisual(entity);
    }

    public static void updateEntityVisual(LivingEntity entity) {
        if (ConfigManager.CONFIG == null) return;

        LivingEntityAccessor accessor = (LivingEntityAccessor) entity;
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();

        EntryData data = findData(id);
        if (data == null) return;
        boolean isHumanoid = isHumanoid(id);

        if (accessor.getGender().isEmpty()) {
            RandomSource rand = entity.getRandom();
            String gender = rand.nextFloat() < data.chance ? "Female" : "Male";
            accessor.setGender(gender);

            if (isHumanoid) {
                accessor.setMobName(NameLoader.getRandomName(gender, rand));
            }
        }
        if (ConfigManager.CONFIG.general.showNames && data.force && !accessor.getMobName().isEmpty()) {
            if (entity.hasCustomName()) {
                Component currentName = entity.getCustomName();
                if (currentName != null && !currentName.getString().equals(accessor.getMobName())) {
                    return;
                }
            }

            MutableComponent nameText = Component.literal(accessor.getMobName());
            if (ConfigManager.CONFIG.general.showColors) {
                int color = parseHex(accessor.getGender().equals("Male") ?
                        ConfigManager.CONFIG.colors.male : ConfigManager.CONFIG.colors.female);
                nameText.setStyle(nameText.getStyle().withColor(color));
            }

            if (entity.hasCustomName() && entity.getCustomName().equals(nameText)) {
                return;
            }

            entity.setCustomName(nameText);
            entity.setCustomNameVisible(false);

        } else if (entity.hasCustomName()) {
            if (entity.getCustomName().getString().equals(accessor.getMobName())) {
                entity.setCustomName((Component) null);
            }
        }
    }

    @SubscribeEvent
    public static void onNameTagUse(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;

        if (event.getTarget() instanceof LivingEntity entity) {
            ItemStack stack = event.getItemStack();

            if (stack.getItem() == Items.NAME_TAG && stack.hasCustomHoverName()) {
                LivingEntityAccessor accessor = (LivingEntityAccessor) entity;

                accessor.setMobName(stack.getHoverName().getString());
                updateEntityVisual(entity);

                if (!event.getEntity().getAbilities().instabuild) {
                    stack.shrink(1);
                }

                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    public static void updateAllNames() {
        if (ServerLifecycleHooks.getCurrentServer() == null) return;
        ServerLifecycleHooks.getCurrentServer().getAllLevels().forEach(level -> {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof LivingEntity living) {
                    updateEntityVisual(living);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        LivingEntity victim = event.getEntity();
        Entity killer = event.getSource().getDirectEntity();

        String victimId = BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType()).toString();

        if (ConfigManager.CONFIG.canBeInfected.contains(victimId) && killer instanceof LivingEntity livingKiller) {
            String killerId = BuiltInRegistries.ENTITY_TYPE.getKey(livingKiller.getType()).toString();

            if (ConfigManager.CONFIG.zombies.contains(killerId)) {
                Difficulty difficulty = victim.level().getDifficulty();
                float chance = difficulty == Difficulty.HARD ? 1.0f : (difficulty == Difficulty.NORMAL ? 0.5f : 0.0f);

                if (victim.getRandom().nextFloat() < chance) {
                    ZombieVillager zombieVillager = EntityType.ZOMBIE_VILLAGER.create(victim.level());

                    if (zombieVillager != null) {
                        zombieVillager.absMoveTo(
                                victim.getX(), victim.getY(), victim.getZ(), victim.getYRot(), victim.getXRot()
                        );
                        zombieVillager.copyPosition(victim);
                        ((LivingEntityAccessor) zombieVillager).setOriginalId(victimId);

                        if (victim instanceof Villager villager) {
                            zombieVillager.setVillagerData(villager.getVillagerData());
                        }

                        victim.level().addFreshEntity(zombieVillager);
                        victim.discard();
                    }
                }
            }
        }
    }

    private static EntryData findData(String id) {
        if (ConfigManager.CONFIG.vanillaHumanoid.containsKey(id))
            return ConfigManager.CONFIG.vanillaHumanoid.get(id);
        if (ConfigManager.CONFIG.customHumanoid.containsKey(id))
            return ConfigManager.CONFIG.customHumanoid.get(id);
        if (ConfigManager.CONFIG.vanillaNonHumanoid.containsKey(id))
            return ConfigManager.CONFIG.vanillaNonHumanoid.get(id);
        if (ConfigManager.CONFIG.customNonHumanoid.containsKey(id))
            return ConfigManager.CONFIG.customNonHumanoid.get(id);
        return null;
    }

    private static boolean isHumanoid(String id) {
        return ConfigManager.CONFIG.vanillaHumanoid.containsKey(id) ||
                ConfigManager.CONFIG.customHumanoid.containsKey(id);
    }

    private static int parseHex(String hex) {
        try {
            if (hex.startsWith("#")) hex = hex.substring(1);
            return (int) Long.parseLong("FF" + hex, 16);
        } catch (Exception e) { return 0xFFFFFFFF; }
    }
}
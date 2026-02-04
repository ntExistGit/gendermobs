package com.ntexist.gendermobs;

import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import com.ntexist.gendermobs.config.ConfigManager;
import com.ntexist.gendermobs.config.EntryData;
import com.ntexist.gendermobs.config.NameLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

import java.util.regex.Pattern;

@Mod.EventBusSubscriber
public class GenderAssigner {

    private static final Pattern COLOR_PATTERN = Pattern.compile("§[0-9a-fklmnor]|&#[0-9a-fA-F]{6}");

    private static final String NBT_GENDER = "GM_Gender";
    private static final String NBT_NAME = "GM_Name";
    private static final String NBT_ORIGINAL_ID = "GM_OriginalId";
    private static final String NBT_PLAYER_NAMED = "GM_PlayerNamed";
    private static final String NBT_ZOMBIE_SAVED_NAME = "GM_ZombieSavedName";

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

        if (data == null) {
            if (!accessor.getGender().isEmpty() || !accessor.getMobName().isEmpty()) {
                accessor.setGender("");
                accessor.setMobName("");
                accessor.setOriginalId("");
            }
            return;
        }

        CompoundTag nbt = entity.getPersistentData();
        boolean playerNamed = nbt.getBoolean(NBT_PLAYER_NAMED);
        boolean isHumanoid = isHumanoid(id);

        if (accessor.getGender().isEmpty()) {
            RandomSource rand = entity.getRandom();
            String gender = rand.nextFloat() < data.chance ? "Female" : "Male";
            accessor.setGender(gender);
        }

        if (isHumanoid) {
            String currentName = accessor.getMobName();

            if (currentName.isEmpty() && !playerNamed) {
                if (nbt.contains(NBT_NAME)) {
                    accessor.setMobName(nbt.getString(NBT_NAME));
                } else if (
                        entity instanceof ZombieVillager && nbt.contains(NBT_ZOMBIE_SAVED_NAME)
                ) {
                    String savedName = nbt.getString(NBT_ZOMBIE_SAVED_NAME);
                    accessor.setMobName(savedName);
                    nbt.putString(NBT_NAME, savedName);
                } else {
                    RandomSource rand = entity.getRandom();
                    String gender = accessor.getGender();
                    if (!gender.isEmpty()) {
                        String generatedName = NameLoader.getRandomName(gender, rand);
                        if (!generatedName.isEmpty()) {
                            accessor.setMobName(generatedName);
                            if (entity instanceof ZombieVillager) {
                                nbt.putString(NBT_ZOMBIE_SAVED_NAME, generatedName);
                            }
                        }
                    }
                }
            }
        }

        if (entity.hasCustomName() && !playerNamed) {
            String customName = entity.getCustomName().getString();
            String cleanName = stripColorCodes(customName);

            if (!cleanName.isEmpty() && !cleanName.equals(accessor.getMobName())) {
                accessor.setMobName(cleanName);
                nbt.putBoolean(NBT_PLAYER_NAMED, true);

                entity.setCustomNameVisible(true);
            }
        }
        updateNameDisplay(entity, accessor, data, nbt);
    }

    private static void updateNameDisplay(
            LivingEntity entity,
            LivingEntityAccessor accessor,
            EntryData data,
            CompoundTag nbt
    ) {
        String mobName = accessor.getMobName();
        boolean playerNamed = nbt.getBoolean(NBT_PLAYER_NAMED);

        if (mobName == null || mobName.isEmpty()) {
            clearOurNameIfPresent(entity, mobName);
            return;
        }

        boolean shouldShowName;

        if (playerNamed) {
            shouldShowName = true;
        } else {
            shouldShowName = ConfigManager.CONFIG.general.showNames && data.force;
        }

        if (!shouldShowName) {
            clearOurNameIfPresent(entity, mobName);
            return;
        }

        MutableComponent displayName;

        if (playerNamed && entity.hasCustomName()) {
            displayName = entity.getCustomName().copy();
        } else {
            displayName = Component.literal(mobName);

            if (ConfigManager.CONFIG.general.showColors) {
                String gender = accessor.getGender();
                if (gender != null && !gender.isEmpty()) {
                    String hexColor = gender.equals("Male") ?
                            ConfigManager.CONFIG.colors.male :
                            ConfigManager.CONFIG.colors.female;
                    int color = parseHex(hexColor);
                    displayName.setStyle(displayName.getStyle().withColor(color));
                }
            }
        }

        if (!entity.hasCustomName() || !entity.getCustomName().equals(displayName)) {
            entity.setCustomName(displayName);
            entity.setCustomNameVisible(playerNamed);
        }
    }

    private static void clearOurNameIfPresent(LivingEntity entity, String ourName) {
        if (entity.hasCustomName() &&
                (ourName == null || entity.getCustomName().getString().equals(ourName))) {
            entity.setCustomName((Component) null);
            entity.setCustomNameVisible(false);
        }
    }

    @SubscribeEvent
    public static void onNameTagUse(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;

        if (event.getTarget() instanceof LivingEntity entity) {
            ItemStack stack = event.getItemStack();

            if (stack.getItem() == Items.NAME_TAG && stack.hasCustomHoverName()) {
                LivingEntityAccessor accessor = (LivingEntityAccessor) entity;
                CompoundTag nbt = entity.getPersistentData();

                String tagName = stack.getHoverName().getString();
                String cleanName = stripColorCodes(tagName);

                if (!cleanName.isEmpty()) {
                    accessor.setMobName(cleanName);
                    nbt.putBoolean(NBT_PLAYER_NAMED, true);

                    String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
                    EntryData data = findData(entityId);

                    if (data != null) {
                        updateNameDisplay(entity, accessor, data, nbt);
                    }

                    entity.setCustomNameVisible(true);

                    if (!event.getEntity().getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide() || ConfigManager.CONFIG == null) return;

        LivingEntity victim = event.getEntity();
        Entity killer = event.getSource().getDirectEntity();

        String victimId = BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType()).toString();

        if (ConfigManager.CONFIG.canBeInfected.contains(victimId) && killer instanceof LivingEntity livingKiller) {
            String killerId = BuiltInRegistries.ENTITY_TYPE.getKey(livingKiller.getType()).toString();

            if (ConfigManager.CONFIG.zombies.contains(killerId)) {
                Difficulty difficulty = victim.level().getDifficulty();
                float chance = difficulty == Difficulty.HARD ? 1.0f :
                        (difficulty == Difficulty.NORMAL ? 0.5f : 0.0f);

                if (victim.getRandom().nextFloat() < chance) {
                    ZombieVillager zombieVillager = EntityType.ZOMBIE_VILLAGER.create(victim.level());
                    if (zombieVillager != null) {
                        zombieVillager.absMoveTo(
                                victim.getX(), victim.getY(), victim.getZ(), victim.getYRot(), victim.getXRot()
                        );
                        CompoundTag victimNbt = new CompoundTag();

                        victim.saveWithoutId(victimNbt);
                        zombieVillager.load(victimNbt);

                        LivingEntityAccessor victimAccessor = (LivingEntityAccessor) victim;
                        LivingEntityAccessor zombieAccessor = (LivingEntityAccessor) zombieVillager;

                        CompoundTag victimPersistentNbt = victim.getPersistentData();
                        CompoundTag zombiePersistentNbt = zombieVillager.getPersistentData();

                        zombieAccessor.setOriginalId(victimId);
                        zombiePersistentNbt.putString(NBT_ORIGINAL_ID, victimId);

                        String victimGender = victimAccessor.getGender();
                        if (!victimGender.isEmpty()) {
                            zombieAccessor.setGender(victimGender);
                            zombiePersistentNbt.putString(NBT_GENDER, victimGender);
                        } else {
                            RandomSource rand = victim.getRandom();
                            EntryData data = findData(victimId);
                            if (data != null) {
                                String gender = rand.nextFloat() < data.chance ? "Female" : "Male";
                                zombieAccessor.setGender(gender);
                                zombiePersistentNbt.putString(NBT_GENDER, gender);
                            }
                        }

                        String victimName = victimAccessor.getMobName();
                        if (!victimName.isEmpty()) {
                            zombieAccessor.setMobName(victimName);
                            zombiePersistentNbt.putString(NBT_NAME, victimName);
                            zombiePersistentNbt.putString(NBT_ZOMBIE_SAVED_NAME, victimName);
                        } else if (isHumanoid(victimId)) {
                            RandomSource rand = victim.getRandom();
                            String gender = zombieAccessor.getGender();
                            if (!gender.isEmpty()) {
                                String generatedName = NameLoader.getRandomName(gender, rand);
                                zombieAccessor.setMobName(generatedName);
                                zombiePersistentNbt.putString(NBT_NAME, generatedName);
                                zombiePersistentNbt.putString(NBT_ZOMBIE_SAVED_NAME, generatedName);
                            }
                        }

                        if (victimPersistentNbt.contains(NBT_PLAYER_NAMED)) {
                            zombiePersistentNbt.putBoolean(NBT_PLAYER_NAMED,
                                    victimPersistentNbt.getBoolean(NBT_PLAYER_NAMED));
                        }

                        if (victim instanceof Villager villager) {
                            zombieVillager.setVillagerData(villager.getVillagerData());
                            zombieVillager.setVillagerXp(villager.getVillagerXp());

                            CompoundTag villagerData = new CompoundTag();

                            villager.addAdditionalSaveData(villagerData);

                            if (villagerData.contains("Gossips", 9)) {
                                zombiePersistentNbt.put("SavedGossips",
                                        villagerData.getList("Gossips", 10));
                            }
                            if (villagerData.contains("Offers", 10)) {
                                zombiePersistentNbt.put("SavedOffers",
                                        villagerData.getCompound("Offers"));
                            }
                        }

                        if (victim.hasCustomName()) {
                            String customName = victim.getCustomName().getString();
                            String cleanName = stripColorCodes(customName);
                            if (!cleanName.isEmpty()) {
                                zombieAccessor.setMobName(cleanName);
                                zombiePersistentNbt.putString(NBT_NAME, cleanName);
                                zombiePersistentNbt.putString(NBT_ZOMBIE_SAVED_NAME, cleanName);
                                zombiePersistentNbt.putBoolean(NBT_PLAYER_NAMED, true);

                                zombieVillager.setCustomName(victim.getCustomName());
                                zombieVillager.setCustomNameVisible(true);
                            }
                        }

                        if (!victim.isBaby()) {
                            zombieVillager.setBaby(false);
                        }

                        float healthRatio = victim.getHealth() / victim.getMaxHealth();
                        zombieVillager.setHealth(zombieVillager.getMaxHealth() * healthRatio);

                        if (victim.hasEffect(MobEffects.WEAKNESS)) {
                            zombieVillager.addEffect(new MobEffectInstance(
                                    net.minecraft.world.effect.MobEffects.WEAKNESS,
                                    200, 0));
                        }

                        updateEntityVisual(zombieVillager);
                        victim.level().addFreshEntity(zombieVillager);
                        victim.discard();
                    }
                }
            }
        }
    }

    private static String stripColorCodes(String text) {
        if (text == null || text.isEmpty()) return text;
        return COLOR_PATTERN.matcher(text).replaceAll("");
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
            if (hex.length() <= 6) {
                hex = "FF" + hex;
            }
            return (int) Long.parseLong(hex, 16);
        } catch (Exception e) {
            return 0xFFFFFFFF;
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
}
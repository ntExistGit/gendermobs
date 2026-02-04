package com.ntexist.gendermobs.mixin;

import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityTransformMixin {

    @Unique
    private static final String NBT_GENDER = "GM_Gender";
    @Unique
    private static final String NBT_NAME = "GM_Name";
    @Unique
    private static final String NBT_ORIGINAL_ID = "GM_OriginalId";
    @Unique
    private static final String NBT_PLAYER_NAMED = "GM_PlayerNamed";
    @Unique
    private static final String NBT_ZOMBIE_SAVED_NAME = "GM_ZombieSavedName";

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void gm$copyGenderAndName(Entity original, CallbackInfo ci) {
        if ((Object)this instanceof LivingEntity livingNew && original instanceof LivingEntity livingOriginal) {
            LivingEntityAccessor oldAccessor = (LivingEntityAccessor) livingOriginal;
            LivingEntityAccessor newAccessor = (LivingEntityAccessor) livingNew;
            CompoundTag oldNbt = livingOriginal.getPersistentData();
            CompoundTag newNbt = livingNew.getPersistentData();

            if (oldNbt.contains(NBT_GENDER)) {
                String gender = oldNbt.getString(NBT_GENDER);
                newNbt.putString(NBT_GENDER, gender);
                newAccessor.setGender(gender);
            }

            if (oldNbt.contains(NBT_NAME)) {
                String name = oldNbt.getString(NBT_NAME);
                newNbt.putString(NBT_NAME, name);
                newAccessor.setMobName(name);
            } else if (livingOriginal instanceof ZombieVillager && oldNbt.contains(NBT_ZOMBIE_SAVED_NAME)) {
                String zombieName = oldNbt.getString(NBT_ZOMBIE_SAVED_NAME);
                newNbt.putString(NBT_NAME, zombieName);
                newAccessor.setMobName(zombieName);
            }

            if (oldNbt.contains(NBT_ORIGINAL_ID)) {
                String originalId = oldNbt.getString(NBT_ORIGINAL_ID);
                newNbt.putString(NBT_ORIGINAL_ID, originalId);
                newAccessor.setOriginalId(originalId);
            }

            if (oldNbt.contains(NBT_PLAYER_NAMED)) {
                newNbt.putBoolean(NBT_PLAYER_NAMED, oldNbt.getBoolean(NBT_PLAYER_NAMED));
            }

            if (oldNbt.contains(NBT_ZOMBIE_SAVED_NAME)) {
                newNbt.putString(NBT_ZOMBIE_SAVED_NAME, oldNbt.getString(NBT_ZOMBIE_SAVED_NAME));
            }
        }
    }
}
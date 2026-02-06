package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.SpawnPipeline;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class LivingEntitySpawnMixin {

    @Inject(
            method = "finalizeSpawn",
            at = @At("TAIL")
    )
    private void mi_onSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType reason,
            SpawnGroupData spawnData,
            CompoundTag dataTag,
            CallbackInfoReturnable<SpawnGroupData> cir
    ) {
        Mob entity = (Mob) (Object)this;
        SpawnPipeline.onSpawn(entity);
    }
}

package com.ntexist.mcidentitymobs.mixin.compat.naturalist;

import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.enums.Gender;
import com.starfish_studios.naturalist.common.entity.Lion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Lion.class)
public abstract class LionCompatMixin {

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void onFinalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType reason,
            SpawnGroupData spawnData,
            CompoundTag dataTag,
            CallbackInfoReturnable<SpawnGroupData> cir
    ) {
        Lion lion = (Lion) (Object) this;
        Gender gender = MobIdentityAPI.getGender(lion);
        if (gender == null) return;

        boolean hasMane = (gender == Gender.MALE);
        lion.setHasMane(hasMane);
    }

    @Inject(method = "ageBoundaryReached", at = @At("HEAD"), cancellable = true)
    private void onAgeBoundaryReached(CallbackInfo ci) {
        Lion lion = (Lion) (Object) this;
        Gender gender = MobIdentityAPI.getGender(lion);
        if (gender != null) {
            boolean hasMane = (gender == Gender.MALE);
            lion.setHasMane(hasMane);
            ci.cancel();
        }
    }
}
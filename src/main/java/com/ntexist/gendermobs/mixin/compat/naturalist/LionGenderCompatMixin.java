package com.ntexist.gendermobs.mixin.compat.naturalist;

import com.ntexist.gendermobs.GenderAssigner;
import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import com.starfish_studios.naturalist.common.entity.Lion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Lion.class)
public abstract class LionGenderCompatMixin extends AgeableMob {

    @Shadow public abstract boolean hasMane();
    @Shadow public abstract void setHasMane(boolean hasMane);

    protected LionGenderCompatMixin(EntityType<? extends AgeableMob> type, Level level) {
        super(type, level);
    }

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void onFinalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            SpawnGroupData spawnGroupData,
            CompoundTag tag,
            CallbackInfoReturnable<SpawnGroupData> cir
    ) {
        if (this.level().isClientSide) return;

        LivingEntityAccessor accessor = (LivingEntityAccessor) this;

        GenderAssigner.updateEntityVisual((LivingEntity) (Object) this);

        if (!this.isBaby()) {
            String gender = accessor.getGender();
            if ("Male".equals(gender)) {
                this.setHasMane(true);
            } else if ("Female".equals(gender)) {
                this.setHasMane(false);
            }
        }
    }

    @Inject(method = "ageBoundaryReached", at = @At("TAIL"))
    private void onAgeBoundaryReached(CallbackInfo ci) {
        if (this.level().isClientSide) return;

        LivingEntityAccessor accessor = (LivingEntityAccessor) this;
        String gender = accessor.getGender();

        // Когда лев вырос, проверяем его пол из твоего аксессора
        if ("Male".equals(gender)) {
            this.setHasMane(true);
        } else if ("Female".equals(gender)) {
            this.setHasMane(false);
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void onAiStep(CallbackInfo ci) {
        if (!this.level().isClientSide && !this.isBaby()) {
            LivingEntityAccessor accessor = (LivingEntityAccessor) this;
            String gender = accessor.getGender();

            boolean needsMane = "Male".equals(gender);
            if (this.hasMane() != needsMane) {
                this.setHasMane(needsMane);
            }
        }
    }
}
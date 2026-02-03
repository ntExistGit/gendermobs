package com.ntexist.gendermobs.mixin;

import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityTransformMixin {

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void gm$copyGenderAndName(Entity original, CallbackInfo ci) {
        if ((Object)this instanceof LivingEntity livingNew && original instanceof LivingEntity livingOriginal) {
            LivingEntityAccessor oldAccessor = (LivingEntityAccessor) livingOriginal;
            LivingEntityAccessor newAccessor = (LivingEntityAccessor) livingNew;

            newAccessor.setGender(oldAccessor.getGender());
            newAccessor.setMobName(oldAccessor.getMobName());
            newAccessor.setOriginalId(oldAccessor.getOriginalId());
        }
    }
}
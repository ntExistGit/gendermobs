package com.ntexist.mcidentitymobs.mixin.compat.alexsmobs;

import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.enums.Gender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityElephant.class)
public abstract class ElephantCompatMixin {

    @Inject(
            method = "setTusked(Z)V",
            at = @At("HEAD"),
            remap = false
    )
    private void onSetTusked(boolean tusked, CallbackInfo ci) {
        if (tusked) {
            EntityElephant elephant = (EntityElephant) (Object) this;
            LivingEntityAccessor accessor = (LivingEntityAccessor) elephant;

            String genderStr = accessor.mcidentitymobs$getGender();

            if (genderStr == null || genderStr.isEmpty() ||
                    !Gender.fromString(genderStr).equals(Gender.MALE)) {
                accessor.mcidentitymobs$setGender(Gender.MALE.name().toLowerCase());
            }
        }
    }
}
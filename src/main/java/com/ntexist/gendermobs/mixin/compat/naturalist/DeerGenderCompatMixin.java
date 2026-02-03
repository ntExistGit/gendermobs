package com.ntexist.gendermobs.mixin.compat.naturalist;

import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.starfish_studios.naturalist.common.entity.Deer", remap = false)
public abstract class DeerGenderCompatMixin {

    @Inject(method = "hasAntlers", at = @At("HEAD"), cancellable = true)
    private void gm$syncDeerAntlers(CallbackInfoReturnable<Boolean> cir) {
        Object self = (Object) this;

        if (self instanceof LivingEntityAccessor accessor) {
            String gender = accessor.getGender();

            if ("Male".equals(gender)) {
                cir.setReturnValue(true);
            } else if ("Female".equals(gender)) {
                cir.setReturnValue(false);
            }
        }
    }
}

package com.ntexist.gendermobs.mixin.compat.naturalist;


import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import com.starfish_studios.naturalist.client.model.LionModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "LionModel.class", remap = false)
public abstract class LionGenderCompatMixin {

    @Inject(method = "isFemale", at = @At("HEAD"), cancellable = true)
    private void gm$syncLionGender(CallbackInfoReturnable<Boolean> cir) {
        Object self = (Object) this;
        if (self instanceof LivingEntityAccessor accessor) {
            String gender = accessor.getGender();
            if ("Female".equals(gender)) {
                cir.setReturnValue(true);
            } else if ("Male".equals(gender)) {
                cir.setReturnValue(false);
            }
        }
    }
}
package com.ntexist.mcidentitymobs.mixin.compat.alexsmobs;

import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
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

            Gender currentGender = MobIdentityAPI.getGender(elephant);

            if (currentGender != Gender.MALE) {
                MobIdentityAPI.setGender(elephant, Gender.MALE);
            }
        }
    }
}
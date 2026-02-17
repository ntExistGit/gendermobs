package com.ntexist.mcidentitymobs.mixin.compat.alexsmobs;

import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.compat.IScaleProvider;
import com.ntexist.mcidentitymobs.enums.Gender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMoose.class)
public abstract class MooseCompatMixin implements IScaleProvider {

    @Inject(
            method = "isAntlered",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void onIsAntlered(CallbackInfoReturnable<Boolean> cir) {
        EntityMoose moose = (EntityMoose) (Object) this;

        Gender gender = MobIdentityAPI.getGender(moose);
        if (gender == Gender.FEMALE) {
            cir.setReturnValue(false);
        }
    }
}
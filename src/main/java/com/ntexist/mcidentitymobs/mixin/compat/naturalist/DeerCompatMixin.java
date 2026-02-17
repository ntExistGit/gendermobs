package com.ntexist.mcidentitymobs.mixin.compat.naturalist;

import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.enums.Gender;
import com.starfish_studios.naturalist.client.model.DeerModel;
import com.starfish_studios.naturalist.common.entity.Deer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

@Mixin(DeerModel.class)
public abstract class DeerCompatMixin {

    @Inject(
            method = "setCustomAnimations(Lcom/starfish_studios/naturalist/common/entity/Deer;JLsoftware/bernie/geckolib/core/animation/AnimationState;)V",
            at = @At("TAIL"),
            remap = false
    )
    private void onSetCustomAnimations(Deer entity, long instanceId, AnimationState<Deer> animationState, CallbackInfo ci) {
        if (animationState == null || entity.isBaby()) return;

        Gender gender = MobIdentityAPI.getGender(entity);
        if (gender == null) return;

        CoreGeoBone antlers = ((DeerModel)(Object)this).getAnimationProcessor().getBone("antlers");
        if (antlers == null) return;

        antlers.setHidden(gender == Gender.FEMALE);
    }
}
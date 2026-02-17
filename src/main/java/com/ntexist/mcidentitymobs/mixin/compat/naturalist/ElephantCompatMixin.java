package com.ntexist.mcidentitymobs.mixin.compat.naturalist;

import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.enums.Gender;
import com.starfish_studios.naturalist.client.model.ElephantModel;
import com.starfish_studios.naturalist.common.entity.Elephant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

@Mixin(ElephantModel.class)
public abstract class ElephantCompatMixin {

    @Inject(
            method = "setCustomAnimations(Lcom/starfish_studios/naturalist/common/entity/Elephant;JLsoftware/bernie/geckolib/core/animation/AnimationState;)V",
            at = @At("TAIL"),
            remap = false
    )
    private void onSetCustomAnimations(Elephant entity, long instanceId, AnimationState<Elephant> animationState, CallbackInfo ci) {
        if (animationState == null) return;

        Gender gender = MobIdentityAPI.getGender(entity);
        if (gender == null) return;

        CoreGeoBone bigTusks = ((ElephantModel)(Object)this).getAnimationProcessor().getBone("tusks");
        CoreGeoBone smallTusks = ((ElephantModel)(Object)this).getAnimationProcessor().getBone("baby_tusks");
        if (bigTusks == null || smallTusks == null) return;

        if (entity.isBaby()) {
            smallTusks.setHidden(gender == Gender.FEMALE);
            bigTusks.setHidden(true);
        } else {
            smallTusks.setHidden(true);
            bigTusks.setHidden(gender == Gender.FEMALE);
        }
    }
}
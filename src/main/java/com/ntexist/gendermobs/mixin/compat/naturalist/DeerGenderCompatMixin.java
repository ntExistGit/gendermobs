package com.ntexist.gendermobs.mixin.compat.naturalist;

import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import com.starfish_studios.naturalist.client.model.DeerModel;
import com.starfish_studios.naturalist.common.entity.Deer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

@Mixin(DeerModel.class)
public abstract class DeerGenderCompatMixin extends GeoModel<Deer> {

    @Inject(
            method = "setCustomAnimations(Lcom/starfish_studios/naturalist/common/entity/Deer;JLsoftware/bernie/geckolib/core/animation/AnimationState;)V",
            at = @At("TAIL"),
            remap = false
    )
    private void onSetCustomAnimations(Deer entity, long instanceId, AnimationState<Deer> animationState, CallbackInfo ci) {
        if (animationState == null) return;

        GeoBone antlers = (GeoBone) ((DeerModel)(Object)this).getAnimationProcessor().getBone("antlers");

        if (antlers != null && entity instanceof LivingEntityAccessor accessor) {
            String gender = accessor.getGender();
            if (gender != null) {
                boolean shouldShowAntlers = !entity.isBaby() && gender.equalsIgnoreCase("Male");
                antlers.setHidden(!shouldShowAntlers);
            }
        }
    }
}
package com.ntexist.mcidentitymobs.mixin.compat.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.client.render.RenderMoose;
import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.compat.IScaleProvider;
import com.ntexist.mcidentitymobs.enums.Gender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderMoose.class)
public class RenderMooseCompatMixin implements IScaleProvider {

    @Inject(
            method = "scale",
            at = @At("RETURN"),
            remap = false
    )
    private void mi$onScale(EntityMoose entity, PoseStack poseStack,
                            float partialTickTime, CallbackInfo ci) {

        if (entity.isBaby()) return;
        if (!(entity instanceof LivingEntityAccessor accessor)) return;

        String genderStr = accessor.mcidentitymobs$getGender();
        if (genderStr == null || genderStr.isEmpty()) return;

        if (Gender.fromString(genderStr) == Gender.FEMALE) {
            float scale = mcidentitymobs$getScaleForEntity(entity, true);
            if (scale != 1.0F) {
                poseStack.scale(scale, scale, scale);
            }
        }
    }
}
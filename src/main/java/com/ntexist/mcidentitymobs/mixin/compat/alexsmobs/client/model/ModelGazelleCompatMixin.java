package com.ntexist.mcidentitymobs.mixin.compat.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.client.model.ModelGazelle;
import com.github.alexthe666.alexsmobs.entity.EntityGazelle;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.enums.Gender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelGazelle.class)
public abstract class ModelGazelleCompatMixin {

    @Final @Shadow private AdvancedModelBox hornL;
    @Final @Shadow private AdvancedModelBox hornR;

    @Inject(
            method = "setupAnim(Lcom/github/alexthe666/alexsmobs/entity/EntityGazelle;FFFFF)V",
            at = @At("TAIL"),
            remap = false
    )
    private void sizeHornsForFemales(
            EntityGazelle entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci
    ) {
        if (entity == null || entity.isBaby()) return;

        Gender gender = MobIdentityAPI.getGender(entity);
        if (gender == null) return;

        boolean isFemale = gender == Gender.FEMALE;
        float hornScaleY = isFemale ? 0.7F : 1.0F;
        float hornScaleXZ = isFemale ? 0.5F : 1.0F;

        this.hornL.setScale(hornScaleXZ, hornScaleY, hornScaleXZ);
        this.hornR.setScale(hornScaleXZ, hornScaleY, hornScaleXZ);
    }
}
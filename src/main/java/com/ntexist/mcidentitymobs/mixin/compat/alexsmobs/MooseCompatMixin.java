package com.ntexist.mcidentitymobs.mixin.compat.alexsmobs;

import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.compat.IScaleProvider;
import com.ntexist.mcidentitymobs.enums.Gender;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
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
        if (moose.isBaby()) return;

        LivingEntityAccessor accessor = (LivingEntityAccessor) moose;
        String genderStr = accessor.mcidentitymobs$getGender();

        Gender gender = Gender.fromString(genderStr);
        if (gender == Gender.FEMALE) {
            cir.setReturnValue(false);
        }
    }
}
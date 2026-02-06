package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.MobIdentityAPI;
import com.ntexist.mcidentitymobs.Gender;
import com.ntexist.mcidentitymobs.NameService;
import com.ntexist.mcidentitymobs.ColorService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityLoadMixin {

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void mi_onLoad(CompoundTag tag, CallbackInfo ci) {
        LivingEntity e = (LivingEntity)(Object)this;

        Gender gender = MobIdentityAPI.getGender(e);
        if (gender != null) {
            NameService.handleName(e, gender);
            ColorService.applyColorIfNeeded(e, gender);
        }
    }
}

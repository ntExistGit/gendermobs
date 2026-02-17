package com.ntexist.mcidentitymobs.mixin.compat.alexsmobs;

import com.github.alexthe666.alexsmobs.entity.EntityCentipedeBody;
import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityCentipedeBody.class)
public abstract class CentipedePartCompatMixin {
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void mcidentitymobs$inheritGender(CallbackInfo ci) {
        EntityCentipedeBody part = (EntityCentipedeBody) (Object) this;

        LivingEntity parent = (LivingEntity) part.getParent();
        if (parent == null) return;

        if (!(parent instanceof LivingEntityAccessor parentAccessor)) return;
        if (!(part instanceof LivingEntityAccessor partAccessor)) return;

        String parentGender = parentAccessor.mcidentitymobs$getGender();
        if (parentGender != null && !parentGender.isEmpty()) {
            String partGender = partAccessor.mcidentitymobs$getGender();
            if (!parentGender.equals(partGender)) {
                partAccessor.mcidentitymobs$setGender(parentGender);
            }
        }
    }
}

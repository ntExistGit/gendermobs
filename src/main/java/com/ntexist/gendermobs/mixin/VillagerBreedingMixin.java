package com.ntexist.gendermobs.mixin;

import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerBreedingMixin {

    @Inject(method = "canBreed", at = @At("RETURN"), cancellable = true)
    private void gm$checkGenderReadiness(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        Villager self = (Villager) (Object) this;

        var partnerOpt = self.getBrain().getMemory(MemoryModuleType.BREED_TARGET);

        if (partnerOpt.isPresent() && partnerOpt.get() instanceof Villager partner) {

            LivingEntityAccessor selfAcc = (LivingEntityAccessor) self;
            LivingEntityAccessor otherAcc = (LivingEntityAccessor) partner;

            String selfGender = selfAcc.getGender();
            String otherGender = otherAcc.getGender();

            if (!selfGender.isEmpty() && !otherGender.isEmpty() && selfGender.equals(otherGender)) {
                cir.setReturnValue(false);
            }
        }
    }
}
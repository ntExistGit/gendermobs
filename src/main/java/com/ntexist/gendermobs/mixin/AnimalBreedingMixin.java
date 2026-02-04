package com.ntexist.gendermobs.mixin;

import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class AnimalBreedingMixin {

    @Inject(method = "canMate", at = @At("HEAD"), cancellable = true)
    private void gm$checkGenderBreeding(Animal other, CallbackInfoReturnable<Boolean> cir) {
        Animal self = (Animal) (Object) this;

        if (self == other) {
            return;
        }

        LivingEntityAccessor selfAccessor = (LivingEntityAccessor) self;
        LivingEntityAccessor otherAccessor = (LivingEntityAccessor) other;

        String selfGender = selfAccessor.getGender();
        String otherGender = otherAccessor.getGender();

        boolean selfHasGender = selfGender != null && !selfGender.isEmpty();
        boolean otherHasGender = otherGender != null && !otherGender.isEmpty();

        if (selfHasGender && otherHasGender) {
            if (selfGender.equals(otherGender)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
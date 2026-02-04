package com.ntexist.gendermobs.mixin;

import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Villager.class)
public abstract class VillagerBreedingMixin {

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void gm$cleanInvalidPartners(CallbackInfo ci) {
        Villager self = (Villager) (Object) this;

        if (!self.isAlive() || self.isBaby()) {
            return;
        }

        Brain<Villager> brain = self.getBrain();

        // ИСПРАВЛЕНО: AgeableMob вместо LivingEntity
        Optional<AgeableMob> partnerOpt = brain.getMemory(MemoryModuleType.BREED_TARGET);

        if (partnerOpt.isPresent() && partnerOpt.get() instanceof Villager partner) {
            if (shouldPreventBreeding(self, partner)) {
                brain.eraseMemory(MemoryModuleType.BREED_TARGET);
                if (partner.isAlive() && !partner.isBaby()) {
                    Brain<Villager> partnerBrain = partner.getBrain();
                    partnerBrain.eraseMemory(MemoryModuleType.BREED_TARGET);
                }
            }
        }
    }

    @Inject(method = "canBreed", at = @At("RETURN"), cancellable = true)
    private void gm$finalCheck(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return;

        Villager self = (Villager) (Object) this;
        Brain<Villager> brain = self.getBrain();

        Optional<AgeableMob> partnerOpt = brain.getMemory(MemoryModuleType.BREED_TARGET);

        if (partnerOpt.isPresent() && partnerOpt.get() instanceof Villager partner) {
            if (shouldPreventBreeding(self, partner)) {
                cir.setReturnValue(false);
            }
        }
    }

    private boolean shouldPreventBreeding(Villager self, Villager partner) {
        if (!self.isAlive() || !partner.isAlive() ||
                self.isBaby() || partner.isBaby()) {
            return true;
        }

        LivingEntityAccessor selfAcc = (LivingEntityAccessor) self;
        LivingEntityAccessor partnerAcc = (LivingEntityAccessor) partner;

        String selfGender = selfAcc.getGender();
        String partnerGender = partnerAcc.getGender();

        boolean selfHasGender = selfGender != null && !selfGender.isEmpty();
        boolean partnerHasGender = partnerGender != null && !partnerGender.isEmpty();

        if (!selfHasGender || !partnerHasGender) {
            return true;
        }
        return selfGender.equals(partnerGender);
    }
}
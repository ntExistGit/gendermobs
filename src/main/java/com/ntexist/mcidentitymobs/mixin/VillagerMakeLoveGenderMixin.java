package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.enums.Gender;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(VillagerMakeLove.class)
public abstract class VillagerMakeLoveGenderMixin {

    @Inject(
            method = "isBreedingPossible(Lnet/minecraft/world/entity/npc/Villager;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void blockSameGender(Villager villager, CallbackInfoReturnable<Boolean> cir) {
        Optional<AgeableMob> targetOpt = villager.getBrain().getMemory(MemoryModuleType.BREED_TARGET);
        if (targetOpt.isEmpty() || !(targetOpt.get() instanceof Villager partner)) {
            return;
        }

        Gender genderA = MobIdentityAPI.getGender(villager);
        Gender genderB = MobIdentityAPI.getGender(partner);

        if (genderA != null && genderB != null && genderA == genderB) {
            cir.setReturnValue(false);
        }
    }
}
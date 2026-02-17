package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.enums.Gender;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public class AnimalBreedMixin {

    @Inject(method = "canMate(Lnet/minecraft/world/entity/animal/Animal;)Z", at = @At("HEAD"), cancellable = true)
    private void preventSameGenderMate(Animal otherParent, CallbackInfoReturnable<Boolean> cir) {
        Animal parentA = (Animal) (Object) this;

        Gender genderA = MobIdentityAPI.getGender(parentA);
        Gender genderB = MobIdentityAPI.getGender(otherParent);

        if (genderA != null && genderB != null && genderA == genderB) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "spawnChildFromBreeding(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onBreed(ServerLevel level, Animal otherParent, CallbackInfo ci) {
        Animal parentA = (Animal) (Object) this;

        Gender genderA = MobIdentityAPI.getGender(parentA);
        Gender genderB = MobIdentityAPI.getGender(otherParent);

        if (genderA != null && genderB != null && genderA == genderB) {
            ci.cancel();
        }
    }
}
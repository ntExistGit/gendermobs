package com.ntexist.gendermobs.mixin;

import com.ntexist.gendermobs.accessor.LivingEntityAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillager.class)
public abstract class ZombieConversionMixin {

    @Inject(
            method = "finishConversion(Lnet/minecraft/server/level/ServerLevel;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gm$restoreOriginalMob(ServerLevel world, CallbackInfo ci) {
        ZombieVillager zombie = (ZombieVillager) (Object) this;
        LivingEntityAccessor accessor = (LivingEntityAccessor) zombie;
        String originalId = accessor.getOriginalId();

        if (!originalId.isEmpty() && !originalId.equals("minecraft:villager")) {
            ResourceLocation id = ResourceLocation.tryParse(originalId);
            if (id == null) return;

            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type != null) {
                var recovered = type.create(world);
                if (recovered instanceof LivingEntity) {
                    recovered.absMoveTo(zombie.getX(), zombie.getY(), zombie.getZ(),
                            zombie.getYRot(), zombie.getXRot());

                    CompoundTag zombieNbt = new CompoundTag();
                    zombie.saveWithoutId(zombieNbt);
                    recovered.load(zombieNbt);
                    recovered.restoreFrom(zombie);
                    world.addFreshEntity(recovered);
                    zombie.discard();
                    ci.cancel();
                }
            }
        }
    }
}
package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.IdentityStorage;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public class NameTagMixin {

    @Inject(method = "interactLivingEntity", at = @At("TAIL"))
    private void mi_onNameTag(
            ItemStack stack,
            Player player,
            LivingEntity entity,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!entity.level().isClientSide) {
            String name = entity.getName().getString();
            IdentityStorage.setName(entity, name);
            IdentityStorage.setPlayerNamed(entity, true);
        }
    }
}
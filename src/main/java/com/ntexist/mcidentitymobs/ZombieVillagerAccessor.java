package com.ntexist.mcidentitymobs.mixin;

import net.minecraft.world.entity.monster.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;

@Mixin(ZombieVillager.class)
public interface ZombieVillagerAccessor {
    @Invoker("startConverting")
    void callStartConverting(UUID playerUUID, int conversionTime);
}
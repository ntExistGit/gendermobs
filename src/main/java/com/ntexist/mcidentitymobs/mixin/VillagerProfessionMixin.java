package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.TextureCounts;
import com.ntexist.mcidentitymobs.enums.Gender;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerProfessionMixin {

    @Unique
    private VillagerProfession mcidentitymobs$oldProfession;

    @Inject(method = "setVillagerData", at = @At("HEAD"))
    private void captureOldProfession(VillagerData newData, CallbackInfo ci) {
        Villager self = (Villager) (Object) this;
        this.mcidentitymobs$oldProfession = self.getVillagerData().getProfession();
    }

    @Inject(method = "setVillagerData", at = @At("TAIL"))
    private void onVillagerDataChange(VillagerData newData, CallbackInfo ci) {
        Villager self = (Villager) (Object) this;
        VillagerProfession newProfession = newData.getProfession();

        Gender gender = MobIdentityAPI.getGender(self);
        if (gender == null) return;
        TextureCounts.GenderCounts counts = (gender == Gender.MALE)
                ? ConfigManager.TEXTURE_COUNTS_DATA.male
                : ConfigManager.TEXTURE_COUNTS_DATA.female;
        if (counts == null) return;

        int currentIndex = MobIdentityAPI.getClothIndex(self);

        int maxIndex;
        if (self.isBaby()) {
            maxIndex = counts.clothing.baby;
        } else {
            ResourceLocation profId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(newProfession);
            String profession = profId == null ? "none" : profId.getPath();
            maxIndex = counts.clothing.byProfession.getOrDefault(profession, counts.clothing.none);
        }

        if (this.mcidentitymobs$oldProfession != newProfession || currentIndex <= 0 || currentIndex > maxIndex) {
            MobIdentityAPI.setClothIndex(self, 0);
            MobIdentityAPI.assignClothIndex(self, counts);
        }
    }
}
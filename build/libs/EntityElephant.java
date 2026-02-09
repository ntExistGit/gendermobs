//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.ElephantAIFollowCaravan;
import com.github.alexthe666.alexsmobs.entity.ai.ElephantAIForageLeaves;
import com.github.alexthe666.alexsmobs.entity.ai.ElephantAIVillagerRide;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags.Items;
import net.minecraftforge.network.NetworkHooks;

public class EntityElephant extends TamableAnimal implements ITargetsDroppedItems, IAnimatedEntity {
    public static final Animation ANIMATION_TRUMPET_0 = Animation.create(20);
    public static final Animation ANIMATION_TRUMPET_1 = Animation.create(30);
    public static final Animation ANIMATION_CHARGE_PREPARE = Animation.create(25);
    public static final Animation ANIMATION_STOMP = Animation.create(20);
    public static final Animation ANIMATION_FLING = Animation.create(25);
    public static final Animation ANIMATION_EAT = Animation.create(30);
    public static final Animation ANIMATION_BREAKLEAVES = Animation.create(20);
    protected static final EntityDimensions TUSKED_SIZE = EntityDimensions.fixed(3.7F, 3.75F);
    private static final EntityDataAccessor<Boolean> TUSKED;
    private static final EntityDataAccessor<Boolean> SITTING;
    private static final EntityDataAccessor<Boolean> STANDING;
    private static final EntityDataAccessor<Boolean> CHESTED;
    private static final EntityDataAccessor<Integer> CARPET_COLOR;
    private static final EntityDataAccessor<Boolean> TRADER;
    public static final Map<DyeColor, Item> DYE_COLOR_ITEM_MAP;
    private static final ResourceLocation TRADER_LOOT;
    public boolean forcedSit = false;
    public float prevSitProgress;
    public float sitProgress;
    public float prevStandProgress;
    public float standProgress;
    public int maxStandTime = 75;
    public boolean aiItemFlag = false;
    public SimpleContainer elephantInventory;
    private int animationTick;
    private Animation currentAnimation;
    private final boolean hasTuskedAttributes = false;
    private int standingTime = 0;
    @Nullable
    private EntityElephant caravanHead;
    @Nullable
    private EntityElephant caravanTail;
    private boolean hasChestVarChanged = false;
    private boolean hasChargedSpeed = false;
    private boolean charging;
    private int chargeCooldown = 0;
    private int chargingTicks = 0;
    @Nullable
    private UUID blossomThrowerUUID = null;
    private int despawnDelay = 47999;

    protected EntityElephant(EntityType type, Level world) {
        super(type, world);
        this.initElephantInventory();
        this.setMaxUpStep(1.1F);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, (double)85.0F).add(Attributes.FOLLOW_RANGE, (double)32.0F).add(Attributes.KNOCKBACK_RESISTANCE, (double)0.9F).add(Attributes.ATTACK_DAMAGE, (double)10.0F).add(Attributes.MOVEMENT_SPEED, (double)0.35F);
    }

    @Nullable
    public static DyeColor getCarpetColor(ItemStack stack) {
        Block lvt_1_1_ = Block.byItem(stack.getItem());
        return lvt_1_1_ instanceof WoolCarpetBlock ? ((WoolCarpetBlock)lvt_1_1_).getColor() : null;
    }

    protected SoundEvent getAmbientSound() {
        return (SoundEvent)AMSoundRegistry.ELEPHANT_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return (SoundEvent)AMSoundRegistry.ELEPHANT_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return (SoundEvent)AMSoundRegistry.ELEPHANT_DIE.get();
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.elephantSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    private void initElephantInventory() {
        SimpleContainer animalchest = this.elephantInventory;
        this.elephantInventory = new SimpleContainer(this, 54) {
            public boolean stillValid(Player player) {
                return EntityElephant.this.isAlive() && !EntityElephant.this.isInsidePortal;
            }
        };
        if (animalchest != null) {
            int i = Math.min(animalchest.getContainerSize(), this.elephantInventory.getContainerSize());

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = animalchest.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.elephantInventory.setItem(j, itemstack.copy());
                }
            }
        }

    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new AdvancedPathNavigateNoTeleport(this, worldIn, true);
    }

    public int getMaxHeadYRot() {
        return super.getMaxHeadYRot();
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isSitting() || this.getAnimation() == ANIMATION_CHARGE_PREPARE && this.getAnimationTick() < 10;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, (double)1.0F, true));
        this.goalSelector.addGoal(2, new PanicGoal(this));
        this.goalSelector.addGoal(2, new ElephantAIVillagerRide(this, (double)1.0F));
        this.goalSelector.addGoal(3, new BreedGoal(this, (double)1.0F));
        this.goalSelector.addGoal(4, new TemptGoal(this, (double)1.0F, Ingredient.of(AMTagRegistry.ELEPHANT_TAMEABLES), false));
        this.goalSelector.addGoal(5, new ElephantAIForageLeaves(this));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, (double)1.0F));
        this.goalSelector.addGoal(7, new ElephantAIFollowCaravan(this, (double)0.5F));
        this.goalSelector.addGoal(8, new AvoidEntityGoal(this, Bee.class, 6.0F, (double)1.0F, 1.2));
        this.goalSelector.addGoal(9, new AIWalkIdle(this, (double)0.5F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new CreatureAITargetItems(this, false));
    }

    public boolean isFood(ItemStack stack) {
        Item item = stack.getItem();
        return this.isTame() && stack.is(AMTagRegistry.ELEPHANT_BREEDABLES);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound((SoundEvent)AMSoundRegistry.ELEPHANT_WALK.get(), 0.2F, 1.0F);
        } else {
            super.playStepSound(pos, state);
        }

    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        for(Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player) {
                return (LivingEntity)passenger;
            }
        }

        return null;
    }

    @Nullable
    public AbstractVillager getControllingVillager() {
        for(Entity passenger : this.getPassengers()) {
            if (passenger instanceof AbstractVillager) {
                return (AbstractVillager)passenger;
            }
        }

        return null;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TUSKED, false);
        this.entityData.define(SITTING, false);
        this.entityData.define(STANDING, false);
        this.entityData.define(CHESTED, false);
        this.entityData.define(TRADER, false);
        this.entityData.define(CARPET_COLOR, -1);
    }

    public void tick() {
        super.tick();
        this.prevSitProgress = this.sitProgress;
        this.prevStandProgress = this.standProgress;
        if (this.isSitting()) {
            if (this.sitProgress < 5.0F) {
                ++this.sitProgress;
            }
        } else if (this.sitProgress > 0.0F) {
            --this.sitProgress;
        }

        if (this.isStanding()) {
            if (this.standProgress < 5.0F) {
                this.standProgress += 0.5F;
            }
        } else if (this.standProgress > 0.0F) {
            this.standProgress -= 0.5F;
        }

        if (this.isStanding() && ++this.standingTime > this.maxStandTime) {
            this.setStanding(false);
            this.standingTime = 0;
            this.maxStandTime = 75 + this.random.nextInt(50);
        }

        if (this.isSitting() && this.isStanding()) {
            this.setStanding(false);
        }

        if (this.hasChestVarChanged && this.elephantInventory != null && !this.isChested()) {
            for(int i = 3; i < 18; ++i) {
                if (!this.elephantInventory.getItem(i).isEmpty()) {
                    if (!this.level().isClientSide) {
                        this.spawnAtLocation(this.elephantInventory.getItem(i), 1.0F);
                    }

                    this.elephantInventory.removeItemNoUpdate(i);
                }
            }

            this.hasChestVarChanged = false;
        }

        if (this.isTusked() && !this.isBaby()) {
            this.refreshDimensions();
        }

        if (this.charging) {
            ++this.chargingTicks;
        }

        if (!this.getMainHandItem().isEmpty() && this.canTargetItem(this.getMainHandItem())) {
            if (this.getAnimation() == NO_ANIMATION) {
                this.setAnimation(ANIMATION_EAT);
            }

            if (this.getAnimation() == ANIMATION_EAT && this.getAnimationTick() == 17) {
                this.eatItemEffect(this.getMainHandItem());
                if (this.getMainHandItem().is(AMTagRegistry.ELEPHANT_TAMEABLES) && !this.isTame() && (!this.isTusked() || this.isBaby()) && this.blossomThrowerUUID != null) {
                    if (this.random.nextInt(3) != 0) {
                        this.level().broadcastEntityEvent(this, (byte)6);
                    } else {
                        this.setTame(true);
                        this.setOwnerUUID(this.blossomThrowerUUID);
                        Player player = this.level().getPlayerByUUID(this.blossomThrowerUUID);
                        if (player != null) {
                            this.tame(player);
                        }

                        for(Entity passenger : this.getPassengers()) {
                            passenger.removeVehicle();
                        }

                        this.level().broadcastEntityEvent(this, (byte)7);
                    }
                }

                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                this.heal(10.0F);
            }
        }

        if (this.chargeCooldown > 0) {
            --this.chargeCooldown;
        }

        if (this.charging) {
            ++this.chargingTicks;
        } else {
            this.chargingTicks = 0;
        }

        if (this.getAnimation() == ANIMATION_CHARGE_PREPARE) {
            this.yBodyRot = this.getYRot();
            if (this.getAnimationTick() == 20) {
                this.charging = true;
            }
        }

        if (this.getControllingPassenger() != null && this.charging && this.chargingTicks > 100) {
            this.charging = false;
            this.chargeCooldown = 200;
        }

        LivingEntity target = this.getTarget();
        double maxAttackMod = (double)0.0F;
        if (this.getControllingPassenger() != null && this.getControllingPassenger() instanceof Player) {
            Player rider = (Player)this.getControllingPassenger();
            if (rider.getLastHurtMob() != null && !this.isAlliedTo(rider.getLastHurtMob())) {
                UUID preyUUID = rider.getLastHurtMob().getUUID();
                if (!this.getUUID().equals(preyUUID)) {
                    target = rider.getLastHurtMob();
                    maxAttackMod = (double)4.0F;
                }
            }
        }

        if (!this.level().isClientSide && target != null) {
            if (this.distanceTo(target) > this.getBbWidth() * 0.5F + 0.5F && this.getControllingPassenger() == null && this.isTusked() && this.hasLineOfSight(target) && this.getAnimation() == NO_ANIMATION && !this.charging && this.chargeCooldown == 0) {
                this.setAnimation(ANIMATION_CHARGE_PREPARE);
            }

            if (this.getAnimation() == ANIMATION_CHARGE_PREPARE && this.getControllingPassenger() == null) {
                this.lookAt(target, 360.0F, 30.0F);
                this.yBodyRot = this.getYRot();
                if (this.getAnimationTick() == 20) {
                    this.charging = true;
                }
            }

            if ((double)this.distanceTo(target) < (double)10.0F && this.charging) {
                this.setAnimation(ANIMATION_FLING);
            }

            if ((double)this.distanceTo(target) < 2.1 && this.charging) {
                target.knockback((double)1.0F, target.getX() - this.getX(), target.getZ() - this.getZ());
                target.hasImpulse = true;
                target.setDeltaMovement(target.getDeltaMovement().add((double)0.0F, (double)0.7F, (double)0.0F));
                target.hurt(this.damageSources().mobAttack(this), 2.4F * (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                this.launch(target, true);
                this.charging = false;
                this.chargeCooldown = 400;
            }

            double dist = (double)this.distanceTo(target);
            if (dist < (double)4.5F + maxAttackMod && this.getAnimation() == ANIMATION_FLING && this.getAnimationTick() == 15) {
                target.knockback((double)1.0F, target.getX() - this.getX(), target.getZ() - this.getZ());
                target.setDeltaMovement(target.getDeltaMovement().add((double)0.0F, (double)0.3F, (double)0.0F));
                this.launch(target, false);
                target.hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            }

            if (dist < (double)4.5F + maxAttackMod && this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() == 17) {
                target.knockback((double)0.3F, target.getX() - this.getX(), target.getZ() - this.getZ());
                target.hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            }
        }

        if (!this.level().isClientSide && this.getTarget() == null && this.getControllingPassenger() == null) {
            this.charging = false;
        }

        if (this.charging && !this.hasChargedSpeed) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)0.65F);
            this.hasChargedSpeed = true;
        }

        if (!this.charging && this.hasChargedSpeed) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)0.35F);
            this.hasChargedSpeed = false;
        }

        if (!this.level().isClientSide && this.getRandom().nextInt(400) == 0 && this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_TRUMPET_0 : ANIMATION_TRUMPET_1);
        }

        if (this.getAnimation() == ANIMATION_TRUMPET_0 && this.getAnimationTick() == 8 || this.getAnimation() == ANIMATION_TRUMPET_1 && this.getAnimationTick() == 4) {
            this.gameEvent(GameEvent.ENTITY_ROAR);
            this.playSound((SoundEvent)AMSoundRegistry.ELEPHANT_TRUMPET.get(), this.getSoundVolume(), this.getVoicePitch());
        }

        if (this.isAlive() && this.charging) {
            for(Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate((double)1.0F))) {
                if ((!this.isTame() || !this.isAlliedTo(entity)) && (this.isTame() || !(entity instanceof EntityElephant)) && entity != this) {
                    entity.hurt(this.damageSources().mobAttack(this), 8.0F + this.random.nextFloat() * 8.0F);
                    this.launch(entity, true);
                }
            }

            this.setMaxUpStep(2.0F);
        } else {
            this.setMaxUpStep(1.1F);
        }

        if (!this.isTame() && this.isTrader() && !this.level().isClientSide) {
            this.tryDespawn();
        }

        if (this.getTarget() != null && !this.getTarget().isAlive()) {
            this.setTarget((LivingEntity)null);
        }

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void aiStep() {
        super.aiStep();
        if (this.isBaby() && this.getEyeHeight() > this.getBbHeight()) {
            this.refreshDimensions();
        }

    }

    private boolean canDespawn() {
        return !this.isTame() && this.isTrader();
    }

    private void tryDespawn() {
        if (this.canDespawn()) {
            if (this.getControllingVillager() instanceof WanderingTrader) {
                int riderDelay = ((WanderingTrader)this.getControllingVillager()).getDespawnDelay();
                if (riderDelay > 0) {
                    this.despawnDelay = riderDelay;
                }
            }

            --this.despawnDelay;
            if (this.despawnDelay <= 0) {
                this.dropLeash(true, false);
                this.elephantInventory.clearContent();
                if (this.getControllingVillager() != null) {
                    this.getControllingVillager().remove(RemovalReason.DISCARDED);
                }

                this.remove(RemovalReason.DISCARDED);
            }
        }

    }

    private void launch(Entity e, boolean huge) {
        if (e.onGround()) {
            double d0 = e.getX() - this.getX();
            double d1 = e.getZ() - this.getZ();
            double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
            float f = huge ? 2.0F : 0.5F;
            e.push(d0 / d2 * (double)f, huge ? (double)0.5F : (double)0.2F, d1 / d2 * (double)f);
        }

    }

    private void eatItemEffect(ItemStack heldItemMainhand) {
        this.gameEvent(GameEvent.EAT);
        this.playSound(SoundEvents.STRIDER_EAT, this.getVoicePitch(), this.getSoundVolume());

        for(int i = 0; i < 8 + this.random.nextInt(3); ++i) {
            double d2 = this.random.nextGaussian() * 0.02;
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            float radius = this.getBbWidth() * 0.65F;
            float angle = ((float)Math.PI / 180F) * this.yBodyRot;
            double extraX = (double)(radius * Mth.sin((float)Math.PI + angle));
            double extraZ = (double)(radius * Mth.cos(angle));
            ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, heldItemMainhand);
            if (heldItemMainhand.getItem() instanceof BlockItem) {
                data = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem)heldItemMainhand.getItem()).getBlock().defaultBlockState());
            }

            this.level().addParticle(data, this.getX() + extraX, this.getY() + (double)(this.getBbHeight() * 0.6F), this.getZ() + extraZ, d0, d1, d2);
        }

    }

    private boolean isChargePlayer(Entity controllingPassenger) {
        return true;
    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION && !this.charging) {
            this.setAnimation(this.random.nextBoolean() ? ANIMATION_FLING : ANIMATION_STOMP);
        }

        return true;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean owner = this.isTame() && this.isOwnedBy(player);
        InteractionResult type = super.mobInteract(player, hand);
        if (this.isChested() && player.isShiftKeyDown()) {
            this.openGUI(player);
            return InteractionResult.SUCCESS;
        } else if (this.canTargetItem(stack) && this.getMainHandItem().isEmpty()) {
            ItemStack rippedStack = stack.copy();
            rippedStack.setCount(1);
            stack.shrink(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, rippedStack);
            if (rippedStack.is(AMTagRegistry.ELEPHANT_TAMEABLES)) {
                this.blossomThrowerUUID = player.getUUID();
            }

            return InteractionResult.SUCCESS;
        } else if (owner && stack.is(ItemTags.WOOL_CARPETS)) {
            DyeColor color = getCarpetColor(stack);
            if (color != this.getColor()) {
                if (this.getColor() != null) {
                    this.spawnAtLocation(this.getCarpetItemBeingWorn());
                }

                this.gameEvent(GameEvent.ENTITY_INTERACT);
                this.playSound(SoundEvents.LLAMA_SWAG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                if (!this.level().isClientSide && player instanceof ServerPlayer) {
                    ServerPlayer serverPlayer = (ServerPlayer)player;
                    AMAdvancementTriggerRegistry.ELEPHANT_SWAG.trigger(serverPlayer);
                }

                stack.shrink(1);
                this.setColor(color);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        } else if (owner && this.getColor() != null && stack.is(Items.SHEARS)) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (this.getColor() != null) {
                this.spawnAtLocation(this.getCarpetItemBeingWorn());
            }

            this.setColor((DyeColor)null);
            return InteractionResult.SUCCESS;
        } else if (owner && !this.isChested() && stack.is(Items.CHESTS_WOODEN)) {
            this.setChested(true);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (owner && this.isChested() && stack.is(Items.SHEARS)) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(Blocks.CHEST);

            for(int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
                this.spawnAtLocation(this.elephantInventory.getItem(i));
            }

            this.elephantInventory.clearContent();
            this.setChested(false);
            return InteractionResult.SUCCESS;
        } else if (owner && !this.isBaby() && type != InteractionResult.CONSUME) {
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }

            return InteractionResult.SUCCESS;
        } else {
            return type;
        }
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return this.isTusked() && !this.isBaby() ? TUSKED_SIZE : super.getDimensions(poseIn);
    }

    public Animation getAnimation() {
        return this.currentAnimation;
    }

    public void setAnimation(Animation animation) {
        this.currentAnimation = animation;
    }

    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_TRUMPET_0, ANIMATION_TRUMPET_1, ANIMATION_CHARGE_PREPARE, ANIMATION_STOMP, ANIMATION_FLING, ANIMATION_EAT, ANIMATION_BREAKLEAVES};
    }

    public int getAnimationTick() {
        return this.animationTick;
    }

    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    public Item getCarpetItemBeingWorn() {
        return this.getColor() != null ? (Item)DYE_COLOR_ITEM_MAP.get(this.getColor()) : net.minecraft.world.item.Items.AIR;
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isChested()) {
            if (!this.level().isClientSide) {
                this.spawnAtLocation(Blocks.CHEST);
            }

            for(int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
                this.spawnAtLocation(this.elephantInventory.getItem(i));
            }

            this.elephantInventory.clearContent();
            this.setChested(false);
        }

        if (!this.isTrader() && this.getColor() != null) {
            if (!this.level().isClientSide) {
                this.spawnAtLocation(this.getCarpetItemBeingWorn());
            }

            this.setColor((DyeColor)null);
        }

    }

    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        EntityElephant baby = (EntityElephant)((EntityType)AMEntityRegistry.ELEPHANT.get()).create(serverWorld);
        baby.setTusked(this.getNearestTusked(this.level(), (double)15.0F) == null || this.random.nextInt(2) == 0);
        return baby;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Tusked", this.isTusked());
        compound.putBoolean("ElephantSitting", this.isSitting());
        compound.putBoolean("Standing", this.isStanding());
        compound.putBoolean("Chested", this.isChested());
        compound.putBoolean("Trader", this.isTrader());
        compound.putBoolean("ForcedToSit", this.forcedSit);
        compound.putBoolean("Tamed", this.isTame());
        compound.putInt("ChargeCooldown", this.chargeCooldown);
        compound.putInt("Carpet", (Integer)this.entityData.get(CARPET_COLOR));
        compound.putInt("DespawnDelay", this.despawnDelay);
        if (this.elephantInventory != null) {
            ListTag nbttaglist = new ListTag();

            for(int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.elephantInventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    CompoundTag CompoundNBT = new CompoundTag();
                    CompoundNBT.putByte("Slot", (byte)i);
                    itemstack.save(CompoundNBT);
                    nbttaglist.add(CompoundNBT);
                }
            }

            compound.put("Items", nbttaglist);
        }

    }

    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return potioneffectIn.getEffect() == MobEffects.WITHER ? false : super.canBeAffected(potioneffectIn);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setTame(compound.getBoolean("Tamed"));
        this.setTusked(compound.getBoolean("Tusked"));
        this.setStanding(compound.getBoolean("Standing"));
        this.setOrderedToSit(compound.getBoolean("ElephantSitting"));
        this.setChested(compound.getBoolean("Chested"));
        this.setTrader(compound.getBoolean("Trader"));
        this.forcedSit = compound.getBoolean("ForcedToSit");
        this.chargeCooldown = compound.getInt("ChargeCooldown");
        this.entityData.set(CARPET_COLOR, compound.getInt("Carpet"));
        if (this.elephantInventory != null) {
            ListTag nbttaglist = compound.getList("Items", 10);
            this.initElephantInventory();

            for(int i = 0; i < nbttaglist.size(); ++i) {
                CompoundTag CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.elephantInventory.setItem(j, ItemStack.of(CompoundNBT));
            }
        } else {
            ListTag nbttaglist = compound.getList("Items", 10);
            this.initElephantInventory();

            for(int i = 0; i < nbttaglist.size(); ++i) {
                CompoundTag CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.initElephantInventory();
                this.elephantInventory.setItem(j, ItemStack.of(CompoundNBT));
            }
        }

        if (compound.contains("DespawnDelay", 99)) {
            this.despawnDelay = compound.getInt("DespawnDelay");
        }

    }

    public boolean isChested() {
        return Boolean.valueOf((Boolean)this.entityData.get(CHESTED));
    }

    public void setChested(boolean chested) {
        this.entityData.set(CHESTED, chested);
        this.hasChestVarChanged = true;
    }

    public boolean setSlot(int inventorySlot, @Nullable ItemStack itemStackIn) {
        int j = inventorySlot - 500 + 2;
        if (j >= 0 && j < this.elephantInventory.getContainerSize()) {
            this.elephantInventory.setItem(j, itemStackIn);
            return true;
        } else {
            return false;
        }
    }

    public void die(DamageSource cause) {
        super.die(cause);
        if (this.elephantInventory != null && !this.level().isClientSide) {
            for(int i = 0; i < this.elephantInventory.getContainerSize(); ++i) {
                ItemStack itemstack = this.elephantInventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    this.spawnAtLocation(itemstack, 0.0F);
                }
            }
        }

    }

    public boolean isStanding() {
        return (Boolean)this.entityData.get(STANDING);
    }

    public void setStanding(boolean standing) {
        this.entityData.set(STANDING, standing);
    }

    public boolean isSitting() {
        return (Boolean)this.entityData.get(SITTING);
    }

    public void setOrderedToSit(boolean sit) {
        this.entityData.set(SITTING, sit);
    }

    @Nullable
    public DyeColor getColor() {
        int lvt_1_1_ = (Integer)this.entityData.get(CARPET_COLOR);
        return lvt_1_1_ == -1 ? null : DyeColor.byId(lvt_1_1_);
    }

    public void setColor(@Nullable DyeColor color) {
        this.entityData.set(CARPET_COLOR, color == null ? -1 : color.getId());
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (spawnDataIn instanceof AgeableMob.AgeableMobGroupData lvt_6_1_) {
            if (lvt_6_1_.getGroupSize() == 0) {
                this.setTusked(true);
            }
        } else {
            this.setTusked(this.getRandom().nextBoolean());
        }

        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Nullable
    public EntityElephant getNearestTusked(LevelAccessor world, double dist) {
        List<? extends EntityElephant> list = world.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(dist, dist / (double)2.0F, dist));
        if (list.isEmpty()) {
            return null;
        } else {
            EntityElephant elephant1 = null;
            double d0 = Double.MAX_VALUE;

            for(EntityElephant elephant : list) {
                if (elephant.isTusked()) {
                    double d1 = this.distanceToSqr(elephant);
                    if (!(d1 > d0)) {
                        d0 = d1;
                        elephant1 = elephant;
                    }
                }
            }

            return elephant1;
        }
    }

    public boolean isTusked() {
        return (Boolean)this.entityData.get(TUSKED);
    }

    public void setTusked(boolean tusked) {
        boolean prev = this.isTusked();
        if (!prev && tusked) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)110.0F);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)15.0F);
            this.setHealth(150.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)85.0F);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)10.0F);
        }

        this.entityData.set(TUSKED, tusked);
    }

    public boolean canTargetItem(ItemStack stack) {
        return stack.is(AMTagRegistry.ELEPHANT_FOODSTUFFS);
    }

    public void onGetItem(ItemEntity e) {
        ItemStack duplicate = e.getItem().copy();
        duplicate.setCount(1);
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide) {
            this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
        }

        Entity itemThrower = e.getOwner();
        if (duplicate.is(AMTagRegistry.ELEPHANT_TAMEABLES) && itemThrower != null) {
            this.blossomThrowerUUID = itemThrower.getUUID();
        } else {
            this.blossomThrowerUUID = null;
        }

        this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
        this.aiItemFlag = false;
    }

    public void onFindTarget(ItemEntity e) {
        this.aiItemFlag = true;
    }

    public void addElephantLoot(@Nullable Player player, int seed) {
        if (this.level().getServer() != null) {
            LootTable loottable = this.level().getServer().getLootData().getLootTable(TRADER_LOOT);
            LootParams.Builder lootcontext$builder = new LootParams.Builder((ServerLevel)this.level());
            loottable.fill(this.elephantInventory, lootcontext$builder.create(LootContextParamSets.EMPTY), (long)seed);
        }

    }

    public void leaveCaravan() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }

        this.caravanHead = null;
    }

    public void joinCaravan(EntityElephant caravanHeadIn) {
        this.caravanHead = caravanHeadIn;
        this.caravanHead.caravanTail = this;
    }

    public boolean hasCaravanTrail() {
        return this.caravanTail != null;
    }

    public boolean inCaravan() {
        return this.caravanHead != null;
    }

    @Nullable
    public EntityElephant getCaravanHead() {
        return this.caravanHead;
    }

    public double getMaxDistToItem() {
        return Math.pow((double)(this.getBbWidth() + 3.0F), (double)2.0F);
    }

    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (this.hasPassenger(passenger)) {
            float standAdd = -0.3F * this.standProgress;
            float scale = this.isBaby() ? 0.5F : (this.isTusked() ? 1.1F : 1.0F);
            float sitAdd = -0.065F * this.sitProgress;
            float scaleY = scale * (2.4F * sitAdd - 0.4F * standAdd);
            if (passenger instanceof AbstractVillager) {
                AbstractVillager villager = (AbstractVillager)passenger;
                scaleY -= 0.3F;
            }

            float radius = scale * (0.5F + standAdd);
            float angle = ((float)Math.PI / 180F) * this.yBodyRot;
            if (this.getAnimation() == ANIMATION_CHARGE_PREPARE) {
                float sinWave = Mth.sin((float)(Math.PI * (double)((float)this.getAnimationTick() / 25.0F)));
                radius += sinWave * 0.2F * scale;
            }

            if (this.getAnimation() == ANIMATION_STOMP) {
                float sinWave = Mth.sin((float)(Math.PI * (double)((float)this.getAnimationTick() / 20.0F)));
                radius -= sinWave * 1.0F * scale;
                scaleY += sinWave * 0.7F * scale;
            }

            double extraX = (double)(radius * Mth.sin((float)Math.PI + angle));
            double extraZ = (double)(radius * Mth.cos(angle));
            passenger.setPos(this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + (double)scaleY + passenger.getMyRidingOffset(), this.getZ() + extraZ);
        }

    }

    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        if (player.zza != 0.0F) {
            float f = player.zza < 0.0F ? 0.5F : 1.0F;
            return new Vec3((double)(player.xxa * 0.25F), (double)0.0F, (double)(player.zza * 0.5F * f));
        } else {
            this.setSprinting(false);
            return Vec3.ZERO;
        }
    }

    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if (player.zza != 0.0F || player.xxa != 0.0F) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
            this.setMaxUpStep(1.0F);
            this.getNavigation().stop();
            this.setTarget((LivingEntity)null);
            this.setSprinting(true);
        }

    }

    protected float getRiddenSpeed(Player rider) {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    public double getPassengersRidingOffset() {
        float scale = this.isBaby() ? 0.5F : (this.isTusked() ? 1.1F : 1.0F);
        float f = Math.min(0.25F, this.walkAnimation.speed());
        float f1 = this.walkAnimation.position();
        float sitAdd = 0.0F;
        float standAdd = 0.0F;
        return (double)this.getBbHeight() - (double)0.05F - (double)scale * ((double)(0.1F * Mth.cos(f1 * 1.4F) * 1.4F * f) + (double)sitAdd + (double)standAdd);
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }

            if (entityIn instanceof TamableAnimal) {
                return ((TamableAnimal)entityIn).isOwnedBy(livingentity);
            }

            if (livingentity != null) {
                return livingentity.isAlliedTo(entityIn);
            }
        }

        return super.isAlliedTo(entityIn);
    }

    public void travel(Vec3 vec3d) {
        if (this.isSitting()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }

            vec3d = Vec3.ZERO;
        }

        super.travel(vec3d);
    }

    public void openGUI(Player playerEntity) {
        if (!this.level().isClientSide && !this.hasPassenger(playerEntity)) {
            NetworkHooks.openScreen((ServerPlayer)playerEntity, new MenuProvider(this) {
                public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
                    return ChestMenu.sixRows(p_createMenu_1_, p_createMenu_2_, EntityElephant.this.elephantInventory);
                }

                public Component getDisplayName() {
                    return Component.translatable("entity.alexsmobs.elephant.chest");
                }
            });
        }

    }

    public boolean isTrader() {
        return (Boolean)this.entityData.get(TRADER);
    }

    public void setTrader(boolean trader) {
        this.entityData.set(TRADER, trader);
    }

    public boolean triggerCharge(ItemStack stack) {
        if (this.getControllingPassenger() != null && this.chargeCooldown == 0 && !this.charging && this.getAnimation() == NO_ANIMATION && this.isTusked()) {
            this.setAnimation(ANIMATION_CHARGE_PREPARE);
            this.eatItemEffect(stack);
            this.heal(2.0F);
            return true;
        } else {
            return false;
        }
    }

    public boolean canSpawnWithTraderHere() {
        return this.level().isLoaded(this.blockPosition()) && this.checkSpawnObstruction(this.level()) && this.level().isEmptyBlock(this.blockPosition().above(4));
    }

    static {
        TUSKED = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
        SITTING = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
        STANDING = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
        CHESTED = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
        CARPET_COLOR = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.INT);
        TRADER = SynchedEntityData.defineId(EntityElephant.class, EntityDataSerializers.BOOLEAN);
        DYE_COLOR_ITEM_MAP = (Map)Util.make(Maps.newHashMap(), (map) -> {
            map.put(DyeColor.WHITE, net.minecraft.world.item.Items.WHITE_CARPET);
            map.put(DyeColor.ORANGE, net.minecraft.world.item.Items.ORANGE_CARPET);
            map.put(DyeColor.MAGENTA, net.minecraft.world.item.Items.MAGENTA_CARPET);
            map.put(DyeColor.LIGHT_BLUE, net.minecraft.world.item.Items.LIGHT_BLUE_CARPET);
            map.put(DyeColor.YELLOW, net.minecraft.world.item.Items.YELLOW_CARPET);
            map.put(DyeColor.LIME, net.minecraft.world.item.Items.LIME_CARPET);
            map.put(DyeColor.PINK, net.minecraft.world.item.Items.PINK_CARPET);
            map.put(DyeColor.GRAY, net.minecraft.world.item.Items.GRAY_CARPET);
            map.put(DyeColor.LIGHT_GRAY, net.minecraft.world.item.Items.LIGHT_GRAY_CARPET);
            map.put(DyeColor.CYAN, net.minecraft.world.item.Items.CYAN_CARPET);
            map.put(DyeColor.PURPLE, net.minecraft.world.item.Items.PURPLE_CARPET);
            map.put(DyeColor.BLUE, net.minecraft.world.item.Items.BLUE_CARPET);
            map.put(DyeColor.BROWN, net.minecraft.world.item.Items.BROWN_CARPET);
            map.put(DyeColor.GREEN, net.minecraft.world.item.Items.GREEN_CARPET);
            map.put(DyeColor.RED, net.minecraft.world.item.Items.RED_CARPET);
            map.put(DyeColor.BLACK, net.minecraft.world.item.Items.BLACK_CARPET);
        });
        TRADER_LOOT = new ResourceLocation("alexsmobs", "gameplay/trader_elephant_chest");
    }

    private class AIWalkIdle extends RandomStrollGoal {
        public AIWalkIdle(EntityElephant e, double v) {
            super(e, v);
        }

        public boolean canUse() {
            this.interval = !EntityElephant.this.isTusked() && EntityElephant.this.inCaravan() ? 120 : 50;
            return super.canUse();
        }

        @Nullable
        protected Vec3 getPosition() {
            return LandRandomPos.getPos(this.mob, !EntityElephant.this.isTusked() && EntityElephant.this.inCaravan() ? 10 : 25, 7);
        }
    }

    class HurtByTargetGoal extends net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal {
        public HurtByTargetGoal(EntityElephant this$0) {
            super(this$0, new Class[0]);
        }

        public void start() {
            if (!EntityElephant.this.isBaby() && EntityElephant.this.isTusked()) {
                super.start();
            } else {
                this.alertOthers();
                this.stop();
            }

        }

        protected void alertOther(Mob mobIn, LivingEntity targetIn) {
            if (mobIn instanceof EntityElephant && (!mobIn.isBaby() || !((EntityElephant)mobIn).isTusked())) {
                super.alertOther(mobIn, targetIn);
            }

        }
    }

    class PanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {
        public PanicGoal(EntityElephant this$0) {
            super(this$0, (double)1.0F);
        }

        public boolean canUse() {
            return (EntityElephant.this.isBaby() || !EntityElephant.this.isTusked() || EntityElephant.this.isOnFire()) && super.canUse();
        }
    }
}

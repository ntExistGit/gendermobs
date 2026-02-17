//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.MooseAIJostle;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public class EntityMoose extends Animal implements IAnimatedEntity {
    public static final Animation ANIMATION_EAT_GRASS = Animation.create(30);
    public static final Animation ANIMATION_ATTACK = Animation.create(15);
    private static final int DAY = 24000;
    private static final EntityDataAccessor<Boolean> ANTLERED;
    private static final EntityDataAccessor<Boolean> JOSTLING;
    private static final EntityDataAccessor<Float> JOSTLE_ANGLE;
    private static final EntityDataAccessor<Optional<UUID>> JOSTLER_UUID;
    private static final EntityDataAccessor<Boolean> SNOWY;
    public float prevJostleAngle;
    public float prevJostleProgress;
    public float jostleProgress;
    public boolean jostleDirection;
    public int jostleTimer = 0;
    public boolean instantlyTriggerJostleAI = false;
    public int jostleCooldown;
    public int timeUntilAntlerDrop;
    private int animationTick;
    private Animation currentAnimation;
    private int snowTimer;
    private boolean permSnow;

    protected EntityMoose(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.jostleCooldown = 100 + this.random.nextInt(40);
        this.timeUntilAntlerDrop = 168000 + this.random.nextInt(3) * 24000;
        this.snowTimer = 0;
        this.permSnow = false;
        this.setMaxUpStep(1.1F);
    }

    public static boolean canMooseSpawn(EntityType<? extends Mob> typeIn, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        BlockState blockstate = worldIn.getBlockState(pos.below());
        return blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(Blocks.SNOW) || blockstate.is(Blocks.SNOW_BLOCK) && worldIn.getRawBrightness(pos, 0) > 8;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, (double)55.0F).add(Attributes.ATTACK_DAMAGE, (double)7.5F).add(Attributes.MOVEMENT_SPEED, (double)0.25F).add(Attributes.KNOCKBACK_RESISTANCE, (double)0.5F);
    }

    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
        return AMEntityRegistry.rollSpawn(AMConfig.mooseSpawnRolls, this.getRandom(), spawnReasonIn);
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MooseAIJostle(this));
        this.goalSelector.addGoal(3, new AnimalAIPanicBaby(this, (double)1.25F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1, true));
        this.goalSelector.addGoal(5, new BreedGoal(this, (double)1.0F));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.1, Ingredient.of(AMTagRegistry.MOOSE_BREEDABLES), false));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(this, 120, (double)1.0F, 14, 7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new AnimalAIHurtByTargetNotBaby(this, new Class[0]));
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 6) {
            for(int lvt_3_1_ = 0; lvt_3_1_ < 7; ++lvt_3_1_) {
                double lvt_4_1_ = this.random.nextGaussian() * 0.02;
                double lvt_6_1_ = this.random.nextGaussian() * 0.02;
                double lvt_8_1_ = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.SMOKE, this.getRandomX((double)1.0F), this.getRandomY() + (double)0.5F, this.getRandomZ((double)1.0F), lvt_4_1_, lvt_6_1_, lvt_8_1_);
            }
        } else {
            super.handleEntityEvent(id);
        }

    }

    public boolean isFood(ItemStack stack) {
        if (stack.is(AMTagRegistry.MOOSE_BREEDABLES) && !this.isInLove() && this.getAge() == 0) {
            if (this.getRandom().nextInt(5) == 0) {
                return true;
            } else {
                this.level().broadcastEntityEvent(this, (byte)6);
                return false;
            }
        } else {
            return false;
        }
    }

    public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
        if (!this.isBaby()) {
            super.setTarget(entitylivingbaseIn);
        }

    }

    public boolean doHurtTarget(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_ATTACK);
        }

        return true;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANTLERED, true);
        this.entityData.define(JOSTLING, false);
        this.entityData.define(SNOWY, false);
        this.entityData.define(JOSTLE_ANGLE, 0.0F);
        this.entityData.define(JOSTLER_UUID, Optional.empty());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSnowy(compound.getBoolean("Snowy"));
        if (compound.contains("AntlerTime")) {
            this.timeUntilAntlerDrop = compound.getInt("AntlerTime");
        }

        this.setAntlered(compound.getBoolean("Antlered"));
        this.jostleCooldown = compound.getInt("JostlingCooldown");
        this.permSnow = compound.getBoolean("SnowPerm");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Snowy", this.isSnowy());
        compound.putBoolean("SnowPerm", this.permSnow);
        compound.putInt("AntlerTime", this.timeUntilAntlerDrop);
        compound.putBoolean("Antlered", this.isAntlered());
        compound.putInt("JostlingCooldown", this.jostleCooldown);
    }

    public void tick() {
        super.tick();
        this.prevJostleProgress = this.jostleProgress;
        this.prevJostleAngle = this.getJostleAngle();
        if (this.isJostling()) {
            if (this.jostleProgress < 5.0F) {
                ++this.jostleProgress;
            }
        } else if (this.jostleProgress > 0.0F) {
            --this.jostleProgress;
        }

        if (this.jostleCooldown > 0) {
            --this.jostleCooldown;
        }

        if (!this.level().isClientSide && this.getAnimation() == NO_ANIMATION && this.getRandom().nextInt(120) == 0 && (this.getTarget() == null || !this.getTarget().isAlive()) && !this.isJostling() && this.getJostlingPartnerUUID() == null && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK) && this.getRandom().nextInt(3) == 0) {
            this.setAnimation(ANIMATION_EAT_GRASS);
        }

        if (this.timeUntilAntlerDrop > 0) {
            --this.timeUntilAntlerDrop;
        }

        if (this.timeUntilAntlerDrop == 0) {
            if (this.isAntlered()) {
                this.setAntlered(false);
                this.spawnAtLocation(new ItemStack((ItemLike)AMItemRegistry.MOOSE_ANTLER.get()));
                this.timeUntilAntlerDrop = '뮀' + this.random.nextInt(3) * 24000;
            } else {
                this.setAntlered(true);
                this.timeUntilAntlerDrop = 168000 + this.random.nextInt(3) * 24000;
            }
        }

        if (this.getTarget() != null && this.getTarget().isAlive()) {
            if (this.isJostling()) {
                this.setJostling(false);
            }

            if (!this.level().isClientSide && this.getAnimation() == ANIMATION_ATTACK && this.getAnimationTick() == 8) {
                float dmg = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                if (!this.isAntlered()) {
                    dmg = 3.0F;
                }

                if (this.getTarget() instanceof Wolf || this.getTarget() instanceof EntityOrca) {
                    dmg = 2.0F;
                }

                this.getTarget().knockback((double)1.0F, this.getTarget().getX() - this.getX(), this.getTarget().getZ() - this.getZ());
                this.getTarget().hurt(this.damageSources().mobAttack(this), dmg);
            }
        }

        if (this.snowTimer > 0) {
            --this.snowTimer;
        }

        if (this.snowTimer == 0 && !this.level().isClientSide) {
            this.snowTimer = 200 + this.random.nextInt(400);
            if (this.isSnowy()) {
                if (!this.permSnow && (!this.level().isClientSide || this.getRemainingFireTicks() > 0 || this.isInWaterOrBubble() || !EntityGrizzlyBear.isSnowingAt(this.level(), this.blockPosition().above()))) {
                    this.setSnowy(false);
                }
            } else if (!this.level().isClientSide && EntityGrizzlyBear.isSnowingAt(this.level(), this.blockPosition())) {
                this.setSnowy(true);
            }
        }

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            if (entity instanceof EntityOrca || entity instanceof Wolf) {
                amount = (amount + 1.0F) * 3.0F;
            }

            return super.hurt(source, amount);
        }
    }

    protected SoundEvent getAmbientSound() {
        return (SoundEvent)AMSoundRegistry.MOOSE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return (SoundEvent)AMSoundRegistry.MOOSE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return (SoundEvent)AMSoundRegistry.MOOSE_HURT.get();
    }

    public boolean isAntlered() {
        return (Boolean)this.entityData.get(ANTLERED);
    }

    public void setAntlered(boolean anters) {
        this.entityData.set(ANTLERED, anters);
    }

    public boolean isJostling() {
        return (Boolean)this.entityData.get(JOSTLING);
    }

    public void setJostling(boolean jostle) {
        this.entityData.set(JOSTLING, jostle);
    }

    public float getJostleAngle() {
        return (Float)this.entityData.get(JOSTLE_ANGLE);
    }

    public void setJostleAngle(float scale) {
        this.entityData.set(JOSTLE_ANGLE, scale);
    }

    @Nullable
    public UUID getJostlingPartnerUUID() {
        return (UUID)((Optional)this.entityData.get(JOSTLER_UUID)).orElse((Object)null);
    }

    public void setJostlingPartnerUUID(@Nullable UUID uniqueId) {
        this.entityData.set(JOSTLER_UUID, Optional.ofNullable(uniqueId));
    }

    public boolean isSnowy() {
        return (Boolean)this.entityData.get(SNOWY);
    }

    public void setSnowy(boolean honeyed) {
        this.entityData.set(SNOWY, honeyed);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        InteractionResult type = super.mobInteract(player, hand);
        if (item == Items.SNOW && !this.isSnowy() && !this.level().isClientSide) {
            this.usePlayerItem(player, hand, itemstack);
            this.permSnow = true;
            this.setSnowy(true);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SNOW_PLACE, this.getSoundVolume(), this.getVoicePitch());
            return InteractionResult.SUCCESS;
        } else if (item instanceof ShovelItem && this.isSnowy() && !this.level().isClientSide) {
            this.permSnow = false;
            if (!player.isCreative()) {
                itemstack.hurt(1, this.getRandom(), player instanceof ServerPlayer ? (ServerPlayer)player : null);
            }

            this.setSnowy(false);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.SNOW_BREAK, this.getSoundVolume(), this.getVoicePitch());
            return InteractionResult.SUCCESS;
        } else {
            return type;
        }
    }

    @Nullable
    public Entity getJostlingPartner() {
        UUID id = this.getJostlingPartnerUUID();
        return id != null && !this.level().isClientSide ? ((ServerLevel)this.level()).getEntity(id) : null;
    }

    public void setJostlingPartner(@Nullable Entity jostlingPartner) {
        if (jostlingPartner == null) {
            this.setJostlingPartnerUUID((UUID)null);
        } else {
            this.setJostlingPartnerUUID(jostlingPartner.getUUID());
        }

    }

    public void pushBackJostling(EntityMoose entityMoose, float strength) {
        this.applyKnockbackFromMoose(strength, entityMoose.getX() - this.getX(), entityMoose.getZ() - this.getZ());
    }

    private void applyKnockbackFromMoose(float strength, double ratioX, double ratioZ) {
        LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(this, strength, ratioX, ratioZ);
        if (!event.isCanceled()) {
            strength = event.getStrength();
            ratioX = event.getRatioX();
            ratioZ = event.getRatioZ();
            if (!(strength <= 0.0F)) {
                this.hasImpulse = true;
                Vec3 vector3d = this.getDeltaMovement();
                Vec3 vector3d1 = (new Vec3(ratioX, (double)0.0F, ratioZ)).normalize().scale((double)strength);
                this.setDeltaMovement(vector3d.x / (double)2.0F - vector3d1.x, (double)0.3F, vector3d.z / (double)2.0F - vector3d1.z);
            }

        }
    }

    public int getAnimationTick() {
        return this.animationTick;
    }

    public void setAnimationTick(int i) {
        this.animationTick = i;
    }

    public Animation getAnimation() {
        return this.currentAnimation;
    }

    public void setAnimation(Animation animation) {
        this.currentAnimation = animation;
    }

    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_ATTACK, ANIMATION_EAT_GRASS};
    }

    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        return (AgeableMob)((EntityType)AMEntityRegistry.MOOSE.get()).create(serverWorld);
    }

    public boolean canJostleWith(EntityMoose moose) {
        return !moose.isJostling() && moose.isAntlered() && moose.getAnimation() == NO_ANIMATION && !moose.isBaby() && moose.getJostlingPartnerUUID() == null && moose.jostleCooldown == 0;
    }

    public void playJostleSound() {
        this.playSound((SoundEvent)AMSoundRegistry.MOOSE_JOSTLE.get(), this.getVoicePitch(), this.getSoundVolume());
    }

    static {
        ANTLERED = SynchedEntityData.defineId(EntityMoose.class, EntityDataSerializers.BOOLEAN);
        JOSTLING = SynchedEntityData.defineId(EntityMoose.class, EntityDataSerializers.BOOLEAN);
        JOSTLE_ANGLE = SynchedEntityData.defineId(EntityMoose.class, EntityDataSerializers.FLOAT);
        JOSTLER_UUID = SynchedEntityData.defineId(EntityMoose.class, EntityDataSerializers.OPTIONAL_UUID);
        SNOWY = SynchedEntityData.defineId(EntityMoose.class, EntityDataSerializers.BOOLEAN);
    }
}

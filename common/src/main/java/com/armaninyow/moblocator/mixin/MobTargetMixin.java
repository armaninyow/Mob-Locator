package com.armaninyow.moblocator.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobTargetMixin extends net.minecraft.world.entity.LivingEntity
		implements com.armaninyow.moblocator.MobTargetAccessor {

	@SuppressWarnings("unchecked")
	protected MobTargetMixin(net.minecraft.world.entity.EntityType type, net.minecraft.world.level.Level level) {
		super(type, level);
	}

	@Unique
	private static final EntityDataAccessor<Boolean> MOBLOCATOR_HAS_TARGET =
		SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BOOLEAN);

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	private void moblocator$defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
		builder.define(MOBLOCATOR_HAS_TARGET, false);
	}

	@Inject(method = "setTarget", at = @At("TAIL"))
	private void moblocator$setTarget(@Nullable LivingEntity target, CallbackInfo ci) {
		this.entityData.set(MOBLOCATOR_HAS_TARGET, target != null);
	}

	@Unique
	public boolean moblocator$hasTarget() {
		return this.entityData.get(MOBLOCATOR_HAS_TARGET);
	}
}
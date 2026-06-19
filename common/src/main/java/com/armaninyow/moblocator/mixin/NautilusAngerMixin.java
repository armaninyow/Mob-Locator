package com.armaninyow.moblocator.mixin;

import com.armaninyow.moblocator.MobTargetAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractNautilus.class)
public abstract class NautilusAngerMixin extends net.minecraft.world.entity.TamableAnimal
		implements com.armaninyow.moblocator.MobTargetAccessor {

	@SuppressWarnings("unchecked")
	protected NautilusAngerMixin(net.minecraft.world.entity.EntityType type, net.minecraft.world.level.Level level) {
		super(type, level);
	}

	@Unique
	private static final EntityDataAccessor<Boolean> MOBLOCATOR_NAUTILUS_ANGRY =
		SynchedEntityData.defineId(AbstractNautilus.class, EntityDataSerializers.BOOLEAN);

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	private void moblocator$defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
		builder.define(MOBLOCATOR_NAUTILUS_ANGRY, false);
	}

	// Set flag when hurt (which triggers NautilusAi.setAngerTarget)
	@Inject(method = "hurtServer", at = @At("TAIL"))
	private void moblocator$hurtServer(ServerLevel level, DamageSource source, float damage,
	                                    CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue() && source.getEntity() instanceof LivingEntity) {
			this.entityData.set(MOBLOCATOR_NAUTILUS_ANGRY, true);
		}
	}

	// Clear flag each server tick when ANGRY_AT and ATTACK_TARGET memories are gone
	@Inject(method = "customServerAiStep", at = @At("TAIL"))
	private void moblocator$customServerAiStep(ServerLevel level, CallbackInfo ci) {
		boolean angry = this.getBrain().hasMemoryValue(MemoryModuleType.ANGRY_AT)
			|| this.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
		this.entityData.set(MOBLOCATOR_NAUTILUS_ANGRY, angry);
	}

	@Unique
	public boolean moblocator$isNautilusAngry() {
		return this.entityData.get(MOBLOCATOR_NAUTILUS_ANGRY);
	}

	// Satisfied at runtime by MobTargetMixin which is applied to Mob (parent of AbstractNautilus)
	@Override
	public boolean moblocator$hasTarget() { return false; }
}
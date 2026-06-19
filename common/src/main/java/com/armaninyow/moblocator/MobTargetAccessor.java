package com.armaninyow.moblocator;

public interface MobTargetAccessor {
	boolean moblocator$hasTarget();
	default boolean moblocator$isNautilusAngry() { return false; }
}
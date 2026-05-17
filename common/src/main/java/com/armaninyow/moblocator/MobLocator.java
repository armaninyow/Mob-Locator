package com.armaninyow.moblocator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MobLocator implements ClientModInitializer {
	public static final String MOD_ID = "moblocator";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("MobLocator - Pixel-perfect icons with color configuration!");
		MobLocatorConfig.load();

		// Register after BOSS_BAR so icons render on top of the entire main HUD
		HudElementRegistry.attachElementAfter(
			VanillaHudElements.BOSS_BAR,
			Identifier.fromNamespaceAndPath(MOD_ID, "mob_indicators"),
			this::renderMobLocator
		);
	}

	public void renderMobLocator(GuiGraphicsExtractor context, DeltaTracker deltaTracker) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.level == null) return;

		Player player = client.player;
		int screenWidth = context.guiWidth();
		int screenHeight = context.guiHeight();

		int xpBarWidth = 182;
		int centerX = screenWidth / 2;
		int xpBarLeft = centerX - xpBarWidth / 2;
		int baseY = screenHeight - 27;

		List<MobInfo> detectedMobs = new ArrayList<>();

		for (LivingEntity entity : client.level.getEntitiesOfClass(LivingEntity.class,
				player.getBoundingBox().inflate(64), e -> e != player && e.isAlive())) {

			if (isBlacklisted(entity)) continue;
			if (entity.isInvisible() && !MobLocatorConfig.showInvisibleMobs) continue;

			double distance = player.distanceTo(entity);
			float hearingRange = getHearingRange(entity);

			if (distance <= hearingRange) {
				boolean isHostile = isHostileMob(entity) || isAngryNeutralMob(entity);
				int iconType = getIconType(distance, hearingRange);

				int color;
				if (entity.isInvisible()) {
					color = MobLocatorConfig.invisibleMobColor;
				} else if (isHostile) {
					color = MobLocatorConfig.hostileMobColor;
				} else {
					color = MobLocatorConfig.passiveMobColor;
				}

				double dy = entity.getY() - player.getY();
				boolean isAbove = dy > 3;
				boolean isBelow = dy < -3;

				detectedMobs.add(new MobInfo(entity, player, iconType, color, isAbove, isBelow));
			}
		}

		for (MobInfo mobInfo : detectedMobs) {
			renderMobIndicator(context, mobInfo, xpBarLeft, xpBarWidth, baseY);
		}
	}

	private boolean isBlacklisted(LivingEntity entity) {
		String entityName = entity.getType().getDescription().getString();
		for (String blacklisted : MobLocatorConfig.blacklistedMobs) {
			if (entityName.equalsIgnoreCase(blacklisted.trim())) return true;
		}
		return false;
	}

	private void renderMobIndicator(GuiGraphicsExtractor context, MobInfo mobInfo, int xpBarLeft, int xpBarWidth, int baseY) {
		double clampedAngle = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, mobInfo.angle));
		double normalizedPos = 0.5 - (clampedAngle / Math.PI);

		int x = xpBarLeft + (int)(normalizedPos * xpBarWidth);
		int y = baseY;

		if (mobInfo.isAbove) {
			IconRenderer.drawArrow(context, x, y - 6, true, mobInfo.color);
		} else if (mobInfo.isBelow) {
			IconRenderer.drawArrow(context, x, y + 6, false, mobInfo.color);
		}

		IconRenderer.drawIcon(context, x, y, mobInfo.iconType, mobInfo.color);
	}

	private float getHearingRange(LivingEntity entity) {
		String entityName = entity.getClass().getSimpleName();
		if (entity instanceof EnderDragon) return 192.0f;
		if (entity instanceof Phantom) return 36.0f;
		if (entity instanceof Warden) return 48.0f;
		if (entity instanceof Ghast) return 64.0f;
		if (entity instanceof Blaze) return 48.0f;
		if (entityName.contains("Wither")) return 40.0f;
		if (entity instanceof EnderMan) return 64.0f;
		if (entity instanceof Drowned) return 32.0f;
		if (entityName.equals("Allay")) return 64f;
		if (entityName.equals("Bat")) return 8.0f;
		return 16.0f;
	}

	private int getIconType(double distance, float hearingRange) {
		double ratio = distance / hearingRange;
		if (ratio <= 0.25) return 3;
		if (ratio <= 0.5) return 2;
		if (ratio <= 0.75) return 1;
		return 0;
	}

	private boolean isHostileMob(LivingEntity entity) {
		return entity instanceof Monster ||
			   entity instanceof Slime ||
			   entity instanceof Phantom ||
			   entity instanceof MagmaCube ||
			   entity instanceof Shulker;
	}

	private boolean isAngryNeutralMob(LivingEntity entity) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null) return false;
		if (entity instanceof EnderMan enderman) return enderman.isCreepy();
		if (entity instanceof Piglin piglin) return piglin.getTarget() == client.player;
		if (entity instanceof ZombifiedPiglin zombifiedPiglin) return zombifiedPiglin.getTarget() == client.player;
		if (entity instanceof Wolf wolf) return wolf.getPersistentAngerTarget() != null;
		if (entity instanceof PolarBear bear) return bear.getPersistentAngerTarget() != null;
		if (entity instanceof IronGolem golem) return golem.getPersistentAngerTarget() != null;
		if (entity instanceof Bee bee) return bee.getPersistentAngerTarget() != null;
		return false;
	}

	private static class MobInfo {
		double angle;
		int iconType;
		int color;
		boolean isAbove;
		boolean isBelow;

		MobInfo(LivingEntity mob, Player player, int iconType, int color, boolean isAbove, boolean isBelow) {
			this.iconType = iconType;
			this.color = color;
			this.isAbove = isAbove;
			this.isBelow = isBelow;

			double dx = mob.getX() - player.getX();
			double dz = mob.getZ() - player.getZ();
			double angleToMob = Math.atan2(dx, dz);
			double playerYawRad = Math.toRadians(-player.getYRot());

			this.angle = angleToMob - playerYawRad;
			while (this.angle > Math.PI) this.angle -= 2 * Math.PI;
			while (this.angle < -Math.PI) this.angle += 2 * Math.PI;
		}
	}
}
package com.armaninyow.moblocator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
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
		// (hotbar, XP bar, health, hunger, status effects, boss bar)
		HudElementRegistry.attachElementAfter(
			VanillaHudElements.BOSS_BAR,
			Identifier.of(MOD_ID, "mob_indicators"),
			this::renderMobLocator
		);
	}

	public void renderMobLocator(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.world == null) return;

		PlayerEntity player = client.player;
		int screenWidth = context.getScaledWindowWidth();
		int screenHeight = context.getScaledWindowHeight();

		int xpBarWidth = 182;
		int centerX = screenWidth / 2;
		int xpBarLeft = centerX - xpBarWidth / 2;
		int baseY = screenHeight - 27;

		List<MobInfo> detectedMobs = new ArrayList<>();

		for (LivingEntity entity : client.world.getEntitiesByClass(LivingEntity.class,
				player.getBoundingBox().expand(64), e -> e != player && e.isAlive())) {

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
		String entityName = entity.getType().getName().getString();
		for (String blacklisted : MobLocatorConfig.blacklistedMobs) {
			if (entityName.equalsIgnoreCase(blacklisted.trim())) return true;
		}
		return false;
	}

	private void renderMobIndicator(DrawContext context, MobInfo mobInfo, int xpBarLeft, int xpBarWidth, int baseY) {
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
		if (entity instanceof EnderDragonEntity) return 192.0f;
		if (entity instanceof PhantomEntity) return 36.0f;
		if (entity instanceof WardenEntity) return 48.0f;
		if (entity instanceof GhastEntity) return 64.0f;
		if (entity instanceof BlazeEntity) return 48.0f;
		if (entityName.contains("Wither")) return 40.0f;
		if (entity instanceof EndermanEntity) return 64.0f;
		if (entity instanceof DrownedEntity) return 32.0f;
		if (entityName.equals("AllayEntity")) return 64f;
		if (entityName.equals("BatEntity")) return 8.0f;
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
		return entity instanceof HostileEntity ||
			   entity instanceof SlimeEntity ||
			   entity instanceof PhantomEntity ||
			   entity instanceof MagmaCubeEntity ||
			   entity instanceof ShulkerEntity;
	}

	private boolean isAngryNeutralMob(LivingEntity entity) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null) return false;
		if (entity instanceof EndermanEntity enderman) return enderman.isAngry();
		if (entity instanceof PiglinEntity piglin) return piglin.getTarget() == client.player;
		if (entity instanceof ZombifiedPiglinEntity zombifiedPiglin) return zombifiedPiglin.getTarget() == client.player;
		if (entity instanceof WolfEntity wolf) return wolf.isAttacking();
		if (entity instanceof PolarBearEntity bear) return bear.isAttacking();
		if (entity instanceof SpiderEntity spider) return spider.isAttacking();
		if (entity instanceof CaveSpiderEntity caveSpider) return caveSpider.isAttacking();
		if (entity instanceof IronGolemEntity golem) return golem.isAttacking();
		if (entity instanceof BeeEntity bee) return bee.hasAngerTime();
		return false;
	}

	private static class MobInfo {
		double angle;
		int iconType;
		int color;
		boolean isAbove;
		boolean isBelow;

		MobInfo(LivingEntity mob, PlayerEntity player, int iconType, int color, boolean isAbove, boolean isBelow) {
			this.iconType = iconType;
			this.color = color;
			this.isAbove = isAbove;
			this.isBelow = isBelow;

			double dx = mob.getX() - player.getX();
			double dz = mob.getZ() - player.getZ();
			double angleToMob = Math.atan2(dx, dz);
			double playerYawRad = Math.toRadians(-player.getYaw());

			this.angle = angleToMob - playerYawRad;
			while (this.angle > Math.PI) this.angle -= 2 * Math.PI;
			while (this.angle < -Math.PI) this.angle += 2 * Math.PI;
		}
	}
}
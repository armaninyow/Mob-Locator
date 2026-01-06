package com.moblocator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
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

public class MobLocatorMod implements ClientModInitializer {
    public static final String MOD_ID = "moblocator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("MobLocator - Pixel-perfect icons with color configuration!");
        MobLocatorConfig.load();
        HudRenderCallback.EVENT.register(this::renderMobLocator);
    }

    private void renderMobLocator(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        
        PlayerEntity player = client.player;
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        
        // XP bar dimensions
        int xpBarWidth = 182;
        int centerX = screenWidth / 2;
        int xpBarLeft = centerX - xpBarWidth / 2;
        int baseY = screenHeight - 27;
        
        List<MobInfo> detectedMobs = new ArrayList<>();
        
        // Scan for mobs
        for (LivingEntity entity : client.world.getEntitiesByClass(LivingEntity.class, 
                player.getBoundingBox().expand(64), e -> e != player && e.isAlive())) {
            
            if (entity.isInvisible() && !MobLocatorConfig.showInvisibleMobs) continue;
            
            double distance = player.distanceTo(entity);
            float hearingRange = getHearingRange(entity);
            
            if (distance <= hearingRange) {
                boolean isHostile = isHostileMob(entity) || isAngryNeutralMob(entity);
                int iconType = getIconType(distance, hearingRange);
                
                // Determine color based on mob type
                int color;
                if (entity.isInvisible()) {
                    color = MobLocatorConfig.invisibleMobColor;
                } else if (isHostile) {
                    color = MobLocatorConfig.hostileMobColor;
                } else {
                    color = MobLocatorConfig.passiveMobColor;
                }
                
                // Check if mob is above or below player (more than 3 blocks)
                double dy = entity.getY() - player.getY();
                boolean isAbove = dy > 3;
                boolean isBelow = dy < -3;
                
                detectedMobs.add(new MobInfo(entity, player, iconType, color, isAbove, isBelow));
            }
        }
        
        // Render each mob indicator
        for (MobInfo mobInfo : detectedMobs) {
            renderMobIndicator(context, mobInfo, xpBarLeft, xpBarWidth, baseY);
        }
    }

    private void renderMobIndicator(DrawContext context, MobInfo mobInfo, int xpBarLeft, int xpBarWidth, int baseY) {
        double angle = mobInfo.angle;
        
        // Clamp to 180° FOV  
        double clampedAngle = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, angle));
        
        // Map angle to bar position
        double normalizedPos = 0.5 - (clampedAngle / Math.PI);
        
        int x = xpBarLeft + (int)(normalizedPos * xpBarWidth);
        int y = baseY;
        
        // Draw vertical arrow if mob is above or below
        if (mobInfo.isAbove) {
            IconRenderer.drawArrow(context, x, y - 6, true, mobInfo.color);
        } else if (mobInfo.isBelow) {
            IconRenderer.drawArrow(context, x, y + 6, false, mobInfo.color);
        }
        
        // Draw the pixel-perfect icon
        IconRenderer.drawIcon(context, x, y, mobInfo.iconType, mobInfo.color);
    }

    private float getHearingRange(LivingEntity entity) {
        // Accurate hearing ranges based on game data
        String entityName = entity.getClass().getSimpleName();
        
        if (entity instanceof EnderDragonEntity) return 192.0f;
        if (entity instanceof PhantomEntity) return 36.0f;
        if (entity instanceof WardenEntity) return 48.0f;
        if (entity instanceof GhastEntity) return 64.0f;
        if (entity instanceof BlazeEntity) return 48.0f;
        if (entityName.contains("Wither")) return 40.0f; // Wither
        if (entity instanceof EndermanEntity) return 64.0f;
        if (entity instanceof DrownedEntity) return 32.0f;
        if (entityName.equals("AllayEntity")) return 64f;
        if (entityName.equals("BatEntity")) return 8.0f;
        // All other mobs default to 16 blocks
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
        
        if (entity instanceof EndermanEntity enderman) {
            return enderman.isAngry();
        }
        if (entity instanceof PiglinEntity piglin) {
            return piglin.getTarget() == client.player;
        }
        if (entity instanceof ZombifiedPiglinEntity zombifiedPiglin) {
            return zombifiedPiglin.getTarget() == client.player;
        }
        if (entity instanceof WolfEntity wolf) {
            return wolf.isAttacking();
        }
        if (entity instanceof PolarBearEntity bear) {
            return bear.isAttacking();
        }
        if (entity instanceof SpiderEntity spider) {
            return spider.isAttacking();
        }
        if (entity instanceof CaveSpiderEntity caveSpider) {
            return caveSpider.isAttacking();
        }
        if (entity instanceof IronGolemEntity golem) {
            return golem.isAttacking();
        }
        if (entity instanceof BeeEntity bee) {
            return bee.hasAngerTime();
        }
        
        return false;
    }

    private static class MobInfo {
        double angle;
        int iconType;
        int color;
        boolean isAbove;
        boolean isBelow;
        float playerYaw;

        MobInfo(LivingEntity mob, PlayerEntity player, int iconType, int color, boolean isAbove, boolean isBelow) {
            this.iconType = iconType;
            this.color = color;
            this.isAbove = isAbove;
            this.isBelow = isBelow;
            this.playerYaw = player.getYaw();
            
            // Calculate angle relative to player's facing direction
            double dx = mob.getX() - player.getX();
            double dz = mob.getZ() - player.getZ();
            
            // Calculate angle from player to mob
            double angleToMob = Math.atan2(dx, dz);
            double playerYawRad = Math.toRadians(-player.getYaw());
            
            // Get relative angle
            this.angle = angleToMob - playerYawRad;
            
            // Normalize to -PI to PI
            while (this.angle > Math.PI) this.angle -= 2 * Math.PI;
            while (this.angle < -Math.PI) this.angle += 2 * Math.PI;
        }
    }
}
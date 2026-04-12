package com.armaninyow.moblocator.mixin;

import com.armaninyow.moblocator.MobLocator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Inject(method = "render", at = @At("TAIL"))
	private void moblocator$renderOnTop(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		if (MinecraftClient.getInstance().player == null) return;

		// In 1.21, LayeredDrawer bumps Z by 200f per layer (LAYER_Z_PADDING).
		// The vanilla HUD has ~15 layers, so hotbar/XP/health sit around Z 200-3000.
		// We translate to Z=10000 to guarantee we're drawn on top of everything.
		context.getMatrices().push();
		context.getMatrices().translate(0, 0, 10000);
		MobLocator.INSTANCE.renderMobLocator(context, tickCounter);
		context.getMatrices().pop();
	}
}
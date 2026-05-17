package com.armaninyow.moblocator;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.ArrayList;

public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			// Build the toggle first so we can reference it for availability
			Option<Boolean> showInvisibleOption = Option.<Boolean>createBuilder()
				.name(Component.translatable("moblocator.config.showInvisible"))
				.description(OptionDescription.of(Component.translatable("moblocator.config.showInvisible.tooltip")))
				.binding(
					false,
					() -> MobLocatorConfig.showInvisibleMobs,
					val -> MobLocatorConfig.showInvisibleMobs = val
				)
				.controller(BooleanControllerBuilder::create)
				.build();

			Option<Color> invisibleColorOption = Option.<Color>createBuilder()
				.name(Component.translatable("moblocator.config.invisibleColor"))
				.description(OptionDescription.of(Component.translatable("moblocator.config.invisibleColor.tooltip")))
				.binding(
					new Color(0x808080),
					() -> new Color(MobLocatorConfig.invisibleMobColor),
					val -> MobLocatorConfig.invisibleMobColor = val.getRGB() & 0xFFFFFF
				)
				.controller(ColorControllerBuilder::create)
				.available(MobLocatorConfig.showInvisibleMobs)
				.build();

			// Keep invisible color greyed out in sync with the toggle
			showInvisibleOption.addListener((opt, val) -> invisibleColorOption.setAvailable(val));

			return YetAnotherConfigLib.createBuilder()
				.title(Component.translatable("moblocator.config.title"))
				.category(ConfigCategory.createBuilder()
					.name(Component.translatable("moblocator.config.settings"))

					// Hostile Mob Color
					.option(Option.<Color>createBuilder()
						.name(Component.translatable("moblocator.config.hostileColor"))
						.description(OptionDescription.of(Component.translatable("moblocator.config.hostileColor.tooltip")))
						.binding(
							new Color(0xFF0000),
							() -> new Color(MobLocatorConfig.hostileMobColor),
							val -> MobLocatorConfig.hostileMobColor = val.getRGB() & 0xFFFFFF
						)
						.controller(ColorControllerBuilder::create)
						.build()
					)

					// Passive Mob Color
					.option(Option.<Color>createBuilder()
						.name(Component.translatable("moblocator.config.passiveColor"))
						.description(OptionDescription.of(Component.translatable("moblocator.config.passiveColor.tooltip")))
						.binding(
							new Color(0xFFFFFF),
							() -> new Color(MobLocatorConfig.passiveMobColor),
							val -> MobLocatorConfig.passiveMobColor = val.getRGB() & 0xFFFFFF
						)
						.controller(ColorControllerBuilder::create)
						.build()
					)

					// Show Invisible Mobs toggle
					.option(showInvisibleOption)

					// Invisible Mob Color (greyed out when show invisible is off)
					.option(invisibleColorOption)

					// Blacklisted Mobs list
					.group(ListOption.<String>createBuilder()
						.name(Component.translatable("moblocator.config.blacklistedMobs"))
						.description(OptionDescription.of(Component.translatable("moblocator.config.blacklistedMobs.tooltip")))
						.binding(
							new ArrayList<>(),
							() -> new ArrayList<>(MobLocatorConfig.blacklistedMobs),
							val -> {
								MobLocatorConfig.blacklistedMobs.clear();
								MobLocatorConfig.blacklistedMobs.addAll(val);
							}
						)
						.controller(StringControllerBuilder::create)
						.initial("")
						.build()
					)

					.build()
				)
				.save(MobLocatorConfig::save)
				.build()
				.generateScreen(parent);
		};
	}
}
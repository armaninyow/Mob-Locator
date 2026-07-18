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
import java.util.List;
import java.util.Map;

public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {

			Option<Boolean> usePerMobColorsOption = Option.<Boolean>createBuilder()
				.name(Component.translatable("moblocator.config.usePerMobColors"))
				.description(OptionDescription.of(Component.translatable("moblocator.config.usePerMobColors.tooltip")))
				.binding(
					false,
					() -> MobLocatorConfig.usePerMobColors,
					val -> MobLocatorConfig.usePerMobColors = val
				)
				.controller(BooleanControllerBuilder::create)
				.build();

			Option<Boolean> useBlackOutlinesOption = Option.<Boolean>createBuilder()
				.name(Component.translatable("moblocator.config.useBlackOutlines"))
				.description(OptionDescription.of(Component.translatable("moblocator.config.useBlackOutlines.tooltip")))
				.binding(
					false,
					() -> MobLocatorConfig.useBlackOutlines,
					val -> MobLocatorConfig.useBlackOutlines = val
				)
				.controller(BooleanControllerBuilder::create)
				.available(MobLocatorConfig.usePerMobColors)
				.build();

			usePerMobColorsOption.addListener((opt, val) -> useBlackOutlinesOption.setAvailable(val));

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

			Option<Color> hostileColorOption = Option.<Color>createBuilder()
				.name(Component.translatable("moblocator.config.hostileColor"))
				.description(OptionDescription.of(Component.translatable("moblocator.config.hostileColor.tooltip")))
				.binding(
					new Color(0xFF0000),
					() -> new Color(MobLocatorConfig.hostileMobColor),
					val -> MobLocatorConfig.hostileMobColor = val.getRGB() & 0xFFFFFF
				)
				.controller(ColorControllerBuilder::create)
				.build();

			Option<Color> passiveColorOption = Option.<Color>createBuilder()
				.name(Component.translatable("moblocator.config.passiveColor"))
				.description(OptionDescription.of(Component.translatable("moblocator.config.passiveColor.tooltip")))
				.binding(
					new Color(0xFFFFFF),
					() -> new Color(MobLocatorConfig.passiveMobColor),
					val -> MobLocatorConfig.passiveMobColor = val.getRGB() & 0xFFFFFF
				)
				.controller(ColorControllerBuilder::create)
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

			showInvisibleOption.addListener((opt, val) -> invisibleColorOption.setAvailable(val));

			List<String> defaultPerMobList = buildPerMobStringList(MobLocatorConfig.DEFAULT_PER_MOB_COLORS);

			ListOption<String> perMobColorList = ListOption.<String>createBuilder()
				.name(Component.translatable("moblocator.config.perMobColors"))
				.description(OptionDescription.of(Component.translatable("moblocator.config.perMobColors.tooltip")))
				.binding(
					defaultPerMobList,
					() -> buildPerMobStringList(MobLocatorConfig.perMobColors),
					val -> applyPerMobStringList(val)
				)
				.controller(StringControllerBuilder::create)
				.initial("Mob Name=#RRGGBB")
				.available(MobLocatorConfig.usePerMobColors)
				.build();

			usePerMobColorsOption.addListener((opt, val) -> perMobColorList.setAvailable(val));

			ListOption<String> blacklistOption = ListOption.<String>createBuilder()
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
				.build();

			return YetAnotherConfigLib.createBuilder()
				.title(Component.translatable("moblocator.config.title"))

				.category(ConfigCategory.createBuilder()
					.name(Component.translatable("moblocator.config.settings"))

					.option(usePerMobColorsOption)

					.option(useBlackOutlinesOption)

					.option(hostileColorOption)

					.option(passiveColorOption)

					.option(showInvisibleOption)

					.option(invisibleColorOption)

					.group(blacklistOption)

					.build()
				)

				.category(ConfigCategory.createBuilder()
					.name(Component.translatable("moblocator.config.perMobColors.tab"))
					.tooltip(Component.translatable("moblocator.config.perMobColors.tab.tooltip"))

					.group(perMobColorList)

					.build()
				)

				.save(MobLocatorConfig::save)
				.build()
				.generateScreen(parent);
		};
	}

	private static List<String> buildPerMobStringList(Map<String, Integer> map) {
		List<String> list = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			list.add(entry.getKey() + "=#" + String.format("%06X", entry.getValue() & 0xFFFFFF));
		}
		return list;
	}

	private static void applyPerMobStringList(List<String> list) {
		MobLocatorConfig.perMobColors.clear();
		MobLocatorConfig.perMobColors.putAll(MobLocatorConfig.DEFAULT_PER_MOB_COLORS);

		for (String entry : list) {
			int sep = entry.lastIndexOf('=');
			if (sep < 1) continue;
			String name = entry.substring(0, sep).trim();
			String hex  = entry.substring(sep + 1).trim();
			if (hex.startsWith("#")) hex = hex.substring(1);
			try {
				int color = Integer.parseInt(hex, 16) & 0xFFFFFF;
				MobLocatorConfig.perMobColors.put(name, color);
			} catch (NumberFormatException ignored) {
			}
		}
	}
}
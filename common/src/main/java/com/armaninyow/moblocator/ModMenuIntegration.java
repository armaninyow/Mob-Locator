package com.armaninyow.moblocator;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.translatable("moblocator.config.title"));

			builder.setGlobalized(false);
			builder.setGlobalizedExpanded(false);

			ConfigEntryBuilder entryBuilder = builder.entryBuilder();

			ConfigCategory settings = builder.getOrCreateCategory(Text.translatable("moblocator.config.settings"));

			// Hostile Mob Color
			settings.addEntry(entryBuilder.startColorField(
				Text.translatable("moblocator.config.hostileColor"),
				MobLocatorConfig.hostileMobColor)
				.setDefaultValue(0xFF0000)
				.setTooltip(Text.translatable("moblocator.config.hostileColor.tooltip"))
				.setSaveConsumer(value -> MobLocatorConfig.hostileMobColor = value & 0xFFFFFF)
				.build()
			);

			// Passive Mob Color
			settings.addEntry(entryBuilder.startColorField(
				Text.translatable("moblocator.config.passiveColor"),
				MobLocatorConfig.passiveMobColor)
				.setDefaultValue(0xFFFFFF)
				.setTooltip(Text.translatable("moblocator.config.passiveColor.tooltip"))
				.setSaveConsumer(value -> MobLocatorConfig.passiveMobColor = value & 0xFFFFFF)
				.build()
			);

			// Show Invisible Mobs toggle
			settings.addEntry(entryBuilder.startBooleanToggle(
				Text.translatable("moblocator.config.showInvisible"),
				MobLocatorConfig.showInvisibleMobs)
				.setDefaultValue(false)
				.setTooltip(Text.translatable("moblocator.config.showInvisible.tooltip"))
				.setSaveConsumer(value -> MobLocatorConfig.showInvisibleMobs = value)
				.build()
			);

			// Invisible Mob Color
			settings.addEntry(entryBuilder.startColorField(
				Text.translatable("moblocator.config.invisibleColor"),
				MobLocatorConfig.invisibleMobColor)
				.setDefaultValue(0x808080)
				.setTooltip(Text.translatable("moblocator.config.invisibleColor.tooltip"))
				.setSaveConsumer(value -> MobLocatorConfig.invisibleMobColor = value & 0xFFFFFF)
				.build()
			);

			// Blacklisted Mobs list
			List<String> blacklistCopy = new ArrayList<>(MobLocatorConfig.blacklistedMobs);
			settings.addEntry(entryBuilder.startStrList(
				Text.translatable("moblocator.config.blacklistedMobs"),
				blacklistCopy)
				.setDefaultValue(new ArrayList<>())
				.setTooltip(Text.translatable("moblocator.config.blacklistedMobs.tooltip"))
				.setSaveConsumer(newList -> {
					MobLocatorConfig.blacklistedMobs.clear();
					MobLocatorConfig.blacklistedMobs.addAll(newList);
				})
				.setExpanded(true)
				.setInsertInFront(false)
				.build()
			);

			builder.setSavingRunnable(MobLocatorConfig::save);

			return builder.build();
		};
	}
}
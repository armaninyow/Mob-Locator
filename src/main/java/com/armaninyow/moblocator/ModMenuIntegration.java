package com.armaninyow.moblocator;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.TextFieldListEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModMenuIntegration implements ModMenuApi {

	private TextFieldListEntry invisibleColorEntry;

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.literal("Mob Locator Configuration"));

			builder.setGlobalized(false);
			builder.setGlobalizedExpanded(false);

			ConfigEntryBuilder entryBuilder = builder.entryBuilder();

			ConfigCategory settings = builder.getOrCreateCategory(Text.literal("Settings"));

			// Hostile Mob Color - hex input without #
			settings.addEntry(entryBuilder.startTextField(
				Text.literal("Hostile Mob Color"),
				String.format("%06X", MobLocatorConfig.hostileMobColor))
				.setDefaultValue("FF0000")
				.setTooltip(Text.literal("Color for hostile/enemy mobs (default: FF0000)"))
				.setSaveConsumer(value -> {
					try {
						MobLocatorConfig.hostileMobColor = Integer.parseInt(value.replace("#", "").toUpperCase(), 16) & 0xFFFFFF;
					} catch (NumberFormatException e) {
						MobLocatorConfig.hostileMobColor = 0xFF0000;
					}
				})
				.setErrorSupplier(value -> {
					try {
						String cleaned = value.replace("#", "").toUpperCase();
						if (cleaned.length() > 6) return Optional.of(Text.literal("Max 6 characters"));
						Integer.parseInt(cleaned, 16);
					} catch (NumberFormatException e) {
						return Optional.of(Text.literal("Invalid hex color"));
					}
					return Optional.empty();
				})
				.build()
			);

			// Passive Mob Color - hex input without #
			settings.addEntry(entryBuilder.startTextField(
				Text.literal("Passive Mob Color"),
				String.format("%06X", MobLocatorConfig.passiveMobColor))
				.setDefaultValue("FFFFFF")
				.setTooltip(Text.literal("Color for passive/friendly mobs (default: FFFFFF)"))
				.setSaveConsumer(value -> {
					try {
						MobLocatorConfig.passiveMobColor = Integer.parseInt(value.replace("#", "").toUpperCase(), 16) & 0xFFFFFF;
					} catch (NumberFormatException e) {
						MobLocatorConfig.passiveMobColor = 0xFFFFFF;
					}
				})
				.setErrorSupplier(value -> {
					try {
						String cleaned = value.replace("#", "").toUpperCase();
						if (cleaned.length() > 6) return Optional.of(Text.literal("Max 6 characters"));
						Integer.parseInt(cleaned, 16);
					} catch (NumberFormatException e) {
						return Optional.of(Text.literal("Invalid hex color"));
					}
					return Optional.empty();
				})
				.build()
			);

			// Show Invisible Mobs toggle
			settings.addEntry(entryBuilder.startBooleanToggle(
				Text.literal("Show Invisible Mobs"),
				MobLocatorConfig.showInvisibleMobs)
				.setDefaultValue(false)
				.setTooltip(Text.literal("Show invisible mobs on the locator bar (displayed in gray). Save settings to enable/disable Invisible Mob Color field."))
				.setSaveConsumer(newValue -> {
					MobLocatorConfig.showInvisibleMobs = newValue;
					if (invisibleColorEntry != null) {
						invisibleColorEntry.setEditable(newValue);
					}
				})
				.build()
			);

			// Invisible Mob Color - hex input without #
			invisibleColorEntry = entryBuilder.startTextField(
				Text.literal("Invisible Mob Color"),
				String.format("%06X", MobLocatorConfig.invisibleMobColor))
				.setDefaultValue("808080")
				.setTooltip(Text.literal("Color for invisible mobs when shown (default: 808080)"))
				.setSaveConsumer(value -> {
					try {
						MobLocatorConfig.invisibleMobColor = Integer.parseInt(value.replace("#", "").toUpperCase(), 16) & 0xFFFFFF;
					} catch (NumberFormatException e) {
						MobLocatorConfig.invisibleMobColor = 0x808080;
					}
				})
				.setErrorSupplier(value -> {
					try {
						String cleaned = value.replace("#", "").toUpperCase();
						if (cleaned.length() > 6) return Optional.of(Text.literal("Max 6 characters"));
						Integer.parseInt(cleaned, 16);
					} catch (NumberFormatException e) {
						return Optional.of(Text.literal("Invalid hex color"));
					}
					return Optional.empty();
				})
				.build();

			invisibleColorEntry.setEditable(MobLocatorConfig.showInvisibleMobs);
			settings.addEntry(invisibleColorEntry);

			// Blacklisted Mobs list
			List<String> blacklistCopy = new ArrayList<>(MobLocatorConfig.blacklistedMobs);
			settings.addEntry(entryBuilder.startStrList(
				Text.literal("Blacklisted Mobs (The text fields are transparent. Be careful!)"),
				blacklistCopy)
				.setDefaultValue(new ArrayList<>())
				.setTooltip(Text.literal("Add mob names to prevent them from showing on the locator (e.g., Creeper, Iron Golem)"))
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
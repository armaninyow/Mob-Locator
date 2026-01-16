package com.armaninyow.moblocator;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.literal("Mob Locator Configuration"));

			ConfigEntryBuilder entryBuilder = builder.entryBuilder();

			ConfigCategory general = builder.getOrCreateCategory(Text.literal("General Settings"));

			general.addEntry(entryBuilder.startBooleanToggle(
				Text.literal("Show Invisible Mobs"),
				MobLocatorConfig.showInvisibleMobs)
				.setDefaultValue(false)
				.setTooltip(Text.literal("Show invisible mobs on the locator bar (displayed in gray)"))
				.setSaveConsumer(newValue -> MobLocatorConfig.showInvisibleMobs = newValue)
				.build()
			);

			ConfigCategory colors = builder.getOrCreateCategory(Text.literal("Color Settings"));

			colors.addEntry(entryBuilder.startColorField(
				Text.literal("Hostile Mob Color"),
				MobLocatorConfig.hostileMobColor)
				.setDefaultValue(0xFF0000)
				.setTooltip(Text.literal("Color for hostile/enemy mobs (default: Red)"))
				.setSaveConsumer(newValue -> MobLocatorConfig.hostileMobColor = newValue)
				.build()
			);

			colors.addEntry(entryBuilder.startColorField(
				Text.literal("Passive Mob Color"),
				MobLocatorConfig.passiveMobColor)
				.setDefaultValue(0xFFFFFF)
				.setTooltip(Text.literal("Color for passive/friendly mobs (default: White)"))
				.setSaveConsumer(newValue -> MobLocatorConfig.passiveMobColor = newValue)
				.build()
			);

			colors.addEntry(entryBuilder.startColorField(
				Text.literal("Invisible Mob Color"),
				MobLocatorConfig.invisibleMobColor)
				.setDefaultValue(0x808080)
				.setTooltip(Text.literal("Color for invisible mobs when shown (default: Gray)"))
				.setSaveConsumer(newValue -> MobLocatorConfig.invisibleMobColor = newValue)
				.build()
			);

			builder.setSavingRunnable(MobLocatorConfig::save);

			return builder.build();
		};
	}
}
package com.armaninyow.moblocator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MobLocatorConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(
        FabricLoader.getInstance().getConfigDir().toFile(), 
        "moblocator.json"
    );

    // Default values (RGB only, no alpha)
    public static boolean showInvisibleMobs = false;
    public static int hostileMobColor = 0xFF0000; // Red
    public static int passiveMobColor = 0xFFFFFF; // White
    public static int invisibleMobColor = 0x808080; // Gray

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    showInvisibleMobs = data.showInvisibleMobs;
                    hostileMobColor = data.hostileMobColor & 0xFFFFFF; // Remove alpha if present
                    passiveMobColor = data.passiveMobColor & 0xFFFFFF;
                    invisibleMobColor = data.invisibleMobColor & 0xFFFFFF;
                }
            } catch (IOException e) {
                MobLocatorMod.LOGGER.error("Failed to load config", e);
            }
        }
        save();
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            ConfigData data = new ConfigData();
            data.showInvisibleMobs = showInvisibleMobs;
            data.hostileMobColor = hostileMobColor & 0xFFFFFF; // Remove alpha
            data.passiveMobColor = passiveMobColor & 0xFFFFFF;
            data.invisibleMobColor = invisibleMobColor & 0xFFFFFF;
            GSON.toJson(data, writer);
        } catch (IOException e) {
            MobLocatorMod.LOGGER.error("Failed to save config", e);
        }
    }

    private static class ConfigData {
        boolean showInvisibleMobs = false;
        int hostileMobColor = 0xFF0000;
        int passiveMobColor = 0xFFFFFF;
        int invisibleMobColor = 0x808080;
    }
}
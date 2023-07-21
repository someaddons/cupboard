package com.cupboard.config;

import com.cupboard.Cupboard;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CupboardConfig<C extends ICommonConfig> {

    /**
     * Loaded everywhere, not synced
     */
    private final C commonConfig;
    private final String filename;
    private boolean loaded = false;

    public static List<CupboardConfig> allConfigs = new ArrayList<>();

    /**
     * Loaded clientside, not synced
     */
    final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Builds configuration tree.
     */
    public CupboardConfig(final String filename, final C commonConfig) {
        this.commonConfig = commonConfig;
        this.filename = filename;
        allConfigs.add(this);
    }

    public void load() {
        loaded = true;
        final Path configPath = FabricLoader.getInstance().getConfigDir().normalize().resolve(filename + ".json");
        final File config = configPath.toFile();

        if (!config.exists()) {
            Cupboard.LOGGER.warn("Config " + filename + " not found, recreating default");
            save();
            load();
        } else {
            try {
                commonConfig.deserialize(gson.fromJson(Files.newBufferedReader(configPath), JsonObject.class));
            } catch (Exception e) {
                Cupboard.LOGGER.error("Could not read config " + filename + " from:" + configPath, e);
                save();
                load();
            }
        }
    }

    public void save() {
        final Path configPath = FabricLoader.getInstance().getConfigDir().normalize().resolve(filename + ".json");
        try {
            final BufferedWriter writer = Files.newBufferedWriter(configPath);
            gson.toJson(commonConfig.serialize(), JsonObject.class, writer);
            writer.close();
        } catch (IOException e) {
            Cupboard.LOGGER.error("Could not write config " + filename + " to:" + configPath, e);
        }
    }

    public C getCommonConfig() {
        if (!loaded) {
            load();
        }

        return commonConfig;
    }
}

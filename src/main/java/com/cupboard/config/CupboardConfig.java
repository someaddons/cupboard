package com.cupboard.config;

import com.cupboard.Cupboard;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CupboardConfig<C extends ICommonConfig> {

    private static Set<CupboardConfig> allConfigs = new HashSet<>();
    private static WatchService watchService = null;
    private static Map<WatchKey, CupboardConfig> watchedConfigs = new HashMap<>();
    private final static Map<CupboardConfig, Integer> scheuledReloads = new HashMap<>();
    private static long lastUpdate = 0;

    /**
     * Loads all registered configs
     */
    public static void initloadAll() {
        for (CupboardConfig config : CupboardConfig.allConfigs) {
            config.getCommonConfig();
        }
    }

    /**
     * Registers the given config and adds a watch entry
     * @param config
     */
    private static void registerConfig(final CupboardConfig config) {
        if (!allConfigs.contains(config)) {
            allConfigs.add(config);
            try {
                watchedConfigs.put(config.configPath.getParent().register(getWatchService(), StandardWatchEventKinds.ENTRY_MODIFY), config);
            } catch (IOException e) {
                Cupboard.LOGGER.warn("Failed to register config path to file watcher", e);
            }
        }
    }

    /**
     * Get watch service
     * @return
     */
    private static WatchService getWatchService() {
        if (watchService == null) {
            try {
                watchService = FileSystems.getDefault().newWatchService();
            } catch (Throwable e) {
                Cupboard.LOGGER.warn("Failed to create config file watcher", e);
            }
        }

        return watchService;
    }

    /**
     * Polls the watch server for changes once a second
     */
    public static void pollConfigs() {
        if (System.currentTimeMillis() - lastUpdate > 1000) {
            lastUpdate = System.currentTimeMillis();
        } else {
            return;
        }

        WatchKey watchKey = getWatchService().poll();
        if (watchKey != null) {
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {

                    final CupboardConfig config = watchedConfigs.get(watchKey);

                    if (config.saveCounter > 0) {
                        config.saveCounter = 0;
                    } else {
                        // schedule reload to happen in 10sec, or refresh existing to 10s
                        scheuledReloads.put(config, 10);
                    }
                }
            }
            watchKey.reset();
        }

        for (final Iterator<Map.Entry<CupboardConfig, Integer>> iterator = scheuledReloads.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<CupboardConfig, Integer> entry = iterator.next();
            if (entry.getValue() > 0) {
                entry.setValue(entry.getValue() - 1);
            } else {
                iterator.remove();
                entry.getKey().load(false);
            }
        }
    }

    /**
     * Loaded everywhere, not synced
     */
    private C commonConfig;
    private final String filename;
    private int loaded = 0;
    private int saveCounter = 0;
    private final Path configPath;

    /**
     * Loaded clientside, not synced
     */
    final static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * Builds configuration tree.
     */
    public CupboardConfig(final String filename, final C commonConfig)
    {
        this.commonConfig = commonConfig;
        this.filename = filename;
        configPath = FMLPaths.CONFIGDIR.get().resolve(filename + ".json");
        registerConfig(this);
    }

    public void load() {
        load(true);
    }

    /**
     * Loads the config from file
     */
    public void load(boolean manualReload) {
        loaded++;
        final File config = configPath.toFile();

        if (!config.exists())
        {
            Cupboard.LOGGER.warn("Config " + filename + " not found, recreating default");
            if (loaded < 3 && manualReload) {
                save();
                load();
            }
        } else {
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                commonConfig.deserialize(gson.fromJson(reader, JsonObject.class));
                Cupboard.LOGGER.info("Loaded config for: " + filename);
            } catch (Exception e) {
                Cupboard.LOGGER.error("Could not read config " + filename + " from:" + configPath, e);

                try {
                    commonConfig = (C) commonConfig.getClass().getDeclaredConstructor().newInstance();
                } catch (Throwable ex) {
                    Cupboard.LOGGER.warn("Error reinitialising config", ex);
                }
                if (loaded < 3 && manualReload) {
                    save();
                    load();
                }
            }
        }
    }

    /**
     * Saves the config to file
     */
    public void save() {
        saveCounter++;
        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            gson.toJson(commonConfig.serialize(), JsonObject.class, writer);
        } catch (Throwable e) {
            Cupboard.LOGGER.error("Could not write config " + filename + " to:" + configPath, e);
        }
    }

    public C getCommonConfig() {
        if (loaded == 0) {
            load();
        }

        return commonConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CupboardConfig<?> config = (CupboardConfig<?>) o;
        return Objects.equals(configPath, config.configPath);
    }

    @Override
    public int hashCode() {
        return configPath.hashCode();
    }
}

package com.cupboard;

import com.cupboard.config.CommonConfiguration;
import com.cupboard.config.CupboardConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class Cupboard implements ModInitializer {

    public static final String MOD_ID = "cupboard";
    public static final Logger LOGGER = LogManager.getLogger();
    private static CupboardConfig<CommonConfiguration> config = null;
    public static Random rand = new Random();

    public Cupboard() {
        // TODO disable
        getConfig();

        CupboardConfig.initloadAll();

        ServerTickEvents.END_SERVER_TICK.register(s -> CupboardConfig.pollConfigs());
    }

    public static CupboardConfig<CommonConfiguration> getConfig() {
        if (config == null) {
            config = new CupboardConfig<>(MOD_ID, new CommonConfiguration());
            config.load();
        }

        return config;
    }

    @Override
    public void onInitialize() {

    }
}

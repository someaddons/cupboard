package com.cupboard;

import com.cupboard.config.CommonConfiguration;
import com.cupboard.config.CupboardConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class Cupboard implements ModInitializer {

    public static final String MOD_ID = "cupboard";
    public static final Logger LOGGER = LogManager.getLogger();
    private static CupboardConfig<CommonConfiguration> config = null;
    public static Random rand = new Random();

    public Cupboard() {
        for (CupboardConfig config : CupboardConfig.allConfigs) {
            config.getCommonConfig();
        }
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

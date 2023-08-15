package com.cupboard;

import com.cupboard.config.CommonConfiguration;
import com.cupboard.config.CupboardConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class Cupboard implements ModInitializer {

    public static final String MOD_ID = "cupboard";
    public static final Logger LOGGER = LogManager.getLogger();
    public static CupboardConfig<CommonConfiguration> config = new CupboardConfig(MOD_ID, new CommonConfiguration());
    public static Random rand = new Random();

    public Cupboard() {
        //getConfig();

        ServerTickEvents.END_SERVER_TICK.register(s -> CupboardConfig.pollConfigs());
        ServerLifecycleEvents.SERVER_STARTED.register(s -> CupboardConfig.initloadAll());
    }

    @Override
    public void onInitialize() {

    }
}

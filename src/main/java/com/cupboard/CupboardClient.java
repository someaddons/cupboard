package com.cupboard;

import com.cupboard.config.CupboardConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;


public class CupboardClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(c -> CupboardConfig.initloadAll());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!Minecraft.getInstance().hasSingleplayerServer()) {
                CupboardConfig.pollConfigs();
            }
        });
    }
}

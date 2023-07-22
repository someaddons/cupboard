package com.cupboard;

import com.cupboard.config.CupboardConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

import static net.minecraft.world.level.Level.OVERWORLD;


public class CupboardClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (!Minecraft.getInstance().hasSingleplayerServer() && world.dimension().equals(OVERWORLD)) {
                CupboardConfig.pollConfigs();
            }
        });
    }
}

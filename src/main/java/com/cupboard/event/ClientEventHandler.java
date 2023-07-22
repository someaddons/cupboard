package com.cupboard.event;

import com.cupboard.config.CupboardConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler
{
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && !Minecraft.getInstance().hasSingleplayerServer())
        {
            CupboardConfig.pollConfigs();
        }
    }
}

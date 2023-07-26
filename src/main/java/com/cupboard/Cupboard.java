package com.cupboard;

import com.cupboard.config.CommonConfiguration;
import com.cupboard.config.CupboardConfig;
import com.cupboard.event.EventHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.cupboard.Cupboard.MOD_ID;

@Mod(MOD_ID)
public class Cupboard
{
    public static final String        MOD_ID = "cupboard";
    public static final Logger                              LOGGER = LogManager.getLogger();
    private static      CupboardConfig<CommonConfiguration> config = null;
    public static       Random                              rand   = new Random();

    public Cupboard()
    {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (a, b) -> true));
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(EventHandler.class);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event)
    {
        // Side safe client event handler
        CupboardClient.onInitializeClient(event);
    }

    public static CupboardConfig<CommonConfiguration> getConfig()
    {
        if (config == null)
        {
            config = new CupboardConfig<>(MOD_ID,new CommonConfiguration());
        }

        return config;
    }
}

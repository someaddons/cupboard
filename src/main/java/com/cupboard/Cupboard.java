package com.cupboard;

import com.cupboard.config.CommonConfiguration;
import com.cupboard.config.CupboardConfig;
import com.cupboard.event.EventHandler;
import com.sun.management.HotSpotDiagnosticMXBean;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.util.Random;

import static com.cupboard.Cupboard.MOD_ID;

@Mod(MOD_ID)
public class Cupboard
{
    public static final String                              MOD_ID = "cupboard";
    public static final Logger                              LOGGER = LogManager.getLogger();
    public static       CupboardConfig<CommonConfiguration> config = new CupboardConfig<>(MOD_ID, new CommonConfiguration());
    public static       Random                              rand   = new Random();

    public Cupboard()
    {
        if (Cupboard.config.getCommonConfig().forceHeapDumpOnOOM)
        {
            try
            {
                HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(
                  ManagementFactory.getPlatformMBeanServer(),
                  "com.sun.management:type=HotSpotDiagnostic",
                  HotSpotDiagnosticMXBean.class);

                bean.setVMOption("HeapDumpOnOutOfMemoryError", "true");
                bean.setVMOption("HeapDumpPath", FMLPaths.GAMEDIR.get().toString());
            }
            catch (Exception e)
            {
                LOGGER.error("Failed to enable heapdump option: ", e);
            }
        }

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
}

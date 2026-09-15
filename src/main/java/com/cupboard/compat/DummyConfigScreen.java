package com.cupboard.compat;

import com.cupboard.config.CupboardConfig;
import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class DummyConfigScreen extends Screen
{
    private final CupboardConfig config;

    public DummyConfigScreen(final CupboardConfig config)
    {
        super(Component.literal("dummy"));
        this.config = config;
    }

    public void openConfigFile()
    {
        Blaze3D.openPath(config.getPath());
    }
}

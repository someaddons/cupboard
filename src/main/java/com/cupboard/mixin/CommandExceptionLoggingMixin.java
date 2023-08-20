package com.cupboard.mixin;

import com.cupboard.Cupboard;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Commands.class)
public class CommandExceptionLoggingMixin
{
    @Redirect(method = "performCommand", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;isDebugEnabled()Z", remap = false))
    private boolean cupboard$logCommandError(final Logger instance)
    {
        return instance.isDebugEnabled() || Cupboard.config.getCommonConfig().showCommandExecutionErrors;
    }
}

package com.cupboard.mixin;

import net.minecraft.util.ThreadingDetector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Stream;

@Mixin(ThreadingDetector.class)
public class ThreadingDetectorMixin
{
    @Redirect(method = "makeThreadingException", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;of([Ljava/lang/Object;)Ljava/util/stream/Stream;"))
    private static <T> Stream<T> onGetThreads(final T[] values)
    {
        return (Stream<T>) Thread.getAllStackTraces().keySet().stream();
    }
}

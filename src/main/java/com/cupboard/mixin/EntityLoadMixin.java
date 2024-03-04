package com.cupboard.mixin;

import com.cupboard.Cupboard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityLoadMixin
{
    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract float getYRot();

    @Shadow
    public abstract float getXRot();

    @Shadow
    public abstract void setPosRaw(final double p_20344_, final double p_20345_, final double p_20346_);

    @Shadow
    public abstract void setXRot(final float p_146927_);

    @Shadow
    public abstract void setYRot(final float p_146923_);

    @Shadow
    private float xRot;

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setYBodyRot(F)V", shift = At.Shift.AFTER))
    private void avoidLoadCrash(final CompoundTag compoundTag, final CallbackInfo ci)
    {
        if (!(Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ())) && Cupboard.config.getCommonConfig().skipErrorOnEntityLoad)
        {
            Cupboard.LOGGER.warn("Prevented crash for entity loaded with invalid position, defaulting to 0 70 0. Nbt:" + compoundTag.toString());
            setPosRaw(0, 70, 0);
        }

        if (!(Double.isFinite((double) this.getYRot()) && Double.isFinite((double) this.getXRot())))
        {
            Cupboard.LOGGER.warn("Prevented crash for entity loaded with invalid rotations, defaulting to 0 0. Nbt:" + compoundTag.toString());
            setXRot(0);
            setYRot(0);
        }
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/CrashReport;forThrowable(Ljava/lang/Throwable;Ljava/lang/String;)Lnet/minecraft/CrashReport;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void skipLoadingErroringEntity(final CompoundTag compoundTag, final CallbackInfo ci, final Throwable throwable)
    {
        if (Cupboard.config.getCommonConfig().skipErrorOnEntityLoad)
        {
            Cupboard.LOGGER.warn("Prevented crash for entity load. Nbt:" + compoundTag.toString(), throwable);
            ci.cancel();
        }
    }
}

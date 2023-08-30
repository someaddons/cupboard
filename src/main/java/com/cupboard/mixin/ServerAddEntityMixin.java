package com.cupboard.mixin;

import com.cupboard.Cupboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(ServerLevel.class)
public abstract class ServerAddEntityMixin
{
    @Shadow
    public abstract boolean addEntity(final Entity entityIn);

    @Unique
    private final ConcurrentLinkedQueue<Entity> toAdd = new ConcurrentLinkedQueue<>();

    @Unique
    private final ConcurrentHashMap<EntityType, EntityType> warned = new ConcurrentHashMap<>();

    @Unique
    private boolean addingLate = false;

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void OnaddEntity(Entity entityIn, CallbackInfoReturnable<Boolean> c)
    {
        String current = Thread.currentThread().getName();
        if (!current.toLowerCase().contains("server"))
        {
            if (Cupboard.config.getCommonConfig().logOffthreadEntityAdd && !warned.contains(entityIn.getType()))
            {
                Cupboard.LOGGER.warn("A mod is trying to add an entity from offthread, this should be avoided. Printing trace:", new Exception());
                warned.put(entityIn.getType(), entityIn.getType());
            }

            toAdd.add(entityIn);
            c.setReturnValue(true);
        }
        else if (!addingLate)
        {
            addingLate = true;

            Iterator<Entity> it = toAdd.iterator();
            while (it.hasNext())
            {
                this.addEntity(it.next());
                it.remove();
            }
            addingLate = false;
        }
    }
}

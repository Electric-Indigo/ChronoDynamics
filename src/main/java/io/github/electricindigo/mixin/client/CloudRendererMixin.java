package io.github.electricindigo.mixin.client;

import io.github.electricindigo.client.TimeDistortion;
import net.minecraft.client.renderer.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CloudRenderer.class)
public abstract class CloudRendererMixin
{
    @ModifyVariable(method = "prepare(ILnet/minecraft/client/CloudStatus;FILnet/minecraft/world/phys/Vec3;JF)V", at = @At("HEAD"), argsOnly = true)
    private long chronodynamics$shiftCloudTime(long gameTime)
    {
        return gameTime + TimeDistortion.offsetTicks();
    }
}

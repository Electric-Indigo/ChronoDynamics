package io.github.electricindigo.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.electricindigo.client.TimeDistortion;
import net.minecraft.client.ClientClockManager;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.timeline.AttributeTrackSampler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttributeTrackSampler.class)
public abstract class AttributeTrackSamplerMixin
{
    @Shadow @Final private ClockManager clockManager;

    @ModifyExpressionValue(
            method = "applyTimeBased",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/clock/ClockInstance;totalTicks()J"))
    private long chronodynamics$shiftSkyTime(long totalTicks)
    {
        if (!(this.clockManager instanceof ClientClockManager)) return totalTicks;
        return totalTicks + TimeDistortion.offsetTicks();
    }
}

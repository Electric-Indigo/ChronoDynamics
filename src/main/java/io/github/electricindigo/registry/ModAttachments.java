package io.github.electricindigo.registry;

import io.github.electricindigo.ChronoDynamics;
import io.github.electricindigo.distortion.TemporalSickness;
import io.github.electricindigo.research.ResearchData;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public class ModAttachments
{
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ChronoDynamics.MODID);

    public static final Supplier<AttachmentType<ResearchData>> RESEARCH =
            ATTACHMENT_TYPES.register("research", () -> AttachmentType.builder(() -> ResearchData.EMPTY)
                    .serialize(ResearchData.MAP_CODEC)
                    .copyOnDeath()
                    .sync((holder, to) -> holder == to, ResearchData.STREAM_CODEC)
                    .build());

    public static final Supplier<AttachmentType<TemporalSickness>> TEMPORAL_SICKNESS =
            ATTACHMENT_TYPES.register("temporal_sickness", () -> AttachmentType.builder(() -> TemporalSickness.NONE)
                    .serialize(TemporalSickness.MAP_CODEC)
                    .sync((holder, to) -> holder == to, TemporalSickness.STREAM_CODEC)
                    .build());

    public static final Supplier<AttachmentType<Optional<BoundingBox>>> NEAREST_SCAR =
            ATTACHMENT_TYPES.register("nearest_scar", () -> AttachmentType.<Optional<BoundingBox>>builder(() -> Optional.empty())
                    .sync((holder, to) -> holder == to, ByteBufCodecs.optional(BoundingBox.STREAM_CODEC))
                    .build());

    public static void register(IEventBus modEventBus)
    {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}

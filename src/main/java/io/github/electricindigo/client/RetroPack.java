package io.github.electricindigo.client;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class RetroPack
{
    private RetroPack(){}

    public static void onAddPackFinders(AddPackFindersEvent event)
    {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

        PackLocationInfo location = new PackLocationInfo(
                "chronodynamics_retro",
                Component.literal("Chrono Dynamics retro textures"),
                PackSource.BUILT_IN,
                Optional.empty()
        );

        Pack.Metadata metadata = new Pack.Metadata(
                Component.literal("Old textures used near rift scars"),
                PackCompatibility.COMPATIBLE,
                FeatureFlagSet.of(),
                List.of(),
                true
        );

        Pack.ResourcesSupplier resources = new Pack.ResourcesSupplier() {
            @Override
            public PackMetadataResources openMetadata(PackLocationInfo packLocationInfo) {
                return new RetroPackResources(packLocationInfo);
            }

            @Override
            public Stream<PackResources> openResources(PackLocationInfo packLocationInfo, Pack.Metadata metadata) {
                return Stream.of(new RetroPackResources(packLocationInfo));
            }
        };

        Pack pack = new Pack(location, resources, metadata, new PackSelectionConfig(true, Pack.Position.TOP, false));
        event.addRepositorySource(consumer -> consumer.accept(pack));
    }
}

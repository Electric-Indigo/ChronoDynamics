package io.github.electricindigo.client;

import com.mojang.logging.LogUtils;
import io.github.electricindigo.ChronoDynamics;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

public class RetroPackResources implements PackResources
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String OUR_PREFIX = "textures/retro/";
    private static final String VANILLA_PREFIX = "textures/";

    private final PackLocationInfo location;
    private @Nullable List<PackResources> programmerArt;

    public RetroPackResources(PackLocationInfo location)
    {
        this.location = location;
    }

    private synchronized List<PackResources> programmerArt()
    {
        if (programmerArt == null)
        {
            Pack pack = Minecraft.getInstance().getResourcePackRepository().getPack("programmer_art");
            if (pack == null)
            {
                LOGGER.warn("Programmer Art pack not found; retro textures will be missing");
                programmerArt = List.of();
            }
            else
            {
                programmerArt = pack.open().toList();
            }
        }
        return programmerArt;
    }

    private static boolean isOurs(PackType type, String name, String path)
    {
        return type == PackType.CLIENT_RESOURCES
                && name.equals(ChronoDynamics.MODID)
                && path.startsWith(OUR_PREFIX);
    }

    private static String toVanillaPath(String ourPath)
    {
        return VANILLA_PREFIX + ourPath.substring(OUR_PREFIX.length());
    }

    private static String toOurPath(String vanillaPath)
    {
        return OUR_PREFIX + vanillaPath.substring(VANILLA_PREFIX.length());
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType packType, Identifier identifier) {
        if (!isOurs(packType, identifier.getNamespace(), identifier.getPath())) return null;

        Identifier vanilla = Identifier.withDefaultNamespace(toVanillaPath(identifier.getPath()));
        for (PackResources source : programmerArt())
        {
            IoSupplier<InputStream> resource = source.getResource(packType, vanilla);
            if (resource != null) return resource;
        }
        return null;
    }

    @Override
    public void listResources(PackType packType, String name, String directory, ResourceOutput resourceOutput)
    {
        if (!isOurs(packType, name, directory + "/")) return;

        String vanillaDirectory = toVanillaPath(directory + "/");
        vanillaDirectory = vanillaDirectory.substring(0, vanillaDirectory.length() - 1);

        for (PackResources source : programmerArt())
        {
            source.listResources(packType, "minecraft", vanillaDirectory, (id, resource) ->
                    resourceOutput.accept(Identifier.fromNamespaceAndPath(ChronoDynamics.MODID, toOurPath(id.getPath())), resource));
        }
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return packType == PackType.CLIENT_RESOURCES ? Set.of(ChronoDynamics.MODID) : Set.of();
    }

    @Override
    public PackLocationInfo location() {
        return location;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... strings) {
        return null;
    }

    @Override
    public <T> @Nullable T getMetadataSection(MetadataSectionType<T> metadataSectionType) throws IOException {
        return null;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public void close() {

    }
}

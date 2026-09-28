package io.github.electricindigo.research.blueprint;

import io.github.electricindigo.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class Blueprints
{
    private static final Map<String, Blueprint> ALL = new LinkedHashMap<>();

    static
    {
        add(new Blueprint("chronite_scanner",
                () -> new ItemStack(ModItems.SCANNER_ITEM.get()),
                List.of(new Blueprint.Cost(ModItems.CIRCUIT_BOARD, 1),
                        new Blueprint.Cost(ModItems.DRIFT_SENSOR, 1),
                        new Blueprint.Cost(() -> Items.COPPER_INGOT, 4),
                        new Blueprint.Cost(() -> Items.GLASS_PANE, 1)),
                "chronometry",
                () -> Items.COMPASS));

        add(new Blueprint("quartz_oscillator",
                () -> new ItemStack(ModItems.QUARTZ_OSCILLATOR.get()),
                List.of(new Blueprint.Cost(ModItems.PURIFIED_QUARTZ, 1),
                        new Blueprint.Cost(() -> Items.COPPER_INGOT, 1),
                        new Blueprint.Cost(ModItems.CONDUCTIVE_REDSTONE_PASTE, 1)),
                "chronometry",
                () -> Items.CLOCK));

        add(new Blueprint("drift_sensor",
                () -> new ItemStack(ModItems.DRIFT_SENSOR.get()),
                List.of(new Blueprint.Cost(ModItems.RESONANT_AMETHYST, 1),
                        new Blueprint.Cost(() -> Items.COPPER_INGOT, 2),
                        new Blueprint.Cost(ModItems.CONDUCTIVE_REDSTONE_PASTE, 1)),
                "chronometry",
                () -> Items.SPYGLASS));

        add(new Blueprint("circuit_board",
                () -> new ItemStack(ModItems.CIRCUIT_BOARD.get()),
                List.of(new Blueprint.Cost(ModItems.CONDUCTIVE_REDSTONE_PASTE, 2),
                        new Blueprint.Cost(ModItems.QUARTZ_OSCILLATOR, 1),
                        new Blueprint.Cost(() -> Items.SMOOTH_STONE_SLAB, 1)),
                "chronometry",
                () -> Items.REPEATER));
    }

    private Blueprints(){}

    private static void add(Blueprint blueprint)
    {
        ALL.put(blueprint.id(), blueprint);
    }

    public static Collection<Blueprint> all()
    {
        return Collections.unmodifiableCollection(ALL.values());
    }

    public static @Nullable Blueprint get(String id)
    {
        return ALL.get(id);
    }
}

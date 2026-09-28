package io.github.electricindigo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.electricindigo.network.OpenWaveformPuzzlePayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class DebugWaveformCommand
{
    private DebugWaveformCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("chrono")
                        .then(Commands.literal("debugwave")
                                .then(Commands.argument("difficulty", IntegerArgumentType.integer(1, 3))
                                        .executes(ctx -> start(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "difficulty")))))
        );
    }

    private static int start(CommandSourceStack source, int difficulty) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        long seed = System.nanoTime();

        PacketDistributor.sendToPlayer(player, new OpenWaveformPuzzlePayload(seed, difficulty));

        return 1;
    }
}

package io.github.electricindigo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.electricindigo.network.OpenCausalityPuzzlePayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.network.PacketDistributor;


public final class DebugPuzzleCommand
{
    private DebugPuzzleCommand(){}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
                Commands.literal("chrono")
                        .then(Commands.literal("debugpuzzle")
                                .then(Commands.argument("difficulty", IntegerArgumentType.integer(1, 3))
                                        .executes(ctx -> run(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "difficulty")))))
                        .executes(context -> run(context.getSource(), 1))
        );
    }

    private static int run(CommandSourceStack source, int difficulty) throws CommandSyntaxException
    {
        long seed = System.nanoTime();
        PacketDistributor.sendToPlayer(source.getPlayerOrException(), new OpenCausalityPuzzlePayload(seed, difficulty));
        return 1;
    }
}

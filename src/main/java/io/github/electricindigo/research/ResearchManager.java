package io.github.electricindigo.research;

import io.github.electricindigo.block.computerdesk.ComputerDeskMenu;
import io.github.electricindigo.block.researchdesk.ResearchDeskMenu;
import io.github.electricindigo.registry.ModAttachments;
import io.github.electricindigo.research.tree.NodeState;
import io.github.electricindigo.research.tree.ResearchNode;
import io.github.electricindigo.research.tree.ResearchTree;
import net.minecraft.server.level.ServerPlayer;

public final class ResearchManager
{
    private ResearchManager(){}

    public static void tryUnlock(ServerPlayer player, String nodeId)
    {
        if (!(player.containerMenu instanceof ComputerDeskMenu)) return;

        ResearchNode node = ResearchTree.get(nodeId);
        if (node == null) return;

        ResearchData data = player.getData(ModAttachments.RESEARCH);
        if (ResearchTree.stateOf(node, data.unlocked()) != NodeState.AVAILABLE) return;

        player.setData(ModAttachments.RESEARCH, data.with(nodeId));
    }
}

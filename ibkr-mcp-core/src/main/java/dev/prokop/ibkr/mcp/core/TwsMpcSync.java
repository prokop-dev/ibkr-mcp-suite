package dev.prokop.ibkr.mcp.core;

import dev.prokop.ibkr.mcp.core.resource.Accounts;
import dev.prokop.ibkr.mcp.core.tool.GetPositions;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class TwsMpcSync {

    public static List<McpServerFeatures.SyncResourceSpecification> resources(final TwsConnection twsConnection) {
        return List.of(new Accounts(twsConnection).specification());
    }

    public static List<McpServerFeatures.SyncToolSpecification> tools(final TwsConnection twsConnection) {
        return List.of(new GetPositions(twsConnection).specification());
    }

}

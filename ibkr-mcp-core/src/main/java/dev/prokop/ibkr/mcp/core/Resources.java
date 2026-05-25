package dev.prokop.ibkr.mcp.core;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Collection;
import java.util.List;

public class Resources {
    private final TwsConnection twsConnection;

    public Resources(TwsConnection twsConnection) {
        this.twsConnection = twsConnection;
    }

    public McpServerFeatures.SyncResourceSpecification managedAccounts() {
        McpSchema.Resource resource = McpSchema.Resource.builder()
                .uri("ibkr://accounts")
                .name("managedAccounts")
                .title("Managed Accounts")
                .description("The list of account numbers available in the current TWS session")
                .mimeType("text/plain")
                .build();

        return new McpServerFeatures.SyncResourceSpecification(
                resource, (exchange, request) -> {
            Collection<String> accounts = twsConnection.getBridge().getManagedAccounts().join();
            McpSchema.TextResourceContents textResourceContents = new McpSchema.TextResourceContents(resource.uri(), resource.mimeType(), accounts.toString());
            return new McpSchema.ReadResourceResult(List.of(textResourceContents));
        });
    }
}

package dev.prokop.ibkr.mcp.core.resource;

import dev.prokop.ibkr.mcp.core.TwsConnection;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;

public class Accounts {
    private final TwsConnection twsConnection;
    final McpSchema.Resource resource = resource();

    public Accounts(TwsConnection twsConnection) {
        this.twsConnection = twsConnection;
    }

    public McpServerFeatures.SyncResourceSpecification specification() {
        return new McpServerFeatures.SyncResourceSpecification(
                resource, (exchange, request) -> {
            // Fix: We join accounts into a single clear payload block rather than breaking them into elements
            String accountsPayload = "Active IBKR Accounts:\n" + String.join("\n", fetchRawAccounts());
            McpSchema.TextResourceContents content = McpSchema.TextResourceContents.builder(resource.uri(), accountsPayload)
                    .mimeType(resource.mimeType())
                    .build();

            return McpSchema.ReadResourceResult.builder(List.of(content)).build();
        });
    }

    private McpSchema.Resource resource() {
        return McpSchema.Resource.builder("ibkr://accounts", "accounts")
                .title("Managed Accounts")
                .description("The list of account numbers available in the current TWS session")
                .mimeType("text/plain")
                .build();
    }

    private List<String> fetchRawAccounts() {
        return twsConnection.twsApi().getAccountsList();
    }

}

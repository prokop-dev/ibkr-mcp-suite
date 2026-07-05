package dev.prokop.ibkr.mcp.core.tool;

import dev.prokop.ibkr.mcp.core.TwsConnection;
import dev.prokop.ibkr.twsapi.TwsEvent;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GetPositions {

    private final TwsConnection twsConnection;
    private final Map<String, TwsEvent.Position> positions = new ConcurrentHashMap<>();

    private final McpSchema.Tool schema = toolDefinition();

    public GetPositions(TwsConnection twsConnection) {
        this.twsConnection = twsConnection;
        setupListeners();
    }

    public McpServerFeatures.SyncToolSpecification specification() {
        return McpServerFeatures.SyncToolSpecification.builder()
                .tool(schema).callHandler((exchange, request) -> {

                    String markdownTable = formatToMarkdownTable(positions.values());
                    McpSchema.TextContent responseContent = McpSchema.TextContent.builder(markdownTable).build();
                    return McpSchema.CallToolResult.builder()
                            .content(List.of(responseContent))
                            .isError(false)
                            .build();
                })
                .build();
    }

    private McpSchema.Tool toolDefinition() {
        // Even for parameterless tools, the JSON schema definition should be explicit
        Map<String, Object> inputSchema = Map.of(
                "type", "object",
                "properties", Map.of()
        );

        return McpSchema.Tool.builder("get_portfolio_positions", inputSchema)
                .title("Retrieves a real-time table of open investment positions.")
                .description("Returns a real-time Markdown table listing open portfolio positions, cost basis, and current quantities. Use this whenever you need to check asset allocations before crafting order modifications.")
                .build();
    }

    private void setupListeners() {
        twsConnection.twsApi().on(TwsEvent.Position.class, position -> {
            // Uniquely identify a position by Account ID and Contract ID
            final var key = position.account() + ":" + position.contract().conid();

            if (position.pos().isZero()) {
                positions.remove(key);
            } else {
                positions.put(key, position);
            }
        });
        twsConnection.twsApi().reqPositions();
    }

    private String formatToMarkdownTable(Collection<TwsEvent.Position> positions) {
        if (positions.isEmpty()) {
            return "No open positions found in the active portfolio.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("| Ticker | SecType | Quantity | Avg Cost | Total Cost |\n");
        sb.append("| :--- | :--- | :--- | :--- | :--- |\n");
        for (TwsEvent.Position p : positions) {
            sb.append(String.format("| %s | %s | %.2f | %.4f | %.2f |\n",
                    p.contract().symbol(), p.contract().getSecType(), p.pos().value().doubleValue(), p.avgCost(), p.pos().value().doubleValue() * p.avgCost()));
        }
        return sb.toString();
    }

}

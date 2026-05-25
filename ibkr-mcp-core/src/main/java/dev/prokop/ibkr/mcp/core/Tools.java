package dev.prokop.ibkr.mcp.core;

import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.List;

public class Tools {
    private final TwsConnection twsConnection;

    public Tools(TwsConnection twsConnection) {
        this.twsConnection = twsConnection;
    }

    /**
     * Data record representing a portfolio position.
     */
    public record Position(
            String account,
            String symbol,
            double position,
            double marketPrice,
            double marketValue,
            double averageCost
    ) {}

    public McpServerFeatures.SyncToolSpecification getPositions() {
        McpSchema.Tool tool = McpSchema.Tool.builder()
                .name("get_positions")
                .description("Get current portfolio positions from Interactive Brokers. Optionally filter by account ID.")
                .inputSchema(McpJsonDefaults.getMapper(), """
                        {
                          "type": "object",
                          "properties": {
                            "accountId": {
                              "type": "string",
                              "description": "Optional account ID to filter positions"
                            }
                          }
                        }
                        """)
                .build();

        return McpServerFeatures.SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {
                    String accountId = (String) request.arguments().get("accountId");
                    
                    // In a real implementation, you would use:
                    var positions = twsConnection.getBridge().getPositions().join();
                    
                    List<Position> mockPositions = List.of(
                        new Position(accountId != null ? accountId : "DU12345", "AAPL", 100, 225.40, 22540.0, 185.20)
                    );

                    try {
                        String json = McpJsonDefaults.getMapper().writeValueAsString(positions);
                        return McpSchema.CallToolResult.builder()
                                .content(List.of(new McpSchema.TextContent(json)))
                                .build();
                    } catch (Exception e) {
                        return McpSchema.CallToolResult.builder()
                                .isError(true)
                                .content(List.of(new McpSchema.TextContent("Failed to serialize positions: " + e.getMessage())))
                                .build();
                    }
                })
                .build();
    }
}

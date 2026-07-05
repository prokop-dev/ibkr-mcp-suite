package dev.prokop.ibkr.mcp.cli;


import dev.prokop.ibkr.mcp.core.TwsMpcSync;
import dev.prokop.ibkr.mcp.core.TwsConnection;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpServerTransportProvider;

import java.time.Duration;

/**
 * A simple "Hello World" MCP server that uses the STDIO transport.
 * It exposes a single tool 'hello' that takes a 'name' argument.
 */
public class IbkrMcpServer {

    private static TwsConnection twsConnection = new TwsConnection();

    public static void main(String[] args) throws Exception {
        twsConnection.start();

        McpSyncServer server = server();
        TwsMpcSync.resources(twsConnection).forEach(server::addResource);
        TwsMpcSync.tools(twsConnection).forEach(server::addTool);
    }

    private static McpSyncServer server() {
        return McpServer.sync(transportProvider())
                .serverInfo("IBKR MCP Server", "1.0.0")
                .requestTimeout(Duration.ofSeconds(30))
                .instructions("Interactive Brokers MCP Server")
                .capabilities(serverCapabilities())
                .build();
    }

    private static McpServerTransportProvider transportProvider() {
        return new StdioServerTransportProvider(McpJsonDefaults.getMapper());
    }

    private static McpSchema.ServerCapabilities serverCapabilities() {
        return McpSchema.ServerCapabilities.builder()
                .resources(true, true)
                //.prompts(true)
                .tools(true)
                .build();
    }

}

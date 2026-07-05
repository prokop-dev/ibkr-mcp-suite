package dev.prokop.ibkr.mcp.cli;


import dev.prokop.ibkr.mcp.core.Resources;
import dev.prokop.ibkr.mcp.core.Tools;
import dev.prokop.ibkr.mcp.core.TwsConnection;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import java.time.Duration;

/**
 * A simple "Hello World" MCP server that uses the STDIO transport.
 * It exposes a single tool 'hello' that takes a 'name' argument.
 */
public class HelloWorldServer {

    public static McpSyncServer server() {
        final McpSchema.ServerCapabilities serverCapabilities =
                McpSchema.ServerCapabilities.builder()
                        .resources(true, true)
                        //.prompts(true)
                        .tools(true)
                        .build();

        return McpServer.sync(new StdioServerTransportProvider(McpJsonDefaults.getMapper()))
                .serverInfo("IBKR MCP Server", "1.0.0")
                .requestTimeout(Duration.ofSeconds(30))
                .instructions("Interactive Brokers MCP Server")
                .capabilities(serverCapabilities)
                .build();
    }

    public static void main(String[] args) throws Exception {
        McpSyncServer server = server();
        
        System.err.println("Connecting to IBKR...");
        TwsConnection twsConnection = new TwsConnection();
        Tools tools = new Tools(twsConnection);
        Resources resources = new Resources(twsConnection);

        server.addTool(tools.getPositions());
        server.addResource(resources.managedAccounts());

        System.err.println("IBKR MCP Server starting on STDIO...");
        twsConnection.getBridge().getManagedAccounts().thenAccept(accounts -> {
            System.err.println("Managed accounts: " + accounts);
        });
        
        // Keep the server alive
        while (true) {
            Thread.sleep(1000);
        }
    }

}

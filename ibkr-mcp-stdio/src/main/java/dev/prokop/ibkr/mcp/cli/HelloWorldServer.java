package dev.prokop.ibkr.mcp.cli;


import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import java.time.Duration;
import java.util.List;

/**
 * A simple "Hello World" MCP server that uses the STDIO transport.
 * It exposes a single tool 'hello' that takes a 'name' argument.
 */
public class HelloWorldServer {

    public static McpSyncServer server() {
        final McpSchema.ServerCapabilities serverCapabilities =
                McpSchema.ServerCapabilities.builder()
                        //.resources(true, true)
                        //.prompts(true)
                        .tools(true)
                        .build();

        return McpServer.sync(new StdioServerTransportProvider(McpJsonDefaults.getMapper()))
                .serverInfo("Hello World Server", "1.0.0")
                .requestTimeout(Duration.ofSeconds(30))
                .instructions("Filesystem MCP Server")
                .capabilities(serverCapabilities)
                .build();

    }

    public static McpServerFeatures.SyncToolSpecification tool1() {
        McpSchema.Tool tool =
                McpSchema.Tool.builder()
                        .name("delete")
                        .title("file/dir delete")
                        .description("Delete a file or dir from the filesystem.")
                        .inputSchema(McpJsonDefaults.getMapper(), schema1)
                        .build();

        return McpServerFeatures.SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((e, r) -> {
                    return McpSchema.CallToolResult.builder()
                            .content(List.of(new McpSchema.TextContent("Hi")))
                            .build();
                })
                .build();
    }

    public static void main(String[] args) throws Exception {
        McpSyncServer server = server();
        server.addTool(tool1());
        Thread.sleep(5000);
        server.close();
//
//
//
//
//
//
//        // 3. Register the 'hello' tool handler
//        server.addTool(helloTool, (arguments) -> {
//            String name = (String) arguments.get("name");
//            if (name == null) name = "World";
//            return new McpSchema.CallToolResult(
//                    List.of(new McpSchema.TextContent("Hello, " + name + "!")),
//                    false
//            );
//        });
//
//        // 4. Start the server
//        // This blocks until the STDIO stream is closed (e.g., when the parent process exits).
//        System.err.println("Hello World MCP Server starting on STDIO...");
//        server.start();
    }

    public static final String schema1 = """
            {
              "$schema": "http://json-schema.org/draft-07/schema#",
              "type": "object",
              "properties": {
                "start": {
                  "type": "string",
                  "description": "The starting path to search, required."
                },
                "name": {
                  "type": "string",
                  "description": "The name of the target file or dir to search, fuzzy matching supported, required."
                }
              },
              "required": [
                "start",
                "name"
              ]
            }
            """.trim();

}

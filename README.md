# IBKR MCP Suite

Interactive Brokers (IBKR) integration for the Model Context Protocol (MCP).

This MCP server is based on the [TWS API Wrapper](https://github.com/prokop-dev/ibkr-tws-api-wrapper).

## Projects

- `ibkr-mcp-core`: Core library for IBKR connectivity and logic.
- `ibkr-mcp-stdio`: MCP server implementation using Standard Input/Output (stdio).
- `mcp-server-quarkus`: Experimental Quarkus-based MCP server.

## Building and Running the Server

To build an executable "uber" JAR containing all dependencies, use the `uberjar` profile:

### 1. Build the uber JAR
```bash
mvn clean package -Puberjar
```

### 2. Run the server
```bash
java -jar ibkr-mcp-stdio/target/ibkr-mcp-stdio-1.0.0-SNAPSHOT.jar
```

### 3. Testing server
```bash
npx @modelcontextprotocol/inspector java -jar /Users/bart/.m2/repository/dev/prokop/ibkr/ibkr-mcp-stdio/1.0.0-SNAPSHOT/ibkr-mcp-stdio-1.0.0-SNAPSHOT.jar
```

## Adding to Gemini MCP (Local Configuration)

To use this server with an MCP-compatible client (like Gemini or Claude), add it to your local configuration.

### Option A: Using Gemini CLI (Recommended)

Run the following command to add the server automatically. Using absolute paths is required:

```bash
gemini mcp add ibkr java -jar /Users/bart/code/ibkr-mcp-suite/ibkr-mcp-stdio/target/ibkr-mcp-stdio-1.0.0-SNAPSHOT.jar
```

### Option B: Manual Configuration Template

Add the following entry to your `mcp_config.json` (the location depends on your OS and client, e.g., `~/Library/Application Support/Google/Chrome/Default/mcp_config.json` or similar):

```json
{
  "mcpServers": {
    "ibkr": {
      "command": "java",
      "args": [
        "-jar",
        "/ABSOLUTE/PATH/TO/ibkr-mcp-suite/ibkr-mcp-stdio/target/ibkr-mcp-stdio-1.0.0-SNAPSHOT.jar"
      ]
    }
  }
}
```

**Note:** Replace `/ABSOLUTE/PATH/TO/` with the actual absolute path to your project directory.

## Features

- **Resources:**
  - `managed-accounts`: List of managed accounts.
- **Tools:**
  - `get-positions`: Retrieve current portfolio positions.

## Prerequisites

- Java 17 or higher.
- A running instance of IBKR TWS or Gateway with API access enabled.

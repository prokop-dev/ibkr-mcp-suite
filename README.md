# 📈 IBKR MCP Suite

[![MCP Version](https://img.shields.io/badge/MCP-1.0.0-blue.svg)](https://modelcontextprotocol.io/)
[![Java Support](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](#)

Interactive Brokers (IBKR) integration for the **Model Context Protocol (MCP)**. This suite enables Large Language Models (LLMs) to securely interact with your local Interactive Brokers Gateway or Trader Workstation (TWS) to query live accounts and retrieve portfolio positions.

This server is built on top of the [TWS API Wrapper](https://github.com/prokop-dev/ibkr-tws-api-wrapper) and operates as a local **STDIO-based MCP server** for maximum security, keeping your credentials, session, and portfolio data entirely on your local machine.

---

## 💡 Example Usecase: Portfolio Analysis & Rebalancing

Here is an example of how an MCP-enabled AI assistant (such as Gemini or Claude) interacts with this server to fetch real-time portfolio data and generate a comprehensive asset allocation analysis and rebalancing recommendation.

### 1. The LLM invokes the `get_portfolio_positions` tool
The assistant queries the server via Standard Input/Output and receives the current portfolio holdings:

| Ticker | SecType | Quantity | Avg Cost | Total Cost |
| :--- | :--- | :--- | :--- | :--- |
| **SPY** | STK | 120.00 | $510.45 | $61,254.00 |
| **QQQ** | STK | 45.00 | $445.20 | $20,034.00 |
| **MSFT** | STK | 30.00 | $420.15 | $12,604.50 |
| **TLT** | STK | 150.00 | $92.50 | $13,875.00 |
| **GLD** | STK | 40.00 | $215.30 | $8,612.00 |
| **AAPL** | STK | 25.00 | $185.50 | $4,637.50 |

*(Note: Data shown is for demonstration purposes only).*

### 2. AI Assistant Analysis and Recommendation
Based on the retrieved positions, the AI assistant generates the following response:

> ### 📊 Portfolio Health & Rebalancing Analysis
> 
> **Total Portfolio Value**: $121,017.00
> 
> #### **1. Asset Allocation Breakdown**
> - 🇺🇸 **Core Equities (S&P 500)**: **50.6%** (`SPY`)
> - 💻 **Megacap Tech / Growth**: **30.8%** (`QQQ`: 16.6%, `MSFT`: 10.4%, `AAPL`: 3.8%)
> - 🛡️ **Fixed Income (Bonds)**: **11.5%** (`TLT`)
> - 🟡 **Alternatives (Gold)**: **7.1%** (`GLD`)
> 
> #### **2. Key Insights**
> * **Tech Sector Concentration**: Your portfolio has high concentration in Megacap Tech (over 30%). While this sector has strong momentum, it introduces substantial sector-specific drawdown risk.
> * **Fixed Income Underweight**: Your bond exposure (`TLT`) stands at 11.5%, which is slightly lower than a standard 60/40 or balanced growth profile (15-20% bonds).
> 
> #### **3. Actionable Rebalancing Plan**
> To realign the portfolio to a more resilient, balanced allocation:
> 
> | Action | Ticker | Quantity | Estimated Price | Proceeds / Cost | Resulting Allocation |
> | :--- | :--- | :--- | :--- | :--- | :--- |
> | 🔴 **Trim** | `MSFT` | -5 | $420.15 | +$2,100.75 | Reduces Tech exposure |
> | 🔴 **Trim** | `SPY` | -20 | $510.45 | +$10,209.00 | Reduces Large Cap exposure |
> | 🟢 **Buy** | `TLT` | +130 | $92.50 | -$12,025.00 | Increases Bond allocation to **21.4%** |
> 
> *Remaining cash of **$284.75** can be kept in reserves or swept into short-term cash instruments.*

---

## 🛠️ MCP Features

### 1. Resources
Resources expose read-only data sources to the LLM.
- **`ibkr://accounts`** (Managed Accounts):
  Exposes the list of account IDs available in the current TWS/Gateway session.

### 2. Tools
Tools allow the LLM to perform actions or fetch dynamic real-time data.
- **`get_portfolio_positions`**:
  Retrieves a real-time markdown table listing open portfolio positions, cost basis, quantities, and total valuation.

---

## ⚙️ Architecture

```
┌──────────────────┐               ┌─────────────────────┐               ┌─────────────────┐
│   MCP Client     │  STDIO (JSON) │  IBKR MCP Server    │   TWS Socket  │  IBKR Gateway   │
│  (Gemini/Claude/ │ <───────────> │  (Java Executable)  │ <───────────> │     or TWS      │
│  Cursor Desktop) │               │   ibkr-mcp-stdio    │               │  (Local Port)   │
└──────────────────┘               └─────────────────────┘               └─────────────────┘
```

The MCP Server communicates with the local IBKR Gateway via socket loopback, ensuring that **no credentials, sensitive session tokens, or trade details are transmitted to external servers**.

---

## 📋 Prerequisites

1. **Java JDK 17 or higher** installed and available on your system path.
2. **Maven 3.8+** (for building from source).
3. **Interactive Brokers Trader Workstation (TWS)** or **IBKR Gateway** running locally.
4. **API Socket Access** enabled in your TWS or Gateway configuration:
   - Go to **Configuration** -> **API** -> **Settings**.
   - Enable **"Enable ActiveX and Socket Clients"**.
   - Check the **"API Port"** configured (TWS defaults to `7496`/`7497`, Gateway defaults to `4001`/`4002`).
   - Add `127.0.0.1` to **"Trusted IPs"** if prompted.

---

## 🚀 Getting Started

### 1. Build the Server Package
Compile the codebase and bundle all dependencies into an executable "uber" JAR:
```bash
mvn clean package -Puberjar
```

### 2. Test Connection Locally
Ensure your IBKR Gateway or TWS is running, then launch the JAR directly:
```bash
java -jar ibkr-mcp-stdio/target/ibkr-mcp-stdio-1.0.0-SNAPSHOT.jar
```
*Note: Since the server uses STDIO to communicate with MCP clients, running it directly in your terminal will open a waiting state expecting JSON-RPC input. To exit, press `Ctrl + C`.*

---

## 🔌 Client Integration

### Option A: Using the Gemini / Antigravity CLI (Recommended)
Add the server to your local configuration using the CLI by specifying the absolute path to the generated JAR:
```bash
gemini mcp add ibkr java -jar /ABSOLUTE/PATH/TO/ibkr-mcp-suite/ibkr-mcp-stdio/target/ibkr-mcp-stdio-1.0.0-SNAPSHOT.jar
```

### Option B: Claude Desktop Configuration
Add the server manually by editing your `claude_desktop_config.json` (typically located in `~/Library/Application Support/Claude/claude_desktop_config.json` on macOS or `%APPDATA%\Claude\claude_desktop_config.json` on Windows):

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
> [!IMPORTANT]
> Make sure to replace `/ABSOLUTE/PATH/TO/` with the actual absolute path to this project on your machine.

---

## 🔍 Debugging & Inspection

You can test and inspect the MCP protocol capabilities (listing tools, checking resources) interactively using the official MCP Inspector tool:

```bash
npx @modelcontextprotocol/inspector java -jar ibkr-mcp-stdio/target/ibkr-mcp-stdio-1.0.0-SNAPSHOT.jar
```

This starts a local web interface (typically at `http://localhost:5173`) where you can trigger `get_portfolio_positions` and inspect JSON payloads.

---

## 📂 Project Structure

- [`ibkr-mcp-core`](./ibkr-mcp-core): Core library containing TWS socket integration, tools like [`GetPositions.java`](./ibkr-mcp-core/src/main/java/dev/prokop/ibkr/mcp/core/tool/GetPositions.java), and resources like [`Accounts.java`](./ibkr-mcp-core/src/main/java/dev/prokop/ibkr/mcp/core/resource/Accounts.java).
- [`ibkr-mcp-stdio`](./ibkr-mcp-stdio): The main executable package handling standard input/output mapping for MCP clients via [`IbkrMcpServer.java`](./ibkr-mcp-stdio/src/main/java/dev/prokop/ibkr/mcp/cli/IbkrMcpServer.java).
- [`pom.xml`](./pom.xml): Root Maven project configuration.

---

## ⚖️ License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

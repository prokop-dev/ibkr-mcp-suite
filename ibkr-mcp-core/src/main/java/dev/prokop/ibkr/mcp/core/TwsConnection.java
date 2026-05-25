package dev.prokop.ibkr.mcp.core;

import dev.prokop.ibkr.twsapi.TwsApi;
import dev.prokop.ibkr.twsapi.TwsSyncBridge;

public class TwsConnection {
    private final TwsSyncBridge bridge;

    public TwsConnection() {
        final var twsApi = new TwsApi();
        twsApi.connect("127.0.0.1");
        this.bridge = new TwsSyncBridge(twsApi);
    }

    public TwsSyncBridge getBridge() {
        return bridge;
    }
}

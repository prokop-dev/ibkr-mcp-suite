package dev.prokop.ibkr.mcp.core;

import dev.prokop.ibkr.twsapi.TwsApi;

public class TwsConnection {
    private final TwsApi twsApi;

    public TwsConnection() {
        this.twsApi = new TwsApi();
    }

    public void start() {
        twsApi.connect("127.0.0.1").join();
    }

    public final TwsApi twsApi() {
        return twsApi;
    }
}

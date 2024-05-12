package io.contek.invoker.deribit.api.websocket.common;

public class WebSocketHeartbeat extends WebSocketInboundMessage {

    // {
    //  "params": {
    //    "type": "test_request"
    //  },
    //  "method": "heartbeat",
    //  "jsonrpc": "2.0"
    //}

    public String jsonrpc;
    public String method; // "heartbeat"
    public Params params;

    public static final class Params {
        public String type;
    }
}

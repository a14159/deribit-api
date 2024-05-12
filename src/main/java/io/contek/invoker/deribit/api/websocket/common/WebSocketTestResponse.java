package io.contek.invoker.deribit.api.websocket.common;

public class WebSocketTestResponse extends WebSocketResponse <WebSocketTestResponse.Result>{

    // {
    //  "jsonrpc": "2.0",
    //  "id": 8212,
    //  "result": {
    //    "version": "1.2.26"
    //  }
    //}
    public static final class Result {
        public String version;
    }
}

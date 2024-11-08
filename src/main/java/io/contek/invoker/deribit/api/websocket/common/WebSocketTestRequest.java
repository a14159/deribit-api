package io.contek.invoker.deribit.api.websocket.common;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;

public class WebSocketTestRequest extends AnyWebSocketMessage {
    public String jsonrpc = "2.0";
    public int id;
    public String method = "public/test";
}

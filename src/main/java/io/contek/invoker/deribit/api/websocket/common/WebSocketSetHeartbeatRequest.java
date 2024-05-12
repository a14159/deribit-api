package io.contek.invoker.deribit.api.websocket.common;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;

public class WebSocketSetHeartbeatRequest extends AnyWebSocketMessage {
    public String jsonrpc = "2.0";
    public Integer id;
    public String method = "public/set_heartbeat";
    public Integer interval;
}

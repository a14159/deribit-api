package io.contek.invoker.deribit.api.websocket.common;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;

public class WebSocketCancelOrderRequest extends AnyWebSocketMessage {

  public String jsonrpc = "2.0";
  public Integer id;
  public String method = "private/cancel_by_label";
  public String label;
}

package io.contek.invoker.deribit.api.websocket.common;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;

public class WebSocketCancelAllOrdersRequest extends AnyWebSocketMessage {

  public String jsonrpc = "2.0";
  public Integer id;
  public String method = "private/cancel_all_by_instrument";
  public String instrument_name;
}

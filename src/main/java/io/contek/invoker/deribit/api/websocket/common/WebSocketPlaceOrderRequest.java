package io.contek.invoker.deribit.api.websocket.common;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;

import java.math.BigDecimal;

public class WebSocketPlaceOrderRequest extends AnyWebSocketMessage {

  public String jsonrpc = "2.0";
  public Integer id;
  public String method = "private/"; // + Side.toAction()
  public String instrument_name;
  public BigDecimal amount;
  public BigDecimal contracts;
  public String type;
  public String label;
  public BigDecimal price;
  public Boolean post_only;
  public Boolean reduce_only;
}

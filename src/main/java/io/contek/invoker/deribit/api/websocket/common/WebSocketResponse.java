package io.contek.invoker.deribit.api.websocket.common;

import io.contek.invoker.deribit.api.common._Error;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class WebSocketResponse<R> extends WebSocketInboundMessage {

  public String jsonrpc;
  public Integer id;
  public R result;
  public _Error error;
  public Long usIn;
  public Long usOut;
  public Long usDiff;
}

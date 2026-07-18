package io.contek.invoker.deribit.api.websocket.common;

import io.contek.invoker.deribit.api.common._Error;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class WebSocketResponse<R> extends WebSocketInboundMessage {

  public String jsonrpc;
  public int id = -1;
  public R result;
  public _Error error;
  public long usIn = -1L;
  public long usOut = -1L;
  public long usDiff = -1L;
}

package io.contek.invoker.deribit.api.websocket;

import io.contek.invoker.deribit.api.websocket.common.WebSocketResponse;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class WebSocketRequestIdGenerator {

  private final WebSocketMessageParser parser;

  public WebSocketRequestIdGenerator(WebSocketMessageParser parser) {
    this.parser = parser;
  }

  public int getNextRequestId(Class<? extends WebSocketResponse<?>> type) {
    return parser.register(type);
  }
}

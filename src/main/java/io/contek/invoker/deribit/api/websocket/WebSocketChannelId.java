package io.contek.invoker.deribit.api.websocket;

import io.contek.invoker.commons.websocket.BaseWebSocketChannelId;
import io.contek.invoker.deribit.api.websocket.common.WebSocketSingleChannelMessage;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class WebSocketChannelId<Message extends WebSocketSingleChannelMessage<?>>
    extends BaseWebSocketChannelId<Message> {

  protected WebSocketChannelId(String channel) {
    super(channel);
  }

  @Override
  public final boolean accepts(Message message) {
    return getValue().equals(message.params.channel);
  }
}

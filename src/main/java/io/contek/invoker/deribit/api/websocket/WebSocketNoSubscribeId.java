package io.contek.invoker.deribit.api.websocket;

import io.contek.invoker.commons.websocket.BaseWebSocketChannelId;
import io.contek.invoker.deribit.api.websocket.common.WebSocketResponse;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class WebSocketNoSubscribeId<Message extends WebSocketResponse<?>>
    extends BaseWebSocketChannelId<Message> {

  protected WebSocketNoSubscribeId(String channel) {
    super(channel);
  }

  public final String getChannel() {
    return getValue();
  }

  @Override
  public final boolean accepts(Message message) {
    return true;
  }
}

package io.contek.invoker.deribit.api.websocket.user;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;
import io.contek.invoker.commons.websocket.BaseWebSocketChannel;
import io.contek.invoker.commons.websocket.SubscriptionState;
import io.contek.invoker.commons.websocket.WebSocketSession;
import io.contek.invoker.deribit.api.websocket.WebSocketNoSubscribeId;
import io.contek.invoker.deribit.api.websocket.WebSocketRequestIdGenerator;
import io.contek.invoker.deribit.api.websocket.common.WebSocketResponse;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.ThreadSafe;

import static io.contek.invoker.commons.websocket.SubscriptionState.SUBSCRIBED;
import static io.contek.invoker.commons.websocket.SubscriptionState.UNSUBSCRIBED;

@ThreadSafe
public abstract class UserWebSocketNoSubscribeChannel<Message extends WebSocketResponse<Data>, Data>
    extends BaseWebSocketChannel<WebSocketNoSubscribeId<Message>, Message, Data> {

  protected WebSocketRequestIdGenerator idGenerator;
  protected volatile WebSocketSession session;
  private SubscriptionState state = UNSUBSCRIBED;

  UserWebSocketNoSubscribeChannel(
      WebSocketNoSubscribeId<Message> id, WebSocketRequestIdGenerator requestIdGenerator) {
    super(id);
    this.idGenerator = requestIdGenerator;
  }

  @Override
  protected Data getData(Message message) {
      return message.result;
  }

  @Override
  protected SubscriptionState subscribe(WebSocketSession webSocketSession) {
    this.state = SUBSCRIBED;
    this.session = webSocketSession;
    return SUBSCRIBED;
  }

  @Override
  protected SubscriptionState unsubscribe(WebSocketSession webSocketSession) {
    this.state = UNSUBSCRIBED;
    this.session = null;
    return UNSUBSCRIBED;
  }

  @Nullable
  @Override
  protected SubscriptionState getState(AnyWebSocketMessage anyWebSocketMessage) {
    return state;
  }

  @Override
  protected void reset() {
    state = UNSUBSCRIBED;
    session = null;
  }
}

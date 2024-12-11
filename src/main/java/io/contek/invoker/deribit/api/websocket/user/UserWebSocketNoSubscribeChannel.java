package io.contek.invoker.deribit.api.websocket.user;

import io.contek.invoker.commons.websocket.BaseWebSocketChannel;
import io.contek.invoker.commons.websocket.SubscriptionState;
import io.contek.invoker.commons.websocket.WebSocketSession;
import io.contek.invoker.deribit.api.websocket.WebSocketNoSubscribeId;
import io.contek.invoker.deribit.api.websocket.WebSocketRequestIdGenerator;
import io.contek.invoker.deribit.api.websocket.common.WebSocketResponse;
import io.contek.invoker.deribit.api.websocket.common.WebSocketTestRequest;
import io.contek.invoker.deribit.api.websocket.common.WebSocketTestResponse;

import javax.annotation.concurrent.ThreadSafe;

import static io.contek.invoker.commons.websocket.SubscriptionState.SUBSCRIBING;
import static io.contek.invoker.commons.websocket.SubscriptionState.UNSUBSCRIBING;

@ThreadSafe
public abstract class UserWebSocketNoSubscribeChannel<Message extends WebSocketResponse<Data>, Data>
    extends BaseWebSocketChannel<WebSocketNoSubscribeId<Message>, Message, Data> {

  protected final WebSocketRequestIdGenerator idGenerator;
  protected volatile WebSocketSession session;

  protected volatile SubscriptionState lastStatusSent;

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
    this.session = webSocketSession;
    // send test request to move to subscribed state faster
    WebSocketTestRequest request = new WebSocketTestRequest();
    request.id = idGenerator.getNextRequestId(WebSocketTestResponse.class);
    session.send(request);
    lastStatusSent = SUBSCRIBING;
    return SUBSCRIBING;
  }

  @Override
  protected SubscriptionState unsubscribe(WebSocketSession webSocketSession) {
    lastStatusSent = UNSUBSCRIBING;
    return UNSUBSCRIBING;
  }

  @Override
  protected void reset() {
    session = null;
  }

  @Override
  public String toString() {
    return getId().toString();
  }
}

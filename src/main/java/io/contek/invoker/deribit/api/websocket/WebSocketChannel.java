package io.contek.invoker.deribit.api.websocket;

import io.contek.invoker.commons.websocket.*;
import io.contek.invoker.deribit.api.websocket.common.SubscriptionParams;
import io.contek.invoker.deribit.api.websocket.common.WebSocketRequest;
import io.contek.invoker.deribit.api.websocket.common.WebSocketSingleChannelMessage;
import io.contek.invoker.deribit.api.websocket.common.WebSocketSubscriptionConfirmation;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

import static io.contek.invoker.commons.websocket.SubscriptionState.*;
import static io.contek.invoker.deribit.api.websocket.common.constants.WebSocketOutboundKeys._subscribe;
import static io.contek.invoker.deribit.api.websocket.common.constants.WebSocketOutboundKeys._unsubscribe;

@ThreadSafe
public abstract class WebSocketChannel<Message extends WebSocketSingleChannelMessage<Data>, Data>
    extends BaseWebSocketChannel<WebSocketChannelId<Message>, Message, Data> {

  private final String scope;
  private final WebSocketRequestIdGenerator requestIdGenerator;

  private final Object pendingRequestLock = new Object();
  @GuardedBy("pendingRequestLock")
  private WebSocketRequest<SubscriptionParams> pendingRequest;

  protected WebSocketChannel(WebSocketChannelId<Message> id, String scope, WebSocketRequestIdGenerator requestIdGenerator) {
    super(id);
    this.scope = scope;
    this.requestIdGenerator = requestIdGenerator;
  }

  @Override
  protected final Data getData(Message message) {
    return message.params.data;
  }

  @Override
  protected final SubscriptionState subscribe(WebSocketSession session) {
    return sendSubscriptionRequest(session, getSubscribeMethod(), SUBSCRIBING);
  }

  @Override
  protected final SubscriptionState unsubscribe(WebSocketSession session) {
    return sendSubscriptionRequest(session, getUnsubscribeMethod(), UNSUBSCRIBING);
  }

  private SubscriptionState sendSubscriptionRequest(
      WebSocketSession session, String method, SubscriptionState pendingState) {
    synchronized (pendingRequestLock) {
      if (pendingRequest != null) {
        throw new IllegalStateException();
      }

      WebSocketChannelId<Message> id = getId();
      SubscriptionParams params = new SubscriptionParams();
      params.channels = List.of(id.getValue());

      WebSocketRequest<SubscriptionParams> request = new WebSocketRequest<>();
      request.id = requestIdGenerator.getNextRequestId(WebSocketSubscriptionConfirmation.class);
      request.method = method;
      request.params = params;
      session.send(request);
      pendingRequest = request;
    }

    return pendingState;
  }

  @Nullable
  @Override
  protected final SubscriptionState getState(AnyWebSocketMessage message) {
    if (!(message instanceof WebSocketSubscriptionConfirmation confirmation)) {
      return null;
    }

    synchronized (pendingRequestLock) {
      WebSocketRequest<SubscriptionParams> command = pendingRequest;
      if (command == null) {
        return null;
      }

      if (command.id == null || confirmation.id != command.id) {
        return null;
      }

      if (confirmation.error != null) {
        throw new WebSocketIllegalMessageException(confirmation.error.code + ": " + confirmation.error.message);
      }

      if (confirmation.result == null || confirmation.result.isEmpty()) {
        return null;
      }

      reset();
      if (command.method.equals(getSubscribeMethod())) {
        return SUBSCRIBED;
      }
      if (command.method.equals(getUnsubscribeMethod())) {
        return UNSUBSCRIBED;
      }
      throw new IllegalStateException();
    }
  }

  @Override
  protected final void reset() {
    synchronized (pendingRequestLock) {
      pendingRequest = null;
    }
  }

  private String getSubscribeMethod() {
    return scope + '/' + _subscribe;
  }

  private String getUnsubscribeMethod() {
    return scope + '/' + _unsubscribe;
  }
}

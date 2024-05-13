package io.contek.invoker.deribit.api.websocket.user;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;
import io.contek.invoker.commons.websocket.SubscriptionState;
import io.contek.invoker.deribit.api.common.constants.OrderTypeKeys;
import io.contek.invoker.deribit.api.websocket.WebSocketNoSubscribeId;
import io.contek.invoker.deribit.api.websocket.WebSocketRequestIdGenerator;
import io.contek.invoker.deribit.api.websocket.common.*;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.math.BigDecimal;


@ThreadSafe
public final class UserOrdersEditChannel extends UserWebSocketNoSubscribeChannel<UserOrdersEditChannel.Message, String> {

  private volatile WebSocketResponseListener<String> listener;

  UserOrdersEditChannel(Id id, WebSocketRequestIdGenerator requestIdGenerator) {
    super(id, requestIdGenerator);
  }

  public void addRawMessageListener(WebSocketResponseListener<String> listener) {
    this.listener = listener;
  }

  @Override
  public Class<Message> getMessageType() {
    return UserOrdersEditChannel.Message.class;
  }

  @Immutable
  public static final class Id extends WebSocketNoSubscribeId<Message> {
    public Id(String value) {
      super(value);
    }
  }

  public int placeLimitOrder(String market, String clientId, String side, BigDecimal price, BigDecimal qty) {
    WebSocketPlaceOrderRequest request = new WebSocketPlaceOrderRequest();
    request.id = idGenerator.getNextRequestId(Message.class);
    request.instrument_name = market;
    request.type = OrderTypeKeys._limit;
    request.label = clientId;
    request.method = request.method + side;
    request.price = price;
    request.amount = qty;

    if (session != null) {
      session.send(request);
      return request.id;
    }
    return -1;
  }

  public int placeMarketOrder(String market, String clientId, String side, BigDecimal qty) {
    WebSocketPlaceOrderRequest request = new WebSocketPlaceOrderRequest();
    request.id = idGenerator.getNextRequestId(Message.class);
    request.instrument_name = market;
    request.type = OrderTypeKeys._market;
    request.label = clientId;
    request.method = request.method + side;
    request.amount = qty;
    if (session != null) {
      session.send(request);
      return request.id;
    }
    return -1;
  }

  public int cancelOrder(String market, String clientId) {
    WebSocketCancelOrderRequest request = new WebSocketCancelOrderRequest();
    request.label = clientId;
    request.id = idGenerator.getNextRequestId(Message.class);

    if (session != null) {
      session.send(request);
      return request.id;
    }
    return -1;
  }

  public int cancelAllOrders(String market) {
    WebSocketCancelAllOrdersRequest request = new WebSocketCancelAllOrdersRequest();
    request.instrument_name = market;
    request.id = idGenerator.getNextRequestId(Message.class);

    if (session != null) {
      session.send(request);
      return request.id;
    }
    return -1;
  }

  @Nullable
  @Override
  protected SubscriptionState getState(AnyWebSocketMessage anyWebSocketMessage) {
    if (listener != null && anyWebSocketMessage instanceof Message msg) {
      listener.onNextRawMessage(msg);
    }
    return null;
  }

  @NotThreadSafe
  public static final class Message extends WebSocketResponse<String> {}
}

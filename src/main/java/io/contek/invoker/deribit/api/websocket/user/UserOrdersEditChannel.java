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
    PlaceOrderParams params = new PlaceOrderParams();
    params.instrument_name = market;
    params.type = OrderTypeKeys._limit;
    params.label = clientId;
    params.price = price;
    params.amount = qty;

    WebSocketRequest<PlaceOrderParams> request = new WebSocketRequest<>();
    request.id = idGenerator.getNextRequestId(Message.class);
    request.method = "private/" + side;
    request.params = params;

    if (session != null) {
      session.send(request);
      return request.id;
    }
    return -1;
  }

  public int placeMarketOrder(String market, String clientId, String side, BigDecimal qty) {
    PlaceOrderParams params = new PlaceOrderParams();
    params.instrument_name = market;
    params.type = OrderTypeKeys._market;
    params.label = clientId;
    params.amount = qty;

    WebSocketRequest<PlaceOrderParams> request = new WebSocketRequest<>();
    request.id = idGenerator.getNextRequestId(Message.class);
    request.method = "private/" + side;
    request.params = params;
    if (session != null) {
      session.send(request);
      return request.id;
    }
    return -1;
  }

  public int cancelOrder(String market, String clientId) {
    CancelOrderParams params = new CancelOrderParams();
    params.label = clientId;
    WebSocketRequest<CancelOrderParams> request = new WebSocketRequest<>();
    request.id = idGenerator.getNextRequestId(Message.class);
    request.method = "private/cancel_by_label";
    request.params = params;

    if (session != null) {
      session.send(request);
      return request.id;
    }
    return -1;
  }

  public int cancelAllOrders(String market) {
    CancelAllOrdersParams params = new CancelAllOrdersParams();
    params.instrument_name = market;
    WebSocketRequest<CancelAllOrdersParams> request = new WebSocketRequest<>();
    request.id = idGenerator.getNextRequestId(Message.class);
    request.method = "private/cancel_all_by_instrument";
    request.params = params;

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

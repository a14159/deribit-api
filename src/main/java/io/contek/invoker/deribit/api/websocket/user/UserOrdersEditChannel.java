package io.contek.invoker.deribit.api.websocket.user;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;
import io.contek.invoker.commons.websocket.SubscriptionState;
import io.contek.invoker.deribit.api.common._PlaceOrderResponse;
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
public final class UserOrdersEditChannel extends UserWebSocketNoSubscribeChannel<UserOrdersEditChannel.CancelResponse, String> {

  private volatile EditOrdersResponseListener listener;

  UserOrdersEditChannel(Id id, WebSocketRequestIdGenerator requestIdGenerator) {
    super(id, requestIdGenerator);
  }

  public void addRawMessageListener(EditOrdersResponseListener listener) {
    this.listener = listener;
  }

  @Override
  public Class<CancelResponse> getMessageType() {
    return CancelResponse.class;
  }

  @Immutable
  public static final class Id extends WebSocketNoSubscribeId<CancelResponse> {
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
    request.id = idGenerator.getNextRequestId(PlaceOrderResponse.class);
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
    request.id = idGenerator.getNextRequestId(PlaceOrderResponse.class);
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
    request.id = idGenerator.getNextRequestId(CancelResponse.class);
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
    request.id = idGenerator.getNextRequestId(CancelResponse.class);
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
    if (listener != null && anyWebSocketMessage instanceof EditOrderResponse msg) {
      listener.onNextRawMessage(msg);
    }
    return null;
  }

  public interface EditOrderResponse {}

  @NotThreadSafe
  public static final class CancelResponse extends WebSocketResponse<String> implements EditOrderResponse {}

  public static final class PlaceOrderResponse extends WebSocketResponse<_PlaceOrderResponse> implements EditOrderResponse {}
}

package io.contek.invoker.deribit.api.websocket.user;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;
import io.contek.invoker.commons.websocket.SubscriptionState;
import io.contek.invoker.deribit.api.common._Order;
import io.contek.invoker.deribit.api.common._PlaceOrderResponse;
import io.contek.invoker.deribit.api.common.constants.OrderTypeKeys;
import io.contek.invoker.deribit.api.common.constants.SideKeys;
import io.contek.invoker.deribit.api.websocket.WebSocketNoSubscribeId;
import io.contek.invoker.deribit.api.websocket.WebSocketRequestIdGenerator;
import io.contek.invoker.deribit.api.websocket.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.math.BigDecimal;

import static io.contek.invoker.commons.websocket.SubscriptionState.*;


@ThreadSafe
public final class UserOrdersEditChannel extends UserWebSocketNoSubscribeChannel<UserOrdersEditChannel.CancelResponse, String> {

  private static final Logger log = LogManager.getLogger(UserOrdersEditChannel.class);

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

  private final WebSocketRequest<PlaceOrderParams> loRequest = new WebSocketRequest<>();
  {
    loRequest.params = new PlaceOrderParams();
    loRequest.params.type = OrderTypeKeys._limit;
  }

  public int placeLimitOrder(String market, String clientId, String side, BigDecimal price, BigDecimal qty) {
    if (session == null) {
      log.warn("Trying to place a limit order but we don't have the session");
      return -1;
    }
    synchronized (loRequest) {
      loRequest.params.instrument_name = market;
      loRequest.params.label = clientId;
      loRequest.params.price = price;
      loRequest.params.amount = qty;

      loRequest.id = idGenerator.getNextRequestId(PlaceOrderResponse.class);
      switch (side) {
        case SideKeys._buy -> loRequest.method = "private/buy";
        case SideKeys._sell -> loRequest.method = "private/sell";
        default -> throw new IllegalArgumentException("Wrong side argument");
      }

      session.send(loRequest);
    }
    return loRequest.id;
  }

  private final WebSocketRequest<EditOrderByIdParams> editRequest = new WebSocketRequest<>();
  {
    editRequest.params = new EditOrderByIdParams();
    editRequest.method = "private/edit";
  }

  public int editLimitOrderById(String orderId, @Nullable BigDecimal price, BigDecimal qty) {
    if (session == null) {
      log.warn("Trying to edit a limit order by order id but we don't have the session");
      return -1;
    }
    synchronized (editRequest) {
      editRequest.params.order_id = orderId;
      editRequest.params.price = price;
      editRequest.params.amount = qty;

      editRequest.id = idGenerator.getNextRequestId(PlaceOrderResponse.class);

      session.send(editRequest);
    }
    return editRequest.id;
  }

  private final WebSocketRequest<PlaceOrderParams> moRequest = new WebSocketRequest<>();
  {
    moRequest.params = new PlaceOrderParams();
    moRequest.params.type = OrderTypeKeys._market;
  }

  public int placeMarketOrder(String market, String clientId, String side, BigDecimal qty) {
    if (session == null) {
      log.warn("Trying to place a market order but we don't have the session");
      return -1;
    }
    synchronized (moRequest) {
      moRequest.params.instrument_name = market;
      moRequest.params.label = clientId;
      moRequest.params.amount = qty;

      moRequest.id = idGenerator.getNextRequestId(PlaceOrderResponse.class);
      switch (side) {
        case SideKeys._buy -> moRequest.method = "private/buy";
        case SideKeys._sell -> moRequest.method = "private/sell";
        default -> throw new IllegalArgumentException("Wrong side argument");
      }

      session.send(moRequest);
    }
    return moRequest.id;
  }

  private final WebSocketRequest<CancelOrderParams> cancelRequest = new WebSocketRequest<>();
  {
    cancelRequest.params = new CancelOrderParams();
    cancelRequest.method = "private/cancel_by_label";
  }

  public int cancelOrderByLabel(String clientId, String currency) {
    if (session == null) {
      log.warn("Trying to cancel an order but we don't have the session");
      return -1;
    }
    synchronized (cancelRequest) {
      cancelRequest.params.label = clientId;
      cancelRequest.params.currency = currency;
      cancelRequest.id = idGenerator.getNextRequestId(CancelResponse.class);

      session.send(cancelRequest);
    }
    return cancelRequest.id;
  }

  private final WebSocketRequest<CancelOrderByIdParams> cancelByIdRequest = new WebSocketRequest<>();
  {
    cancelByIdRequest.params = new CancelOrderByIdParams();
    cancelByIdRequest.method = "private/cancel";
  }

  public int cancelOrderById(String orderId) {
    if (session == null) {
      log.warn("Trying to cancel an order by id but we don't have the session");
      return -1;
    }
    synchronized (cancelByIdRequest) {
      cancelByIdRequest.params.order_id = orderId;
      cancelByIdRequest.id = idGenerator.getNextRequestId(CancelByIdResponse.class);

      session.send(cancelByIdRequest);
    }
    return cancelByIdRequest.id;
  }

  private final WebSocketRequest<CancelAllOrdersParams> cancelAllRequest = new WebSocketRequest<>();
  {
    cancelAllRequest.params = new CancelAllOrdersParams();
    cancelAllRequest.method = "private/cancel_all_by_instrument";
  }

  public int cancelAllOrders(String market) {
    if (session == null) {
      log.warn("Trying to cancel all orders but we don't have the session");
      return -1;
    }

    synchronized (cancelAllRequest) {
      cancelAllRequest.params.instrument_name = market;
      cancelAllRequest.id = idGenerator.getNextRequestId(CancelResponse.class);

      session.send(cancelAllRequest);
    }
    return cancelAllRequest.id;
  }

  @Override
  protected SubscriptionState getState(AnyWebSocketMessage anyWebSocketMessage) {
    if (listener != null && anyWebSocketMessage instanceof EditOrderResponse msg) {
      listener.onNextRawMessage(msg);
    }
    if (lastStatusSent == SUBSCRIBING) {
      lastStatusSent = SUBSCRIBED;
      return SubscriptionState.SUBSCRIBED;
    }
    if (lastStatusSent == UNSUBSCRIBING) {
      lastStatusSent = UNSUBSCRIBED;
      return SubscriptionState.SUBSCRIBED;
    }
    return null;
  }

  public interface EditOrderResponse {}

  @NotThreadSafe
  public static final class CancelResponse extends WebSocketResponse<String> implements EditOrderResponse {}

  public static final class CancelByIdResponse extends WebSocketResponse<_Order> implements EditOrderResponse {}

  public static final class PlaceOrderResponse extends WebSocketResponse<_PlaceOrderResponse> implements EditOrderResponse {}
}

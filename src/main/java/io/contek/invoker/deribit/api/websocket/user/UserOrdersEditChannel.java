package io.contek.invoker.deribit.api.websocket.user;

import io.contek.invoker.deribit.api.common._Order;
import io.contek.invoker.deribit.api.websocket.WebSocketNoSubscribeId;
import io.contek.invoker.deribit.api.websocket.WebSocketRequestIdGenerator;
import io.contek.invoker.deribit.api.websocket.common.WebSocketResponse;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import java.math.BigDecimal;

import static java.lang.String.format;

@ThreadSafe
public final class UserOrdersEditChannel extends UserWebSocketNoSubscribeChannel<UserOrdersEditChannel.Message, UserOrdersEditChannel.Data> {

  UserOrdersEditChannel(Id id, WebSocketRequestIdGenerator requestIdGenerator) {
    super(id, requestIdGenerator);
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
    return -1;
  }

  public int placeMarketOrder(String market, String clientId, String side, BigDecimal qty) {
    return -1;
  }

  public int cancelOrder(String market, String clientId) {
    return -1;
  }

  @NotThreadSafe
  public static final class Message extends WebSocketResponse<Data> {}

  public static final class Data extends _Order {}
}

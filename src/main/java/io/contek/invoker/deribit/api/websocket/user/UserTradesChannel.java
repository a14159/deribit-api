package io.contek.invoker.deribit.api.websocket.user;

import io.contek.invoker.deribit.api.common._LightUserTrade;
import io.contek.invoker.deribit.api.websocket.WebSocketChannelId;
import io.contek.invoker.deribit.api.websocket.WebSocketRequestIdGenerator;
import io.contek.invoker.deribit.api.websocket.common.WebSocketSingleChannelMessage;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;

import static java.lang.String.format;

@ThreadSafe
public final class UserTradesChannel
    extends UserWebSocketChannel<UserTradesChannel.Message, UserTradesChannel.Data> {

  UserTradesChannel(Id id, WebSocketRequestIdGenerator requestIdGenerator) {
    super(id, requestIdGenerator);
  }

  @Override
  public Class<Message> getMessageType() {
    return Message.class;
  }

  @Immutable
  public static final class Id extends WebSocketChannelId<Message> {

    private Id(String value) {
      super(value);
    }

    public static Id of(String kind, String currency, String interval) {
      return new Id(format("user.trades.%s.%s.%s", kind, currency, interval));
    }

    public static Id of(String instrumentName, String interval) {
      return new Id(format("user.trades.%s.%s", instrumentName, interval));
    }
  }

  @NotThreadSafe
  public static final class Message extends WebSocketSingleChannelMessage<Data> {}

  @NotThreadSafe
  public static final class Data extends ArrayList<_LightUserTrade> {}
}

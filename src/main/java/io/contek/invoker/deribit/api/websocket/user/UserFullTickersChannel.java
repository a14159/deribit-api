package io.contek.invoker.deribit.api.websocket.user;

import io.contek.invoker.deribit.api.common._Ticker;
import io.contek.invoker.deribit.api.websocket.WebSocketChannelId;
import io.contek.invoker.deribit.api.websocket.WebSocketRequestIdGenerator;
import io.contek.invoker.deribit.api.websocket.common.WebSocketSingleChannelMessage;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import static java.lang.String.format;

/**
 * Full ticker subscription for consumers which need option valuation fields such as mark IV.
 * Latency-sensitive BBO consumers should continue to use {@link UserTickersChannel}.
 */
@ThreadSafe
public final class UserFullTickersChannel
    extends UserWebSocketChannel<UserFullTickersChannel.Message, UserFullTickersChannel.Data> {

  UserFullTickersChannel(Id id, WebSocketRequestIdGenerator requestIdGenerator) {
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

    public static Id of(String instrumentName, String interval) {
      return new Id(format("ticker.%s.%s", instrumentName, interval));
    }
  }

  @NotThreadSafe
  public static final class Message extends WebSocketSingleChannelMessage<Data> {}

  @NotThreadSafe
  public static final class Data extends _Ticker {}
}

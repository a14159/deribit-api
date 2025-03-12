package io.contek.invoker.deribit.api.websocket.market;

import io.contek.invoker.deribit.api.common._OrderBookLevel;
import io.contek.invoker.deribit.api.common._OrderBookLevelUpdate;
import io.contek.invoker.deribit.api.websocket.WebSocketChannelId;
import io.contek.invoker.deribit.api.websocket.WebSocketRequestIdGenerator;
import io.contek.invoker.deribit.api.websocket.common.WebSocketSingleChannelMessage;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

import static io.contek.invoker.deribit.api.websocket.common.constants.WebSocketChannelKeys._book;
import static java.lang.String.format;

@ThreadSafe
public final class BookRealtimeChannel
        extends MarketWebSocketChannel<BookRealtimeChannel.Message, BookRealtimeChannel.Data> {

  BookRealtimeChannel(Id id, WebSocketRequestIdGenerator requestIdGenerator) {
    super(id, requestIdGenerator);
  }

  @Override
  public Class<BookRealtimeChannel.Message> getMessageType() {
    return BookRealtimeChannel.Message.class;
  }

  @Immutable
  public static final class Id extends WebSocketChannelId<Message> {

    private Id(String value) {
      super(value);
    }

    public static Id of(String instrumentName, String interval) {
      return new Id(format("%s.%s.%s", _book, instrumentName, interval));
    }
  }

  @NotThreadSafe
  public static final class Message extends WebSocketSingleChannelMessage<Data> {}

  @NotThreadSafe
  public static final class Data {

    public long timestamp;
    public String instrument_name;
    public long change_id;
    public long prev_change_id;
    public String type;
    public List<_OrderBookLevelUpdate> bids;
    public List<_OrderBookLevelUpdate> asks;
  }
}

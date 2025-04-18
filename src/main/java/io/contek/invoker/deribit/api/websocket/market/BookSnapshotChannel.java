package io.contek.invoker.deribit.api.websocket.market;

import io.contek.invoker.deribit.api.common._OrderBookLevel;
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
public final class BookSnapshotChannel
    extends MarketWebSocketChannel<BookSnapshotChannel.Message, BookSnapshotChannel.Data> {

  BookSnapshotChannel(Id id, WebSocketRequestIdGenerator requestIdGenerator) {
    super(id, requestIdGenerator);
  }

  @Override
  public Class<BookSnapshotChannel.Message> getMessageType() {
    return BookSnapshotChannel.Message.class;
  }

  @Immutable
  public static final class Id extends WebSocketChannelId<Message> {

    private Id(String value) {
      super(value);
    }

    public static Id of(String instrumentName, String group, int depth, String interval) {
      return new Id(format("%s.%s.%s.%d.%s", _book, instrumentName, group, depth, interval));
    }
  }

  @NotThreadSafe
  public static final class Message extends WebSocketSingleChannelMessage<Data> {}

  @NotThreadSafe
  public static final class Data {

    public long timestamp;
    public String instrument_name;
    public long change_id;
    public List<_OrderBookLevel> bids;
    public List<_OrderBookLevel> asks;
    public long traceNano = System.nanoTime();
  }
}

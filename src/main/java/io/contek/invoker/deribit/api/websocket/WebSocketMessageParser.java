package io.contek.invoker.deribit.api.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.contek.invoker.commons.websocket.AnyWebSocketMessage;
import io.contek.invoker.commons.websocket.IWebSocketComponent;
import io.contek.invoker.commons.websocket.WebSocketTextMessageParser;
import io.contek.invoker.deribit.api.websocket.common.WebSocketHeartbeat;
import io.contek.invoker.deribit.api.websocket.common.WebSocketInboundMessage;
import io.contek.invoker.deribit.api.websocket.common.WebSocketResponse;
import io.contek.invoker.deribit.api.websocket.common.constants.WebSocketChannelKeys;
import io.contek.invoker.deribit.api.websocket.market.BookSnapshotChannel;
import io.contek.invoker.deribit.api.websocket.market.TradesChannel;
import io.contek.invoker.deribit.api.websocket.user.*;
import is.fm.util.collections.ExpiringIntMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
final class WebSocketMessageParser extends WebSocketTextMessageParser {

  private static final Logger log = LogManager.getLogger(WebSocketMessageParser.class);
  private static final int MAX_UNKNOWN_RESPONSE_TYPES = 20;

//  private final Map<Integer, Class<? extends WebSocketResponse<?>>> pendingRequests = new ExpiringMap<>(100);
  private final ExpiringIntMap<Class<? extends WebSocketResponse<?>>> pendingRequests = new ExpiringIntMap<>(129);

  private int unknownResponseTypes;

  public void register(int id, Class<? extends WebSocketResponse<?>> type) {
    synchronized (pendingRequests) {
      pendingRequests.put(id, type);
    }
  }

  @Override
  public void register(IWebSocketComponent component) {}

  @Override
  protected AnyWebSocketMessage fromText(String text) {
    JSONObject obj = JSON.parseObject(text);
    if (obj.containsKey("params")) {
      if (obj.containsKey("method") && obj.get("method").toString().equals("heartbeat"))
        return new WebSocketHeartbeat();
      return toDataMessage(obj);
    }
    if (obj.containsKey("id")) {
      return toResponseMessage(obj);
    }

    throw new IllegalArgumentException(text);
  }

  private WebSocketResponse<?> toResponseMessage(JSONObject obj) {
    int id = obj.getIntValue("id");
    Class<? extends WebSocketResponse<?>> type;
    synchronized (pendingRequests) {
      type = pendingRequests.remove(id);
    }
    if (type == null) {
      return handleUnknownResponseType(id, obj);
    }
    return obj.toJavaObject(type);
  }

  private WebSocketResponse<?> handleUnknownResponseType(int id, JSONObject message) {
    final int count;
    synchronized (pendingRequests) {
      count = ++unknownResponseTypes;
      if (count == MAX_UNKNOWN_RESPONSE_TYPES) {
        unknownResponseTypes = 0;
      }
    }

    log.warn("Expected response type not found for id {} ({}/{}); message: {}",
        id, count, MAX_UNKNOWN_RESPONSE_TYPES, message);

    if (count == MAX_UNKNOWN_RESPONSE_TYPES) {
      throw new IllegalStateException(
          "Expected response type not found " + MAX_UNKNOWN_RESPONSE_TYPES + " times; last id: " + id);
    }

    return IgnoredWebSocketResponse.INSTANCE;
  }

  private WebSocketInboundMessage toDataMessage(JSONObject obj) {
    final JSONObject params = obj.getJSONObject("params");
    final String channel = params.get("channel").toString();
    if (channel.startsWith(WebSocketChannelKeys._user_trades)) {
      return obj.toJavaObject(UserTradesChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._user_orders)) {
      return obj.toJavaObject(UserOrdersChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._tickers)) {
      return obj.toJavaObject(UserTickersChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._book)) {
      JSONObject data = params.getJSONObject("data");
      if (data.containsKey("type")) {
        return obj.toJavaObject(UserBookChangeChannel.Message.class);
      }
      return obj.toJavaObject(BookSnapshotChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._user_changes)) {
      return obj.toJavaObject(UserChangesChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._trades)) {
      return obj.toJavaObject(TradesChannel.Message.class);
    }

    throw new IllegalArgumentException(obj.toString());
  }

  private static final class IgnoredWebSocketResponse extends WebSocketResponse<Object> {
    private static final IgnoredWebSocketResponse INSTANCE = new IgnoredWebSocketResponse();
  }
}

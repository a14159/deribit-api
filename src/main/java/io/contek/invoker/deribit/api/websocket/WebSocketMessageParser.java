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

import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;

@ThreadSafe
final class WebSocketMessageParser extends WebSocketTextMessageParser {

  private final Map<Integer, Class<? extends WebSocketResponse<?>>> pendingRequests = new ExpiringMap<>(100);

  public void register(Integer id, Class<? extends WebSocketResponse<?>> type) {
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
    int id = Integer.parseInt(obj.get("id").toString());
    Class<? extends WebSocketResponse<?>> type;
    synchronized (pendingRequests) {
      type = pendingRequests.remove(id);
    }
    if (type == null) {
      throw new IllegalStateException("Expected response type not found: " + id);
    }
    return obj.toJavaObject(type);
  }

  private WebSocketInboundMessage toDataMessage(JSONObject obj) {
    final JSONObject params = obj.getJSONObject("params");
    final String channel = params.get("channel").toString();
    if (channel.startsWith(WebSocketChannelKeys._user_trades)) {
      return obj.toJavaObject(UserTradesChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._tickers)) {
      return obj.toJavaObject(UserTickersChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._user_orders)) {
      return obj.toJavaObject(UserOrdersChannel.Message.class);
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
}

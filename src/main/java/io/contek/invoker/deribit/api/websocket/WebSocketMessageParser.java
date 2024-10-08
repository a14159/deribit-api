package io.contek.invoker.deribit.api.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

import static java.lang.String.format;

@ThreadSafe
final class WebSocketMessageParser extends WebSocketTextMessageParser {

  private final Gson gson = new Gson();

  private final Map<Integer, Class<? extends WebSocketResponse<?>>> pendingRequests =
      new ExpiringMap<>(100);

  public void register(Integer id, Class<? extends WebSocketResponse<?>> type) {
    synchronized (pendingRequests) {
      pendingRequests.put(id, type);
    }
  }

  @Override
  public void register(IWebSocketComponent component) {}

  @Override
  protected AnyWebSocketMessage fromText(String text) {
    JsonElement json = gson.fromJson(text, JsonElement.class);
    if (!json.isJsonObject()) {
      throw new IllegalArgumentException(text);
    }
    JsonObject obj = json.getAsJsonObject();
    if (obj.has("id")) {
      return toResponseMessage(obj);
    } else if (obj.has("params")) {
      if (obj.has("method") && obj.get("method").getAsString().equals("heartbeat"))
        return new WebSocketHeartbeat();
      return toDataMessage(obj);
    } else {
      throw new IllegalArgumentException(text);
    }
  }

  private WebSocketResponse<?> toResponseMessage(JsonObject obj) {
    int id = obj.get("id").getAsInt();
    Class<? extends WebSocketResponse<?>> type = pendingRequests.remove(id);
    if (type == null) {
      throw new IllegalStateException(format("Expected response type not found: %d", id));
    }
    return gson.fromJson(obj, type);
  }

  private WebSocketInboundMessage toDataMessage(JsonObject obj) {
    JsonObject params = obj.getAsJsonObject("params");
    String channel = params.get("channel").getAsString();
    if (channel.startsWith(WebSocketChannelKeys._book)) {
      JsonObject data = params.getAsJsonObject("data");
      if (data.has("type")) {
        return gson.fromJson(obj, UserBookChangeChannel.Message.class);
      }
      return gson.fromJson(obj, BookSnapshotChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._trades)) {
      return gson.fromJson(obj, TradesChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._user_changes)) {
      return gson.fromJson(obj, UserChangesChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._user_orders)) {
      return gson.fromJson(obj, UserOrdersChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._user_trades)) {
      return gson.fromJson(obj, UserTradesChannel.Message.class);
    }
    if (channel.startsWith(WebSocketChannelKeys._tickers)) {
      return gson.fromJson(obj, UserTickersChannel.Message.class);
    } else {
      throw new IllegalArgumentException(obj.toString());
    }
  }
}

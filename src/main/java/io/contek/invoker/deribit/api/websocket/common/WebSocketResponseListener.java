package io.contek.invoker.deribit.api.websocket.common;

public interface WebSocketResponseListener<T> {

  void onNextRawMessage(WebSocketResponse<T> response);
}

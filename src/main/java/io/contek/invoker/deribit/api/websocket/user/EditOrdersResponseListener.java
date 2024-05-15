package io.contek.invoker.deribit.api.websocket.user;

public interface EditOrdersResponseListener {

  void onNextRawMessage(UserOrdersEditChannel.EditOrderResponse response);
}

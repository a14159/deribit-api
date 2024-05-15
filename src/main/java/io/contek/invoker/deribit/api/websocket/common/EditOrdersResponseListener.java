package io.contek.invoker.deribit.api.websocket.common;

import io.contek.invoker.deribit.api.websocket.user.UserOrdersEditChannel;

public interface EditOrdersResponseListener {

  void onNextRawMessage(UserOrdersEditChannel.EditOrderResponse response);
}

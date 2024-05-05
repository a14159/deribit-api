package io.contek.invoker.deribit.api.rest.user;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class GetBuy extends BasePlaceOrderRequest {

  GetBuy(IActor actor, RestContext context) {
    super(actor, context);
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/buy";
  }
}

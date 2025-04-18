package io.contek.invoker.deribit.api.rest.user;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.common._Order;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;

import static io.contek.invoker.commons.rest.RestMethod.GET;
import static java.util.Objects.requireNonNull;

@NotThreadSafe
public final class GetCancel extends UserRestRequest<GetCancel.Response> {

  private String orderId;

  GetCancel(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetCancel setOrderId(String orderId) {
    this.orderId = orderId;
    return this;
  }

  @Override
  protected Class<GetCancel.Response> getResponseType() {
    return Response.class;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/cancel";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(orderId);
    builder.add("order_id", orderId);

    return builder.build();
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<_Order> {}
}

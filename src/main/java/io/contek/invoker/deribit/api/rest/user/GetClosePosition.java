package io.contek.invoker.deribit.api.rest.user;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.common._PlaceOrderResponse;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;

import static io.contek.invoker.commons.rest.RestMethod.GET;
import static java.util.Objects.requireNonNull;

@NotThreadSafe
public final class GetClosePosition extends UserRestRequest<GetClosePosition.Response> {

  private String instrument_name;
  private String type;
  private Double price;

  GetClosePosition(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetClosePosition setInstrumentName(String instrument_name) {
    this.instrument_name = instrument_name;
    return this;
  }

  public GetClosePosition setType(String type) {
    this.type = type;
    return this;
  }

  public GetClosePosition setPrice(double price) {
    this.price = price;
    return this;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/close_position";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(instrument_name);
    builder.add("instrument_name", instrument_name);

    requireNonNull(type);
    builder.add("type", type);

    if (price != null) {
      builder.add("price", price);
    }

    return builder.build();
  }

  @Override
  protected Class<Response> getResponseType() {
    return Response.class;
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<_PlaceOrderResponse> {}
}

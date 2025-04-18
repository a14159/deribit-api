package io.contek.invoker.deribit.api.rest.user;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.common._Position;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;

import static io.contek.invoker.commons.rest.RestMethod.GET;
import static java.util.Objects.requireNonNull;

@NotThreadSafe
public final class GetPosition extends UserRestRequest<GetPosition.Response> {

  private String instrumentName;

  GetPosition(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetPosition setInstrumentName(String instrumentName) {
    this.instrumentName = instrumentName;
    return this;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/get_position";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(instrumentName);
    builder.add("instrument_name", instrumentName);

    return builder.build();
  }

  @Override
  protected Class<GetPosition.Response> getResponseType() {
    return GetPosition.Response.class;
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<_Position> {}
}

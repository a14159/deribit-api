package io.contek.invoker.deribit.api.rest.user;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;

import static io.contek.invoker.commons.rest.RestMethod.GET;
import static java.util.Objects.requireNonNull;

@NotThreadSafe
public final class GetCancelAllByInstrument extends UserRestRequest<GetCancelAllByInstrument.Response> {

  private String instrument;

  GetCancelAllByInstrument(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetCancelAllByInstrument setInstrumentName(String instrument_name) {
    this.instrument = instrument_name;
    return this;
  }

  @Override
  protected Class<GetCancelAllByInstrument.Response> getResponseType() {
    return Response.class;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/cancel_all_by_instrument";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(instrument);
    builder.add("instrument_name", instrument);

    return builder.build();
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<Integer> {}
}

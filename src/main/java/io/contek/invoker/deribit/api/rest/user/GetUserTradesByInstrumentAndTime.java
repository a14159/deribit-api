package io.contek.invoker.deribit.api.rest.user;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.TypedPermitRequest;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.common._UserTrades;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;

import static io.contek.invoker.commons.rest.RestMethod.GET;
import static io.contek.invoker.deribit.api.ApiFactory.RateLimits.ONE_API_KEY_NON_MATCHING_ENGINE_REQUEST;
import static java.util.Objects.requireNonNull;

@NotThreadSafe
public final class GetUserTradesByInstrumentAndTime
    extends UserRestRequest<GetUserTradesByInstrumentAndTime.Response> {

  private String instrumentName;
  private long startTime;
  private long endTime;

    GetUserTradesByInstrumentAndTime(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetUserTradesByInstrumentAndTime setInstrumentName(String instrumentName) {
    this.instrumentName = instrumentName;
    return this;
  }

  public GetUserTradesByInstrumentAndTime setStartTime(long startTime) {
      this.startTime = startTime;
      return this;
  }

  public GetUserTradesByInstrumentAndTime setEndTime(long endTime) {
    this.endTime = endTime;
    return this;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/get_user_trades_by_instrument_and_time";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(instrumentName);
    builder.add("instrument_name", instrumentName);
    builder.add("start_timestamp", startTime);
    builder.add("end_timestamp", endTime);
    builder.add("count", 1000);

    return builder.build();
  }

  @Override
  protected ImmutableList<TypedPermitRequest> getRequiredQuotas() {
    return ONE_API_KEY_NON_MATCHING_ENGINE_REQUEST;
  }

  @Override
  protected Class<Response> getResponseType() {
    return Response.class;
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<_UserTrades> {}
}

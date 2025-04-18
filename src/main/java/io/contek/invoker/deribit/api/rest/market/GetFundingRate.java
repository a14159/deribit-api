package io.contek.invoker.deribit.api.rest.market;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;
import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

@NotThreadSafe
public final class GetFundingRate extends MarketRestRequest<GetFundingRate.Response> {

  private String instrumentName;
  private long startTime;
  private long endTime;

  GetFundingRate(IActor actor, RestContext context) {
    super(actor, context);
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/public/get_funding_rate_value";
  }

  public GetFundingRate setInstrumentName(String instrumentName) {
    this.instrumentName = instrumentName;
    return this;
  }

  public GetFundingRate setStartTime(long ts) {
    this.startTime = ts;
    return this;
  }

  public GetFundingRate setEndTime(long ts) {
    this.endTime = ts;
    return this;
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(instrumentName);
    builder.add("instrument_name", instrumentName);
    builder.add("start_timestamp", startTime);
    builder.add("end_timestamp", endTime);

    return builder.build();
  }

  @Override
  protected Class<Response> getResponseType() {
    return Response.class;
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<BigDecimal> {}
}

package io.contek.invoker.deribit.api.rest.user;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.TypedPermitRequest;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.common._UserTrade;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

import static io.contek.invoker.commons.rest.RestMethod.GET;
import static io.contek.invoker.deribit.api.ApiFactory.RateLimits.ONE_API_KEY_NON_MATCHING_ENGINE_REQUEST;
import static java.util.Objects.requireNonNull;

@NotThreadSafe
public final class GetUserTradesByCurrencyAndTime
    extends UserRestRequest<GetUserTradesByCurrencyAndTime.Response> {

  private String currency;
  private String kind;
  private long startTime;
  private long endTime;

    GetUserTradesByCurrencyAndTime(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetUserTradesByCurrencyAndTime setCurrency(String currency) {
    this.currency = currency;
    return this;
  }

  public GetUserTradesByCurrencyAndTime setKind(String kind) {
    this.kind = kind;
    return this;
  }

  public GetUserTradesByCurrencyAndTime setStartTime(long startTime) {
      this.startTime = startTime;
      return this;
  }

  public GetUserTradesByCurrencyAndTime setEndTime(long endTime) {
    this.endTime = endTime;
    return this;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/get_user_trades_by_currency_and_time";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(currency);
    requireNonNull(kind);
    builder.add("currency", currency);
    builder.add("kind", kind);
    builder.add("start_timestamp", startTime);
    builder.add("end_timestamp", endTime);
    builder.add("count", 10000);

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
  public static final class Response extends RestResponse<List<_UserTrade>> {}
}
package io.contek.invoker.deribit.api.rest.market;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.TypedPermitRequest;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.common._OrderBook;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import static io.contek.invoker.deribit.api.ApiFactory.RateLimits.ONE_IP_NON_MATCHING_ENGINE_REQUEST;
import static java.util.Objects.requireNonNull;

@NotThreadSafe
public final class GetOrderBookById extends MarketRestRequest<GetOrderBookById.Response> {

  private Integer instrument_id;
  private Integer depth;

  GetOrderBookById(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetOrderBookById setInstrumentName(int instrumentId) {
    this.instrument_id = instrumentId;
    return this;
  }

  public GetOrderBookById setDepth(@Nullable Integer depth) {
    this.depth = depth;
    return this;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/public/get_order_book_by_instrument_id";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(instrument_id);
    builder.add("instrument_id", instrument_id);

    if (depth != null) {
      builder.add("depth", depth);
    }

    return builder.build();
  }

  @Override
  protected Class<Response> getResponseType() {
    return Response.class;
  }

  @Override
  protected ImmutableList<TypedPermitRequest> getRequiredQuotas() {
    return ONE_IP_NON_MATCHING_ENGINE_REQUEST;
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<_OrderBook> {}
}

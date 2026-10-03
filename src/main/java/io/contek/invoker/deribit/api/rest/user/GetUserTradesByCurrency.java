package io.contek.invoker.deribit.api.rest.user;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.common._LightUserTrades;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;

import static io.contek.invoker.commons.rest.RestMethod.GET;
import static java.util.Objects.requireNonNull;

/** One page of user trades. Inspect result.has_more; pagination is caller controlled. */
@NotThreadSafe
public final class GetUserTradesByCurrency extends UserRestRequest<GetUserTradesByCurrency.Response> {

  private String currency;
  private String kind;
  private String startId;
  private String endId;
  private Long startTime;
  private Long endTime;
  private int count = 1000;
  private String sorting;
  private Boolean historical;

  GetUserTradesByCurrency(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetUserTradesByCurrency setCurrency(String currency) {
    this.currency = currency;
    return this;
  }

  public GetUserTradesByCurrency setKind(String kind) {
    this.kind = kind;
    return this;
  }

  /** First trade ID to return. IDs are exchange supplied strings, not numeric offsets. */
  public GetUserTradesByCurrency setStartId(String startId) {
    this.startId = startId;
    return this;
  }

  /** Last trade ID to return. */
  public GetUserTradesByCurrency setEndId(String endId) {
    this.endId = endId;
    return this;
  }

  /** Earliest exchange timestamp, in milliseconds since the Unix epoch. */
  public GetUserTradesByCurrency setStartTime(long startTime) {
    this.startTime = startTime;
    return this;
  }

  /** Latest exchange timestamp, in milliseconds since the Unix epoch. */
  public GetUserTradesByCurrency setEndTime(long endTime) {
    this.endTime = endTime;
    return this;
  }

  /** Page size from 1 to 1000; defaults to 1000, matching the time-based requests. */
  public GetUserTradesByCurrency setCount(int count) {
    this.count = count;
    return this;
  }

  /** asc, desc, or default; null leaves the exchange's sorting default in effect. */
  public GetUserTradesByCurrency setSorting(String sorting) {
    this.sorting = sorting;
    return this;
  }

  /** Historical records exclude recent records; null uses the exchange default (false). */
  public GetUserTradesByCurrency setHistorical(Boolean historical) {
    this.historical = historical;
    return this;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/get_user_trades_by_currency";
  }

  @Override
  protected RestParams getParams() {
    requireNonNull(currency, "currency");
    if (startTime != null && endTime != null && startTime > endTime) {
      throw new IllegalArgumentException("start_timestamp must not exceed end_timestamp");
    }
    if (count < 1 || count > 1000) {
      throw new IllegalArgumentException("count must be between 1 and 1000");
    }
    if (sorting != null && !"asc".equals(sorting) && !"desc".equals(sorting)
        && !"default".equals(sorting)) {
      throw new IllegalArgumentException("sorting must be asc, desc, or default");
    }

    RestParams.Builder builder = RestParams.newBuilder();
    builder.add("currency", currency);
    if (kind != null) {
      builder.add("kind", kind);
    }
    if (startId != null) {
      builder.add("start_id", startId);
    }
    if (endId != null) {
      builder.add("end_id", endId);
    }
    if (startTime != null) {
      builder.add("start_timestamp", startTime);
    }
    if (endTime != null) {
      builder.add("end_timestamp", endTime);
    }
    builder.add("count", count);
    if (sorting != null) {
      builder.add("sorting", sorting);
    }
    if (historical != null) {
      builder.add("historical", historical);
    }
    return builder.build();
  }

  @Override
  protected Class<Response> getResponseType() {
    return Response.class;
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<_LightUserTrades> {}
}

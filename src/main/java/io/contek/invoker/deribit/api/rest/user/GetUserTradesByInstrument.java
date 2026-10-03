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
public final class GetUserTradesByInstrument extends UserRestRequest<GetUserTradesByInstrument.Response> {

  private String instrumentName;
  private Long startSeq;
  private Long endSeq;
  private Long startTime;
  private Long endTime;
  private int count = 1000;
  private String sorting;
  private Boolean historical;

  GetUserTradesByInstrument(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetUserTradesByInstrument setInstrumentName(String instrumentName) {
    this.instrumentName = instrumentName;
    return this;
  }

  /** First trade sequence to return, scoped to this instrument. */
  public GetUserTradesByInstrument setStartSeq(long startSeq) {
    this.startSeq = startSeq;
    return this;
  }

  /** Last trade sequence to return, scoped to this instrument. */
  public GetUserTradesByInstrument setEndSeq(long endSeq) {
    this.endSeq = endSeq;
    return this;
  }

  /** Earliest exchange timestamp, in milliseconds since the Unix epoch. */
  public GetUserTradesByInstrument setStartTime(long startTime) {
    this.startTime = startTime;
    return this;
  }

  /** Latest exchange timestamp, in milliseconds since the Unix epoch. */
  public GetUserTradesByInstrument setEndTime(long endTime) {
    this.endTime = endTime;
    return this;
  }

  /** Page size from 1 to 1000; defaults to 1000, matching the time-based requests. */
  public GetUserTradesByInstrument setCount(int count) {
    this.count = count;
    return this;
  }

  /** asc, desc, or default; null leaves the exchange's sorting default in effect. */
  public GetUserTradesByInstrument setSorting(String sorting) {
    this.sorting = sorting;
    return this;
  }

  /** Historical records exclude recent records; null uses the exchange default (false). */
  public GetUserTradesByInstrument setHistorical(Boolean historical) {
    this.historical = historical;
    return this;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/get_user_trades_by_instrument";
  }

  @Override
  protected RestParams getParams() {
    requireNonNull(instrumentName, "instrument_name");
    if (startSeq != null && endSeq != null && startSeq > endSeq) {
      throw new IllegalArgumentException("start_seq must not exceed end_seq");
    }
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
    builder.add("instrument_name", instrumentName);
    if (startSeq != null) {
      builder.add("start_seq", startSeq);
    }
    if (endSeq != null) {
      builder.add("end_seq", endSeq);
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

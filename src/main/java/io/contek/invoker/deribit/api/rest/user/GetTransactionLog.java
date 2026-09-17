package io.contek.invoker.deribit.api.rest.user;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.common._Error;
import io.contek.invoker.deribit.api.common._TransactionLog;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;

import static io.contek.invoker.commons.rest.RestMethod.GET;
import static java.util.Objects.requireNonNull;

/** One page of account transactions. Pagination and reconciliation are caller controlled. */
@NotThreadSafe
public final class GetTransactionLog extends UserRestRequest<GetTransactionLog.Response> {

  private String currency;
  private Long startTimestamp;
  private Long endTimestamp;
  private String query;
  private Integer count;
  private Integer subAccountId;
  private Long continuation;

  GetTransactionLog(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetTransactionLog setCurrency(String currency) {
    this.currency = currency;
    return this;
  }

  /** Earliest exchange timestamp, in milliseconds since the Unix epoch. */
  public GetTransactionLog setStartTimestamp(long startTimestamp) {
    this.startTimestamp = startTimestamp;
    return this;
  }

  /** Latest exchange timestamp, in milliseconds since the Unix epoch. */
  public GetTransactionLog setEndTimestamp(long endTimestamp) {
    this.endTimestamp = endTimestamp;
    return this;
  }

  /** Exchange filter keywords or address; null omits the filter. */
  public GetTransactionLog setQuery(String query) {
    this.query = query;
    return this;
  }

  /** Page size from 1 to 250; null uses the exchange default (100). */
  public GetTransactionLog setCount(Integer count) {
    this.count = count;
    return this;
  }

  public GetTransactionLog setSubAccountId(Integer subAccountId) {
    this.subAccountId = subAccountId;
    return this;
  }

  /** Pass the previous response token unchanged; null requests the first page. */
  public GetTransactionLog setContinuation(Long continuation) {
    this.continuation = continuation;
    return this;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/get_transaction_log";
  }

  @Override
  protected RestParams getParams() {
    requireNonNull(currency, "currency");
    requireNonNull(startTimestamp, "start_timestamp");
    requireNonNull(endTimestamp, "end_timestamp");
    if (startTimestamp > endTimestamp) {
      throw new IllegalArgumentException("start_timestamp must not exceed end_timestamp");
    }
    if (count != null && (count < 1 || count > 250)) {
      throw new IllegalArgumentException("count must be between 1 and 250");
    }

    RestParams.Builder builder = RestParams.newBuilder();
    builder.add("currency", currency);
    builder.add("start_timestamp", startTimestamp);
    builder.add("end_timestamp", endTimestamp);
    if (query != null) {
      builder.add("query", query);
    }
    if (count != null) {
      builder.add("count", count);
    }
    if (subAccountId != null) {
      builder.add("subaccount_id", subAccountId);
    }
    if (continuation != null) {
      builder.add("continuation", continuation);
    }
    return builder.build();
  }

  @Override
  protected Class<Response> getResponseType() {
    return Response.class;
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<_TransactionLog> {

    public _Error error;
  }
}

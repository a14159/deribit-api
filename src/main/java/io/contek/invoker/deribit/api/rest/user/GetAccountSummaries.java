package io.contek.invoker.deribit.api.rest.user;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.TypedPermitRequest;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.common._AccountSummaries;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;

import static io.contek.invoker.commons.rest.RestMethod.GET;
import static io.contek.invoker.deribit.api.ApiFactory.RateLimits.ONE_API_KEY_NON_MATCHING_ENGINE_REQUEST;

@NotThreadSafe
public final class GetAccountSummaries extends UserRestRequest<GetAccountSummaries.Response> {

  private String subaccountId;
  private Boolean extended;

  GetAccountSummaries(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetAccountSummaries setSubaccountId(String subId) {
    this.subaccountId = subId;
    return this;
  }

  public GetAccountSummaries setExtended(boolean extended) {
    this.extended = extended;
    return this;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/get_account_summaries";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    if (subaccountId != null) {
      builder.add("subaccount_id", subaccountId);
    }

    if (extended != null) {
      builder.add("extended", extended);
    }

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
  public static final class Response extends RestResponse<_AccountSummaries> {}
}

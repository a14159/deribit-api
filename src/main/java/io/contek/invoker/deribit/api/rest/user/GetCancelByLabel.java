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
public final class GetCancelByLabel extends UserRestRequest<GetCancelByLabel.Response> {
  private String label;
  private String currency;

  GetCancelByLabel(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetCancelByLabel setLabel(String label) {
    this.label = label;
    return this;
  }

  public GetCancelByLabel setCurrency(String currency) {
    this.currency = currency;
    return this;
  }

  @Override
  protected Class<GetCancelByLabel.Response> getResponseType() {
    return Response.class;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/cancel_by_label";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(label);
    builder.add("label", label);
    if (currency != null)
      builder.add("currency", currency);

    return builder.build();
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<Integer> {}
}

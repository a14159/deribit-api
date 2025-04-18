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
public final class GetEnableCancelOnDisconnect extends UserRestRequest<GetEnableCancelOnDisconnect.Response> {

  private String scope = "account";

  GetEnableCancelOnDisconnect(IActor actor, RestContext context) {
    super(actor, context);
  }

  public GetEnableCancelOnDisconnect setScope(String scope) {
    this.scope = scope;
    return this;
  }

  @Override
  protected Class<GetEnableCancelOnDisconnect.Response> getResponseType() {
    return Response.class;
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/enable_cancel_on_disconnect";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(scope);
    builder.add("scope", scope);

    return builder.build();
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<String> {}
}

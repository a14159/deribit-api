package io.contek.invoker.deribit.api.rest.user;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.TypedPermitRequest;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.common._PlaceOrderResponse;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;
import java.math.BigDecimal;

import static io.contek.invoker.commons.rest.RestMethod.GET;
import static io.contek.invoker.deribit.api.ApiFactory.RateLimits.ONE_API_KEY_MATCHING_ENGINE_REQUEST;
import static java.util.Objects.requireNonNull;

@NotThreadSafe
public class GetEditOrder extends UserRestRequest<GetEditOrder.Response> {

  private String orderId;
  private BigDecimal amount;
  private BigDecimal contracts;
  private BigDecimal price;
  private Boolean post_only;
  private Boolean reject_post_only;
  private Boolean reduce_only;
  private String trigger_price;
  private String trigger_offset;
  private String advanced;
  private Boolean mmp;

  GetEditOrder(IActor actor, RestContext context) {
    super(actor, context);
  }


  public final GetEditOrder setOrderId(String orderId) {
    this.orderId = orderId;
    return this;
  }

  public final GetEditOrder setAmount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  public final GetEditOrder setContracts(BigDecimal contracts) {
    this.contracts = contracts;
    return this;
  }

  public final GetEditOrder setPrice(BigDecimal price) {
    this.price = price;
    return this;
  }

  public final GetEditOrder setPostOnly(Boolean postOnly) {
    this.post_only = postOnly;
    return this;
  }

  public final GetEditOrder setRejectPostOnly(Boolean rejectPostOnly) {
    this.reject_post_only = rejectPostOnly;
    return this;
  }

  public final GetEditOrder setReduceOnly(Boolean reduceOnly) {
    this.reduce_only = reduceOnly;
    return this;
  }

  public final GetEditOrder setTriggerPrice(String triggerPrice) {
    this.trigger_price = triggerPrice;
    return this;
  }

  public final GetEditOrder setTriggerOffset(String triggerOffset) {
    this.trigger_offset = triggerOffset;
    return this;
  }

  public final GetEditOrder setAdvanced(String advanced) {
    this.advanced = advanced;
    return this;
  }

  public final GetEditOrder setMmp(boolean mmp) {
    this.mmp = mmp;
    return this;
  }

  @Override
  protected final RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/v2/private/edit";
  }

  @Override
  protected final RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(orderId);
    builder.add("order_id", orderId);

    if (amount != null) {
      builder.add("amount", amount.toPlainString());
    }

    if (contracts != null) {
      builder.add("contracts", contracts.toPlainString());
    }

    if (price != null) {
      builder.add("price", price.toPlainString());
    }

    if (post_only != null) {
      builder.add("post_only", post_only);
    }

    if (reject_post_only != null) {
      builder.add("reject_post_only", reject_post_only);
    }

    if (reduce_only != null) {
      builder.add("reduce_only", reduce_only);
    }

    if (trigger_price != null) {
      builder.add("trigger_price", trigger_price);
    }

    if (trigger_offset != null) {
      builder.add("trigger_offset", trigger_offset);
    }

    if (advanced != null) {
      builder.add("advanced", advanced);
    }

    if (mmp != null) {
      builder.add("mmp", mmp);
    }

    return builder.build();
  }

  @Override
  protected final Class<Response> getResponseType() {
    return Response.class;
  }

  @Override
  protected final ImmutableList<TypedPermitRequest> getRequiredQuotas() {
    return ONE_API_KEY_MATCHING_ENGINE_REQUEST;
  }

  @NotThreadSafe
  public static final class Response extends RestResponse<_PlaceOrderResponse> {}
}

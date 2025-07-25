package io.contek.invoker.deribit.api.rest.user;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.deribit.api.common._PlaceOrderResponse;
import io.contek.invoker.deribit.api.rest.common.RestResponse;

import javax.annotation.concurrent.NotThreadSafe;
import java.math.BigDecimal;

import static io.contek.invoker.commons.rest.RestMethod.GET;
import static java.util.Objects.requireNonNull;

@NotThreadSafe
public abstract class BasePlaceOrderRequest extends UserRestRequest<BasePlaceOrderRequest.Response> {

  private String instrument_name;
  private BigDecimal amount;
  private BigDecimal contracts;
  private String type;
  private String label;
  private BigDecimal price;
  private String time_in_force;
  private Double max_show;
  private Boolean post_only;
  private Boolean reject_post_only;
  private Boolean reduce_only;
  private BigDecimal stop_price;
  private String trigger;
  private String advanced;
  private Boolean mmp;

  BasePlaceOrderRequest(IActor actor, RestContext context) {
    super(actor, context);
  }

  public final BasePlaceOrderRequest setInstrumentName(String instrument_name) {
    this.instrument_name = instrument_name;
    return this;
  }

  public final BasePlaceOrderRequest setAmount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  public final BasePlaceOrderRequest setContracts(BigDecimal contracts) {
    this.contracts = contracts;
    return this;
  }

  public final BasePlaceOrderRequest setType(String type) {
    this.type = type;
    return this;
  }

  public final BasePlaceOrderRequest setLabel(String label) {
    this.label = label;
    return this;
  }

  public final BasePlaceOrderRequest setPrice(BigDecimal price) {
    this.price = price;
    return this;
  }

  public final BasePlaceOrderRequest setTimeInForce(String timeInForce) {
    this.time_in_force = timeInForce;
    return this;
  }

  public final BasePlaceOrderRequest setMaxShow(double maxShow) {
    this.max_show = maxShow;
    return this;
  }

  public final BasePlaceOrderRequest setPostOnly(Boolean postOnly) {
    this.post_only = postOnly;
    return this;
  }

  public final BasePlaceOrderRequest setRejectPostOnly(Boolean rejectPostOnly) {
    this.reject_post_only = rejectPostOnly;
    return this;
  }

  public final BasePlaceOrderRequest setReduceOnly(Boolean reduceOnly) {
    this.reduce_only = reduceOnly;
    return this;
  }

  public final BasePlaceOrderRequest setStopPrice(BigDecimal stopPrice) {
    this.stop_price = stopPrice;
    return this;
  }

  public final BasePlaceOrderRequest setTrigger(String trigger) {
    this.trigger = trigger;
    return this;
  }

  public final BasePlaceOrderRequest setAdvanced(String advanced) {
    this.advanced = advanced;
    return this;
  }

  public final BasePlaceOrderRequest setMmp(boolean mmp) {
    this.mmp = mmp;
    return this;
  }

  @Override
  protected final RestMethod getMethod() {
    return GET;
  }

  @Override
  protected abstract String getEndpointPath();

  @Override
  protected final RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(instrument_name);
    builder.add("instrument_name", instrument_name);

    if (amount == null && contracts == null)
      throw new IllegalArgumentException("One of the 'amount' or 'contracts' params must be set");

    if (amount != null) {
        builder.add("amount", amount.toPlainString());
    }

    if (contracts != null) {
      builder.add("contracts", contracts.toPlainString());
    }

    requireNonNull(type);
    builder.add("type", type);

    if (label != null) {
      builder.add("label", label);
    }

    if (price != null) {
      builder.add("price", price.toPlainString());
    }

    if (time_in_force != null) {
      builder.add("time_in_force", time_in_force);
    }

    if (max_show != null) {
      builder.add("max_show", max_show);
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

    if (stop_price != null) {
      builder.add("stop_price", stop_price.toPlainString());
    }

    if (trigger != null) {
      builder.add("trigger", trigger);
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

  @NotThreadSafe
  public static final class Response extends RestResponse<_PlaceOrderResponse> {}
}

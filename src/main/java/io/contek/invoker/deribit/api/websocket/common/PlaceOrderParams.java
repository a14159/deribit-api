package io.contek.invoker.deribit.api.websocket.common;

import java.math.BigDecimal;

public class PlaceOrderParams extends Params {
  public String instrument_name;
  public BigDecimal amount;
  public BigDecimal contracts;
  public String type;
  public String label;
  public BigDecimal price;
  public Boolean post_only;
  public Boolean reduce_only;
}

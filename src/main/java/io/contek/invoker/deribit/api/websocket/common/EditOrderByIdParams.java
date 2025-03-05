package io.contek.invoker.deribit.api.websocket.common;

import java.math.BigDecimal;

public class EditOrderByIdParams extends Params {
  public String order_id;
  public BigDecimal amount;
  public BigDecimal contracts;
  public BigDecimal price;
  public Boolean post_only;
  public Boolean reduce_only;
}

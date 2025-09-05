package io.contek.invoker.deribit.api.websocket.common;


public class EditOrderByIdParams extends Params {
  public String order_id;
  public String amount;
  public String contracts;
  public String price;
  public Boolean post_only;
  public Boolean reduce_only;
}

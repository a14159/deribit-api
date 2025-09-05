package io.contek.invoker.deribit.api.websocket.common;


public class PlaceOrderParams extends Params {
  public String instrument_name;
  public String amount;
  public String contracts;
  public String type;
  public String label;
  public String price;
  public Boolean post_only;
  public Boolean reduce_only;
}

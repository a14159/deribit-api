package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class _LightOrder {

  public String order_state;
//  public Double max_show;
//  public Boolean api;
  public Double amount;
//  public Boolean web;
  public String instrument_name;
//  public String advanced;
//  public Boolean triggered;
//  public Boolean block_trade;
//  public String original_order_type;
  public Object price;
  public String time_in_force;
//  public Boolean auto_replaced;
//  public String stop_order_id;
  public long last_update_timestamp;
  public Boolean post_only;
//  public Boolean replaced;
  public Double filled_amount;
//  public Double average_price;
  public String order_id;
  public Boolean reduce_only;
//  public Double commission;
//  public String app_name;
//  public Double stop_price;
  public String label;
  public long creation_timestamp;
  public String direction;
//  public Boolean is_liquidation;
  public String order_type;
//  public Double usd;
//  public Double profit_loss;
//  public Double implv;
//  public String trigger;

  public double getPrice() {
    if (price instanceof Number number) {
      return number.doubleValue();
    }
    if ("market_price".equals(price)) {
      return 0.0;
    }
    throw new IllegalStateException("Unexpected price value: " + price);
  }
}

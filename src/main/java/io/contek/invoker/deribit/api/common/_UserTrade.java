package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class _UserTrade {

  public long trade_seq;
  public String trade_id;
  public long timestamp;
  public int tick_direction;
  public String state;
  public boolean self_trade;
  public boolean reduce_only;
  public double price;
  public boolean post_only;
  public String order_type;
  public String order_id;
  public String matching_id;
  public double mark_price;
  public String liquidity;
  public String instrument_name;
  public double index_price;
  public String fee_currency;
  public double fee;
  public String direction;
  public double amount;
  public String advanced;
  public String block_trade_id;
  public Double iv;
  public String label;
  public String liquidation;
  public Double profit_loss;
  public Double stop_price;
  public String trigger;
  public Boolean mmp;
  public long traceNano = System.nanoTime();
}

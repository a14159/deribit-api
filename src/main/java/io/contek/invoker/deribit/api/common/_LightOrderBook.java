package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

@NotThreadSafe
public class _LightOrderBook {

//  public Double ask_iv; // use -1 to represent empty maybe?
  public List<_OrderBookLevel> asks;
//  public double best_ask_amount;
//  public double best_ask_price;
//  public double best_bid_amount;
//  public double best_bid_price;
//  public Double bid_iv;
  public List<_OrderBookLevel> bids;
//  public Double current_funding;
//  public Double delivery_price;
//  public Double funding_8h;
//  public _Greek greeks;
//  public Double index_price;
  public String instrument_name;
//  public Double interest_rate;
//  public Double last_price;
//  public Double mark_iv;
//  public Double mark_price;
//  public Double max_price;
//  public Double min_price;
//  public Double open_interest;
//  public Double settlement_price;
//  public String state;
//  public _Stats stats;
  public long timestamp;
  public long change_id;
//  public Double underlying_index;
//  public Double underlying_price;
  public long traceNano = System.nanoTime();
}

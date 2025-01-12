package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;
import java.math.BigDecimal;

@NotThreadSafe
public class _LightTicker {

  public BigDecimal best_ask_amount;
  public BigDecimal best_ask_price;
  public BigDecimal best_bid_amount;
  public BigDecimal best_bid_price;
  public BigDecimal current_funding;
  public BigDecimal funding_8h;
//  public _Greek greeks;
  public Double index_price;
  public String instrument_name;
//  public Double interest_rate;
  public BigDecimal last_price;
//  public Double mark_price;
//  public Double max_price;
//  public Double min_price;
//  public Double open_interest;
//  public Double settlement_price;
  public String state;
//  public _Stats stats;
  public Long timestamp;
  public long traceNano = System.nanoTime();
}

package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;


@NotThreadSafe
public class _Position {

  public double average_price;
  public Double average_price_usd;
  public double delta;
  public String direction;
  public Double estimated_liquidation_price;
  public double floating_profit_loss;
  public Double floating_profit_loss_usd;
  public Double gamma;
  public double index_price;
  public double initial_margin;
  public String instrument_name;
  public String kind;
  public Integer leverage;
  public double maintenance_margin;
  public double mark_price;
  public Double open_orders_margin;
  public Double realized_funding;
  public double realized_profit_loss;
  public Double settlement_price;
  public double size;
  public Double size_currency;
  public Double theta;
  public double total_profit_loss;
  public Double vega;

  public double getSizeCurrency() {
    if (size_currency != null)
      return size_currency;
    return 0.0;
  }
}

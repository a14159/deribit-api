package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;
import java.math.BigDecimal;

@NotThreadSafe
public class _Position {

  public BigDecimal average_price;
  public BigDecimal average_price_usd;
  public BigDecimal delta;
  public String direction;
  public BigDecimal estimated_liquidation_price;
  public BigDecimal floating_profit_loss;
  public BigDecimal floating_profit_loss_usd;
  public BigDecimal gamma;
  public BigDecimal index_price;
  public BigDecimal initial_margin;
  public String instrument_name;
  public String kind;
  public Integer leverage;
  public BigDecimal maintenance_margin;
  public BigDecimal mark_price;
  public BigDecimal open_orders_margin;
  public BigDecimal realized_funding;
  public BigDecimal realized_profit_loss;
  public BigDecimal settlement_price;
  public BigDecimal size;
  public String size_currency;
  public BigDecimal theta;
  public BigDecimal total_profit_loss;
  public BigDecimal vega;
}

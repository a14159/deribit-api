package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class _Instrument {

  public String base_currency;
  public String quote_currency;
  public String instrument_name;
  public int instrument_id;
  public String instrument_type;
  public String block_trade_commission;
  public Double contract_size;
  public Long creation_timestamp;
  public Long expiration_timestamp;
  public boolean is_active;
  public String kind;
  public Long leverage;
  public String maker_commission;
  public String taker_commission;
  public Double min_trade_amount;
  public String option_type;
  public String settlement_period;
  public String settlement_currency;
  public Double strike;
  public Double tick_size;
}

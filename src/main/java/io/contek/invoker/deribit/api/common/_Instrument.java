package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;
import java.math.BigDecimal;

@NotThreadSafe
public class _Instrument {

  public String base_currency;
  public String quote_currency;
  public String instrument_name;
  public int instrument_id;
  public String block_trade_commission;
  public BigDecimal contract_size;
  public Long creation_timestamp;
  public Long expiration_timestamp;
  public boolean is_active;
  public String kind;
  public Long leverage;
  public String maker_commission;
  public String taker_commission;
  public BigDecimal min_trade_amount;
  public String option_type;
  public String settlement_period;
  public String strike;
  public BigDecimal tick_size;
}

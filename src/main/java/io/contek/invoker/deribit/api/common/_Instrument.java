package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;
import java.math.BigDecimal;

@NotThreadSafe
public class _Instrument {

  public String base_currency;
  public Double block_trade_commission;
  public BigDecimal contract_size;
  public Long creation_timestamp;
  public Long expiration_timestamp;
  public String instrument_name;
  public Boolean is_active;
  public String kind;
  public Long leverage;
  public Double maker_commission;
  public BigDecimal min_trade_amount;
  public String option_type;
  public String quote_currency;
  public String settlement_period;
  public Double strike;
  public Double taker_commission;
  public BigDecimal tick_size;
}

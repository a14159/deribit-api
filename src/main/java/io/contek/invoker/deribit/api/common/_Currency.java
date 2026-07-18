package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

@NotThreadSafe
public class _Currency {

  public String coin_type;
  public String currency;
  public String currency_long;
  public Integer fee_precision;
  public int min_confirmations;
  public Double min_withdrawal_fee;
  public double withdrawal_fee;
  public List<_WithdrawPriority> withdrawal_priorities;
}

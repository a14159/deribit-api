package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class _Deposit {

  public String address;
  public Double amount;
  public String currency;
  public Long received_timestamp;
  public String state;
  public String transaction_id;
  public Long updated_timestamp;
}

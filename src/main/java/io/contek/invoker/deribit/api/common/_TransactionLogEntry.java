package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;

/** Raw exchange transaction. Nullable numbers preserve missing/null versus zero. */
@NotThreadSafe
public class _TransactionLogEntry {

  public Long id;
  public String currency;
  public Long timestamp;
  public Long user_id;
  public Long user_seq;
  public String type;
  public String username;
  public String role;
  /** The exchange sends either structured information, a string, or null. */
  public Object info;
  public String instrument_name;
  public String side;
  public String price_currency;
  public String trade_id;
  public String order_id;
  public String user_role;
  public String fee_role;
  public String ip;
  public Long block_rfq_id;
  public Long starbase_match_id;
  public Long starbase_order_id;
  public Long starbase_timestamp;
  public Boolean profit_as_cashflow;
  public Double commission;
  public Double cashflow;
  public Double balance;
  public Double change;
  public Double equity;
  public Double mark_price;
  public Double settlement_price;
  public Double index_price;
  public Double position;
  public Double amount;
  public Double price;
  public Double contracts;
  /** Funding since the previous trade or position change, not session cumulative. */
  public Double interest_pl;
  /** Funding accumulated since daily settlement at 08:00 UTC; resets each session. */
  public Double total_interest_pl;
  /** Realized session PnL, not funding alone; resets at daily settlement. */
  public Double session_rpl;
  public Double session_upl;
}

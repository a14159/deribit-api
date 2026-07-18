package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

@NotThreadSafe
public class _AccountSummary {

  public double options_gamma;
  public double projected_maintenance_margin;
  public String system_name;
  public Double margin_balance;
  public Boolean tfa_enabled;
  public double options_value;
  public String username;
  public _Limits limits;
  public double equity;
  public double futures_pl;
  public List<_Fee> fees;
  public double options_session_upl;
  public Integer id;
  public double options_vega;
  public String referrer_id;
  public String currency;
  public Boolean login_enabled;
  public String type;
  public double futures_session_rpl;
  public double options_theta;
  public Boolean portfolio_margining_enabled;
  public double projected_delta_total;
  public double session_rpl;
  public double delta_total;
  public double options_pl;
  public double available_withdrawal_funds;
  public double maintenance_margin;
  public double initial_margin;
  public Boolean interuser_transfers_enabled;
  public double futures_session_upl;
  public double options_session_rpl;
  public double available_funds;
  public String email;
  public Long creation_timestamp;
  public double session_upl;
  public double total_pl;
  public double options_delta;
  public double balance;
  public Double projected_initial_margin;
  public String deposit_address;
  public Double total_equity_usd;
}

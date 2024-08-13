package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class _MatchingEngineLimits {

  public _Trading trading;
  public _Limit spot;
  public _Limit maximum_quotes;
  public _Limit maximum_mass_quotes;
  public _Limit guaranteed_mass_quotes;
  public _Limit cancel_all;

  public static class _Trading {
    public _Limit total;
  }
}

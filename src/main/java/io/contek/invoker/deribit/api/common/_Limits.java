package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class _Limits {

  public _Limit non_matching_engine;
  public _MatchingEngineLimits matching_engine;
  public boolean limits_per_currency;
}

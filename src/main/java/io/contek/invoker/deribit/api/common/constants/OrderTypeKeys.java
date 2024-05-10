package io.contek.invoker.deribit.api.common.constants;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class OrderTypeKeys {

  public static final String _all = "all";

  public static final String _limit = "limit";

  public static final String _market = "market";

  public static final String _market_limit = "market_limit";

  public static final String _stop_all = "stop_all";

  public static final String _take_all = "stop_all";

  public static final String _stop_limit = "stop_limit";

  public static final String _take_limit = "take_limit";

  public static final String _stop_market = "stop_market";

  public static final String _take_market = "take_market";

  private OrderTypeKeys() {}
}

package io.contek.invoker.deribit.api.rest.user;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class UserRestApi {

  private final IActor actor;
  private final RestContext context;

  public UserRestApi(IActor actor, RestContext context) {
    this.actor = actor;
    this.context = context;
  }

  public GetEnableCancelOnDisconnect getEnableCancelOnDisconnect() {
    return new GetEnableCancelOnDisconnect(actor, context);
  }

  public GetBuy getBuy() {
    return new GetBuy(actor, context);
  }

  public GetSell getSell() {
    return new GetSell(actor, context);
  }

  public GetEditOrder getEditOrder() {
    return new GetEditOrder(actor, context);
  }

  public GetCancel getCancel() {
    return new GetCancel(actor, context);
  }

  public GetCancelByLabel getCancelByLabel() {
    return new GetCancelByLabel(actor, context);
  }

  public GetCancelAllByInstrument getCancelAllByInstrument() {
    return new GetCancelAllByInstrument(actor, context);
  }

  public GetCancelAll getCancelAll() {
    return new GetCancelAll(actor, context);
  }

  public GetClosePosition getClosePosition() {
    return new GetClosePosition(actor, context);
  }

  public GetDeposits getDeposits() {
    return new GetDeposits(actor, context);
  }

  public GetPosition getPosition() {
    return new GetPosition(actor, context);
  }

  public GetPositions getPositions() {
    return new GetPositions(actor, context);
  }

  public GetOrderHistoryByCurrency getOrderHistoryByCurrency() {
    return new GetOrderHistoryByCurrency(actor, context);
  }

  public GetOrderHistoryByInstrument getOrderHistoryByInstrument() {
    return new GetOrderHistoryByInstrument(actor, context);
  }

  public GetOrderState getOrderState() {
    return new GetOrderState(actor, context);
  }

  public GetUserTradesByOrder getUserTradesByOrder() {
    return new GetUserTradesByOrder(actor, context);
  }

  public GetUserTradesByInstrumentAndTime getUserTradesByInstrumentAndTime() {
    return new GetUserTradesByInstrumentAndTime(actor, context);
  }

  public GetUserTradesByCurrencyAndTime getUserTradesByCurrencyAndTime() {
    return new GetUserTradesByCurrencyAndTime(actor, context);
  }

  public GetAccountSummary getAccountSummary() {
    return new GetAccountSummary(actor, context);
  }

  public GetAccountSummaries getAccountSummaries() {
    return new GetAccountSummaries(actor, context);
  }

  public GetSubAccounts getSubAccounts() {
    return new GetSubAccounts(actor, context);
  }

  public GetOpenOrdersByInstrument getOpenOrdersByInstrument() {
    return new GetOpenOrdersByInstrument(actor, context);
  }
}

package io.contek.invoker.deribit.api.rest.common;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class RestResponse<T> {

  public String jsonrpc;
  public int id;
  public Long usIn;
  public Long usOut;
  public Long usDiff;
  public T result;
}

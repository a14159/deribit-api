package io.contek.invoker.deribit.api.rest.common;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class RestResponse<T> {

  public String jsonrpc;
  public int id;
  public long usIn = -1L;
  public long usOut = -1L;
  public long usDiff = -1L;
  public T result;
}

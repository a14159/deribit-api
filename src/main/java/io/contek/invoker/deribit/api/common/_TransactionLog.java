package io.contek.invoker.deribit.api.common;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

@NotThreadSafe
public class _TransactionLog {

  public List<_TransactionLogEntry> logs;
  /** Exchange pagination token; null when there is no continuation. */
  public Long continuation;
}

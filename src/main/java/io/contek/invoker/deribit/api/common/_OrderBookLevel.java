package io.contek.invoker.deribit.api.common;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONType;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
@JSONType(
    orders = {"price", "amount"},
    deserializeFeatures = JSONReader.Feature.SupportArrayToBean,
    serializeFeatures = JSONWriter.Feature.BeanToArray
)
public class _OrderBookLevel {

  public double price;
  public double amount;
}

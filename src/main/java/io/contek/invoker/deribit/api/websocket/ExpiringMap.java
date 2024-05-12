package io.contek.invoker.deribit.api.websocket;


import java.util.LinkedHashMap;
import java.util.Map;

public final class ExpiringMap<K, V> extends LinkedHashMap<K, V> {

    private final int maxCapacity;


    public ExpiringMap(int maxSize) {
        this.maxCapacity = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }
}

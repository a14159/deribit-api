package is.fm.util;

import java.util.Arrays;
import java.util.function.Consumer;

// See io/netty/util/collection/KObjectHashMap.template
// performance + garbage free (no boxing/unboxing)
public final class ExpiringIntMap<T> {

    private final int maxCapacity;

    private final int[] keys;
    private final int[] keysCnt;
    private final T[] values;
    private final int mask;

    private int size;

    private int seen = 0;

    @SuppressWarnings("unchecked")
    public ExpiringIntMap(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        // size = next power of two
        int capacity = 1 << (32 - Integer.numberOfLeadingZeros(maxCapacity - 1));
        this.mask = capacity - 1;
        keys = new int[capacity];
        keysCnt = new int[capacity];
        values = (T[]) new Object[capacity];
    }

    public T put(int key, T value) {
        int bucket = key & mask;
        int index = bucket;

        final int[] localKeys = keys;
        final int[] localKeysCnt = keysCnt;
        final T[] localVals = values;
        for (;;) {
            if (localVals[index] == null) {
                if (removeEldestEntry()) {
                    return put(key, value);
                }

                // Found empty slot, use it
                localKeys[index] = key;
                localVals[index] = value;
                localKeysCnt[index] = ++seen;
                size++;
                return null;
            }
            if (localKeys[index] == key) {
                // Found existing entry with this key, just replace the value
                T previousValue = localVals[index];
                localVals[index] = value;
                localKeysCnt[index] = ++seen;
                return previousValue;
            }

            // Conflict, keep probing until we reach the starting point
            if ((index = (index + 1) & mask) == bucket) {
                // Can only happen if the map is full
                throw new IllegalStateException("Unable to insert");
            }
        }
    }

    private boolean removeEldestEntry() {
        if (size >= maxCapacity) {
            final int capacity = keys.length;
            final int maxAge = seen - maxCapacity + 1;

            int[] localKeysCnt = keysCnt;
            for (int i = 0; i < capacity; i++) {
                if (localKeysCnt[i] != 0 && localKeysCnt[i] <= maxAge) {
                    remove(keys[i]);
                    seen++; // remove will decrement seen we don't need to do it here
                    return true;
                }
            }
        }

        return false;
    }

    public T remove(int key) {
        int bucket = key & mask;
        int index = bucket;

        final int[] localKeys = keys;
        final int[] localKeysCnt = keysCnt;
        final T[] localVals = values;
        for (;;) {
            if (localVals[index] == null) {
                // slot is available, so no chance that this value exists anywhere in the map.
                return null;
            }
            if (key == localKeys[index]) {
                T rez = localVals[index];
                localVals[index] = null;
                localKeys[index] = 0;
                localKeysCnt[index] = 0;
                seen--;
                size--;

                // Knuth Section 6.4 Algorithm R, also used by the JDK's IdentityHashMap.
                int nextFree = index;
                int i = (index + 1) & mask;
                for (T value = localVals[i]; value != null; value = localVals[i = (i + 1) & mask]) {
                    int key1 = localKeys[i];
                    int keyCnt1 = localKeysCnt[i];
                    int bucket1 = key1 & mask;
                    if (i < bucket1 && (bucket1 <= nextFree || nextFree <= i) ||
                            bucket1 <= nextFree && nextFree <= i) {
                        // Move the displaced entry "back" to the first available position.
                        localKeys[nextFree] = key1;
                        localKeysCnt[nextFree] = keyCnt1;
                        localVals[nextFree] = value;
                        // Put the first entry after the displaced entry
                        localKeys[i] = 0;
                        localKeysCnt[i] = 0;
                        localVals[i] = null;
                        nextFree = i;
                    }
                }

                return rez;
            }

            // Conflict, keep probing until we reach the starting point
            if ((index = (index + 1) & mask) == bucket) {
                return null;
            }
        }
    }

    public T get(int key) {
        int bucket = key & mask;
        int index = bucket;

        final int[] localKeys = keys;
        final T[] localVals = values;
        for (;;) {
            if (localVals[index] == null) {
                // slot is available, so no chance that this value exists anywhere in the map.
                return null;
            }
            if (key == localKeys[index]) {
                return localVals[index];
            }

            // Conflict, keep probing until we reach the starting point
            if ((index = (index + 1) & mask) == bucket) {
                return null;
            }
        }
    }

    public void forEachValue(Consumer<T> consumer) {
        for (T value : values) {
            if (value != null) {
                consumer.accept(value);
            }
        }
    }

    public int size() {
        return size;
    }

    public void clear() {
        Arrays.fill(keys, 0);
        Arrays.fill(keysCnt, 0);
        Arrays.fill(values, null);
        size = 0;
        seen = 0;
    }
}

package is.fm.util.collections;

import java.util.function.Consumer;

public interface BasicIntMap<T> {

    T put(int key, T value);

    T remove(int key);

    T get(int key);

    void forEachValue(Consumer<T> consumer);

    int size();

    void clear();
}

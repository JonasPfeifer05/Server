package util;

import java.util.HashMap;
import java.util.Map;

public class LimitedMap<T, U> {
    private final Map<T, U> storage = new HashMap<>();
    private final int maxSize;

    public LimitedMap(int maxSize) {
        this.maxSize = maxSize;
    }

    public void add(T t, U u) {
        storage.put(t, u);
        if (storage.size() > maxSize) storage.remove(storage.keySet().iterator().next());
    }

    public boolean contains(T t) {
        return storage.containsKey(t);
    }

    public U get(T t) {
        return storage.get(t);
    }

    public U pop(T t) {
        U u = storage.get(t);
        storage.remove(t);

        return u;
    }

    @Override
    public String toString() {
        return storage.toString();
    }
}

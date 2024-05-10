package recipecalc.util;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Similar to SimpleImmutableEntry<K, V>
 * but there are only simpler features.
 */
public class ImmutablePair<K, V> {
    private final K key;
    private final V value;

    /**
     * Creating Pair of Key and Value.
     * @param key key
     * @param value value
     * @throws NullPointerException at trying put {@code key} or {@code value} as null.
     */
    public ImmutablePair(K key, V value) {
        if (key==null || value==null) {
            throw new NullPointerException("can't put Key or Value as null !");
        }
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }

    public boolean equals(ImmutablePair<K, V> pair) {
        return this.key.equals(pair.key) && this.value.equals(pair.value);
    }

    /**
     * make Map from List of Pair
     * @param <K> Any Key
     * @param <V> Any Value
     * @param pairs The original list.
     * @return Map reflecting the argument.
     * @throws IllegalArgumentException at give null as argument.
     */
    public static <K, V> LinkedHashMap<K, V> asMap(List<ImmutablePair<K, V>> pairs) {
        if (pairs==null) {
            throw new IllegalArgumentException("argument is null !");
        }
        if (pairs.isEmpty()) {
            return new LinkedHashMap<>(){};
        } else {
            final LinkedHashMap<K, V> map = new LinkedHashMap<>();
            for (ImmutablePair<K, V> pair : pairs) {
                map.put(pair.key, pair.value);
            }
            return map;
        }
    }
}
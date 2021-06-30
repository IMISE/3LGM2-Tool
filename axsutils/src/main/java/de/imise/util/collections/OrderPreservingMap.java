/**
 *
 */
package de.imise.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.imise.util.pair.Pair;

/**
 * A map that always returns the key values in the order in which they were
 * added.
 *
 * @author AXS (30.06.2021)
 */
public class OrderPreservingMap<K, V> implements Map<K, V> {

    /**
     *
     */
    private final ListSet<MyEntry<K, V>> mapContent;

    /**
    *
    */
    public OrderPreservingMap() {
        mapContent = new ListSet<>();
    }

    /**
    *
    */
    public OrderPreservingMap(final int capacity) {
        mapContent = new ListSet<>(capacity);
    }

    @Override
    public int size() {
        return mapContent.size();
    }

    @Override
    public boolean isEmpty() {
        return mapContent.isEmpty();
    }

    /**
     * @param key
     * @return
     */
    private int indexOfKey(final Object key) {
        for (int i = 0; i < mapContent.size(); i++) {
            MyEntry<K, V> entry = mapContent.get(i);
            K entryKey = entry.getKey();
            if (Objects.equals(key, entryKey)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param value
     * @return
     */
    private int indexOfValue(final Object value) {
        for (int i = 0; i < mapContent.size(); i++) {
            MyEntry<K, V> entry = mapContent.get(i);
            V entryValue = entry.getValue();
            if (Objects.equals(value, entryValue)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean containsKey(final Object key) {
        return indexOfKey(key) >= 0;
    }

    @Override
    public boolean containsValue(final Object value) {
        return indexOfValue(value) >= 0;
    }

    @Override
    public V get(final Object key) {
        int indexOfKey = indexOfKey(key);
        if (indexOfKey < 0) {
            return null;
        }
        MyEntry<K, V> entry = mapContent.get(indexOfKey);
        return entry.getValue();
    }

    @Override
    public V put(final K key, final V value) {
        remove(key);
        MyEntry<K, V> entry = new MyEntry<>(key, value);
        mapContent.add(entry);
        return value;
    }

    @Override
    public V remove(final Object key) {
        int indexOfKey = indexOfKey(key);
        if (indexOfKey < 0) {
            return null;
        }
        MyEntry<K, V> entry = mapContent.remove(indexOfKey);
        return entry.getValue();
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (K key : map.keySet()) {
            V value = map.get(key);
            put(key, value);
        }
    }

    @Override
    public void clear() {
        mapContent.clear();
    }

    @Override
    public Set<K> keySet() {
        return keyListSet();
    }

    /**
     * @return
     */
    public ListSet<K> keyListSet() {
        ListSet<K> keySet = new ListSet<>();
        for (MyEntry<K, V> entry : mapContent) {
            K key = entry.getKey();
            keySet.add(key);
        }
        return keySet;
    }

    @Override
    public Collection<V> values() {
        return valuesList();
    }

    /**
     * @return
     */
    public List<V> valuesList() {
        List<V> values = new ArrayList<>();
        for (MyEntry<K, V> entry : mapContent) {
            V value = entry.getValue();
            values.add(value);
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new HashSet<>(mapContent);
    }

    /**
     * @return
     */
    public List<Entry<K, V>> entryList() {
        return new ArrayList<>(mapContent);
    }

    /**
     * @author AXS (30.06.2021)
     * @param <T>
     * @param <S>
     */
    private static class MyEntry<T, S> extends Pair<T, S> implements Map.Entry<T, S> {

        public MyEntry(final T o1, final S o2) {
            super(o1, o2);
        }

        @Override
        public T getKey() {
            return getFirstItem();
        }

        @Override
        public S getValue() {
            return getSecondItem();
        }

        @Override
        public S setValue(final S value) {
            setSecondItem(value);
            return value;
        }

    }

}

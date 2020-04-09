package de.imise.util.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.imise.util.pair.Pair;

/**
 * Erweiterungs-Klasse der {@link HashMap}.
 *
 * @author fstephan
 * @param <K>
 * @param <V>
 */
public class ExtendedMap<K, V> extends HashMap<K, V> {

    public ExtendedMap(final Map<K, V> map) {
        super(map);
    }
    /**
     * Konstruktor
     * Erzeugt eine neue Map mit den Keys und Values aus <code>keysAndValues</code>.
     *
     * @param keysAndValues
     *            Folge von {@link K}s und jeweiligen {@link V}s.<br>
     *            Form:
     *
     *            <pre>
     * K1, V1, K2, V2, ..., Kn, Vn
     *            </pre>
     */
    @SuppressWarnings("unchecked")
    public ExtendedMap(final Object... keysAndValues) {
        super(keysAndValues.length / 2, 1);

        for (int i = 0; i < keysAndValues.length; i += 2) {
            put((K) keysAndValues[i], (V) keysAndValues[i + 1]);
        }
    }

    /**
     * Gibt ein Array aller Schlüssel-Wert-Paare zurück.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Pair<K, V>[] toPairs() {
        Pair<K, V>[] pairs = new Pair[size()];
        Set<K> keys = keySet();
        int i = 0;
        for (K key : keys) {
            pairs[i] = new Pair<>(key, get(key));
        }
        return pairs;
    }

    @Override
    public String toString() {
        return toString(this);
    }

    public static String toString(final Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("Size=").append(map.size()).append("\n");
        int i = 0;
        for (Object key : map.keySet()) {
            Object value = map.get(key);
            String hash = value == null ? "null" : String.valueOf(value.hashCode());
            sb.append(i++).append(": ").append("key=").append(key).append(" (").append(key.hashCode()).append(")").append("   ->   value=").append(value).append(" (").append(hash).append(")\n");
        }
        return sb.toString();
    }

}

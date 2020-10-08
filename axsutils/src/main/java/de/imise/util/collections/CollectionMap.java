package de.imise.util.collections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Basisklasse für das Mapping auf {@link Collection}s.
 * <p>
 * Es besteht hier die Möglichkeit, den Typ der Collection vorher festzulegen. <br>
 * Darüber hinaus, wird das Erzeugen der Collections automatisch vorgenommen,
 * falls dies erforderlich ist.
 *
 * @param <K>
 *            Die <code>keys</code>
 * @param <E>
 *            Die <code>entries</code> in den Collections
 * @param <C>
 *            Der Typ der Collections
 * @author fstephan
 */
public class CollectionMap<K, E, C extends Collection<E>> extends HashMap<K, C> {

    private final Class<? extends C> collectionType;

    /**
     * Erzeugt eine neue CollectionMap.
     * <p>
     * Die Collections, welche die <code>values</code> innerhalb dieser Map sind, werden
     * anhand des <code>collectionType</code> instanziiert.
     *
     * @param collectionType
     *            Collection-Klasse <b>mit Default-Constructor!</b>
     */
    public CollectionMap(final Class<? extends C> collectionType) {
        super();
        if (!hasDefaultConstructor(collectionType)) {
            throw new IllegalArgumentException("Collection-Klasse benötigt einen Default-Constructor");
        }
        this.collectionType = collectionType;

    }

    /**
     * Erzeugt eine neue CollectionMap.
     * <p>
     * Die Collections, welche die <code>values</code> innerhalb dieser Map sind, werden
     * anhand des <code>collectionType</code> instanziiert.
     *
     * @param initialCapacity
     * @param loadFactor
     * @param collectionType
     *            Collection-Klasse <b>mit Default-Constructor!</b>
     */
    public CollectionMap(final int initialCapacity, final float loadFactor, final Class<? extends C> collectionType) {
        super(initialCapacity, loadFactor);
        if (!hasDefaultConstructor(collectionType)) {
            throw new IllegalArgumentException("Collection-Klasse benötigt einen Default-Constructor");
        }
        this.collectionType = collectionType;
    }

    /**
     * Erzeugt eine neue CollectionMap.
     * <p>
     * Die Collections, welche die <code>values</code> innerhalb dieser Map sind, werden
     * anhand des <code>collectionType</code> instanziiert.
     *
     * @param initialCapacity
     * @param collectionType
     *            Collection-Klasse <b>mit Default-Constructor!</b>
     */
    public CollectionMap(final int initialCapacity, final Class<? extends C> collectionType) {
        super(initialCapacity);
        if (!hasDefaultConstructor(collectionType)) {
            throw new IllegalArgumentException("Collection-Klasse benötigt einen Default-Constructor");
        }
        this.collectionType = collectionType;
    }

    /**
     * Erzeugt eine neue CollectionMap.
     * <p>
     * Die Collections, welche die <code>values</code> innerhalb dieser Map sind, werden
     * anhand des <code>collectionType</code> instanziiert.
     *
     * @param m
     * @param collectionType
     *            Collection-Klasse <b>mit Default-Constructor!</b>
     */
    public CollectionMap(final Map<? extends K, ? extends C> m, final Class<? extends C> collectionType) {
        super(m);
        if (!hasDefaultConstructor(collectionType)) {
            throw new IllegalArgumentException("Collection-Klasse benötigt einen Default-Constructor");
        }
        this.collectionType = collectionType;
    }

    /*
     * Optimierung. Man merkt sich den zuletzt gesuchten Key, um schneller
     * an die Collection zu kommen. Wichtig bei konsekutivem Aufrufen
     * von #putCollectionEntry().
     */
    private static Object illegal = new Object();
    private transient Object lastQueriedKey = illegal;
    private transient C lastRetrievedEntry = null;

    /*
     * (non-Javadoc)
     * @see java.util.HashMap#get(java.lang.Object)
     */
    @Override
    public C get(final Object key) {
        if (key == null) {
            if (lastQueriedKey == null) {
                return lastRetrievedEntry;
            }
        } else {
            if (key == lastQueriedKey || key.equals(lastQueriedKey)) {
                return lastRetrievedEntry;
            }
        }
        lastQueriedKey = key;
        lastRetrievedEntry = super.get(key);
        return lastRetrievedEntry;
    }

    /*
     * (non-Javadoc)
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public C put(final K key, final C value) {
        if (key == null) {
            if (lastQueriedKey == null) {
                lastQueriedKey = illegal;
            }
        } else {
            if (key == lastQueriedKey || key.equals(lastQueriedKey)) {
                lastQueriedKey = illegal;
            }
        }
        return super.put(key, value);
    }

    /*
     * (non-Javadoc)
     * @see java.util.HashMap#remove(java.lang.Object)
     */
    @Override
    public C remove(final Object key) {
        if (key == null) {
            if (lastQueriedKey == null) {
                lastQueriedKey = illegal;
            }
        } else {
            if (key == lastQueriedKey || key.equals(lastQueriedKey)) {
                lastQueriedKey = illegal;
            }
        }
        return super.remove(key);
    }

    /*
     * (non-Javadoc)
     * @see java.util.HashMap#clear()
     */
    @Override
    public void clear() {
        super.clear();
        lastQueriedKey = illegal;
    }

    /**
     * Falls bereits eine Collection für den <code>key</code> existiert, wird
     * der <code>entry</code> zu ihr hinzugefügt. Andernfalls wird zunächst eine
     * neue Collection entsprechend dem {@link #collectionType} angelegt, welche
     * dann als <code>value</code> für den <code>key</code> gesetzt wird. Der
     * <code>entry</code> wird dann ebenfalls der Collection angefügt.
     *
     * @param key
     *            Der Schlüssel für den Wert (die Collection) in dieser Map
     * @param entry
     *            Der Eintrag der dieser Collection hinzugefügt werden soll
     * @return
     *         Die Collection, in der der <code>entry</code> eingefügt wurde.
     */
    public Collection<E> putCollectionEntry(final K key, final E entry) {
        C value = get(key);
        if (value == null) {
            value = createCollection(collectionType);
            put(key, value);
        }
        value.add(entry);
        return value;
    }

    /**
     * Entfernt den <code>entry</code> aus der für den <code>key</code>
     * registrierten Collection, falls vorhanden.
     *
     * @param key
     *            Der Schlüssel für die Collection (<code>value</code>)
     * @param entry
     *            Der Eintrag der aus dieser Collection gelöscht werden soll
     * @return <code>true</code>, wenn für den <code>key</code> eine Collection
     *         registriert ist, die den spezifizierten <code>entry</code> enthält;
     *         <code>false</code>, sonst
     * @see Collection#remove(Object)
     */
    public boolean removeCollectionEntry(final K key, final E entry) {
        C value = get(key);
        if (value == null) {
            return false;
        }
        return value.remove(entry);
    }

    /**
     * Falls bereits eine Collection zu dem <code>key</code> in dieser Map existiert,
     * werden alle Einträge aus <code>entries</code> dieser Collection hinzugefügt.<br>
     * Andernfalls, wird zunächst eine neue Collection erstellt.
     *
     * @param key
     *            Der Schlüssel für die Einträge
     * @param entries
     *            Die Einträge, die für den <code>key</code> registriert werden soll.
     * @return
     *         Der <code>value</code> (die Collection) für den <code>key</code>
     */
    public C putCollectionEntries(final K key, final Collection<E> entries) {
        C value = get(key);
        if (value == null) {
            value = createCollection(collectionType);
            put(key, value);
        }
        value.addAll(entries);
        return value;
    }

    /**
     * Entfernt alle <code>entries</code> aus der für den <code>key</code>
     * registrierten Collection, falls vorhanden.
     *
     * @param key
     *            Der Schlüssel für die Collection (<code>value</code>)
     * @param entries
     *            Der Eintrag der aus dieser Collection gelöscht werden soll
     * @return <code>true</code>, wenn für den <code>key</code> eine Collection
     *         registriert ist, die die spezifizierten <code>entries</code> enthält;
     *         <code>false</code>, sonst
     * @see Collection#removeAll(Collection)
     */
    public boolean removeCollectionEntries(final K key, final Collection<E> entries) {
        C value = get(key);
        if (value == null) {
            return false;
        }
        return value.removeAll(entries);
    }

    /**
     * Registriert alle Einträge über {@link #putCollectionEntries(Object, Collection)}. <br>
     * Alle bestehenden Collections, werden durch die übergebenen erweitert.
     * Bei unbekanntem Schlüssel, wird eine neue Collection erzeugt und dann die
     * Einträge aus der übergebenen Collection angefügt.
     *
     * @param m
     *            Ein beliebige Map, welche auf Collections als <code>value</code>
     *            abbildet.
     */
    public void putAllCollectionEntries(final Map<K, ? extends Collection<E>> m) {
        Set<K> keys = m.keySet();
        for (K key : keys) {
            putCollectionEntries(key, m.get(key));
        }
    }

    /**
     * Entfernt alle <code>entries</code> aus den für die <code>keys</code>
     * registrierten Collections, falls vorhanden.
     *
     * @param m
     *            Ein beliebige Map, welche auf Collections als <code>value</code>
     *            abbildet.
     * @return <code>true</code>, wenn für alle <code>keys</code> eine Collection
     *         registriert ist, die die spezifizierten <code>entries</code> enthält;
     *         <code>false</code>, sonst
     * @see Collection#removeAll(Collection)
     * @see {@link #removeCollectionEntries(Object, Collection)}
     */
    public boolean removeAllCollectionEntries(final Map<K, ? extends Collection<E>> m) {
        Set<K> keys = m.keySet();
        boolean b = true;
        for (K key : keys) {
            b &= removeCollectionEntries(key, m.get(key));
        }
        return b;
    }

    /**
     * Gibt wieder, ob der <code>entry</code> in einer der {@link Collection}s vorhanden ist, welche
     * als <code>values</code> in dieser Map enthalten sind.
     *
     * @param entry
     *            Der gesuchte Eintrag
     * @return <code>true</code>, falls eine Collection in dieser Map existiert, welche den
     *         <code>entry</code> beinhaltet; <code>false</code>, sonst
     */
    public boolean containsCollectionEntry(final E entry) {
        Collection<C> values = values();
        for (Collection<E> value : values) {
            if (value.contains(entry)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clont diese Map und alle darin als <code>values</code> enthalten
     * {@link Collection}s.
     *
     * @return
     *         Eine Kopie dieser Map
     * @throws CloneNotSupportedException
     *             Wird dann geworfen, wenn sich die Collections nicht clonen lassen.
     */
    @SuppressWarnings("unchecked")
    public Object deepClone() throws CloneNotSupportedException {
        if (!collectionType.isAssignableFrom(Cloneable.class)) {
            throwCNSException();
        }
        Method m = null;
        try {
            m = collectionType.getDeclaredMethod("clone", (Class[]) null);
        } catch (SecurityException e) {
            throwCNSException();
        } catch (NoSuchMethodException e) {
            throwCNSException();
        }

        CollectionMap<K, E, Collection<E>> clone = (CollectionMap<K, E, Collection<E>>) super.clone();
        clone.lastQueriedKey = illegal;
        Set<K> keySet = clone.keySet();
        for (K key : keySet) {
            Collection<E> value = get(key);
            Collection<E> collectionClone = null;
            try {
                collectionClone = (Collection<E>) m.invoke(value, (Object[]) null);
            } catch (IllegalArgumentException e) {
                throw new InternalError();
            } catch (IllegalAccessException e) {
                throwCNSException();
            } catch (InvocationTargetException e) {
                throwCNSException();
            }
            clone.put(key, collectionClone);
        }
        return clone;
    }

    private static void throwCNSException() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Die Collections können nicht geclont werden!");
    }

    /**
     * Prüft, ob die übergebene Collection-Klasse einen parameterlosen Konstruktor besitzt, sonst <code>false</code>.
     *
     * @param collectionType
     * @return Liefert <code>true</code>, wenn die übergebene Collection-Klasse einen parameterlosen Konstruktor
     *         besitzt, sonst <code>false</code>.
     */
    private static boolean hasDefaultConstructor(final Class<? extends Collection<?>> collectionType) {
        try {
            collectionType.getConstructor((Class<?>[]) null);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * @param collectionType
     * @return
     */
    private static <V extends Collection<?>> V createCollection(final Class<V> collectionType) {
        try {
            Constructor<V> emptyConstructor = collectionType.getDeclaredConstructor();
            return emptyConstructor.newInstance();
        } catch (Throwable e) {
            throw new InternalError(e);
        }
    }
}

package de.imise.util.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

import de.imise.util.Sys;

/**
 * Stellt Funktionen für Objektsammlungen bereit, die <code>Arrays</code> und <code>Collections</code> nicht bieten.
 *
 * @author AXS
 * @version 0.0.7
 */
public abstract class CollectionUtils {

    /**
     * Liefert einen Namen zurück, der aus dem Prefix und einem mit Leerzeichen abgetrennten
     * Index ab 1 besteht. Der erste Name dieser Form, der nicht in der übergebene <code>Collection</code> vorkommt, wird zurückgegeben.
     *
     * @param prefix
     * @param alreadyExistingNames
     * @return
     */
    public static final String getNextIndicatedName(final String prefix, final Collection<?> alreadyExistingNames) {
        return getNextIndicatedName(prefix, null, alreadyExistingNames);
    }

    public static final String getNextIndicatedName(final String unindicatedName, final Iterable<?> alreadyExistingNames, final boolean brackets, final boolean indicateFirst) {
        if (!indicateFirst) {
            String trimmedUnindicatedName = unindicatedName.trim();
            if (!containsName(trimmedUnindicatedName, alreadyExistingNames, true)) {
                return unindicatedName;
            }
        }
        String namePrefix = brackets ? unindicatedName + "(" : unindicatedName;
        String namePostfix = brackets ? ")" : "";
        String name = getNextIndicatedName(namePrefix, namePostfix, 2, alreadyExistingNames);
        return name;
    }

    /**
     * Liefert einen Namen zurück, der aus dem Prefix und einem Index ab 1 und dem übergebenen Postfix
     * besteht. Der erste Name dieser Form, der nicht in der übergebene <code>Collection</code> vorkommt,
     * wird zurückgegeben.
     *
     * @param prefix
     * @param postfix
     * @param alreadyExistingNames
     * @return
     */
    public static final String getNextIndicatedName(final String prefix, final String postfix, final Iterable<?> alreadyExistingNames) {
        return getNextIndicatedName(prefix, postfix, 1, alreadyExistingNames);
    }

    /**
     * Liefert einen Namen zurück, der aus dem Prefix und einem Index ab 1 und dem übergebenen Postfix
     * besteht. Der erste Name dieser Form, der nicht in der übergebene <code>Collection</code> vorkommt,
     * wird zurückgegeben.
     *
     * @param prefix
     * @param postfix
     * @param startIndex
     * @param alreadyExistingNames
     * @return
     */
    public static final String getNextIndicatedName(String prefix, String postfix, final int startIndex, final Iterable<?> alreadyExistingNames) {
        if (prefix == null) {
            prefix = "";
        }
        if (postfix == null) {
            postfix = "";
        }
        StringBuilder newNameBuilder = new StringBuilder(prefix);
        if (alreadyExistingNames == null) {
            return newNameBuilder.append("1").append(postfix).toString();
        }
        int index = startIndex;
        while (true) {
            newNameBuilder.setLength(prefix.length());
            newNameBuilder.append(index);
            newNameBuilder.append(postfix);
            String newName = newNameBuilder.toString();
            if (!containsName(newName, alreadyExistingNames, false)) {
                return newName;
            }
            index++;
        }
    }

    /**
     * Liefert <code>true</code>, wenn ein Objekt in der Collection alreadyExistingNames über seine toString()-Funktion den
     * übergebenen Namen zurück liefert.
     *
     * @param name
     * @param alreadyExistingNames
     * @param trim
     * @return
     */
    private static final boolean containsName(String name, final Iterable<?> alreadyExistingNames, final boolean trim) {
        if (alreadyExistingNames == null) {
            return false;
        }
        name = trim ? name.trim() : name;
        boolean containsName = false;
        Iterator<?> it = alreadyExistingNames.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o == null) {
                continue;
            }
            String n = trim ? o.toString().trim() : o.toString();
            if (n.equals(name)) {
                containsName = true;
                break;
            }
        }
        return containsName;

    }

    /**
     * Liefert eine Liste der SimpleClassNames der übergebenen Objekte. Bei übergebenen Objekten, die selbst
     * Klassen sind, wird der Name dieser Klasse zurück gegeben.
     *
     * @param elements
     * @return
     */
    public static final <T> Set<String> getSimpleClassNames(final Iterable<T> elements) {
        ImmutableSet.Builder<String> classNames = new ImmutableSet.Builder<>();
        for (T element : elements) {
            Class<?> clazz = element instanceof Class ? (Class<?>) element : element.getClass();
            classNames.add(clazz.getSimpleName());
        }
        return classNames.build();
    }

    /**
     * Fügt zwei Arrays zu einem zusammen. Zuerst kommen die Elemente
     * des zuerst übergebenen Arrays, dann die des zweiten.
     *
     * @param array1
     *            erstes Array
     * @param array2
     *            zweites Array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] joinArrays(final T[] array1, final T[] array2) {
        Object[] retVal = new Object[array1.length + array2.length];
        System.arraycopy(array1, 0, retVal, 0, array1.length);
        System.arraycopy(array2, 0, retVal, array1.length, array2.length);
        return (T[]) retVal;
    }

    /**
     * Fügt zwei Class-Arrays zu einem zusammen. Zuerst kommen die Elemente
     * des zuerst übergebenen Arrays, dann die des zweiten.
     *
     * @param array1
     *            erstes Array
     * @param array2
     *            zweites Array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T>[] joinClassArrays(final Class<? extends T>[] array1, final Class<? extends T>[] array2) {
        Class<T>[] retVal = new Class[array1.length + array2.length];
        System.arraycopy(array1, 0, retVal, 0, array1.length);
        System.arraycopy(array2, 0, retVal, array1.length, array2.length);
        return retVal;
    }

    /**
     * Fügt eine beliebige Anzahl von Arrays mti gleichen Typen zusammen.
     * Diese Funktion ist die abgewandelte Variante aus org.apache.commons.lang3.ArrayUtils.
     *
     * @param arrays
     * @return
     */
    @SafeVarargs
    public static <T> T[] joinArrays(final T[]... arrays) {
        final Class<?> type1 = arrays[0].getClass().getComponentType();
        int fullSize = 0;
        for (int i = 0; i < arrays.length; i++) {
            fullSize += arrays[i].length;
        }
        @SuppressWarnings("unchecked") // OK, because array is of type T
        final T[] joinedArray = (T[]) Array.newInstance(type1, fullSize);
        int offset = 0;
        for (int i = 0; i < arrays.length; i++) {
            int length = arrays[i].length;
            try {
                System.arraycopy(arrays[i], 0, joinedArray, offset, length);
            } catch (final ArrayStoreException ase) {
                // Check if problem was due to incompatible types
                /*
                 * We do this here, rather than before the copy because:
                 * - it would be a wasted check most of the time
                 * - safer, in case check turns out to be too strict
                 */
                final Class<?> type2 = arrays[i].getClass().getComponentType();
                if (!type1.isAssignableFrom(type2)) {
                    throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), ase);
                }
                throw ase; // No, so rethrow original
            }
            offset += length;
        }
        return joinedArray;
    }

    /**
     * @param array
     * @param element
     * @return
     */
    public static boolean arrayContains(final Object[] array, final Object element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param array
     * @param element
     * @return
     */
    public static boolean arrayContains(final int[] array, final int element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert eine <code>Collection</code> aller Elemente deren Klassenname <b>genau</b> dem
     * übergebenen entspricht.
     *
     * @param source
     * @param clazz
     * @return <code>Collection</code> aller Elemente der angegebenen Klasse.
     * @see #getAllElementsOf(Collection, Class, boolean)
     */
    public static final <T> List<T> getAllElementsOf(final Collection<T> source, final Class<T> clazz) {
        return getAllElementsOf(source, clazz, false);
    }

    /**
     * Liefert eine <code>Collection</code> aller Elemente die Insatnz einer Klasse des
     * übergebenen Namens sind.
     *
     * @param source
     * @param clazz
     * @return <code>Collection</code> aller Elemente, die Instanz der angegebenen Klasse sind.
     * @see #getAllElementsOf(Collection, Class, boolean)
     */
    public static final <T> List<T> getAllInstancesOf(final Collection<?> source, final Class<T> clazz) {
        return getAllElementsOf(source, clazz, true);
    }

    /**
     * Gibt wieder, ob die spezifizierte Collection Instanzen der spezifizierten Klasse enthält.
     *
     * @param source
     *            zu durchsuchende Collection
     * @param clazz
     *            gesuchte Klasse
     * @param includeSubClasses
     *            <code>true</code>: auch Unterklassen werden bei der Suche einbezogen
     * @return
     */
    public static boolean containsInstancesOf(final Collection<?> source, final Class<?> clazz, final boolean includeSubClasses) {
        for (Object o : source) {
            if (includeSubClasses) {
                if (clazz.isAssignableFrom(o.getClass())) {
                    return true;
                }
            } else {
                if (clazz.equals(o.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gibt wieder, ob die spezifizierte Collection ausschließlich Instanzen der spezifizierten Klasse enthält.
     *
     * @param source
     *            zu durchsuchende Collection
     * @param clazz
     *            gesuchte Klasse
     * @param includeSubClasses
     *            <code>true</code>: auch Unterklassen werden bei der Suche einbezogen
     * @return
     */
    public static boolean containsOnlyInstancesOf(final Collection<?> source, final Class<?> clazz, final boolean includeSubClasses) {
        for (Object o : source) {
            if (includeSubClasses) {
                if (!clazz.isAssignableFrom(o.getClass())) {
                    return false;
                }
            } else {
                if (!clazz.equals(o.getClass())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gibt wieder, ob die spezifizierte Collection ausschließlich Instanzen der spezifizierten Klassen enthält.
     *
     * @param source
     *            zu durchsuchende Collection
     * @param includeSubClasses
     *            <code>true</code>: auch Unterklassen werden bei der Suche einbezogen
     * @param classes
     *            gesuchte Klassen
     * @return
     */
    public static boolean containsOnlyInstancesOf(final Collection<?> source, final boolean includeSubClasses, final Class<?>... classes) {

        boolean b = false;
        for (Object o : source) {
            if (includeSubClasses) {
                for (Class<?> clazz : classes) {
                    b = b || clazz.isAssignableFrom(o.getClass());
                }
                if (!b) {
                    return false;
                }
                b = false;
            } else {
                for (Class<?> clazz : classes) {
                    b = b || clazz.equals(o.getClass());
                }
                if (!b) {
                    return false;
                }
                b = false;
            }
        }
        return true;
    }

    /**
     * Liefert alle Elemente der angegebenen Klasse, die in der Source-<code>Collection</code> enthalten
     * sind.<br>
     * Wird strict == <code>true</code> uebergeben, dann sind die zurueckgegebenen Elemente
     * unmittelbar Instanzen der Klasse, bei <code>false</code> sind sie Instanzen der Klasse
     * oder Instanzen von abgeleiteten Klassen.
     *
     * @param source
     * @param clazz
     * @param strict
     * @return <code>Collection</code> aller Elemente oder aller Instanzen der angegebenen Klasse
     */
    @SuppressWarnings("unchecked")
    private static final <T> List<T> getAllElementsOf(final Collection<?> source, final Class<T> clazz, final boolean strict) {
        List<T> retList = new ArrayList<>();
        Iterator<?> it = source.iterator();
        if (!strict) {
            while (it.hasNext()) {
                Object o = it.next();
                if (clazz.isInstance(o)) {
                    retList.add((T) o);
                }
            }
        } else {
            while (it.hasNext()) {
                Object o = it.next();
                if (o.getClass().equals(clazz)) {
                    retList.add((T) o);
                }
            }
        }
        return retList;
    }

    /**
     * Liefert eine Immutable-Variante des übergebenen Sets. Ist es bereits immutable, dann kommt das Set selbst zurück.
     *
     * @param original
     * @return
     */
    public static <T> ImmutableSet<T> ensureImmutable(final Set<T> original) {
        if (original == null) {
            return ImmutableSet.of();
        }
        return original instanceof ImmutableSet ? (ImmutableSet<T>) original : ImmutableSet.copyOf(original);
    }

    /**
     * Liefert eine Immutable-Variante der übergebenen List. Ist es bereits immutable, dann kommt die List selbst zurück.
     *
     * @param original
     * @return
     */
    public static <T> ImmutableList<T> ensureImmutable(final List<T> original) {
        if (original == null) {
            return ImmutableList.of();
        }
        return original instanceof ImmutableList ? (ImmutableList<T>) original : ImmutableList.copyOf(original);
    }

    /**
     * Liefert eine Immutable-Variante der übergebenen Map. Ist es bereits immutable, dann kommt die Map selbst zurück.
     *
     * @param original
     * @return
     */
    public static <K, V> ImmutableMap<K, V> ensureImmutable(final Map<K, V> original) {
        if (original == null) {
            return ImmutableMap.of();
        }
        return original instanceof ImmutableMap ? (ImmutableMap<K, V>) original : ImmutableMap.copyOf(original);
    }

    /**
     * Liefert eine Immutable-Variante der übergebenen ListMultimap. Ist es bereits immutable, dann kommt die ListMultimap selbst zurück.
     *
     * @param original
     * @return
     */
    public static <K, V> ImmutableListMultimap<K, V> ensureImmutable(final ListMultimap<K, V> original) {
        if (original == null) {
            return ImmutableListMultimap.of();
        }
        return original instanceof ImmutableListMultimap ? (ImmutableListMultimap<K, V>) original : ImmutableListMultimap.copyOf(original);
    }

    /**
     * Liefert eine Immutable-Variante der übergebenen SetMultimap. Ist es bereits immutable, dann kommt die SetMultimap selbst zurück.
     *
     * @param original
     * @return
     */
    public static <K, V> ImmutableSetMultimap<K, V> ensureImmutable(final SetMultimap<K, V> original) {
        if (original == null) {
            return ImmutableSetMultimap.of();
        }
        return original instanceof ImmutableSetMultimap ? (ImmutableSetMultimap<K, V>) original : ImmutableSetMultimap.copyOf(original);
    }

    /**
     * Liefert eine Immutable-Variante der übergebenen SetMultimap. Ist es bereits immutable, dann kommt die SetMultimap selbst zurück.
     *
     * @param original
     * @return
     */
    public static <K, V> ImmutableMultimap<K, V> ensureImmutable(final Multimap<K, V> original) {
        if (original == null) {
            return ImmutableMultimap.of();
        }
        return original instanceof SetMultimap ? ensureImmutable((SetMultimap<K, V>) original) : ensureImmutable((ListMultimap<K, V>) original);
    }

    /**
     * Gibt eine neue <code>List</code> zurück, die jedes Element aus der übergebenen Liste
     * genau einmal enthält.
     *
     * @param list
     */
    public static final List<?> _getNoMultiplesList(final List<?> list) {
        List<Object> returnList = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (!returnList.contains(o)) {
                returnList.add(o);
            }
        }
        return returnList;
    }

    /**
     * Löscht aus der übergebenen Liste jedes mehrfache Vorkommen des selben Elementes.
     *
     * @param list
     */
    public static final void _removeMultiples(final List<?> list) {
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                if (o == list.get(j)) {
                    list.remove(j--);
                }
            }
        }
    }

    /**
     * Fügt der Sammlung <code>col1</code> alle Elemente aus <code>col2</code> einmal hinzu, die
     * bisher nicht in <code>col1</code> vorkamen.
     *
     * @param col1
     * @param col2
     * @return
     *         col1
     */
    public static final <T> Collection<T> addNonMultiples(final Collection<T> col1, final Collection<T> col2) {
        for (Iterator<T> it = col2.iterator(); it.hasNext();) {
            T o = it.next();
            if (!col1.contains(o)) {
                col1.add(o);
            }
        }
        return col1;
    }

    /**
     * Wandelt <code>rows</code> in ein <code>Object[][]</code> um, falls es sich um eine gültige <code>Collection</code> handelt.<br>
     * Sonst wird <code>null</code> zurückgegeben.
     * <p>
     * <code>rows</code> ist eine gültige <code>Collection</code>, falls ihre Elemente selbst wieder vom Typ {@link Collection} sind und alle die
     * gleiche Elementanzahl besitzen.<br>
     * Das heißt, dass <code>rows</code> eine <code>Collection</code> aller Zeilen sein muss. Außerdem darf <code>rows</code> nicht <code>null</code>
     * sein.
     * <p>
     * Diese Methode bildet dabei die Zeilen und Spalten von <code>rows</code> identisch auf die Zeilen und Spalten des zurückgegebenen Arrays ab.
     *
     * @throws ArrayIndexOutOfBoundsException
     * @throws IllegalArgumentException
     * @param rows
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T[][] toMatrixArray(final Collection<Collection<? extends T>> rows) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {

        if (rows == null) {
            return null;
        }

        T[][] array = null;

        // Zeilen-/Spaltenzähler
        int i = 0, j = 0;

        for (Object nextRow : rows) {
            Collection<? extends T> row = null;
            if (nextRow instanceof Collection<?>) { // rows ist mehrdimensional
                row = (Collection<? extends T>) nextRow;
            } else { // rows ist nicht 2-dimensional
                throw new IllegalArgumentException("The given Collection is not 2 dimensional");
            }

            if (array == null) {
                array = (T[][]) new Object[rows.size()][row.size()];
            }

            for (T nextColumn : row) {
                array[i][j] = nextColumn; // Setzen der Werte im array
                j++;
            }
            j = 0;
            i++;
        }
        return array;
    }

    /**
     * Wandelt das <code>objectArray</code> in ein <code>String[][]</code> um.<br>
     * Dabei wird auf jedes {@link Object} aus <code>objectArray</code> {@link Object#toString()} angewendet und das Resultat an die entsprechende
     * Stelle im <code>String[][]</code> geschrieben.
     * <p>
     * Ist <code>objectArray=null</code> wird <code>null</code> zurückgegeben.
     *
     * @throws ArrayIndexOutOfBoundsException
     * @throws IllegalArgumentException
     * @param objectArray
     * @return
     */
    public static String[][] toStringArray(final Object[]... objectArray) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {

        if (objectArray == null) {
            throw new IllegalArgumentException("Das übergebene Array ist null");
        }

        String[][] stringArray = new String[objectArray.length][objectArray[0].length];

        for (int i = 0; i < objectArray.length; i++) {
            for (int j = 0; j < objectArray[0].length; j++) {
                stringArray[i][j] = objectArray[i][j] != null ? objectArray[i][j].toString() : null;
            }
        }
        return stringArray;
    }

    /**
     * Wandelt das <code>objectArray</code> in ein <code>String[]</code> um.<br>
     * Dabei wird auf jedes {@link Object} aus <code>objectArray</code> {@link Object#toString()} angewendet und das Resultat an die entsprechende
     * Stelle im <code>String[]</code> geschrieben.
     * <p>
     * Ist <code>objectArray=null</code> wird <code>null</code> zurückgegeben.
     *
     * @throws ArrayIndexOutOfBoundsException
     * @throws IllegalArgumentException
     * @param objectArray
     * @return
     */
    public static String[] toStringArray(final Object... objectArray) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {

        if (objectArray == null) {
            throw new IllegalArgumentException("Das übergebene Array ist null");
        }

        String[] stringArray = new String[objectArray.length];

        for (int i = 0; i < objectArray.length; i++) {
            stringArray[i] = objectArray[i] != null ? objectArray[i].toString() : null;
        }

        return stringArray;
    }

    /**
     * @param longs
     * @return
     */
    public static final String toString(final long[] longs) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; longs != null && i < longs.length; i++) {
            sb.append(longs[i]);
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");
        return sb.toString();
    }

    /**
     * @param doubles
     * @return
     */
    public static final String toString(final double[] doubles) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; doubles != null && i < doubles.length; i++) {
            sb.append(doubles[i]);
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");
        return sb.toString();
    }

    /**
     * @param ints
     * @return
     */
    public static final String toString(final int[] ints) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; ints != null && i < ints.length; i++) {
            sb.append(ints[i]);
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");
        return sb.toString();
    }

    /**
     * Liefert eine von eckicken Klammern eingerahmte und durch Kommas separierte Listendarstellung des übergebenen Arrays.
     *
     * @param array
     * @return
     */
    public static final String toListString(final Object[] array) {
        return toString(array, "[", "]", ", ");
    }

    /**
     * Liefert einen String in dem einfach die Rückgabewerte der toString()-Methode aller übergebenen Objects aneinadergehängt wird.
     *
     * @param array
     * @return
     */
    public static final String toJoinedString(final Object[] array) {
        return toString(array, null, null, null);
    }

    /**
     * Allgemeine Funktion um einen Listenausdruck eines Arrays zu bekommen
     *
     * @param array
     *            Das aufzulistende Array
     * @param suffix
     *            Anfang des Rückgabestrings
     * @param postfix
     *            Ende des Rückgabestrings
     * @param delimiter
     *            Trenner zwischen den einzelnen Listenwerten
     * @return
     */
    public static final String toString(final Object[] array, final String suffix, final String postfix, final String delimiter) {
        StringBuilder sb = new StringBuilder();
        if (suffix != null) {
            sb.append(suffix);
        }
        if (array != null) {
            for (Object o : array) {
                sb.append(o);
                if (delimiter != null) {
                    sb.append(delimiter);
                }
            }
            if (delimiter != null && array.length > 0) {
                sb.setLength(sb.length() - delimiter.length());
            }
        }
        if (postfix != null) {
            sb.append(postfix);
        }
        return sb.toString();
    }

    /**
     * Berechnet das Maximum der Größen der {@link Collection}s.<br>
     * Falls keine Collections enthalten sind, wird <code>-1</code> zurückgegeben.
     *
     * @param allCollections
     * @return
     * @throws NullPointerException
     */
    public static int maxSize(final Collection<?>... allCollections) throws NullPointerException {
        int max = 0;
        if (allCollections.length == 0) {
            return -1;
        }
        for (Collection<?> col : allCollections) {
            max = Math.max(max, col.size());
        }
        return max;
    }

    /**
     * Wraps the Iterator with an new Iterable.
     *
     * @param <T>
     * @param iterator
     * @return
     */
    public static <T> Iterable<T> iterable(final Iterator<T> iterator) {
        return () -> iterator;
    }

    /**
     * Wraps the list wit an new Iterable. If the list ist <code>null</code> an empty iterable will be returned.
     *
     * @param <T>
     * @param list
     * @return
     */
    public static <T> Iterable<T> iterable(final List<T> list) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                if (list == null) {
                    return Collections.emptyIterator();
                }
                return list.iterator();
            }
        };
    }

    /**
     * @param iterables
     * @return
     */
    @SafeVarargs
    public static <T> Iterable<T> getCommonIterable(final Iterable<? extends T>... iterables) {
        return () -> new Iterator<T>() {

            int currentIterableIndex = 0;

            Iterator<? extends T> currentIterator = null;

            private void init() {
                if (iterables != null && iterables.length != 0) {
                    if (currentIterator == null) {
                        if (currentIterableIndex < iterables.length) {
                            currentIterator = iterables[currentIterableIndex].iterator();
                            init(); // das muss sein, falls ein Iterator leer ist! Damit wird er übersprungen und nicht hier abgebrochen.
                        }
                    } else if (!currentIterator.hasNext()) {
                        currentIterableIndex++;
                        currentIterator = null;
                        init();
                    }
                }
            }

            @Override
            public boolean hasNext() {
                init();
                return currentIterator != null && currentIterator.hasNext();
            }

            @Override
            public T next() {
                init();
                return currentIterator.next();
            }

            @Override
            public void remove() {
                init();
                currentIterator.remove();
            }
        };
    }

    /**
     * @param iterables
     * @return
     */
    public static <T> Iterable<T> getCommonIterable(final List<Iterable<T>> iterables) {
        return () -> new Iterator<T>() {

            int currentIterableIndex = 0;

            Iterator<? extends T> currentIterator = null;

            private void init() {
                if (iterables != null && iterables.size() != 0) {
                    if (currentIterator == null) {
                        if (currentIterableIndex < iterables.size()) {
                            currentIterator = iterables.get(currentIterableIndex).iterator();
                        }
                    } else if (!currentIterator.hasNext()) {
                        currentIterableIndex++;
                        currentIterator = null;
                        init();
                    }
                }
            }

            @Override
            public boolean hasNext() {
                init();
                return currentIterator != null && currentIterator.hasNext();
            }

            @Override
            public T next() {
                init();
                return currentIterator.next();
            }

            @Override
            public void remove() {
                init();
                currentIterator.remove();
            }
        };
    }

    public static <K, V> Iterable<V> getValuesIterable(final Map<K, Iterable<V>> map) {
        return getValuesIterable(map, null);
    }

    public static <K, V> Iterable<V> getValuesIterable(final Map<K, Iterable<V>> map, final Predicate<K> keyCondition) {
        Set<K> keys = map.keySet();
        ImmutableList.Builder<Iterable<V>> iterables = new ImmutableList.Builder<>();
        for (K key : keys) {
            if (keyCondition == null || keyCondition.test(key)) {
                iterables.add(map.get(key));
            }
        }
        ImmutableList<Iterable<V>> build = iterables.build();
        return getCommonIterable(build);
    }

    /**
     * Liefert ein Iterable-Objekt, das rückwärts durch die gegebene Liste iteriert.
     *
     * @param list
     * @return
     */
    public static final <T> Iterable<T> getBackwardIterable(final List<T> list) {
        return () -> new ListIterator<T>() {

            private final ListIterator<T> originalIterator = list.listIterator(list.size());

            @Override
            public boolean hasNext() {
                return originalIterator.hasPrevious();
            }

            @Override
            public T next() {
                return originalIterator.previous();
            }

            @Override
            public boolean hasPrevious() {
                return originalIterator.hasNext();
            }

            @Override
            public T previous() {
                return originalIterator.next();
            }

            @Override
            public int nextIndex() {
                return originalIterator.previousIndex();
            }

            @Override
            public int previousIndex() {
                return originalIterator.nextIndex();
            }

            @Override
            public void remove() {
                originalIterator.remove();
            }

            @Override
            public void set(final T e) {
                originalIterator.set(e);
            }

            @Override
            public void add(final T e) {
                originalIterator.add(e);
            }
        };
    }

    private enum MapKey {
        ONE,
        TWO,
        THREE,
    }

    private static final Set<MapKey> specialKeys = ImmutableSet.of(MapKey.TWO);

    public static Predicate<MapKey> isNotTwo() {
        return key -> !specialKeys.contains(key);
    };

    @SuppressWarnings("unused")
    private static void testMapIterable() {
        Map<MapKey, Iterable<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("eins");
        list.add("zwei");
        list.add("drei");
        map.put(MapKey.ONE, list);
        list = new ArrayList<>();
        list.add("vier");
        list.add("fünf");
        list.add("sechs");
        map.put(MapKey.TWO, list);
        list = new ArrayList<>();
        list.add("sieben");
        list.add("acht");
        list.add("neun");
        map.put(MapKey.THREE, list);
        for (String s : getValuesIterable(map, isNotTwo())) {
            System.err.println(s);
        }
    }

    private static void testCommonIterable() {
        List<String> strings = new ArrayList<>();
        strings.add("eins");
        strings.add("zwei");
        strings.add("drei");
        List<Integer> ints = new ArrayList<>();
        ints.add(new Integer(1));
        ints.add(new Integer(2));
        ints.add(new Integer(3));
        List<Object> empty = new ArrayList<>();
        List<?>[] lists = {
                strings, empty, ints
        };
        for (Object o : getCommonIterable(strings, empty, ints)) {
            System.err.println(o);
        }
        for (Object o : getCommonIterable(lists)) {
            System.err.println(o);
        }
    }

    public static void main(final String[] args) {
        testCommonIterable();
        //        Sys.err1(new CollectionUtils.Sub(500).clone().toString());
    }

    public static class Super implements Cloneable {

        protected boolean isSuper = true;

        protected Map<String, String> map = new HashMap<>();

        public Super() {
            map.put("Papa", "Alex");
        }

        @Override
        protected Super clone() {
            Super s;
            try {
                s = (Super) super.clone();
            } catch (Exception e) {
                Sys.err1("Fehler bei Super" + e);
                return null;
            }
            return s;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " isSuper=" + isSuper + " " + map.keySet().iterator().next() + " -> " + map.get(map.keySet().iterator().next());
        }
    }

    public static class Sub extends Super {

        public int i = 0;

        public Sub(final int i) {
            this.i = i;
        }

        @Override
        protected Sub clone() {
            Sub s = (Sub) super.clone();
            //            s.map = (Map<String, String>) ((Map<String, String>) map).clone();
            s.map = new HashMap<>(map);
            return s;
        }

        @Override
        public String toString() {
            return super.toString() + " i=" + i;
        }

    }

}

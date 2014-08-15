package de.imise.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
    public static final String getNextIndicatedName(String prefix, String postfix, final Collection<?> alreadyExistingNames) {
        if (prefix == null) {
            prefix = "";
        }
        if (postfix == null) {
            postfix = "";
        }
        if (alreadyExistingNames == null) {
            return prefix + "1" + postfix;
        }
        int index = 0;
        StringBuilder newName = new StringBuilder(prefix);
        while (true) {
            index++;
            newName.setLength(prefix.length());
            newName.append(index);
            newName.append(postfix);
            boolean newNameIsAvailable = true;
            Iterator<?> it = alreadyExistingNames.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (o == null) {
                    continue;
                }
                String n = o.toString();
                if (n.equals(newName.toString())) {
                    newNameIsAvailable = false;
                    break;
                }
            }
            if (newNameIsAvailable) {
                return newName.toString();
            }
        }
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
     * Liefert eine <code>Collection</code> aller Elemente deren Klassenname <b>genau</b> dem
     * übergebenen entspricht.
     * 
     * @param source
     * @param clazz
     * @return <code>Collection</code> aller Elemente der angegebenen Klasse.
     * @see #getAllElementsOf(Collection, Class, boolean)
     */
    public static final <T> ArrayList<T> getAllElementsOf(final Collection<T> source, final Class<T> clazz) {
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
    public static final <T> ArrayList<T> getAllInstancesOf(final Collection<?> source, final Class<T> clazz) {
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
    private static final <T> ArrayList<T> getAllElementsOf(final Collection<?> source, final Class<T> clazz, final boolean strict) {
        ArrayList<T> retList = new ArrayList<T>();
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
     * Gibt eine neue <code>ArrayList</code> zurück, die jedes Element aus der übergebenen Liste
     * genau einmal enthält.
     * 
     * @param list
     */
    public static final ArrayList<?> _getNoMultiplesList(final ArrayList<?> list) {
        ArrayList<Object> returnList = new ArrayList<Object>(list.size());
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
    public static final void _removeMultiples(final ArrayList<?> list) {
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
     * @param row
     * @return
     * @throws ArrayIndexOutOfBoundsException
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(final Collection<? extends T> row) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {

        if (row == null) {
            return null;
        }
        row.toArray();

        int n = row.size();
        T[] array = (T[]) new Object[n];

        // Zeilen-/Spaltenzähler
        int i = 0;
        for (T value : row) {
            array[i] = value;
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
    public static String[][] toStringArray(final Object[][] objectArray) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {

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
    public static String[] toStringArray(final Object[] objectArray) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {

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
     * @param iterables
     * @return
     */
    public static <T> Iterable<T> getCommonIterable(final Iterable<?>... iterables) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    int actualIterableIndex = 0;

                    @Override
                    public boolean hasNext() {
                        if (iterables == null || actualIterableIndex == iterables.length) {
                            return false;
                        }
                        if (iterables[actualIterableIndex].iterator().hasNext()) {
                            return true;
                        }
                        actualIterableIndex++;
                        return hasNext();
                    }

                    @Override
                    public T next() {
                        return (T) iterables[actualIterableIndex].iterator().next();
                    }

                    @Override
                    public void remove() {
                        iterables[actualIterableIndex].iterator().remove();
                    }
                };
            }
        };
    }

}

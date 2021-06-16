package de.imise.util;

import de.imise.util.pair.Pair;

/**
 * Hilfsklasse, um Objekte zu kapseln, deren <code>toString()</code>-Methode
 * nicht das zurückliefert, was angezeigt werden soll. Dieses Objekt speichert
 * das Originalobjekt und zusätzlich einen String, der über
 * <code>toString()</code> zurückgeliefert wird.
 *
 * @author AXS
 * @created 17.10.2007
 */
public class NamedObjectContainer<E> extends Pair<E, String> {

    /**
     * Wenn <code>true</code>, reicht bei {@link #equals(Object)} für Gleichheit
     * Identität oder dass beide {@link #toString()}-Funktionen dasselbe
     * liefern. Wenn <code>false</code> wird auch geprüft, ob das andere Objekt
     * ebenfalls ein {@link NamedObjectContainer} ist und die enthaltenen
     * Objekte auch equals ist.
     */
    protected final boolean equalsIfToStringIsEquals;

    /**
     * Legt ein Obekt an, das über die <code>toString()</code> -Methode den
     * <code>toString</code> zurück liefert und zusätzlich das Objekt
     * <code>modelElement</code> speichert.
     *
     * @param modelElement
     * @param toString
     */
    public NamedObjectContainer(final E object, final String toString) {
        this(object, toString, false);
    }

    /**
     * Legt ein Obekt an, das über die <code>toString()</code> -Methode den
     * <code>toString</code> zurück liefert und zusätzlich das Objekt
     * <code>modelElement</code> speichert.
     *
     * @param modelElement
     * @param toString
     * @param equalsIfToStringIsEquals Wenn <code>true</code>, reicht bei
     *            {@link #equals(Object)} für Gleichheit Identität oder dass
     *            beide {@link #toString()}-Funktionen dasselbe liefern. Wenn
     *            <code>false</code> wird auch geprüft, ob das andere Objekt
     *            ebenfalls ein {@link NamedObjectContainer} ist und die
     *            enthaltenen Objekte auch equals ist.
     */
    public NamedObjectContainer(final E object, final String toString, final boolean equalsIfToStringIsEquals) {
        super(object, toString);
        this.equalsIfToStringIsEquals = equalsIfToStringIsEquals;
    }

    /**
     * Liefert einen {@link NamedObjectContainer} mit dem Object object und dem
     * String toString.
     *
     * @param object
     * @param toString
     * @return
     */
    public static <T> NamedObjectContainer<T> of(final T object, final String toString) {
        return new NamedObjectContainer<>(object, toString);
    }

    @Override
    public String toString() {
        return String.valueOf(getSecondItem());
    }

    /**
     * @return Liefert das <code>object</code>
     */
    public E getObject() {
        return getFirstItem();
    }

    /**
     * @return Liefert den <code>String</code>
     */
    public String getString() {
        return getSecondItem();
    }

    /**
     * @param o
     * @param type
     * @return <code>true</code> if the Object o is a
     *         {@link NamedObjectContainer} and the type of the contained object
     *         is assignable from the given type.
     */
    public static final boolean isInstanceWithType(final Object o, final Class<?> type) {
        if (o instanceof NamedObjectContainer) {
            NamedObjectContainer<?> instance = (NamedObjectContainer<?>) o;
            if (instance.hasObjectType(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param type
     * @return
     */
    public boolean hasObjectType(final Class<?> type) {
        E object = getObject();
        if (object == null) {
            return false;
        }
        Class<? extends Object> objectType = object.getClass();
        return type.isAssignableFrom(objectType);
    }

    //AXS: 16.06.2021: ab jetzt lasse ich das ändern zu, weil eigentlich nichts dagegen spricht und ich es brauche
    //    /**
    //     * Guaranteed to throw an exception and leave the list unmodified.
    //     *
    //     * @throws UnsupportedOperationException always
    //     * @deprecated Unsupported operation.
    //     */
    //    @CanIgnoreReturnValue
    //    @Deprecated
    //    @Override
    //    public final void setFirstItem(final E o) {
    //        throw new UnsupportedOperationException();
    //    }
    //
    //    /**
    //     * Guaranteed to throw an exception and leave the toStringValue unmodified.
    //     *
    //     * @throws UnsupportedOperationException always
    //     * @deprecated Unsupported operation.
    //     */
    //    @CanIgnoreReturnValue
    //    @Deprecated
    //    @Override
    //    public void setSecondItem(final String o) {
    //        throw new UnsupportedOperationException();
    //    }
    //
    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof NamedObjectContainer)) {
            return false;
        }
        NamedObjectContainer<?> otherNoc = (NamedObjectContainer<?>) other;
        if (!(equalsIfToStringIsEquals && otherNoc.equalsIfToStringIsEquals)) {//wenn nicht nur der String-Wert sondern auch die Elementklasse und das enthaltene Objekt auch getestet werden soll
            if (firstObject == null) {
                if (otherNoc.firstObject != null) {
                    return false;
                }
            } else if (!firstObject.equals(otherNoc.firstObject)) {
                return false;
            }
        }
        if (!toString().equals(other.toString())) {
            return false;
        }
        return true;
    }

}

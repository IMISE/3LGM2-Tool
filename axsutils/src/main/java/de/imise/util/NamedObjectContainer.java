package de.imise.util;

/**
 * Hilfsklasse, um Objekte zu kapseln, deren <code>toString()</code>-Methode nicht
 * das zurückliefert, was angezeigt werden soll. Dieses Objekt speichert das
 * Originalobjekt und zusätzlich einen String, der über <code>toString()</code> zurückgeliefert wird.
 *
 * @author AXS
 * @created 17.10.2007
 */
public final class NamedObjectContainer<E> {

    /**
     * <code>String</code> der in der Liste angezeigt wird. <br>
     * Dies ist also der String, der über <code>toString()</code> zurück
     * gegeben wird.
     */
    protected final String toString; //ACHTUNG: niemals unfinalizen und set() hierfür schreiben!

    /**
     * Das Objekt das durch den angezeigten String dargestellt wird
     */
    protected final E object; //ACHTUNG: niemals unfinalizen und set() hierfür schreiben!

    /**
     * Wenn <code>true</code>, reicht bei {@link #equals(Object)} für Gleichheit Identität oder dass beide {@link #toString()}-Funktionen dasselbe
     * liefern. Wenn <code>false</code> wird auch geprüft, ob das andere Objekt ebenfalls ein {@link NamedObjectContainer} ist und die enthaltenen
     * Objekte auch equals ist.
     */
    protected final boolean equalsIfToStringIsEquals;

    /**
     * Legt ein Obekt an, das über die <code>toString()</code> -Methode den <code>toString</code> zurück liefert und zusätzlich das Objekt
     * <code>modelElement</code> speichert.
     *
     * @param modelElement
     * @param toString
     */
    public NamedObjectContainer(final E object, final String toString) {
        this(object, toString, false);
    }

    /**
     * Legt ein Obekt an, das über die <code>toString()</code> -Methode den <code>toString</code> zurück liefert und zusätzlich das Objekt
     * <code>modelElement</code> speichert.
     *
     * @param modelElement
     * @param toString
     * @param equalsIfToStringIsEquals
     *            Wenn <code>true</code>, reicht bei {@link #equals(Object)} für Gleichheit Identität oder dass beide {@link #toString()}-Funktionen
     *            dasselbe liefern. Wenn <code>false</code> wird auch geprüft, ob das andere Objekt ebenfalls ein {@link NamedObjectContainer} ist und
     *            die enthaltenen Objekte auch equals ist.
     */
    public NamedObjectContainer(final E object, final String toString, final boolean equalsIfToStringIsEquals) {
        this.toString = toString;
        this.object = object;
        this.equalsIfToStringIsEquals = equalsIfToStringIsEquals;
        //System.err.println(getFullString(this));
    }

    /**
     * Liefert einen {@link NamedObjectContainer} mit dem Object object und dem String toString.
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
        return toString;
    }

    /**
     * @return Liefert das <code>object</code>
     */
    public E getObject() {
        return object;
    }

    @Override
    public int hashCode() {
        if (toString == null && object == null) {
            return super.hashCode();
        }
        if (toString == null) {
            return object.hashCode();
        }
        if (object == null) {
            return toString.hashCode();
        }
        return toString.hashCode() * object.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        if (!equalsIfToStringIsEquals) {//wenn nicht nur der String-Wert sondern auch die Elementklasse und das enthaltene Objekt auch getestet werden soll
            if (!(obj instanceof NamedObjectContainer)) {
                return false;
            }
            if (((NamedObjectContainer<?>) obj).getObject().equals(getObject())) {
                return false;
            }
        }
        if (!toString().equals(obj.toString())) {
            return false;
        }
        return true;
    }

    public final String getFullString() {
        return "NamedObjectContainer(" + object + ", \"" + toString + "\")";
    }

}

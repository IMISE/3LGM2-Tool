package de.imise.util;

/**
 * Hilfsklasse, um Objekte zu kapseln, deren <code>toString()</code>-Methode nicht
 * das zurückliefert, was angezeigt werden soll. Dieses Objekt speichert das
 * Originalobjekt und zusätzlich einen String, der über <code>toString()</code> zurückgeliefert wird.
 * 
 * @author AXS
 * @created 17.10.2007
 */
public class NamedObjectContainer<E> {

    /**
     * <code>String</code> der in der Liste angezeigt wird. <br>
     * Dies ist also der String, der über <code>toString()</code> zurück
     * gegeben wird.
     */
    protected final String toString;

    /**
     * Das Objekt das durch den angezeigten String dargestellt wird
     */
    protected final E object;

    /**
     * Legt ein Obekt an, das über die <code>toString()</code> -Methode den <code>toString</code> zurück liefert und zusätzlich das Objekt
     * <code>modelElement</code> speichert.
     * 
     * @param modelElement
     * @param toString
     */
    public NamedObjectContainer(final E object, final String toString) {
        this.toString = toString;
        this.object = object;
    }

    /**
     * Liefert einen {@link NamedObjectContainer} mit dem Object object und dem String toString.
     * 
     * @param object
     * @param toString
     * @return
     */
    public static <T> NamedObjectContainer<T> of(final T object, final String toString) {
        return new NamedObjectContainer<T>(object, toString);
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
        if (!(obj instanceof NamedObjectContainer)) {
            return false;
        }
        if (!toString().equals(obj.toString())) {
            return false;
        }
        if (((NamedObjectContainer<?>) obj).getObject() != getObject()) {
            return false;
        }
        return true;
    }
}

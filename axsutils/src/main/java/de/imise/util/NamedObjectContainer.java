package de.imise.util;

/**
 * Hilfsklasse, um Objekte zu kapseln, deren <code>toString()</code>-Methode nicht
 * das zurückliefert, was angezeigt werden soll. Dieses Objekt speichert das 
 * Originalobjekt und zusätzlich einen String, der über <code>toString()</code>
 * zurückgeliefert wird.
 * 
 * @author AXS
 * 
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
	 * Legt ein Obekt an, das über die <code>toString()</code> -Methode den
	 * <code>toString</code> zurück liefert und zusätzlich das Objekt
	 * <code>modelElement</code> speichert.
	 * 
	 * @param modelElement
	 * @param toString
	 */
	public NamedObjectContainer(E object, String toString) {
		this.toString = toString;
		this.object = object;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
//	    assert false : "hashCode not designed";
	   // return 42; // any arbitrary constant will do 
		return super.hashCode();
	}

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
	@Override
    public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof NamedObjectContainer))
			return false;
		if (!toString().equals(obj.toString()))
			return false;
		if (((NamedObjectContainer<?>) obj).getObject() != getObject())
			return false;
		return true;
	}
}

package de.imise.util;

/**
 * Repräsentation eines geordneten Triples.
 * 
 * @author fstephan
 *
 * @param <S>
 * @param <T>
 * @param <V>
 */
public class Triple <S,T,V> {
	
	/** erstes Element */
	private S e1;
	
	/** zweites Element */
	private T e2;
	
	/** drittes Element */
	private V e3;
	
	public Triple() {}
	
	public Triple (S e1, T e2, V e3) {
		this.e1 = e1;
		this.e2 = e2;
		this.e3 = e3;
	}
	
	/** 
	 * Setzt das erste Element auf <code>e</code>.
	 * @param e
	 */
	public void setFirstElement(S e) {
		e1 = e;
	}
	
	/** 
	 * Setzt das zweite Element auf <code>e</code>.
	 * @param e
	 */
	public void setSecondElement(T e) {
		e2 = e;
	}
	
	/** 
	 * Setzt das dritte Element auf <code>e</code>.
	 * @param e
	 */
	public void setThirdElement(V e) {
		e3 = e;
	}
	
	/**
	 * Gibt das erste Element wieder.
	 * @return {@link #e1}
	 */
	public S getFirstElement() {
		return e1;
	}

	/**
	 * Gibt das zweite Element wieder.
	 * @return {@link #e2}
	 */
	public T getSecondElement() {
		return e2;
	}
	
	/**
	 * Gibt das dritte Element wieder.
	 * @return {@link #e3}
	 */
	public V getThirdElement() {
		return e3;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object that) {
		if(that == this)
			return true;
		if(!(that instanceof Triple))
			return false;
		Triple<?,?,?> t = (Triple<?,?,?>) that;
		
		if (e1 == t.e1 && e2 == t.e2 && e3 == t.e3)
			return true;
		
		if (e1 == null || e2 == null || e3 == null)
			return false;
		
		return (e1.equals(t.e1) && e2.equals(t.e2) && e3.equals(t.e3));
	}

}

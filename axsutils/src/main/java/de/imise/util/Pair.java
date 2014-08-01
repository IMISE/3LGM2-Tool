/*
 * Created on 23.04.2004
 */
package de.imise.util;

/**
 * @author AXS
 */
public class Pair<T, S> {
	/**
	 * COMMENTME
	 */
	protected T firstObject;
	
	/**
	 * COMMENTME
	 */
	protected S secondObject;

	/**
	 * @param o
	 */
	public void setFirstItem(T o) {
		firstObject = o;
	}

	/**
	 * @param o
	 */
	public void setSecondItem(S o) {
		secondObject = o;
	}

	/**
	 * @param o1
	 * @param o2
	 * @param i
	 */
	public Pair(T o1, S o2) {
		firstObject = o1;
		secondObject = o2;
	}

	/**
	 * @return
	 */
	public T getFirstItem() {
		return firstObject;
	}

	/**
	 * @return
	 */
	public S getSecondItem() {
		return secondObject;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof Pair))
			return false;
		Pair<?, ?> op = (Pair<?, ?>) obj;
		if (op.firstObject == firstObject && op.secondObject == secondObject)
			return true;
		if (firstObject == null || secondObject == null)
			return false;
		return firstObject.equals(op.firstObject) && secondObject.equals(op.secondObject);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "firstObject:" + firstObject.toString() + ";secondObject:" + secondObject.toString();
	}

}

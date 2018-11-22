package de.imise.util.collections;

import java.util.ArrayList;

/**
 * Erweiterungsklasse zur {@link ArrayList}
 * @author fstephan
 *
 * @param <E>
 */
public class ExtendedList<E> extends ArrayList<E> {
	
	public ExtendedList(E... es) {
		this();
		for (E e : es)
			add(e);
	}
	
	public ExtendedList() {
		super();
	}
	
	public void setLastElement(E e) {
		set(size()-1,e);
	}
	
	public boolean removeLastElement() {
		int n = size();
		if(n>0) {
			remove(size()-1);
			return true;
		}
		return false;
	}
	
	public E getLastElement() {
		if (isEmpty())
			return null;
		return get(size()-1);
	}
	
	public void addAll(E...es) {
		for (E e : es)
			add(e);
	}
}

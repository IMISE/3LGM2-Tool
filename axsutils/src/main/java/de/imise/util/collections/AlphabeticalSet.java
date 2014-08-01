/*
 * Created on 26.10.2007
 */
package de.imise.util.collections;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.imise.util.Alphabetical;


/**
 * Set dessen Elemente immer alphabetisch sortiert sind.<br>
 * Kein Element kommt doppelt vor und <code>null</code> ist als Element erlaubt.
 * Zugriffszeit ist immer log(n). Vorteil gegenüber <code>HashSet</code>
 * ist, dass die Reihenfolge der Elemente immer gleich ist, was das debuggen erleichtert.
 * 
 * @author AXS
 */
public class AlphabeticalSet<E> extends AbstractSet<E> implements Cloneable {

	/**
	 * Liste mit den eigentlichen Elementen
	 */
	private ArrayList<E>elements;
	
	/**
	 * 
	 */
	public AlphabeticalSet() {
		super();
		elements = new ArrayList<E>();
	}
	/**
	 * @param initialCapacity
	 */
	public AlphabeticalSet(int initialCapacity) {
		super();
		elements = new ArrayList<E>(initialCapacity);
	}
	/**
	 * @param arg0
	 */
	public AlphabeticalSet(Collection<? extends E> arg0) {
		this(arg0.size());
		addAll(arg0);
	}
	
	/**
	 * @param arg0
	 * @return
	 */
	private int getInsertPosition(Object arg0){
		//hole die Position an der das Element eingefügt werden müsste
		int pos = Alphabetical.binarySearch(elements, arg0);
		//es kommt bisher kein Element mit demselben Namen in der Liste vor
		if (pos<0)
			return pos;

		//es kommt schon mind. ein Element mit derselben toString() vor

		//wenn das Object mit dem gleichen Namen das übergebene ist
		if (elements.get(pos)==arg0)
			return pos;

		String name = arg0.toString();
		int p = pos;

		//solange nach vorne alle gleichbenannten Elemente durchsuchen, ob das übergebene schon dabei ist
		while (--p>0){
			Object element = elements.get(p);
			//wenn das Element vor dem gleichbenannten nicht mehr genauso heißt -> Suche nach vorne abbrechen
			if (!element.toString().equals(name))
				break;
			//wenn das Element identisch ist
			if (element==arg0)
				return p;
		}
		//solange nach hinten alle gleichbenannten Elemente durchsuchen, ob das übergebene schon dabei ist
		p = pos;
		int elementsSize = elements.size();
		while (++p<elementsSize){
			Object element = elements.get(p);
			//wenn das Element vor dem gleichbenannten nicht mehr genauso heißt -> Suche nch hinten abbrechen
			if (!element.toString().equals(name))
				break;
			//wenn das Element identisch ist
			if (element==arg0)
				return p;
		}
		//das Element ist noch nicht enthalten -> Rückgabe wie bei Collections.binarySearch()
		return -p-1;
	}
	
	////////////////////////////////////
	// Methoden aus dem Interface Set //
	////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() {
		return elements.size();
	}
	/* (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return elements.size()==0;
	}
	/* (non-Javadoc)
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return getInsertPosition(o)>=0;
	}
	/* (non-Javadoc)
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator <E>iterator() {
		return elements.iterator();
	}
	/* (non-Javadoc)
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() {
		return elements.toArray();
	}
	/* (non-Javadoc)
	 * @see java.util.Set#toArray(java.lang.Object[])
	 */
	@Override
	public <T> T[] toArray(T[] arg0) {
		return elements.toArray(arg0);
	}
	/* (non-Javadoc)
	 * @see java.util.Set#add(java.lang.Object)
	 */
	@Override
	public boolean add(E arg0) {
		//hole die Position an der das Element eingefügt werden müsste
		int pos = getInsertPosition(arg0);
		
		//es kommt bisher kein Element mit demselben Namen in der Liste vor
		if (pos < 0) {
			//die Position auf den richtigen Wert zurückrechnen (siehe Collections.binarySearch())
			elements.add(-pos - 1, arg0);
			return true;
		}
		//das Element kommt bereits in der Liste vor
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return elements.remove(o);
	}
	/* (non-Javadoc)
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> arg0) {
		return elements.containsAll(arg0);
	}
	/* (non-Javadoc)
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		boolean b = true;
		for (E next : arg0)
			if (!add(next))
				b = false;
		return b;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> arg0) {
		return elements.retainAll(arg0);
	}
	/* (non-Javadoc)
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> arg0) {
		return elements.removeAll(arg0);
	}
	/* (non-Javadoc)
	 * @see java.util.Set#clear()
	 */
	@Override
	public void clear() {
		elements.clear();
	}

	//////////////////////////////////
	// Stellvertreter aus ArrayList //
	//////////////////////////////////
	
	/**
	 * @param minCapacity
	 */
	public void ensureCapacity(int minCapacity) {
		elements.ensureCapacity(minCapacity);
	}
	/**
	 * @param index
	 * @return
	 */
	public E get(int index) {
		return elements.get(index);
	}
	/**
	 * @param o
	 * @return
	 */
	public int indexOf(Object o) {
		return elements.indexOf(o);
	}
	/**
	 * @return
	 */
	public ListIterator<E> listIterator() {
		return elements.listIterator();
	}
	/**
	 * @param index
	 * @return
	 */
	public ListIterator<E> listIterator(int index) {
		return elements.listIterator(index);
	}
	/**
	 * @param index
	 * @return
	 */
	public Object remove(int index) {
		return elements.remove(index);
	}
	/**
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public List<E> subList(int fromIndex, int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}
	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#toString()
	 */
	@Override
	public String toString() {
		return elements.toString();
	}
	/**
	 * 
	 */
	public void trimToSize() {
		elements.trimToSize();
	}

	/**
	 * Clont dieses Set. Die enthaltenen Elemente werden dabei nicht geclont. 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public AlphabeticalSet<E> clone() {
		try {
	        AlphabeticalSet<E> clone = (AlphabeticalSet<E>) super.clone();
	        clone.elements = (ArrayList<E>) elements.clone();
	       	return clone;
        } catch (CloneNotSupportedException e) {
	        throw new InternalError();
        }
	}
}

package de.imise.util.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Implemenation einer Multimenge.<p>
 * 
 * Elemente können hier mehrfach enthalten sein.<br>
 * Beim Vergleich zweier {@link MultiSet}s, wird die Reihenfolge der Elemente NICHT beachtet.
 * 
 * 
 * @author fstephan
 *
 * @param <E>
 * 		Typ der enthaltenen Elemente
 */
public class MultiSet<E> extends ArrayList<E> implements Set<E> {

	/**
	 * Gibt wieder ob das übergebene Objekt ebenfalls ein {@link MultiSet} ist und
	 * beide die gleichen Elemente enthalten.<br>
	 * Im Unterschied zur {@link List} wird deren Reihenfolge wird hier nicht beachtet.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public boolean equals(Object o) {
    	
    	if (!(o instanceof MultiSet))
    		return false;
    	
    	MultiSet list = (MultiSet) o;
    	
    	if (size() != list.size())
    		return false;
    	
    	MultiSet disjunction = (MultiSet) clone();
    	disjunction.removeAll(list);
    	
    	return disjunction.isEmpty();
    }
	
    /**
     * Gibt die Zahl der Verkommen des spezifizierten Elementes in dieser Multimenge wieder.
     */
    public int frequency(E element) {
    	return Collections.frequency(this, element);
    }
}

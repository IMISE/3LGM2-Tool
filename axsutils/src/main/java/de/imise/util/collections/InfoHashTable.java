package de.imise.util.collections;

import java.util.Hashtable;
import java.util.Map;

/**
 *
 * Dient nur zur Überwachung des Inhaltes eines HashTables, falls man mit debuggen nicht mehr weiter
 * kommt. Bei jeder Änderung des Inhaltes wird der Table ausgegeben.
 *  
 * @author AXS
 * Created on 18.04.2008
 */

public class InfoHashTable<K,V> extends Hashtable<K,V>  {

	Object owner = null;
	
	/**
	 * 
	 */
	public InfoHashTable(Object owner, int i) {
		super();
		if (owner==null)
			this.owner = "";
		else
			this.owner = owner;
	}
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public synchronized void clear() {
		System.err.println(owner);
		System.err.println("InfoHashTable.clear()");
		super.clear();
	}
	/* (non-Javadoc)
	 * @see java.util.Dictionary#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public synchronized V put(final K arg0, final V arg1) {
		System.err.println(owner);
		V o  = super.put(arg0, arg1);
		System.err.println("InfoHashTable.put(): key=" + arg0 + "\tvalue=" + arg1 + "\told value=" + o);
		System.err.println(this);
		return o;
	}
	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public synchronized void putAll(final Map<? extends K, ? extends V> t) {
		System.err.println(owner);
		System.err.println("InfoHashTable.putAll() - newValue");
		System.err.println(t);
		System.err.println("InfoHashTable.putAll() - oldTable");
		System.err.println(this);
		super.putAll(t);
		System.err.println("InfoHashTable.putAll() - newTable");
		System.err.println(this);
	}
	/* (non-Javadoc)
	 * @see java.util.Dictionary#remove(java.lang.Object)
	 */
	@Override
	public synchronized V remove(final Object key) {
		System.err.println(owner);
		System.err.println("InfoHashTable.remove() - oldTable");
		System.err.println(this);
		V o = super.remove(key);
		System.err.println("InfoHashTable.remove(): key=" + key + "\tvalue=" + o);
		System.err.println("InfoHashTable.remove() - newTable");
		System.err.println(this);
		return o;
	}
}

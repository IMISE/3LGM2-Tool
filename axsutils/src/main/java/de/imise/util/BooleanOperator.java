package de.imise.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementierung grundlegender Mengenoperationen.
 * 
 * @author fstephan
 */
public enum BooleanOperator {
	
	
	/**
	 * Disjunktion
	 */
	AND {
		@Override
        public <E, T extends Set<E>> void calculate(T s1, T s2, T target) {
			prepareTarget(s1, target);
			target.retainAll(s2);
        }
    },
    
    /**
     * Konjunktion
     */
	OR {
        @Override
        public <E, T extends Set<E>> void calculate(T s1, T s2, T target) {
        	prepareTarget(s1, target);
        	target.addAll(s2);
        }
    },
    
    /**
     * Kontravalenz
     */
	XOR {
	    @Override
	    public <E, T extends Set<E>> void calculate(T s1, T s2, T target) {
	    	HashSet<E> disjunction = new HashSet<E>(s1.size() + s2.size());
	    	AND.calculate(s1, s2, disjunction);
	    	OR.calculate(s1, s2, target);
	    	target.removeAll(disjunction);
	    }
    },
    
    /**
     * Komplement
     */
	NOT {
    	
    	/**
    	 * Berechnet das Komplement der Menge <i>s1</i> bezüglich des Universums <i>s2</i>
    	 * @see BooleanOperator#calculate(Set, Set, Set)
    	 */
	    @Override
	    public <E, T extends Set<E>> void calculate(T s1, T s2, T target) {
		    prepareTarget(s2, target);
		    target.removeAll(s1);
	    }
    };
	
    /**
     * Führt die jeweilige boolsche Operation auf den Elementen in <i>s1</i> und <i>s2</i> aus und
     * speichert das Resultat. 
     * <p>
     * Die {@link Set}s <i>s1</i> und <i>s2</i> werden dabei nicht verändert.
     * 
     * @param s1
     * 			Menge 1.
     * @param s2
     * 			Menge 2.
     * @param target
     * 			Menge in der das Resultat gespeichert wird.
     */
	public abstract <E, T extends Set<E>> void calculate(T s1, T s2, T target);
	
	/*
	 * Leert target und fügt alle Elemente aus s an. 
	 */
	private static <E, T extends Set<E>> void prepareTarget(T s, T target) {
		target.clear();
		target.addAll(s);
	}

}

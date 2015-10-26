package de.imise.util;

/**
 * Repräsentation eines geordneten Triples.
 * 
 * @author fstephan
 * @param <S>
 * @param <T>
 * @param <V>
 */
public class Triple<S, T, V> {

    /** erstes Element */
    private S e1;

    /** zweites Element */
    private T e2;

    /** drittes Element */
    private V e3;

    public Triple() {
    }

    public Triple(final S e1, final T e2, final V e3) {
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    /**
     * Setzt das erste Element auf <code>e</code>.
     * 
     * @param e
     */
    public void setFirstElement(final S e) {
        e1 = e;
    }

    /**
     * Setzt das zweite Element auf <code>e</code>.
     * 
     * @param e
     */
    public void setSecondElement(final T e) {
        e2 = e;
    }

    /**
     * Setzt das dritte Element auf <code>e</code>.
     * 
     * @param e
     */
    public void setThirdElement(final V e) {
        e3 = e;
    }

    /**
     * Gibt das erste Element wieder.
     * 
     * @return {@link #e1}
     */
    public S getFirstElement() {
        return e1;
    }

    /**
     * Gibt das zweite Element wieder.
     * 
     * @return {@link #e2}
     */
    public T getSecondElement() {
        return e2;
    }

    /**
     * Gibt das dritte Element wieder.
     * 
     * @return {@link #e3}
     */
    public V getThirdElement() {
        return e3;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (e1 == null ? 0 : e1.hashCode());
        result = prime * result + (e2 == null ? 0 : e2.hashCode());
        result = prime * result + (e3 == null ? 0 : e3.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
        if (e1 == null) {
            if (other.e1 != null) {
                return false;
            }
        } else if (!e1.equals(other.e1)) {
            return false;
        }
        if (e2 == null) {
            if (other.e2 != null) {
                return false;
            }
        } else if (!e2.equals(other.e2)) {
            return false;
        }
        if (e3 == null) {
            if (other.e3 != null) {
                return false;
            }
        } else if (!e3.equals(other.e3)) {
            return false;
        }
        return true;
    }

}

/**
 *
 */
package de.imise.util.swing.component.tree;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import de.imise.util.Alphabetical;

/**
 * A typed TreeNode. These TreeNodes are equal if they have the same UserObject
 * ({@link #equals(Object)}).
 *
 * @author AXS (03.03.2021)
 */
public class TypedTreeNode<T> extends DefaultMutableTreeNode {

    /**
     * If you do not want to display the value of the toString() function of the
     * userObject, you can set this alternative display string.
     */
    private String visibleText = null;

    /** If <code>true</code> the node will sort its children */
    private boolean sort;

    /**
     * @param o
     * @param setTreeNode
     */
    protected TypedTreeNode(final T o) {
        this(o, true);
    }

    /**
     * @param o
     * @param sort
     */
    public TypedTreeNode(final T o, final boolean sort) {
        this(o, null, sort);
    }

    /**
     * @param o
     * @param visibleText
     * @param sort
     */
    public TypedTreeNode(final T o, final String visibleText, final boolean sort) {
        super(o);
        this.visibleText = visibleText;
        this.sort = sort;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getUserObject() {
        // If someone has set another UserObject that has
        // the wrong type, this will of course go wrong.
        return (T) super.getUserObject();
    }

    /**
     * @param sort
     */
    public void setSort(final boolean sort) {
        this.sort = sort;
    }

    /**
     * @param text
     */
    public void setText(final String text) {
        visibleText = text;
    }

    /**
     * @return
     */
    public String getText() {
        return visibleText;
    }

    @Override
    public String toString() {
        if (visibleText != null) {
            return visibleText;
        } else if (userObject != null) {
            return userObject.toString();
        }
        return "";
    }

    @Override
    public void insert(final MutableTreeNode newChild, int childIndex) {
        if (children != null && sort) {
            childIndex = Alphabetical.getInsertPosition((Vector<?>) children, newChild);
        }
        super.insert(newChild, childIndex);
    }

    /**
     * @param node
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> TypedTreeNode<E> getEqualsChild(final TypedTreeNode<E> node) {
        if (children != null) {
            for (TreeNode child : children) {
                if (node.equals(child)) {
                    return (TypedTreeNode<E>) child;
                }
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (userObject == null ? 0 : userObject.hashCode());
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
        TypedTreeNode<?> other = (TypedTreeNode<?>) obj;
        if (userObject == null) {
            if (other.userObject != null) {
                return false;
            }
        } else if (!userObject.equals(other.userObject)) {
            return false;
        }
        return true;
    }

}

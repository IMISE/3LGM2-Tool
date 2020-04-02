package de.imise.tool3lgm.graphtools.path.paths;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;

/**
 * Knoten, der genutzt werden kann um Pfade in einem Baum zu speichern. Jeder dieser
 * Knoten kann einen Elementarpfad enthalten, über den ausgehend vom Endelement des
 * Pfades des Parentknotens dieser Knoten einen weiteren Elementarpfadschritt repräsentiert.
 *
 * @author AXS
 * @create 02.11.2010
 */
public class PathResultTreeNode extends DefaultMutableTreeNode {

    /**
     * Knotentypen
     */
    public static enum NodeType {
        /** Wurzelknoten */
        ROOT,
        /** Typ für Knoten, die Startelemente enthalten */
        START_ELEMENT,
        /** Typ für alle Knoten, die im äußersten Pfad vorkommen */
        SUPERSTEP,
        /** Gibt an, ob dieser Knoten aus einem in einem Sequenzpfad enthaltenen nicht-Elementarpfad ist. */
        SUBSTEP
    };

    /** Iterable dieses Objektes wird zurück gegeben, wenn dieser Knoten keine Kinder besitzt */
    private static final Vector<PathResultTreeNode> EMPTY_CHILDREN = new Vector<>(0);

    /**
     * Typ des Knotens
     */
    private NodeType type = null;

    /**
     * @param userObject
     * @param type
     */
    public PathResultTreeNode(final ElementaryPath path, final NodeType type) {
        super(path);
        this.type = type;
    }

    /**
     * @param node2Clone
     */
    public PathResultTreeNode(final PathResultTreeNode node2Clone) {
        super(node2Clone.getUserObject());
        type = node2Clone.type;
    }

    /**
     * @return
     */
    public Iterable<PathResultTreeNode> getChildren() {
        return children == null ? EMPTY_CHILDREN : getChildrenCopy();
    }

    /**
     * Liefert das userObejct als {@link Path}
     */
    public ElementaryPath getPathObject() {
        return (ElementaryPath) userObject;
    }

    /**
     * @return
     * @see tool3lgm.graphtools.newpath.pathmodel.Path#getEndElement()
     */
    public final ModelElement getEndElement() {
        ElementaryPath path = getPathObject();
        if (path == null) {
            return null;
        }
        return path.getEndElement();
    }

    /**
     * @return
     * @see tool3lgm.graphtools.newpath.pathmodel.Path#getMetaPath()
     */
    public final ElementaryMetaPath getMetaPath() {
        ElementaryPath path = getPathObject();
        if (path == null) {
            return null;
        }
        return path.getMetaPath();
    }

    /**
     * @return
     * @see tool3lgm.graphtools.newpath.pathmodel.Path#getStartElement()
     */
    public final ModelElement getStartElement() {
        ElementaryPath path = getPathObject();
        if (path == null) {
            return null;
        }
        return path.getStartElement();
    }

    /**
     * @return
     * @see tool3lgm.graphtools.newpath.pathmodel.Path#getEgde()
     */
    public final Edge getEdge() {
        ElementaryPath path = getPathObject();
        if (path == null) {
            return null;
        }
        return path.getEdge();
    }

    /**
     * @return the type
     */
    public final NodeType getType() {
        return type;
    }

    /**
     * Liefert eine Kopie des Vectors {@link #children()} als {@link ArrayList}
     *
     * @return
     */
    public final List<PathResultTreeNode> getChildrenCopy() {
        List<PathResultTreeNode> childrenCopy = new ArrayList<>(children.size());
        appendChildren(childrenCopy);
        return childrenCopy;
    }

    /**
     * Hängt an die übergebene Menge alle Unterknoten dieses Knotens an.
     *
     * @param nodes
     */
    @SuppressWarnings("unchecked")
    public final void appendChildren(final Collection<PathResultTreeNode> nodes) {
        Iterator<TreeNode> childrenIt = children.iterator();
        while (childrenIt.hasNext()) {
            PathResultTreeNode node = (PathResultTreeNode) childrenIt.next();
            nodes.add(node);
        }
    }

    /**
     * 2 Knoten sind gleich, wenn sie das gleiche userObject haben.<br>
     * Das hier überschreibt absichtlich nicht die equals(), weil dann sonst in der
     * Funktion {@link DefaultMutableTreeNode#removeFromParent()} das zu löschende
     * Kind ungünstigerweise über equals rausgesucht wird und dann eventuell der
     * falsche Knoten gelöscht wird.
     *
     * @param obj
     * @return
     */
    public boolean equalsTo(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PathResultTreeNode other = (PathResultTreeNode) obj;
        if (type != other.type) {
            return false;
        }
        if (!Objects.equals(userObject, other.userObject)) {
            return false;
        }
        return true;
    }

    @Override
    public PathResultTreeNode getParent() {
        return (PathResultTreeNode) super.getParent();
    }

    /**
     * @return the path to the root as array of {@link PathResultTreeNode}
     */
    public PathResultTreeNode[] getPathToRoot() {
        int level = getLevel();
        PathResultTreeNode[] path = new PathResultTreeNode[level];
        PathResultTreeNode levelNode = this;
        for (--level; level >= 0; level--) {
            path[level] = levelNode;
            levelNode = levelNode.getParent();
        }
        return path;
    }

    /**
     * Checks if the tree path represented by the other node up to root is contained
     * completely in the tree path of this node.
     *
     * @param other
     * @return <code>true</code> if tree path of the other node up to root is contained
     *         completly in the tree path of this node, otherwise <code>false</code>
     */
    public final boolean containsPath(final PathResultTreeNode other) {
        int thisPathLength = getLevel();
        int otherPathLength = other.getLevel();
        if (thisPathLength < otherPathLength) {
            return false;
        }
        PathResultTreeNode currentPathNodeOfThis = this;
        PathResultTreeNode currentPathNodeOfOther = other;
        while (thisPathLength > otherPathLength) {
            currentPathNodeOfThis = currentPathNodeOfThis.getParent();
            thisPathLength--;
        }
        for (int i = thisPathLength; i >= 0; i--) {
            if (!currentPathNodeOfThis.equalsTo(currentPathNodeOfOther)) {
                return false;
            }
            currentPathNodeOfThis = currentPathNodeOfThis.getParent();
            currentPathNodeOfOther = currentPathNodeOfOther.getParent();
        }
        return true;
    }

}

package de.imise.tool3lgm.graphtools.path.pathmodel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;

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
    @SuppressWarnings("unchecked")
    public Iterable<PathResultTreeNode> getChildren() {
        if (children == null) {
            return EMPTY_CHILDREN;
        }
        return children;
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
    public final AbstractMetaPath getMetaPath() {
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
    @SuppressWarnings("unchecked")
    public final ArrayList<PathResultTreeNode> getChildrenCopy() {
        return new ArrayList<PathResultTreeNode>(children);
    }

    /**
     * Hängt an die übergebene Menge alle Unterknoten dieses Knotens an.
     *
     * @param nodes
     */
    @SuppressWarnings("unchecked")
    public final void appendChildren(final Collection<PathResultTreeNode> nodes) {
        nodes.addAll(children);
    }

}

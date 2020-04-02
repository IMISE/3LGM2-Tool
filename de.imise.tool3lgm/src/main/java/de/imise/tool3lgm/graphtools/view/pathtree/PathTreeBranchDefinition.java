package de.imise.tool3lgm.graphtools.view.pathtree;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementClassTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.StringTreeNode;
import de.imise.util.collections.CollectionUtils;

/**
 * Defines one branch of a tree. A branch consists of optional hierarchy
 * elements and then a path of model elements that defines the elements
 *
 * @author AXS (02.09.2019)
 */
public class PathTreeBranchDefinition extends MetaModelSpecificAdapter {

    /**
     * Alle Objekte in dieser Liste geben vor, welche Hierarchie-Knoten
     * unterhalb von Root angezeigt werden sollen. Sind die Objekte
     * Strings, dann werden sie als Resorucen-Keys interpretiert und im
     * Baum ein {@link StringTreeNode} angelegt. Sind die Objekte
     * Elementklassen, dann wird um Baum ein {@link ElementClassTreeNode}
     * angelegt und der AnzeigeName der Klasse gezeigt. Sind die Objekte
     * Modell-Elemente, dann wird im Baum ein {@link ElementContainerTreeNode}
     * angelegt.
     */
    private final List<Object> hierarchyObjects = new ArrayList<>();

    /**
     * Der Pfad der Elemente, die angezeigt werden sollen.
     */
    private final SimpleMetaPath elementsPath;

    /**
     * @param elementsPath
     * @param hierarchyObjects
     */
    public PathTreeBranchDefinition(final SimpleMetaPath elementsPath, final Object... hierarchyObjects) {
        super(elementsPath);
        this.elementsPath = elementsPath;
        for (Object hierarchyObject : hierarchyObjects) {
            this.hierarchyObjects.add(hierarchyObject);
        }
    }

    /**
     * @return Iteable over all hierarchy objects
     */
    public Iterable<Object> iterableHierarchyObjects() {
        return CollectionUtils.iterable(hierarchyObjects.iterator());
    }

    /**
     * @return the visiblePath
     */
    public SimpleMetaPath getElementsPath() {
        return elementsPath;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (elementsPath == null ? 0 : elementsPath.hashCode());
        result = prime * result + (hierarchyObjects == null ? 0 : hierarchyObjects.hashCode());
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
        PathTreeBranchDefinition other = (PathTreeBranchDefinition) obj;
        if (elementsPath == null) {
            if (other.elementsPath != null) {
                return false;
            }
        } else if (!elementsPath.equals(other.elementsPath)) {
            return false;
        }
        if (hierarchyObjects == null) {
            if (other.hierarchyObjects != null) {
                return false;
            }
        } else if (!hierarchyObjects.equals(other.hierarchyObjects)) {
            return false;
        }
        return true;
    }

}

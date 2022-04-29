package de.imise.tool3lgm.graphtools.view.pathtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.ImageIcon;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementClassTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.StringTreeNode;
import de.imise.util.collections.CollectionUtils;
import de.imise.util.resource.SimpleResourceSource;

/**
 * Defines one branch of a tree. A branch consists of optional hierarchy
 * elements and then a path of model elements that defines the elements
 *
 * @author AXS (02.09.2019)
 */
public final class PathTreeBranchDefinition implements SimpleResourceSource {

    /**
     * Alle Objekte in dieser Liste geben vor, welche Hierarchie-Knoten
     * unterhalb von Root angezeigt werden sollen. Sind die Objekte Strings,
     * dann werden sie als Resourcen-Keys interpretiert und im Baum ein
     * {@link StringTreeNode} angelegt. Sind die Objekte Elementklassen, dann
     * wird um Baum ein {@link ElementClassTreeNode} angelegt und der
     * AnzeigeName der Klasse gezeigt. Sind die Objekte Modell-Elemente, dann
     * wird im Baum ein {@link ElementContainerTreeNode} angelegt.
     */
    private final List<Object> hierarchyObjects = new ArrayList<>();

    /**
     * Der Pfad der Elemente, die angezeigt werden sollen.
     */
    private final SequenceMetaPath elementsPath;

    /**
     * Respurce Handler to get the strings and icons
     */
    private final SimpleResourceSource resourceHandler;

    /**
     * @param resourceHandler
     * @param elementsPath
     * @param hierarchyObjects
     */
    public PathTreeBranchDefinition(final SimpleResourceSource resourceHandler, final Object... hierarchyObjects) {
        this.resourceHandler = resourceHandler;
        elementsPath = null;
        initHierarchyObjects(hierarchyObjects);
    }

    /**
     * @param resourceHandler
     * @param elementsPath
     * @param hierarchyObjects
     */
    public PathTreeBranchDefinition(final SimpleResourceSource resourceHandler, final SequenceMetaPath elementsPath, final Object... hierarchyObjects) {
        this.resourceHandler = resourceHandler;
        this.elementsPath = elementsPath;
        initHierarchyObjects(hierarchyObjects);
    }

    /**
     * @param hierarchyObjects
     */
    private void initHierarchyObjects(Object... hierarchyObjects) {
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
    public SequenceMetaPath getElementsPath() {
        return elementsPath;
    }

    @Override
    public final ImageIcon getIcon(final String name) {
        //TODO: PathTreeModel macht diesen Aufruf bei jedem Knoten (z.B. beim Aufbau des TemplateBrowsers). Da sollte man die Icons evtl. cachen in einer Map.
        return resourceHandler == null ? null : resourceHandler.getIcon(name);
    }

    /**
     * @param elementClass
     * @return
     */
    public final ImageIcon getIcon(final Class<? extends ModelElement> elementClass) {
        return getIcon(elementClass.getSimpleName());
    }

    /**
     * @param elementClass
     * @return
     */
    public final ImageIcon getIcon(final Object hierachyObject) {
        return getIcon(String.valueOf(hierachyObject));
    }

    @Override
    public final String getResString(final String resKey) {
        return resourceHandler == null ? resKey : resourceHandler.getResString(resKey);
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return resourceHandler == null ? null : resourceHandler.getResourceBundle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementsPath, hierarchyObjects);
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
        if (!Objects.equals(elementsPath, other.elementsPath)) {
            return false;
        }
        if (!Objects.equals(hierarchyObjects, other.hierarchyObjects)) {
            return false;
        }
        return true;
    }

    /**
     * @param allElementaryMetaPaths If <code>true</code> then the whole
     *            connection classes of the elementary path steps are searched
     *            out. If <code>false</code> then the connection classes of the
     *            outer contained SequenceMetaPaths are returned. If the
     *            metapath consists only of SequenceMetaPaths of length 1 (i.e.
     *            only one elementary metaPath at a time), then this parameter
     *            is irrelevant.
     * @return a set of all classes which are defined as visible through this
     *         tree branch
     */
    public final Set<Class<? extends ModelElement>> getVisibleElementTypes(final boolean allElementaryMetaPaths) {
        if (elementsPath == null) {
            return ImmutableSet.of();
        }
        if (allElementaryMetaPaths) {
            return elementsPath.getAllElementaryPathsStartAndEndClasses();
        }
        return elementsPath.getAllFirstLevelSubMetaPathsStartAndEndClasses();
    }

}

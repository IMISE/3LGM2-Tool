package de.imise.tool3lgm.graphtools.path.metapaths;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.PathFunctions.PathConnectionState;
import de.imise.tool3lgm.graphtools.path.paths.PathResultTreeModel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Oberklasse für alle Metapfade.
 *
 * @author AXS
 * @create 12.10.2010
 */
abstract class MetaPathImpl extends BasicMetaPathImpl implements MetaPath {

    /**
     * @param metaModel
     */
    public MetaPathImpl(final MetaModel metaModel) {
        super(metaModel);
    }

    /**
     * @param metaModel
     * @param name Anzeigenamen
     */
    public MetaPathImpl(final MetaModel metaModel, final String name) {
        super(metaModel, name);
    }

    /**
     * @param metaModel
     * @param startElementClass
     * @param endElementClass
     */
    public MetaPathImpl(final MetaModel metaModel, final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass) {
        super(metaModel, startElementClass, endElementClass);

    }

    /**
     * @param metaModel
     * @param startElementClass
     * @param endElementClass
     * @param name
     */
    public MetaPathImpl(final MetaModel metaModel, final Class<? extends ModelElement> startElementClass, final Class<? extends ModelElement> endElementClass, final String name) {
        super(metaModel, startElementClass, endElementClass, name);

    }

    /**
     * @param metaModel
     * @param startElementClasses
     * @param endElementClasses
     * @param name
     */
    public MetaPathImpl(final MetaModel metaModel, final Set<Class<? extends ModelElement>> startElementClasses, final Set<Class<? extends ModelElement>> endElementClasses, final String name) {
        super(metaModel, startElementClasses, endElementClasses, name);
    }

    @Override
    public String getBaseResKeyOrName() {
        return null;
    }

    @Override
    public InvalidityCheckResult getInvalidityCheckResult() {
        if (invalidityCheckResult == null) {
            InvalidReason invalidReason = null;
            if (startElementClasses == null || startElementClasses.size() == 0) {
                invalidReason = InvalidReason.INVALID_START_CLASSES;
            } else if (endElementClasses == null || endElementClasses.size() == 0) {
                invalidReason = InvalidReason.INVALID_END_CLASSES;
            } else {
                invalidReason = null;
            }
            invalidityCheckResult = new InvalidityCheckResult(invalidReason);
        }
        return invalidityCheckResult;
    }

    @Override
    public final boolean isValid() {
        return getInvalidityCheckResult().invalidReason == null;
    }

    @Override
    public final boolean isSingleConnection() {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        for (ElementaryMetaPath elementaryMetaPath : elementaryMetaPaths) {
            if (elementaryMetaPath.getForwardCardinality().max() != 1) {
                return false;
            }
        }
        //wenn der Pfad keine einfache Liste von Elementarpfaden ist, dann wird davon ausgegangen, dass mehrere Verbindungen mgl. sind
        return !elementaryMetaPaths.isEmpty();
    }

    @Override
    public final boolean isStartDependent() {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        //wenn der Pfad keine einfache Liste von Elementarpfaden ist, dann wird davon ausgegangen, dass mehrere Verbindungen mgl. sind
        if (elementaryMetaPaths.isEmpty()) {
            return false;
        }
        for (ElementaryMetaPath elementaryMetaPath : elementaryMetaPaths) {
            //min cardinality == 0 -> dependent
            EdgeCardinality forwardCardinality = elementaryMetaPath.getForwardCardinality();
            int forwardMinCardinality = forwardCardinality.min();
            if (forwardMinCardinality > 0) {
                continue;
            }
            //InstanciationEgde -> dependent
            Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
            if (InstanciationEdge.class.isAssignableFrom(edgeClass)) {
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public final boolean isEndElementDependent() {
        MetaPath otherDirection = getOtherDirection();
        if (otherDirection == null) {
            return false;
        }
        return otherDirection.isStartDependent();
    }

    @Override
    public MetaPath getOtherDirection() {
        return otherDirection;
    }

    @Override
    public List<ElementaryMetaPath> getElementaryMetaPaths() {
        return EMPTY_ELEMENTARY_PATH_LIST;
    }

    @Override
    public int getElementaryMetaPathCount() {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        return elementaryMetaPaths == null || elementaryMetaPaths.isEmpty() ? 0 : elementaryMetaPaths.size();
    }

    @Override
    public final MetaPath getSubMetaPath(final int index) {
        List<MetaPath> subMetaPaths = getSubMetaPaths();
        return subMetaPaths.get(index);
    }

    @Override
    public final int getSubMetaPathCount() {
        List<MetaPath> subMetaPaths = getSubMetaPaths();
        return subMetaPaths.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final List<MetaPath> getSubMetaPaths(final boolean elementaryMetaPaths) {
        List<MetaPath> subMetaPaths = elementaryMetaPaths ? (List<MetaPath>) (List<?>) getElementaryMetaPaths() : getSubMetaPaths();
        return subMetaPaths;
    }

    @Override
    public ElementaryMetaPath getFirstElementaryMetaPath() {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        //wenn der Pfad keine einfache Liste von Elementarpfaden ist, dann wird davon ausgegangen, dass das letzte Pfadelement gebraucht wird
        if (elementaryMetaPaths.isEmpty()) {
            return null;
        }
        ElementaryMetaPath lastElementaryMetaPath = elementaryMetaPaths.get(0);
        return lastElementaryMetaPath;
    }

    @Override
    public ElementaryMetaPath getLastElementaryMetaPath() {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        //wenn der Pfad keine einfache Liste von Elementarpfaden ist, dann wird davon ausgegangen, dass das letzte Pfadelement gebraucht wird
        if (elementaryMetaPaths == null || elementaryMetaPaths.isEmpty()) {
            return null;
        }
        int lastElementaryMetaPathIndex = elementaryMetaPaths.size() - 1;
        ElementaryMetaPath lastElementaryMetaPath = elementaryMetaPaths.get(lastElementaryMetaPathIndex);
        return lastElementaryMetaPath;
    }

    @Override
    public final Class<? extends ModelElement> getElementaryMetaPathStepConnectingClass(final int pathStepIndex) {
        return MetaPathFunctions.getElementaryMetaPathsConnectingClass(this, pathStepIndex);
    }

    @Override
    public final Class<? extends ModelElement> getSubMetaPathStepConnectingClass(final int pathStepIndex) {
        return MetaPathFunctions.getSubMetaPathsConnectingClass(this, pathStepIndex);
    }

    @Override
    public boolean isAssignable(final MetaPath other) {
        //Maybe there would be an useful expression here for general MetaPath too, but
        //we only need this function for SimpleMetaPaths and ElementaryMetaPaths
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    //getConnectedElements(...) + getConnectedContainer(...) + getResultTree //
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public List<ModelElement> getConnectedElements(final ModelElement me, final boolean multiple) {
        List<ModelElement> modelElements = new ArrayList<>();
        modelElements.add(me);
        return getConnectedElements(modelElements, multiple);
    }

    @Override
    public List<ModelElement> getConnectedElements(final ModelElement me) {
        return getConnectedElements(me, false);
    }

    @Override
    public List<ModelElement> getConnectedElements(final Collection<ModelElement> modelElements) {
        return getConnectedElements(modelElements, false);
    }

    @Override
    public List<ModelElement> getConnectedElements(final Collection<ModelElement> modelElements, final boolean multiple) {
        PathResultTreeModel resultTree = getResultTree(modelElements);
        return resultTree.getConnectedElements(multiple);
    }

    @Override
    public List<ElementContainer> getConnectedContainer(final ModelElement me, final GraphDocument doc) {
        return getConnectedContainer(me, doc, false);
    }

    @Override
    public List<ElementContainer> getConnectedContainer(final ModelElement me, final GraphDocument doc, final boolean forlast) {
        PathResultTreeModel resultTree = getResultTree(me);
        return resultTree.getConnectedContainer(doc, forlast);
    }

    @Override
    public PathConnectionState getPathConnectionState(final ModelElement startElement, final ModelElement endElement, final boolean searchParents, final boolean searchParts) {
        return PathFunctions.getPathConnectionState(startElement, endElement, this, searchParents, searchParts);
    }

    @Override
    public PathConnectionState getPathConnectionState(final ModelElement startElement, final ModelElement endElement) {
        return PathFunctions.getPathConnectionState(startElement, endElement, this, OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS.is(), OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS.is());
    }

    @Override
    public boolean isConnected(final ModelElement startElement, final ModelElement endElement) {
        return getPathConnectionState(startElement, endElement) != PathConnectionState.NOT_CONNECTED;
    }

    @Override
    public boolean isDirectConnected(final ModelElement startElement, final ModelElement endElement) {
        return getPathConnectionState(startElement, endElement, false, false) != PathConnectionState.NOT_CONNECTED;
    }

    @Override
    public PathResultTreeModel getResultTree(final ModelElement startElement) {
        return new PathResultTreeModel(this, startElement);
    }

    @Override
    public PathResultTreeModel getResultTree(final ModelElement startElement, final boolean keepIncompleteBranches) {
        return new PathResultTreeModel(this, startElement, keepIncompleteBranches);
    }

    @Override
    public PathResultTreeModel getResultTree(final Collection<ModelElement> startElements) {
        return new PathResultTreeModel(this, startElements);
    }

    @Override
    public PathResultTreeModel getResultTree(final Collection<ModelElement> startElements, final boolean keepIncompleteBranches) {
        return new PathResultTreeModel(this, startElements, keepIncompleteBranches);
    }

    @Override
    public PathResultTreeModel getResultTree(final List<Collection<ModelElement>> startElements) {
        return new PathResultTreeModel(this, startElements);
    }

    @Override
    public PathResultTreeModel getResultTree(final List<Collection<ModelElement>> startElements, final boolean keepIncompleteBranches) {
        return new PathResultTreeModel(this, startElements, keepIncompleteBranches);
    }

}

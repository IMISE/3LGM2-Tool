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

    /**
     * @return
     */
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

    /**
     * Liefert <code>true</code>, wenn der Pfad keine Fehler enthält.
     *
     * @return
     */
    @Override
    public final boolean isValid() {
        return getInvalidityCheckResult().invalidReason == null;
    }

    /**
     * Liefert <code>true</code>, wenn der Pfad eine einfache Assoziationsfolge
     * ist (also bei {@link #getElementaryMetaPaths()} nicht <code>null</code>
     * zurück gibt und jeder Einzelpfad die maximale Endkardinalität von 1 hat.
     */
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

    /**
     * Liefert <code>true</code>, wenn das erste Element des Pfades nur
     * existieren kann, wenn es mit einem auf dem Pfad dahinter liegenden
     * Element verbunden ist. Das wird gebraucht, um zu entscheiden, ob ein neu
     * angelegtes EndElement des Pfades immer sofort verbunden werden muss.
     *
     * @return
     */
    @Override
    public final boolean isFirstPathElementDependent() {
        ElementaryMetaPath firstElementaryMetaPathInPath = getFirstElementaryMetaPath();
        if (firstElementaryMetaPathInPath == null) {
            return false;
        }
        //Verbindungen, die durch InstanciationEgdes bestehen, kann man nicht einfach lösen/ändern und gelten als existenznotwendig
        Class<? extends Edge> edgeClass = firstElementaryMetaPathInPath.getEdgeClass();
        if (InstanciationEdge.class.isAssignableFrom(edgeClass)) {
            return true;
        }
        EdgeCardinality forwardCardinality = firstElementaryMetaPathInPath.getForwardCardinality();
        int minCardinality = forwardCardinality.min();
        return minCardinality > 0;
    }

    /**
     * Liefert <code>true</code>, wenn das letzte Element des Pfades nur
     * existieren kann, wenn es mit einem auf dem Pfad davor liegenden Element
     * verbunden ist. Das wird gebraucht, um zu entscheiden, ob ein neu
     * angelegtes EndElement des Pfades immer sofort verbunden werden muss.
     *
     * @return
     */
    @Override
    public final boolean isLastPathElementDependent() {
        ElementaryMetaPath lastElementaryMetaPathInPath = getLastElementaryMetaPath();
        if (lastElementaryMetaPathInPath == null) {
            return false;
        }
        //Verbindungen, die durch InstanciationEgdes bestehen, kann man nicht einfach lösen/ändern und gelten als existenznotwendig
        Class<? extends Edge> edgeClass = lastElementaryMetaPathInPath.getEdgeClass();
        if (InstanciationEdge.class.isAssignableFrom(edgeClass)) {
            return true;
        }
        EdgeCardinality backwardCardinality = lastElementaryMetaPathInPath.getBackwardCardinality();
        int minCardinality = backwardCardinality.min();
        return minCardinality > 0;
    }

    /**
     * Liefert den MetaPfad der die Gegenricthung beschreibt oder
     * <code>null</code>, wenn es einen solchen nicht gibt.
     *
     * @return the otherDirectionPath
     */
    @Override
    public MetaPath getOtherDirection() {
        return otherDirection;
    }

    /**
     * Liefert eine Folge von Elementarpfaden, wenn sich dieser Pfad so bilden
     * lässt, ansonsten kommt eine leere Liste zurück. Alle parallelen Pfade
     * geben hier leere Liste zurück. {@link SerialMetaPath} geben nur keine
     * leere Liste zurück, wenn sie im innersten ein einzelner Pfad sind ohne
     * parallele oder rekursive Pfade sind.
     *
     * @return
     */
    @Override
    public List<ElementaryMetaPath> getElementaryMetaPaths() {
        return EMPTY_ELEMENTARY_PATH_LIST;
    }

    @Override
    public int getElementaryMetaPathCount() {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        return elementaryMetaPaths == null || elementaryMetaPaths.isEmpty() ? 0 : elementaryMetaPaths.size();
    }

    /**
     * @return den ersten ElementaryMetaPath aus
     *         {@link #getElementaryMetaPaths()}, wenn die Liste mind. einen
     *         solchen Elementarpfad enthält.
     */
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

    /**
     * @return den letzten ElementaryMetaPath aus
     *         {@link #getElementaryMetaPaths()}, wenn die Liste mind. einen
     *         solchen Elementarpfad enthält.
     */
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

    /**
     * Returns the connection class of the path step with the passed index in
     * the element path list of this path. With index 0, this is the more
     * special of the end class of the first elementary path and the start class
     * of the next elementary path. The path step with the index of path length
     * -1 is the end class of the last elementary path = end class of the whole
     * elementary path list. The start class of the complete path is not
     * accessible through this function.
     *
     * @param pathStepIndex
     * @return
     */
    @Override
    public final Class<? extends ModelElement> getPathStepElementClass(final int pathStepIndex) {
        List<ElementaryMetaPath> elementaryMetaPaths = getElementaryMetaPaths();
        if (elementaryMetaPaths.isEmpty()) {
            return null;
        }
        ElementaryMetaPath elementaryMetaPath1 = pathStepIndex >= 0 ? elementaryMetaPaths.get(pathStepIndex) : null;
        ElementaryMetaPath elementaryMetaPath2 = pathStepIndex == elementaryMetaPaths.size() - 1 ? null : elementaryMetaPaths.get(pathStepIndex + 1);
        Class<? extends ModelElement> connectingClass = MetaPathFunctions.getElementaryPathsConnectingClass(elementaryMetaPath1, elementaryMetaPath2);
        return connectingClass;
    }

    /**
     * @param other
     * @return only <code>true</code> if this and the other metapath have an
     *         assignable start class, an assignable end class, an assignable
     *         edge class, the same direction and the same type. Assignable only
     *         means that one of the class must be a subclass of the other
     *         (which is sub and which super dosn't matters).
     */
    @Override
    public boolean isAssignable(final MetaPath other) {
        //Maybe there would be an useful expression here for general MetaPath too, but
        //we only need this function for SimpleMetaPaths and ElementaryMetaPaths
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    //getConnectedElements(...) + getConnectedContainer(...) + getResultTree //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Liefert alle mit dem übergebenen Element über diesen MetaPfad verbundenen
     * Elemente.
     *
     * @param me Ausgangselement
     * @param multiple Wenn <code>true</code> sind mehrfach verbundene Element
     *            auch mehrfach in der Ergebnisliste, bei <code>false</code> ist
     *            jedes Element nur einmal enthalten.
     * @return
     */
    @Override
    public List<ModelElement> getConnectedElements(final ModelElement me, final boolean multiple) {
        List<ModelElement> modelElements = new ArrayList<>();
        modelElements.add(me);
        return getConnectedElements(modelElements, multiple);
    }

    /**
     * @param me
     * @return
     */
    @Override
    public List<ModelElement> getConnectedElements(final ModelElement me) {
        return getConnectedElements(me, false);
    }

    /**
     * Liefert eine Sammlung aller Elemente, die über diesen Pfad mit den
     * übergebenen Elementen verbunden sind.
     *
     * @param modelElements
     * @return
     */
    @Override
    public List<ModelElement> getConnectedElements(final Collection<ModelElement> modelElements) {
        return getConnectedElements(modelElements, false);
    }

    /**
     * Liefert eine Sammlung aller Elemente, die über diesen Pfad mit den
     * übergebenen Elementen verbunden sind.
     *
     * @param modelElements Ausgangselemente
     * @param multiple Wenn <code>true</code> enthält die Rückgabesammlung
     *            dieselben Elemente sooft, wie sie mit Elementen der
     *            Ausgangliste über diesen Pfad verbunden sind. Bei
     *            <code>false</code> ist jedes Element nur einmal enthalten.
     * @return
     */
    @Override
    public List<ModelElement> getConnectedElements(final Collection<ModelElement> modelElements, final boolean multiple) {
        PathResultTreeModel resultTree = getResultTree(modelElements);
        return resultTree.getConnectedElements(multiple);
    }

    /**
     * @param me
     * @param doc
     * @return
     */
    @Override
    public List<ElementContainer> getConnectedContainer(final ModelElement me, final GraphDocument doc) {
        return getConnectedContainer(me, doc, false);
    }

    /**
     * @param me
     * @param doc
     * @param forlast
     * @return
     */
    @Override
    public List<ElementContainer> getConnectedContainer(final ModelElement me, final GraphDocument doc, final boolean forlast) {
        PathResultTreeModel resultTree = getResultTree(me);
        return resultTree.getConnectedContainer(doc, forlast);
    }
    /**
     * @param startElement
     * @param endElement
     * @param searchParents
     * @param searchParts
     * @return
     */
    @Override
    public PathConnectionState getPathConnectionState(final ModelElement startElement, final ModelElement endElement, final boolean searchParents, final boolean searchParts) {
        return PathFunctions.getPathConnectionState(startElement, endElement, this, searchParents, searchParts);
    }

    /**
     * @param startElement
     * @param endElement
     * @return
     */
    @Override
    public PathConnectionState getPathConnectionState(final ModelElement startElement, final ModelElement endElement) {
        return PathFunctions.getPathConnectionState(startElement, endElement, this, OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS.is(), OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS.is());
    }

    /**
     * @param startElement
     * @param endElement
     * @return
     */
    @Override
    public boolean isConnected(final ModelElement startElement, final ModelElement endElement) {
        return getPathConnectionState(startElement, endElement) != PathConnectionState.NOT_CONNECTED;
    }

    /**
     * @param startElement
     * @param endElement
     * @return
     */
    @Override
    public boolean isDirectConnected(final ModelElement startElement, final ModelElement endElement) {
        return getPathConnectionState(startElement, endElement, false, false) != PathConnectionState.NOT_CONNECTED;
    }

    /**
     * Liefert einen Ergebnisbaum, der alle eventuell vorhandenen Pfade
     * ausgehend vom übergebenen Element aufspannt
     *
     * @param startElement
     * @return
     */
    @Override
    public PathResultTreeModel getResultTree(final ModelElement startElement) {
        return new PathResultTreeModel(this, startElement);
    }

    /**
     * Liefert einen Ergebnisbaum, der alle eventuell vorhandenen Pfade
     * ausgehend vom übergebenen Element aufspannt
     *
     * @param startElement
     * @param keepIncompleteBranches
     * @return
     */
    @Override
    public PathResultTreeModel getResultTree(final ModelElement startElement, final boolean keepIncompleteBranches) {
        return new PathResultTreeModel(this, startElement, keepIncompleteBranches);
    }

    /**
     * @param startElements
     * @return
     */
    @Override
    public PathResultTreeModel getResultTree(final Collection<ModelElement> startElements) {
        return new PathResultTreeModel(this, startElements);
    }

    /**
     * @param startElements
     * @param keepIncompleteBranches
     * @return
     */
    @Override
    public PathResultTreeModel getResultTree(final Collection<ModelElement> startElements, final boolean keepIncompleteBranches) {
        return new PathResultTreeModel(this, startElements, keepIncompleteBranches);
    }

    /**
     * @param startElements
     * @return
     */
    @Override
    public PathResultTreeModel getResultTree(final List<Collection<ModelElement>> startElements) {
        return new PathResultTreeModel(this, startElements);
    }

    /**
     * @param startElements
     * @param keepIncompleteBranches
     * @return
     */
    @Override
    public PathResultTreeModel getResultTree(final List<Collection<ModelElement>> startElements, final boolean keepIncompleteBranches) {
        return new PathResultTreeModel(this, startElements, keepIncompleteBranches);
    }

}

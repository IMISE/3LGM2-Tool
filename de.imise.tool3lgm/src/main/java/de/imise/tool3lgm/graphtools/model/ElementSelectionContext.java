package de.imise.tool3lgm.graphtools.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

public class ElementSelectionContext {

    /**
     * COMMENTME
     */
    protected ModelSelection selectedContainer;

    /**
     *
     */
    public ElementSelectionContext() {
        super();
        selectedContainer = new ModelSelection();
    }

    /**
     * @return
     * @see tool3lgm.graphtools.ModelSelection#isJoinableElementsSelected()
     */
    public boolean isJoinableElementsSelected() {
        return selectedContainer.isJoinableElementsSelected();
    }

    /**
     * @return
     * @see de.imise.tool3lgm.graphtools.model.ModelSelection#iterableBendpointContainer()
     */
    public Iterable<BendpointContainer> getSelectedBendpointContainerIterable() {
        return selectedContainer.iterableBendpointContainer();
    }

    /**
     * @return
     * @see de.imise.tool3lgm.graphtools.model.ModelSelection#iterableRealElementContainer()
     */
    public Iterable<NodeContainer> getSelectedRealElementContainerIterable() {
        return selectedContainer.iterableRealElementContainer();
    }

    /**
     * @return
     * @see de.imise.tool3lgm.graphtools.model.ModelSelection#iterableEdgeContainer()
     */
    public Iterable<EdgeContainer> getSelectedEdgeContainerIterable() {
        return selectedContainer.iterableEdgeContainer();
    }

    /**
     * @return
     * @see de.imise.tool3lgm.graphtools.model.ModelSelection.iterable()
     */
    public Iterable<ElementContainer> getSelectedContainerIterator() {
        return selectedContainer.iterable();
    }

    /**
     * Löscht die aktuelle Selektion.
     *
     * @return
     */
    public void clearSelection() {
        selectedContainer.clear();
    }

    /**
     * Liefert die Liste der aktuell selektierten {@link ModelElement}s.<br />
     * Das zuletzt selektierte Element ist auch in dieser Liste das letzte Element.
     *
     * @return
     *         Liste der ModellElemente der selektierten ElementContainer
     * @see ModelSelection#getSelectedElements()
     */
    public final List<ModelElement> getSelectedElements() {
        return selectedContainer.getSelectedElements();
    }

    /**
     * Liefert die Liste der aktuell selektierten {@link ElementContainer}.<br />
     * Der zuletzt selektierte Container ist in dieser Liste das letzte Element.
     *
     * @return
     *         Liste der selektierten {@link ElementContainer}
     * @see ModelSelection#getSelectedContainer()
     */
    public final List<ElementContainer> getSelectedContainer() {
        return selectedContainer.getSelectedContainer();
    }

    /**
     * @return
     */
    public final ElementContainer getLastSelected() {
        return selectedContainer.getLastSelected();
    }

    /**
     * @param ec
     * @return
     */
    public final boolean isSelected(final ElementContainer ec) {
        return selectedContainer.contains(ec);
    }

    /**
     * Fügt den angegebenen {@link ElementContainer} als letztes Element zur Selektion hinzu.
     * Falls es schon in der Selektion vorhanden war, wird das alte Vorkommen entfernt.
     * Diese Aktion wird weder Undo-/Redo-technisch geloggt noch wird ein Event gefeuert.
     *
     * @param mc
     * @see #addToSelection(ElementContainer, int)
     */
    public final void addSimpleToSelection(final ElementContainer mc) {
        if (mc == null) {
            return;
        }
        selectedContainer.add(mc);
    }

    /**
     * @param me
     */
    protected final void deselectInElementSelectionContext(final ModelElement me) {
        if (me == null) {
            return;
        }
        ArrayList<ElementContainer> deselected = new ArrayList<>();
        for (ElementContainer ec : selectedContainer) {
            if (ec.getElement() == me) {
                deselected.add(ec);
            }
        }
        selectedContainer.removeAll(deselected);
    }

    /**
     * Gibt wieder, wieviele {@link ElementContainer} selektiert sind.
     *
     * @return
     */
    public int getSelectionSize() {
        return selectedContainer.size();
    }

    /**
     * @return
     * @see ModelSelection2#getSelectedBendpointContainerCount()
     */
    public int getSelectedBendpointContainerCount() {
        return selectedContainer.getSelectedBendpointContainerCount();
    }

    /**
     * @return
     * @see ModelSelection2#getSelectedEdgeContainerCount()
     */
    public int getSelectedEdgeContainerCount() {
        return selectedContainer.getSelectedEdgeContainerCount();
    }

    /**
     * @return
     * @see ModelSelection2#getSelectedRealElementContainerCount()
     */
    public int getSelectedRealElementContainerCount() {
        return selectedContainer.getSelectedRealElementContainerCount();
    }

    /**
     * @return
     * @see de.imise.tool3lgm.graphtools.model.ModelSelection#getMostSpecialRealElementsClass()
     */
    public Class<? extends ModelElement> _getMostSpecialRealElementsClass() {
        return selectedContainer._getMostSpecialRealElementsClass();
    }

    /**
     * @return
     * @see de.imise.tool3lgm.graphtools.model.ModelSelection#getSelectedRealElementClasses()
     */
    public Set<Class<? extends ModelElement>> getSelectedRealElementClasses() {
        return selectedContainer.getSelectedRealElementClasses();
    }

    /**
     * @return
     */
    public final boolean isSelection() {
        return getSelectionSize() > 0;
    }

    /**
     * Liefert <code>true</code>, wenn genau 1 Element selektiert ist.
     *
     * @return
     */
    public boolean isSingleSelection() {
        return getSelectionSize() == 1;
    }

    /**
     * Gibt zurück, ob mehr als ein {@link ElementContainer} selektiert ist.
     *
     * @return
     */
    public final boolean isMultipleSelection() {
        return getSelectionSize() > 1;
    }

    /**
     * Gibt zurück, ob mehr als ein {@link NodeContainer} oder {@link BendpointContainer} selektiert ist.
     *
     * @return
     */
    public final boolean isMultipleNodeSelection() {
        return getSelectedRealElementContainerCount() + getSelectedBendpointContainerCount() > 1;
    }

    /**
     * Gibt zurück, ob mehr als ein {@link NodeContainer} selektiert ist.
     *
     * @return
     */
    public final boolean isMultipleRealNodeSelection() {
        return getSelectedRealElementContainerCount() > 1;
    }

    /**
     * Liefert <code>true</code>, wenn mind. ein {@link NodeContainer} selektiert ist. {@link BendpointContainer} als Unterklasse von
     * {@link NodeContainer} zählen auch.
     *
     * @return
     */
    public boolean isSelectedAtLeastOneNode() {
        return selectedContainer.getSelectedRealElementContainerCount() > 0 || selectedContainer.getSelectedBendpointContainerCount() > 0;
    }

    /**
     * Liefert <code>true</code>, wenn mind. ein {@link BendpointContainer} selektiert ist.
     *
     * @return
     */
    public boolean isSelectedAtLeastOneBendpoint() {
        return selectedContainer.getSelectedBendpointContainerCount() > 0;
    }

    /**
     * Liefert <code>true</code>, wenn mind. ein {@link EdgeContainer} selektiert ist.
     *
     * @return
     */
    public boolean isSelectedAtLeastOneEdge() {
        return selectedContainer.getSelectedEdgeContainerCount() > 0;
    }

    /**
     * Liefert <code>true</code>, wenn mind. ein {@link NodeContainer} selektiert ist, der kein {@link BendpointContainer} ist.
     *
     * @return
     */
    public boolean isSelectedAtLeastOneRealNode() {
        return selectedContainer.getSelectedRealElementContainerCount() > 0;
    }

    /**
     * Liefert <code>true</code>, wenn nur {@link NodeContainer} selektiert sind.
     *
     * @return
     */
    public final boolean isSelectedOnlyNodes() {
        return isSelectedAtLeastOneNode() && !isSelectedAtLeastOneEdge();
    }

    /**
     * Liefert <code>true</code>, wenn nur {@link BendpointContainer} selektiert sind.
     *
     * @return
     */
    public final boolean isSelectedOnlyBendpoints() {
        return isSelectedAtLeastOneBendpoint() && !isSelectedAtLeastOneNode() && !isSelectedAtLeastOneEdge();
    }

    /**
     * Liefert <code>true</code>, wenn nur {@link EdgeContainer} selektiert sind.
     *
     * @return
     */
    public final boolean isSelectedOnlyEdges() {
        return !isSelectedAtLeastOneBendpoint() && !isSelectedAtLeastOneNode() && isSelectedAtLeastOneEdge();
    }

    /**
     * Liefert <code>true</code>, wenn nur {@link NodeContainer} selektiert sind, die keine {@link BendpointContainer} sind.
     *
     * @return
     */
    public final boolean isSelectedOnlyRealNodes() {
        return !isSelectedAtLeastOneBendpoint() && isSelectedAtLeastOneNode() && !isSelectedAtLeastOneEdge();
    }

    /**
     * Gibt wieder, ob alle ausgewählten Elemente <em>unique</em> sind, also in allen Teilmodellen
     * vorkommen und keine grafische Repräsentation besitzen.
     *
     * @return
     */
    public boolean isSelectedOnlyUnique() {
        return selectedContainer.isSelectedOnlyUniqueNodes();
    }

    /**
     * Gibt wieder, ob die selektierten Elemente ausschließlich im Teilmodell existieren
     *
     * @return
     */
    public boolean isSelectedOnlySubmodelElements() {
        return selectedContainer.isSelectedOnlyBendpointsAndTextfields();
    }

    /**
     * Gibt wieder, ob nur untergeordnete Elemente in den RealNodes selektiert sind.
     *
     * @return
     */
    public boolean isSelectedOnlySlaveRealNodes() {
        return selectedContainer.isSelectedOnlySlaveRealNodes();
    }

    /**
     * Giebt einen Ausgabestring der Selektion zurück.
     *
     * @return
     */
    public String selectionToString() {
        return selectedContainer.toString();
    }

}

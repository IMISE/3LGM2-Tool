package de.imise.tool3lgm.graphtools.model;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Textfield;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer.Bendpoint;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.ReflectionUtils;
import de.imise.util.collections.CollectionUtils;

/**
 * Grundklasse zur Verwaltung der Modellselektion. Sie besteht aus 3 Mengen (selektierte Node, Kanten und Knickpunkte).
 * Zusätzlich dazu wird sich das zuletzt zur Selektion hinzugefügte Element gemerkt.
 *
 * @author AXS
 *         created on 11.12.2006
 */
public class ModelSelection implements Set<ElementContainer> {

    /**
     * Speziellste Oberklasse der {@link ModelElement}s aller selektierbaren {@link NodeContainer}. Das
     * sollte in alle Metamodellen {@link ModelElement} sein, muss es aber in Zukunft nicht.
     */
    private static final Class<?> COMMON_REAL_ELEMENTS_SUPER_CLASS = ReflectionUtils.getCommonSuperClass(ModelConstants.ALL_NODES);

    /**
     * Liste, die die selektierten {@link NodeContainer} enthält.
     */
    private final HashSet<NodeContainer> selectedRealNodeContainer;

    /**
     * Liste, die die selektierten {@link Bendpoint} enthält.
     */
    private final HashSet<Bendpoint> selectedBendpointContainer;

    /**
     * Liste, die die selektierten {@link EdgeContainer} enthält.
     */
    private final HashSet<EdgeContainer> selectedEdgeContainer;

    /**
     * Zuletzt selektiertes Element
     */
    private ElementContainer lastSelected = null;

    /**
     * Enthält die speziellste gemeinsame Unterklasse von {@link ModelElement} aus allen Elementen
     * der selektierten {@link NodeContainer} aus <code>selectedRealNodeContainer</code>.<br>
     * Wenn nichts selektiert ist, ist dieser Wert <code>null</code>.
     */
    private Class<? extends ModelElement> mostSpecialRealElementClass = null;

    /**
     * Wenn viele Elemente hinzugefügt oder entfernt werden sollen, muss nicht bei jedem hinzufügen
     * einzeln
     */
    private boolean preventUpdate = false;

    /**
     * @param initialCapacity
     */
    public ModelSelection(final int initialCapacity) {
        super();
        selectedRealNodeContainer = new HashSet<>(initialCapacity);
        selectedBendpointContainer = new HashSet<>(initialCapacity);
        selectedEdgeContainer = new HashSet<>(initialCapacity);
    }

    /**
     * Erzeugt ein neues Selektionsobjekt mit einer leeren Selektionsliste.
     */
    public ModelSelection() {
        this(100);
    }

    /**
     * Erzeugt eine neue Selektion mit den übergebenen Objeckten als Selektionsmenge.
     */
    public ModelSelection(final Collection<? extends ElementContainer> selection) {
        this(selection.size());
        addAll(selection);
    }

    /**
     * Liefert das zuletzt selektierte Element, auf das sich alle Aktionen beziehen.
     *
     * @return
     */
    public ElementContainer getLastSelected() {
        return lastSelected;
    }

    /**
     * Einheitliche Schnittstelle zum internen setzen von {@link #lastSelected}.
     * Dient nur der Möglichkeit hier mal auszugeben, welches Element das ist.
     *
     * @param lastSelected
     */
    private void setLastSelected(final ElementContainer lastSelected) {
        /*
         * if (lastSelected instanceof EdgeContainer) {
         * Edge edge = (Edge) lastSelected.getElement();
         * System.err.println(ModelConstants.getDisplayableName(edge) + ": Start=" + edge.getStart() + " <-> End=" + edge.getEnd());
         * }
         */
        this.lastSelected = lastSelected;
    }

    /**
     * Liefert die Anzahl der selektierten Objekte
     *
     * @see java.util.Set#size()
     */
    @Override
    public int size() {
        return selectedRealNodeContainer.size() + selectedBendpointContainer.size() + selectedEdgeContainer.size();
    }

    /**
     * Liefert die Selektion. Wenn die Parameter-Listen nicht leer sind, dann werden die selektierten Node, die in
     * den Parameter-Listen enthalten sind, am Anfang der Rückgabeliste in derselben Reihenfolge eingetragen, in der sie
     * in den Parameter-Listen stehen.
     * Die Parameter-Listen sollten die node-Listen der LayerContainer sein, so dass die Rückgabe-Liste die Reihenfolge
     * enthält, mit der die Node auf dem Layer gezeichnet werden.
     *
     * @param orderSources
     * @return
     */
    public final List<ElementContainer> getSortedSelection(final Iterable<NodeContainer>... orderSources) {
        List<ElementContainer> returnList = new ArrayList<>(size());
        returnList.addAll(selectedRealNodeContainer);
        int insertIndex = 0;
        for (Iterable<NodeContainer> orderSource : orderSources) {
            for (NodeContainer nc : orderSource) {
                if (returnList.remove(nc)) {
                    returnList.add(insertIndex++, nc);
                }
            }
        }
        returnList.addAll(selectedEdgeContainer);
        returnList.addAll(selectedBendpointContainer);
        return returnList;
    }

    /**
     * Liefert die Liste der aktuell selektierten <code>ModelElements</code>.<br />
     * Das zuletzt selektierte Element ist auch in dieser Liste das letzte Element.
     *
     * @return
     *         Liste der ModellElemente der selektierten ElementContainer
     */
    public final List<ModelElement> getSelectedElements() {
        ArrayList<ModelElement> returnList = new ArrayList<>(size());
        returnList.addAll(GDCollection.getModelElements(selectedRealNodeContainer));
        returnList.addAll(GDCollection.getModelElements(selectedEdgeContainer));
        returnList.addAll(GDCollection.getModelElements(selectedBendpointContainer));
        //sicher stellen, dass das lastSelected Element auch als letztes Element in der Liste ist
        if (lastSelected != null) {
            int i = returnList.indexOf(lastSelected.getElement());
            if (i >= 0) {
                returnList.add(returnList.remove(i));
            }
        }
        return returnList;
    }

    /**
     * Liefert alle Elementklassen aller selektierten Knotenelemente ohne das zuletzt selektierte Element.
     *
     * @return
     */
    public Set<Class<? extends ModelElement>> getSelectedRealElementClasses() {
        Set<Class<? extends ModelElement>> returnClasses = new HashSet<>();
        for (NodeContainer nc : selectedRealNodeContainer) {
            if (lastSelected != nc) {
                returnClasses.add(nc.getElement().getClass());
            }
        }
        return returnClasses;
    }

    /**
     * Setzt die Selektion auf die übergebenen Objekte.<br>
     * Jedes Objekt wird maximal einmal hinzugefügt.
     *
     * @param selectedObjects
     */
    public void set(final Collection<? extends ElementContainer> selectedObjects) {
        clear();
        if (selectedObjects != null) {
            addAll(selectedObjects);
        }
    }

    @Override
    public boolean addAll(final Collection<? extends ElementContainer> selectedObjects) {
        if (selectedObjects == null) {
            return false;
        }
        boolean returnValue = false;
        for (ElementContainer ec : selectedObjects) {
            if (add(ec)) {
                returnValue = true;
            }
        }
        return returnValue;
    }

    @Override
    public boolean add(final ElementContainer ec) {
        //wenn das gleiche Element nochmal gesetzt werden soll -> raus
        if (ec == null || ec == lastSelected) {
            return false;
        }
        setLastSelected(ec);
        //Knickpunkt hinzugefügt
        if (ec instanceof BendpointContainer) {
            return selectedBendpointContainer.add((BendpointContainer) ec);
        }
        //Edge hinzugefügt
        if (ec instanceof EdgeContainer) {
            return selectedEdgeContainer.add((EdgeContainer) ec);
        }

        //Node (der kein Knickpunkt ist) hinzugefügt
        if (!selectedRealNodeContainer.add((NodeContainer) ec)) {
            return false;
        }

        //braucht nur ausgeführt werden, wenn ein richtiger Node hinzugekommen ist
        updateSelectionState();
        return true;
    }

    @Override
    public void clear() {
        selectedRealNodeContainer.clear();
        selectedBendpointContainer.clear();
        selectedEdgeContainer.clear();
        updateSelectionState();
        setLastSelected(null);
    }

    @Override
    public boolean contains(final Object o) {
        if (o instanceof BendpointContainer) {
            return selectedBendpointContainer.contains(o);
        } else if (o instanceof NodeContainer) {
            return selectedRealNodeContainer.contains(o);
        } else if (o instanceof EdgeContainer) {
            return selectedEdgeContainer.contains(o);
        }
        return false;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Iterator<ElementContainer> iterator() {
        return new SelectionIterator();
    }

    public Iterable<ElementContainer> iterable() {
        return () -> new SelectionIterator();
    }

    private class SelectionIterator implements Iterator<ElementContainer> {

        private int index;

        private final Iterator<NodeContainer> nodeContainerIt;

        private final Iterator<EdgeContainer> edgeContainerIt;

        private final Iterator<BendpointContainer> bendpointContainerIt;

        public SelectionIterator() {
            index = 0;
            nodeContainerIt = selectedRealNodeContainer.iterator();
            edgeContainerIt = selectedEdgeContainer.iterator();
            bendpointContainerIt = selectedBendpointContainer.iterator();
        }

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public ElementContainer next() {
            int nodesCount = selectedRealNodeContainer.size();
            int edgesCount = selectedEdgeContainer.size();
            ElementContainer ec = null;
            if (index < nodesCount) {
                ec = nodeContainerIt.next();
            } else if (index < nodesCount + edgesCount) {
                ec = edgeContainerIt.next();
            } else if (index < nodesCount + edgesCount + selectedBendpointContainer.size()) {
                ec = bendpointContainerIt.next();
            }
            index++;
            if (ec == null) {
                throw new NoSuchElementException();
            }
            return ec;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * @return
     */
    public Iterable<NodeContainer> iterableRealElementContainer() {
        return selectedRealNodeContainer;
    }

    /**
     * @return
     */
    public Iterable<EdgeContainer> iterableEdgeContainer() {
        return selectedEdgeContainer;
    }

    /**
     * @return
     */
    public Iterable<BendpointContainer> iterableBendpointContainer() {
        return selectedBendpointContainer;
    }

    /**
     * Liefert eine neu erzeugte Liste aller selektierten Container. Die Reihenfolge der
     * Elemente ist zufällig, aber das zueltzt selektierte Element ist immer ganz hinten
     * in der Liste. Diese Rückgabeliste kann genutzt werden, um über die Funktion {@link #set(Collection)} eine alte Selektion wiederherzustellen.
     *
     * @return
     */
    public List<ElementContainer> getSelectedContainer() {
        ArrayList<ElementContainer> al = new ArrayList<>(size());
        al.addAll(selectedRealNodeContainer);
        al.addAll(selectedEdgeContainer);
        al.addAll(selectedBendpointContainer);
        //sicher stellen, dass das lastSelected Element auch als letztes Element in der Liste ist
        int i = al.indexOf(lastSelected);
        if (i >= 0) {
            al.add(al.remove(i));
        }
        return al;
    }

    @Override
    public boolean remove(final Object ec) {
        boolean removed = false;
        boolean update = false;
        if (ec instanceof BendpointContainer) {
            removed = selectedBendpointContainer.remove(ec);
            if (removed && !preventUpdate) {
                update = true;
            }
        } else if (ec instanceof NodeContainer) {
            removed = selectedRealNodeContainer.remove(ec);
        } else if (ec instanceof EdgeContainer) {
            removed = selectedEdgeContainer.remove(ec);
        } else {
            return removed;
        }

        if (removed) {
            if (ec == lastSelected) {
                setLastSelected(null);
                if (selectedRealNodeContainer.size() > 0) {
                    setLastSelected(selectedRealNodeContainer.iterator().next());
                } else if (selectedEdgeContainer.size() > 0) {
                    setLastSelected(selectedEdgeContainer.iterator().next());
                } else if (selectedBendpointContainer.size() > 0) {
                    setLastSelected(selectedBendpointContainer.iterator().next());
                }
            }
            if (update) {
                updateSelectionState();
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        boolean pu = preventUpdate;
        preventUpdate = true;
        boolean removed = false;
        for (Object o : c) {
            if (remove(o)) {
                removed = true;
            }
        }
        preventUpdate = pu;
        if (removed) {
            updateSelectionState();
        }
        return removed;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        boolean pu = preventUpdate;
        preventUpdate = true;
        boolean removed = false;
        for (NodeContainer kc : selectedRealNodeContainer) {
            if (!c.contains(kc)) {
                removed = remove(kc);
            }
        }
        for (EdgeContainer kc : selectedEdgeContainer) {
            if (!c.contains(kc)) {
                removed = remove(kc);
            }
        }
        for (BendpointContainer kc : selectedBendpointContainer) {
            if (!c.contains(kc)) {
                removed = remove(kc);
            }
        }
        preventUpdate = pu;
        if (removed) {
            updateSelectionState();
        }
        return removed;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size()];
        System.arraycopy(selectedRealNodeContainer.toArray(), 0, array, 0, selectedRealNodeContainer.size());
        System.arraycopy(selectedEdgeContainer.toArray(), 0, array, selectedRealNodeContainer.size(), selectedEdgeContainer.size());
        int destPos = selectedRealNodeContainer.size() + selectedEdgeContainer.size() - 1;
        if (destPos < 0) {
            destPos = 0;
        }
        System.arraycopy(selectedBendpointContainer.toArray(), 0, array, destPos, selectedBendpointContainer.size());
        return array;
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        ArrayList<ElementContainer> al = new ArrayList<>(size());
        al.addAll(selectedRealNodeContainer);
        al.addAll(selectedEdgeContainer);
        al.addAll(selectedBendpointContainer);
        return al.toArray(a);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("lastSelected=");
        sb.append(lastSelected == null ? "null" : lastSelected.toString());
        sb.append("\n");
        sb.append("Class=");
        sb.append(mostSpecialRealElementClass);
        sb.append("\n");
        sb.append(selectedRealNodeContainer.toString());
        sb.append("\n");
        sb.append(selectedEdgeContainer.toString());
        sb.append("\n");
        sb.append(selectedBendpointContainer.toString());
        sb.append("\n");
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Aktualisiert die <code>mostSpecialFullSelectionClass</code> und alle <code>boolean</code>-Werte dieser Klasse.
     */
    public void updateSelectionState() {
        if (preventUpdate) {
            return;
        }
        //mostSpecialClass mit null und den Rest false initialisieren
        mostSpecialRealElementClass = null;

        //es ist nichts selektiert -> raus
        if (selectedRealNodeContainer.size() == 0) {
            return;
        }

        if (selectedRealNodeContainer.size() == 1) {
            mostSpecialRealElementClass = lastSelected.getElement().getClass();
            return;
        }

        //speziellste gemeinsame Oberklasse aller selektierten Elemente (nicht von deren Containern) ermitteln
        for (Iterator<NodeContainer> elemIt = selectedRealNodeContainer.iterator(); elemIt.hasNext();) {
            NodeContainer kc = elemIt.next();
            mostSpecialRealElementClass = ReflectionUtils.getCommonSuperClass(mostSpecialRealElementClass, kc.getElement().getClass()).asSubclass(ModelElement.class);
            if (mostSpecialRealElementClass == COMMON_REAL_ELEMENTS_SUPER_CLASS) {
                break;
            }
        }
    }

    /**
     * Gibt die erste gemeinsame Klasse aller selektierten Elemente zurück.<br>
     * In der Selektion sind zwar {@link ElementContainer}, aber hier kommt die Oberklasse
     * der in den Containern befindlichen Unterklassen von {@link ModelElement} zurück.
     * Wenn nichts selektiert ist, ist dieser Wert <code>null</code>.
     *
     * @return
     */
    public Class<? extends ModelElement> _getMostSpecialRealElementsClass() {
        return mostSpecialRealElementClass;
    }

    /**
     * Liefert <code>true</code>, wenn die selektierten Elemente vereinbar sind. Das gilt,
     * wenn die <code>mostSpecialFullSelectionElementClass</code> nicht abtract ist und wenigstens 2 Elemente selektiert sind.
     *
     * @return Returns the joinableElements.
     */
    public boolean isJoinableElementsSelected() {
        return mostSpecialRealElementClass != null && !Modifier.isAbstract(mostSpecialRealElementClass.getModifiers()) && selectedRealNodeContainer.size() > 1;
    }

    /**
     * Liefert <code>true</code>, wenn nur Knickpunkte in der Selektion vorkommen.
     *
     * @return
     */
    public boolean isSelectedOnlyBendpoints() {
        return getSelectedBendpointContainerCount() > 0 && getSelectedEdgeContainerCount() == 0 && getSelectedRealElementContainerCount() == 0;
    }

    /**
     * Gibt wieder, ob alle ausgewählten Elemente <em>unique</em> sind (= ohne grafische
     * Repräsentation immer in allen Teilmodellen vorkommen).
     *
     * @return
     */
    public boolean isSelectedOnlyUniqueNodes() {
        for (ElementContainer ec : selectedRealNodeContainer) {
            if (!ModelConstants.isUnique(ec.getElement().getClass())) {
                return false;
            }
        }

        //  Falls nur Kanten ausgewählt sind, ist selectedRealNodeContainer leer.
        //  Damit wird korrekterweise true zurückgegeben , da alle Kanten unique sind.
        //  D.h., dass ein Durchlaufen der selektierten Kanten entfallen kann.

        return true;
    }

    /**
     * Gibt wieder, ob nur untergeordnete Elemente in den RealNodes selektiert sind.
     *
     * @return
     */
    public boolean isSelectedOnlySlaveRealNodes() {
        for (ElementContainer ec : selectedRealNodeContainer) {
            if (!ModelConstants.isSlaveType(ec.getElement().getClass())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gibt wieder, ob ausschließlich {@link ElementContainer} von {@link Textfield} und {@link Knickpunkt} selektiert sind.
     *
     * @return
     */
    public boolean isSelectedOnlyBendpointsAndTextfields() {
        Collection<?> elements = GDCollection.getModelElements(selectedRealNodeContainer);
        return CollectionUtils.containsOnlyInstancesOf(elements, true, Textfield.class, Knickpunkt.class);
    }

    /**
     * Liefert die Anzahl an selektierten Modellelementen (also alles außer Knickpunkte und Kanten).
     *
     * @return
     */
    public int getSelectedRealElementContainerCount() {
        return selectedRealNodeContainer.size();
    }

    /**
     * Liefert die Anzahl der Knickpunkte in der Selektion.
     *
     * @return
     */
    public int getSelectedBendpointContainerCount() {
        return selectedBendpointContainer.size();
    }

    /**
     * Liefert die Anzahl der Kanten in der Selektion.
     *
     * @return
     */
    public int getSelectedEdgeContainerCount() {
        return selectedEdgeContainer.size();
    }

}

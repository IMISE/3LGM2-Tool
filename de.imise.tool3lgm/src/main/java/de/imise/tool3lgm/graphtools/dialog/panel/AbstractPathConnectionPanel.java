package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Static.contextGenerator;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.ReflectionUtils;
import de.imise.util.StringUtils;

/**
 * Panel für alle einfachen Verbindungen zwischen 2 Elementen
 *
 * @author AXS
 * @created 25.04.2017
 */
public abstract class AbstractPathConnectionPanel extends ConnectedElementsPanel {

    /** Der MetaPfad zu anderen Elementen */
    protected SimpleMetaPath metaPath;

    /** Label vor dem verbundenen Element mit der Art des Elementes */
    protected final JLabel westLabel;

    /**
     * Indikator, ob über den Pfad verbundene Elemente immer eindeutig an einem Punkt angehängt werden können (=<code>true</code>) oder
     * ob es mehrere Möglichkeiten gibt, weil Zwischenpfade mehrfach existieren können.
     */
    protected final boolean isConnectionPointUnique;

    /**
     * Panel für eine einfache Assoziation. Das Label trägt den Anzeigenamen der letzten Elementart.
     *
     * @param dialog
     * @param simpleMetaPath
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final SimpleMetaPath simpleMetaPath) {
        this(dialog, false, simpleMetaPath);
    }

    /**
     * Panel für eine einfache Assoziation. Gelabelt wird das verbundene Element der letzten Edge oder die letzte Edge selbst.
     *
     * @param dialog
     * @param labelEdgeName wenn <code>true</code> dann wird ans Labels statt des Namens der über die letzte Edge im Pfad verbundenen
     *            Elementart der Name der letzten Edge selbst ans Label geschrieben.
     * @param simpleMetaPath
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final boolean labelEdgeName, final SimpleMetaPath simpleMetaPath) {
        this(dialog, -1, labelEdgeName, simpleMetaPath);
    }

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param labelEdgeIndex Index der Edge, die vorgibt, was als searchElementClass angesehen werden soll, also was ans Label geschrieben
     *            wird. Es wird immer die Endklasse des Pfades bis zur Edge mit dem jeweiligen Index ans Label geschrieben. Wird ein Wert < 0
     *            übergeben, dann wird dieser Wert von der Anzahl der Kanten im Gesamtpfad abgezogen, um auf den tatsächlichen Index zu kommen.
     * @param labelEdgeName wenn <code>true</code> dann wird ans Labels statt des Namens der verbundenen Elementart,
     *            der Name der Edge selbst ans Label geschrieben. Welche Edge im Pfad das ist, wird durch labelEdgeIndex
     *            festgelegt.
     * @param simpleMetaPath
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final int labelEdgeIndex, final boolean labelEdgeName, final SimpleMetaPath simpleMetaPath) {
        super(dialog);
        metaPath = simpleMetaPath;
        searchElementClass = getInitialSearchElementClass(metaPath);
        isConnectionPointUnique = isConnectionPointUnique();

        // Das WestLabel auf jeden Fall initialisieren, denn es kann von anderen Panels dann hinzugefügt werden
        westLabel = new JLabel();
        //bei allen SingleConnectionPanels kann das Westlabel auch die MouseActions bekommen, so dass man auf dem Label an das verknüpfte Element kommt
        if (isSingleConnectionPath()) {
            addMouseActions(westLabel);
        }
        String westLabelText;
        if (labelEdgeName) {
            Class<? extends Edge> edgeClass = getEdgeClassInPath(labelEdgeIndex);
            Direction directionInPath = getDirectionInPath(labelEdgeIndex);
            westLabelText = elementsNameBuilder.getMetaAssociationName(edgeClass, directionInPath);
        } else {
            Class<? extends ModelElement> labelPathStepEndClass = MetaPathFunctions.getElementaryPathsConnectingClass(metaPath, labelEdgeIndex);
            //zur Beschriftung des Labels wird immer die speziellere Klasse genommen aus Endklasse des Pfades und searchElementClass. Weil immer nur davon können die verbundenen Elemente sein.
            if (labelPathStepEndClass == null || labelPathStepEndClass.isAssignableFrom(searchElementClass)) {
                labelPathStepEndClass = searchElementClass;
            }
            westLabelText = elementsNameBuilder.getDisplayableName(!metaPath.isSingleConnection(), labelPathStepEndClass);
        }
        westLabelText = StringUtils.capitalizeFirstChar(westLabelText); // Den ersten Buchstaben des Labels immer groß schreiben
        westLabel.setText(westLabelText);
        setName(westLabelText);
    }

    /**
     * Gibt die gemeinsame Oberklasse aller Endklassen des Pfades zurück. Das ist die SearchElementClass.
     *
     * @param metaPath
     * @return
     */
    private static Class<? extends ModelElement> getInitialSearchElementClass(final AbstractMetaPath metaPath) {
        Set<Class<? extends ModelElement>> endClasses = metaPath.getEndClasses();
        Class<?> superEndClass = ReflectionUtils.getCommonSuperClass(endClasses);
        Class<? extends ModelElement> searchElementClass = superEndClass.asSubclass(ModelElement.class);
        return searchElementClass;
    }

    /**
     * @return
     */
    public Class<? extends Edge> getLastEdgeClassInPath() {
        return getEdgeClassInPath(-1);
    }

    /**
     * @return
     */
    public Direction getLastDirectionInPath() {
        return getDirectionInPath(-1);
    }

    /**
     * Gibt einen Elementarpfad anhand eines übergebenen Index zurück. Ist der Index >= 0, dann wird genau der Index zurück gegeben. Ist der Index <
     * 0, dann wird der übergebene Index von der Länge der Geamtliste der Elementarfade abgezogen. Möchte man also den letzten Elementarpfad haben,
     * muss man -1 übergeben, für den vorletzten -2 usw.
     *
     * @param index
     * @return
     */
    public ElementaryMetaPath getElementaryMetaPathInPath(final int index) {
        return MetaPathFunctions.getElementaryMetaPathInPath(metaPath, index);
    }

    /**
     * @param index
     *            Index der Kante im Pfad, wenn dieser eindeutig ist. Wird ein Wert < 0 übergeben, dann ergibt sich der Index aus der Summe der
     *            Gesamtanzahl der Elementarpfade und diesem Wert.
     * @return
     */
    public Direction getDirectionInPath(final int index) {
        ElementaryMetaPath elementaryMetaPathInPath = getElementaryMetaPathInPath(index);
        return elementaryMetaPathInPath == null ? null : elementaryMetaPathInPath.getDirection();
    }

    /**
     * @param index
     *            Index der Kante im Pfad, wenn dieser eindeutig ist. Wird ein Wert < 0 übergeben, dann ergibt sich der Index aus der Summe der
     *            Gesamtanzahl der Elementarpfade und diesem Wert.
     * @return
     */
    public Class<? extends Edge> getEdgeClassInPath(final int index) {
        ElementaryMetaPath elementaryMetaPathInPath = getElementaryMetaPathInPath(index);
        return elementaryMetaPathInPath == null ? null : elementaryMetaPathInPath.getEdgeClass();
    }

    /**
     * @return
     */
    public int getEdgesInPathCount() {
        return metaPath.getElementaryMetaPaths().size();
    }

    /**
     * @return
     */
    public final JLabel getWestLabel() {
        return westLabel;
    }

    /**
     * Liefert <code>true</code>, wenn das letzte Element des Pfades nur existieren kann, wenn es mit einem
     * auf dem Pfad davor liegenden Element verbunden ist. Das wird gebraucht, um zu entscheiden, ob ein neu
     * angelegtes EndElement des Pfades immer sofort verbunden werden muss.
     *
     * @return
     */
    protected boolean isLastPathElementDependent() {
        ElementaryMetaPath lastElementaryMetaPathInPath = getElementaryMetaPathInPath(-1);
        if (lastElementaryMetaPathInPath == null) {
            return false;
        }
        EdgeCardinality backwardCardinality = lastElementaryMetaPathInPath.getBackwardCardinality();
        int minCardinality = backwardCardinality.min();
        return minCardinality > 0;
    }

    /**
     * Liefert <code>true</code>, wenn das Element des Panels/Dialoges nur existieren kann, wenn es eine Verbindung über die letzte Edge des Pfades
     * hat . Das wird gebarucht, um zu entscheiden, ob man anbieten kann, diese Verbindung zu lösen oder nicht. Wenn der Pfad keine einfache Liste von
     * Elementarpfaden ist, dann wird davon ausgegangen, dass das letzte Pfadelement gebraucht wird
     *
     * @return
     */
    protected boolean isLastPathElementNeededForExistence() {
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        //wenn der Pfad keine einfache Liste von Elementarpfaden ist, dann wird davon ausgegangen, dass das letzte Pfadelement gebraucht wird
        if (elementaryMetaPaths.isEmpty()) {
            return true;
        }
        ElementaryMetaPath lastElementaryMetaPath = elementaryMetaPaths.get(elementaryMetaPaths.size() - 1);
        //Verbindungen, die durch InstanciationEgdes bestehen, kann man nicht einfach lösen/ändern und gelten als existenznotwendig
        Class<? extends Edge> edgeClass = lastElementaryMetaPath.getEdgeClass();
        if (InstanciationEdge.class.isAssignableFrom(edgeClass)) {
            return true;
        }
        EdgeCardinality forwardCardinality = lastElementaryMetaPath.getForwardCardinality();
        int minCardinality = forwardCardinality.min();
        return minCardinality > 0;
    }

    /**
     * Liefert <code>true</code>, wenm der Pfad maximal ein Element als Ergebnis liefert, also das StartElement maximal ein Mal mit dem EndElement
     * verbunden sein darf.
     * Diese Funktion (genau wie die anderen isLastPathElementDependent(), isLastPathElementNeededForExistence() und isConnectionPointUnique()) könnte
     * man auch direkt in die Pfade schreiben (falls sie noch woanders gebraucht werden))
     *
     * @return
     */
    protected boolean isSingleConnectionPath() {
        ElementaryMetaPath lastElementaryMetaPathInPath = getElementaryMetaPathInPath(-1);
        //wenn der Pfad keine einfache Liste von Elementarpfaden ist, dann wird davon ausgegangen, dass mehrere Verbindungen mgl. sind
        if (lastElementaryMetaPathInPath == null) {
            return false;
        }
        return lastElementaryMetaPathInPath.getForwardCardinality().max() == 1;
    }

    /**
     * Liefert <code>true</code>, wenn der durch die Kanten vorgegebene Pfad eindeitig festlegt, wo zu verbindende Elemente verknüpft werden.
     * Sobald in einem Pfad der Länge > 1 (also mind. aus 2 Kanten) eines der mittleren Elemente mehrfach mit dem Ausgangselement verbunden
     * sein kann, ist nicht mehr eindeutig, wo die Endelemente angehängt werden sollen.
     * Außerdem ist der Pfad nicht eindeutig, wenn die letzte Kante eine {@link MultipleEdge} ist, also eine Kante, bei der dieselben Elemente
     * mehrfach miteinander verbunden sein können.
     *
     * @return
     */
    protected boolean isConnectionPointUnique() {
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        //wenn der Pfad keine einfache Liste von Elementarpfaden ist, dann wird davon ausgegangen, dass das der Verbindungspunkt nicht eindeutig ist
        if (elementaryMetaPaths.isEmpty()) {
            return false;
        }
        int lastElementaryMetaPathIndex = elementaryMetaPaths.size() - 1;
        //für alle Kanten außer der letzten
        for (int i = 0; i < lastElementaryMetaPathIndex; i++) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(i);
            //hole die maximale Verbindungsanzahl zum nächsten Element
            int maxCardinality = elementaryMetaPath.getForwardCardinality().max();
            //wenn dieses Zwischenelement mehrfach verbunden sein kann
            if (maxCardinality > 1) {
                //nicht eindeutig
                return false;
            }
        }
        //System.err.println(edgeClasses[lastEdgeIndex].getSimpleName() + " " + !MultipleEdge.class.isAssignableFrom(edgeClasses[lastEdgeIndex]));
        ElementaryMetaPath lastElementaryMetaPath = elementaryMetaPaths.get(lastElementaryMetaPathIndex);
        Class<? extends Edge> lastEdgeClass = lastElementaryMetaPath.getEdgeClass();
        return !MultipleEdge.class.isAssignableFrom(lastEdgeClass);
    }

    /**
     * Legt für das übergebene Startelement den Teilpfad ab startEdgeIndex an und hängt die übergebenen elements2connect an.
     *
     * @param startElement Das Element bei dem der Teilpfad losgehen soll.
     * @param elements2Connect Die Elemente die am Ende angehängt werden sollen
     * @param startEdgeIndex Index der Edge, bei der der anzulegende Teilpfad losgeht
     */
    protected void connect(final ModelElement startElement, final Iterable<ModelElement> elements2Connect, final int startEdgeIndex) {
        GraphDocument selDoc = getSelectedGraphDocument();
        GDCollection gdcoll = selDoc.getCollection();
        ModelElement targetElement = startElement;
        int pid = getTransactionID();
        //wenn ein gültiges Element2Connect übergeben wurde, dann muss man den Pfad nur bis zur vorletzten Edge
        //anlegen, sonst bis einschließlich zur letzten
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        int elementaryMetaPathCount = elementaryMetaPaths.size();
        int edgeSearchStopIndex = elements2Connect != null ? elementaryMetaPathCount - 1 : elementaryMetaPathCount;
        for (int i = startEdgeIndex; i < edgeSearchStopIndex; i++) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(i);
            Class<? extends Edge> edgeClass2Create = elementaryMetaPath.getEdgeClass();
            Direction edgeClass2CreateDirection = elementaryMetaPath.getDirection();
            elementaryMetaPath = i + 1 < elementaryMetaPathCount ? elementaryMetaPaths.get(i + 1) : null;
            Class<? extends Edge> nextEdgeClass2Create = elementaryMetaPath != null ? elementaryMetaPath.getEdgeClass() : null;
            //wenn es noch eine nächte Edge gibt, dann gibt es auch noch eine nächste direction. Wenn nicht wird einfach FORWARD übergeben, weil das egal ist
            Direction nextEdgeClass2CreateDirection = elementaryMetaPath != null ? elementaryMetaPath.getDirection() : FORWARD;
            targetElement = MetaPathFunctions.createNodeWithContainerAndDependents(selDoc, targetElement, edgeClass2Create, edgeClass2CreateDirection, nextEdgeClass2Create, nextEdgeClass2CreateDirection, pid);
        }
        //wenn gültige elments2Connect übergeben wurde, dann müssen sie an das vorletzte Pfadelement angehängt werden
        if (edgeSearchStopIndex < elementaryMetaPathCount) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(edgeSearchStopIndex);
            Class<? extends Edge> edgeClass2Create = elementaryMetaPath.getEdgeClass();
            Direction direction = elementaryMetaPath.getDirection();
            for (ModelElement element2Connect : elements2Connect) {
                link(gdcoll, targetElement, element2Connect, edgeClass2Create, direction, pid);
            }
        }
    }

    /**
     * Verbindet die beiden Elemente je nach übergebener Richtung vorwärts oder rückwärts.
     *
     * @param gdcoll
     * @param startElement
     * @param endElement
     * @param edgeClass
     * @param direction
     * @param pid
     */
    protected static void link(final GDCollection gdcoll, final ModelElement startElement, final ModelElement endElement, final Class<? extends Edge> edgeClass, final Direction direction, final int pid) {
        //das neue Element mit dem startElement verknüpfen
        if (direction == FORWARD) {
            gdcoll.link(edgeClass, startElement, endElement, pid);
        } else {
            gdcoll.link(edgeClass, endElement, startElement, pid);
        }
    }

    /**
     * Löst die Verbindung zwischen Start- und Endelement in der angegebenen Richtung
     *
     * @param gdcoll
     * @param startElement
     * @param endElement
     * @param edgeClass
     * @param direction
     * @param pid
     */
    protected static void unlink(final GDCollection gdcoll, final ModelElement startElement, final ModelElement endElement, final Class<? extends Edge> edgeClass, final Direction direction, final int pid) {
        if (direction == FORWARD) {
            gdcoll.unlink(startElement, endElement, edgeClass, pid);
        } else {
            gdcoll.unlink(endElement, startElement, edgeClass, pid);
        }
    }

    /**
     * Legt den kompletten Pfad bei jeweils dem ersten Element an, das ausgehend vom ModelElement des Dialoges
     * gefunden wird. Wenn der Pfad schon existiert, passiert nichts. Wenn er zu Teilen besteht, wird der Rest
     * angelegt.
     *
     * @param element2Connect wenn hier ein nicht null-Element übergeben wird, dann wird dieses als letztes verknüpft.
     *            Ist es null wird auch das letzte Element des Pfades neu angelegt.
     */
    protected void connectToFirstPath(final ModelElement element2Connect) {
        ModelElement me = dialog.getModelElement();
        //für den gesamten Pfad der angelegt werden muss
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        int elementaryMetaPathCount = elementaryMetaPaths.size();
        for (int i = 0; i < elementaryMetaPathCount; i++) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(i);
            Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
            Direction direction = elementaryMetaPath.getDirection();
            //hole die mit dem aktuellen me verbundenen Elemente der aktuellen Kantenart
            List<ModelElement> connectedElements = me.getConnectedElements(ModelElement.class, edgeClass, direction);
            //wenn bereits mind. ein verbundenes Element ex.
            if (i + 1 < elementaryMetaPathCount && !connectedElements.isEmpty()) {
                //hole das erste
                me = connectedElements.get(0);
                continue;
            } else {
                List<ModelElement> elements2Connect = element2Connect != null ? ImmutableList.of(element2Connect) : null;
                connect(me, elements2Connect, i);
                break;
            }
        }
    }

    protected abstract Object getSelection(MouseEvent e);

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die auf Mouse-Aktionen für ein selektiertes Element reagiert.
     *
     * @return
     */
    protected final LGMAction getMouseClickedAction() {
        return new LGMAction() {

            @Override
            public void execute(final EventObject eo) {
                MouseEvent e = (MouseEvent) eo;
                if (e.getClickCount() > 0) {
                    Object selection = getSelection(e);
                    executeMouseClickedAction(selection, e);
                }
            }
        };

    }

    /**
     * Wenn die übergebene Selektion ein {@link ElementContainer} oder {@link ModelElement} ist, dann
     * wird bei einem Rechtsklick das Kontextmenü des Elementes gezeigt oder bei einem Doppelklick
     * wird der Eigenschaftsdialog des Elementes geöffnet.
     *
     * @param selectionObject
     * @param e
     */
    private final void executeMouseClickedAction(final Object selectionObject, final MouseEvent e) {
        if (selectionObject == null) {
            return;
        }
        Iterable<?> fullSelection = selectionObject instanceof Iterable ? (Iterable<?>) selectionObject : ImmutableList.of(selectionObject);
        boolean popup = Tool3lgmConstants.isPopupTrigger(e);
        boolean doubleClick = !popup && e.getClickCount() > 1;
        //set selection
        GraphDocument doc = getGraphDocument();
        doc.deselectAll(true);
        ElementContainer selected = null;
        boolean first = true;
        for (Object selectedObject : fullSelection) {
            if (selectedObject instanceof ElementContainer) {
                selected = (ElementContainer) selectedObject;
            } else if (selectedObject instanceof ModelElement) {
                //da die Selektion sowieso in allen Teilmodellen ausgeführt wird, ist es hier ok, das ModelElement durch
                //den Container aus dem Hauptdokument zu ersetzen
                ModelElement me = (ModelElement) selectedObject;
                GraphDocument mainDoc = doc.getCollection().getMainGraphDocument();
                selected = me.getContainer(mainDoc);
            }
            if (selected != null) {
                if (first) {
                    doc.select(selected, getTransactionID());
                    first = false;
                } else {
                    doc.addToSelection(selected, getTransactionID());
                }
                if (doubleClick) {
                    doc.showPropertyDialog(selected.getElement());
                }
            }
        }
        if (popup && doc.isSelection()) {
            boolean showOnlyOpenPropertiesInContexMenu = sourceIs(e, JTable.class);
            JPopupMenu dialogSelectionContextMenu = contextGenerator.getDialogSelectionContextMenu(showOnlyOpenPropertiesInContexMenu);
            dialogSelectionContextMenu.show(e.getComponent(), e.getX() + 3, e.getY() + 3);
        }
    }

    private boolean sourceIs(final MouseEvent e, final Class<? extends Component> maybeSourceClass) {
        Object source = e.getSource();
        if (maybeSourceClass.isAssignableFrom(source.getClass())) {
            return true;
        }
        if (source instanceof Component) {
            Component comp = ((Component) source).getParent();
            while (comp != null) {
                if (maybeSourceClass.isAssignableFrom(comp.getClass())) {
                    return true;
                }
                comp = comp.getParent();
            }
        }
        return false;
    }

    /**
     * Liefert eine Liste aller Elementcontainer, die zum Verbinden zur Verfügung stehen. In der Regel sind das alle Elemente der
     * <code>searchElementClass</code>. Besteht, der Pfad des Panels aber nur aus einer einzigen Kante und diese hat zusätzlich einen
     * ConditionPath (also einen Pfad, über den das startElement außerdem noch mit den Zielelementen verbunden sein muss), dann
     * werden nur diese Zielelemente als zum Verbinden verfügbare Elemente zurück gegeben, die auch über diesen ConditonPath verbunden sind.
     *
     * @return
     */
    protected List<ElementContainer> getAvailableConnectables() {
        List<ElementContainer> available = null;
        //Pfad des Panels besteht aus genau einer Kante
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        if (elementaryMetaPaths.size() == 1) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(0);
            Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
            ModelElement me = getModelElement();
            MetaModel metaModel = me.getMetaModel();
            SimpleMetaPath conditionPath = metaModel.getConditionPath(edgeClass);
            //für diese eine Kante ist ein ConditionPath angegeben
            if (conditionPath != null) {
                if (elementaryMetaPath.getDirection() == BACKWARD) {
                    conditionPath = conditionPath.getOtherDirection();
                }
                Collection<ModelElement> conditionElements = MetaPathFunctions.getConnectedElements(me, conditionPath);
                available = new ArrayList<>(conditionElements.size());
                for (ModelElement conditionElement : conditionElements) {
                    available.add(conditionElement.getContainer(mainDoc));
                }
            }
        }
        if (available == null) {
            available = mainDoc.getElementContainers(searchElementClass, true);
        }
        return available;
    }

    protected final MouseListener mouseListener = new LGMMouseListener(null, null, null, getMouseClickedAction(), null);

    /**
     * Fügt der übergebenen Komponente die Doppelklick-Öffne-Eigenschaftsdialog-des-selektierten-Elementes-Action
     * hinzu und die Rechte-Maustastae-Öffnet-KontextMenü-Action.
     *
     * @param component
     */
    protected void addMouseActions(final Component component) {
        // Das unten auskommentierte hatte ich (AXS) mal gebaut, damit auf Comboboxen auch das Kontextmenü funktioniert. Das klappt aber auf dem MAC gar nicht
        // und es reicht der untere Aufruf völlig -> Testen ob das auch auf Windows so geht und wenn ja, dann das auskommentierte Löschen. Das muss aber mal
        // nötig gewesen sein, sonst hätte ich das nicht geschrieben. Evtl. auch Änderung durch neue Java-Version!?
        //        if (component instanceof JComboBox<?>) {
        //            JComboBox<?> box = (JComboBox<?>) component;
        //            //box.getEditor().getEditorComponent().addMouseListener(mouseListener); // funktioniert nicht!!!
        //            Component c[] = box.getComponents();
        //            for (int i = 0; i < c.length; i++) {
        //                // add event listener to all of the child components
        //                MouseListener[] mouseListeners = c[i].getMouseListeners();
        //                if (!CollectionUtils.arrayContains(mouseListeners, mouseListener)) {
        //                    c[i].addMouseListener(mouseListener);
        //                }
        //            }
        //        } else {
        component.addMouseListener(mouseListener);
        //        }
    }

}
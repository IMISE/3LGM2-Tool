package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Static.contextGenerator;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE_PLURAL;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE_SINGULAR;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_ELEMENT_NAME;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_ELEMENT_NAME_PLURAL;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_START_ELEMENT_TYPE;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_START_ELEMENT_TYPE_PLURAL;
import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_START_ELEMENT_TYPE_SINGULAR;
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

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.PathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.ReflectionUtils;
import de.imise.util.StringUtils;
import de.imise.util.swing.component.ParentComponentFinder;

/**
 * Panel für alle einfachen Verbindungen zwischen 2 Elementen
 *
 * @author AXS
 * @created 25.04.2017
 */
public abstract class AbstractPathConnectionPanel extends ConnectedElementsPanel {

    /**
     * Options which Label should be presented for a panel.
     *
     * @author AXS (20.01.2020)
     */
    public static enum PanelLabelOption {
        /**
         * Indicator to label the panel with the end element type name from the resources.
         * If the meta path is a single connection meta path, the singular will be shown
         * as label. If not the plural.
         */
        LABEL_END_ELEMENT_TYPE,
        /**
         * Indicator to label the panel with the end element type name from the resources
         * in singular.
         */
        LABEL_END_ELEMENT_TYPE_SINGULAR,
        /**
         * Indicator to label the panel with the end element type name from the resources
         * in plural.
         */
        LABEL_END_ELEMENT_TYPE_PLURAL,
        /**
         * Indicator to label the panel with last edge element type name from the resources.
         * If the meta path is a single connection meta path, the singular will be shown
         * as label. If not the plural.
         */
        LABEL_LAST_EDGE_ELEMENT_NAME,
        /**
         * Indicator to label the panel with last edge element type name from the resources
         * in singular.
         */
        LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR,
        /**
         * Indicator to label the panel with last edge element type name from the resources
         * in plural.
         */
        LABEL_LAST_EDGE_ELEMENT_NAME_PLURAL,
        /**
         * Indicator to label the panel with directed name of the connection from the resources.
         */
        LABEL_LAST_EDGE_CONNECTION_NAME,
        /**
         * Indicator to label the panel with the start element type of the last edge with
         * the name from the resources.
         * If the meta path is a single connection meta path, the singular will be shown
         * as label. If not the plural.
         */
        LABEL_LAST_EDGE_START_ELEMENT_TYPE,
        /**
         * Indicator to label the panel with the end element type of the last edge with
         * name from the resources in singular.
         */
        LABEL_LAST_EDGE_START_ELEMENT_TYPE_SINGULAR,
        /**
         * Indicator to label the panel with the end element type of the last edge with
         * name from the resources in plural.
         */
        LABEL_LAST_EDGE_START_ELEMENT_TYPE_PLURAL,
    }

    /** Der MetaPfad zu anderen Elementen */
    protected AbstractMetaPath metaPath;

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
     * @param metaPath
     */
    public AbstractPathConnectionPanel(final AbstractElementPropertyDialog dialog, final AbstractMetaPath metaPath) {
        this(dialog, LABEL_END_ELEMENT_TYPE, LABEL_LAST_EDGE_CONNECTION_NAME, metaPath);
    }

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param labelEdgeIndex Index der Edge, die vorgibt, was als searchElementClass angesehen werden soll, also was ans Label geschrieben
     *            wird. Es wird immer die Endklasse des Pfades bis zur Edge mit dem jeweiligen Index ans Label geschrieben. Wird ein Wert < 0
     *            übergeben, dann wird dieser Wert von der Anzahl der Kanten im Gesamtpfad abgezogen, um auf den tatsächlichen Index zu kommen.
     * @param titleLabelOption Das Label kann folgende Werte annehmen:
     *            <ul>
     *            <li>{@link PanelLabelOption#LABEL_END_ELEMENT_TYPE} = Anzeigename der EndElement-Art des MetaPfades</li>
     *            <li>{@link PanelLabelOption#LABEL_LAST_EDGE_ELEMENT_NAME} = Anzeigename der Element-Art der Kante mit dem Index labelEdgeIndex
     *            im MetaPfad</li>
     *            <li>{@link PanelLabelOption#LABEL_LAST_EDGE_CONNECTION_NAME} = Anzeigename der gerichteten Verbindung der Kante mit dem Index
     *            labelEdgeIndex
     *            im MetaPfad</li>
     *            </ul>
     * @param westLabelOption analog titleLabelOption
     * @param metaPath
     */
    public AbstractPathConnectionPanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final AbstractMetaPath metaPath) {
        super(dialog);
        this.metaPath = metaPath;
        searchElementClass = getInitialSearchElementClass(metaPath);
        isConnectionPointUnique = isConnectionPointUnique();

        // Das WestLabel auf jeden Fall initialisieren, denn es kann von anderen Panels dann hinzugefügt werden
        westLabel = new JLabel();
        //bei allen SingleConnectionPanels kann das Westlabel auch die MouseActions bekommen, so dass man auf dem Label an das verknüpfte Element kommt
        if (metaPath.isSingleConnection()) {
            addMouseActions(westLabel);
        }
        String title = getTextByPanelLabelOption(titleLabelOption);
        setName(title);
        String westLabelText = getTextByPanelLabelOption(westLabelOption);
        westLabel.setText(westLabelText);
    }

    /**
     * Erstellt den Namen des Panels, der auch der String des westLabels wird.
     *
     * @param labelEdgeIndex
     * @param panelLabelOption
     * @return
     */
    private String getTextByPanelLabelOption(final PanelLabelOption panelLabelOption) {
        String westLabelText;
        int labelEdgeIndex = getEdgesInPathCount() - 1;
        if (panelLabelOption == LABEL_LAST_EDGE_CONNECTION_NAME) {
            if (labelEdgeIndex == 0) {
                Class<? extends Edge> edgeClass = getEdgeClassInPath(labelEdgeIndex);
                Direction directionInPath = getDirectionInPath(labelEdgeIndex);
                westLabelText = elementsNameBuilder.getMetaAssociationName(edgeClass, directionInPath);
            } else {//der Pfad besteht nicht aus einer einfachen Elementarpfadliste
                westLabelText = Tool3lgmConstants.getResString("verb");
            }
        } else {
            //Name of the class to display (end node or edge of metapath step)
            Class<? extends ModelElement> nameSourceClass;
            if (panelLabelOption == LABEL_LAST_EDGE_ELEMENT_NAME || panelLabelOption == LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR || panelLabelOption == LABEL_LAST_EDGE_ELEMENT_NAME_PLURAL) {
                nameSourceClass = getEdgeClassInPath(labelEdgeIndex);
            } else {
                boolean labelSecondLastElementClass = panelLabelOption == LABEL_LAST_EDGE_START_ELEMENT_TYPE || panelLabelOption == LABEL_LAST_EDGE_START_ELEMENT_TYPE_SINGULAR || panelLabelOption == LABEL_LAST_EDGE_START_ELEMENT_TYPE_PLURAL;
                int labelEdgeIndexDiff = labelSecondLastElementClass ? 1 : 0;
                nameSourceClass = MetaPathFunctions.getElementaryPathsConnectingClass(metaPath, labelEdgeIndex - labelEdgeIndexDiff);
                //zur Beschriftung des Labels wird immer die speziellere Klasse genommen aus Endklasse des Pfades und searchElementClass. Weil immer nur davon können die verbundenen Elemente sein.
                if (nameSourceClass == null || nameSourceClass.isAssignableFrom(searchElementClass)) {
                    nameSourceClass = searchElementClass;
                }
            }

            // display plural name?
            boolean plural;
            if (panelLabelOption == LABEL_END_ELEMENT_TYPE_SINGULAR || panelLabelOption == LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR || panelLabelOption == LABEL_LAST_EDGE_START_ELEMENT_TYPE_SINGULAR) {
                plural = false;
            } else if (panelLabelOption == LABEL_END_ELEMENT_TYPE_PLURAL || panelLabelOption == LABEL_LAST_EDGE_ELEMENT_NAME_PLURAL || panelLabelOption == LABEL_LAST_EDGE_START_ELEMENT_TYPE_PLURAL) {
                plural = true;
            } else {
                plural = !metaPath.isSingleConnection();
            }
            westLabelText = elementsNameBuilder.getDisplayableName(plural, nameSourceClass);
        }
        westLabelText = StringUtils.capitalizeFirstChar(westLabelText); // Den ersten Buchstaben des Labels immer groß schreiben
        return westLabelText;
    }

    /**
     * Gibt die gemeinsame Oberklasse aller Endklassen des Pfades zurück. Das ist die SearchElementClass.
     *
     * @param metaPath
     * @return
     */
    private static Class<? extends ModelElement> getInitialSearchElementClass(final AbstractMetaPath metaPath) {
        Set<Class<? extends ModelElement>> endClasses = metaPath.getEndClasses();
        Class<? extends ModelElement> searchElementClass = ReflectionUtils.getCommonSuperClassOfClasses(endClasses);
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
     * Gibt einen Elementarpfad anhand eines übergebenen Index zurück. Ist der Index >= 0, dann wird genau der Index zurück gegeben. Ist der Index
     * <
     * 0, dann wird der übergebene Index von der Länge der Geamtliste der Elementarfade abgezogen. Möchte man also den letzten Elementarpfad
     * haben,
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
    protected void connectOld(final ModelElement startElement, final Iterable<ModelElement> elements2Connect, final int startEdgeIndex) {
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
            ElementaryMetaPath nextElementaryMetaPath = i + 1 < elementaryMetaPathCount ? elementaryMetaPaths.get(i + 1) : null;
            targetElement = PathFunctions.createNodeWithContainerAndDependents(selDoc, startElement, elementaryMetaPath, nextElementaryMetaPath, pid);
        }
        //wenn gültige elments2Connect übergeben wurde, dann müssen sie an das vorletzte Pfadelement angehängt werden
        if (edgeSearchStopIndex < elementaryMetaPathCount) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(edgeSearchStopIndex);
            Class<? extends Edge> edgeClass2Create = elementaryMetaPath.getEdgeClass();
            Direction direction = elementaryMetaPath.getDirection();
            for (ModelElement element2Connect : elements2Connect) {
                gdcoll.link(targetElement, element2Connect, edgeClass2Create, direction, pid);
            }
        }
    }

    /**
     * Legt für das übergebene Startelement den Teilpfad ab startEdgeIndex an und hängt die übergebenen elements2connect an.
     *
     * @param startElement Das Element bei dem der Teilpfad losgehen soll.
     * @param elements2Connect Die Elemente die am Ende angehängt werden sollen
     * @param startEdgeIndex Index der Edge, bei der der anzulegende Teilpfad losgeht
     */
    protected final void connect(final ModelElement startElement, final Iterable<ModelElement> elements2Connect, final int startEdgeIndex) {
        GraphDocument selDoc = getSelectedGraphDocument();
        int pid = getTransactionID();
        GDCollection gdcoll = selDoc.getCollection();

        //Ausnahme für Mac-Java-Bug: wenn Dialoge auf dem MAC aus einem Drag&Drop-Ereignis heraus gestartet werden, kann man sie nicht mehr mit der Maus ansprechen. Nur mit Tasten.
        //Da dieser Bug nicht so einfach zu umgehen ist, wird in diesem Fall der Dialog einfach nicht angezeigt und der Name generiert.
        boolean dragNDropOnMac = Static.isDragNDropOnMac();
        boolean lastAutomaticMode = gdcoll.setAutomaticMode(dragNDropOnMac);

        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        if (!elementaryMetaPaths.isEmpty()) {
            SimpleMetaPath path2Create;
            if (startEdgeIndex == 0) {
                path2Create = metaPath instanceof SimpleMetaPath ? (SimpleMetaPath) metaPath : new SimpleMetaPath(elementaryMetaPaths);
            } else {
                int size = elementaryMetaPaths.size();
                List<ElementaryMetaPath> subelementaryMetaPaths = elementaryMetaPaths.subList(startEdgeIndex, size);
                path2Create = new SimpleMetaPath(subelementaryMetaPaths);
            }
            if (elements2Connect == null) {
                selDoc.createPath(startElement, null, path2Create, pid);
            } else {
                for (ModelElement endElement : elements2Connect) {
                    selDoc.createPath(startElement, endElement, path2Create, pid);
                }
            }
        }
        gdcoll.setAutomaticMode(lastAutomaticMode);
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
                GraphDocument mainDoc = doc.getCollection().getMainDoc();
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
                    //even if this dialog model element is a template element this
                    //Static call tries to open the dialog of the enventually existing
                    //element with the same id (hashString) in the currently selected
                    //doc
                    Static.showPropertyDialog(selected);
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
            return ParentComponentFinder.hasParent((Component) source, maybeSourceClass);
        }
        return false;
    }

    /**
     * Liefert eine Liste aller Elementcontainer, die zum Verbinden zur Verfügung stehen. In der Regel sind das alle Elemente der
     * <code>searchElementClass</code>. Besteht, der Pfad des Panels aber nur aus einer einzigen Kante und diese hat zusätzlich einen
     * ConditionMetaPath (also einen Pfad, über den das startElement außerdem noch mit den Zielelementen verbunden sein muss), dann
     * werden nur diese Zielelemente als zum Verbinden verfügbare Elemente zurück gegeben, die auch über diesen ConditonPath verbunden sind.
     * <br>
     * Außerdem wird geprüft, ob für ein verbindbares Element die Kante gar nicht mehr gelten soll.
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
            SimpleMetaPath conditionMetaPath = metaModel.getConditionMetaPath(edgeClass);
            //für diese eine Kante ist ein ConditionMetaPath angegeben
            if (conditionMetaPath != null) {
                if (elementaryMetaPath.getDirection() == BACKWARD) {
                    conditionMetaPath = conditionMetaPath.getOtherDirection();
                }
                Collection<ModelElement> conditionElements = PathFunctions.getConnectedElements(me, conditionMetaPath);
                available = new ArrayList<>(conditionElements.size());
                for (ModelElement conditionElement : conditionElements) {
                    available.add(conditionElement.getContainer(mainDoc));
                }
            }
            if (available == null) {
                available = mainDoc.getElementContainers(searchElementClass, true);
            }
            //alle available entfernen, für die die Kante nicht mehr gelten soll
            for (int i = available.size() - 1; i >= 0; i--) {
                ElementContainer ec = available.get(i);
                ModelElement availableMe = ec.getElement();
                Class<? extends ModelElement> elementClass = availableMe.getClass();
                boolean remove = false;
                if (elementaryMetaPath.getDirection() == FORWARD) {
                    remove = !MetaModel.isEndClass(edgeClass, elementClass);
                } else { // if (elementaryMetaPath.getDirection() == BACKWARD) {
                    remove = !MetaModel.isStartClass(edgeClass, elementClass);
                }
                if (remove) {
                    available.remove(i);
                }
            }
        } else {
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
    protected final void addMouseActions(final Component component) {
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
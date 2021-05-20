package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.Static.contextGenerator;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE_PLURAL;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE_SINGULAR;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_ELEMENT_NAME;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_ELEMENT_NAME_PLURAL;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_START_ELEMENT_TYPE;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_START_ELEMENT_TYPE_PLURAL;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_LAST_EDGE_START_ELEMENT_TYPE_SINGULAR;
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
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractElementaryPathError;
import de.imise.tool3lgm.graphtools.consistency.error.type.MissingPathError;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.PathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.Alphabetical;
import de.imise.util.ReflectionUtils;
import de.imise.util.StringUtils;
import de.imise.util.swing.component.ParentComponentFinder;

/**
 * Panel für alle einfachen Verbindungen zwischen 2 Elementen
 *
 * @author AXS
 * @created 25.04.2017
 */
public abstract class AbstractPathConnectionPanel extends ConnectedElementsPanel implements DisplayAndFixConsistencyErrorPanel {

    /**
     * Options which Label should be presented for a panel.
     *
     * @author AXS (20.01.2020)
     */
    public static enum PanelLabelOption {
        /**
         * Indicator to label the panel with the end element type name from the
         * resources. If the meta path is a single connection meta path, the
         * singular will be shown as label. If not the plural.
         */
        LABEL_END_ELEMENT_TYPE,
        /**
         * Indicator to label the panel with the end element type name from the
         * resources in singular.
         */
        LABEL_END_ELEMENT_TYPE_SINGULAR,
        /**
         * Indicator to label the panel with the end element type name from the
         * resources in plural.
         */
        LABEL_END_ELEMENT_TYPE_PLURAL,
        /**
         * Indicator to label the panel with last edge element type name from
         * the resources. If the meta path is a single connection meta path, the
         * singular will be shown as label. If not the plural.
         */
        LABEL_LAST_EDGE_ELEMENT_NAME,
        /**
         * Indicator to label the panel with last edge element type name from
         * the resources in singular.
         */
        LABEL_LAST_EDGE_ELEMENT_NAME_SINGULAR,
        /**
         * Indicator to label the panel with last edge element type name from
         * the resources in plural.
         */
        LABEL_LAST_EDGE_ELEMENT_NAME_PLURAL,
        /**
         * Indicator to label the panel with directed name of the connection
         * from the resources.
         */
        LABEL_LAST_EDGE_CONNECTION_NAME,
        /**
         * Indicator to label the panel with the start element type of the last
         * edge with the name from the resources. If the meta path is a single
         * connection meta path, the singular will be shown as label. If not the
         * plural.
         */
        LABEL_LAST_EDGE_START_ELEMENT_TYPE,
        /**
         * Indicator to label the panel with the end element type of the last
         * edge with name from the resources in singular.
         */
        LABEL_LAST_EDGE_START_ELEMENT_TYPE_SINGULAR,
        /**
         * Indicator to label the panel with the end element type of the last
         * edge with name from the resources in plural.
         */
        LABEL_LAST_EDGE_START_ELEMENT_TYPE_PLURAL,
    }

    /** The metapath to the connected elements */
    protected MetaPath metaPath;

    /** Label in front of the connected element with the type of element */
    protected final JLabel westLabel;

    /**
     * Indicator, whether elements connected by the path can always be attached
     * unambiguously to one point (=<code>true</code>) or whether there are
     * several possibilities, because intermediate paths can exist multiple
     * times.
     */
    protected final boolean isConnectionPointUnique;

    /**
     *
     */
    private final ModelElement modelElement;

    /**
     * Panel for simple association. The label has the display name of the last
     * element type.
     *
     * @param dialog
     * @param metaPath
     */
    public AbstractPathConnectionPanel(final AbstractElementPropertyDialog dialog, final MetaPath metaPath) {
        this(dialog, LABEL_END_ELEMENT_TYPE, LABEL_LAST_EDGE_CONNECTION_NAME, metaPath);
    }

    /**
     * Panel for simple association
     *
     * @param dialog
     * @param titleLabelOption Das Label kann folgende Werte annehmen:
     *            <ul>
     *            <li>{@link PanelLabelOption#LABEL_END_ELEMENT_TYPE} =
     *            Anzeigename der EndElement-Art des MetaPfades</li>
     *            <li>{@link PanelLabelOption#LABEL_LAST_EDGE_ELEMENT_NAME} =
     *            Anzeigename der Element-Art der Kante mit dem Index
     *            labelEdgeIndex im MetaPfad</li>
     *            <li>{@link PanelLabelOption#LABEL_LAST_EDGE_CONNECTION_NAME} =
     *            Anzeigename der gerichteten Verbindung der Kante mit dem Index
     *            labelEdgeIndex im MetaPfad</li>
     *            </ul>
     * @param metaPath
     */
    public AbstractPathConnectionPanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final MetaPath metaPath) {
        this(dialog, titleLabelOption, LABEL_LAST_EDGE_CONNECTION_NAME, metaPath);
    }

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param titleLabelOption Das Label kann folgende Werte annehmen:
     *            <ul>
     *            <li>{@link PanelLabelOption#LABEL_END_ELEMENT_TYPE} = Display
     *            name of the EndElement type of the MetaPath</li>
     *            <li>{@link PanelLabelOption#LABEL_LAST_EDGE_ELEMENT_NAME} =
     *            Display name of the element type of the edge with the index
     *            labelEdgeIndex in the MetaPath</li>
     *            <li>{@link PanelLabelOption#LABEL_LAST_EDGE_CONNECTION_NAME} =
     *            Display name of the directed connection of the edge with the
     *            index labelEdgeIndex in the MetaPath</li>
     *            </ul>
     * @param westLabelOption analog titleLabelOption
     * @param metaPath
     */
    public AbstractPathConnectionPanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final MetaPath metaPath) {
        super(dialog);
        this.metaPath = metaPath;
        modelElement = getPanelModelElement();
        searchElementClass = getInitialSearchElementClass(metaPath);
        isConnectionPointUnique = isConnectionPointUnique();

        // Make sure to initialize the WestLabel, because it can be added by other panels
        westLabel = new JLabel();
        //with all SingleConnectionPanels the west label can also get the MouseActions, so that
        //you can get to the connected element with a double click or the context menu on the label
        if (metaPath.isSingleConnection()) {
            addMouseActions(westLabel);
        }
        String title = getTextByPanelLabelOption(titleLabelOption);
        setName(title);
        String westLabelText = getTextByPanelLabelOption(westLabelOption);
        westLabel.setText(westLabelText);
    }

    /**
     * Creates the name of the panel, which also becomes the string of the
     * westLabel.
     *
     * @param labelEdgeIndex
     * @param panelLabelOption
     * @return
     */
    private String getTextByPanelLabelOption(final PanelLabelOption panelLabelOption) {
        String westLabelText;
        int pathLength = getEdgesInPathCount();
        int labelEdgeIndex = pathLength - 1;
        if (panelLabelOption == LABEL_LAST_EDGE_CONNECTION_NAME) {
            if (pathLength > 0) {
                Class<? extends Edge> edgeClass = getEdgeClassInPath(labelEdgeIndex);
                Direction directionInPath = getDirectionInPath(labelEdgeIndex);
                westLabelText = elementsNameBuilder.getMetaAssociationName(edgeClass, directionInPath);
            } else {//the path does not consist of a simple elementary path list
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
                nameSourceClass = MetaPathFunctions.getElementaryMetaPathsConnectingClass(metaPath, labelEdgeIndex - labelEdgeIndexDiff);
                //As text of the label always the more special class is taken from end class of the path
                //and searchElementClass (only the connected elements can be of this class)
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
            //if this is a connection from a regular element class to a pure template element
            //class so append "(Temlate)" to the westLabelText to mark the difference
            Class<? extends ModelElement> dialogModelElementClass = modelElement.getClass();
            if (nameSourceClass != dialogModelElementClass) {
                MetaModel metaModel = getMetaModel();
                if (metaModel.isPureTemplateElementClass(nameSourceClass)) {
                    String dialogElementDisplayableName = elementsNameBuilder.getDisplayableName(plural, dialogModelElementClass);
                    if (dialogElementDisplayableName.equals(westLabelText)) {
                        westLabelText = elementsNameBuilder.getDisplayableFullName(plural, dialogModelElementClass);
                    }
                }
            }
        }
        westLabelText = StringUtils.capitalizeFirstChar(westLabelText); // Always capitalize the first letter of the label
        return westLabelText;
    }

    /**
     * Checks wheter this panel must show a template element or the model
     * element. The template elememt will be shown if the metapath starts with a
     * {@link ElementaryMetaPath} which is defined between pure template
     * elenents.
     *
     * @return the real visible {@link ModelElement}
     */
    private final ModelElement getPanelModelElement() {
        boolean startsWitTemplateElementsElementaryMetaPath = MetaPathFunctions.startsWitTemplateElementsElementaryMetaPath(metaPath);
        ModelElement templateElementSource = dialog.getTemplateElementSource();
        ModelElement modelElement = startsWitTemplateElementsElementaryMetaPath && templateElementSource != null ? templateElementSource : super.getModelElement();
        return modelElement;
    }

    /**
     * @return modelElement
     */
    @Override
    public ModelElement getModelElement() {
        return modelElement;
    }

    /**
     * Returns the common superclass of all end classes of the path. This is the
     * searchElementClass.
     *
     * @param metaPath
     * @return
     */
    private static Class<? extends ModelElement> getInitialSearchElementClass(final MetaPath metaPath) {
        Set<Class<? extends ModelElement>> endClasses = metaPath.getEndClasses();
        Class<? extends ModelElement> searchElementClass = ReflectionUtils.getCommonSuperClassOfClasses(endClasses);
        return searchElementClass;
    }

    /**
     * @param metaPath
     * @return
     */
    public boolean hasMetaPath(final MetaPath metaPath) {
        return this.metaPath.isAssignable(metaPath);
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
     * Returns an elementary path using a given index. If the index >= 0,then
     * exactly the index is returned. If the index is < 0, then the given index
     * is subtracted from the length of the list of elementary paths. Thus, if
     * you want to have the last elementary path, you must pass index == -1, for
     * the second last -2, etc.
     *
     * @param index
     * @return
     */
    public ElementaryMetaPath getElementaryMetaPathInPath(final int index) {
        return MetaPathFunctions.getElementaryMetaPathInPath(metaPath, index);
    }

    /**
     * @param index Index of the edge in the path, if it is unique. If a value <
     *            0 is given, then the index results from the sum of the total
     *            number of elementary paths and this value.
     * @return
     */
    public Direction getDirectionInPath(final int index) {
        ElementaryMetaPath elementaryMetaPathInPath = getElementaryMetaPathInPath(index);
        return elementaryMetaPathInPath == null ? null : elementaryMetaPathInPath.getDirection();
    }

    /**
     * @param index Index of the edge in the path, if it is unique. If a value <
     *            0 is given, then the index results from the sum of the total
     *            number of elementary paths and this value.
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
     * @return <code>true</code>, if the path given by the edges uniquely
     *         defines where to connect elements. As soon as in a MetaPath of
     *         length > 1 (i.e. at least of 2 edges) one of the middle elements
     *         can be connected multiple times to the starting element, it is no
     *         longer clear where to append the end elements. Furthermore, the
     *         path is ambiguous if the last edge is a {@link MultipleEdge},
     *         i.e. an edge where the same elements can be connected several
     *         times.
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
     * Legt für das übergebene Startelement den Teilpfad ab startEdgeIndex an
     * und hängt die übergebenen elements2connect an.
     *
     * @param startElement Das Element bei dem der Teilpfad losgehen soll.
     * @param elements2Connect Die Elemente die am Ende angehängt werden sollen
     * @param startEdgeIndex Index der Edge, bei der der anzulegende Teilpfad
     *            losgeht
     */
    protected void connectOld(final ModelElement startElement, final Iterable<ModelElement> elements2Connect, final int startEdgeIndex) {
        LGMGraphDocument selectedDoc = getSelectedDoc();
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
            targetElement = PathFunctions.createNodeWithContainerAndDependents(selectedDoc, startElement, elementaryMetaPath, nextElementaryMetaPath, pid);
        }
        //wenn gültige elments2Connect übergeben wurde, dann müssen sie an das vorletzte Pfadelement angehängt werden
        if (edgeSearchStopIndex < elementaryMetaPathCount) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(edgeSearchStopIndex);
            Class<? extends Edge> edgeClass2Create = elementaryMetaPath.getEdgeClass();
            Direction direction = elementaryMetaPath.getDirection();
            GDCollection gdcoll = getCollection();
            for (ModelElement element2Connect : elements2Connect) {
                gdcoll.link(targetElement, element2Connect, edgeClass2Create, direction, pid);
            }
        }
    }

    /**
     * Legt für das übergebene Startelement den Teilpfad ab startEdgeIndex an
     * und hängt die übergebenen elements2connect an.
     *
     * @param startElement Das Element bei dem der Teilpfad losgehen soll.
     * @param elements2Connect Die Elemente die am Ende angehängt werden sollen
     * @param startEdgeIndex Index der Edge, bei der der anzulegende Teilpfad
     *            losgeht
     */
    protected final void connect(final ModelElement startElement, final Iterable<ModelElement> elements2Connect, final int startEdgeIndex) {
        int pid = getTransactionID();

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
            LGMGraphDocument selectedDoc = getSelectedDoc();
            if (elements2Connect == null) {
                //Ausnahme für Mac-Java-Bug: wenn Dialoge auf dem MAC aus einem Drag&Drop-Ereignis heraus
                //gestartet werden, kann man sie nicht mehr mit der Maus ansprechen. Nur mit Tasten.
                //Da dieser Bug nicht so einfach zu umgehen ist, wird in diesem Fall der Dialog einfach
                //nicht angezeigt und der Name generiert.
                boolean dragNDropOnMac = Static.isDragNDropOnMac();
                selectedDoc.createPath(startElement, null, path2Create, !dragNDropOnMac, pid);
            } else {
                for (ModelElement endElement : elements2Connect) {
                    selectedDoc.createPath(startElement, endElement, path2Create, pid);
                }
            }
        }
    }

    /**
     * Legt den kompletten Pfad bei jeweils dem ersten Element an, das ausgehend
     * vom ModelElement des Dialoges gefunden wird. Wenn der Pfad schon
     * existiert, passiert nichts. Wenn er zu Teilen besteht, wird der Rest
     * angelegt.
     *
     * @param element2Connect wenn hier ein nicht <code>null</code>-Element
     *            übergeben wird, dann wird dieses als letztes verknüpft. Ist es
     *            <code>null</code> wird auch das letzte Element des Pfades neu
     *            angelegt.
     */
    protected void connectToFirstPath(final ModelElement element2Connect) {
        ModelElement me = getModelElement();
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
            } else {
                List<ModelElement> elements2Connect = element2Connect != null ? ImmutableList.of(element2Connect) : null;
                connect(me, elements2Connect, i);
                break;
            }
        }
    }

    protected abstract Object getSelection(MouseEvent e);

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die auf
     * Mouse-Aktionen für ein selektiertes Element reagiert.
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
     * Wenn die übergebene Selektion ein {@link ElementContainer} oder
     * {@link ModelElement} ist, dann wird bei einem Rechtsklick das Kontextmenü
     * des Elementes gezeigt oder bei einem Doppelklick wird der
     * Eigenschaftsdialog des Elementes geöffnet.
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
        List<GraphDocument> docs = new ArrayList<>();
        GraphDocument mainDoc = getMainDoc();
        mainDoc.deselectAll(true);
        for (Object selectedObject : fullSelection) {
            ModelElement me = null;
            if (selectedObject instanceof ElementContainer) {
                ElementContainer ec = (ElementContainer) selectedObject;
                me = ec.getElement();
            } else if (selectedObject instanceof ModelElement) {
                me = (ModelElement) selectedObject;
            }
            String meID = me.getID();
            GDCollection gdcoll = me.getCollection();
            LGMGraphDocument selectedObjectMainDoc = gdcoll.getMainDoc();
            if (selectedObjectMainDoc != mainDoc && !docs.contains(selectedObjectMainDoc)) {
                docs.add(selectedObjectMainDoc);
                selectedObjectMainDoc.deselectAll(true);
            }
            ElementContainer selectedContainer = mainDoc.findContainerCoded(meID);
            if (selectedContainer == null) {
                selectedContainer = selectedObjectMainDoc.findContainerCoded(meID);
                selectedObjectMainDoc.addToSelection(selectedContainer, getTransactionID());
            } else {
                mainDoc.addToSelection(selectedContainer, getTransactionID());
            }
            if (selectedContainer != null) {
                if (doubleClick) {
                    //even if this dialog model element is a template element this
                    //Static call tries to open the dialog of the enventually existing
                    //element with the same ID in the currently selected
                    //doc
                    Static.showPropertyDialog(selectedContainer);
                }
            }
        }
        if (popup && mainDoc.isSelection()) {
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
     * Liefert eine Liste aller Elementcontainer, die zum Verbinden zur
     * Verfügung stehen. In der Regel sind das alle Elemente der
     * <code>searchElementClass</code>. Besteht, der Pfad des Panels aber nur
     * aus einer einzigen Kante und diese hat zusätzlich einen ConditionMetaPath
     * (also einen Pfad, über den das startElement außerdem noch mit den
     * Zielelementen verbunden sein muss), dann werden nur diese Zielelemente
     * als zum Verbinden verfügbare Elemente zurück gegeben, die auch über
     * diesen ConditonPath verbunden sind. <br>
     * Außerdem wird geprüft, ob für ein verbindbares Element die Kante gar
     * nicht mehr gelten soll.
     *
     * @return
     */
    protected final List<ElementContainer> getAvailableConnectables() {
        List<ElementContainer> available = null;
        //Pfad des Panels besteht aus genau einer Kante
        List<ElementaryMetaPath> elementaryMetaPaths = metaPath.getElementaryMetaPaths();
        GraphDocument mainDoc = getMainDoc();
        if (elementaryMetaPaths.size() == 1) {
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(0);
            Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();
            MetaModel metaModel = mainDoc.getMetaModel();
            SimpleMetaPath conditionMetaPath = metaModel.getConditionMetaPath(edgeClass);
            //for this edge is a condition metapath defined
            if (conditionMetaPath != null) {
                if (elementaryMetaPath.getDirection() == BACKWARD) {
                    conditionMetaPath = conditionMetaPath.getOtherDirection();
                }
                ModelElement me = getModelElement();
                Collection<ModelElement> conditionElements = conditionMetaPath.getConnectedElements(me);
                available = new ArrayList<>(conditionElements.size());
                for (ModelElement conditionElement : conditionElements) {
                    ElementContainer conditionElementContainer = conditionElement.getContainer(mainDoc);
                    available.add(conditionElementContainer);
                }
            }
            if (available == null) {
                available = mainDoc.getElementContainers(searchElementClass, true);
            }
            addAvailablesFromTemplate(available);
            //remove all available that must not have the edge to be created here according
            //to the metamodel (some subclasses do not inherit some edges of their
            //superclasses, which is checked by metamodel.isStartClass(...)
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
            addAvailablesFromTemplate(available);
        }
        Alphabetical.sort(available);
        return available;
    }

    /**
     * If the searchElement is a pure template class, then all elements from the
     * template that do not occur in the model are also added here to the
     * connectable elements.
     *
     * @param availables the list of connectable elements which will be filled
     *            with template elements if the searchElementClass is a pure
     *            template class
     */
    private void addAvailablesFromTemplate(final List<ElementContainer> availables) {
        MetaModel metaModel = getMetaModel();
        if (metaModel.isPureTemplateElementClass(searchElementClass)) {
            TemplateLibrariesManager templateLibrariesManager = Static.getTemplateLibrariesManager();
            Collection<GDCollection> allActiveTemplates = templateLibrariesManager.getAllActiveTemplates();
            LGMGraphDocument targetMainDoc = getMainDoc();
            for (GDCollection template : allActiveTemplates) {
                LGMGraphDocument templateMainDoc = template.getMainDoc();
                List<ElementContainer> templateAvalibales = templateMainDoc.getElementContainers(searchElementClass, true);
                for (ElementContainer templateAvalibale : templateAvalibales) {
                    String templateAvalibaleID = templateAvalibale.getID();
                    if (!targetMainDoc.isMyElement(templateAvalibaleID)) {
                        availables.add(templateAvalibale);
                    }
                }
            }
        }
    }

    protected final MouseListener mouseListener = new LGMMouseListener(null, null, null, getMouseClickedAction(), null);

    /**
     * Fügt der übergebenen Komponente die
     * Doppelklick-Öffne-Eigenschaftsdialog-des-selektierten-Elementes-Action
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

    @Override
    public ElementDialogPanel getResponsiblePanelForConsistencyError(final AbstractConsistencyError consistencyError) {
        ModelElement errorModelElement = consistencyError.getModelElement();
        if (errorModelElement != modelElement) {
            return null;
        }
        MetaPath errorFixingMetaPath = null;
        if (consistencyError instanceof MissingPathError) {
            MissingPathError pathError = (MissingPathError) consistencyError;
            errorFixingMetaPath = pathError.getErrorFixingCreatableMetaPath();
        } else if (consistencyError instanceof AbstractElementaryPathError) {
            AbstractElementaryPathError pathError = (AbstractElementaryPathError) consistencyError;
            errorFixingMetaPath = pathError.getMetaPath();
        }
        return errorFixingMetaPath != null && hasMetaPath(errorFixingMetaPath) ? this : null;
    }

}
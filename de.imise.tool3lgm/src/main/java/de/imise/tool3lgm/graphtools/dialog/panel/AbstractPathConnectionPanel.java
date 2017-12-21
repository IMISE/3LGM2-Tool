package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.FORWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getMaxBackwardCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getMaxForwardCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getMinBackwardCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getMinForwardCardinality;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isStartClass;

import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;

import javax.swing.JLabel;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.CompositionEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.ReflectionUtils;
import de.imise.util.StringUtils;
import de.imise.util.Sys;

/**
 * Panel für alle einfachen Verbindungen zwischen 2 Elementen
 *
 * @author AXS
 * @created 25.04.2017
 */
public abstract class AbstractPathConnectionPanel extends ConnectedElementsPanel {

    /** Die Kantenklasse zum anderen Element */
    protected final Class<? extends Edge>[] edgeClasses;

    //die sind entweder Dopplekante.FORWARD oder Doppelkante.BACKWARD
    protected final int[] directions;

    /** Label vor dem verbundenen Element mit der Art des Elementes */
    protected final JLabel westLabel;

    /** Index der letzten Edge im Pfad */
    protected final int lastEdgeIndex;

    /**
     * Indikator, ob über den Pfad verbundene Elemente immer eindeutig an einem Punkt angehängt werden können (=<code>true</code>) oder
     * ob es mehrere Möglichkeiten gibt, weil Zwischenpfade mehrfach existieren können.
     */
    protected final boolean isConnectionPointUnique;

    /**
     * Panel für eine einfache Assoziation. Das Label trägt den Anzeigenamen der letzten Elementart.
     *
     * @param dialog
     * @param edgeClasses
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final Class<? extends Edge>... edgeClasses) {
        this(dialog, null, edgeClasses);
    }

    /**
     * Panel für eine einfache Assoziation. Das Label trägt den Anzeigenamen der letzten Elementart.
     *
     * @param dialog
     * @param searchElementClass
     * @param edgeClasss
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        this(dialog, false, searchElementClass, edgeClasses);
    }

    /**
     * Panel für eine einfache Assoziation. Gelabelt wird das verbundene Element der letzten Edge oder die letzte Edge selbst.
     *
     * @param dialog
     * @param labelEdgeName wenn <code>true</code> dann wird ans Labels statt des Namens der über die letzte Edge im Pfad verbundenen
     *            Elementart der Name der letzten Edge selbst ans Label geschrieben.
     * @param edgeClasses
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final boolean labelEdgeName, final Class<? extends Edge>... edgeClasses) {
        this(dialog, edgeClasses.length - 1, labelEdgeName, edgeClasses);
    }

    /**
     * Panel für eine einfache Assoziation. Gelabelt wird das verbundene Element der letzten Edge oder die letzte Edge selbst.
     *
     * @param dialog
     * @param labelEdgeName wenn <code>true</code> dann wird ans Labels statt des Namens der über die letzte Edge im Pfad verbundenen
     *            Elementart der Name der letzten Edge selbst ans Label geschrieben.
     * @param searchElementClass
     * @param edgeClasses
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final boolean labelEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        this(dialog, edgeClasses.length - 1, labelEdgeName, searchElementClass, edgeClasses);
    }

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param searchEdgeIndex Index der Edge, die vorgibt, was als searchElementClass angesehen werden soll, also was ans Label geschrieben
     *            wird. Es wird immer die Endklasse des Pfades bis zur Edge mit dem jeweiliegn Index ans Label geschrieben.
     * @param labelEdgeName wenn <code>true</code> dann wird ans Labels statt des Namens der verbundenen Elementart,
     *            der Name der Edge selbst ans Label geschrieben. Welche Edge im Pfad das ist, wird durch connectionLabelIndex
     *            festgelegt.
     * @param edgeClasses
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final int searchEdgeIndex, final boolean labelEdgeName, final Class<? extends Edge>... edgeClasses) {
        this(dialog, searchEdgeIndex, labelEdgeName, null, edgeClasses);

    }

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param labelEdgeIndex Index der Edge, die vorgibt, was als searchElementClass angesehen werden soll, also was ans Label geschrieben
     *            wird. Es wird immer die Endklasse des Pfades bis zur Edge mit dem jeweiligen Index ans Label geschrieben.
     * @param labelEdgeName wenn <code>true</code> dann wird ans Labels statt des Namens der verbundenen Elementart,
     *            der Name der Edge selbst ans Label geschrieben. Welche Edge im Pfad das ist, wird durch connectionLabelIndex
     *            festgelegt.
     * @param searchElementClass wird hier <code>null</code> übergeben, wird diese Klasse aus den edgeClasses bestimmt, sond wird die übergebene
     *            Klasse genommen
     * @param edgeClasses
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final int labelEdgeIndex, final boolean labelEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge>... edgeClasses) {
        super(dialog);
        this.edgeClasses = edgeClasses;
        directions = getEdgeDirections();
        this.searchElementClass = searchElementClass != null ? searchElementClass : getPathStepEndClass(edgeClasses.length - 1);
        lastEdgeIndex = edgeClasses.length - 1;
        isConnectionPointUnique = isConnectionPointUnique();

        // Das WestLabel auf jeden Fall initialisieren, denn es kann von anderen Panels dann hinzugefügt werden
        westLabel = new JLabel();
        String westLabelText;
        Class<? extends Edge> edgeClass = edgeClasses[labelEdgeIndex];

        if (labelEdgeName) {
            westLabelText = directions[labelEdgeIndex] == FORWARD ? ModelConstants.getForwardMetaAssociationName(edgeClass) : ModelConstants.getBackwardMetaAssociationName(edgeClass);
        } else {
            Class<? extends ModelElement> labelPathStepEndClass = getPathStepEndClass(labelEdgeIndex);
            westLabelText = isSingleConnectionPath() ? ModelConstants.getDisplayableName(labelPathStepEndClass) : ModelConstants.getDisplayablePluralName(labelPathStepEndClass);
        }
        westLabelText = StringUtils.capitalizeFirstChar(westLabelText); // Den ersten Buchstaben des Labels immer groß schreiben
        westLabel.setText(westLabelText);
        setName(westLabelText);
    }

    public static String generateName(final Class<? extends ModelElement> startClass, final Class<? extends Edge>... edgeClasses) {
        return generateName(startClass, edgeClasses.length - 1, false, null, edgeClasses);
    }

    private static String generateName(final Class<? extends ModelElement> startClass, final int labelEdgeIndex, final boolean labelEdgeName, final int[] directions, final Class<? extends Edge>... edgeClasses) {
        int[] usedEdgeDirections = directions != null ? directions : getEdgeDirections(startClass, edgeClasses);
        String name;
        Class<? extends Edge> edgeClass = edgeClasses[labelEdgeIndex];

        if (labelEdgeName) {
            name = directions[labelEdgeIndex] == FORWARD ? ModelConstants.getForwardMetaAssociationName(edgeClass) : ModelConstants.getBackwardMetaAssociationName(edgeClass);
        } else {
            Class<? extends ModelElement> labelPathStepEndClass = getPathStepEndClass(labelEdgeIndex, usedEdgeDirections, edgeClasses);
            name = isSingleConnectionPath(usedEdgeDirections, edgeClasses) ? ModelConstants.getDisplayableName(labelPathStepEndClass) : ModelConstants.getDisplayablePluralName(labelPathStepEndClass);
        }
        name = StringUtils.capitalizeFirstChar(name); // Den ersten Buchstaben des Labels immer groß schreiben
        return name;
    }

    private int[] getEdgeDirections() {
        return getEdgeDirections(dialog.getModelElement().getClass(), edgeClasses);
    }

    private static int[] getEdgeDirections(final Class<? extends ModelElement> startClass, final Class<? extends Edge>[] edgeClasses) {
        int[] returnValue = new int[edgeClasses.length];
        for (int i = 0; i < edgeClasses.length; i++) {
            Class<? extends ModelElement> clazz = i == 0 ? startClass : null;
            clazz = clazz == null ? returnValue[i - 1] == FORWARD ? getEndClass(edgeClasses[i - 1]) : getStartClass(edgeClasses[i - 1]) : clazz;
            returnValue[i] = isStartClass(edgeClasses[i], clazz) ? FORWARD : BACKWARD;
        }
        return returnValue;
    }

    private static Class<? extends ModelElement> getPathStepStartClass(final int edgeIndex, final int[] directions, final Class<? extends Edge>[] edgeClasses) {
        return directions[edgeIndex] == FORWARD ? getStartClass(edgeClasses[edgeIndex]) : getEndClass(edgeClasses[edgeIndex]);
    }

    private static Class<? extends ModelElement> getPathStepEndClass(final int edgeIndex, final int[] directions, final Class<? extends Edge>[] edgeClasses) {
        return directions[edgeIndex] == FORWARD ? getEndClass(edgeClasses[edgeIndex]) : getStartClass(edgeClasses[edgeIndex]);
    }

    private Class<? extends ModelElement> getPathStepStartClass(final int edgeIndex) {
        return getPathStepStartClass(edgeIndex, directions, edgeClasses);
    }

    private Class<? extends ModelElement> getPathStepEndClass(final int edgeIndex) {
        return getPathStepEndClass(edgeIndex, directions, edgeClasses);
    }

    protected Class<? extends ModelElement> getPathStepEndElementClass(final int edgeIndex) {
        Class<? extends ModelElement> pathStepEndElementClass = getPathStepEndClass(edgeIndex);
        Class<? extends ModelElement> nextPathStepStartElementClass = edgeIndex + 1 < edgeClasses.length ? getPathStepStartClass(edgeIndex + 1) : searchElementClass;
        pathStepEndElementClass = ReflectionUtils.getMostSpecialElementClass(pathStepEndElementClass, nextPathStepStartElementClass).asSubclass(ModelElement.class);
        return pathStepEndElementClass;
    }

    private boolean isSingleConnectionPath() {
        return isSingleConnectionPath(directions, edgeClasses);
    }

    /**
     * @return <code>true</code>, wenn maximal 1 Element der {@link #searchElementClass} mit dem Ausgangselement über den
     *         angegebenen Pfad verbunden sein kann. Wenn irgendeine Edge des Pfades mehrfach verbunden sein kann, dann
     *         ist es kein SingleConnectionPath
     */
    private static boolean isSingleConnectionPath(final int[] directions, final Class<? extends Edge>[] edgeClasses) {
        for (int i = 0; i < edgeClasses.length; i++) {
            Class<? extends Edge> edgeClass = edgeClasses[i];
            int maxCard = directions[i] == FORWARD ? getMaxForwardCardinality(edgeClass) : getMaxBackwardCardinality(edgeClass);
            if (maxCard > 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return
     */
    public final JLabel getWestLabel() {
        return westLabel;
    }

    /**
     * Liefert true, wenn die übergebene Kantenklasse eine Composition ist und die
     * zugehörige Richtung (direction) vom Master auf den Slave zeigt.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    protected static boolean isCompositionFromMasterToSlave(final Class<? extends Edge> edgeClass, final int direction) {
        boolean isEdgeComposition = ModelConstants.isComposition(edgeClass);
        isEdgeComposition &= direction == FORWARD;
        return isEdgeComposition;
    }

    /**
     * Liefert true, wenn die Kantenklasse am übergebenen Index eine Composition ist und die
     * zugehörige Richtung (direction) vom Master auf den Slave zeigt.
     *
     * @param index
     * @return
     */
    protected boolean isCompositionFromMasterToSlave(final int index) {
        return isCompositionFromMasterToSlave(edgeClasses[index], directions[index]);
    }

    /**
     * Liefert <code>true</code>, wenn der durch die Kanten angegebene Pfad anlegbar ist (also nicht über abstracte Klassen läuft).
     *
     * @return
     */
    protected boolean isPathCreatable() {
        int pathLength = edgeClasses.length;
        for (int edgeIndex = 0; edgeIndex < pathLength; edgeIndex++) {
            Class<? extends ModelElement> class2Create = getPathStepEndElementClass(edgeIndex);
            if (ModelConstants.isAbstract(class2Create)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Liefert <code>true</code>, wenn das letzte Element des Pfades nur existieren kann, wenn es mit einem
     * auf dem Pfad davor liegenden Element verbunden ist. Das wird gebarucht, um zu entscheiden, ob ein neu
     * angelegtes EndElement des Pfades imemr sofort verbunden werden muss.
     *
     * @return
     */
    protected boolean isLastPathElementDependent() {
        Class<? extends Edge> edgeClass = edgeClasses[lastEdgeIndex];
        int direction = directions[lastEdgeIndex];
        int minCardinality = direction == FORWARD ? getMinBackwardCardinality(edgeClass) : getMinForwardCardinality(edgeClass);
        return minCardinality > 0;
    }

    /**
     * Liefert <code>true</code>, wenn das Element des Panels/Dialoges nur existieren kann, wenn es eine Verbindung
     * über die letzte Edge des Pfades hat . Das wird gebarucht, um zu entscheiden, ob man anbieten kann, diese
     * Verbindung zu lösen oder nicht.
     *
     * @return
     */
    protected boolean isLastPathElementNeededForExistence() {
        Class<? extends Edge> edgeClass = edgeClasses[lastEdgeIndex];
        int direction = directions[lastEdgeIndex];
        int minCardinality = direction == BACKWARD ? getMinBackwardCardinality(edgeClass) : getMinForwardCardinality(edgeClass);
        return minCardinality > 0;
    }

    /**
     * Liefert <code>true</code>, wenn der durch die Kanten vorgegebene Pfad eindeitig festlegt, wo zu verbindende Elemente verknüpft werden.
     * Sobald in einem Pfad der Länge > 1 (also mind. aus 2 Kanten) eines der mittleren Elemente mehrfach mit dem Ausgangselement verbunden
     * sein kann, ist nicht mehr eindeutig, wo die Endelemente angehängt werden sollen.
     *
     * @return
     */
    protected boolean isConnectionPointUnique() {
        //für alle Kanten außer der letzten
        for (int i = 0; i < lastEdgeIndex; i++) {
            //hole die maximale Verbindungsanzahl zum nächsten Element
            int maxCardinality = directions[i] == FORWARD ? getMaxForwardCardinality(edgeClasses[i]) : getMaxBackwardCardinality(edgeClasses[i]);
            //wenn dieses Zwischenelement mehrfach verbunden sein kann
            if (maxCardinality > 1) {
                //nicht eindeutig
                return false;
            }
        }
        return true;
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
        int edgeSearchStopIndex = elements2Connect != null ? edgeClasses.length - 1 : edgeClasses.length;
        for (int i = startEdgeIndex; i < edgeSearchStopIndex; i++) {
            Class<? extends Edge> edgeClass2Create = edgeClasses[i];
            int edgeClass2CreateDirection = directions[i];
            Class<? extends Edge> nextEdgeClass2Create = i + 1 < edgeClasses.length ? edgeClasses[i + 1] : null;
            //wenn es noch eine nächte Edge gibt, dann gibt es auch noch eine nächste direction. Wenn nicht wird einfach FORWARD übergeben, weil das egal ist
            int nextEdgeClass2CreateDirection = nextEdgeClass2Create != null ? directions[i + 1] : FORWARD;
            targetElement = createNodeWithContainerAndDependents(selDoc, targetElement, edgeClass2Create, edgeClass2CreateDirection, nextEdgeClass2Create, nextEdgeClass2CreateDirection, pid);
        }
        //wenn gültige elments2Connect übergeben wurde, dann müssen sie an das vorletzte Pfadelement angehängt werden
        if (edgeSearchStopIndex < edgeClasses.length) {
            int direction = directions[edgeSearchStopIndex];
            Class<? extends Edge> edgeClass2Create = edgeClasses[edgeSearchStopIndex];
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
    private static void link(final GDCollection gdcoll, final ModelElement startElement, final ModelElement endElement, final Class<? extends Edge> edgeClass, final int direction, final int pid) {
        //das neue Element mit dem startElement verknüpfen
        if (direction == FORWARD) {
            gdcoll.link(edgeClass, startElement, endElement, pid);
        } else {
            gdcoll.link(edgeClass, endElement, startElement, pid);
        }
    }

    /**
     * Erzeugt ein neues Element und verknüpft es mit dem übergebenen Startelelement. Für das neue Element werden alle anderen
     * Elemente angelegt, die es braucht, damit keine Verletzung irgendwelcher Kardinalitäten bestehen.
     *
     * @param doc GraphDocument, in dem die anzulegenden Container landen sollen (wenn sie teilmodellspezifisch sind)
     * @param startElement Element, von dem aus die Kanten angelegt werden sollen. Ist dieses Element null, dann wird nur das neue Element angelegt,
     *            aber nichts verknüpft.
     * @param edgeClassToNewElement Kantenklasse, die zwischen dem startElement und dem anzulegenden Element bestehen soll. Diese Klasse und die
     *            directionToNewElement geben vor, welche Elementart neu angelegt werden soll
     * @param directionToNewElement Richtung der neu anzulegenden Edge ausgehend vom startContainer
     * @param edgeClassFromNewElement Kantenklasse, die nicht neu angelegt wird, auch wenn die Kardinalität das bedingen würde. Da diese Funktion hier
     *            für einen anzulegenden Pfad aufgerufen wird, dürfen die Edge, dieses Pfades eben nicht schon hier automatisch angelegt werden.
     * @param pid Process-ID des Dialoges
     * @return den neu angelegtes ModelElement mit allen davon abhängigen Elementen (außer denen, die evtl. auf dem Pfad liegen, der insgesamt
     *         angelegt werden soll)
     */
    protected ModelElement createNodeWithContainerAndDependents(final GraphDocument doc, final ModelElement startElement, final Class<? extends Edge> edgeClassToNewElement, final int directionToNewElement,
            final Class<? extends Edge> edgeClassFromNewElement, final int directionFromNewElement, final int pid) {
        //Collection des übergebenen doc holen
        GDCollection gdcoll = doc.getCollection();
        //den interactiveMode auf false setzen, damit man nicht nach den Namen für die Zwischenelemente gefragt wird,
        //bei denen der Namen normalerweise nicht generiert wird
        boolean isInteractiveMode = gdcoll.isInteractiveMode();

        boolean lastEdge = edgeClassFromNewElement == null;

        //bei der letzten Edge sollte man bei neuen Elementen nach dem Namen fragen
        boolean newInteractiveMode = lastEdge;
        //Ausnahme für Mac-Java-Bug: wenn Dialoge aus einem Drag&Drop-Ereignis heraus gestartet werden, kann man sie nicht mehr mit der Maus ansprechen. Nur mit Tasten.
        //Da dieser Bug nicht so einfach zu umgehen ist, wird in diesem Fall der Dialog einfach nicht angezeigt und der Name generiert.
        if (System.getProperty("os.name").toLowerCase().contains("mac") && Sys.stackTraceContains(DropTarget.class)) {
            newInteractiveMode = false;
        }
        gdcoll.setInteractiveMode(newInteractiveMode);

        //Richtung der Edge FORWARD -> die Endklasse muss angelegt werden, sonst die Startklasse
        Class<? extends ModelElement> elementClass2Create = directionToNewElement == FORWARD ? getEndClass(edgeClassToNewElement) : getStartClass(edgeClassToNewElement);
        //wenn die anzulegende Klasse abstract ist, dann sollte sie aus der ncähsten Edge ermittelt werden können.
        if (ModelConstants.isAbstract(elementClass2Create)) {
            //Richtung der nächsten Edge FORWARD -> die Startklasse muss angelegt werden, sonst die Endklasse
            elementClass2Create = lastEdge ? searchElementClass : directionFromNewElement == FORWARD ? getStartClass(edgeClassFromNewElement) : getEndClass(edgeClassFromNewElement);
        }
        //abstracte Elemente können nicht angelegt werden!
        if (ModelConstants.isAbstract(elementClass2Create)) {
            return null;
        }

        ModelElement createdDependent;

        //wenn ein gültiges startElement übergeben wurde und die Kantenart eine Composition ist
        if (startElement != null && isCompositionFromMasterToSlave(edgeClassToNewElement, directionToNewElement)) {
            //erzeuge ein untergeordnetes Element
            createdDependent = GraphDocument.createAddicted(doc, startElement, edgeClassToNewElement.asSubclass(CompositionEdge.class), elementClass2Create, pid);
        } else {
            //das neue Element gleich mit Container im doc anlegen
            ElementContainer createdContainer = doc.createKnotenWithContainer(elementClass2Create, pid);
            if (createdContainer == null) {
                return null;
            }
            //das Element des neu angelgten Containers holen
            createdDependent = createdContainer.getElement();

            //das neue Element mit dem startElement verknüpfen. Dast Startelement kann null sein, wenn nur das neue Element angelegt werden soll
            if (startElement != null) {
                link(gdcoll, startElement, createdDependent, edgeClassToNewElement, directionToNewElement, pid);
            }
        }
        //alle Kantentpyen der neu angelegten Elementart holen
        Class<? extends Edge>[] edgeTypes = ModelConstants.getEdgeTypes(elementClass2Create);
        //für jede dieser Kantenarten
        boolean interrupted = false;
        for (int i = 0; i < edgeTypes.length && !interrupted; i++) {
            //aktuelle Kantenart holen
            Class<? extends Edge> edgeType = edgeTypes[i];
            //die Kanten, die über den Pfad als nächstes angelegt werden sollen, dürfen hier nicht angelegt werden
            if (edgeType == edgeClassFromNewElement) {
                continue;
            }
            //wenn das neu angelegte Element StartElement der Edge ist
            if (isStartClass(edgeType, elementClass2Create)) {
                //hole die MinKardnalität zu dem anderen Element der Edge
                int minCardinalityForwardToOther = getMinForwardCardinality(edgeType);
                if (minCardinalityForwardToOther > 0) {
                    //hole alle Kanten des neu angelgten Elementes, die denselben Typ haben
                    List<Edge> edgesForwardTo = createdDependent.getEdgesTo(ModelElement.class, edgeType);
                    //Anzahl der bestehenden Kanten der aktuellen Kantenart zu anderen Elementen
                    int edgesForwardToCount = edgesForwardTo.size();
                    //wenn weitere Kanten angelegt werden müssen
                    while (minCardinalityForwardToOther - edgesForwardToCount > 0) {
                        //für das neu angelegte Element müssen auch alle abhängigen Elemente angelegt werden. Da der Pfad von hier nicht weiter
                        //geht, ist die edgeCLassFromNewElement null. Der zweite directions-Parameter ist egal, da die zugehörige Edge null ist -> einfach FORWARD übergeben.
                        ModelElement created = createNodeWithContainerAndDependents(doc, createdDependent, edgeType, FORWARD, null, FORWARD, pid);
                        if (created == null) {
                            interrupted = true;
                            break;
                        }
                        edgesForwardToCount++;
                    }
                }
                //wenn das neu angelegte Element EndElement der Edge ist
            } else {
                //hole die MinKardnalität zu dem anderen Element der Edge
                int minCardinalityBackwardToOther = getMinBackwardCardinality(edgeType);
                if (minCardinalityBackwardToOther > 0) {
                    //hole alle Kanten des neu angelgten Elementes, die denselben Typ haben
                    List<Edge> edgesBackwardTo = createdDependent.getEdgesFrom(ModelElement.class, edgeType);
                    //Anzahl der bestehenden Kanten der aktuellen Kantenart zu anderen Elementen
                    int edgesBackwardToCount = edgesBackwardTo.size();
                    //wenn weitere Kanten angelegt werden müssen
                    while (minCardinalityBackwardToOther - edgesBackwardToCount > 0) {
                        //für das neu angelegte Elemente, müssen auch alle abhängigen Elemente angelegt werden. Da der Pfad von hier nicht weiter
                        //geht, ist die edgeCLassFromNewElement null. Der zweite directions-Parameter ist egal, da die zugehörige Edge null ist -> einfach FORWARD übergeben.
                        ModelElement created = createNodeWithContainerAndDependents(doc, createdDependent, edgeType, BACKWARD, null, FORWARD, pid);
                        if (created == null) {
                            interrupted = true;
                            break;
                        }
                        edgesBackwardToCount++;
                    }
                }
            }

        }
        gdcoll.setInteractiveMode(isInteractiveMode);
        return createdDependent;
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
        for (int i = 0; i < edgeClasses.length; i++) {
            //hole die mit dem aktuellen me verbundenen Elemente der aktuellen Kantenart
            List<ModelElement> connectedElements = me.getConnectedElements(ModelElement.class, edgeClasses[i], directions[i]);
            //wenn bereits mind. ein verbundenes Element ex.
            if (i < lastEdgeIndex && !connectedElements.isEmpty()) {
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
     * @param selection
     * @param e
     */
    private final void executeMouseClickedAction(final Object selection, final MouseEvent e) {
        boolean popup = Tool3lgmConstants.isPopupTrigger(e);
        boolean doubleClick = !popup && e.getClickCount() > 1;
        //set selection
        GraphDocument doc = getGraphDocument();
        ElementContainer selected = null;
        if (selection instanceof ElementContainer) {
            selected = (ElementContainer) selection;
        } else if (selection instanceof ModelElement) {
            //da die Selektion sowieso in allen Teilmodellen ausgeführt wird, ist es hier ok, das ModelElement durch
            //den Container aus dem Hauptdokument zu ersetzen
            ModelElement me = (ModelElement) selection;
            GraphDocument mainDoc = doc.getCollection().getMainGraphDocument();
            selected = me.getContainer(mainDoc);
        }
        if (selected != null) {
            doc.select(selected, getTransactionID());
            if (popup) {
                Tool3lgm.getContextGenerator().getTreeKnotContextMenu().show(e.getComponent(), e.getX() + 3, e.getY() + 3);
            } else if (doubleClick) {
                doc.showPropertyDialog(selected.getElement());
            }
        }
    }

}
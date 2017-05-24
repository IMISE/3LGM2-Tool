package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.graphtools.elements.Doppelkante.BACKWARD;
import static de.imise.tool3lgm.graphtools.elements.Doppelkante.FORWARD;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Panel für alle einfachen Verbindungen zwischen 2 Elementen
 *
 * @author AXS
 * @created 25.04.2017
 */
public abstract class AbstractPathConnectionPanel extends LGMDragNDropPanel {

    /** Die Kantenklasse zum anderen Element */
    protected final Class<? extends Kante>[] edgeClasses;

    /**
     * Die Elementklasse die im Panel angezeigt wird. Das muss nicht das Ende des durch die edgeClasses vorgegebenen Pfades
     * sein, sondern kann auch eine Kante in der Mitte sein.
     *
     * @see #searchEdgeIndex
     */
    protected final Class<? extends ModelElement> searchElementClass;

    /**
     * Index der Kante, die vorgibt, was als searchElementClass angesehen werden soll, also was ans Label geschrieben
     * wird. Der Pfad muss mindestens searchEdgeIndex + 1 Elemente haben, kann aber auch länger sein.
     */
    protected final int searchEdgeIndex;

    //die sind entweder Dopplekante.FORWARD oder Doppelkante.BACKWARD
    protected final int[] directions;

    /** Label vor dem verbundenen Element mit der Art des Elementes */
    protected final JLabel westLabel;

    /**
     * Panel für eine einfache Assoziation. Das Label trägt den Anzeigenamen der letzten Elementart.
     *
     * @param edgeClass
     * @param dialog
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final Class<? extends Kante>... edgeClasses) {
        this(dialog, false, edgeClasses);
    }

    /**
     * Panel für eine einfache Assoziation. Gelabelt wird das verbundene Element der letzten Kante oder die letzte Kante selbst.
     *
     * @param dialog
     * @param labelEdgeName wenn <code>true</code> dann wird ans Labels statt des Namens der über die letzte Kante im Pfad verbundenen
     *            Elementart der Name der letzten Kante selbst ans Label geschrieben.
     * @param edgeClasses
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final boolean labelEdgeName, final Class<? extends Kante>... edgeClasses) {
        this(dialog, edgeClasses.length - 1, labelEdgeName, edgeClasses);
    }

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param searchEdgeIndex Index der Kante, die vorgibt, was als searchElementClass angesehen werden soll, also was ans Label geschrieben
     *            wird. Es wird immer die Endklasse des Pfades bis zur Kante mit dem jeweiliegn Index ans Label geschrieben.
     * @param labelEdgeName wenn <code>true</code> dann wird ans Labels statt des Namens der verbundenen Elementart,
     *            der Name der Kante selbst ans Label geschrieben. Welche Kante im Pfad das ist, wird durch connectionLabelIndex
     *            festgelegt.
     * @param edgeClasses
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final int searchEdgeIndex, final boolean labelEdgeName, final Class<? extends Kante>... edgeClasses) {
        super(dialog);
        this.searchEdgeIndex = searchEdgeIndex;
        this.edgeClasses = edgeClasses;
        directions = getEdgeDirections();
        searchElementClass = getPathStepEndElementClass(searchEdgeIndex);
        setName(ModelConstants.getDisplayableName(searchElementClass));

        // Das WestLabel auf jeden Fall initialisieren, denn es kann von anderen Panels dann hinzugefügt werden
        westLabel = new JLabel();
        String westLabelText;
        Class<? extends Kante> edgeClass = edgeClasses[searchEdgeIndex];

        //Name des Panels ist immer der Name der über die letzte Kante verbundenen Elemente
        String panelName = isSingleConnectionPath() ? ModelConstants.getDisplayableName(searchElementClass) : ModelConstants.getDisplayablePluralName(searchElementClass);
        setName(panelName);

        if (labelEdgeName) {
            if (directions[searchEdgeIndex] == FORWARD) {
                westLabelText = ModelConstants.getForwardMetaAssociationName(edgeClass);
            } else {
                westLabelText = ModelConstants.getBackwardMetaAssociationName(edgeClass);
            }
        } else {
            westLabelText = panelName;
        }
        westLabelText = westLabelText.substring(0, 1).toUpperCase() + westLabelText.substring(1); // Der ersten Buchstaben des Labels immer groß schreiben
        westLabel.setText(westLabelText);
    }

    private int[] getEdgeDirections() {
        int[] returnValue = new int[edgeClasses.length];
        for (int i = 0; i < edgeClasses.length; i++) {
            Class<? extends ModelElement> clazz = i == 0 ? dialog.getModelElement().getClass() : null;
            clazz = clazz == null ? returnValue[i - 1] == FORWARD ? Kante.getEndClass(edgeClasses[i - 1]) : Kante.getStartClass(edgeClasses[i - 1]) : clazz;
            returnValue[i] = Kante.isStartClass(edgeClasses[i], clazz) ? FORWARD : Doppelkante.BACKWARD;
        }
        return returnValue;
    }

    protected Class<? extends ModelElement> getPathStepEndElementClass(final int edgeIndex) {
        return directions[edgeIndex] == FORWARD ? Kante.getEndClass(edgeClasses[edgeIndex]) : Kante.getStartClass(edgeClasses[edgeIndex]);
    }

    /**
     * @return <code>true</code>, wenn maximal 1 Element der {@link #searchElementClass} mit dem Ausgangselement über den
     *         angegebenen Pfad verbunden sein kann. Wenn irgendeine Kante des Pfades mehrfach verbunden sein kann, dann
     *         ist es kein SingleConnectionPath
     */
    private boolean isSingleConnectionPath() {
        for (int i = 0; i < edgeClasses.length; i++) {
            if (directions[i] == FORWARD) {
                if (Kante.getMaxStartToEndCardinality(edgeClasses[searchEdgeIndex]) > 1) {
                    return false;
                }
            } else {
                if (Kante.getMaxEndToStartCardinality(edgeClasses[searchEdgeIndex]) > 1) {
                    return false;
                }
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
     * Liefert die mit dem ModelElement des Dialoges über die angegebenen Kanten verbundenen Elemente.
     *
     * @param forelastInPath wenn <code>true</code> werden nicht die letzten, sondern die vorletzten im
     *            Pfad zurück gegeben. Bei Pfaden, die nur aus einer Kante bestehen ist das das
     *            Ausgangselement des Pfades, also das ModelElement des Dialoges.
     * @return
     */
    private List<ElementContainer> getConnectedContainer(final boolean forelastInPath) {
        List<ElementContainer> connectedElements = Lists.newArrayList();
        connectedElements.add(dialog.getModelElement().getContainer(mainDoc));
        int edgeSearchIndex = forelastInPath ? edgeClasses.length - 1 : edgeClasses.length;
        for (int i = 0; i < edgeSearchIndex; i++) {
            List<ElementContainer> tempConnectedElements = Lists.newArrayList();
            for (ElementContainer ec : connectedElements) {
                tempConnectedElements.addAll(ec.getElement().getConnectedContainer(ModelElement.class, mainDoc, edgeClasses[i], directions[i]));
            }
            connectedElements = tempConnectedElements;
        }
        return connectedElements;
    }

    protected List<ElementContainer> getConnectedContainer() {
        return getConnectedContainer(false);
    }

    /**
     * Liefert die Elemente, die auf dem durch die Kanten angegebenen Pfad diejenigen sind, die tatsächlich
     * mit dem searchElementen verbunden sind. Bei einem Pfad der Länge 1 ist das immer nur das ModelElement
     * selbst bzw. dessen HauptDokument-Container. Bei einem Pfad der Länge 2 sind es die Elemente in der
     * Mitte, also immer die direkt nach dem Ausgangs-ModelElement und vor dem searchElement usw.
     *
     * @return
     */
    protected List<ElementContainer> getSearchElementConnectedContainer() {
        return getConnectedContainer(true);
    }

    /**
     * Trennt alle Verbindungen zwischen den vorletzten Elementen im Kanten-Pfad und den searchElementen.
     */
    protected void unlinkAll() {
        List<ElementContainer> searchElementConnectedContainer = getSearchElementConnectedContainer();
        GDCollection gdcoll = mainDoc.getCollection();
        int lastEdgeIndex = edgeClasses.length - 1;
        Class<? extends Kante> lastEdgeInPath = edgeClasses[lastEdgeIndex];
        int lastEdgeDirection = directions[lastEdgeIndex];
        for (ElementContainer ec : searchElementConnectedContainer) {
            ModelElement me = ec.getElement();
            ArrayList<ModelElement> connectedElements = me.getConnectedElements(searchElementClass, lastEdgeInPath, lastEdgeDirection);
            for (ModelElement connected : connectedElements) {
                gdcoll.unlink(me, connected, lastEdgeInPath, dialog.getTransactionID());
            }
        }
    }

    /**
     * Liefert true, wenn die übergebene Kantenklasse eine Composition ist und die
     * zugehörige Richtung (direction) vom Master auf den Slave zeigt.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    protected static boolean isCompositionFromMasterToSlave(final Class<? extends Kante> edgeClass, final int direction) {
        boolean isEdgeComposition = Composition.class.isAssignableFrom(edgeClass);
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
        for (int i = 0; i < edgeClasses.length; i++) {
            Class<? extends ModelElement> class2Create = directions[i] == FORWARD ? Kante.getEndClass(edgeClasses[i]) : Kante.getStartClass(edgeClasses[i]);
            //wenn die anzulegende End-Klasse der aktuellen Kante abstract ist
            if (ModelConstants.isAbstract(class2Create)) {
                //prüfe, ob die anzulegende StartKlasse der nächsten Kante nicht abstract und somit eindeutig ist
                if (i + 1 < edgeClasses.length) {
                    class2Create = directions[i + 1] == FORWARD ? Kante.getStartClass(edgeClasses[i + 1]) : Kante.getEndClass(edgeClasses[i + 1]);
                }
                if (ModelConstants.isAbstract(class2Create)) {
                    return false;
                }
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
        int lastEdgeIndex = edgeClasses.length - 1;
        Class<? extends Kante> edgeClass = edgeClasses[lastEdgeIndex];
        int direction = directions[lastEdgeIndex];
        int minCardinality = direction == FORWARD ? Kante.getMinEndToStartCardinality(edgeClass) : Kante.getMinStartToEndCardinality(edgeClass);
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
        for (int i = 0; i < edgeClasses.length - 1; i++) {
            //hole die maximale Verbindungsanzahl zum nächsten Element
            int maxCardinality = directions[i] == FORWARD ? Kante.getMaxStartToEndCardinality(edgeClasses[i]) : Kante.getMaxEndToStartCardinality(edgeClasses[i]);
            //wenn dieses Zwischenelement mehrfach verbunden sein kann
            if (maxCardinality > 1) {
                //nicht eindeutig
                return false;
            }
        }
        return true;
    }

    /**
     * Legt für das übergebene Startelement den gesamten Teilpfad ab startEdgeIndex bis einschließlich zur letzten Kante an.
     *
     * @param startElement Das Element bei dem der Teilpfad losgehen soll.
     * @param startEdgeIndex Index der Kante, bei der der anzulegende Teilpfad losgeht
     */
    protected void createPath(final ModelElement startElement, final int startEdgeIndex) {
        connect(startElement, null, startEdgeIndex);
    }

    /**
     * Legt für das übergebene Startelement den Teilpfad ab startEdgeIndex an und hängt die übergebenen elements2connect an.
     *
     * @param startElement Das Element bei dem der Teilpfad losgehen soll.
     * @param elements2Connect Die Elemente die am Ende angehängt werden sollen
     * @param startEdgeIndex Index der Kante, bei der der anzulegende Teilpfad losgeht
     */
    protected void connect(final ModelElement startElement, final Iterable<ModelElement> elements2Connect, final int startEdgeIndex) {
        GraphDocument selDoc = getSelectedGraphDocument();
        GDCollection gdcoll = selDoc.getCollection();
        ModelElement targetElement = startElement;
        int pid = getTransactionID();
        //wenn ein gültiges Element2Connect übergeben wurde, dann muss man den Pfad nur bis zur vorletzten Kante
        //anlegen, sonst bis einschließlich zur letzten
        int firstNotCreatedEdgeIndex = elements2Connect == null ? edgeClasses.length : edgeClasses.length - 1;
        for (int i = startEdgeIndex; i < firstNotCreatedEdgeIndex; i++) {
            Class<? extends Kante> edgeClass2Create = edgeClasses[i];
            int edgeClass2CreateDirection = directions[i];
            Class<? extends Kante> nextEdgeClass2Create = i + 1 < edgeClasses.length ? edgeClasses[i + 1] : null;
            //wenn es noch eine nächte Kante gibt, dann gibt es auch noch eine nächste direction. Wenn nicht wird einfach FORWARD übergeben, weil das egal ist
            int nextEdgeClass2CreateDirection = nextEdgeClass2Create != null ? directions[i + 1] : FORWARD;
            targetElement = createNodeWithContainerAndDependents(selDoc, targetElement, edgeClass2Create, edgeClass2CreateDirection, nextEdgeClass2Create, nextEdgeClass2CreateDirection, pid);
        }
        //wenn gültige elments2Connect übergeben wurde, dann müssen sie an das vorletzte Pfadelement angehängt werden
        if (firstNotCreatedEdgeIndex < edgeClasses.length) {
            int direction = directions[firstNotCreatedEdgeIndex];
            Class<? extends Kante> edgeClass2Create = edgeClasses[firstNotCreatedEdgeIndex];
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
    private static void link(final GDCollection gdcoll, final ModelElement startElement, final ModelElement endElement, final Class<? extends Kante> edgeClass, final int direction, final int pid) {
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
     * @param directionToNewElement Richtung der neu anzulegenden Kante ausgehend vom startContainer
     * @param edgeClassFromNewElement Kantenklasse, die nicht neu angelegt wird, auch wenn die Kardinalität das bedingen würde. Da diese Funktion hier
     *            für einen anzulegenden Pfad aufgerufen wird, dürfen die Kante, dieses Pfades eben nicht schon hier automatisch angelegt werden.
     * @param pid Process-ID des Dialoges
     * @return den neu angelegtes ModelElement mit allen davon abhängigen Elementen (außer denen, die evtl. auf dem Pfad liegen, der insgesamt
     *         angelegt werden soll)
     */
    protected static ModelElement createNodeWithContainerAndDependents(final GraphDocument doc, final ModelElement startElement, final Class<? extends Kante> edgeClassToNewElement, final int directionToNewElement,
            final Class<? extends Kante> edgeClassFromNewElement, final int directionFromNewElement, final int pid) {
        //Collection des übergebenen doc holen
        GDCollection gdcoll = doc.getCollection();
        //den interactiveMode auf false setzen, damit man nicht nach den Namen für die Zwischenelemente gefragt wird,
        //bei denen der Namen normalerweise nicht generiert wird
        boolean isInteractiveMode = gdcoll.isInteractiveMode();
        gdcoll.setInteractiveMode(edgeClassFromNewElement == null);

        //Richtung der Kante FORWARD -> die Endklasse muss angelegt werden, sonst die Startklasse
        Class<? extends ModelElement> elementClass2Create = directionToNewElement == FORWARD ? Kante.getEndClass(edgeClassToNewElement) : Kante.getStartClass(edgeClassToNewElement);
        //wenn die anzulegende Klasse abstract ist, dann sollte sie aus der ncähsten Kante ermittelt werden können.
        if (ModelConstants.isAbstract(elementClass2Create)) {
            //Richtung der nächsten Kante FORWARD -> die Startklasse muss angelegt werden, sonst die Endklasse
            elementClass2Create = directionFromNewElement == FORWARD ? Kante.getStartClass(edgeClassFromNewElement) : Kante.getEndClass(edgeClassFromNewElement);
        }
        //abstracte Elemente können nicht angelegt werden!
        if (ModelConstants.isAbstract(elementClass2Create)) {
            return null;
        }

        ModelElement createdDependent;

        //wenn ein gültiges startElement übergeben wurde und die Kantenart eine Composition ist
        if (startElement != null && isCompositionFromMasterToSlave(edgeClassToNewElement, directionToNewElement)) {
            //erzeuge ein untergeordnetes Element
            createdDependent = GraphDocument.createAddicted(doc, startElement, edgeClassToNewElement.asSubclass(Composition.class), elementClass2Create, pid);
        } else {
            //das neue Element gleich mit Container im doc anlegen
            ElementContainer createdContainer = doc.createKnotenWithContainer(elementClass2Create, pid);
            //das Element des neu angelgten Containers holen
            createdDependent = createdContainer.getElement();

            //das neue Element mit dem startElement verknüpfen. Dast Startelement kann null sein, wenn nur das neue Element angelegt werden soll
            if (startElement != null) {
                link(gdcoll, startElement, createdDependent, edgeClassToNewElement, directionToNewElement, pid);
            }
        }
        //alle Kantentpyen der neu angelegten Elementart holen
        Class<? extends Kante>[] edgeTypes = ModelConstants.getEdgeTypes(elementClass2Create);
        //für jede dieser Kantenarten
        for (int i = 0; i < edgeTypes.length; i++) {
            //aktuelle Kantenart holen
            Class<? extends Kante> edgeType = edgeTypes[i];
            //die Kanten, die über den Pfad als nächstes angelegt werden sollen, dürfen hier nicht angelegt werden
            if (edgeType == edgeClassFromNewElement) {
                continue;
            }
            //wenn das neu angelegte Element StartElement der Kante ist
            if (Kante.isStartClass(edgeType, elementClass2Create)) {
                //hole die MinKardnalität zu dem anderen Element der Kante
                int minCardinalityForwardToOther = Kante.getMinStartToEndCardinality(edgeType);
                if (minCardinalityForwardToOther > 0) {
                    //hole alle Kanten des neu angelgten Elementes, die denselben Typ haben
                    ArrayList<Kante> edgesForwardTo = createdDependent.getEdgesTo(ModelElement.class, edgeType);
                    //Anzahl der bestehenden Kanten der aktuellen Kantenart zu anderen Elementen
                    int edgesForwardToCount = edgesForwardTo.size();
                    //wenn weitere Kanten angelegt werden müssen
                    while (minCardinalityForwardToOther - edgesForwardToCount > 0) {
                        //für das neu angelegte Element müssen auch alle abhängigen Elemente angelegt werden. Da der Pfad von hier nicht weiter
                        //geht, ist die edgeCLassFromNewElement null. Der zweite directions-Parameter ist egal, da die zugehörige Kante null ist -> einfach FORWARD übergeben.
                        createNodeWithContainerAndDependents(doc, createdDependent, edgeType, FORWARD, null, FORWARD, pid);
                        edgesForwardToCount++;
                    }
                }
                //wenn das neu angelegte Element EndElement der Kante ist
            } else {
                //hole die MinKardnalität zu dem anderen Element der Kante
                int minCardinalityBackwardToOther = Kante.getMinEndToStartCardinality(edgeType);
                if (minCardinalityBackwardToOther > 0) {
                    //hole alle Kanten des neu angelgten Elementes, die denselben Typ haben
                    ArrayList<Kante> edgesBackwardTo = createdDependent.getEdgesFrom(ModelElement.class, edgeType);
                    //Anzahl der bestehenden Kanten der aktuellen Kantenart zu anderen Elementen
                    int edgesBackwardToCount = edgesBackwardTo.size();
                    //wenn weitere Kanten angelegt werden müssen
                    while (minCardinalityBackwardToOther - edgesBackwardToCount > 0) {
                        //für das neu angelegte Elemente, müssen auch alle abhängigen Elemente angelegt werden. Da der Pfad von hier nicht weiter
                        //geht, ist die edgeCLassFromNewElement null. Der zweite directions-Parameter ist egal, da die zugehörige Kante null ist -> einfach FORWARD übergeben.
                        createNodeWithContainerAndDependents(doc, createdDependent, edgeType, BACKWARD, null, FORWARD, pid);
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
            if (i < edgeClasses.length - 1 && !connectedElements.isEmpty()) {
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

}
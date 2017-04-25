package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.graphtools.elements.Doppelkante.FORWARD;

import java.util.ArrayList;
import java.util.List;

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
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * Panel für alle einfachen Verbindungen zwischen 2 Elementen
 *
 * @author AXS
 * @created 25.04.2017
 */
public abstract class AbstractPathConnectionPanel extends LGMDragNDropPanel {

    /** Die Kantenklasse zum anderen Element */
    protected final Class<? extends Kante>[] edgeClasses;

    protected final Class<? extends ModelElement> searchElementClass;

    //die sind entweder Dopplekante.FORWARD oder Doppelkante.BACKWARD
    protected final int[] directions;

    /**
     * Panel für eine einfache Assoziation
     *
     * @param edgeClass
     * @param dialog
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final Class<? extends Kante>... edgeClasses) {
        super(dialog);
        this.edgeClasses = edgeClasses;
        directions = getEdgeDirections();
        searchElementClass = getSearchElementClass();
        setName(ModelConstants.getDisplayableName(searchElementClass));
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

    private Class<? extends ModelElement> getSearchElementClass() {
        Class<? extends ModelElement> searchElementClass = ModelElement.class;
        for (int i = 0; i < edgeClasses.length; i++) {
            searchElementClass = directions[i] == FORWARD ? Kante.getEndClass(edgeClasses[i]) : Kante.getStartClass(edgeClasses[i]);
        }
        return searchElementClass;
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
     * Liefert true, wenn die Kantenklasse am übergebenen Index eine Composition ist und die
     * zugehörige Richtung (direction) vom Master auf den Slave zeigt.
     *
     * @param index
     * @return
     */
    protected boolean isEdgeCompostion(final int index) {
        Class<? extends Kante> edgeInPath = edgeClasses[index];
        boolean isEdgeComposition = Composition.class.isAssignableFrom(edgeInPath);
        isEdgeComposition &= directions[index] == FORWARD;
        return isEdgeComposition;
    }

    protected boolean isLastEdgeComposition() {
        return isEdgeCompostion(edgeClasses.length - 1);
    }

    /**
     * Legt den kompletten Pfad bei jeweils dem ersten Element an, das ausgehend vom ModelElement des Dialoges
     * gefunden wird. Wenn der Pfad schon existiert, passiert nichts. Wenn er zu Teilen besteht, wird der Rest
     * angelegt.
     * ACHTUNG: Sollten auf dem Pfad Kanten mit abstrackten Klassen liegen und diese verbundenen Elemente nicht
     * existieren, dann kommt es zu einem Fehler, der hier nicht abgefangen wird.
     * Was auch ungünstig wäre, ist ein Pfad der mit Compositionen von einem Sclave zu einem Master verläuft.
     */
    protected void createNew() {
        ModelElement me = dialog.getModelElement();
        GraphDocument selDoc = getSelectedGraphDocument();
        //für den gesamten Pfad
        for (int i = 0; i < edgeClasses.length; i++) {
            //hole die mit dem aktuellen me verbundenen Elemente der aktuellen Kantenart
            List<ModelElement> connectedElements = me.getConnectedElements(ModelElement.class, edgeClasses[i], directions[i]);
            //wenn bereits mind. ein verbundenes Element ex.
            if (!connectedElements.isEmpty()) {
                //hole das erste
                me = connectedElements.get(0);
            } else {
                //wenn kein verbundenes Element ex.
                int pid = getTransactionID();
                //hole die Elementart, die als nächstes angelegt werden soll
                Class<? extends ModelElement> elementClass2Create = directions[i] == FORWARD ? Kante.getEndClass(edgeClasses[i]) : Kante.getStartClass(edgeClasses[i]);
                //wenn die Kantenart eine Composition ist
                if (isEdgeCompostion(i)) {
                    //erzeuge ein untergeordnetes Element
                    me = GraphDocument.createAddicted(selDoc, me, (Class<? extends Composition>) edgeClasses[i], elementClass2Create, pid);
                } else {
                    //wenn es keine Composition ist, sondern eine 'normale Kante'
                    //lege ein neues Element an (im gerade selektierten doc
                    NodeContainer nc = selDoc.createKnotenWithContainer(elementClass2Create, pid);
                    ModelElement created = nc.getElement();
                    //verbinde das aktuelle me mit dem neuen Element in der angegebenen Richtung
                    if (directions[i] == FORWARD) {
                        selDoc.getCollection().link(edgeClasses[i], me, created, pid);
                    } else {
                        selDoc.getCollection().link(edgeClasses[i], created, me, pid);
                    }
                    //setze das neu angelegte Element als das nächste me
                    me = created;
                }
            }

        }
    }

}

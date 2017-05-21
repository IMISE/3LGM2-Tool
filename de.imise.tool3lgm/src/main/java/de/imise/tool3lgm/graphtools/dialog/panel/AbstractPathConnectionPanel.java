package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.graphtools.elements.Doppelkante.FORWARD;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.graphtools.GDCollection;
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
     * @param edgeClasses
     */
    public AbstractPathConnectionPanel(final ElementPropertyDialog dialog, final int searchEdgeIndex, final Class<? extends Kante>... edgeClasses) {
        this(dialog, searchEdgeIndex, false, edgeClasses);
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
        if (labelEdgeName) {
            if (directions[searchEdgeIndex] == FORWARD) {
                westLabelText = ModelConstants.getForwardMetaAssociationName(edgeClass);
            } else {
                westLabelText = ModelConstants.getBackwardMetaAssociationName(edgeClass);
            }
        } else {
            westLabelText = isSingleConnectionPath() ? ModelConstants.getDisplayableName(searchElementClass) : ModelConstants.getDisplayablePluralName(searchElementClass);
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
     *         angegebenen Pfad verbunden sein kann.
     */
    private boolean isSingleConnectionPath() {
        return (directions[searchEdgeIndex] == FORWARD ? Kante.getMaxStartToEndCardinality(edgeClasses[searchEdgeIndex]) : Kante.getMaxEndToStartCardinality(edgeClasses[searchEdgeIndex])) == 1;
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

    protected boolean isLastEdgeCompositionFromMasterToSlave() {
        return isCompositionFromMasterToSlave(edgeClasses.length - 1);
    }

}
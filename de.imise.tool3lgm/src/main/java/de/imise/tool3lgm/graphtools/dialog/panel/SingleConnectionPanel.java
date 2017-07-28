package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMItemListener;
import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.LimitedSizeScrollTextPane;

/**
 * @author AXS
 *         Dieses Panel stellt in einer Combobox ein einzelne Element zur direkten Verknüpfung mit dem
 *         ModelElement des Dialoges zur Auswahl. Je nachdem ob die Verknüpfung über eine normale
 *         {@link Kante} oder eine {@link Composition} läuft, werden andere im Modell befindliche
 *         Elemente zur Verknüpfung angeboten {@link Kante}) oder nicht ({@link Composition}).
 *         Die Verknüpfung kann über einen Pfad erfolgen, d.h. es gehen nicht nur direkte Verbindungen.
 *         ABER: Dieses Panel beachtet keine Vererbung. Das heißt es werden immer nur direkt mit
 *         dem Ausgangselement verbundene Elemente angezeigt. Im PathConnectionPanel hingegen werden
 *         auch Elemente, die man durch Vererbung erhält, angezeigt. Das ist hier aber nicht sinnvoll,
 *         da man z.B. jedem Anwendungsbaustein sein eigenes Datenbanksystem geben will, auch wenn ein
 *         übergeordneter Anwendungsbaustein schon eines besitzt.
 */
public class SingleConnectionPanel extends AbstractSingleConnectionPanel {

    /** Box, in der die verbindbaren Elemente zur Auswahl gestellt werden, wenn es mehr als eines gibt. */
    private final AlphabeticalComboBox connectedElementsBox;

    /** Eingabefeld, in dem der Name des verbundenen Elementes angezeit wird und geändert werden kann. */
    private final LimitedSizeScrollTextPane connectedElementName;

    /** Je nachdem was von beiden (Box oder Textfeld) initialisert wurde, ist dies diese Komponente */
    protected final Component connectedElementViewComponent;

    /** Das verbundene Element das angezeigt wird (wenn es mind. eins gibt) */
    protected ModelElement connectedElement;

    /** Cache zur Speicherung, ob der Name des verbundenen Elementes geändert wurde */
    private String oldname = "";

    /**
     * COMMENTME
     */
    private final NamedObjectContainer<?> createNew;

    /**
     * COMMENTME
     */
    private final ItemListener itemListener;

    /**
     * @param dialog
     * @param edgeClasses
     */
    public SingleConnectionPanel(final ElementPropertyDialog dialog, final Class<? extends Kante>... edgeClasses) {
        this(dialog, false, edgeClasses);
    }

    /**
     * @param dialog
     * @param labelLastEdgeName wenn <code>true</code> dann wird ans WestLabel statt des Namens der searchElementClass der Name der
     *            letzten Kante aus den edgeClasses geschrieben.
     * @param edgeClasses
     */
    public SingleConnectionPanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        super(dialog, labelLastEdgeName, edgeClasses);
        setLayout(new BorderLayout());

        if (isLastPathElementNeededForExistence()) {
            connectedElementsBox = null;
            itemListener = null;
            connectedElementName = new LimitedSizeScrollTextPane(4);
            connectedElementViewComponent = connectedElementName;

            //Doppelklick-Action und Kontextmenü anghängen
            addMouseActions(connectedElementName);
            add(connectedElementName, BorderLayout.CENTER);
        } else {
            connectedElementsBox = new AlphabeticalComboBox();
            itemListener = new LGMItemListener(getItemStateChangedAction(this, searchElementClass));
            connectedElementName = null;
            connectedElementViewComponent = connectedElementsBox;

            connectedElementsBox.addItemListener(itemListener);
            add(connectedElementsBox, BorderLayout.CENTER);
        }

        createNew = isPathCreatable() ? new NamedObjectContainer<Object>(this, Tool3lgmConstants.getResString("auswahlPanel_neu") + " " + ModelConstants.getDisplayableName(searchElementClass)) : null;
    }

    @Override
    public void update() {
        List<ElementContainer> allConnectedContainers = getConnectedContainer();
        ElementContainer connectedContainer = allConnectedContainers.isEmpty() ? null : allConnectedContainers.get(0);
        connectedElement = connectedContainer == null ? null : connectedContainer.getElement();

        if (connectedElementsBox != null) {
            boolean isLastPathElementDependent = isLastPathElementDependent();
            connectedElementsBox.removeItemListener(itemListener);
            connectedElementsBox.removeAllItems();
            connectedElementsBox.addItem(" ");
            //bei abhängigen Elementen werden in der Auswahlbox nur die angezeigt, die mit dem Element des Dialoges/Panels verbunden sind, sonst alle
            List<ElementContainer> available = isLastPathElementDependent ? allConnectedContainers : mainDoc.getElementContainer(searchElementClass);

            //neues Element anlegen und verknüpfen soll nur gezeigt werden, wenn der Pfad an sich anlegbar ist. Ist die searchElementClass
            //abhängig von der Existenz des Elementes davor im Pfad, dann soll auch kein Neu-Anlegen-Eintrag kommen
            boolean showNewEntry = createNew != null;
            if (showNewEntry && isLastPathElementDependent && !allConnectedContainers.isEmpty()) {
                showNewEntry = false;
            }
            if (showNewEntry) {
                connectedElementsBox.addItem(createNew);
            }
            connectedElementsBox.addSeparator(false);
            connectedElementsBox.addAll(available);
            for (ElementContainer ec : allConnectedContainers) {
                connectedElementsBox.removeItem(ec);
                connectedElementsBox.addItem(ec);
            }
            connectedElementsBox.setSelectedItem(connectedContainer);
            connectedElementsBox.addItemListener(itemListener);
            //Doppelklick-Action und Kontextmenü anghängen
            addMouseActions(connectedElementsBox);
        } else /* if (connectedElementName != null) */ {
            if (connectedElement != null) {
                oldname = connectedElement.getName();
                connectedElementName.setText(oldname);
            } else {
                connectedElementName.setText("");
            }
        }
    }

    @Override
    protected final Object getSelection(final MouseEvent e) {
        //das Mausevent ist egal, da immer nur das eine verbundene Element des Panel selektiert sein kann
        return connectedElementsBox != null ? connectedElementsBox.getSelectedItem() : connectedElement;
    }

    @Override
    public void commit() {
        //der Name kann nicht geändert werden, wenn die ComboBox angezeigt wird (und nicht das EingabeTextfeld)
        //oder wenn kein verbundenes Element vorhanden ist
        if (connectedElementName == null || connectedElement == null) {
            return;
        }
        String newName = connectedElementName.getText();
        if (newName != null && !oldname.equals(newName)) {
            doc.setName(connectedElement, GraphDocument.getParseSaveString(newName), dialog.getTransactionID());
        }
        connectedElement.refreshText();
    }

    /**
     * Dies ist die Action, wenn sich die Combobox-Auswahl ändert
     *
     * @param panel
     * @param elementClass
     * @return
     */
    private static final LGMAction getItemStateChangedAction(final SingleConnectionPanel panel, final Class<? extends ModelElement> elementClass) {
        final GraphDocument mainDoc = panel.getGraphDocument();
        final ElementPropertyDialog dialog = panel.getDialog();
        final ModelElement modelElement = panel.getModelElement();

        return new LGMAction() {
            @Override
            public void execute(final EventObject eo) {
                if (!(eo instanceof ItemEvent)) {
                    return;
                }
                ItemEvent e = (ItemEvent) eo;
                Object selected = e.getItem();
                mainDoc.start_transaction(dialog.getTransactionID());

                // vor jedem select gibt es ein Deselect, wenn erst etwas selektiert war -> alte
                // Verbindung trennen
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    if (selected instanceof NodeContainer) {
                        panel.unlinkAll();
                        modelElement.getContainer(mainDoc).refreshText();
                        mainDoc.finish_transaction(dialog.getTransactionID());
                        return;
                    }
                }

                // Neues Element anlegen
                if (selected == panel.createNew) {
                    panel.connectToFirstPath(null);
                    panel.update();
                } else if (selected instanceof NodeContainer) { //vorhandemes Element verknüpfen
                    ElementContainer container2Connect = (ElementContainer) selected;
                    ModelElement element2Connect = container2Connect.getElement();
                    panel.connectToFirstPath(element2Connect);
                }
                modelElement.getContainer(mainDoc).refreshText();
                mainDoc.finish_transaction(dialog.getTransactionID());
                mainDoc.distributeEvent(GraphDocument.DATA_CHANGED, dialog.getTransactionID());
            }
        };
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
        int edgeSearchStopIndex = forelastInPath ? edgeClasses.length - 1 : edgeClasses.length;
        for (int i = 0; i < edgeSearchStopIndex; i++) {
            List<ElementContainer> tempConnectedElements = Lists.newArrayList();
            for (ElementContainer ec : connectedElements) {
                tempConnectedElements.addAll(ec.getElement().getConnectedContainer(ModelElement.class, mainDoc, edgeClasses[i], directions[i]));
            }
            connectedElements = tempConnectedElements;
        }
        return connectedElements;
    }

    /**
     * Liefert alle Elemente der searchElementClass, die mit dem Ausgangselement direkt verbunden sind.
     *
     * @return
     */
    private final List<ElementContainer> getConnectedContainer() {
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
    private final List<ElementContainer> getSearchElementConnectedContainer() {
        return getConnectedContainer(true);
    }

    /**
     * Trennt alle Verbindungen zwischen den vorletzten Elementen im Kanten-Pfad und den searchElementen.
     */
    private final void unlinkAll() {
        List<ElementContainer> searchElementConnectedContainer = getSearchElementConnectedContainer();
        GDCollection gdcoll = mainDoc.getCollection();
        Class<? extends Kante> lastEdgeInPath = edgeClasses[lastEdgeIndex];
        int lastEdgeDirection = directions[lastEdgeIndex];
        for (ElementContainer ec : searchElementConnectedContainer) {
            ModelElement me = ec.getElement();
            List<ModelElement> connectedElements = me.getConnectedElements(searchElementClass, lastEdgeInPath, lastEdgeDirection);
            for (ModelElement connected : connectedElements) {
                gdcoll.unlink(me, connected, lastEdgeInPath, dialog.getTransactionID());
            }
        }
    }

}
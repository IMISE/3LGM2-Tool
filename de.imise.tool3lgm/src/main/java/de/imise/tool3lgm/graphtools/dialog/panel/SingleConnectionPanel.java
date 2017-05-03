package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;
import java.util.List;

import de.imise.tool3lgm.Tool3lgmConstants;
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

/**
 * @author AXS
 *         Dieses Panel stellt in einer Combobox ein einzelne Element zur Verknüpfung mit dem
 *         ModelElement des Dialoges zur Auswahl. Je nachdem ob die Verknüpfung über eine normale
 *         {@link Kante} oder eine {@link Composition} läuft, werden andere im Modell befindliche
 *         Elemente zur Verknüpfung angeboten {@link Kante}) oder nicht ({@link Composition}).
 *         Die Verknüpfung kann über einen Pfad erfolgen, d.h. es gehen nicht nur direkte Verbindungen.
 */
public class SingleConnectionPanel extends AbstractSingleConnectionPanel {

    /**
     * COMMENTME
     */
    private final AlphabeticalComboBox box = new AlphabeticalComboBox();

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

        itemListener = new LGMItemListener(getItemStateChangedAction(this, searchElementClass));
        box.addItemListener(itemListener);

        add(box, BorderLayout.CENTER);

        createNew = ModelConstants.isAbstract(searchElementClass) ? null : new NamedObjectContainer<Object>(this, Tool3lgmConstants.getResString("auswahlPanel_neu") + " " + ModelConstants.getDisplayableName(searchElementClass));
        init();
    }

    @Override
    protected void init() {
        super.init();
        doc.start_transaction(dialog.getTransactionID(), false);
        box.removeItemListener(itemListener);
        box.removeAllItems();
        box.addItem(" ");
        if (createNew != null) {
            box.addItem(createNew);
        }
        box.addSeparator(false);
        List<ElementContainer> connected = getConnectedContainer();
        List<ElementContainer> available = isLastEdgeComposition() ? connected : mainDoc.getElementContainer(searchElementClass);
        box.addAll(available);
        for (ElementContainer ec : connected) {
            box.removeItem(ec);
            box.addItem(ec);
        }
        if (connected.size() > 0) {
            box.setSelectedItem(connected.get(0));
        }
        doc.finish_transaction(dialog.getTransactionID(), false);
        box.addItemListener(itemListener);
        addMouseActions(box);
    }

    @Override
    protected void showFullDialog() {
        super.showFullDialog();
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
                    panel.createNew(null);

                } else if (selected instanceof NodeContainer) { //vorhandemes Element verknüpfen
                    panel.createNew((NodeContainer) selected);
                }

                modelElement.getContainer(mainDoc).refreshText();
                mainDoc.finish_transaction(dialog.getTransactionID());
                mainDoc.distributeEvent(GraphDocument.DATA_CHANGED, dialog.getTransactionID());
                panel.showFullDialog(false);
            }
        };
    }

    @Override
    public Object getSelection() {
        return box.getSelectedItem();
    }

}
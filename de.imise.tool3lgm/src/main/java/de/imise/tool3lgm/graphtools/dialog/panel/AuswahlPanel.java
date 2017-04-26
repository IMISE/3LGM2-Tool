package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.JLabel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMItemListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
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
public class AuswahlPanel extends AbstractPathConnectionPanel {

    /**
     * COMMENTME
     */
    private final AlphabeticalComboBox box;

    /**
     * COMMENTME
     */
    private final JLabel westLabel;

    /**
     * COMMENTME
     */
    private NamedObjectContainer<?> createNew = null;

    /**
     * COMMENTME
     */
    private final ItemListener itemListener;

    /**
     * @param dialog
     * @param edgeClasses
     */
    public AuswahlPanel(final ElementPropertyDialog dialog, final Class<? extends Kante>... edgeClasses) {
        super(dialog, edgeClasses);
        setLayout(new BorderLayout());
        box = new AlphabeticalComboBox();
        // Action erstellen und Listener an Panel und Box anhängen
        addMouseActions(box);

        itemListener = new LGMItemListener(getItemStateChangedAction(this, searchElementClass));
        box.addItemListener(itemListener);

        add(box, BorderLayout.CENTER);

        // Das WestLabel auf jeden Fall initialisieren, denn es kann von anderen Panels dann
        // hinzugefügt werden
        westLabel = new JLabel();
        westLabel.setText(Tool3lgmConstants.getResString(searchElementClass.getSimpleName()));
        createNew = new NamedObjectContainer<Object>(this, Tool3lgmConstants.getResString("auswahlPanel_neu") + " " + ModelConstants.getDisplayableName(searchElementClass));
        init();
    }

    /**
     * @return
     */
    public JLabel getWestLabel() {
        return westLabel;
    }

    @Override
    protected void init() {
        super.init();
        doc.start_transaction(dialog.getTransactionID(), false);
        box.removeItemListener(itemListener);
        box.removeAllItems();
        box.addItem("");
        box.addItem(createNew);
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
    }

    @Override
    protected void showFullDialog() {
        super.showFullDialog();
    }

    /**
     * @param occp
     * @param elementClass
     * @return
     */
    private static final LGMAction getItemStateChangedAction(final AuswahlPanel occp, final Class<? extends ModelElement> elementClass) {
        final GraphDocument mainDoc = occp.getGraphDocument();
        final GDCollection gdcoll = mainDoc.getCollection();
        final Class<? extends ModelElement> searchElementClass = elementClass;
        final ElementPropertyDialog dialog = occp.getDialog();
        final AuswahlPanel panel = occp;
        final ModelElement modelElement = occp.getModelElement();

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
    protected DragNDropActionChain[] collectDragNDropActionChains() {
        return new DragNDropActionChain[] {};
    }

    @Override
    public LGMDragNDropTree[] getAllDragNDropTrees() {
        return new LGMDragNDropTree[] {};
    }

    @Override
    protected Object getMouseSelectedItem() {
        return box.getSelectedItem();
    }

}
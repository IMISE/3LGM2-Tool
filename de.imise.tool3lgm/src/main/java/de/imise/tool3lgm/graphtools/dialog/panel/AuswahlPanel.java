package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JLabel;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMItemListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsprogramm;
import de.imise.tool3lgm.graphtools.elements.node.Softwareprodukt;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * @author AXS
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
        // Action erstell und Listener an Panel und Box anhängen
        box.addMouseListener(new LGMMouseListener(null, null, null, getMouseAction(), null));

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
     * @param dialog
     * @param addLabel
     * @param edgeClasses
     */
    public AuswahlPanel(final ElementPropertyDialog dialog, final boolean addLabel, final Class<? extends Kante>... edgeClasses) {
        this(dialog, edgeClasses);
        if (addLabel == false) {
            remove(westLabel);
        }
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
     * @return
     */
    public AlphabeticalComboBox getBox() {
        return box;
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
                    panel.createNew();
                } else if (selected instanceof NodeContainer) {
                    if (Softwareprodukt.class.isAssignableFrom(searchElementClass)) {
                        Softwareprodukt swp = (Softwareprodukt) ((NodeContainer) selected).getElement();

                        NodeContainer awp = null;
                        ArrayList<ElementContainer> awpl = modelElement.getConnectedContainer(Anwendungsprogramm.class, mainDoc);
                        if (awpl.size() > 0) {
                            awp = (NodeContainer) awpl.get(0);
                        }

                        if (awp == null) {
                            boolean old_mode = gdcoll.isInteractiveMode();
                            gdcoll.setInteractiveMode(false);
                            // mainDoc.createAWPforABS(modelElement.
                            // getHashString(), modelElement.getClearName()
                            // + "_" + swp.getName(),
                            // dialog.getTransactionID());
                            GraphDocument.createAddicted(mainDoc.getCollection().getSelectedDoc(), modelElement, RawbAwpVerbindung.class, Anwendungsprogramm.class, modelElement.getClearName() + "_" + swp.getName(), dialog.getTransactionID());
                            gdcoll.setInteractiveMode(old_mode);
                            awp = mainDoc.getLastCreated();
                        }
                        gdcoll.link(AwpSwpVerbindung.class, awp.getElement(), swp, dialog.getTransactionID());
                    } else {
                        NodeContainer knot = (NodeContainer) selected;
                        gdcoll.link(modelElement, knot.getElement(), dialog.getTransactionID());
                    }
                }

                modelElement.getContainer(mainDoc).refreshText();
                mainDoc.finish_transaction(dialog.getTransactionID());
                mainDoc.distributeEvent(GraphDocument.DATA_CHANGED, dialog.getTransactionID());
                panel.showFullDialog(false);
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die auf Mouse-Aktionen in Panels
     * reagiert.
     *
     * @param edp
     */
    private final LGMAction getMouseAction() {
        final AuswahlPanel panel = this;
        final GraphDocument doc = panel.getGraphDocument();
        final ElementPropertyDialog dialog = panel.getDialog();
        return new LGMAction() {
            @Override
            public void execute(final EventObject eo) {
                MouseEvent e = (MouseEvent) eo;
                if (Tool3lgmConstants.isPopupTrigger(e)) {
                    Object item = panel.getBox().getSelectedItem();
                    if (item != null && item instanceof NodeContainer) {
                        NodeContainer knot = (NodeContainer) item;
                        doc.select(knot, dialog.getTransactionID());
                        Tool3lgm.getContextGenerator().getTreeKnotContextMenu().show(e.getComponent(), e.getX() + 3, e.getY() + 3);
                    }
                }
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

}
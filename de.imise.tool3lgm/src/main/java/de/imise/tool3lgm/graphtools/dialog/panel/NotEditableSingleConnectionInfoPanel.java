package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JLabel;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * @author AXS
 *         Dieses Panel zeigt einfach nur *ein* mit dem ModelElement des Dialoges verknüpftes
 *         einzelnes Element in einem Label an. Es eignet sich z.B. um für Slave-Elemente einer
 *         {@link Composition} den für dieses Element nicht editierbaren Master anzuzeigen.
 *         Z.B. Datenbanken zeigen ihren Anwendungsbaustein an.
 *         Falls es mehrere Elemente sein sollten, die mit dem ModelElement verknüft sind, wird
 *         das erste gefundene angezeigt (was die Verwendung dieses Panels für mehrfach verknüpfte
 *         Elemente nicht besonders sinnvoll macht).
 */
public class NotEditableSingleConnectionInfoPanel extends AbstractPathConnectionPanel {

    /** Label in dem der Name des verbundenen Elementes angezeit wird */
    private final JLabel connectedElementLabel;

    /** Label vor dem verbundenen Element mit der Art des Elementes */
    private final JLabel westLabel;

    /** Das verbundene Element das angezeigt wird (wenn es mind. eins gibt) */
    private ModelElement connectedElement;

    /**
     * @param dialog
     * @param edgeClasses
     */
    public NotEditableSingleConnectionInfoPanel(final ElementPropertyDialog dialog, final Class<? extends Kante>... edgeClasses) {
        super(dialog, edgeClasses);
        setLayout(new BorderLayout());
        connectedElementLabel = new JLabel();
        // Action erstellen und Listener an Panel und Box anhängen
        addMouseActions(connectedElementLabel);

        add(connectedElementLabel, BorderLayout.CENTER);

        // Das WestLabel auf jeden Fall initialisieren, denn es kann von anderen Panels dann
        // hinzugefügt werden
        westLabel = new JLabel();
        westLabel.setText(ModelConstants.getDisplayableName(searchElementClass));
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
        List<ElementContainer> connected = getConnectedContainer();
        if (connected.size() > 0) {
            ElementContainer ec = connected.get(0);
            connectedElement = ec.getElement();
            connectedElementLabel.setText(ec.toString());
        }
    }

    @Override
    protected void showFullDialog() {
        super.showFullDialog();
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
        return connectedElement.getContainer(getSelectedGraphDocument());
    }

}
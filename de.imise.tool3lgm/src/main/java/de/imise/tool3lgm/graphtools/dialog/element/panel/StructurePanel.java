/*
 * Created on 23.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.dialog.element.panel.PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeSelectionModel;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.ElementDialogPanelTree;
import de.imise.util.swing.SwingUtils;

/**
 * @author fstephan
 */
public final class StructurePanel extends AbstractPathOfOneEdgePanel {

    private ElementDialogPanelTree lotree;
    private ElementDialogPanelTree lutree;
    private ElementDialogPanelTree rtree;
    private JPanel control1;
    private JPanel control2;
    private JLabel rlabel;

    /**
     * Liste aller ElementContainer, die nicht im rectne Baum angezeigt werden
     * sollen, weil sie links schon verknüpft sind
     */
    private final Collection<ElementContainer> childrenToExcludeFromRtree = new HashSet<>();

    private LGMAction loaddAction;
    private LGMAction loremoveAction;
    private LGMAction luaddAction;
    private LGMAction luremoveAction;

    /**
     * @param dialog
     * @param hasPartEdgeClass
     */
    public StructurePanel(final AbstractElementPropertyDialog dialog, final Class<? extends Edge> hasPartEdgeClass) {
        super(dialog, LABEL_LAST_EDGE_CONNECTION_NAME, LABEL_LAST_EDGE_CONNECTION_NAME, Edge.getEndClass(hasPartEdgeClass), hasPartEdgeClass); //die beiden LabelOptions sind egal
        internalInit();
    }

    /**
     *
     */
    private void internalInit() {
        ModelElement me = getModelElement();
        GDCollection gdcoll = me.getCollection();
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        String name = me.getName();
        // lotree
        ElementaryMetaPath backwardMetaPath = metaPath.getOtherDirection();
        JLabel lolabel = new JLabel(backwardMetaPath.getName());
        lotree = new ElementDialogPanelTree(name, mainDoc);
        lotree.setName("lotree");
        lotree.setRootVisible(false);
        lotree.setShowsRootHandles(true);
        lotree.setCellRenderer(treeRenderer);
        lotree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        // lutree
        JLabel lulabel = new JLabel(metaPath.getName());
        lutree = new ElementDialogPanelTree(name, mainDoc);
        lutree.setName("lutree");
        lutree.setRootVisible(false);
        lutree.setCellRenderer(treeRenderer);

        // PanelLayout
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        boolean editable = !dialog.isInfoDialog() && metaPath.isCreatable(false);
        if (editable) {
            add(this, viewButton, constraints, 0, 6, 1, 1);
        }

        constraints.anchor = GridBagConstraints.WEST;
        add(this, lolabel, constraints, 0, 0, 1, 1);
        add(this, lulabel, constraints, 0, 2, 1, 1);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1d;
        constraints.weighty = 1d;
        JScrollPane lotreeScrollPane = lotree.getScrollPane();
        JScrollPane lutreeScrollPane = lutree.getScrollPane();
        add(this, lotreeScrollPane, constraints, 0, 1, 1, 1);
        add(this, lutreeScrollPane, constraints, 0, 3, 1, 1);

        // rtree
        rlabel = new JLabel(getResString("frei"));
        String rtreeRootString = getResString("frei");
        rtree = new ElementDialogPanelTree(rtreeRootString, mainDoc);
        rtree.setName("rtree");
        rtree.setRootVisible(false);
        rtree.setShowsRootHandles(true);
        rtree.setCellRenderer(treeRenderer);
        rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        JScrollPane rtreeScrollPane = rtree.getScrollPane();

        /*
         * Start: Buttons & Actions erstellen und registrieren ...
         */
        loaddAction = getConnectAction(rtree, lotree, BACKWARD);
        loremoveAction = getDisconnectAction(lotree, rtree, BACKWARD);
        luaddAction = getConnectAction(rtree, lutree, FORWARD);
        luremoveAction = getDisconnectAction(lutree, rtree, FORWARD);

        /*
         * ... end: Buttons & Actions erstellen und registrieren
         */

        // ButtonPanels erstellen
        control1 = createBetweenTreesButtonPanel(loaddAction, loremoveAction);
        control2 = createBetweenTreesButtonPanel(luaddAction, luremoveAction);

        initTreeListenerAndDragNDrop();

        //alles dafür tun, dass beide Dialogseiten gleich breit sind. Das wird über die PreferredSize der breitesten Komponente gesteuert.
        SwingUtils.fillToSameLength(lolabel, lulabel, rlabel);
        SwingUtils.setSamePreferredSize(lolabel, lulabel, rlabel);
        SwingUtils.setSamePreferredSize(lotreeScrollPane, lutreeScrollPane, rtreeScrollPane);

        showFullDialog(true);
    }

    @Override
    public void update() {
        childrenToExcludeFromRtree.clear();
        ModelElement me = getModelElement();
        GDCollection gdcoll = me.getCollection();
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        ElementContainer meContainer = me.getContainer(mainDoc);
        childrenToExcludeFromRtree.add(meContainer);
        lotree.saveExpansionAndSelection();
        lotree.reset();
        ElementaryMetaPath backwardMetaPath = metaPath.getOtherDirection();
        List<ElementContainer> backwardConnectedContainer = backwardMetaPath.getConnectedContainer(me, mainDoc);
        for (ElementContainer ec : backwardConnectedContainer) {
            childrenToExcludeFromRtree.add(ec);
            lotree.addObject(ec, true, false, false);
        }
        lotree.reloadModel();
        lotree.restoreExpansionAndSelection();

        lutree.saveExpansionAndSelection();
        lutree.reset();
        List<ElementContainer> forwardConnectedContainer = metaPath.getConnectedContainer(me, mainDoc);
        for (ElementContainer ec : forwardConnectedContainer) {
            childrenToExcludeFromRtree.add(ec);
            lutree.addObject(ec, true, false, false);
        }
        lutree.reloadModel();
        lutree.restoreExpansionAndSelection();

        if (isRightSideVisible()) {
            rtree.saveExpansionAndSelection();
            rtree.reset();
            Class<? extends Edge> edgeClass = metaPath.getEdgeClass();
            List<ElementContainer> all = mainDoc.getElementContainersOfEndClass(edgeClass);
            all.remove(meContainer);
            for (ElementContainer ec : all) {
                rtree.addObject(ec, childrenToExcludeFromRtree, false, false, true);
            }
            rtree.reloadModel();
            rtree.restoreExpansionAndSelection();
        }
        revalidate();
        repaint();
    }

    @Override
    protected void showFullDialog() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.CENTER;
        constraints.weightx = 0d;
        constraints.weighty = 0d;
        add(this, control1, constraints, 1, 1, 1, 1);
        add(this, control2, constraints, 1, 3, 1, 1);
        constraints.anchor = GridBagConstraints.WEST;
        add(this, rlabel, constraints, 2, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1d;
        constraints.weighty = 1d;
        add(this, rtree.getScrollPane(), constraints, 2, 1, 1, 3);
    }

    @Override
    protected void showPartlyDialog() {
        remove(control1);
        remove(control2);
        remove(rlabel);
        remove(rtree.getScrollPane());
    }

    /**
     * Hier werden alle möglichen DragNDrop-Aktionen zwischen den Trees des
     * Panels zumsammengefasst und als Attribut in der Oberklasse abgespeichert.
     */
    @Override
    protected DragNDropInitializer.DragNDropActionChain[] collectDragNDropActionChains() {
        /*
         * alle Aktionen zwischen rtree <-> lotree und rtree <-> lutree
         */
        DragNDropInitializer.DragNDropActionChain tac1 = DragNDropInitializer.createNewDragNDropActionChain(rtree, lotree, loaddAction);
        DragNDropInitializer.DragNDropActionChain tac2 = DragNDropInitializer.createNewDragNDropActionChain(rtree, lutree, luaddAction);
        DragNDropInitializer.DragNDropActionChain tac3 = DragNDropInitializer.createNewDragNDropActionChain(lotree, rtree, loremoveAction);
        DragNDropInitializer.DragNDropActionChain tac4 = DragNDropInitializer.createNewDragNDropActionChain(lutree, rtree, luremoveAction);

        /*
         * alle Aktionen zwischen lotree <-> lutree
         */
        DragNDropInitializer.DragNDropActionChain tac5 = DragNDropInitializer.createNewDragNDropActionChain(new ElementDialogPanelTree[] {
                lotree, rtree, lutree
        }, new LGMAction[] {
                loremoveAction, luaddAction
        });
        DragNDropInitializer.DragNDropActionChain tac6 = DragNDropInitializer.createNewDragNDropActionChain(new ElementDialogPanelTree[] {
                lutree, rtree, lotree
        }, new LGMAction[] {
                luremoveAction, loaddAction
        });

        DragNDropInitializer.DragNDropActionChain[] allDndActionChains = new DragNDropInitializer.DragNDropActionChain[] {
                tac1, tac2, tac3, tac4, tac5, tac6
        };

        return allDndActionChains;

    }

    @Override
    public ElementDialogPanelTree[] getAllDragNDropTrees() {
        return new ElementDialogPanelTree[] {
                rtree, lotree, lutree
        };
    }

    @Override
    public Collection<JComponent> getToolTipTargets() {
        return Lists.newArrayList(rtree, lotree, lutree);
    }

}

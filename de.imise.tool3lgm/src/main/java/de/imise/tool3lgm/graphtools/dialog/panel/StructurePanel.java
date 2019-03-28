/*
 * Created on 23.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTree;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.util.swing.SwingUtils;

/**
 * @author fstephan
 */
public class StructurePanel extends AbstractPathOfOneEdgePanel {

    private LGMTree lotree, lutree;
    private LGMTree rtree;
    private DefaultTreeModel lomodel, lumodel, rmodel;
    private LGMTreeNode loroot, luroot, rroot;
    private JPanel control1, control2;
    private JLabel rlabel;
    private JScrollPane rscrollPane;

    /**
     * Liste aller ElementContainer, die nicht im rectne Baum angezeigt werden sollen, weil sie links schon verknüpft sind
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
    public StructurePanel(final ElementPropertyDialog dialog, final Class<? extends HasPartEdge> hasPartEdgeClass) {
        super(dialog, true, Edge.getEndClass(hasPartEdgeClass), hasPartEdgeClass);
        internalInit();
    }

    private void internalInit() {
        // lotree
        JLabel lolabel = new JLabel(getResString("ueberg"));
        loroot = new LGMTreeNode(getModelElement().getName(), false);
        lomodel = new DefaultTreeModel(loroot);
        lotree = new LGMTree(lomodel, mainDoc);
        lotree.setName("lotree");
        lotree.setRootVisible(false);
        lotree.setShowsRootHandles(true);
        lotree.setCellRenderer(treeRenderer);
        lotree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        JScrollPane lotreeScrollPane = new JScrollPane(lotree);

        // lutree
        JLabel lulabel = new JLabel(getResString("unterg"));
        luroot = new LGMTreeNode(getModelElement().getName(), false);
        lumodel = new DefaultTreeModel(luroot);
        lutree = new LGMTree(lumodel, mainDoc);
        lutree.setName("lutree");
        lutree.setRootVisible(false);
        lutree.setCellRenderer(treeRenderer);
        JScrollPane lutreeScrollPane = new JScrollPane(lutree);

        // PanelLayout
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        add(this, viewButton, constraints, 0, 6, 1, 1);

        constraints.anchor = GridBagConstraints.WEST;
        add(this, lolabel, constraints, 0, 0, 1, 1);
        add(this, lulabel, constraints, 0, 2, 1, 1);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1d;
        constraints.weighty = 1d;
        add(this, lotreeScrollPane, constraints, 0, 1, 1, 1);
        add(this, lutreeScrollPane, constraints, 0, 3, 1, 1);

        // rtree
        rlabel = new JLabel(getResString("frei"));
        rroot = new LGMTreeNode(getResString("frei"), false);
        rmodel = new DefaultTreeModel(rroot);
        rtree = new LGMTree(rmodel, mainDoc);
        rtree.setName("rtree");
        rtree.setRootVisible(false);
        rtree.setShowsRootHandles(true);
        rtree.setCellRenderer(treeRenderer);
        rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        rscrollPane = new JScrollPane(rtree);

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
        SwingUtils.setSamePreferredSize(lotreeScrollPane, lutreeScrollPane, rscrollPane);

        showFullDialog(true);
    }

    @Override
    public void update() {
        childrenToExcludeFromRtree.clear();
        ModelElement me = getModelElement();
        ElementContainer meContainer = me.getContainer(doc);
        childrenToExcludeFromRtree.add(meContainer);
        lotree.saveExpansionAndSelection();
        loroot.removeAllChildren();
        lotree.reset();
        for (ElementContainer ec : me.getDirectParentContainer(mainDoc)) {
            childrenToExcludeFromRtree.add(ec);
            lotree.addObject(ec, loroot, null, true, false, false);
        }
        lomodel.reload();
        lotree.restoreExpansionAndSelection();

        lutree.saveExpansionAndSelection();
        luroot.removeAllChildren();
        lutree.reset();
        for (ElementContainer ec : me.getDirectPartContainer(mainDoc)) {
            childrenToExcludeFromRtree.add(ec);
            lutree.addObject(ec, luroot, null, true, false, false);
        }
        lumodel.reload();
        lutree.restoreExpansionAndSelection();

        if (isRightSideVisible()) {
            rtree.saveExpansionAndSelection();
            rroot.removeAllChildren();
            rtree.reset();
            List<ElementContainer> all = mainDoc.getElementContainer(searchElementClass);
            all.remove(meContainer);
            for (ElementContainer ec : all) {
                rtree.addObject(ec, rroot, childrenToExcludeFromRtree, false, false, true);
            }
            rmodel.reload();
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
        add(this, rscrollPane, constraints, 2, 1, 1, 3);
    }

    @Override
    protected void showPartlyDialog() {
        remove(control1);
        remove(control2);
        remove(rlabel);
        remove(rscrollPane);
    }

    /**
     * Hier werden alle möglichen DragNDrop-Aktionen zwischen den Trees des Panels zumsammengefasst
     * und als Attribut in der Oberklasse abgespeichert.
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
        DragNDropInitializer.DragNDropActionChain tac5 = DragNDropInitializer.createNewDragNDropActionChain(new LGMTree[] {
                lotree, rtree, lutree
        }, new LGMAction[] {
                loremoveAction, luaddAction
        });
        DragNDropInitializer.DragNDropActionChain tac6 = DragNDropInitializer.createNewDragNDropActionChain(new LGMTree[] {
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
    public LGMTree[] getAllDragNDropTrees() {
        return new LGMTree[] {
                rtree, lotree, lutree
        };
    }

}

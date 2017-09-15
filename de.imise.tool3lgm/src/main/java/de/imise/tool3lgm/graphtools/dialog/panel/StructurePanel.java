/*
 * Created on 23.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTree;
import de.imise.tool3lgm.tools.LGMTreeNode;

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
    private JScrollPane sp2;

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
     */
    public StructurePanel(final ElementPropertyDialog dialog, final Class<? extends PartOfBeziehung> partOfEdgeClass) {
        super(dialog, true, dialog.getModelElement().getClass(), partOfEdgeClass);
        internalInit();
    }

    private void internalInit() {
        // lotree
        JLabel lolabel = new JLabel(Tool3lgmConstants.getResString("ueberg"));
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
        JLabel lulabel = new JLabel(Tool3lgmConstants.getResString("unterg"));
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
        //nur für Windows wichtig
        //        constraints.ipadx = -30;
        //        constraints.ipady = -10;
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
        rlabel = new JLabel(Tool3lgmConstants.getResString("frei"));
        rroot = new LGMTreeNode(Tool3lgmConstants.getResString("frei"), false);
        rmodel = new DefaultTreeModel(rroot);
        rtree = new LGMTree(rmodel, mainDoc);
        rtree.setName("rtree");
        rtree.setRootVisible(false);
        rtree.setShowsRootHandles(true);
        rtree.setCellRenderer(treeRenderer);
        rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        sp2 = new JScrollPane(rtree);

        /*
         * Start: Buttons & Actions erstellen und registrieren ...
         */
        JButton loaddButton = new JButton();
        JButton loremoveButton = new JButton();
        JButton luaddButton = new JButton();
        JButton luremoveButton = new JButton();

        loaddAction = getConnectAction(rtree, lotree, true);
        loremoveAction = getDisconnectAction(lotree, rtree, true);
        luaddAction = getConnectAction(rtree, lutree, false);
        luremoveAction = getDisconnectAction(lutree, rtree, false);

        loaddButton.setAction(loaddAction);
        loremoveButton.setAction(loremoveAction);
        luaddButton.setAction(luaddAction);
        luremoveButton.setAction(luremoveAction);
        /*
         * ... end: Buttons & Actions erstellen und registrieren
         */

        // ButtonPanels erstellen
        control1 = new JPanel(new GridLayout(2, 1));
        control2 = new JPanel(new GridLayout(2, 1));

        // Buttons dem Panel hinzufügen
        control1.add(loaddButton);
        control1.add(loremoveButton);
        control2.add(luaddButton);
        control2.add(luremoveButton);

        initTreeListenerAndDragNDrop();

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
            List<ElementContainer> all = mainDoc.getElementContainer(me.getClass());
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
        add(this, sp2, constraints, 2, 1, 1, 3);
    }

    @Override
    protected void showPartlyDialog() {
        remove(control1);
        remove(control2);
        remove(rlabel);
        remove(sp2);
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
                lotree,
                rtree,
                lutree
        }, new LGMAction[] {
                loremoveAction,
                luaddAction
        });
        DragNDropInitializer.DragNDropActionChain tac6 = DragNDropInitializer.createNewDragNDropActionChain(new LGMTree[] {
                lutree,
                rtree,
                lotree
        }, new LGMAction[] {
                luremoveAction,
                loaddAction
        });

        DragNDropInitializer.DragNDropActionChain[] allDndActionChains = new DragNDropInitializer.DragNDropActionChain[] {
                tac1,
                tac2,
                tac3,
                tac4,
                tac5,
                tac6
        };

        return allDndActionChains;

    }

    @Override
    public LGMTree[] getAllDragNDropTrees() {
        return new LGMTree[] {
                rtree,
                lotree,
                lutree
        };
    }

}

package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.ElementDialogPanelTree;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.StringTreeNode;
import de.imise.util.StringUtils;
import de.imise.util.swing.SwingUtils;

/**
 * Mit diesem Panel zeigen
 * <ul>
 * <li>Aufgaben ihre Objekttypen</li>
 * <li>Objekttypen ihre Aufgaben</li>
 * </ul>
 * <br>
 * an. Hinzufügen von Assoziationen zw. den Elementen und Neuanlegen von Aufgaben bzw. Objekttypen
 * geht auch.
 * Das hier könnte man auch aus 2 {@link PathConnectionPanel} zusammen bauen
 */
public class DoubleMeaningEdgePanel extends AbstractPathOfOneEdgePanel {

    private final ElementDialogPanelTree lotree, lutree;
    private ElementDialogPanelTree rotree, rutree;
    private final DefaultTreeModel lomodel, lumodel;
    private DefaultTreeModel romodel;
    private DefaultTreeModel rumodel;
    private final LGMTreeNode loroot, luroot;
    private LGMTreeNode roroot;
    private LGMTreeNode ruroot;
    private JPanel buttonpanel1, buttonpanel2;
    private JLabel rolabel, rulabel;
    private JScrollPane sp3, sp4;

    private LGMAction loaddAction;
    private LGMAction loremoveAction;
    private LGMAction luaddAction;
    private LGMAction luremoveAction;

    public DoubleMeaningEdgePanel(final ElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        super(dialog, titleLabelOption, titleLabelOption, searchElementClass, edgeClass); //westLabelOption ist egal, da sowieso eigene Kantennamen-Labels über die Bäume kommen

        boolean editable = !dialog.isInfoDialog() && metaPath.isCreatable(false);
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        String lolabeltext = StringUtils.capitalizeFirstChar(getEdgeDisplayName(ConnectionState.BACKWARD));
        JLabel lolabel = new JLabel(lolabeltext);

        //hier niemals das this löschen, weil die globale searchElementClass im super-Konsturktor richtig gesetzt wird
        MetaModel metaModel = getMetaModel();
        boolean showRootHandles = metaModel.canHaveParts(this.searchElementClass);

        loroot = new StringTreeNode("loroot");
        lomodel = new DefaultTreeModel(loroot);
        lotree = new ElementDialogPanelTree(lomodel, mainDoc);
        lotree.setRootVisible(false);
        lotree.setShowsRootHandles(showRootHandles);
        lotree.setCellRenderer(treeRenderer);
        lotree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        JScrollPane sp1 = new JScrollPane(lotree);

        String lulabeltext = StringUtils.capitalizeFirstChar(getEdgeDisplayName(ConnectionState.FORWARD));
        JLabel lulabel = new JLabel(lulabeltext);

        luroot = new StringTreeNode("luroot");
        lumodel = new DefaultTreeModel(luroot);
        lutree = new ElementDialogPanelTree(lumodel, mainDoc);
        lutree.setRootVisible(false);
        lutree.setShowsRootHandles(showRootHandles);
        lutree.setCellRenderer(treeRenderer);
        lutree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        JScrollPane sp2 = new JScrollPane(lutree);

        if (editable) {
            constraints.anchor = GridBagConstraints.EAST;
            add(this, viewButton, constraints, 0, 6, 1, 1);
        }

        constraints.anchor = GridBagConstraints.WEST;
        add(this, lolabel, constraints, 0, 0, 1, 1);
        add(this, lulabel, constraints, 0, 2, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp1, constraints, 0, 1, 1, 1);
        add(this, sp2, constraints, 0, 3, 1, 1);

        if (editable) {
            roroot = new StringTreeNode("roroot");
            romodel = new DefaultTreeModel(roroot);
            rotree = new ElementDialogPanelTree(romodel, mainDoc);
            rotree.setRootVisible(false);
            rotree.setShowsRootHandles(showRootHandles);
            rotree.setCellRenderer(treeRenderer);
            rotree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            sp3 = new JScrollPane(rotree);

            String unconnected = getResString("frei");
            rolabel = new JLabel(unconnected);
            rulabel = new JLabel(unconnected);
            ruroot = new StringTreeNode("ruroot");
            rumodel = new DefaultTreeModel(ruroot);
            rutree = new ElementDialogPanelTree(rumodel, mainDoc);
            rutree.setRootVisible(false);
            rutree.setShowsRootHandles(showRootHandles);
            rutree.setCellRenderer(treeRenderer);
            rutree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            sp4 = new JScrollPane(rutree);

            loaddAction = getConnectAction(rotree, lotree, BACKWARD);
            loremoveAction = getDisconnectAction(lotree, rotree, BACKWARD);
            luaddAction = getConnectAction(rutree, lutree, FORWARD);
            luremoveAction = getDisconnectAction(lutree, rutree, FORWARD);

            /*
             * ... end: Buttons & Actions erstellen und registrieren
             */

            buttonpanel1 = createBetweenTreesButtonPanel(loaddAction, loremoveAction);
            buttonpanel2 = createBetweenTreesButtonPanel(luaddAction, luremoveAction);

            initTreeListenerAndDragNDrop();

            //alles dafür tun, dass beide Dialogseiten gleich breit sind. Das wird über die PreferredSize der breitesten Komponente gesteuert.
            SwingUtils.fillToSameLength(lolabel, lulabel, rolabel, rulabel);
            SwingUtils.setSamePreferredSize(lolabel, lulabel, rolabel, rulabel);
            SwingUtils.setSamePreferredSize(sp1, sp2, sp3, sp4);

            showFullDialog(true);
        }
    }

    protected String getEdgeDisplayName(final ConnectionState connectionState) {
        boolean isDoubleMeaningEdge = MetaModel.isDoubleMeaningEdge(edgeClass);
        //dieses Panel war urspünglich nur für Kanten mit doppelter Bedeutung. Danach hat AXS das auch für Kanten zwischen denselben Elementen, die aber eine Richtung haben, angepasst.
        //Kanten ohne doppelte Bedeutung haben immer die Richtung FORWARD, aber der connectionState muss hier als Lesrichtung der Kante interpretiert werden, damit über den beiden Bäumen jeweils eine Richtung steht
        boolean forward = isDoubleMeaningEdge && edgeIsForward || !isDoubleMeaningEdge && connectionState == ConnectionState.FORWARD;
        return forward ? elementsNameBuilder.getForwardMetaAssociationName(edgeClass, connectionState, false, false) : elementsNameBuilder.getBackwardMetaAssociationName(edgeClass, connectionState, false, false);
    }

    ArrayList<ElementContainer> childrenToExcludeFromRotree = new ArrayList<>();
    ArrayList<ElementContainer> childrenToExcludeFromRutree = new ArrayList<>();

    @Override
    public void update() {

        lotree.saveExpansion();
        lotree.saveSelection();
        lutree.saveExpansion();
        lutree.saveSelection();

        loroot.removeAllChildren();
        luroot.removeAllChildren();
        lotree.reset();
        lutree.reset();
        childrenToExcludeFromRotree.clear();
        childrenToExcludeFromRutree.clear();

        boolean searchParts = OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS.is();
        boolean searchParents = OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS.is();

        ModelElement modelElement = getModelElement();

        ElementContainer elementContainer = modelElement.getContainer(mainDoc);
        //egal welche Kante: es ist (im Moment) nicht erlaubt, sich selbst zu verbinden
        //-> eigenen Container niemals anbieten (was nur bei Kanten zw. derselben Elementklasse einen Effekt hat)
        childrenToExcludeFromRotree.add(elementContainer);
        childrenToExcludeFromRutree.add(elementContainer);

        for (ElementContainer ec : modelElement.getConnectedContainers(searchElementClass, mainDoc, edgeClass, BACKWARD)) {
            lotree.addObject(ec, loroot, null, true, false, false);
            childrenToExcludeFromRotree.add(ec);
        }
        if (searchParts) {
            for (ElementContainer ec : modelElement.getPartConnectedContainers(searchElementClass, mainDoc, edgeClass, BACKWARD)) {
                LGMTreeNode node = lotree.addObject(ec, loroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRotree.add(ec);
            }
        }
        if (searchParents) {
            for (ElementContainer ec : modelElement.getParentConnectedContainers(searchElementClass, mainDoc, edgeClass, BACKWARD)) {
                LGMTreeNode node = lotree.addObject(ec, loroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRotree.add(ec);
            }
        }

        for (ElementContainer ec : modelElement.getConnectedContainers(searchElementClass, mainDoc, edgeClass, FORWARD)) {
            lutree.addObject(ec, luroot, null, true, false, false);
            childrenToExcludeFromRutree.add(ec);
        }
        if (searchParts) {
            for (ElementContainer ec : modelElement.getPartConnectedContainers(searchElementClass, mainDoc, edgeClass, FORWARD)) {
                LGMTreeNode node = lutree.addObject(ec, luroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRutree.add(ec);
            }
        }
        if (searchParents) {
            for (ElementContainer ec : modelElement.getParentConnectedContainers(searchElementClass, mainDoc, edgeClass, FORWARD)) {
                LGMTreeNode node = lutree.addObject(ec, luroot, null, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
                childrenToExcludeFromRutree.add(ec);
            }
        }
        lomodel.reload();
        lotree.restoreExpansion();
        lumodel.reload();
        lutree.restoreExpansion();

        if (isRightSideVisible()) {
            rotree.saveExpansion();
            rutree.saveExpansion();
            rotree.saveSelection();
            rutree.saveSelection();

            roroot.removeAllChildren();
            ruroot.removeAllChildren();
            rotree.reset();
            rutree.reset();

            for (ElementContainer ec : mainDoc.getElementContainers(searchElementClass)) {
                rotree.addObject(ec, roroot, childrenToExcludeFromRotree, false, true);
                rutree.addObject(ec, ruroot, childrenToExcludeFromRutree, false, true);
            }
            romodel.reload();
            // expandTree(rotree);
            rumodel.reload();
            // expandTree(rutree);
            rotree.restoreExpansion();
            rutree.restoreExpansion();
        }

        repaint();
        revalidate();

    }

    @Override
    protected void showFullDialog() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        add(this, buttonpanel1, constraints, 1, 1, 1, 2);
        constraints.fill = GridBagConstraints.NONE;
        add(this, buttonpanel2, constraints, 1, 3, 1, 2);

        constraints.anchor = GridBagConstraints.WEST;
        add(this, rolabel, constraints, 2, 0, 1, 1);
        add(this, rulabel, constraints, 2, 2, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp3, constraints, 2, 1, 1, 1);
        add(this, sp4, constraints, 2, 3, 1, 1);
    }

    @Override
    protected void showPartlyDialog() {
        remove(buttonpanel1);
        remove(buttonpanel2);
        remove(rolabel);
        remove(rulabel);
        remove(sp3);
        remove(sp4);
    }

    @Override
    protected DragNDropActionChain[] collectDragNDropActionChains() {
        DragNDropActionChain tac1 = DragNDropInitializer.createNewDragNDropActionChain(rotree, lotree, loaddAction);
        DragNDropActionChain tac2 = DragNDropInitializer.createNewDragNDropActionChain(lotree, rotree, loremoveAction);
        DragNDropActionChain tac3 = DragNDropInitializer.createNewDragNDropActionChain(rutree, lutree, luaddAction);
        DragNDropActionChain tac4 = DragNDropInitializer.createNewDragNDropActionChain(lutree, rutree, luremoveAction);

        return new DragNDropActionChain[] {
                tac1, tac2, tac3, tac4
        };
    }

    @Override
    public JTree[] getAllDragNDropTrees() {
        return new JTree[] {
                lotree, rotree, lutree, rutree
        };
    }
}

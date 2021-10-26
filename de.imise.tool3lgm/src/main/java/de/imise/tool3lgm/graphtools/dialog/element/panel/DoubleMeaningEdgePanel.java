package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.ElementDialogPanelTree;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.util.StringUtils;
import de.imise.util.swing.SwingUtils;
import de.imise.util.swing.component.LimitedHeightScrollTreePane;

/**
 * Mit diesem Panel zeigen
 * <ul>
 * <li>Aufgaben ihre Objekttypen</li>
 * <li>Objekttypen ihre Aufgaben</li>
 * </ul>
 * <br>
 * an. Hinzufügen von Assoziationen zw. den Elementen und Neuanlegen von
 * Aufgaben bzw. Objekttypen geht auch. Das hier könnte man auch aus 2
 * {@link PathConnectionPanel} zusammen bauen
 */
public class DoubleMeaningEdgePanel extends AbstractPathOfOneEdgePanel {

    /** the upper left tree */
    private final ElementDialogPanelTree upperLeftTree;

    /** the down left tree */
    private final ElementDialogPanelTree bottomLeftTree;

    /** the upper right tree */
    private ElementDialogPanelTree upperRightTree;

    /** the down right tree */
    private ElementDialogPanelTree bottomRightTree;

    /** the upper button panel */
    private JPanel upperButtonPanel;

    /** the down button panel */
    private JPanel bottomButtonPanel;

    /** the upper right label */
    private JLabel upperRightLabel;

    /** the down right label */
    private JLabel bottomRightLabel;

    /** the connect action for the button in the {@link #upperButtonPanel} */
    private LGMAction upperConnectAction;

    /** the disconnect action for the button in the {@link #upperButtonPanel} */
    private LGMAction upperDisconnectAction;

    /** the connect action for the button in the {@link #bottomButtonPanel} */
    private LGMAction bottomConnectAction;

    /**
     * the disconnect action for the button in the {@link #bottomButtonPanel}
     */
    private LGMAction bottomDisconnectAction;

    /**
     * all elemenrt container which are already connected directly to the
     * element and must be disabled in the upper right tree
     */
    ArrayList<ElementContainer> childrenToExcludeFromUpperRighttree = new ArrayList<>();

    /**
     * all elemenrt container which are already connected directly to the
     * element and must be disabled in the bottom right tree
     */
    ArrayList<ElementContainer> childrenToExcludeFromBottomRighttree = new ArrayList<>();

    /**
     * @param dialog
     * @param titleLabelOption
     * @param searchElementClass
     * @param edgeClass
     */
    public DoubleMeaningEdgePanel(final ElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final Class<? extends ModelElement> searchElementClass, final Class<? extends DoubleMeaningEdge> edgeClass) {
        super(dialog, titleLabelOption, titleLabelOption, searchElementClass, edgeClass); //westLabelOption ist egal, da sowieso eigene Kantennamen-Labels über die Bäume kommen
        boolean editable = !dialog.isInfoDialog() && metaPath.isCreatable(false);
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        String lolabeltext = StringUtils.capitalizeFirstChar(getEdgeDisplayName(ConnectionState.BACKWARD));
        JLabel lolabel = new JLabel(lolabeltext);

        //If you only show the root handles if the searchElementClass can have parts then
        //you need to add a border -> simply always show root handles in all trees
        boolean showRootHandles = true;

        GraphDocument mainDoc = getMainDoc();
        upperLeftTree = new ElementDialogPanelTree("loroot", mainDoc);
        upperLeftTree.setRootVisible(false);
        upperLeftTree.setShowsRootHandles(showRootHandles);
        upperLeftTree.setCellRenderer(treeRenderer);
        upperLeftTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        String lulabeltext = StringUtils.capitalizeFirstChar(getEdgeDisplayName(ConnectionState.FORWARD));
        JLabel lulabel = new JLabel(lulabeltext);

        bottomLeftTree = new ElementDialogPanelTree("luroot", mainDoc);
        bottomLeftTree.setRootVisible(false);
        bottomLeftTree.setShowsRootHandles(showRootHandles);
        bottomLeftTree.setCellRenderer(treeRenderer);
        bottomLeftTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        if (editable) {
            constraints.anchor = GridBagConstraints.EAST;
            add(this, viewButton, constraints, 0, 6, 1, 1);
        } else {
            viewButton = null;
        }

        constraints.anchor = GridBagConstraints.WEST;
        add(this, lolabel, constraints, 0, 0, 1, 1);
        add(this, lulabel, constraints, 0, 2, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        LimitedHeightScrollTreePane lotreeScrollPane = upperLeftTree.getScrollPane();
        LimitedHeightScrollTreePane lutreeScrollPane = bottomLeftTree.getScrollPane();
        add(this, lotreeScrollPane, constraints, 0, 1, 1, 1);
        add(this, lutreeScrollPane, constraints, 0, 3, 1, 1);

        if (editable) {
            upperRightTree = new ElementDialogPanelTree("roroot", mainDoc);
            upperRightTree.setRootVisible(false);
            upperRightTree.setShowsRootHandles(showRootHandles);
            upperRightTree.setCellRenderer(treeRenderer);
            upperRightTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

            String unconnected = getResString("frei");
            upperRightLabel = new JLabel(unconnected);
            bottomRightLabel = new JLabel(unconnected);
            bottomRightTree = new ElementDialogPanelTree("ruroot", mainDoc);
            bottomRightTree.setRootVisible(false);
            bottomRightTree.setShowsRootHandles(showRootHandles);
            bottomRightTree.setCellRenderer(treeRenderer);
            bottomRightTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

            upperConnectAction = getConnectAction(upperRightTree, upperLeftTree, BACKWARD);
            upperDisconnectAction = getDisconnectAction(upperLeftTree, upperRightTree, BACKWARD);
            bottomConnectAction = getConnectAction(bottomRightTree, bottomLeftTree, FORWARD);
            bottomDisconnectAction = getDisconnectAction(bottomLeftTree, bottomRightTree, FORWARD);

            /*
             * ... end: Buttons & Actions erstellen und registrieren
             */

            upperButtonPanel = createBetweenTreesButtonPanel(upperConnectAction, upperDisconnectAction);
            bottomButtonPanel = createBetweenTreesButtonPanel(bottomConnectAction, bottomDisconnectAction);

            //alles dafür tun, dass beide Dialogseiten gleich breit sind. Das wird über die PreferredSize der breitesten Komponente gesteuert.
            SwingUtils.fillToSameLength(lolabel, lulabel, upperRightLabel, bottomRightLabel);
            SwingUtils.setSamePreferredSize(lolabel, lulabel, upperRightLabel, bottomRightLabel);
            LimitedHeightScrollTreePane rotreeScrollPane = upperRightTree.getScrollPane();
            LimitedHeightScrollTreePane rutreeScrollPane = bottomRightTree.getScrollPane();
            SwingUtils.setSamePreferredSize(lotreeScrollPane, lutreeScrollPane, rotreeScrollPane, rutreeScrollPane);

            showFullDialog(true);
        }

        initTreeListenerAndDragNDrop();

    }

    /**
     * @param connectionState
     * @return
     */
    protected String getEdgeDisplayName(final ConnectionState connectionState) {
        Class<? extends Edge> edgeClass = metaPath.getEdgeClass();
        boolean isDoubleMeaningEdge = MetaModel.isDoubleMeaningEdge(edgeClass);
        boolean edgeIsForward = metaPath.getDirection() == FORWARD;
        //dieses Panel war urspünglich nur für Kanten mit doppelter Bedeutung. Danach hat AXS das auch für Kanten zwischen denselben Elementen, die aber eine Richtung haben, angepasst.
        //Kanten ohne doppelte Bedeutung haben immer die Richtung FORWARD, aber der connectionState muss hier als Lesrichtung der Kante interpretiert werden, damit über den beiden Bäumen jeweils eine Richtung steht
        boolean forward = isDoubleMeaningEdge && edgeIsForward || !isDoubleMeaningEdge && connectionState == ConnectionState.FORWARD;
        return forward ? elementsNameBuilder.getForwardMetaAssociationName(edgeClass, connectionState, false, false) : elementsNameBuilder.getBackwardMetaAssociationName(edgeClass, connectionState, false, false);
    }

    @Override
    public void update() {

        upperLeftTree.saveExpansion();
        upperLeftTree.saveSelection();
        bottomLeftTree.saveExpansion();
        bottomLeftTree.saveSelection();

        upperLeftTree.reset();
        bottomLeftTree.reset();
        childrenToExcludeFromUpperRighttree.clear();
        childrenToExcludeFromBottomRighttree.clear();

        boolean searchParts = OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS.is();
        boolean searchParents = OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS.is();

        GraphDocument mainDoc = getMainDoc();
        ModelElement modelElement = getModelElement();
        ElementContainer elementContainer = modelElement.getContainer(mainDoc);
        //egal welche Kante: es ist (im Moment) nicht erlaubt, sich selbst zu verbinden
        //-> eigenen Container niemals anbieten (was nur bei Kanten zw. derselben Elementklasse einen Effekt hat)
        childrenToExcludeFromUpperRighttree.add(elementContainer);
        childrenToExcludeFromBottomRighttree.add(elementContainer);

        Class<? extends Edge> edgeClass = metaPath.getEdgeClass();
        Direction direction = metaPath.getDirection();
        for (ElementContainer ec : modelElement.getConnectedContainers(searchElementClass, mainDoc, edgeClass, direction, ConnectionState.BACKWARD)) {
            upperLeftTree.addObject(ec, true, false, false);
            childrenToExcludeFromUpperRighttree.add(ec);
        }
        if (searchParts) {
            for (ElementContainer ec : modelElement.getPartConnectedContainers(searchElementClass, mainDoc, edgeClass, direction, ConnectionState.BACKWARD)) {
                ElementContainerTreeNode node = upperLeftTree.addObject(ec, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
            }
        }
        if (searchParents) {
            for (ElementContainer ec : modelElement.getParentConnectedContainers(searchElementClass, mainDoc, edgeClass, direction, ConnectionState.BACKWARD)) {
                ElementContainerTreeNode node = upperLeftTree.addObject(ec, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
            }
        }

        for (ElementContainer ec : modelElement.getConnectedContainers(searchElementClass, mainDoc, edgeClass, direction, ConnectionState.FORWARD)) {
            bottomLeftTree.addObject(ec, true, false, false);
            childrenToExcludeFromBottomRighttree.add(ec);
        }
        if (searchParts) {
            for (ElementContainer ec : modelElement.getPartConnectedContainers(searchElementClass, mainDoc, edgeClass, direction, ConnectionState.FORWARD)) {
                ElementContainerTreeNode node = bottomLeftTree.addObject(ec, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
            }
        }
        if (searchParents) {
            for (ElementContainer ec : modelElement.getParentConnectedContainers(searchElementClass, mainDoc, edgeClass, direction, ConnectionState.FORWARD)) {
                ElementContainerTreeNode node = bottomLeftTree.addObject(ec, true, false, false);
                if (node != null) {
                    node.setSelectable(false);
                }
            }
        }
        upperLeftTree.reloadModel();
        upperLeftTree.restoreExpansion();
        bottomLeftTree.reloadModel();
        bottomLeftTree.restoreExpansion();

        if (isRightSideVisible()) {
            upperRightTree.saveExpansion();
            bottomRightTree.saveExpansion();
            upperRightTree.saveSelection();
            bottomRightTree.saveSelection();

            upperRightTree.reset();
            bottomRightTree.reset();

            for (ElementContainer ec : mainDoc.getElementContainers(searchElementClass)) {
                upperRightTree.addObject(ec, childrenToExcludeFromUpperRighttree, false, true);
                bottomRightTree.addObject(ec, childrenToExcludeFromBottomRighttree, false, true);
            }
            upperRightTree.reloadModel();
            // expandTree(rotree);
            bottomRightTree.reloadModel();
            // expandTree(rutree);
            upperRightTree.restoreExpansion();
            bottomRightTree.restoreExpansion();
        }

        repaint();
        revalidate();

    }

    @Override
    protected void showFullDialog() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        add(this, upperButtonPanel, constraints, 1, 1, 1, 2);
        constraints.fill = GridBagConstraints.NONE;
        add(this, bottomButtonPanel, constraints, 1, 3, 1, 2);

        constraints.anchor = GridBagConstraints.WEST;
        add(this, upperRightLabel, constraints, 2, 0, 1, 1);
        add(this, bottomRightLabel, constraints, 2, 2, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, upperRightTree.getScrollPane(), constraints, 2, 1, 1, 1);
        add(this, bottomRightTree.getScrollPane(), constraints, 2, 3, 1, 1);
    }

    @Override
    protected void showPartlyDialog() {
        remove(upperButtonPanel);
        remove(bottomButtonPanel);
        remove(upperRightLabel);
        remove(bottomRightLabel);
        remove(upperRightTree.getScrollPane());
        remove(bottomRightTree.getScrollPane());
    }

    @Override
    protected DragNDropActionChain[] collectDragNDropActionChains() {
        DragNDropActionChain tac1 = DragNDropInitializer.createNewDragNDropActionChain(upperRightTree, upperLeftTree, upperConnectAction);
        DragNDropActionChain tac2 = DragNDropInitializer.createNewDragNDropActionChain(upperLeftTree, upperRightTree, upperDisconnectAction);
        DragNDropActionChain tac3 = DragNDropInitializer.createNewDragNDropActionChain(bottomRightTree, bottomLeftTree, bottomConnectAction);
        DragNDropActionChain tac4 = DragNDropInitializer.createNewDragNDropActionChain(bottomLeftTree, bottomRightTree, bottomDisconnectAction);

        return new DragNDropActionChain[] {
                tac1, tac2, tac3, tac4
        };
    }

    @Override
    public JTree[] getAllDragNDropTrees() {
        return new JTree[] {
                upperLeftTree, upperRightTree, bottomLeftTree, bottomRightTree
        };
    }

    @Override
    public Collection<JComponent> getToolTipTargets() {
        return Lists.newArrayList(upperLeftTree, bottomLeftTree, upperRightTree, bottomRightTree);
    }

}

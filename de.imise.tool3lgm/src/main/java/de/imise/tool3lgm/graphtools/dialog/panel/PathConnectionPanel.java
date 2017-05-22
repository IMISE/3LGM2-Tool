package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.elements.Doppelkante.BACKWARD;
import static de.imise.tool3lgm.graphtools.elements.Doppelkante.FORWARD;

/**
 * @author AXS created on 20.05.2007
 */
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.ActionNotDefinedForClassException;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.LGMDragNDropTree;
import de.imise.tool3lgm.graphtools.elements.Composition;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.Pair;
import de.imise.util.StringUtils;

/**
 * Mit diesem Panel können für ein Element über einen Pfad von mehr als einer Kante verbundene Elemente
 * angezeigt, hinzugefügt und entfernt werden.
 */
public class PathConnectionPanel extends AbstractPathConnectionPanel {

    private final LGMDragNDropTree ltree;
    private final LGMDragNDropTree rtree;
    private final DefaultTreeModel model, abmodel;
    private final LGMTreeNode root, abroot;
    private final JLabel rtreeLabel, redundanzLabel;
    private final JScrollPane sp2;
    private final JPanel buttonpanel;
    //    private static String teilmodell = Tool3lgmConstants.getResString("submodel");
    //    private static String gesamtmodell = Tool3lgmConstants.getResString("whole_model");
    //    private static String redundanteKonfs = Tool3lgmConstants.getResString("redundante_Konfigs");

    private LGMAction addAction;
    private LGMAction removeAction;

    public PathConnectionPanel(final ElementPropertyDialog pd, final Class<? extends Kante>... edgeClasses) {
        super(pd, edgeClasses);
        setPreferredSize(new Dimension(550, 350));
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        redundanzLabel = new JLabel();

        String ltreeLabelString = getResString("verb");
        String rtreeLabelString = getResString("frei");
        Pair<String, String> treeLabels = StringUtils.makeSameLength(ltreeLabelString, rtreeLabelString);
        ltreeLabelString = treeLabels.getFirstItem();
        rtreeLabelString = treeLabels.getSecondItem();

        westLabel.setText(ltreeLabelString);
        JLabel ltreeLabel = westLabel;
        root = new LGMTreeNode(getModelElement().getContainer(mainDoc), false);
        model = new DefaultTreeModel(root);
        ltree = new LGMDragNDropTree(model, mainDoc);
        ltree.setRootVisible(false);
        ltree.setShowsRootHandles(true);
        ltree.setCellRenderer(treeRenderer);
        ltree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        JScrollPane sp = new JScrollPane(ltree);

        constraints.anchor = GridBagConstraints.EAST;
        constraints.ipadx = -30;
        constraints.ipady = -10;
        add(this, viewButton, constraints, 0, 5, 1, 1);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        add(this, ltreeLabel, constraints, 0, 0, 1, 1);
        add(this, redundanzLabel, constraints, 0, 5, 5, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp, constraints, 0, 1, 1, 4);

        rtreeLabel = new JLabel(rtreeLabelString);
        abroot = new LGMTreeNode(rtreeLabelString, false);
        abmodel = new DefaultTreeModel(abroot);
        rtree = new LGMDragNDropTree(abmodel, mainDoc);
        rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        rtree.setRootVisible(false);
        rtree.setShowsRootHandles(true);
        rtree.setCellRenderer(treeRenderer);
        sp2 = new JScrollPane(rtree);

        /*
         * Start: MouseListener erstellen und an Trees anhängen ...
         */
        LGMAction ltreeMouseAction = LGMActionLibrary.getMouseAction(ltree, this);
        LGMAction rtreeMouseAction = LGMActionLibrary.getMouseAction(rtree, this);

        ltree.addMouseListener(new LGMMouseListener(null, null, null, ltreeMouseAction, null));
        rtree.addMouseListener(new LGMMouseListener(null, null, null, rtreeMouseAction, null));
        /*
         * ... End: MouseListener erstellen und an Trees anhängen
         */

        /*
         * Start: TreeSelectionListener erstellen und an Trees anhängen ...
         */
        LGMAction ltreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(ltree, this);
        LGMAction rtreeSelectionAction = LGMActionLibrary.getTreeSelectionAction(rtree, this);

        ltree.addTreeSelectionListener(new LGMTreeSelectionListener(ltreeSelectionAction));
        rtree.addTreeSelectionListener(new LGMTreeSelectionListener(rtreeSelectionAction));
        /*
         * ... End: TreeSelectionListener erstellen und an Trees anhängen
         */

        /*
         * Start: Buttons & Actions erstellen, Actions setzen ...
         */
        JButton addButton = new JButton();
        JButton removeButton = new JButton();

        try {
            addAction = getAddElementAction(rtree, ltree, this);
            removeAction = getDisconnectAction(ltree, this);
        } catch (ActionNotDefinedForClassException e) {
            Log.log(Log.DEBUG, e.getMessage());
        }
        addButton.setAction(addAction);
        removeButton.setAction(removeAction);
        /*
         * ... end: Buttons & Actions erstellen, Actions setzen
         */

        buttonpanel = new JPanel();
        buttonpanel.setSize(30, 250);
        buttonpanel.setLayout(new GridLayout(3, 1));
        buttonpanel.add(addButton);
        buttonpanel.add(removeButton);

        init();
    }

    @Override
    public void init() {
        super.init();
        remove(buttonpanel);
        remove(rtreeLabel);
        remove(sp2);
        buildTree();
        revalidate();
        repaint();
    }

    @Override
    public void showFullDialog() {
        super.showFullDialog();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        add(this, buttonpanel, constraints, 1, 3, 1, 2);
        constraints.anchor = GridBagConstraints.WEST;
        add(this, rtreeLabel, constraints, 2, 0, 1, 1);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, sp2, constraints, 2, 1, 1, 4);

        buildRightTree();

        revalidate();
        repaint();
    }

    private void buildTree() {
        ltree.saveExpansion();
        ltree.saveSelection();
        root.removeAllChildren();
        ltree.reset();

        Class<? extends ModelElement> pathStepEndClass = getPathStepEndElementClass(0);
        List<ElementContainer> all = getModelElement().getConnectedContainer(pathStepEndClass, mainDoc);
        // nur Knoten für Elemente in der all-Liste bis zur Größe der direkt verbundenen dürfen am Ende selektierbar sein
        int firstNonSelectableIndex = all.size();
        if (UserProperties.isSearchParts()) {
            all.addAll(getModelElement().getPartConnectedContainer(pathStepEndClass, mainDoc));
        }
        if (UserProperties.isSearchParents()) {
            all.addAll(getModelElement().getParentConnectedContainer(pathStepEndClass, mainDoc));
        }
        List<LGMTreeNode> firstLevelNodes = Lists.newArrayListWithCapacity(all.size());
        for (ElementContainer ec : all) {
            LGMTreeNode node = ltree.addObject(ec, root, null, true, false, false);
            firstLevelNodes.add(node);
        }
        List<LGMTreeNode> nextStepStartNodes = firstLevelNodes;
        for (int edgeIndex = 1; edgeIndex < edgeClasses.length; edgeIndex++) {
            pathStepEndClass = getPathStepEndElementClass(edgeIndex);
            List<LGMTreeNode> newNextStartNodes = Lists.newArrayList();
            for (LGMTreeNode node : nextStepStartNodes) {
                ElementContainer nodeElementContainer = (ElementContainer) node.getUserObject();
                ModelElement me = nodeElementContainer.getElement();
                List<ElementContainer> connected = me.getConnectedContainer(pathStepEndClass, mainDoc);
                for (ElementContainer ec : connected) {
                    LGMTreeNode newNode = ltree.addObject(ec, node, null, true, false, false);
                    newNextStartNodes.add(newNode);
                }
            }
            nextStepStartNodes = newNextStartNodes;
            // alle Elemente die von den Parts oder Parents kamen, nichtselektierbar setzen
            for (int i = firstLevelNodes.size() - 1; i >= firstNonSelectableIndex; i--) {
                firstLevelNodes.get(i).setSelectable(false);
            }
        }

        model.reload();
        ltree.restoreExpansion();
        ltree.restoreSelection();

        //        StringBuilder sb = new StringBuilder(60);
        //        sb.append(redundanteKonfs);
        //        sb.append(" ");
        //        GraphDocument selDoc = getSelectedGraphDocument();
        //        if (selDoc instanceof Szenario) {
        //            sb.append(((Aufgabe) getModelElement()).getAllDifferentKonfigs(selDoc).size() - 1);
        //            sb.append(" (");
        //            sb.append(teilmodell);
        //            sb.append(") ");
        //        }
        //        sb.append(((Aufgabe) getModelElement()).getAllDifferentKonfigs(mainDoc).size() - 1);
        //        sb.append(" (");
        //        sb.append(gesamtmodell);
        //        sb.append(")");
        //        redundanzLabel.setText(sb.toString());
    }

    /**
     *
     */
    private void buildRightTree() {
        rtree.saveExpansion();
        rtree.saveSelection();
        abroot.removeAllChildren();
        rtree.reset();
        for (ElementContainer ec : mainDoc.getElementContainer(searchElementClass, true, true)) {
            rtree.addObject(ec, abroot, null, false, true);
        }
        abmodel.reload();
        rtree.restoreExpansion();
        rtree.restoreSelection();
    }

    @Override
    protected final DragNDropActionChain[] collectDragNDropActionChains() {
        DragNDropActionChain dndAC1 = DragNDropInitializer.createNewDragNDropActionChain(rtree, ltree, addAction);
        DragNDropActionChain dndAC2 = DragNDropInitializer.createNewDragNDropActionChain(ltree, rtree, removeAction);

        return new DragNDropActionChain[] {
                dndAC1,
                dndAC2
        };

    }

    @Override
    public final LGMDragNDropTree[] getAllDragNDropTrees() {
        return new LGMDragNDropTree[] {
                rtree,
                ltree
        };
    }

    public final LGMAction getAddElementAction(final JTree srcTree, final JTree targetTree, final PathConnectionPanel panel) throws ActionNotDefinedForClassException {
        return new LGMAction("", Tool3lgmConstants.getIcon("arrow_left2.gif")) {
            @Override
            public void execute(final EventObject eo) {
                //falls in den TargetTree gedroppt wurde -> selektiere den zur DropPosition nächstegelegenen TreePath
                LGMActionLibrary.getDragNDropLocateElementAsTargetAction(targetTree).execute(eo);
                // Anzahl der selektierten Elemente im rechten Baum, die verbunden werden sollen, ermitteln
                int srcTreeSelRowsCount = srcTree.getSelectionCount();
                if (srcTreeSelRowsCount < 1) {
                    Static.showMessgae(panel, "selction_in_right_tree");
                    return;
                }

                // Anzahl der selektierten Zeilen im linken Ziel-Baum ermitteln
                int targetTreeSelRowsCount = targetTree.getSelectionCount();

                // wenn im linken Ziel-Baum nur eine Zeile enthalten ist und bisher nichts
                // selektiert ist, selektiere diese eine Zeile (falls die Zeile sich nicht
                // selektieren lässt, weil sie dialbels ist, wenn si über Teil-Von-Beziehungen
                // ererbt wurde, dann its danach immer noch nichts selektiert
                if (targetTreeSelRowsCount == 0 && targetTree.getRowCount() == 1) {
                    targetTree.setSelectionRow(0);
                }

                // Anzahl der selektierten Zeilen im linken Ziel-Baum erneut ermitteln
                targetTreeSelRowsCount = targetTree.getSelectionCount();

                //ausgewählter Path im TargetTree -> wenn sich vorher was selktieren ließ, dann das sonst der root
                TreePath targetTreeSelectionPath = targetTreeSelRowsCount > 0 ? targetTree.getSelectionPath() : new TreePath(targetTree.getModel().getRoot());
                TreePath[] sourceTreePaths = srcTree.getSelectionPaths();

                connect(targetTreeSelectionPath, sourceTreePaths);

                //TODO: das hier expandiert das neue überhaupt nicht, sondern nur bis zum vorher schon geöffneten Knoten. Das ist doof!
                targetTree.expandPath(targetTreeSelectionPath);
                targetTree.clearSelection();

                return;
            }
        };
    }

    private static ModelElement getPathModelElement(final TreePath treePath) {
        LGMTreeNode node = (LGMTreeNode) treePath.getLastPathComponent();
        return getNodeModelElement(node);
    }

    private static ModelElement getNodeModelElement(final LGMTreeNode node) {
        ElementContainer ec = (ElementContainer) node.getUserObject();
        ModelElement me = ec.getElement();
        return me;
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "removeButtons" der Panels angefügt werden.
     *
     * @param srcTree linker Baum mit dem verknüpften Pfaden
     * @param targetTree rechter Baum mit den Elementen, die ausgewählt werden können
     * @param panel
     */
    public static final LGMAction getDisconnectAction(final JTree srcTree, final PathConnectionPanel panel) {

        return new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {

            @Override
            public void execute(final EventObject eo) {
                int selrows = srcTree.getSelectionCount();
                if (selrows < 1) {
                    return;
                }

                TreePath[] path2disconnect = srcTree.getSelectionPaths();
                for (int i = 0; i < path2disconnect.length; i++) {
                    //das ist der Index der Kante im Pfad, ab der entfernt werden soll
                    int treePathEdgeIndex = path2disconnect[i].getPathCount() - 2;
                    ModelElement element2Unlink = PathConnectionPanel.getPathModelElement(path2disconnect[i]);
                    ModelElement parentOfElement2Unlink = PathConnectionPanel.getPathModelElement(path2disconnect[i].getParentPath());
                    panel.disconnect(parentOfElement2Unlink, element2Unlink, treePathEdgeIndex);
                }
            }
        };

    }

    protected void disconnect(final ModelElement startInPath, final ModelElement endInPath, final int edgeIndexInPath) {
        GraphDocument selDoc = getSelectedGraphDocument();
        GDCollection gdcoll = selDoc.getCollection();
        int pid = getTransactionID();
        Class<? extends Kante> edgeClass = edgeClasses[edgeIndexInPath];
        gdcoll.unlink(startInPath, endInPath, edgeClass, pid);
        if (!startInPath.isConsistent()) {
            gdcoll.deleteElement(startInPath, selDoc, pid);
        }
        int nextEdgeIndexInPath = edgeIndexInPath + 1;
        if (nextEdgeIndexInPath < edgeClasses.length) {
            Class<? extends Kante> nextEdgeClass = edgeClasses[nextEdgeIndexInPath];
            Class<? extends ModelElement> nextElementClassInPath = directions[nextEdgeIndexInPath] == FORWARD ? Kante.getEndClass(nextEdgeClass) : Kante.getStartClass(nextEdgeClass);
            List<ModelElement> connectedElements = endInPath.getConnectedElements(nextElementClassInPath, nextEdgeClass);
            for (ModelElement connectedElement : connectedElements) {
                disconnect(endInPath, connectedElement, nextEdgeIndexInPath);
            }
        }
        if (!endInPath.isConsistent()) {
            gdcoll.deleteElement(endInPath, selDoc, pid);
        }
    }

    /**
     * Hängt an den targetTreePath die lastPathComponent der sourceTreePaths an. Wenn als targetTreePath ein vollständiger Pfad
     * übergeben wird, dann werden die sourceTReePath-Elemente an den Parent der LastPathComponent gehängt. Wenn der Pfad gleich
     * nur bis zum Parent geht, dann werden sie da angehängt. Ist der Pfad kürzer, dann wird er bis zum Parent erzeugt und dann
     * die übergebenen sourceTreePath-Elemente angehängt.
     *
     * @param targetTreePath
     * @param sourceTreePaths
     */
    protected void connect(final TreePath targetTreePath, final TreePath... sourceTreePaths) {
        GraphDocument selDoc = getSelectedGraphDocument();
        GDCollection gdcoll = selDoc.getCollection();

        //das ist der Index der Kante im Pfad, ab der hinzugefügt werden soll
        int targetTreePathEdgeIndex = targetTreePath.getPathCount() - 1;

        TreePath realTargetTreePath = targetTreePath;
        //falls der TargetPath bis zum letzten Element angegeben wurde, dann soll eigenlich an den Parent angehängt werden, weil
        //die letzte Elemente im Pfad immer die anzuhängenden selbst sind, die auch auf der rechten Seite ausgewählt werden können
        if (targetTreePathEdgeIndex == edgeClasses.length) {
            //nimm vom aktuell auf der linken Seite ausgewählten Pfad das vorletzte Pfadelement
            realTargetTreePath = realTargetTreePath.getParentPath();
            targetTreePathEdgeIndex--;
        }

        //Element holen, an das der Pfad angehängt werden soll
        ModelElement targetElement = getPathModelElement(realTargetTreePath);

        //wenn kein Pfad bis zum vorletzten Element angegeben wurde -> den Pfad bis zum vorletzten Element neu erstellen
        int pid = getTransactionID();
        for (int i = targetTreePathEdgeIndex; i < edgeClasses.length - 1; i++) {
            Class<? extends Kante> edgeClass2Create = edgeClasses[i];
            int edgeClass2CreateDirection = directions[i];
            Class<? extends Kante> nextEdgeClass2Create = i + 1 < edgeClasses.length ? edgeClasses[i + 1] : null;
            targetElement = createNodeWithContainerAndDependents(selDoc, targetElement, edgeClass2Create, edgeClass2CreateDirection, nextEdgeClass2Create, pid);
        }

        //die im rechten Baum (sourceTreePath) selektierten Elemente an das vorletzte Pfadelement im targetTree anhängen (linken)
        int direction = directions[directions.length - 1];
        Class<? extends Kante> edgeClass2Create = edgeClasses[edgeClasses.length - 1];
        for (TreePath sourceTreePath : sourceTreePaths) {
            ModelElement sourceElement = getPathModelElement(sourceTreePath);
            link(gdcoll, targetElement, sourceElement, edgeClass2Create, direction, pid);
        }

    }

    protected static void link(final GDCollection gdcoll, final ModelElement startElement, final ModelElement endElement, final Class<? extends Kante> edgeClass, final int direction, final int pid) {
        //das neue Element mit dem startElement verknüpfen
        if (direction == FORWARD) {
            gdcoll.link(edgeClass, startElement, endElement, pid);
        } else {
            gdcoll.link(edgeClass, endElement, startElement, pid);
        }
    }

    /**
     * @param doc GraphDocument, in dem die anzulegenden Container landen sollen (wenn sie teilmodellspezifisch sind)
     * @param startElement Element, von dem aus die Kanten angelegt werden sollen
     * @param edgeClassToNewElement Kantenklasse, die zwischen dem startContainer und dem anzulegenden Element bestehen soll
     * @param directionToNewElement Richtung der neu anzulegenden Kante ausgehend vom startContainer
     * @param edgeClassFromNewElement Kantenklasse, die nicht neu angelegt wird, auch wenn die Kardinalität das bedingen würde. Da diese Funktion hier
     *            für einen anzulegenden Pfad aufgerufen wird, dürfen die Kante, dieses Pfades eben nicht schon hier automatisch angelegt werden.
     * @param pid Process-ID des Dialoges
     * @return den neu angelegten ElementContainer mit allen davon abhängigen Elementen (außer denen, die evtl. auf dem Pfad liegen, der insgesamt
     *         angelegt werden soll)
     */
    private static ModelElement createNodeWithContainerAndDependents(final GraphDocument doc, final ModelElement startElement, final Class<? extends Kante> edgeClassToNewElement, final int directionToNewElement,
            final Class<? extends Kante> edgeClassFromNewElement, final int pid) {
        //Collection des übergebenen doc holen
        GDCollection gdcoll = doc.getCollection();
        //den interactiveMode auf false setzen, damit man nicht nach den Namen für die Zwischenelemente gefragt wird,
        //bei denen der Namen normalerweise nicht generiert wird
        boolean isInteractiveMode = gdcoll.isInteractiveMode();
        gdcoll.setInteractiveMode(false);

        //Richtung der Kante FORWARD -> die Endklasse muss angelegt werden, sonst die Startklasse
        Class<? extends ModelElement> elementClass2Create = directionToNewElement == FORWARD ? Kante.getEndClass(edgeClassToNewElement) : Kante.getStartClass(edgeClassToNewElement);

        ModelElement createdDependent;

        //wenn die Kantenart eine Composition ist
        if (isCompositionFromMasterToSlave(edgeClassToNewElement, directionToNewElement)) {
            //erzeuge ein untergeordnetes Element
            createdDependent = GraphDocument.createAddicted(doc, startElement, edgeClassToNewElement.asSubclass(Composition.class), elementClass2Create, pid);
        } else {
            //das neue Element gleich mit Container im doc anlegen
            ElementContainer createdContainer = doc.createKnotenWithContainer(elementClass2Create, pid);
            //das Element des neu angelgten Containers holen
            createdDependent = createdContainer.getElement();

            //das neue Element mit dem startElement verknüpfen
            link(gdcoll, startElement, createdDependent, edgeClassToNewElement, directionToNewElement, pid);
        }
        //alle Kantentpyen der neu angelegten Elementart holen
        Class<? extends Kante>[] edgeTypes = ModelConstants.getEdgeTypes(elementClass2Create);
        //für jede dieser Kantenarten
        for (int i = 0; i < edgeTypes.length; i++) {
            //aktuelle Kantenart holen
            Class<? extends Kante> edgeType = edgeTypes[i];
            //die Kanten, die über den Pfad als nächstes angelegt werden sollen, dürfen hier nicht angelegt werden
            if (edgeType == edgeClassFromNewElement) {
                continue;
            }
            //wenn das neu angelegte Element StartElement der Kante ist
            if (Kante.isStartClass(edgeType, elementClass2Create)) {
                //hole die MinKardnalität zu dem anderen Element der Kante
                int minCardinalityForwardToOther = Kante.getMinStartToEndCardinality(edgeType);
                //hole alle Kanten des neu angelgten Elementes, die denselben Typ haben
                ArrayList<Kante> edgesForwardTo = createdDependent.getEdgesTo(ModelElement.class, edgeType);
                //Anzahl der bestehenden Kanten der aktuellen Kantenart zu anderen Elementen
                int edgesForwardToCount = edgesForwardTo.size();
                //wenn weitere Kanten angelegt werden müssen
                while (minCardinalityForwardToOther - edgesForwardToCount > 0) {
                    //für das neu angelegte Element müssen auch alle abhängigen Elemente angelegt werden. Da der Pfad von hier nicht weiter
                    //geht, ist die edgeCLassFromNewElement null
                    createNodeWithContainerAndDependents(doc, createdDependent, edgeType, FORWARD, null, pid);
                    edgesForwardToCount++;
                }
                //wenn das neu angelegte Element EndElement der Kante ist
            } else {
                //hole die MinKardnalität zu dem anderen Element der Kante
                int minCardinalityBackwardToOther = Kante.getMinEndToStartCardinality(edgeType);
                //hole alle Kanten des neu angelgten Elementes, die denselben Typ haben
                ArrayList<Kante> edgesBackwardTo = createdDependent.getEdgesFrom(ModelElement.class, edgeType);
                //Anzahl der bestehenden Kanten der aktuellen Kantenart zu anderen Elementen
                int edgesBackwardToCount = edgesBackwardTo.size();
                //wenn weitere Kanten angelegt werden müssen
                while (minCardinalityBackwardToOther - edgesBackwardToCount > 0) {
                    //für das neu angelegte Elemente, müssen auch alle abhängigen Elemente angelegt werden. Da der Pfad von hier nicht weiter
                    //geht, ist die edgeCLassFromNewElement null
                    createNodeWithContainerAndDependents(doc, createdDependent, edgeType, BACKWARD, null, pid);
                    edgesBackwardToCount++;
                }
            }

        }
        gdcoll.setInteractiveMode(isInteractiveMode);
        return createdDependent;
    }

}
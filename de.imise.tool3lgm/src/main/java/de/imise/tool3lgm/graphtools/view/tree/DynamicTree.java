package de.imise.tool3lgm.graphtools.view.tree;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ContextGenerator;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.GraphDocumentListener;
import de.imise.tool3lgm.graphtools.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.Textfeld;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.event.UserFieldListener;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * @author N.N.
 */
public final class DynamicTree extends JTree implements MouseListener, ActionListener, GraphDocumentListener, TreeSelectionListener, UserFieldListener, GraphDocumentOwner {

    /**
     * COMMENTME
     */
    private LGMTreeNode fachebene = null;
    /**
     * COMMENTME
     */
    private LGMTreeNode logebene = null;
    /**
     * COMMENTME
     */
    private LGMTreeNode phyebene = null;
    /**
     * COMMENTME
     */
    private LGMTreeNode awb = null;
    /**
     * COMMENTME
     */
    private LGMTreeNode textFieldDomainLayer = null;
    /**
     * COMMENTME
     */
    private LGMTreeNode textFieldLogicalLayer = null;
    /**
     * COMMENTME
     */
    private LGMTreeNode textFieldPhysicalLayer = null;

    /**
     * COMMENTME
     */
    private GraphDocument doc;

    /**
     * Alle Knoten deren Kinder immer wieder removed und neu angelegt werden.
     */
    private final LGMTreeNode[] nodesToClear = new LGMTreeNode[ModelConstants.TREE_DOMAIN_LAYER_NODES.length + ModelConstants.TREE_LOGICAL_LAYER_NODES.length + ModelConstants.TREE_PHYSICAL_LAYER_NODES.length];

    /**
     * Transaktions-ID, mit der der Baum alle seine Änderungen vornimmt.
     */
    public static final int PID = TransactionManager.STANDARD_PID;

    /**
     * COMMENTME
     */
    private final DefaultTreeModel myModel;
    /**
     * COMMENTME
     */
    private final TreePath rootPath;

    /**
     * @param d
     */
    public DynamicTree(final GraphDocument d) {
        super(new DefaultTreeModel(new LGMTreeNode(Tool3lgmConstants.getResString("browser"), false, false)));

        rootPath = new TreePath(((DefaultTreeModel) getModel()).getPathToRoot((LGMTreeNode) getModel().getRoot()));
        doc = d;
        addMouseListener(this);

        setCellRenderer(new TreeRenderer(doc));
        ((TreeRenderer) getCellRenderer()).setBackgroundNonSelectionColor(getBackground());
        setBackground(getBackground());
        setEditable(false);
        putClientProperty("JTree.lineStyle", "Angled");
        setToggleClickCount(-1);

        setRootVisible(false);
        setShowsRootHandles(true);
        buildTree();
        myModel = (DefaultTreeModel) getModel();
    }

    /**
     * @return
     */
    @Override
    public final GraphDocument getGraphDocument() {
        return doc;
    }

    /**
     * @return
     */
    @Override
    public final GDCollection getCollection() {
        return getGraphDocument().getCollection();
    }

    /**
     * Setzt das übergebene {@link GraphDocument} für diesen Baum und fügt den Baum als {@link GraphDocumentListener} hinzu. Beim vorherigen
     * {@link GraphDocument} des Baumes wird der Baum als Listener entfernt.
     * 
     * @param doc
     */
    public void setGraphDocument(final GraphDocument doc) {
        if (this.doc != null) {
            this.doc.removeGraphDocumentListener(this);
            if (doc != null) {
                doc.addGraphDocumentListener(this);
            }
            this.doc = doc;
        }
        buildTree();
    }

    /**
     * Der allgemeine Baum wird erzeugt oder zurueckgesetzt
     */
    private void createTree() {
        LGMTreeNode top = (LGMTreeNode) treeModel.getRoot();
        if (top.getChildCount() == 0) {
            //der Baum muss die Knoten sortieren, an denen nicht direkt Modelemente hängen (da Reihenfolge je nach Sprache differieren kann)
            //alle anderen Knoten erhalten Sortierung durch Abfragen der alphabetischen Knotenlisten
            fachebene = new LGMTreeNode(Tool3lgmConstants.getResString("domain_layer"), false, true);
            top.add(fachebene);

            int nodesToClearIndex = 0;

            for (int c = 0; c < ModelConstants.TREE_DOMAIN_LAYER_NODES.length; c++) {
                @SuppressWarnings("unchecked")
                LGMTreeNode node = new LGMTreeNode(ModelConstants.getDisplayableName(ModelConstants.TREE_DOMAIN_LAYER_NODES[c]), false, false);
                fachebene.add(node);
                nodesToClear[nodesToClearIndex++] = node;
            }
            logebene = new LGMTreeNode(Tool3lgmConstants.getResString("logical_tool_layer"), false, true);
            top.add(logebene);
            awb = new LGMTreeNode(Tool3lgmConstants.getResString("Anwendungsbaustein_p"), false, true);
            logebene.add(awb);
            for (int c = 0; c < ModelConstants.TREE_LOGICAL_LAYER_NODES.length; c++) {
                Class<? extends ModelElement> clazz = ((Class<?>) ModelConstants.TREE_LOGICAL_LAYER_NODES[c]).asSubclass(ModelElement.class);
                LGMTreeNode node = new LGMTreeNode(ModelConstants.getDisplayableName(clazz), false, false);
                if (Anwendungsbaustein.class.isAssignableFrom(clazz)) {
                    awb.add(node);
                } else {
                    logebene.add(node);
                }
                nodesToClear[nodesToClearIndex++] = node;
            }
            phyebene = new LGMTreeNode(Tool3lgmConstants.getResString("physical_tool_layer"), false, true);
            top.add(phyebene);
            for (int c = 0; c < ModelConstants.TREE_PHYSICAL_LAYER_NODES.length; c++) {
                @SuppressWarnings("unchecked")
                LGMTreeNode node = new LGMTreeNode(ModelConstants.getDisplayableName(ModelConstants.TREE_PHYSICAL_LAYER_NODES[c]), false, false);
                phyebene.add(node);
                nodesToClear[nodesToClearIndex++] = node;
            }
        } else {
            for (int i = 0; i < nodesToClear.length; i++) {
                nodesToClear[i].removeAllChildren();
            }
            if (textFieldDomainLayer != null && textFieldDomainLayer.getParent() == fachebene) {
                fachebene.remove(textFieldDomainLayer);
            }
            if (textFieldLogicalLayer != null && textFieldLogicalLayer.getParent() == logebene) {
                logebene.remove(textFieldLogicalLayer);
            }
            if (textFieldPhysicalLayer != null && textFieldPhysicalLayer.getParent() == phyebene) {
                phyebene.remove(textFieldPhysicalLayer);
            }
        }
    }

    /**
     * @param ec
     * @param performingRebuild
     * @param selDoc
     * @param layer
     */
    public void addObject(final ElementContainer ec, final boolean performingRebuild, final GraphDocument selDoc, final int layer) {
        if (ec instanceof NodeContainer) {
            NodeContainer kc = (NodeContainer) ec;
            NodeContainer kc2 = (NodeContainer) kc.getElement().getContainer(selDoc);
            if (kc2 != null) {
                kc = kc2;
            }
            if (!performingRebuild && getParentNodeOf(kc) != null) {
                return;
            }

            LGMTreeNode elementNode = kc.getTreeNode();
            //			if (kc.getElement() instanceof Prozess) { 
            //				kc.checkIcon();
            //				elementNode = new LGMTreeNode(kc, true, false);
            //			}
            if (elementNode == null) {
                elementNode = new LGMTreeNode(kc, true, false);
            } else {
                elementNode.removeAllChildren();
            }
            LGMTreeNode parent_node = getParentNodeOfType(kc, layer);
            if (parent_node != null) {
                parent_node.add(elementNode);
                addChildren(elementNode, performingRebuild, selDoc);
            }
        }
    }

    /**
     * @param elementNode
     * @param performingRebuild
     * @param selDoc
     */
    private void addChildren(final LGMTreeNode elementNode, final boolean performingRebuild, final GraphDocument selDoc) {
        if (showUserDefinedProperties) {
            addUserDefinedProperties(elementNode, performingRebuild, selDoc);
        }

        if (!showPartOfHierarchy) {
            return;
        }

        ElementContainer kc = (ElementContainer) elementNode.getUserObject();

        LGMTreeNode parent = (LGMTreeNode) elementNode.getParent();

        GraphDocument maindoc = doc.getCollection().getMainGraphDocument();
        ArrayList<ElementContainer> all = kc.getElement().getDirectPartContainer(UserProperties.isEnableSubmodelBrowser() ? selDoc : maindoc);
        loop1: for (int i = 0; i < all.size(); i++) {
            ElementContainer pc = all.get(i);
            ElementContainer pc2 = pc.getElement().getContainer(selDoc);
            if (pc2 != null) {
                pc = pc2;
            }

            if (UserProperties.isEnableSubmodelBrowser()) {
                if (!pc.getElement().isUnique() && pc.getElement().getContainer(selDoc) == null) {
                    continue;
                }
            }

            LGMTreeNode p = parent;
            while (p != null) {
                if (p.getUserObject() == pc) {
                    continue loop1;
                }
                p = (LGMTreeNode) p.getParent();
            }

            LGMTreeNode childNode = new LGMTreeNode(pc, true, false);
            elementNode.add(childNode);
            addChildren(childNode, performingRebuild, selDoc);
        }
    }

    /**
     * @author N.N.
     */
    private class HyperlinkString {
        private String name = "";
        private String value = "";

        public HyperlinkString(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * @param elementNode
     * @param performingRebuild
     * @param selDoc
     */
    private void addUserDefinedProperties(final LGMTreeNode elementNode, final boolean performingRebuild, final GraphDocument selDoc) {
        ElementContainer ec = (ElementContainer) elementNode.getUserObject();
        ModelElement me = ec.getElement();

        Set<UserField> allOfThisElement = me.getUserFieldInputValueKeys();

        GDCollection gdcol = doc.getCollection();
        UserFieldDefinitions ufDefs = gdcol.getUserFieldDefinitions();

        for (UserField uf : ufDefs.getUserFields(me.getClass())) {
            if (uf.hasStyle(UserField.Style.HYPERLINK)) {
                if (uf.isTreeVisibility()) {
                    String value = me.getUserFieldInputValue(uf);
                    HyperlinkString label = new HyperlinkString(uf.getName() + ": " + value, value);
                    LGMTreeNode childNode = new LGMTreeNode(label, true, false);
                    elementNode.add(childNode);
                }
            } else if (uf.hasStyle(UserField.Style.SEPARATOR)) {
                if (uf.isTreeVisibility()) {
                    String label = "--- " + uf.getName() + " ---------";
                    LGMTreeNode childNode = new LGMTreeNode(label, true, false);
                    elementNode.add(childNode);
                }
            } else if (uf.isClassificationUserField()) {
                if (uf.isTreeVisibility()) {

                    //					System.err.println("Name:\t\t"+uf.getName());
                    //					System.err.println("mename:\t\t"+me.getName());
                    //					System.err.println("value:\t\t"+uf.getValue(me));
                    //					System.err.println("formatetvalue:\t"+uf.getFormatedValue(me,true)+"\n");

                    String label = uf.getName() + ": " + uf.getFormatedValue(me, true);
                    LGMTreeNode childNode = new LGMTreeNode(label, true, false);
                    elementNode.add(childNode);
                }
            } else {
                if (uf.isTreeVisibility() && allOfThisElement.contains(uf)) {
                    String value = me.getUserFieldInputValue(uf);
                    String label = uf.getName() + ": " + value;
                    elementNode.add(new LGMTreeNode(label, true, false));
                }
            }
        }

    }

    /**
     * @param objekt
     */
    public void removeObject(final ElementContainer objekt) {
        if (objekt instanceof NodeContainer) {
            LGMTreeNode node = ((NodeContainer) objekt).getTreeNode();
            ((DefaultTreeModel) treeModel).removeNodeFromParent(node);
        }
        ((DefaultTreeModel) treeModel).reload();
    }

    /**
     * @param obj
     * @return
     */
    public static LGMTreeNode getParentNodeOf(final NodeContainer obj) {
        if (obj == null) {
            return null;
        }
        if (obj.getTreeNode() == null) {
            return null;
        }
        return (LGMTreeNode) obj.getTreeNode().getParent();
    }

    /**
     * @param obj
     * @param layer
     * @return
     */
    protected LGMTreeNode getParentNodeOfType(final NodeContainer obj, final int layer) {
        Knoten me = (Knoten) obj.getElement();
        String name = ModelConstants.getDisplayableName(me.getClass());
        switch (layer) {
        case 4:
            for (int n = 0; n < fachebene.getChildCount(); n++) {
                if (name.equals(fachebene.getChildAt(n).toString())) {
                    return (LGMTreeNode) fachebene.getChildAt(n);
                }
            }
            if (me instanceof Textfeld) {
                if (textFieldDomainLayer == null) {
                    textFieldDomainLayer = new LGMTreeNode(ModelConstants.getDisplayableName(Textfeld.class), false, false);
                }
                if (textFieldDomainLayer.getParent() == null) {
                    textFieldDomainLayer.removeAllChildren();
                    fachebene.add(textFieldDomainLayer);
                }
                return textFieldDomainLayer;
            }
            break;
        case 2:
            if (me instanceof Anwendungsbaustein) {
                for (int n = 0; n < awb.getChildCount(); n++) {
                    if (name.equals(awb.getChildAt(n).toString())) {
                        return (LGMTreeNode) awb.getChildAt(n);
                    }
                }
            } else if (me instanceof Textfeld) {
                if (textFieldLogicalLayer == null) {
                    textFieldLogicalLayer = new LGMTreeNode(ModelConstants.getDisplayableName(Textfeld.class), false, false);
                }
                if (textFieldLogicalLayer.getParent() == null) {
                    textFieldLogicalLayer.removeAllChildren();
                    logebene.add(textFieldLogicalLayer);
                }
                return textFieldLogicalLayer;
            } else {
                for (int n = 0; n < logebene.getChildCount(); n++) {
                    if (name.equals(logebene.getChildAt(n).toString())) {
                        return (LGMTreeNode) logebene.getChildAt(n);
                    }
                }
            }
            break;
        case 0:
            for (int n = 0; n < phyebene.getChildCount(); n++) {
                if (name.equals(phyebene.getChildAt(n).toString())) {
                    return (LGMTreeNode) phyebene.getChildAt(n);
                }
            }
            if (me instanceof Textfeld) {
                if (textFieldPhysicalLayer == null) {
                    textFieldPhysicalLayer = new LGMTreeNode(ModelConstants.getDisplayableName(Textfeld.class), false, false);
                }
                if (textFieldPhysicalLayer.getParent() == null) {
                    textFieldPhysicalLayer.removeAllChildren();
                    phyebene.add(textFieldPhysicalLayer);
                }
                return textFieldPhysicalLayer;
            }
            break;
        }

        return null;
    }

    /**
     * @param obj
     * @return
     */
    protected LGMTreeNode createAndGetUserDefParentNodeOfType(final NodeContainer obj) {

        Knoten me = (Knoten) obj.getElement();
        String name = ModelConstants.getDisplayableName(me.getClass());

        switch (me.layerFor()) {
        case 4:
            for (int n = 0; n < fachebene.getChildCount(); n++) {
                if (name.equals(fachebene.getChildAt(n).toString())) {
                    return (LGMTreeNode) fachebene.getChildAt(n);
                }
            }
            break;
        case 2:
            if (me instanceof Anwendungsbaustein) {
                for (int n = 0; n < awb.getChildCount(); n++) {
                    if (name.equals(awb.getChildAt(n).toString())) {
                        return (LGMTreeNode) awb.getChildAt(n);
                    }
                }
            } else {
                for (int n = 0; n < logebene.getChildCount(); n++) {
                    if (name.equals(logebene.getChildAt(n).toString())) {
                        return (LGMTreeNode) logebene.getChildAt(n);
                    }
                }
            }
            break;
        case 0:
            for (int n = 0; n < phyebene.getChildCount(); n++) {
                if (name.equals(phyebene.getChildAt(n).toString())) {
                    return (LGMTreeNode) phyebene.getChildAt(n);
                }
            }
            break;
        }

        return null;
    }

    static boolean showPartOfHierarchy = false;
    static boolean showUserDefinedProperties = false;
    static int count = 0;

    /**
	 * 
	 */
    public void buildTree() {
        if (doc == null) {
            return;
        }
        GraphDocument maindoc = doc.getCollection().getMainGraphDocument();

        removeTreeSelectionListener(this);
        createTree();
        saveExpansionState();
        showPartOfHierarchy = UserProperties.isShowPartOfHierarchy();
        showUserDefinedProperties = UserProperties.isShowUserDefinedPropertiesInModelBrowser();

        int ebene, n;
        for (ebene = 4; ebene >= 0; ebene -= 2) {
            ArrayList<NodeContainer> knoten = maindoc.getLayer(ebene).getKnotenAlphabetical();
            for (n = 0; n < knoten.size(); n++) {
                NodeContainer ec = knoten.get(n);
                ModelElement me = ec.getElement();

                if (UserProperties.isEnableSubmodelBrowser()) {
                    if (me.isUnique()) {
                        if (showPartOfHierarchy && ec.hasParent(maindoc)) {
                            continue;
                        }
                    } else {
                        if (me.getContainer(doc) == null || showPartOfHierarchy && ec.hasParent(doc)) {
                            continue;
                        }
                    }
                } else {
                    if (showPartOfHierarchy && ec.hasParent(maindoc)) {
                        continue;
                    }
                }
                addObject(ec, true, doc, ebene);
            }
        }
        ((DefaultTreeModel) treeModel).reload();
        restoreExpansionState();
        addTreeSelectionListener(this);
        selectObjects();
    }

    /**
     * @param layer
     */
    public void changeActiveLayer(final int layer) {
        changingLayer = true;
        switch (layer) {
        case 4:
            setSelectionPath(new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(fachebene)));
            break;
        case 2:
            setSelectionPath(new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(logebene)));
            break;
        case 0:
            setSelectionPath(new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(phyebene)));
            break;
        }
        changingLayer = false;
    }

    /**
     * Selektiert im Baum alle Elemente, die im dazugehörigen {@link GraphDocument} selektiert sind.
     */
    public void selectObjects() {
        removeTreeSelectionListener(this);
        TreePath[] path = new TreePath[doc.getSelectedRealElementContainerCount()];
        int m = 0;
        GraphDocument mainDoc = doc.getCollection().getMainGraphDocument();

        for (NodeContainer ec : doc.getSelectedRealElementContainerIterable()) {
            ModelElement me = ec.getElement();
            ec = (NodeContainer) me.getContainer(doc);
            if (ec == null) {
                ec = (NodeContainer) me.getContainer(mainDoc);
            }
            LGMTreeNode node = ec.getTreeNode();
            if (node != null) {
                path[m++] = new TreePath(((DefaultTreeModel) treeModel).getPathToRoot(node));
            }
        }
        setSelectionPaths(path);
        if (path.length > 0) {
            scrollPathToVisible(path[path.length - 1]);
        }
        addTreeSelectionListener(this);
    }

    /**
     * @param str
     * @return
     */
    private final JPopupMenu showNewInstanceContextMenu(final String str, final int x, final int y) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem item;

        item = new JMenuItem(Tool3lgmConstants.getResString("neue_instanz"));
        item.addActionListener(this);
        item.setActionCommand("newInstanze " + str);
        menu.add(item);
        Tool3lgm.setLastActionPosition(x + getX(), y + getY());
        menu.show(this, x, y);

        return menu;
    }

    /**
     * @param knot
     * @return
     */
    public static final int getLayerOf(final Knoten knot) {
        return knot.layerFor();
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        if (doc == null) {
            return;
        }
        String str = e.getActionCommand();
        if (str.startsWith("newInstanze ")) {
            StringTokenizer s = new StringTokenizer(str, " ");
            if (s.countTokens() < 2) {
                return;
            }
            s.nextToken();
            String klassenname = s.nextToken();
            doc.createKnotenWithContainer(Tool3lgmConstants.NODE_PACKAGE_NAME + klassenname, PID);
            return;
        }
    }

    /**
     * COMMENTME
     */
    private Object tmpUserObject = null; // Aus Performancegründen hier global

    // für die Kommunikation zwischen mousePressed und mouseClicked 

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (e.getClickCount() <= 1) {
            return;
        }
        boolean left_button = false;

        if (!Tool3lgmConstants.isPopupTrigger(e)) {
            left_button = true;
        }
        // Verknüpftes Teilmodell öffnen
        if ((e.getModifiers() & InputEvent.ALT_MASK) != 0) {
            //Component source, int id, long when, int modifiers,
            //int keyCode, char keyChar, int keyLocation
            dispatchEvent(new KeyEvent(this, KeyEvent.KEY_RELEASED, 0l, 0, KeyEvent.VK_ALT, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD));
            if (left_button && tmpUserObject != null && tmpUserObject instanceof HyperlinkString) {
                String value = ((HyperlinkString) tmpUserObject).getValue();
                try {
                    Desktop.getDesktop().browse(new URI(value));
                } catch (Exception exp) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein") + "\n" + exp.getMessage() + "\n" + exp.toString(), exp);
                }
                return;
            }
            Tool3lgm.tool.changeToLinked(doc);
            return;
        }

        // Teilobjkete zeigen oder verstecken
        if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
            doc.auf_zuklappen(PID);
            return;
        }

        if (left_button && tmpUserObject != null && tmpUserObject instanceof NodeContainer) {
            doc.showPropertyDialog(((NodeContainer) tmpUserObject).getElement());
        }
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void mousePressed(final MouseEvent e) {

        if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
            ContextGenerator.setControlled(true);
        }

        boolean right_button = false;
        int xin = e.getX();
        int yin = e.getY();

        final JTree sourceTree = (JTree) e.getComponent();
        TreePath path = sourceTree.getPathForLocation(xin, yin);

        //Wenn die rechte Maustaste gedrückt wurde, wird <code>right_button</code> true;
        if (Tool3lgmConstants.isPopupTrigger(e)) {
            right_button = true;

            if (path != null) {
                LGMTreeNode lastNode = (LGMTreeNode) path.getLastPathComponent();
                // Wenn eine ElementClass rechtsgeklickt wurde, wird schon ein anderes Kontextmenü geladen, 
                // so dass hier keine weiter Selektion erstellt werden muss.
                if (!(lastNode.getUserObject() instanceof ElementContainer)) {
                    selectionModel.setSelectionPath(getPathForLocation(xin, yin));
                }
            } else {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem item = new JMenuItem(new AbstractAction(Tool3lgmConstants.getResString("expand_all")) {

                    @Override
                    public void actionPerformed(final ActionEvent arg0) {
                        for (int i = 0; i < sourceTree.getRowCount(); i++) {
                            sourceTree.expandRow(i);
                        }
                    }
                });
                menu.add(item);
                menu.show(this, xin + 3, yin + 3);
            }
        }

        if (path != null) {
            Object knot = ((LGMTreeNode) path.getLastPathComponent()).getUserObject();
            tmpUserObject = knot;

            Object lastPathComponent = path.getLastPathComponent();
            if (lastPathComponent == fachebene || lastPathComponent == logebene || lastPathComponent == phyebene) {
                if (right_button) {
                    JPopupMenu pm = ContextGenerator.getLayerContextMenu();
                    if (pm != null) {
                        pm.show(this, xin + 3, yin + 3);
                    }
                }
                return;
            }

            // TODO:FST: Actions für Item aus GlobalActionLibrary holen und setzen
            TreePath parent = path.getParentPath();
            if (parent != null) {
                if (parent.getLastPathComponent() == fachebene || parent.getLastPathComponent() == logebene || parent.getLastPathComponent() == phyebene || parent.getLastPathComponent() == awb) {
                    if (right_button) {
                        String label = path.getLastPathComponent().toString();
                        Class<? extends ModelElement> elementClass = null;
                        for (int c = 0; c < ModelConstants.TREE_CREATABLE_DOMAIN_LAYER_NODES.length; c++) {
                            if (ModelConstants.getDisplayableName(ModelConstants.TREE_CREATABLE_DOMAIN_LAYER_NODES[c]).equals(label)) {
                                elementClass = ((Class<?>) ModelConstants.TREE_CREATABLE_DOMAIN_LAYER_NODES[c]).asSubclass(ModelElement.class);
                            }
                        }
                        for (int c = 0; c < ModelConstants.TREE_CREATABLE_LOGICAL_LAYER_NODES.length; c++) {
                            if (ModelConstants.getDisplayableName(ModelConstants.TREE_CREATABLE_LOGICAL_LAYER_NODES[c]).equals(label)) {
                                elementClass = ((Class<?>) ModelConstants.TREE_CREATABLE_LOGICAL_LAYER_NODES[c]).asSubclass(ModelElement.class);
                            }
                        }
                        for (int c = 0; c < ModelConstants.TREE_CREATABLE_PHYSICAL_LAYER_NODES.length; c++) {
                            if (ModelConstants.getDisplayableName(ModelConstants.TREE_CREATABLE_PHYSICAL_LAYER_NODES[c]).equals(label)) {
                                elementClass = ((Class<?>) ModelConstants.TREE_CREATABLE_PHYSICAL_LAYER_NODES[c]).asSubclass(ModelElement.class);
                            }
                        }
                        if (elementClass == null) {
                            return;
                        }

                        showNewInstanceContextMenu(elementClass.getSimpleName(), xin + 3, yin + 3);
                    }
                }
            }
            if (knot instanceof ElementContainer) {
                if (right_button) {
                    ElementContainer elem = (ElementContainer) knot;
                    //wenn das Element schon in der Selektion war, wird es nur an die hinterste Position in der Selektiion verschoben
                    //und ist somit das Element, bezüglich dessen für andere selektierte Elemente das Kontextmenü angeboten wird
                    elem.getGraphDocument().addToSelection(elem, PID);
                    JPopupMenu pm = Tool3lgm.getContextGenerator().getKnotContextMenu(this);
                    if (pm != null) {
                        pm.show(this, xin + 3, yin + 3);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        ContextGenerator.setControlled(false);
    }

    /**
     * @param node
     */
    private void refreshNode(final TreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode n = node.getChildAt(i);
            myModel.nodeChanged(n);
            refreshNode(n);
        }
    }

    /**
	 * 
	 */
    public void refreshTree() {
        for (int i = 0; i < nodesToClear.length; i++) {
            refreshNode(nodesToClear[i]);
        }
    }

    /**
     * COMMENTME
     */
    boolean changingLayer = false;

    @Override
    public void activeLayerChanged(final GraphDocument source) {
        changingLayer = true;
        changeActiveLayer(doc.getCollection().getActiveLayer());
        changingLayer = false;
    }

    @Override
    public void dataChanged(final GraphDocument source) {
        if (Tool3lgm.DEBUG) {
            System.err.println(getClass().getSimpleName() + "dataChanged() " + source);
        }
        //		System.out.println("dataChanged");
        buildTree();
    }

    @Override
    public void elementGraphicsChanged(final GraphDocument source, final ElementContainer element) {
        //		System.out.println("elementGraphicsChanged");
        refreshTree();
        //		repaint();
    }

    @Override
    public void layoutChanged(final GraphDocument source) {
        //		System.out.println("layoutChanged");
    }

    @Override
    public void elementAdded(final GraphDocument source, final ElementContainer element) {
        //		System.out.println("elementAdded");
        buildTree();
    }

    @Override
    public void elementDeleted(final GraphDocument source, final ElementContainer element) {
        //		System.out.println("elementDeleted");
        buildTree();
    }

    @Override
    public void groupOrderChanged(final GraphDocument source) {
        //		System.out.println("groupOrderChanged");
    }

    @Override
    public void colorsChanged(final GraphDocument source) {
        //		System.out.println("colorsChanged");
    }

    @Override
    public void selectionChanged(final GraphDocument source) {
        //		System.out.println("selectionChanged");
        //		long start = System.currentTimeMillis();
        selectObjects();
        //		long end = System.currentTimeMillis();
        //		System.err.println("DynamicTree.selectionChanged()");
        //		System.err.println(end - start);
    }

    //	----------------------------------------------------------------------------------------------------------------------------------
    //	TreeSelectionListener 

    /**
     * COMMENTME
     */
    int correctingSelectionCount = 0;

    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        if (correctingSelectionCount > 0) {
            return;
        }
        if (changingLayer) {
            return;
        }

        JTree tree = (JTree) e.getSource();

        doc.removeGraphDocumentListener(this);
        doc.start_transaction(PID, false);
        doc.deselectAll(true);
        TreePath[] paths = tree.getSelectionPaths();
        boolean layerChanged = false;
        if (paths != null) {
            for (int i = 0; i < paths.length; i++) {
                LGMTreeNode node = (LGMTreeNode) paths[i].getLastPathComponent();
                Object uo = node.getUserObject();
                if (uo != null) {
                    if (uo instanceof NodeContainer) {
                        if (node.isSelectable()) {
                            NodeContainer knot = (NodeContainer) uo;
                            doc.addToSelection(knot, PID);
                        } else {
                            correctingSelectionCount++;
                            tree.removeSelectionPath(paths[i]);
                            correctingSelectionCount--;
                        }
                    } else if (node == fachebene) {
                        layerChanged = true;
                        doc.getCollection().setActiveLayer(4);
                    } else if (node == logebene) {
                        layerChanged = true;
                        doc.getCollection().setActiveLayer(2);
                    } else if (node == phyebene) {
                        layerChanged = true;
                        doc.getCollection().setActiveLayer(0);
                    }
                }
            }
        }
        doc.finish_transaction(PID, false);
        doc.distributeEvent(GraphDocument.SELECTION_CHANGED);
        if (layerChanged) {
            doc.distributeEvent(GraphDocument.ACTIVE_LAYER_CHANGED);
        }
        doc.addGraphDocumentListener(this);
    }

    Enumeration<TreePath> expansionEnum = null;

    void saveExpansionState() {
        expansionEnum = getExpandedDescendants(rootPath);
    }

    void restoreExpansionState() {
        if (expansionEnum != null) {
            while (expansionEnum.hasMoreElements()) {
                TreePath path = expansionEnum.nextElement();
                expandPath(path);
            }
        }
    }

    ///////////////////////
    // UserFieldListener //
    ///////////////////////

    @Override
    public void userFieldAdded() {
        if (UserProperties.isShowUserDefinedPropertiesInModelBrowser()) {
            buildTree();
        }
    }

    @Override
    public void userFieldRemoved() {
        if (UserProperties.isShowUserDefinedPropertiesInModelBrowser()) {
            buildTree();
        }
    }

    @Override
    public void userFieldValueChanged() {
        if (UserProperties.isShowUserDefinedPropertiesInModelBrowser()) {
            refreshTree();
        }
    }
}

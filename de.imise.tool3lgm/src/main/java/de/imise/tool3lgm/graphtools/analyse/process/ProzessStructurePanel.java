/*
 * Created on 27.11.2003 To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.analyse.process;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.ActionNotDefinedForClassException;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMTreeSelectionListener;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer;
import de.imise.tool3lgm.graphtools.dialog.dragdrop.DragNDropInitializer.DragNDropActionChain;
import de.imise.tool3lgm.graphtools.dialog.panel.AbstractExpandablePanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PrzAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Prozess;
import de.imise.tool3lgm.tools.LGMTree;
import de.imise.tool3lgm.tools.LGMTreeNode;

/**
 * @author AXS
 */
public class ProzessStructurePanel extends AbstractExpandablePanel implements TreeWillExpandListener {

    /**
     * COMMENTME
     */
    private final LGMTree ltree, rtree;

    /**
     * COMMENTME
     */
    private final DefaultTreeModel lmodel, rmodel;

    /**
     * COMMENTME
     */
    private final LGMTreeNode lroot, rroot;

    /**
     * COMMENTME
     */
    private JButton errorBut;

    /**
     * COMMENTME
     */
    private JCheckBox verificationCheck;

    /**
     * COMMENTME
     */
    private final JPanel addRemoveControl;

    /**
     * COMMENTME
     */
    private final JLabel labelRechts;

    /**
     * COMMENTME
     */
    private final JScrollPane spRechts;

    /**
     * COMMENTME
     */
    protected boolean selectionChanged = false;

    /**
     * COMMENTME
     */
    private final Prozess prozess;

    /**
     * werden gebraucht, um die Bäume zu aktualisieren, wenn sich die Anzahl der Aufgaben,
     * Objekttypen oder die Kanten zwischen diesen beiden ändert während dieses Panel offen ist, NUR
     * DANN muss aktualisiert werden, sonst nicht
     */
    private int oldObjectCounter = -1, objectCounter = 0;

    /**
     * COMMENTME
     */
    private boolean willExpand = false;

    /**
     * COMMENTME
     */
    private boolean willCollapse = false;

    /**
     * COMMENTME
     */
    private boolean expandFullPath = false;

    /**
     * COMMENTME
     */
    private TreePath pathToExpandOrCollapse;

    /**
     * COMMENTME
     */
    private static boolean verify = true;

    /**
     * COMMENTME
     */
    private String errorTitle = "";

    /**
     * COMMENTME
     */
    private String errorMessage = "";

    /**
     * COMMENTME
     */
    private List<ElementContainer> aufgabenContainer;

    /**
     * COMMENTME
     */
    private final LGMAction addAction;

    /**
     * COMMENTME
     */
    private final LGMAction removeAction;

    private LGMAction upAction;

    /**
     * COMMENTME
     */
    private LGMAction downAction;

    /**
     * @param pd
     */
    public ProzessStructurePanel(final ElementPropertyDialog pd, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante> multipleConnectableEgdeClass) {
        super(pd, false, searchElementClass, multipleConnectableEgdeClass);

        prozess = (Prozess) getModelElement();

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel labelLinks = new JLabel(Tool3lgmConstants.getResString("proz_ablauf"));

        lroot = new LGMTreeNode(Tool3lgmConstants.getResString("proz_ablauf"), false, false);
        lroot.setSelectable(false);
        lmodel = new DefaultTreeModel(lroot);
        ltree = new LGMTree(lmodel);
        // ltree.setRootVisible(false); TRUE IST STANDARD UND ES M U S S IN
        // DIESEM DIALOG AUCH TRUE SEIN!!!
        // (bei false fehlen im Baum die Kinder und die ganze Ausrichtung stimmt
        // nicht mehr, weil der erste
        // Knoten (wenn root sichtbar ist, dann ist es root selbst) durch ne
        // viewport Verschiebung gar nicht
        // wirklich sichbar ist.)
        ltree.setName("lefttree");
        ltree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ltree.setEditable(false);
        ltree.addTreeWillExpandListener(this);
        ltree.setBorder(BorderFactory.createEmptyBorder(-ltree.getRowHeight(), 0, 3, 0));
        JScrollPane spLinks = new JScrollPane(ltree);
        // dieses preferredSize setzen muss sein, weil sonst aus unklärbaren
        // Gründen beim resizen oder repainten Mist passieren kann
        spLinks.setPreferredSize(new Dimension(1, 1));

        // Das Label (oben) links hinzufuegen
        constraints.anchor = GridBagConstraints.WEST;
        // constraints.weightx = 0.5;
        constraints.weightx = 0;
        add(this, labelLinks, constraints, 0, 0, 5, 1);

        // das linke Scrollpane mit dem linken Baum drin hinzufuegen
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.4;
        constraints.weighty = 1;
        add(this, spLinks, constraints, 0, 1, 5, 1);

        // den Löschen-Button anlegen
        /*
         * / erstmal wieder raugenommen, weil sich das beim Abbrechen nicht zurücknehmen lässt
         * JButton clearBut = new JButton(Tool3lgmConstants.getIcon("clear.gif"));
         * clearBut.setActionCommand("clear"); clearBut.addActionListener(this); /
         */JPanel clearBut = new JPanel(); // Platzhalten für den deaktivierten
                                           // clearBut;

        // Panel für die Buttons zur Aenderung der Aufgabenreihenfolge anlegen
        JPanel upDownControl = new JPanel(new GridLayout(1, 2));

        /*
         * Start: Buttons & Actions erstellen, Actions setzen ...
         */
        JButton upButton = new JButton();
        JButton downButton = new JButton();

        try {
            upAction = getMoveUpAction(ltree, this);
            downAction = getMoveDownAction(ltree, this);
        } catch (ActionNotDefinedForClassException e) {
            e.printStackTrace();
        }
        upButton.setAction(upAction);
        downButton.setAction(downAction);
        /*
         * ... end: Buttons & Actions erstellen, Actions setzen
         */

        upDownControl.add(upButton);
        upDownControl.add(downButton);

        // den auf- und zuklapp-Button anlegen
        // klappBut = new JButton(Tool3lgmConstants.getIcon("auf.gif"));
        // klappBut.setActionCommand("aufklappen");

        final LGMAction showAllAction1 = showAllAction;
        final boolean ver = verify;

        showAllAction = new LGMAction("", (ImageIcon) viewButton.getIcon()) {
            @Override
            public void execute(final EventObject eo) {
                showAllAction1.execute(eo);
                if (ver) {
                    check3LGMBuisnessProcess();
                }
            }
        };

        // den cler-Button hinzufügen
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.weighty = 0;
        //Windows-spezifisch
        //        constraints.ipadx = -30;
        //        constraints.ipady = -10;
        add(this, clearBut, constraints, 0, 2, 1, 1);

        // leeres Panel fuer den Abstand zw. clear-Button und dem upDownControl
        JPanel tmpPanel = new JPanel();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.1;
        // constraints.weighty = 0;
        add(this, tmpPanel, constraints, 1, 2, 1, 1);

        // Panel für die Buttons zur Aenderung der Aufgabenreihenfolge
        // hinzufügen
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        // constraints.weighty = 0;
        //Windows-spezifisch
        //        constraints.ipadx = -60;
        // constraints.ipady = - 10;
        add(this, upDownControl, constraints, 2, 2, 1, 1);

        // leeres Panel fuer den Abstand zw. dem upDownControl und dem klappBut
        tmpPanel = new JPanel();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.1;
        // constraints.weighty = 0;
        add(this, tmpPanel, constraints, 3, 2, 1, 1);

        // den auf- und zuklapp-Button hinzufügen
        constraints.anchor = GridBagConstraints.EAST;
        // constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        // constraints.weighty = 0;
        //Windows-spezifisch
        //        constraints.ipadx = -30;
        // constraints.ipady = - 10;
        add(this, viewButton, constraints, 4, 2, 1, 1);

        /*
         * Start: Aktionen für Verifikation und für FehlerButton setzen ...
         */
        try {
            verificationCheck = new JCheckBox();
            verificationCheck.setSelected(verify);

            errorBut = new JButton();
            errorBut.setEnabled(false);

            LGMAction verificationCheckAction = getVerfikationAction(this, ltree);
            LGMAction errorAction = getFehlerAction(this);

            verificationCheck.setAction(verificationCheckAction);
            errorBut.setAction(errorAction);
        } catch (ActionNotDefinedForClassException andfce) {
            andfce.printStackTrace();
        }
        /*
         * End: Aktionen für Verifikation und für FehlerButton setzen
         */

        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.NONE;
        // constraints.weightx = 0;
        // constraints.weighty = 0;
        constraints.ipadx = 0;
        constraints.ipady = 0;
        tmpPanel = new JPanel();
        tmpPanel.add(verificationCheck);

        tmpPanel.add(errorBut);
        errorBut.setVisible(verify);

        add(this, tmpPanel, constraints, 0, 3, 7, 1);

        labelRechts = new JLabel(Tool3lgmConstants.getResString("verf_auf"));

        if (labelRechts.getPreferredSize().width > labelLinks.getPreferredSize().width) {
            labelLinks.setPreferredSize(labelRechts.getPreferredSize());
            labelLinks.setMinimumSize(labelRechts.getMinimumSize());
            labelLinks.setMaximumSize(labelRechts.getMaximumSize());
        } else {
            labelRechts.setPreferredSize(labelLinks.getPreferredSize());
            labelRechts.setMinimumSize(labelLinks.getMinimumSize());
            labelRechts.setMaximumSize(labelLinks.getMaximumSize());
        }

        rroot = new LGMTreeNode(Tool3lgmConstants.getResString("verf_auf"), false);
        rroot.setSelectable(false);
        rmodel = new DefaultTreeModel(rroot);
        rtree = new LGMTree(rmodel);
        // rtree.setRootVisible(false); TRUE IST STANDARD UND ES M U S S IN
        // DIESEM DIALOG AUCH TRUE SEIN!!!
        // (bei false fehlen im Baum die Kinder und die ganze Ausrichtung stimmt
        // nicht mehr, weil der erste
        // Knoten (wenn root sichtbar ist, dann ist es root selbst) durch ne
        // viewport Verschiebung gar nicht
        // wirklich sichbar ist.)
        rtree.setName("righttree");
        rtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        rtree.setEditable(false);
        rtree.setBorder(BorderFactory.createEmptyBorder(-rtree.getRowHeight(), 0, 3, 0));
        rtree.addTreeWillExpandListener(this);

        ltree.setCellRenderer(treeRenderer);
        rtree.setCellRenderer(treeRenderer);

        spRechts = new JScrollPane(rtree);
        // dieses setzen der Dimension muss sein, damit sich der rechte Baum nie
        // mehr Platz holt,
        // als ihm in den Constraints gegeben wurde (spLinks und spRechts haben
        // beide weigthx=0.5)
        // ->jetzt ist es egal, wenn im linken Baum nichts steht, beide Baeume
        // sind immer gleich breit!
        spRechts.setPreferredSize(new Dimension(1, 1));

        /*
         * Start: Buttons & Actions erstellen, Actions setzen ...
         */
        JButton addButton = new JButton();
        JButton removeButton = new JButton();

        addAction = getAddElementAction(rtree, ltree);
        removeAction = getDisconnectAction(ltree, rtree);
        addButton.setAction(addAction);
        removeButton.setAction(removeAction);
        /*
         * ... end: Buttons & Actions erstellen, Actions setzen
         */

        addRemoveControl = new JPanel();
        addRemoveControl.setLayout(new GridLayout(2, 1));
        addRemoveControl.add(addButton);
        addRemoveControl.add(removeButton);

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

        constraints.anchor = GridBagConstraints.CENTER;
        // constraints.fill = GridBagConstraints.NONE;
        constraints.ipadx = -Tool3lgmConstants.getIcon("arrow_left2.gif").getIconWidth() - 10;
        // constraints.ipady = 0;
        // constraints.weightx = 0;
        // constraints.weighty = 0;
        add(this, addRemoveControl, constraints, 5, 1, 1, 1);

        constraints.ipadx = 0;
        constraints.anchor = GridBagConstraints.WEST;
        // constraints.weightx = 0.5;
        constraints.weightx = 0;
        add(this, labelRechts, constraints, 6, 0, 1, 1);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.4;
        constraints.weighty = 1;
        add(this, spRechts, constraints, 6, 1, 1, 1);

        update();

    }

    /**
     * Haengt an den uebergebenen LGMTreeNode neue LGMTreeNodes mit den Objekttypen an, die von der
     * Aufgabe (UserObject des uebergebenen LGMTreeNode) bearbeitet und interpretiert werden.
     *
     * @param aufgabenContainerNode
     */
    private void appendObjectTypes(final LGMTreeNode aufgabenContainerNode) {
        ModelElement me = ((NodeContainer) aufgabenContainerNode.getUserObject()).getElement();
        List<ElementContainer> ots = me.getConnectedContainer(Objekttyp.class, doc, null, Doppelkante.BACKWARD, false);
        if (ots.size() > 0) {
            LGMTreeNode tmpNode = new LGMTreeNode(Tool3lgmConstants.getResString("AufObjVerbindung_f_b"), false);
            tmpNode.setSelectable(false);
            for (ElementContainer ot : ots) {
                LGMTreeNode otNode = new LGMTreeNode(ot, false);
                otNode.setSelectable(false);
                tmpNode.add(otNode);
            }
            aufgabenContainerNode.add(tmpNode);
        }
        ots = me.getConnectedContainer(Objekttyp.class, doc, null, Doppelkante.FORWARD, false);
        if (ots.size() > 0) {
            LGMTreeNode tmpNode = new LGMTreeNode(Tool3lgmConstants.getResString("AufObjVerbindung_f_f"), false);
            tmpNode.setSelectable(false);
            for (ElementContainer ot : ots) {
                LGMTreeNode otNode = new LGMTreeNode(ot, false);
                otNode.setSelectable(false);
                tmpNode.add(otNode);
            }
            aufgabenContainerNode.add(tmpNode);
        }
    }

    /**
     * Baut den rechten Baum auf
     */
    private void buildRightTree() {
        if (!spRechts.isVisible()) {
            return;
        }
        // System.out.println("buildRightTree");
        // die ArrayList aller Aufgaben diese GraphDocuments holen
        List<ElementContainer> aufgabenConts = doc.getElementContainer(Aufgabe.class);

        // wenn sich die Anzahl der Aufgaben, Objekttypen oder die der
        // Verbindungen zw. Auf und OTs geändert hat
        // muss aktualisiert werden (und nur dann)
        objectCounter = doc.getLayer(4).countType(Aufgabe.class);
        objectCounter += doc.getLayer(4).countType(Objekttyp.class);
        objectCounter += doc.getLayer(4).countType(AufObjVerbindung.class);

        // wenn auf der FE eine Kante (Prozesskanten zählen nicht mit)
        // hinzugekommen ist oder
        // ein Objekttyp gelöscht wurde oder eine Aufgabe hinzugekommen ist ->
        // Baum einfach komplett neu aufbauen
        rtree.getComponent(0).setSize(rtree.getComponent(0).getWidth(), rtree.getComponent(0).getHeight() * 2);
        if (objectCounter != oldObjectCounter || rroot.getChildCount() < aufgabenConts.size()) {
            rroot.removeAllChildren();
            for (int m = 0; m < aufgabenConts.size(); m++) {
                // System.out.println(aufgaben.get(m));
                LGMTreeNode node = new LGMTreeNode(aufgabenConts.get(m), false, false);
                rroot.add(node);
                appendObjectTypes(node);
            }
            rmodel.reload();
            return;
        }
        // es wurde mind. eine Aufgabe auf der FE gelöscht
        // -> diese Aufgaben im Baum löschen (Expandierungen bleiben erhalten,
        // da nicht das ganze TreeModel reloadet wird)
        else if (rroot.getChildCount() > aufgabenConts.size()) {
            int diff = rroot.getChildCount() - aufgabenConts.size();
            for (int n = 0; n < rroot.getChildCount(); n++) {
                boolean found = false;
                for (int m = 0; m < aufgabenConts.size(); m++) {
                    if (((LGMTreeNode) rroot.getChildAt(n)).getUserObject() == aufgabenConts.get(m)) {
                        found = true;
                        break;
                    }
                    if (!found) {
                        rmodel.removeNodeFromParent((LGMTreeNode) rroot.getChildAt(n));
                        diff--;
                    }
                    if (diff == 0) {
                        break;
                    }
                }
            }
        }
        // stellt sicher, dass Namensänderungen von Aufgaben und Objekttypen
        // angezeigt werden
        rtree.repaint();
    }

    /**
     * Baut den linken Baum auf.
     */
    private void buildLeftTree() {
        // System.out.println("buildLeftTree");
        // Die nicht alphabetisch sortierten Aufgaben holen
        aufgabenContainer = prozess.getConnectedContainer(Aufgabe.class, doc, null, Doppelkante.ANY, false);
        // System.out.println(aufgabenContainer);
        // wenn sich die Anzahl der Aufgaben, Objekttypen oder die der
        // Verbindungen zw. Auf und OTs geändert hat
        // muss aktualisiert werden (und nur dann). Wenn der rechte Baum
        // angezeigt wird, dann wurde objectCounter
        // dort schon aktualisiert
        if (!spRechts.isVisible()) {
            objectCounter = doc.getLayer(ModelConstants.DOMAIN_LAYER).countType(Aufgabe.class);
            objectCounter += doc.getLayer(ModelConstants.DOMAIN_LAYER).countType(Objekttyp.class);
            objectCounter += doc.getLayer(ModelConstants.DOMAIN_LAYER).countType(AufObjVerbindung.class);
        }
        // wenn auf der FE eine Kante (Prozesskanten zählen nicht mit)
        // hinzugekommen ist oder ein Objekttyp gelöscht
        // wurde oder der Baum noch leer ist -> Baum einfach komplett neu
        // aufbauen
        if (objectCounter != oldObjectCounter || lroot.getChildCount() == 0) {
            // buildLeftTree wird auf jeden Fall und immer nach buildRightTree
            // aufgerufen
            // -> buildLeftTree aktualisiert oldKantenCount und oldOTCount.
            oldObjectCounter = objectCounter;
            lroot.removeAllChildren();
            for (int m = 0; m < aufgabenContainer.size(); m++) {
                LGMTreeNode node = new LGMTreeNode(aufgabenContainer.get(m), "[" + (m + 1) + "] " + aufgabenContainer.get(m), false);
                lroot.add(node);
                appendObjectTypes(node);
            }
            lmodel.reload();
            if (verify) {
                check3LGMBuisnessProcess();
            }
            return;
        }
        // wenn dem Prozess eine neue Aufgabe hinzugefügt wurde muss der neue
        // Knoten eingefügt werden
        // (dies hat im Gegensatz zum kompletten Neuaufbau den Vorteil, dass
        // expandierte Knoten expandiert bleiben und
        // nur deswegen hier der ganze Aufwand)
        else if (lroot.getChildCount() < aufgabenContainer.size()) {
            // nach dem Knoten, welcher neu eingefügt wird, muss die
            // Nummerierung aller folgenden Knoten
            // um 1 erhöht werden. Das passiert erst ab da, wenn
            // actualizeNodes==true ist.
            boolean actualizeNodes = false;
            for (int m = 0; m < aufgabenContainer.size(); m++) {
                if (m >= lroot.getChildCount() || ((LGMTreeNode) lroot.getChildAt(m)).getUserObject() != aufgabenContainer.get(m)) {
                    LGMTreeNode node = new LGMTreeNode(aufgabenContainer.get(m), "[" + (m + 1) + "] " + aufgabenContainer.get(m), false);
                    appendObjectTypes(node);
                    lmodel.insertNodeInto(node, lroot, m);
                    actualizeNodes = true;
                } else if (actualizeNodes) {
                    ((LGMTreeNode) lroot.getChildAt(m)).setText("[" + (m + 1) + "] " + aufgabenContainer.get(m));
                }
            }
        }
        // es wurde mind. eine Aufgabe auf der FE gelöscht, die auch im Prozess
        // vorkam
        else if (lroot.getChildCount() > aufgabenContainer.size()) {
            boolean actualizeNodes = false;
            // für alle Aufgaben des Prozesses in sub
            for (int m = 0; m < aufgabenContainer.size(); m++) {
                // wenn im Baum eine andere Aufgabe steht als an der selben
                // Position in sub
                if (((LGMTreeNode) lroot.getChildAt(m)).getUserObject() != aufgabenContainer.get(m)) {
                    // lösche diesen Knoten im Baum
                    lmodel.removeNodeFromParent((LGMTreeNode) lroot.getChildAt(m));
                    // dieselbe Position muss nochmal getestet werden
                    m--;
                    // ab jetzt muss für alle folgenden Knoten, die nicht auch
                    // gelöscht werden, die Nummerierung angepasst werden
                    actualizeNodes = true;
                    // wenn die Aufgabe erhalten bleibt und hinter einer Aufgabe
                    // stand, die gelöscht wurde
                } else if (actualizeNodes) {
                    // schreibe die Nummerierung neu
                    ((LGMTreeNode) lroot.getChildAt(m)).setText("[" + (m + 1) + "] " + aufgabenContainer.get(m));
                }
            }
            // am Ende alle Knoten löschen, die mehr im Baum sind, als in der
            // Aufgabenliste sub
            while (lroot.getChildCount() > aufgabenContainer.size()) {
                lmodel.removeNodeFromParent((LGMTreeNode) lroot.getChildAt(lroot.getChildCount() - 1));
            }
        }
        if (verify) {
            check3LGMBuisnessProcess();
        }
        // stellt sicher, dass Namensänderungen von Aufgaben und Objekttypen
        // angezeigt werden
        ltree.repaint();
    }

    @Override
    public void update() {
        buildLeftTree();
        if (isRightSideVisible()) {
            buildRightTree();
        }
    }

    @Override
    protected void showFullDialog() {
        addRemoveControl.setVisible(true);
        labelRechts.setVisible(true);
        spRechts.setVisible(true);
    }

    @Override
    protected void showPartlyDialog() {
        addRemoveControl.setVisible(false);
        labelRechts.setVisible(false);
        spRechts.setVisible(false);
    }

    /**
     * Prueft, wenn 3LGM-Geschaeftsprozesse angelegt werden sollen, welche Aufgaben im rechten Baum
     * selektierbar sein dürfen. setSelectable(false) hat lediglich Auswirkungen auf die
     * Darstellung.
     */
    public void checkRightPossibleTasks() {
        // System.out.println("checkRightPossibleTasks");
        if (!spRechts.isVisible()) {
            return;
        }
        // wenn links gar keine Aufgabe steht, dann müssen rechts alle Aufgaben
        // setSelectable(true) gesetzt werden
        int lrootChildCount = lroot.getChildCount();
        TreePath selPath = ltree.getSelectionPath();
        // rechts alle Aufgaben verfügbar anzeigen, wenn links keine Aufgabe
        // steht, nicht verifiziert werden soll oder die erste Aufgabe
        // selektiert ist
        if (lrootChildCount == 0 || !verificationCheck.isSelected() || selPath != null && ltree.getSelectionRows() != null && ltree.getSelectionRows()[0] == 1) {
            for (int i = 1; i < rtree.getRowCount(); i++) {
                TreePath path = rtree.getPathForRow(i);
                if (path.getPathCount() == 2) {
                    ((LGMTreeNode) path.getLastPathComponent()).setSelectable(true);
                }
            }
            // links ist mind. eine Aufgabe vorhanden, es soll verifiziert
            // werden und es ist nicht die erste Aufgabe selektiert
        } else {
            int selRow = -1;
            // wenn links gar nichts selelktiert ist
            if (selPath == null) {
                selRow = aufgabenContainer.size();
                // ansonsten, wenn der links selektierte Pfad eine Aufgabe ist
            } else if (selPath.getPathCount() == 2) {
                // selRow auf den Index der selektierten Aufgabe innerhalb des
                // Prozesses setzen
                selRow = lroot.getIndex((LGMTreeNode) selPath.getLastPathComponent());
            }
            // wenn links etwas selektiert war, was keine Aufgabe ist
            if (selRow == -1) {
                // rechts einfach alles setSelectable(false) setzen
                for (int i = 1; i < rtree.getRowCount(); i++) {
                    ((LGMTreeNode) rtree.getPathForRow(i).getLastPathComponent()).setSelectable(false);
                    // wenn links gar nichts oder eine Aufgabe selektiert war
                }
            } else {
                // hole die Aufgaben des Prozesses
                List<ModelElement> aufgaben = prozess.getConnectedElements(Aufgabe.class);
                // für alle Zeilen des rechten Baumes
                for (int i = 1; i < rtree.getRowCount(); i++) {
                    selPath = rtree.getPathForRow(i);
                    // wenn der momentane Pfad eine Aufgabe ist
                    if (selPath.getPathCount() == 2) {
                        LGMTreeNode node = (LGMTreeNode) selPath.getLastPathComponent();
                        // wenn von mind. einem Knoten VOR dem selektierten bzw.
                        // (wenn nichts selektiert ist) dem
                        // letzten Knoten mit der i-ten rechten Aufgabe einen
                        // LGM-Buisness-Process-Step bildet, dann
                        // soll die i-te rechte Aufgabe selektierbar sein
                        List<LGMProzessStep> al = prozess.getProcessStepsForAufgabe(aufgaben, ((NodeContainer) node.getUserObject()).getElement(), selRow, true);
                        node.setSelectable(al.size() > 0);
                    }
                }
            }
        }
        rtree.repaint();
    }

    /**
     * Prüft den Prozess, ob er ein 3LGM-Geschäftsprozess ist. Der erste spezifische Fehler, der
     * Auftritt wird sich in errorMessage gemerkt. Alle fehlerhaften Positionen werden im linken
     * Baum mit einem Error-Icon markiert.
     */
    public void check3LGMBuisnessProcess() {
        // System.out.println("check3LGMBuisnessProcess");

        StringBuilder errorBuffer = new StringBuilder();
        errorMessage = "";
        // Das deaktivieren von Aufgabe auf der rechten Seite ist rausgenommen,
        // da beim Drag und Drop nicht von vornerein fest steht,
        // an welcher Position die Aufgabe links hinzugefügt werden soll
        // checkRightPossibleTasks();

        // aufgabenContainer wird in buildLeftTree() aktualisiert
        // aufgabenContainer =
        // modelElement.getConnectedKnot(ModelConstants.AUFGABE
        // ,doc,-1,false,true, false);
        int size = aufgabenContainer.size();
        // der Prozess ist leer
        if (size == 0) {
            errorTitle = Tool3lgmConstants.getErrString("keine_Aufabe");
            errorBuffer.append(Tool3lgmConstants.getErrString("keine_Aufabe_1"));
        }
        // der Prozess hat nur eine Aufgabe
        else if (size == 1) {
            errorTitle = Tool3lgmConstants.getErrString("einzelne_Aufabe");
            errorBuffer.append(Tool3lgmConstants.getErrString("einzelne_Aufabe_1"));
            ((LGMTreeNode) lroot.getChildAt(0)).setIconState(LGMTreeNode.SHOW_ERROR_ICON);
            // der Prozess hat mind. 2 Aufgaben
        } else {
            // Reseten aller Icons auf normal
            for (int i = 0; i < aufgabenContainer.size(); i++) {
                ((LGMTreeNode) lroot.getChildAt(i)).setIconState(LGMTreeNode.SHOW_NORMAL_ICON);
            }
            // hole die Aufgaben des Prozesses
            List<ModelElement> aufgaben = prozess.getConnectedElements(Aufgabe.class);
            // die Icons auf fehlerhaft setzen, deren Aufgabe kein
            // Geschäftsprozessschritt von einer der vorherigen Aufgaben ist
            for (int i = 1; i < size; i++) {
                if (prozess.getProcessStepsForAufgabe(aufgaben, i, true).size() == 0) {
                    if (errorBuffer.length() == 0) {
                        errorTitle = Tool3lgmConstants.getErrString("keine_OT-Kette");
                        errorBuffer.append(Tool3lgmConstants.getErrString("keine_OT-Kette_1"));
                        errorBuffer.append(((NodeContainer) aufgabenContainer.get(i)).getElement());
                        errorBuffer.append(Tool3lgmConstants.getErrString("keine_OT-Kette_2"));
                        errorBuffer.append(i + 1);
                        errorBuffer.append(Tool3lgmConstants.getErrString("keine_OT-Kette_3"));
                    }
                    ((LGMTreeNode) lroot.getChildAt(i)).setIconState(LGMTreeNode.SHOW_ERROR_ICON);
                }
            }
        }
        if (errorBuffer.length() > 0) {
            errorMessage = errorBuffer.toString();
            errorBut.setEnabled(true);
        } else {
            errorBut.setEnabled(false);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    //    public void actionPerformed(final ActionEvent e) {
    //        // System.out.println(); //
    //        System.out.println("actionPerformed command: " + e.getActionCommand());
    //        super.actionPerformed(e);
    //        String str = e.getActionCommand();
    //        if (str.equals("aufklappen")) {
    //            if (verify) {
    //                check3LGMBuisnessProcess();
    //            }
    //        }
    //        if (str.equals("verifikation")) {
    //            verify = verificationCheck.isSelected();
    //            errorBut.setVisible(verify);
    //            if (verify) {
    //                check3LGMBuisnessProcess();
    //            } else {
    //                checkRightPossibleTasks();
    //                for (int i = 0; i < lroot.getChildCount(); i++) {
    //                    ((LGMTreeNode) lroot.getChildAt(i)).setIconState(LGMTreeNode.SHOW_NORMAL_ICON);
    //                }
    //            }
    //            return;
    //        }
    //        if (str.equals("addueber")) {
    //            synchronized (ltree.getTreeLock()) {
    //                TreePath selPath = rtree.getSelectionPath();
    //                //wenn rechts etwas selektiert war
    //                if (selPath != null) {
    //                    //den selektierten Knoten ermitteln
    //                    LGMTreeNode node = (LGMTreeNode) selPath.getLastPathComponent();
    //                    //wenn es sich bei dem rechts selektierten Knoten um eine Aufgabe handelt
    //                    if (node.getUserObject() instanceof NodeContainer) { //seinen Container holen (diese muessen links im Baum geaddet werden)
    //                        NodeContainer knot = (NodeContainer) node.getUserObject();
    //                        if (knot.getElement() instanceof Aufgabe) {
    //                            //wenn links was selektiert ist, dann muss die naechste Aufgabe ueber bzw. vor der selektierten eingefuegt werden
    //                            selPath = ltree.getSelectionPath();
    //                            boolean nothingSelected = true;
    //                            //wenn links etwas selektiert war
    //                            if (selPath != null) {
    //                                nothingSelected = false;
    //                                node = (LGMTreeNode) selPath.getLastPathComponent();
    //                                //prüfen, ob eine Aufgabe selektiert ist
    //                                if (node.getUserObject() instanceof NodeContainer && ((NodeContainer) node.getUserObject()).getElement() instanceof Aufgabe) {
    //                                    int selRow = ltree.getRowForPath(selPath);
    //                                    int index = lmodel.getIndexOfChild(lroot, node);
    //                                    doc.exec("LINK " + "null" + " " + "null" + " " + modelElement.getHashString() + " " + knot.getHashString() + " " + index, dialog.getTransactionID());
    //                                    // ((NodeContainer)modelElement.getContainer(doc)).addSpecialInfoTarget(index,knot);
    //                                    selRow++;
    //                                    while (ltree.getPathForRow(selRow).getPathCount() != 2) {
    //                                        selRow++;
    //                                    }
    //                                    setSelectionRow(ltree, selRow);
    //                                }
    //                            }
    //                            //wenn links nichts selektiert war -> einfach hinten anhaengen
    //                            if (nothingSelected) {
    //                                //es wird eine neue ProzessKante angelegt, aber der rechte Baum braucht nicht aktualisiert werden
    //                                doc.exec("LINK " + "null" + " " + "null" + " " + modelElement.getHashString() + " " + knot.getHashString() + " " + modelElement.countConnections(), dialog.getTransactionID());
    //                                ltree.scrollRowToVisible(ltree.getRowCount() - 1);
    //                            }
    //                        }
    //                    }
    //                }
    //            }
    //            return;
    //        }
    //        if (str.equals("removeueber")) {
    //            synchronized (rtree.getTreeLock()) {
    //                TreePath selPath = ltree.getSelectionPath();
    //                //wenn links etwas selektiert war
    //                if (selPath != null) {
    //                    LGMTreeNode node = (LGMTreeNode) selPath.getLastPathComponent();
    //                    //prüfen, ob eine Aufgabe selektiert ist
    //                    Object knot = node.getUserObject();
    //                    //auf keinen Fall hier gleich auf ElementContainer casten, weil es auch ein String sein kann !!!
    //                    if (!(knot instanceof String) && ((ElementContainer) knot).getElement() instanceof Aufgabe) {
    //                        //es wird eine neue ProzessKante angelegt, aber der rechte Baum braucht nicht aktualisiert werden
    //                        int index = lmodel.getIndexOfChild(lroot, node);
    //                        doc.exec("UNLINK " + modelElement.getHashString() + " " + ((ElementContainer) knot).getHashString() + " " + index, dialog.getTransactionID());
    //                    }
    //                    //####################################################################################################
    //                    //Dies hier evtl. weglassen, da es auf nem lahmen Rechner und nem großen Modell rel. lange dauern kann
    //                    //Die der auf der linken Seite entfernten Aufgabe auf der rechten Seite entsprechende wird selektiert.
    //                    //Diese Funktion geht davon aus, dass der rechte Baum komplett expandiert ist, was er in dem Fall,
    //                    //dass alle Kinder von rroot Blätter sind und rroot selbst nicht angezeigt wird immer automatisch ist.
    //                    for (int i = 0; i < rtree.getRowCount(); i++) {
    //                        if (((LGMTreeNode) rtree.getPathForRow(i).getLastPathComponent()).getUserObject() == knot) {
    //                            setSelectionRow(rtree, i);
    //                            break;
    //                        }
    //                    }
    //                }
    //                return;
    //            }
    //        }
    //        if (str.equals("moveup")) {
    //            //Aufaben haben Pfadlänge 2 das nicht sichtbare root hat die 1)
    //            TreePath selPath = ltree.getSelectionPath();
    //            //wenn links eine Aufgabe selektiert ist
    //            if (selPath != null && selPath.getPathCount() == 2) {
    //                //Position der selektierten Aufgabe im Baum bzw. Prozess holen
    //                int pos1 = lmodel.getIndexOfChild(lroot, selPath.getLastPathComponent());
    //                //wenn nicht die erste sondern eine Aufgabe dahinter selektiert ist
    //                if (pos1 > 0) {
    //                    //jetzt die Position der über der selektierten Aufgabe liegenden Aufgabe holen
    //                    //-> von dieser alle evtl. expandierten Unterknoten merken
    //                    //-> sie removen und unter der selektierten wieder einfügen
    //                    //-> alles was von ihr expandiert war, wieder expandieren
    //                    int pos2 = ltree.getRowForPath(selPath) - 1;
    //                    TreePath path = ltree.getPathForRow(pos2);
    //                    while (path.getPathCount() > 2) {
    //                        pos2--;
    //                        path = ltree.getPathForRow(pos2);
    //                    }
    //                    //wenn die selektierte Aufgabe expandierte Unterknoten hat (können max. 2 sein, nämlich
    //                    //"Interpretiert" und "Bearbeitet"), dann sind diese TreePathes jetzt in enum
    //                    Enumeration en = ltree.getExpandedDescendants(path);
    //                    //jetzt den Baum anpassen (DER WIRD IN DIESEM FALL IN buildLeftTree() NICHT VERÄNDERT)
    //                    //und weil hier noch die Expasionen anpasst werden (über enum), soll das auch hier bleiben!
    //                    LGMTreeNode node = (LGMTreeNode) lroot.getChildAt(pos1 - 1);
    //                    //den oberen Knoten holen
    //                    lmodel.removeNodeFromParent(node);
    //                    //ihn entfernen
    //                    lmodel.insertNodeInto(node, lroot, pos1);
    //                    //ihn einen tiefer als vorher einfügen
    //                    //wenn die selektierte Aufgabe expandiert war
    //                    if (en != null) {
    //                        expandFullPath = true;
    //                        //muss sein wegen treeWillExpand, damits auch wirklich expandiert wird
    //                        ltree.expandRow(pos1 + 1);
    //                        //den Knoten wieder expandieren
    //                        while (en.hasMoreElements()) {
    //                            ltree.expandPath((TreePath) en.nextElement());
    //                            //seine Unterknoten auch expandieren
    //                        }
    //                        //das muss immer nch einem Expandieren zurückgesetzt werden (siehe treeWillExpand)
    //                        willExpand = false;
    //                        expandFullPath = false;
    //                    }
    //                    ltree.scrollPathToVisible(selPath);
    //                    //jetzt erst die Nummerierungen anpassen (auf keinen Fall vor dem Expandieren, weil sonst die Pfade nicht mehr stimmen)
    //                    node.setText("[" + (pos1 + 1) + "] " + node.getUserObject());
    //                    node = (LGMTreeNode) lroot.getChildAt(pos1 - 1);
    //                    node.setText("[" + pos1 + "] " + node.getUserObject());
    //                    //das switchen in den connections vom Prozess ausführen
    //                    doc.exec("SWAP_EDGE_POSITIONS " + modelElement.getHashString() + " " + pos1 + " " + (pos1 - 1), dialog.getTransactionID());
    //                }
    //            }
    //            ltree.repaint();
    //            return;
    //        }
    //        if (str.equals("movedown")) {
    //            //Aufaben haben Pfadlänge 2 (das nicht sichtbare root hat die 1)
    //            TreePath selPath = ltree.getSelectionPath();
    //            if (selPath != null && selPath.getPathCount() == 2) {
    //                int pos1 = lmodel.getIndexOfChild(lroot, selPath.getLastPathComponent());
    //                if (pos1 < lroot.getChildCount() - 1) {
    //                    int pos2 = ltree.getRowForPath(selPath) + 1;
    //                    TreePath path = ltree.getPathForRow(pos2);
    //                    while (path.getPathCount() > 2) {
    //                        pos2++;
    //                        path = ltree.getPathForRow(pos2);
    //                    }
    //                    Enumeration en = ltree.getExpandedDescendants(path);
    //                    LGMTreeNode node = (LGMTreeNode) lroot.getChildAt(pos1 + 1);
    //                    lmodel.removeNodeFromParent(node);
    //                    lmodel.insertNodeInto(node, lroot, pos1);
    //                    if (en != null) {
    //                        expandFullPath = true;
    //                        ltree.expandRow(pos1 + 1);
    //                        while (en.hasMoreElements()) {
    //                            ltree.expandPath((TreePath) en.nextElement());
    //                            //seine Unterknoten auch expandieren
    //                        }
    //                        willExpand = false;
    //                        expandFullPath = false;
    //                    }
    //                    ltree.scrollPathToVisible(selPath);
    //                    node.setText("[" + (pos1 + 1) + "] " + node.getUserObject());
    //                    node = (LGMTreeNode) lroot.getChildAt(pos1 + 1);
    //                    node.setText("[" + (pos1 + 2) + "] " + node.getUserObject());
    //                    doc.exec("SWAP_EDGE_POSITIONS " + modelElement.getHashString() + " " + pos1 + " " + (pos1 + 1), dialog.getTransactionID());
    //                }
    //            }
    //            ltree.repaint();
    //            return;
    //        }
    //        if (str.equals("fehler")) {
    //            JOptionPane.showMessageDialog(this, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
    //            return;
    //        }
    //
    //        /*
    //         * Das hier würde sich bei drücken auf Abbrechen nicht zurücknehmen lassen, da das
    //         * UNDO für link nicht gesetzt wurde
    //         */
    //
    //        if (str.equals("clear")) {
    //            //diese Funktion geht davon aus, dass der Baum komplett expandiert ist, was er in dem Fall,
    //            //dass alle Kinder von lroot Blätter sind und lroot selbst nicht angezeigt wird immer automatisch ist.
    //            for (int i = ltree.getRowCount() - 1; i >= 0; i--) {
    //                NodeContainer knotCont = (NodeContainer) ((LGMTreeNode) lroot.getChildAt(i)).getUserObject();
    //                doc.exec("UNLINK " + prozess.getHashString() + " " + knotCont.getHashString(), dialog.getTransactionID());
    //                lroot.remove(i);
    //            }
    //            return;
    //        }
    //    }

    @Override
    public void treeWillExpand(final TreeExpansionEvent e) throws ExpandVetoException {
        //System.out.println("will-expand");
        //System.out.println(willExpand + "  " + willCollapse + "  " +
        //expandFullPath);
        if (!willExpand || expandFullPath) {
            willExpand = true;
            pathToExpandOrCollapse = e.getPath();
            if (!expandFullPath) {
                throw new ExpandVetoException(e);
            }
        } else {
            willExpand = false;
        }
    }

    @Override
    public void treeWillCollapse(final TreeExpansionEvent e) throws ExpandVetoException {
        //System.out.println("will-collapse");
        if (!willCollapse) {
            willCollapse = true;
            willExpand = false;
            pathToExpandOrCollapse = e.getPath();
            throw new ExpandVetoException(e);
        }
        willCollapse = false;
    }

    public void valueChanged(final TreeSelectionEvent e) {
        // System.out.println("valueChanged");
        selectionChanged = true;
        if (e == null) {
            return;
        }
        lastSelEvent = e;

        // wenn sich links die Selektion geändert hat, die rechte Seite sichtbar
        // ist und verifiziert werden soll
        if (verificationCheck.isSelected() && labelRechts.isShowing() && ((JTree) e.getSource()).getName().equals("lefttree")) {
            checkRightPossibleTasks();
        }

        // man kann immer nur einen einzelnen TreeNode selektieren->
        // wenn sich die Selektion ändert, einfach erst highlight leer machen
        // und dann das neu selektierte hinzufügen (wenn der TreeNode ein
        // ModelElement
        // und nicht nur einen String repräsentiert)
        removeHighLights();
        LGMTreeNode node = null;
        if (e.getNewLeadSelectionPath() != null) {
            node = (LGMTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
            // root ist zwar in beiden Bäumen durch Verschieben des
            // Darstellungsoffsets nicht sichtbar, kann
            // aber per Tastaur erreicht werden ->wenn root selektiert wird,
            // einfach die erste Aufgabe selektieren
            if (node.isRoot()) {
                ((JTree) e.getSource()).setSelectionRow(1);
                return;
            }
            // die Nodes, die die Strings "Bearbeitet" und "Interpretiert"
            // enthalten, werden nicht zu highlight geaddet
            if (!(node.getUserObject() instanceof String)) {
                NodeContainer knot = (NodeContainer) node.getUserObject();
                knot.setHighLight(true);
                addHighlight(knot);
            }
        }
        NodeContainer kc = (NodeContainer) prozess.getContainer(doc);
        doc.deselectAll(false);
        doc.addToSelection(kc, dialog.getTransactionID());
        /*
         * for (int pos = 0; pos < doc.selectedContainer.size(); pos++) { ElementContainer ec =
         * (ElementContainer) doc.selectedContainer.get(pos); if (doc.selectedContainer.get(pos) !=
         * kc) doc.deselect(ec, dialog.getTransactionID()); }
         */
        if (node != null && !(node.getUserObject() instanceof String)) {
            doc.addToSelection((NodeContainer) node.getUserObject(), dialog.getTransactionID());
        }
    }

    /**
     * @param e
     */
    public final void mousePressed(final MouseEvent e) {
        // System.out.println("pressed");
        JTree tree = (JTree) e.getSource();
        int selRow = tree.getLeadSelectionRow();
        int clickedRow = tree.getRowForLocation(e.getX(), e.getY());
        // bei Links-Klick muss hier nichts gemacht werden, es geht also nur um
        // das PopupMenu (Links-Klick in mousePressed)
        if (!Tool3lgmConstants.isPopupTrigger(e)) {
            return;
        }
        // nichts selektiert
        if (selRow == -1) {
            // es ist nichts selektiert und es wurde mit rechts neben die
            // Einträge geklickt -> return
            if (clickedRow == -1) {
                return;
            }
            // es wurde mit rechts auf einen (logischerweise nicht selektierten)
            // Eintrag geklickt
            // wenn die angeklickte Zeile eine Aufgabe oder ein Objekttyp ist
            Object o = ((LGMTreeNode) tree.getPathForRow(clickedRow).getLastPathComponent()).getUserObject();
            if (!(o instanceof String)) {
                // den angeklickten selektieren
                setSelectionRow(tree, clickedRow);
                Tool3lgm.getContextGenerator().getTreeKnotContextMenu((ElementContainer) o).show(e.getComponent(), e.getX() + 3, e.getY() + 3);
            }
        }
        // es ist etwas selektiert
        else {
            // wenn mit rechts auf einen Eintrag und nicht neben die Einträge
            // geklickt wurde
            if (clickedRow != -1) {
                // wenn keine Aufgabe oder ein Objekttyp angeklickt wurde ->
                // return
                if (((LGMTreeNode) tree.getPathForRow(clickedRow).getLastPathComponent()).getUserObject() instanceof String) {
                    return;
                } else if (clickedRow != selRow) {
                    setSelectionRow(tree, clickedRow);
                }
            }
            // das PopupMenü für den jetzt selektierten Eintrag anzeigen
            Object o = ((LGMTreeNode) tree.getPathForRow(clickedRow).getLastPathComponent()).getUserObject();
            Tool3lgm.getContextGenerator().getTreeKnotContextMenu((ElementContainer) o).show(e.getComponent(), e.getX() + 3, e.getY() + 3);
        }
    }

    /**
     * @param e
     */
    public final void mouseClicked(final MouseEvent e) {
        // System.out.println("MouseClicked");
        // rechte Maustaste wird in mousePressed behandelt
        if (Tool3lgmConstants.isPopupTrigger(e)) {
            return;
        }
        // wenn in einem der Bäume 1x geklickt wurde oder durch Doppelklicken
        // auf einen Knoten mit Pfadlänge>2 (also
        // alles ausser der Wurzel und den Aufgaben)
        // (pathToExpandOrCollapse ist nur != null, wenn vorher treeWillExpand
        // oder treeWillCollapse ausgelöst wurde)
        JTree tree = (JTree) e.getSource();
        if (e.getClickCount() == 1 || pathToExpandOrCollapse != null && pathToExpandOrCollapse.getPathCount() > 2) {
            // wenn vorher treeWillExpand ausgelöst wurde (dort wurde das erste
            // eigentliche Expandieren verhindert, da
            // Aufgaben beim Doppelklicken in den jeweils anderen Baum
            // verschoben werden sollen)
            if (willExpand) {
                // expandFullPath = true bewirkt, dass jetzt beim Auslösen von
                // treeWillExpand die Expansion auch wirklich
                // passiert
                expandFullPath = true;
                int row = tree.getRowForPath(pathToExpandOrCollapse);
                // Expandieren des obersten Knoten des zu expandierenden Knotens
                tree.expandRow(row);
                for (int n = row + 1; n < tree.getRowCount(); n++) {
                    // alle Knoten mit Pfadlänge größer 2 unter diesem Knoten
                    // expandieren (das >2 bewirkt, dass nur bis
                    // zur nächsten Aufgabe expandiert wird)
                    if (tree.getPathForRow(n).getPathCount() > 2) {
                        tree.expandRow(n);
                    } else {
                        break;
                    }
                }
                // willExpand und expandFullPath zurücksetzen
                willExpand = false;
                expandFullPath = false;
                pathToExpandOrCollapse = null;
            }
            // wenn vorher treeWillCollapse ausgelöst wurde (dort wurde das
            // eigentliche Collapsen verhindert, da Aufgaben
            // beim Doppelklicken in den jeweils anderen Baum verschoben werden
            // sollen)
            else if (willCollapse) {
                tree.collapsePath(pathToExpandOrCollapse);
                pathToExpandOrCollapse = null;
            }
            // das hier tritt ein, wenn man 1x irgendwo hingeklickt hat und kein
            // Expandieren oder Collapsen ausgelöst wurde
            // -> eine evtl. vorhandene Selektion wird aufgehoben (wichtig, weil
            // man im linken Baum Aufgaben nur hinten
            // anhängen kann, wenn nichts selektiert ist)
            else {
                int selRow = tree.getLeadSelectionRow();
                if (selRow != -1) {
                    int row = tree.getRowForLocation(e.getX(), e.getY());
                    if (selRow != -1 && (row == -1 || row == selRow && !selectionChanged)) {
                        setSelectionRow(tree, -1);
                    } else {
                        selectionChanged = false;
                    }
                }
            }
            // wenn mind. 2x geklickt wurde und kein Expandieren oder Collapsen
            // ausgelöst wurde, dann wurden auf jeden
            // Fall Aufgaben doppelt angeklickt, welche entweder von links nach
            // rechts oder umgekehrt verschoben werden
        } else {
            String sourceName = ((JTree) e.getSource()).getName();
            if (sourceName.equals("righttree")) {
                addAction.execute(e);
            } else if (sourceName.equals("lefttree")) {
                removeAction.execute(e);
            }
            willExpand = false;
            willCollapse = false;
            pathToExpandOrCollapse = null;
        }
    }

    @Override
    protected void expandTree(final JTree tree) {
        expandFullPath = true;
        for (int n = 0; n < tree.getRowCount(); n++) {
            // hier wird immer treeWillExpand ausgelöst und damit er wirklich
            // expandiert muss die ganze Zeit expandFullPath = true sein
            tree.expandRow(n);
        }
        willExpand = false;
        expandFullPath = false;
    }

    /**
     * Selekiert in destinationTree die Zeile row. Wird row=-1 uebergeben, wird eine evtl. Selekion
     * gelöscht.
     *
     * @param destinationTree
     * @param row
     */
    public void setSelectionRow(final JTree destinationTree, final int row) {
        if (row == -1) {
            destinationTree.setSelectionPath(null);
        } else {
            destinationTree.setSelectionRow(row);
            destinationTree.scrollRowToVisible(row);
        }
        selectionChanged = false;
    }

    /**
     * @param b
     */
    public void expandFullPath(final boolean b) {
        expandFullPath = b;
    }

    /**
     * @param b
     */
    public void willExpand(final boolean b) {
        willExpand = b;
    }

    /**
     * @param b
     */
    public static void verify(final boolean b) {
        verify = b;
    }

    /**
     * @return
     */
    public JCheckBox getVerificationCheckBox() {
        return verificationCheck;
    }

    /**
     * @return
     */
    public JButton getErrorButton() {
        return errorBut;
    }

    /**
     * @return
     */
    public String getErrorTitel() {
        return errorTitle;
    }

    /**
     * @return
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return
     */
    @Override
    protected DragNDropActionChain[] collectDragNDropActionChains() {
        DragNDropActionChain dndAC1 = DragNDropInitializer.createNewDragNDropActionChain(rtree, ltree, addAction);
        DragNDropActionChain dndAC2 = DragNDropInitializer.createNewDragNDropActionChain(ltree, rtree, removeAction);

        DragNDropActionChain dndAC3 = DragNDropInitializer.createNewDragNDropActionChain(new LGMTree[] {
                ltree,
                rtree,
                ltree
        }, new LGMAction[] {
                removeAction,
                addAction
        });

        return new DragNDropActionChain[] {
                dndAC1,
                dndAC2,
                dndAC3
        };

    }

    /**
     * @return
     */
    @Override
    public LGMTree[] getAllDragNDropTrees() {
        return new LGMTree[] {
                rtree,
                ltree
        };
    }

    // --- kleine Hilfsmethoden --- Ende ---

    /**
     * @param edp
     * @param tree
     * @return
     * @throws ActionNotDefinedForClassException
     */
    private static final LGMAction getVerfikationAction(final ElementDialogPanel edp, final JTree tree) throws ActionNotDefinedForClassException {
        final ElementDialogPanel pane = edp;
        final LGMTreeNode lroot = (LGMTreeNode) tree.getModel().getRoot();
        if (edp instanceof ProzessStructurePanel) {
            return new LGMAction(Tool3lgmConstants.getResString("verif")) {
                @Override
                public void execute(final EventObject e) {
                    ProzessStructurePanel panel = (ProzessStructurePanel) pane;
                    boolean verify = panel.getVerificationCheckBox().isSelected();
                    ProzessStructurePanel.verify(verify);
                    panel.getErrorButton().setVisible(verify);
                    if (verify) {
                        panel.check3LGMBuisnessProcess();
                    } else {
                        panel.checkRightPossibleTasks();
                        for (int i = 0; i < lroot.getChildCount(); i++) {
                            ((LGMTreeNode) lroot.getChildAt(i)).setIconState(LGMTreeNode.SHOW_NORMAL_ICON);
                        }
                    }
                    return;
                }
            };
        }
        throw new ActionNotDefinedForClassException(edp.getClass().getName());
    }

    /**
     * @param edp
     * @return
     * @throws ActionNotDefinedForClassException
     */
    private static final LGMAction getFehlerAction(final ElementDialogPanel edp) throws ActionNotDefinedForClassException {
        final ElementDialogPanel pane = edp;
        if (edp instanceof ProzessStructurePanel) {
            return new LGMAction(Tool3lgmConstants.getResString("fehler")) {
                @Override
                public void execute(final EventObject e) {
                    ProzessStructurePanel panel = (ProzessStructurePanel) pane;
                    JOptionPane.showMessageDialog(panel, panel.getErrorMessage(), panel.getErrorTitel(), JOptionPane.ERROR_MESSAGE);
                }
            };
        }
        throw new ActionNotDefinedForClassException(edp.getClass().getName());
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "addButtons" der Panels angefügt werden.
     *
     * @param srcTree
     * @param targetTree
     */
    private final LGMAction getAddElementAction(final JTree srcTree, final JTree targetTree) {

        final GraphDocument doc = getGraphDocument();
        final GDCollection gdcoll = doc.getCollection();
        final ModelElement modelElement = getModelElement();

        return new LGMAction("", Tool3lgmConstants.getIcon("arrow_left2.gif")) {

            @Override
            public void execute(final EventObject eo) {
                LGMActionLibrary.getDragNDropLocateElementAsTargetAction(targetTree).execute(eo);
                TreeModel lmodel = targetTree.getModel();
                LGMTreeNode lroot = (LGMTreeNode) lmodel.getRoot();

                synchronized (targetTree.getTreeLock()) {
                    TreePath selPath = srcTree.getSelectionPath();
                    // wenn rechts etwas selektiert war
                    if (selPath != null) {
                        // den selektierten Knoten ermitteln
                        LGMTreeNode node = (LGMTreeNode) selPath.getLastPathComponent();
                        // wenn es sich bei dem rechts selektierten Knoten
                        // um eine Aufgabe handelt
                        if (node.getUserObject() instanceof NodeContainer) {
                            // seinen Container holen (diese muessen links
                            // im Baum geaddet werden)
                            NodeContainer knot = (NodeContainer) node.getUserObject();
                            if (knot.getElement() instanceof Aufgabe) {
                                // wenn links was selektiert ist, dann muss
                                // die naechste Aufgabe ueber bzw. vor der
                                // selektierten eingefuegt werden
                                selPath = targetTree.getSelectionPath();
                                boolean nothingSelected = true;
                                // wenn links etwas selektiert war
                                if (selPath != null) {
                                    nothingSelected = false;
                                    node = (LGMTreeNode) selPath.getLastPathComponent();
                                    // prüfen, ob eine Aufgabe selektiert ist
                                    if (node.getUserObject() instanceof NodeContainer && ((NodeContainer) node.getUserObject()).getElement() instanceof Aufgabe) {
                                        int selRow = targetTree.getRowForPath(selPath);
                                        int index = lmodel.getIndexOfChild(lroot, node);
                                        gdcoll.link(PrzAufVerbindung.class, modelElement, knot.getElement(), index, GDCommands.INVALID_EDGE_INDEX, dialog.getTransactionID());
                                        // ((NodeContainer)modelElement.getContainer(doc)).addSpecialInfoTarget(index,knot);
                                        selRow++;
                                        while (targetTree.getPathForRow(selRow).getPathCount() != 2) {
                                            selRow++;
                                        }
                                        setSelectionRow(targetTree, selRow);

                                    }
                                }
                                // wenn links nichts selektiert war ->
                                // einfach hinten anhaengen
                                if (nothingSelected) {
                                    // es wird eine neue ProzessKante
                                    // angelegt, aber der rechte Baum
                                    // braucht nicht aktualisiert werden
                                    gdcoll.link(PrzAufVerbindung.class, modelElement, knot.getElement(), modelElement.getEdgesCount(), GDCommands.INVALID_EDGE_INDEX, dialog.getTransactionID());
                                    targetTree.scrollRowToVisible(targetTree.getRowCount() - 1);
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "removeButtons" der Panels angefügt werden.
     *
     * @param srcTree
     * @param targetTree
     */
    private final LGMAction getDisconnectAction(final JTree srcTree, final JTree targetTree) {

        final GDCollection gdcoll = getGraphDocument().getCollection();
        final ElementPropertyDialog dialog = getDialog();
        final ModelElement modelElement = getModelElement();

        return new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {
            @Override
            public void execute(final EventObject e) {
                LGMActionLibrary.getDragNDropLocateElementAsTargetAction(targetTree).execute(e);
                TreeModel lmodel = srcTree.getModel();
                LGMTreeNode lroot = (LGMTreeNode) lmodel.getRoot();

                synchronized (targetTree.getTreeLock()) {
                    TreePath selPath = srcTree.getSelectionPath();
                    // wenn links etwas selektiert war
                    if (selPath != null) {
                        LGMTreeNode node = (LGMTreeNode) selPath.getLastPathComponent();
                        // prüfen, ob eine Aufgabe selektiert ist
                        Object knot = node.getUserObject(); // auf keinen
                        // Fall hier
                        // gleich auf
                        // ElementContainer
                        // casten, weil
                        // es auch ein
                        // String sein
                        // kann !!!
                        if (!(knot instanceof String)) {
                            ModelElement otherMe = ((ElementContainer) knot).getElement();
                            if (otherMe instanceof Aufgabe) {
                                // es wird eine neue ProzessKante angelegt, aber
                                // der rechte Baum braucht nicht aktualisiert
                                // werden
                                int index = lmodel.getIndexOfChild(lroot, node);
                                gdcoll.unlink(modelElement, otherMe, PrzAufVerbindung.class, index, dialog.getTransactionID());
                            }
                        }
                        // ####################################################################################################
                        // Dies hier evtl. weglassen, da es auf nem lahmen
                        // Rechner und nem großen Modell rel. lange dauern
                        // kann
                        // Die der auf der linken Seite entfernten Aufgabe
                        // auf der rechten Seite entsprechende wird
                        // selektiert.
                        // Diese Funktion geht davon aus, dass der rechte
                        // Baum komplett expandiert ist, was er in dem Fall,
                        // dass alle Kinder von rroot Blätter sind und rroot
                        // selbst nicht angezeigt wird immer automatisch
                        // ist.
                        for (int i = 0; i < targetTree.getRowCount(); i++) {
                            if (((LGMTreeNode) targetTree.getPathForRow(i).getLastPathComponent()).getUserObject() == knot) {
                                setSelectionRow(targetTree, i);
                                break;
                            }
                        }
                    }
                    return;
                }
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben eines Elements in
     * einem Tree realisiert.
     *
     * @param tree
     * @param edp
     * @throws ActionNotDefinedForClassException
     */
    public static final LGMAction getMoveUpAction(final JTree tree, final ElementDialogPanel edp) throws ActionNotDefinedForClassException {

        final ElementDialogPanel pane = edp;
        final JTree ltree = tree;
        final DefaultTreeModel lmodel = (DefaultTreeModel) tree.getModel();
        final LGMTreeNode lroot = (LGMTreeNode) lmodel.getRoot();
        final GraphDocument doc = edp.getGraphDocument();
        final ModelElement modelElement = edp.getModelElement();
        final ElementPropertyDialog dialog = edp.getDialog();

        if (pane instanceof ProzessStructurePanel) {
            return new LGMAction("", Tool3lgmConstants.getIcon("hoch2.gif")) {
                @Override
                public void execute(final EventObject eo) {
                    ProzessStructurePanel panel = (ProzessStructurePanel) pane;
                    // Aufaben haben Pfadlänge 2 (das nicht sichtbare root hat
                    // die 1)
                    TreePath selPath = ltree.getSelectionPath();
                    // wenn links eine Aufgabe selektiert ist
                    if (selPath != null && selPath.getPathCount() == 2) {
                        // Position der selektierten Aufgabe im Baum bzw. Prozess holen
                        int pos1 = lmodel.getIndexOfChild(lroot, selPath.getLastPathComponent());
                        // wenn nicht die erste sondern eine Aufgabe dahinter selektiert ist
                        if (pos1 > 0) {
                            // jetzt die Position der über der selektierten Aufgabe liegenden
                            // Aufgabe holen
                            // -> von dieser alle evtl. expandierten Unterknoten merken
                            // -> sie removen und unter der selektierten wieder einfügen
                            // -> alles was von ihr expandiert war, wieder expandieren
                            int pos2 = ltree.getRowForPath(selPath) - 1;
                            TreePath path = ltree.getPathForRow(pos2);
                            while (path.getPathCount() > 2) {
                                pos2--;
                                path = ltree.getPathForRow(pos2);
                            }

                            // wenn die selektierte Aufgabe expandierte Unterknoten hat (können max.
                            // 2 sein, nämlich
                            // "Interpretiert" und "Bearbeitet"), dann sind diese TreePathes jetzt
                            // in enum
                            Enumeration<TreePath> en = ltree.getExpandedDescendants(path);

                            // jetzt den Baum anpassen (DER WIRD IN DIESEM FALL IN buildLeftTree()
                            // NICHT VERÄNDERT)
                            // und weil hier noch die Expasionen anpasst werden (über enum), soll
                            // das auch hier bleiben!
                            LGMTreeNode node = (LGMTreeNode) lroot.getChildAt(pos1 - 1); // den
                            // oberen Knoten holen
                            lmodel.removeNodeFromParent(node); // ihn entfernen
                            lmodel.insertNodeInto(node, lroot, pos1); // ihn einen tiefer als vorher
                                                                      // einfügen, wenn die
                                                                      // selektierte Aufgabe
                                                                      // expandiert war
                            if (en != null) {
                                panel.expandFullPath(true); // muss sein wegen treeWillExpand,
                                                            // damits auch wirklich expandiert wird
                                ltree.expandRow(pos1 + 1); // den Knoten wieder expandieren
                                while (en.hasMoreElements()) {
                                    ltree.expandPath(en.nextElement()); // seine Unterknoten auch
                                                                        // expandieren
                                }
                                // das muss immer nch einem Expandieren zurückgesetzt werden (siehe
                                // treeWillExpand)
                                panel.willExpand(false);
                                panel.expandFullPath(false);
                            }
                            ltree.scrollPathToVisible(selPath);
                            // jetzt erst die Nummerierungen anpassen (auf keinen Fall vor dem
                            // Expandieren, weil sonst die Pfade nicht mehr stimmen)
                            node.setText("[" + (pos1 + 1) + "] " + node.getUserObject());
                            node = (LGMTreeNode) lroot.getChildAt(pos1 - 1);
                            node.setText("[" + pos1 + "] " + node.getUserObject());

                            // das switchen in den connections vom Prozess ausführen
                            doc.swapEdgePositions(modelElement, pos1, pos1 - 1, dialog.getTransactionID());
                        }
                    }
                    ltree.repaint();
                    return;
                }
            };
        }
        throw new ActionNotDefinedForClassException(edp.getClass().getName());
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben eines Elements in
     * einem Tree realisiert.
     *
     * @param tree
     * @param edp
     * @throws ActionNotDefinedForClassException
     */
    public static final LGMAction getMoveDownAction(final JTree tree, final ElementDialogPanel edp) throws ActionNotDefinedForClassException {

        final ElementDialogPanel pane = edp;
        final JTree ltree = tree;
        final DefaultTreeModel lmodel = (DefaultTreeModel) tree.getModel();
        final LGMTreeNode lroot = (LGMTreeNode) lmodel.getRoot();
        final GraphDocument doc = edp.getGraphDocument();
        final ModelElement modelElement = edp.getModelElement();
        final ElementPropertyDialog dialog = edp.getDialog();

        if (pane instanceof ProzessStructurePanel) {
            return new LGMAction("", Tool3lgmConstants.getIcon("runter2.gif")) {
                @Override
                public void execute(final EventObject eo) {
                    ProzessStructurePanel panel = (ProzessStructurePanel) pane;
                    // Aufaben haben Pfadlänge 2 (das nicht sichtbare root hat
                    // die 1)
                    TreePath selPath = ltree.getSelectionPath();
                    if (selPath != null && selPath.getPathCount() == 2) {
                        int pos1 = lmodel.getIndexOfChild(lroot, selPath.getLastPathComponent());
                        if (pos1 < lroot.getChildCount() - 1) {
                            int pos2 = ltree.getRowForPath(selPath) + 1;
                            TreePath path = ltree.getPathForRow(pos2);
                            while (path.getPathCount() > 2) {
                                pos2++;
                                path = ltree.getPathForRow(pos2);
                            }
                            Enumeration<TreePath> en = ltree.getExpandedDescendants(path);
                            LGMTreeNode node = (LGMTreeNode) lroot.getChildAt(pos1 + 1);
                            lmodel.removeNodeFromParent(node);
                            lmodel.insertNodeInto(node, lroot, pos1);

                            if (en != null) {
                                panel.expandFullPath(true);
                                ltree.expandRow(pos1 + 1);
                                while (en.hasMoreElements()) {
                                    ltree.expandPath(en.nextElement()); // seine Unterknoten auch
                                                                        // expandieren
                                }
                                panel.willExpand(false);
                                panel.expandFullPath(false);
                            }
                            ltree.scrollPathToVisible(selPath);
                            node.setText("[" + (pos1 + 1) + "] " + node.getUserObject());
                            node = (LGMTreeNode) lroot.getChildAt(pos1 + 1);
                            node.setText("[" + (pos1 + 2) + "] " + node.getUserObject());
                            doc.swapEdgePositions(modelElement, pos1, pos1 + 1, dialog.getTransactionID());
                        }
                    }
                    ltree.repaint();
                    return;
                }
            };
        }
        throw new ActionNotDefinedForClassException(edp.getClass().getName());
    }

}
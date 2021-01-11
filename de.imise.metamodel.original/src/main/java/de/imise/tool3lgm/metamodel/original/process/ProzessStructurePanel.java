/*
 * Created on 27.11.2003 To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.metamodel.original.process;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.imise.tool3lgm.graphtools.dialog.action.ActionNotDefinedForClassException;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.element.DialogActionCommands;
import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.PathConnectionLeafPanel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.metapaths.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.tree.ElementDialogPanelTree;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.StringTreeNode;
import de.imise.tool3lgm.metamodel.original.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PrzAufVerbindung;
import de.imise.tool3lgm.metamodel.original.node.Aufgabe;
import de.imise.tool3lgm.metamodel.original.node.Objekttyp;

/**
 * 24.10.2018: Dieses Panel funktioniert im Moment überhaupt nicht mehr richtig.
 * Die Aufgaben des Prozesses werden alphabetisch sortiert und die Umbenennung
 * mit den Nummern davor findet auch nicht mehr statt. Die Objekttypen werden
 * auch nicht mehr angehängt und das Verifizieren geht auch nicht mehr.
 *
 * @author AXS
 */
@SuppressWarnings({
        "unused", "serial"
})
public class ProzessStructurePanel extends PathConnectionLeafPanel implements TreeWillExpandListener {

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
    protected boolean selectionChanged = false;

    /**
     * werden gebraucht, um die Bäume zu aktualisieren, wenn sich die Anzahl der
     * Aufgaben, Objekttypen oder die Kanten zwischen diesen beiden ändert
     * während dieses Panel offen ist, NUR DANN muss aktualisiert werden, sonst
     * nicht
     */
    private final int oldObjectCounter = -1, objectCounter = 0;

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
    private final String errorTitle = "";

    /**
     * COMMENTME
     */
    private final String errorMessage = "";

    /**
     * COMMENTME
     */
    private List<ElementContainer> aufgabenContainer;

    private final LGMAction upAction;

    /**
     * COMMENTME
     */
    private final LGMAction downAction;

    private final Class<? extends Edge> doubleMeaningEdgeClass;

    /**
     * @param dialog
     * @param multipleConnectionEgdeClass
     * @param doubleMeaningEdgeClass
     */
    public ProzessStructurePanel(final ElementPropertyDialog dialog, final Class<? extends MultipleEdge> multipleConnectionEgdeClass, final Class<? extends Edge> doubleMeaningEdgeClass) {
        super(dialog, PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME, PanelLabelOption.LABEL_LAST_EDGE_CONNECTION_NAME, createSimpleMetaPath(dialog, multipleConnectionEgdeClass, doubleMeaningEdgeClass));
        this.doubleMeaningEdgeClass = doubleMeaningEdgeClass;

        // Panel für die Buttons zur Aenderung der Aufgabenreihenfolge anlegen
        JPanel upDownControl = new JPanel(new GridLayout(1, 2));

        /*
         * Start: Buttons & Actions erstellen, Actions setzen ...
         */
        JButton upButton = new JButton();
        JButton downButton = new JButton();

        upAction = getMoveUpAction(ltree);
        downAction = getMoveDownAction(ltree);

        upButton.setAction(upAction);
        downButton.setAction(downAction);
        /*
         * ... end: Buttons & Actions erstellen, Actions setzen
         */

        upDownControl.add(upButton);
        upDownControl.add(downButton);

        // Panel für die Buttons zur Aenderung der Aufgabenreihenfolge
        // hinzufügen
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.weighty = 0;
        //Windows-spezifisch
        //        constraints.ipadx = -60;
        // constraints.ipady = - 10;
        addUnderLeftTree(upDownControl, constraints);

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
        JPanel tmpPanel = new JPanel();
        tmpPanel.add(verificationCheck);

        tmpPanel.add(errorBut);
        errorBut.setVisible(verify);

        addSouth(tmpPanel, constraints, 2);

        update();

    }

    /**
     * @param dialog
     * @param multipleConnectionEgdeClass
     * @param doubleMeaningEdgeClass
     * @return
     */
    private static SequenceMetaPath createSimpleMetaPath(final ElementPropertyDialog dialog, final Class<? extends MultipleEdge> multipleConnectionEgdeClass, final Class<? extends Edge> doubleMeaningEdgeClass) {
        ModelElement me = dialog.getModelElement();
        Class<? extends ModelElement> elementClass = me.getClass();
        MetaModel metaModel = dialog.getMetaModel();
        ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
        ElementaryMetaPath metaPath = elementaryMetaPathHandler.getMetaPath(elementClass, multipleConnectionEgdeClass);
        return metaPath;
    }

    @Override
    protected int getTreesSelectionModel() {
        return TreeSelectionModel.SINGLE_TREE_SELECTION;
    }

    /**
     * Haengt an den uebergebenen LGMTreeNode neue LGMTreeNodes mit den
     * Objekttypen an, die von der Aufgabe (UserObject des uebergebenen
     * LGMTreeNode) bearbeitet und interpretiert werden.
     *
     * @param aufgabenContainerNode
     */
    private void appendObjectTypes(final ElementContainerTreeNode aufgabenContainerNode) {
        ModelElement me = ((NodeContainer) aufgabenContainerNode.getUserObject()).getElement();
        GraphDocument mainDoc = getMainDoc();
        List<ElementContainer> ots = me.getConnectedContainers(Objekttyp.class, mainDoc, AufObjVerbindung.class, FORWARD, ConnectionState.BACKWARD, false);
        if (!ots.isEmpty()) {
            String typeNodeName = elementsNameBuilder.getForwardMetaAssociationName(AufObjVerbindung.class, ConnectionState.BACKWARD, false, false);
            StringTreeNode tmpNode = new StringTreeNode(typeNodeName);
            tmpNode.setSelectable(false);
            addNodes(tmpNode, ots, false);
            aufgabenContainerNode.add(tmpNode);
        }
        ots = me.getConnectedContainers(Objekttyp.class, mainDoc, AufObjVerbindung.class, FORWARD, ConnectionState.FORWARD, false);
        if (!ots.isEmpty()) {
            String typeNodeName = elementsNameBuilder.getForwardMetaAssociationName(AufObjVerbindung.class, ConnectionState.FORWARD, false, false);
            StringTreeNode tmpNode = new StringTreeNode(typeNodeName);
            tmpNode.setSelectable(false);
            addNodes(tmpNode, ots, false);
            aufgabenContainerNode.add(tmpNode);
        }
    }

    //    /**
    //     * Baut im linken Baum nur die Elemente der letzten Edge des Pfades auf
    //     */
    //    @Override
    //    protected Collection<LGMTreeNode> buildLeftTree() {
    //        Collection<LGMTreeNode> leafNodes = super.buildLeftTree();
    //        //wenn dieses Panel mit einem Pfad der Länge 1 initialisiert wurde, dann gibt es hier nichts zu tun,
    //        //da es keine Zwischenelemente gibt, die nicht angezeigt werden sollen
    //        if (edgeClasses.length == 1) {
    //            return leafNodes;
    //        }
    //        if (nodeToUserObjectPath == null) {
    //            nodeToUserObjectPath = new HashMap<>();
    //        } else {
    //            nodeToUserObjectPath.clear();
    //        }
    //        if (!leafNodes.isEmpty()) {
    //            //vor dem Umhängen der Blätter an den root für jedes Blatt das echte Vorgängerelement auf dem Pfad merken
    //            for (LGMTreeNode leaf : leafNodes) {
    //                LGMTreeNode leafParent = (LGMTreeNode) leaf.getParent();
    //                ModelElement parentMe = getNodeModelElement(leafParent);
    //                nodeToUserObjectPath.put(leaf, parentMe);
    //                lroot.add(leaf);
    //            }
    //            //alle Elemente vom root abhängen
    //            lroot.removeAllChildren();
    //            //alle Blätter direkt an den root hängen
    //            for (LGMTreeNode leaf : leafNodes) {
    //                lroot.add(leaf);
    //            }
    //        }
    //        return leafNodes;
    //    }
    //
    //    /**
    //     * Baut den linken Baum auf.
    //     */
    //    @Override
    //    private void buildLeftTree() {
    //        // System.out.println("buildLeftTree");
    //        // Die nicht alphabetisch sortierten Aufgaben holen
    //        aufgabenContainer = prozess.getConnectedContainer(Aufgabe.class, doc, null, Doppelkante.ANY, false);
    //        // System.out.println(aufgabenContainer);
    //        // wenn sich die Anzahl der Aufgaben, Objekttypen oder die der
    //        // Verbindungen zw. Auf und OTs geändert hat
    //        // muss aktualisiert werden (und nur dann). Wenn der rechte Baum
    //        // angezeigt wird, dann wurde objectCounter
    //        // dort schon aktualisiert
    //        if (!spRechts.isVisible()) {
    //            objectCounter = doc.getLayer(ModelConstants.DOMAIN_LAYER).countType(Aufgabe.class);
    //            objectCounter += doc.getLayer(ModelConstants.DOMAIN_LAYER).countType(Objekttyp.class);
    //            objectCounter += doc.getLayer(ModelConstants.DOMAIN_LAYER).countType(AufObjVerbindung.class);
    //        }
    //        // wenn auf der FE eine Edge (Prozesskanten zählen nicht mit)
    //        // hinzugekommen ist oder ein Objekttyp gelöscht
    //        // wurde oder der Baum noch leer ist -> Baum einfach komplett neu
    //        // aufbauen
    //        if (objectCounter != oldObjectCounter || lroot.getChildCount() == 0) {
    //            // buildLeftTree wird auf jeden Fall und immer nach buildRightTree
    //            // aufgerufen
    //            // -> buildLeftTree aktualisiert oldKantenCount und oldOTCount.
    //            oldObjectCounter = objectCounter;
    //            lroot.removeAllChildren();
    //            for (int m = 0; m < aufgabenContainer.size(); m++) {
    //                LGMTreeNode node = new LGMTreeNode(aufgabenContainer.get(m), "[" + (m + 1) + "] " + aufgabenContainer.get(m), false);
    //                lroot.add(node);
    //                appendObjectTypes(node);
    //            }
    //            lmodel.reload();
    //            if (verify) {
    //                check3LGMBuisnessProcess();
    //            }
    //            return;
    //        }
    //        // wenn dem Prozess eine neue Aufgabe hinzugefügt wurde muss der neue
    //        // Node eingefügt werden
    //        // (dies hat im Gegensatz zum kompletten Neuaufbau den Vorteil, dass
    //        // expandierte Node expandiert bleiben und
    //        // nur deswegen hier der ganze Aufwand)
    //        else if (lroot.getChildCount() < aufgabenContainer.size()) {
    //            // nach dem Node, welcher neu eingefügt wird, muss die
    //            // Nummerierung aller folgenden Node
    //            // um 1 erhöht werden. Das passiert erst ab da, wenn
    //            // actualizeNodes==true ist.
    //            boolean actualizeNodes = false;
    //            for (int m = 0; m < aufgabenContainer.size(); m++) {
    //                if (m >= lroot.getChildCount() || ((LGMTreeNode) lroot.getChildAt(m)).getUserObject() != aufgabenContainer.get(m)) {
    //                    LGMTreeNode node = new LGMTreeNode(aufgabenContainer.get(m), "[" + (m + 1) + "] " + aufgabenContainer.get(m), false);
    //                    appendObjectTypes(node);
    //                    lmodel.insertNodeInto(node, lroot, m);
    //                    actualizeNodes = true;
    //                } else if (actualizeNodes) {
    //                    ((LGMTreeNode) lroot.getChildAt(m)).setText("[" + (m + 1) + "] " + aufgabenContainer.get(m));
    //                }
    //            }
    //        }
    //        // es wurde mind. eine Aufgabe auf der FE gelöscht, die auch im Prozess
    //        // vorkam
    //        else if (lroot.getChildCount() > aufgabenContainer.size()) {
    //            boolean actualizeNodes = false;
    //            // für alle Aufgaben des Prozesses in sub
    //            for (int m = 0; m < aufgabenContainer.size(); m++) {
    //                // wenn im Baum eine andere Aufgabe steht als an der selben
    //                // Position in sub
    //                if (((LGMTreeNode) lroot.getChildAt(m)).getUserObject() != aufgabenContainer.get(m)) {
    //                    // lösche diesen Node im Baum
    //                    lmodel.removeNodeFromParent((LGMTreeNode) lroot.getChildAt(m));
    //                    // dieselbe Position muss nochmal getestet werden
    //                    m--;
    //                    // ab jetzt muss für alle folgenden Node, die nicht auch
    //                    // gelöscht werden, die Nummerierung angepasst werden
    //                    actualizeNodes = true;
    //                    // wenn die Aufgabe erhalten bleibt und hinter einer Aufgabe
    //                    // stand, die gelöscht wurde
    //                } else if (actualizeNodes) {
    //                    // schreibe die Nummerierung neu
    //                    ((LGMTreeNode) lroot.getChildAt(m)).setText("[" + (m + 1) + "] " + aufgabenContainer.get(m));
    //                }
    //            }
    //            // am Ende alle Node löschen, die mehr im Baum sind, als in der
    //            // Aufgabenliste sub
    //            while (lroot.getChildCount() > aufgabenContainer.size()) {
    //                lmodel.removeNodeFromParent((LGMTreeNode) lroot.getChildAt(lroot.getChildCount() - 1));
    //            }
    //        }
    //        if (verify) {
    //            check3LGMBuisnessProcess();
    //        }
    //        // stellt sicher, dass Namensänderungen von Aufgaben und Objekttypen
    //        // angezeigt werden
    //        ltree.repaint();
    //    }

    //    /**
    //     * Prueft, wenn 3LGM-Geschaeftsprozesse angelegt werden sollen, welche Aufgaben im rechten Baum
    //     * selektierbar sein dürfen. setSelectable(false) hat lediglich Auswirkungen auf die
    //     * Darstellung.
    //     */
    //    public void checkRightPossibleTasks() {
    //        // System.out.println("checkRightPossibleTasks");
    //        if (!spRechts.isVisible()) {
    //            return;
    //        }
    //        // wenn links gar keine Aufgabe steht, dann müssen rechts alle Aufgaben
    //        // setSelectable(true) gesetzt werden
    //        int lrootChildCount = lroot.getChildCount();
    //        TreePath selPath = ltree.getSelectionPath();
    //        // rechts alle Aufgaben verfügbar anzeigen, wenn links keine Aufgabe
    //        // steht, nicht verifiziert werden soll oder die erste Aufgabe
    //        // selektiert ist
    //        if (lrootChildCount == 0 || !verificationCheck.isSelected() || selPath != null && ltree.getSelectionRows() != null && ltree.getSelectionRows()[0] == 1) {
    //            for (int i = 1; i < rtree.getRowCount(); i++) {
    //                TreePath path = rtree.getPathForRow(i);
    //                if (path.getPathCount() == 2) {
    //                    ((LGMTreeNode) path.getLastPathComponent()).setSelectable(true);
    //                }
    //            }
    //            // links ist mind. eine Aufgabe vorhanden, es soll verifiziert
    //            // werden und es ist nicht die erste Aufgabe selektiert
    //        } else {
    //            int selRow = -1;
    //            // wenn links gar nichts selelktiert ist
    //            if (selPath == null) {
    //                selRow = aufgabenContainer.size();
    //                // ansonsten, wenn der links selektierte Pfad eine Aufgabe ist
    //            } else if (selPath.getPathCount() == 2) {
    //                // selRow auf den Index der selektierten Aufgabe innerhalb des
    //                // Prozesses setzen
    //                selRow = lroot.getIndex((LGMTreeNode) selPath.getLastPathComponent());
    //            }
    //            // wenn links etwas selektiert war, was keine Aufgabe ist
    //            if (selRow == -1) {
    //                // rechts einfach alles setSelectable(false) setzen
    //                for (int i = 1; i < rtree.getRowCount(); i++) {
    //                    ((LGMTreeNode) rtree.getPathForRow(i).getLastPathComponent()).setSelectable(false);
    //                    // wenn links gar nichts oder eine Aufgabe selektiert war
    //                }
    //            } else {
    //                // hole die Aufgaben des Prozesses
    //                List<ModelElement> aufgaben = prozess.getConnectedElements(Aufgabe.class);
    //                // für alle Zeilen des rechten Baumes
    //                for (int i = 1; i < rtree.getRowCount(); i++) {
    //                    selPath = rtree.getPathForRow(i);
    //                    // wenn der momentane Pfad eine Aufgabe ist
    //                    if (selPath.getPathCount() == 2) {
    //                        LGMTreeNode node = (LGMTreeNode) selPath.getLastPathComponent();
    //                        // wenn von mind. einem Node VOR dem selektierten bzw.
    //                        // (wenn nichts selektiert ist) dem
    //                        // letzten Node mit der i-ten rechten Aufgabe einen
    //                        // LGM-Buisness-Process-Step bildet, dann
    //                        // soll die i-te rechte Aufgabe selektierbar sein
    //                        List<LGMProzessStep> al = prozess.getProcessStepsForAufgabe(aufgaben, ((NodeContainer) node.getUserObject()).getElement(), selRow, true);
    //                        node.setSelectable(al.size() > 0);
    //                    }
    //                }
    //            }
    //        }
    //        rtree.repaint();
    //    }

    //    /**
    //     * Prüft den Prozess, ob er ein 3LGM-Geschäftsprozess ist. Der erste spezifische Fehler, der
    //     * Auftritt wird sich in errorMessage gemerkt. Alle fehlerhaften Positionen werden im linken
    //     * Baum mit einem Error-Icon markiert.
    //     */
    //    public void check3LGMBuisnessProcess() {
    //        // System.out.println("check3LGMBuisnessProcess");
    //
    //        StringBuilder errorBuffer = new StringBuilder();
    //        errorMessage = "";
    //        // Das deaktivieren von Aufgabe auf der rechten Seite ist rausgenommen,
    //        // da beim Drag und Drop nicht von vornerein fest steht,
    //        // an welcher Position die Aufgabe links hinzugefügt werden soll
    //        // checkRightPossibleTasks();
    //
    //        // aufgabenContainer wird in buildLeftTree() aktualisiert
    //        // aufgabenContainer =
    //        // modelElement.getConnectedKnot(ModelConstants.AUFGABE
    //        // ,doc,-1,false,true, false);
    //        int size = aufgabenContainer.size();
    //        // der Prozess ist leer
    //        if (size == 0) {
    //            errorTitle = getResString("process_error_no_function");
    //            errorBuffer.append(getResString("process_error_no_function_1"));
    //        }
    //        // der Prozess hat nur eine Aufgabe
    //        else if (size == 1) {
    //            errorTitle = getResString("process_error_single_function");
    //            errorBuffer.append(getResString("process_error_single_function_1"));
    //            ((LGMTreeNode) lroot.getChildAt(0)).setIconState(LGMTreeNode.SHOW_ERROR_ICON);
    //            // der Prozess hat mind. 2 Aufgaben
    //        } else {
    //            // Reseten aller Icons auf normal
    //            for (int i = 0; i < aufgabenContainer.size(); i++) {
    //                ((LGMTreeNode) lroot.getChildAt(i)).setIconState(LGMTreeNode.SHOW_NORMAL_ICON);
    //            }
    //            // hole die Aufgaben des Prozesses
    //            List<ModelElement> aufgaben = prozess.getConnectedElements(Aufgabe.class);
    //            // die Icons auf fehlerhaft setzen, deren Aufgabe kein
    //            // Geschäftsprozessschritt von einer der vorherigen Aufgaben ist
    //            for (int i = 1; i < size; i++) {
    //                if (prozess.getProcessStepsForAufgabe(aufgaben, i, true).size() == 0) {
    //                    if (errorBuffer.length() == 0) {
    //                        errorTitle = getResString("process_error_no_process_step");
    //                        errorBuffer.append(getResString("process_error_no_process_step_1"));
    //                        errorBuffer.append(((NodeContainer) aufgabenContainer.get(i)).getElement());
    //                        errorBuffer.append(getResString("process_error_no_process_step_2"));
    //                        errorBuffer.append(i + 1);
    //                        errorBuffer.append(getResString("process_error_no_process_step_3"));
    //                    }
    //                    ((LGMTreeNode) lroot.getChildAt(i)).setIconState(LGMTreeNode.SHOW_ERROR_ICON);
    //                }
    //            }
    //        }
    //        if (errorBuffer.length() > 0) {
    //            errorMessage = errorBuffer.toString();
    //            errorBut.setEnabled(true);
    //        } else {
    //            errorBut.setEnabled(false);
    //        }
    //    }

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
    //                    //den selektierten Node ermitteln
    //                    LGMTreeNode node = (LGMTreeNode) selPath.getLastPathComponent();
    //                    //wenn es sich bei dem rechts selektierten Node um eine Aufgabe handelt
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
    //                                    doc.exec("MODEL_ACTION_LINK " + "null" + " " + "null" + " " + modelElement.getID() + " " + knot.getID() + " " + index, dialog.getTransactionID());
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
    //                                doc.exec("MODEL_ACTION_LINK " + "null" + " " + "null" + " " + modelElement.getID() + " " + knot.getID() + " " + modelElement.countConnections(), dialog.getTransactionID());
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
    //                        doc.exec("MODEL_ACTION_UNLINK " + modelElement.getID() + " " + ((ElementContainer) knot).getID() + " " + index, dialog.getTransactionID());
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
    //                    //den oberen Node holen
    //                    lmodel.removeNodeFromParent(node);
    //                    //ihn entfernen
    //                    lmodel.insertNodeInto(node, lroot, pos1);
    //                    //ihn einen tiefer als vorher einfügen
    //                    //wenn die selektierte Aufgabe expandiert war
    //                    if (en != null) {
    //                        expandFullPath = true;
    //                        //muss sein wegen treeWillExpand, damits auch wirklich expandiert wird
    //                        ltree.expandRow(pos1 + 1);
    //                        //den Node wieder expandieren
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
    //                    doc.swapEdgePositions(modelElement, pos1, pos1 - 1, dialog.getTransactionID())
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
    //                    doc.swapEdgePositions(modelElement, pos1, pos1 + 1, dialog.getTransactionID())
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
    //                doc.exec("MODEL_ACTION_UNLINK " + prozess.getID() + " " + knotCont.getID(), dialog.getTransactionID());
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

    //    public void valueChanged(final TreeSelectionEvent e) {
    //        // System.out.println("valueChanged");
    //        selectionChanged = true;
    //        if (e == null) {
    //            return;
    //        }
    //        lastSelEvent = e;
    //
    //        // wenn sich links die Selektion geändert hat, die rechte Seite sichtbar
    //        // ist und verifiziert werden soll
    //        if (verificationCheck.isSelected() && labelRechts.isShowing() && ((JTree) e.getSource()).getName().equals("lefttree")) {
    //            checkRightPossibleTasks();
    //        }
    //
    //        // man kann immer nur einen einzelnen TreeNode selektieren->
    //        // wenn sich die Selektion ändert, einfach erst highlight leer machen
    //        // und dann das neu selektierte hinzufügen (wenn der TreeNode ein
    //        // ModelElement
    //        // und nicht nur einen String repräsentiert)
    //        removeHighLights();
    //        LGMTreeNode node = null;
    //        if (e.getNewLeadSelectionPath() != null) {
    //            node = (LGMTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
    //            // root ist zwar in beiden Bäumen durch Verschieben des
    //            // Darstellungsoffsets nicht sichtbar, kann
    //            // aber per Tastaur erreicht werden ->wenn root selektiert wird,
    //            // einfach die erste Aufgabe selektieren
    //            if (node.isRoot()) {
    //                ((JTree) e.getSource()).setSelectionRow(1);
    //                return;
    //            }
    //            // die Nodes, die die Strings "Bearbeitet" und "Interpretiert"
    //            // enthalten, werden nicht zu highlight geaddet
    //            if (!(node.getUserObject() instanceof String)) {
    //                NodeContainer knot = (NodeContainer) node.getUserObject();
    //                knot.setHighLight(true);
    //                addHighlight(knot);
    //            }
    //        }
    //        NodeContainer kc = (NodeContainer) prozess.getContainer(doc);
    //        doc.deselectAll(false);
    //        doc.addToSelection(kc, dialog.getTransactionID());
    //        /*
    //         * for (int pos = 0; pos < doc.selectedContainer.size(); pos++) { ElementContainer ec =
    //         * (ElementContainer) doc.selectedContainer.get(pos); if (doc.selectedContainer.get(pos) !=
    //         * kc) doc.deselect(ec, dialog.getTransactionID()); }
    //         */
    //        if (node != null && !(node.getUserObject() instanceof String)) {
    //            doc.addToSelection((NodeContainer) node.getUserObject(), dialog.getTransactionID());
    //        }
    //    }

    //    /**
    //     * @param e
    //     */
    //    public final void mousePressed(final MouseEvent e) {
    //        // System.out.println("pressed");
    //        JTree tree = (JTree) e.getSource();
    //        int selRow = tree.getLeadSelectionRow();
    //        int clickedRow = tree.getRowForLocation(e.getX(), e.getY());
    //        // bei Links-Klick muss hier nichts gemacht werden, es geht also nur um
    //        // das PopupMenu (Links-Klick in mousePressed)
    //        if (!Tool3lgmConstants.isPopupTrigger(e)) {
    //            return;
    //        }
    //        // nichts selektiert
    //        if (selRow == -1) {
    //            // es ist nichts selektiert und es wurde mit rechts neben die
    //            // Einträge geklickt -> return
    //            if (clickedRow == -1) {
    //                return;
    //            }
    //            // es wurde mit rechts auf einen (logischerweise nicht selektierten)
    //            // Eintrag geklickt
    //            // wenn die angeklickte Zeile eine Aufgabe oder ein Objekttyp ist
    //            Object o = ((LGMTreeNode) tree.getPathForRow(clickedRow).getLastPathComponent()).getUserObject();
    //            if (!(o instanceof String)) {
    //                // den angeklickten selektieren
    //                setSelectionRow(tree, clickedRow);
    //                Tool3lgm.getContextGenerator().getDialogSelectionContextMenu((ElementContainer) o).show(e.getComponent(), e.getX() + 3, e.getY() + 3);
    //            }
    //        }
    //        // es ist etwas selektiert
    //        else {
    //            // wenn mit rechts auf einen Eintrag und nicht neben die Einträge
    //            // geklickt wurde
    //            if (clickedRow != -1) {
    //                // wenn keine Aufgabe oder ein Objekttyp angeklickt wurde ->
    //                // return
    //                if (((LGMTreeNode) tree.getPathForRow(clickedRow).getLastPathComponent()).getUserObject() instanceof String) {
    //                    return;
    //                } else if (clickedRow != selRow) {
    //                    setSelectionRow(tree, clickedRow);
    //                }
    //            }
    //            // das PopupMenü für den jetzt selektierten Eintrag anzeigen
    //            Object o = ((LGMTreeNode) tree.getPathForRow(clickedRow).getLastPathComponent()).getUserObject();
    //            Tool3lgm.getContextGenerator().getDialogSelectionContextMenu((ElementContainer) o).show(e.getComponent(), e.getX() + 3, e.getY() + 3);
    //        }
    //    }
    //
    //    /**
    //     * @param e
    //     */
    //    public final void mouseClicked(final MouseEvent e) {
    //        // System.out.println("MouseClicked");
    //        // rechte Maustaste wird in mousePressed behandelt
    //        if (Tool3lgmConstants.isPopupTrigger(e)) {
    //            return;
    //        }
    //        // wenn in einem der Bäume 1x geklickt wurde oder durch Doppelklicken
    //        // auf einen Node mit Pfadlänge>2 (also
    //        // alles ausser der Wurzel und den Aufgaben)
    //        // (pathToExpandOrCollapse ist nur != null, wenn vorher treeWillExpand
    //        // oder treeWillCollapse ausgelöst wurde)
    //        JTree tree = (JTree) e.getSource();
    //        if (e.getClickCount() == 1 || pathToExpandOrCollapse != null && pathToExpandOrCollapse.getPathCount() > 2) {
    //            // wenn vorher treeWillExpand ausgelöst wurde (dort wurde das erste
    //            // eigentliche Expandieren verhindert, da
    //            // Aufgaben beim Doppelklicken in den jeweils anderen Baum
    //            // verschoben werden sollen)
    //            if (willExpand) {
    //                // expandFullPath = true bewirkt, dass jetzt beim Auslösen von
    //                // treeWillExpand die Expansion auch wirklich
    //                // passiert
    //                expandFullPath = true;
    //                int row = tree.getRowForPath(pathToExpandOrCollapse);
    //                // Expandieren des obersten Node des zu expandierenden Knotens
    //                tree.expandRow(row);
    //                for (int n = row + 1; n < tree.getRowCount(); n++) {
    //                    // alle Node mit Pfadlänge größer 2 unter diesem Node
    //                    // expandieren (das >2 bewirkt, dass nur bis
    //                    // zur nächsten Aufgabe expandiert wird)
    //                    if (tree.getPathForRow(n).getPathCount() > 2) {
    //                        tree.expandRow(n);
    //                    } else {
    //                        break;
    //                    }
    //                }
    //                // willExpand und expandFullPath zurücksetzen
    //                willExpand = false;
    //                expandFullPath = false;
    //                pathToExpandOrCollapse = null;
    //            }
    //            // wenn vorher treeWillCollapse ausgelöst wurde (dort wurde das
    //            // eigentliche Collapsen verhindert, da Aufgaben
    //            // beim Doppelklicken in den jeweils anderen Baum verschoben werden
    //            // sollen)
    //            else if (willCollapse) {
    //                tree.collapsePath(pathToExpandOrCollapse);
    //                pathToExpandOrCollapse = null;
    //            }
    //            // das hier tritt ein, wenn man 1x irgendwo hingeklickt hat und kein
    //            // Expandieren oder Collapsen ausgelöst wurde
    //            // -> eine evtl. vorhandene Selektion wird aufgehoben (wichtig, weil
    //            // man im linken Baum Aufgaben nur hinten
    //            // anhängen kann, wenn nichts selektiert ist)
    //            else {
    //                int selRow = tree.getLeadSelectionRow();
    //                if (selRow != -1) {
    //                    int row = tree.getRowForLocation(e.getX(), e.getY());
    //                    if (selRow != -1 && (row == -1 || row == selRow && !selectionChanged)) {
    //                        setSelectionRow(tree, -1);
    //                    } else {
    //                        selectionChanged = false;
    //                    }
    //                }
    //            }
    //            // wenn mind. 2x geklickt wurde und kein Expandieren oder Collapsen
    //            // ausgelöst wurde, dann wurden auf jeden
    //            // Fall Aufgaben doppelt angeklickt, welche entweder von links nach
    //            // rechts oder umgekehrt verschoben werden
    //        } else {
    //            String sourceName = ((JTree) e.getSource()).getName();
    //            if (sourceName.equals("righttree")) {
    //                addAction.execute(e);
    //            } else if (sourceName.equals("lefttree")) {
    //                removeAction.execute(e);
    //            }
    //            willExpand = false;
    //            willCollapse = false;
    //            pathToExpandOrCollapse = null;
    //        }
    //    }

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
     * Selekiert in destinationTree die Zeile row. Wird row=-1 uebergeben, wird
     * eine evtl. Selekion gelöscht.
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

    //    /**
    //     * @return
    //     */
    //    @Override
    //    protected DragNDropActionChain[] collectDragNDropActionChains() {
    //        DragNDropActionChain dndAC1 = DragNDropInitializer.createNewDragNDropActionChain(rtree, ltree, addAction);
    //        DragNDropActionChain dndAC2 = DragNDropInitializer.createNewDragNDropActionChain(ltree, rtree, removeAction);
    //
    //        DragNDropActionChain dndAC3 = DragNDropInitializer.createNewDragNDropActionChain(new LGMTree[] {
    //                ltree,
    //                rtree,
    //                ltree
    //        }, new LGMAction[] {
    //                removeAction,
    //                addAction
    //        });
    //
    //        return new DragNDropActionChain[] {
    //                dndAC1,
    //                dndAC2,
    //                dndAC3
    //        };
    //
    //    }
    //
    //    /**
    //     * @return
    //     */
    //    @Override
    //    public LGMTree[] getAllDragNDropTrees() {
    //        return new LGMTree[] {
    //                rtree,
    //                ltree
    //        };
    //    }

    // --- kleine Hilfsmethoden --- Ende ---

    /**
     * @param panel
     * @param tree
     * @return
     * @throws ActionNotDefinedForClassException
     */
    private static final LGMAction getVerfikationAction(final ElementDialogPanel panel, final ElementDialogPanelTree tree) throws ActionNotDefinedForClassException {
        final LGMTreeNode<?> lroot = tree.getRoot();
        if (panel instanceof ProzessStructurePanel) {
            GraphDocument mainDoc = panel.getMainDoc();
            return new LGMAction(mainDoc.getResString("PROCESS_PANEL_VERIFY")) {

                @Override
                public void execute(final EventObject e) {
                    ProzessStructurePanel processPanel = (ProzessStructurePanel) panel;
                    boolean verify = processPanel.getVerificationCheckBox().isSelected();
                    ProzessStructurePanel.verify(verify);
                    processPanel.getErrorButton().setVisible(verify);
                    //                    if (verify) {
                    //                        panel.check3LGMBuisnessProcess();
                    //                    } else {
                    //                        panel.checkRightPossibleTasks();
                    //                        for (int i = 0; i < lroot.getChildCount(); i++) {
                    //                            ((LGMTreeNode) lroot.getChildAt(i)).setIconState(LGMTreeNode.SHOW_NORMAL_ICON);
                    //                        }
                    //                    }
                    return;
                }
            };
        }
        throw new ActionNotDefinedForClassException(panel.getClass().getName());
    }

    /**
     * @param edp
     * @return
     * @throws ActionNotDefinedForClassException
     */
    private static final LGMAction getFehlerAction(final ElementDialogPanel panel) throws ActionNotDefinedForClassException {
        if (panel instanceof ProzessStructurePanel) {
            GraphDocument doc = panel.getMainDoc();
            return new LGMAction(doc.getResString("PROCESS_PANEL_ERROR")) {
                @Override
                public void execute(final EventObject e) {
                    ProzessStructurePanel processPanel = (ProzessStructurePanel) panel;
                    JOptionPane.showMessageDialog(processPanel, processPanel.getErrorMessage(), processPanel.getErrorTitel(), JOptionPane.ERROR_MESSAGE);
                }
            };
        }
        throw new ActionNotDefinedForClassException(panel.getClass().getName());
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben
     * von Elementen aus dem <code>srcTree</code> in den <code>targetTree</code>
     * realisiert. Diese <code>LGMAction</code> sollte an die "addButtons" der
     * Panels angefügt werden.
     *
     * @param srcTree
     * @param targetTree
     */
    private final LGMAction getAddElementAction(final ElementDialogPanelTree srcTree, final ElementDialogPanelTree targetTree) {

        ModelElement modelElement = getModelElement();
        GDCollection gdcoll = modelElement.getCollection();

        return new LGMAction(DialogActionCommands.ACTION_DIALOG_CONNECT_ELEMENT) {

            @Override
            public void execute(final EventObject eo) {
                LGMActionLibrary.getDragNDropLocateElementAsTargetAction(targetTree).execute(eo);
                LGMTreeNode<?> lroot = targetTree.getRoot();

                synchronized (targetTree.getTreeLock()) {
                    TreePath selPath = srcTree.getSelectionPath();
                    // wenn rechts etwas selektiert war
                    if (selPath != null) {
                        // den selektierten Node ermitteln
                        LGMTreeNode<?> treeNode = (LGMTreeNode<?>) selPath.getLastPathComponent();
                        // wenn es sich bei dem rechts selektierten Node
                        // um eine Aufgabe handelt
                        if (treeNode.getUserObject() instanceof NodeContainer) {
                            // seinen Container holen (diese muessen links
                            // im Baum geaddet werden)
                            NodeContainer nc = (NodeContainer) treeNode.getUserObject();
                            if (nc.getElement() instanceof Aufgabe) {
                                // wenn links was selektiert ist, dann muss
                                // die naechste Aufgabe ueber bzw. vor der
                                // selektierten eingefuegt werden
                                selPath = targetTree.getSelectionPath();
                                boolean nothingSelected = true;
                                // wenn links etwas selektiert war
                                if (selPath != null) {
                                    nothingSelected = false;
                                    treeNode = (LGMTreeNode<?>) selPath.getLastPathComponent();
                                    // prüfen, ob eine Aufgabe selektiert ist
                                    if (treeNode.getUserObject() instanceof NodeContainer && ((NodeContainer) treeNode.getUserObject()).getElement() instanceof Aufgabe) {
                                        int selRow = targetTree.getRowForPath(selPath);
                                        TreeModel lmodel = targetTree.getModel();
                                        int index = lmodel.getIndexOfChild(lroot, treeNode);
                                        int pid = getTransactionID();
                                        gdcoll.link(PrzAufVerbindung.class, modelElement, nc.getElement(), index, GDCommands.INVALID_EDGE_INDEX, pid);
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
                                    int pid = getTransactionID();
                                    gdcoll.link(PrzAufVerbindung.class, modelElement, nc.getElement(), modelElement.getEdgesCount(), GDCommands.INVALID_EDGE_INDEX, pid);
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
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben
     * von Elementen aus dem <code>srcTree</code> in den <code>targetTree</code>
     * realisiert. Diese <code>LGMAction</code> sollte an die "removeButtons"
     * der Panels angefügt werden.
     *
     * @param srcTree
     * @param targetTree
     */
    private final LGMAction getDisconnectAction(final ElementDialogPanelTree srcTree, final ElementDialogPanelTree targetTree) {

        final ModelElement modelElement = getModelElement();
        final GDCollection gdcoll = getCollection();

        return new LGMAction(DialogActionCommands.ACTION_DIALOG_DISCONNECT_ELEMENT) {

            @Override
            public void execute(final EventObject e) {
                LGMActionLibrary.getDragNDropLocateElementAsTargetAction(targetTree).execute(e);
                LGMTreeNode<?> lroot = srcTree.getRoot();

                synchronized (targetTree.getTreeLock()) {
                    TreePath selPath = srcTree.getSelectionPath();
                    // wenn links etwas selektiert war
                    if (selPath != null) {
                        LGMTreeNode<?> node = (LGMTreeNode<?>) selPath.getLastPathComponent();
                        // prüfen, ob eine Aufgabe selektiert ist
                        Object knot = node.getUserObject(); // auf keinen
                        // Falls hier gleich auf ElementContainer casten, weil es auch ein String sein kann !!!
                        if (!(knot instanceof String)) {
                            ModelElement otherMe = ((ElementContainer) knot).getElement();
                            if (otherMe instanceof Aufgabe) {
                                // es wird eine neue ProzessKante angelegt, aber
                                // der rechte Baum braucht nicht aktualisiert
                                // werden
                                TreeModel lmodel = srcTree.getModel();
                                int index = lmodel.getIndexOfChild(lroot, node);
                                int pid = getTransactionID();
                                gdcoll.unlink(modelElement, otherMe, PrzAufVerbindung.class, index, pid);
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
                            if (((LGMTreeNode<?>) targetTree.getPathForRow(i).getLastPathComponent()).getUserObject() == knot) {
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
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben
     * eines Elements in einem Tree realisiert.
     *
     * @param tree
     * @param edp
     * @throws ActionNotDefinedForClassException
     */
    private final LGMAction getMoveUpAction(final JTree tree) {

        final ProzessStructurePanel panel = this;
        final GraphDocument doc = getMainDoc();
        final ModelElement modelElement = getModelElement();

        return new LGMAction(DialogActionCommands.ACTION_DIALOG_MOVE_CONNECTION_STEP_UP) {

            @Override
            public void execute(final EventObject eo) {
                // Aufaben haben Pfadlänge 2 (das nicht sichtbare root hat die 1)
                TreePath selPath = ltree.getSelectionPath();
                // wenn links eine Aufgabe selektiert ist
                if (selPath != null && selPath.getPathCount() == 2) {
                    LGMTreeNode<?> lroot = ltree.getRoot();
                    // Position der selektierten Aufgabe im Baum bzw. Prozess holen
                    DefaultTreeModel lmodel = (DefaultTreeModel) ltree.getModel();
                    int pos1 = lmodel.getIndexOfChild(lroot, selPath.getLastPathComponent());
                    // wenn nicht die erste sondern eine Aufgabe dahinter selektiert ist
                    if (pos1 > 0) {
                        // jetzt die Position der über der selektierten Aufgabe liegenden Aufgabe holen
                        // -> von dieser alle evtl. expandierten Unterknoten merken
                        // -> sie removen und unter der selektierten wieder einfügen
                        // -> alles was von ihr expandiert war, wieder expandieren
                        int pos2 = ltree.getRowForPath(selPath) - 1;
                        TreePath path = ltree.getPathForRow(pos2);
                        while (path.getPathCount() > 2) {
                            pos2--;
                            path = ltree.getPathForRow(pos2);
                        }

                        // wenn die selektierte Aufgabe expandierte Unterknoten hat (können max. 2 sein, nämlich
                        // "Interpretiert" und "Bearbeitet"), dann sind diese TreePathes jetzt in enum
                        Enumeration<TreePath> en = ltree.getExpandedDescendants(path);

                        // jetzt den Baum anpassen (DER WIRD IN DIESEM FALL IN buildLeftTree() NICHT VERÄNDERT)
                        // und weil hier noch die Expasionen anpasst werden (über enum), soll das auch hier bleiben!
                        LGMTreeNode<?> node = (LGMTreeNode<?>) lroot.getChildAt(pos1 - 1); // den
                        // oberen Node holen
                        lmodel.removeNodeFromParent(node); // ihn entfernen
                        lmodel.insertNodeInto(node, lroot, pos1); // ihn einen tiefer als vorher einfügen, wenn die selektierte Aufgabe expandiert war
                        if (en != null) {
                            panel.expandFullPath(true); // muss sein wegen treeWillExpand, damits auch wirklich expandiert wird
                            ltree.expandRow(pos1 + 1); // den Node wieder expandieren
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
                        node = (LGMTreeNode<?>) lroot.getChildAt(pos1 - 1);
                        node.setText("[" + pos1 + "] " + node.getUserObject());

                        // das switchen in den connections vom Prozess ausführen
                        int pid = getTransactionID();
                        doc.swapEdgePositions(modelElement, pos1, pos1 - 1, pid);
                    }
                }
                ltree.repaint();
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben
     * eines Elements in einem Tree realisiert.
     *
     * @param tree
     * @throws ActionNotDefinedForClassException
     */
    public final LGMAction getMoveDownAction(final ElementDialogPanelTree tree) {

        final ProzessStructurePanel panel = this;
        final LGMTreeNode<?> lroot = tree.getRoot();
        final GraphDocument doc = getMainDoc();
        final ModelElement modelElement = getModelElement();

        return new LGMAction(DialogActionCommands.ACTION_DIALOG_MOVE_CONNECTION_STEP_DOWN) {

            @Override
            public void execute(final EventObject eo) {
                // Aufaben haben Pfadlänge 2 (das nicht sichtbare root hat
                // die 1)
                TreePath selPath = tree.getSelectionPath();
                if (selPath != null && selPath.getPathCount() == 2) {
                    final DefaultTreeModel lmodel = (DefaultTreeModel) tree.getModel();
                    int pos1 = lmodel.getIndexOfChild(lroot, selPath.getLastPathComponent());
                    if (pos1 < lroot.getChildCount() - 1) {
                        int pos2 = tree.getRowForPath(selPath) + 1;
                        TreePath path = tree.getPathForRow(pos2);
                        while (path.getPathCount() > 2) {
                            pos2++;
                            path = tree.getPathForRow(pos2);
                        }
                        Enumeration<TreePath> en = tree.getExpandedDescendants(path);
                        LGMTreeNode<?> node = (LGMTreeNode<?>) lroot.getChildAt(pos1 + 1);
                        lmodel.removeNodeFromParent(node);
                        lmodel.insertNodeInto(node, lroot, pos1);

                        if (en != null) {
                            panel.expandFullPath(true);
                            tree.expandRow(pos1 + 1);
                            while (en.hasMoreElements()) {
                                tree.expandPath(en.nextElement()); // seine Unterknoten auch
                                                                   // expandieren
                            }
                            panel.willExpand(false);
                            panel.expandFullPath(false);
                        }
                        tree.scrollPathToVisible(selPath);
                        node.setText("[" + (pos1 + 1) + "] " + node.getUserObject());
                        node = (LGMTreeNode<?>) lroot.getChildAt(pos1 + 1);
                        node.setText("[" + (pos1 + 2) + "] " + node.getUserObject());
                        int pid = getTransactionID();
                        doc.swapEdgePositions(modelElement, pos1, pos1 + 1, pid);
                    }
                }
                tree.repaint();
                return;
            }
        };
    }

}
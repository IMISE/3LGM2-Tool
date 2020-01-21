package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.NotImplementedException;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.MultipleEdge;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

/**
 * Ein {@link PathConnectionPanel}, das statt im linken bzw. einzigen Baum den ganzen Pfad anzuzeigen immer
 * nur die Blätter unter root anzeigt, wobei diese "Blätter" dann noch ihre Teile (über HasPartEdges)
 * angehängt bekommen. Es werden also nur die End-Elemente des Pfades angezeigt und keine Zwischenelemente.
 *
 * @author astruebi
 * @created 31.05.2017
 */
public class PathConnectionLeafPanel extends PathConnectionPanel {

    /**
     * Mappt von einem Endelement (Blattknoten) auf das ModelElement des im linke Baum darüber liegenden
     * Knotens. Das ist der Node, von dem aus ein eventuell durchzuführendes Unlinken angestoßen werden muss.
     */
    private Map<LGMTreeNode, ModelElement> nodeToParentModelElement;

    public PathConnectionLeafPanel(final AbstractElementPropertyDialog dialog, final SimpleMetaPath simpleMetaPath) {
        this(dialog, LABEL_END_ELEMENT_TYPE, simpleMetaPath);
    }

    public PathConnectionLeafPanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption panelLabelOption, final SimpleMetaPath simpleMetaPath) {
        this(dialog, panelLabelOption, -1, simpleMetaPath);
    }

    public PathConnectionLeafPanel(final AbstractElementPropertyDialog dialog, final int maxLines, final SimpleMetaPath simpleMetaPath) {
        this(dialog, LABEL_END_ELEMENT_TYPE, maxLines, simpleMetaPath);
    }

    public PathConnectionLeafPanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption panelLabelOption, final int maxLines, final SimpleMetaPath simpleMetaPath) {
        super(dialog, panelLabelOption, maxLines, false, simpleMetaPath);
        //die Treenodes nicht einrücken, da ja nur eine Liste dargestellt werden soll
        BasicTreeUI basicTreeUI = (BasicTreeUI) ltree.getUI();
        basicTreeUI.setRightChildIndent(0);
    }

    @Override
    protected boolean isConnectionPointUnique() {
        //bei MultipleEdges wird im rechten Baum nichts ausgeschlossen, bei allen anderen, darf man jedes Element nur einmal verknüpfen -> sie werden im rechten Baum deaktiviert, wenn sie im linken verknüpft sind
        Class<? extends Edge> lastEdgeClassInPath = getLastEdgeClassInPath();
        return !MultipleEdge.class.isAssignableFrom(lastEdgeClassInPath);
    }

    /**
     * Baut im linken Baum nur die Elemente der letzten Edge des Pfades auf
     */
    @Override
    protected Collection<LGMTreeNode> buildLeftTree() {
        Collection<LGMTreeNode> leafNodes = super.buildLeftTree();
        //wenn dieses Panel mit einem Pfad der Länge 1 initialisiert wurde, dann gibt es hier nichts zu tun,
        //da es keine Zwischenelemente gibt, die nicht angezeigt werden sollen
        if (getEdgesInPathCount() == 1) {
            return leafNodes;
        }
        if (nodeToParentModelElement == null) {
            nodeToParentModelElement = new HashMap<>();
        } else {
            nodeToParentModelElement.clear();
        }
        if (!leafNodes.isEmpty()) {
            if (metaPath.isCreatable(false)) { //wen der anlegbar ist, dann werden die Blätter sooft angezeigt, wie Pfade existeren (also Elemente evtl. auch doppelt)
                //vor dem Umhängen der Blätter an den root für jedes Blatt das echte Vorgängerelement auf dem Pfad merken
                for (LGMTreeNode leaf : leafNodes) {
                    LGMTreeNode leafParent = (LGMTreeNode) leaf.getParent();
                    ModelElement parentMe = getNodeModelElement(leafParent);
                    nodeToParentModelElement.put(leaf, parentMe);
                }
                //alle Elemente vom root abhängen
                lroot.removeAllChildren();
                //alle Blätter direkt an den root hängen
                for (LGMTreeNode leaf : leafNodes) {
                    lroot.add(leaf);
                }
            } else { //der Pfad ist nicht anlegbar und somit werden die verbundenen Elemente nur angezeigt -> keins doppelt anzeigen! In diesem Fall ist nodeToParentModelElement egal!
                ImmutableList.Builder<LGMTreeNode> newLeafNodes = ImmutableList.builder();
                //alle Elemente vom root abhängen
                lroot.removeAllChildren();
                ltree.reset(); //check von bereits hinzugefügten Elementen zurück setzen
                //alle Blätter neu erzeugen und direkt an den root hängen
                for (LGMTreeNode leaf : leafNodes) {
                    Object userObject = leaf.getUserObject();
                    if (userObject instanceof ElementContainer) {
                        ElementContainer ec = (ElementContainer) userObject;
                        LGMTreeNode node = ltree.addObject(ec, lroot, null, true, true, false);
                        if (node != null) {
                            newLeafNodes.add(node);
                        }
                    }
                }
                return newLeafNodes.build();
            }
        }
        return leafNodes;
    }

    @Override
    protected TreePath getConnectActionTargetTreeSelectionPath() {
        //ausgewählter Path im TargetTree ist immer root -> immer der gesamte Pfad wird neu angelegt
        return new TreePath(ltree.getModel().getRoot());
    }

    @Override
    protected LGMAction getDisconnectAction() {
        return new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {
            @Override
            public void execute(final EventObject eo) {
                int selrows = ltree.getSelectionCount();
                if (selrows < 1) {
                    return;
                }
                TreePath[] path2disconnect = ltree.getSelectionPaths();
                int treePathEdgeIndex = getEdgesInPathCount() - 1;
                for (int i = 0; i < path2disconnect.length; i++) {
                    LGMTreeNode node = (LGMTreeNode) path2disconnect[i].getLastPathComponent();
                    ModelElement element2Disconnect = getNodeModelElement(node);
                    //immer nur die letzte Edge im Pfad entfernen
                    //das ist der Index der Edge im Pfad, ab der entfernt werden soll
                    ModelElement parentOfElement2Disconnect = nodeToParentModelElement.get(node);
                    disconnect(parentOfElement2Disconnect, element2Disconnect, treePathEdgeIndex);
                }
            }
        };
    }

    @Override
    protected void connectToFirstPath(final ModelElement element2Connect) {
        //dieses Panel ändert das ursprüngliche Verhalten dahingehend, dass es immer den ganzen Pfad neu anlegt und nicht nur den letzten Teil
        createPath(element2Connect);
    }

    /**
     * @param endElement
     */
    public void createPath(final ModelElement endElement) {
        if (!metaPath.isCreatable(endElement != null)) {
            return;
        }
        if (metaPath instanceof SimpleMetaPath) {
            doc.createPath(dialog.getModelElement(), endElement, (SimpleMetaPath) metaPath, true, dialog.getTransactionID());
        } else {
            throw new NotImplementedException("Creation of complex metapaths is not implemented");
        }
    }

}

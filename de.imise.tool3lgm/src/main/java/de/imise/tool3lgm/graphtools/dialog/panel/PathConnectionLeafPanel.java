package de.imise.tool3lgm.graphtools.dialog.panel;

import java.util.Collection;
import java.util.EventObject;
import java.util.Map;

import javax.swing.tree.TreePath;

import com.google.common.collect.Maps;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.tools.LGMTreeNode;

/**
 * Ein {@link PathConnectionPanel}, das statt im linken bzw. einzigen Baum den ganzen Pfad anzuzeigen immer
 * nur die Blätter unter root anzeigt, wobei diese "Blätter" dann noch ihre Teile (über PartOfBeziehungen)
 * angehängt bekommen. Es werden also nur die End-Elemente des Pfades angezeigt und keine Zwischenelemente.
 *
 * @author astruebi
 * @created 31.05.2017
 */
public class PathConnectionLeafPanel extends PathConnectionPanel {

    /**
     * Mappt von einem Endelement auf
     */
    private Map<LGMTreeNode, ModelElement> nodeToUserObjectPath;

    public PathConnectionLeafPanel(final ElementPropertyDialog dialog, final boolean showRightTree, final Class<? extends Kante>... edgeClasses) {
        super(dialog, showRightTree, edgeClasses);
    }

    public PathConnectionLeafPanel(final ElementPropertyDialog dialog, final boolean showRightTree, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante>... edgeClasses) {
        super(dialog, showRightTree, searchElementClass, edgeClasses);
    }

    public PathConnectionLeafPanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final boolean showRightTree, final Class<? extends Kante>... edgeClasses) {
        super(dialog, labelLastEdgeName, showRightTree, edgeClasses);
    }

    @Override
    protected boolean isConnectionPointUnique() {
        //es wird immer an root angehängt
        return true;
    }

    /**
     * Baut im linken Baum nur die Elemente der letzten Kante des Pfades auf
     */
    @Override
    protected Collection<LGMTreeNode> buildTree() {
        Collection<LGMTreeNode> leafNodes = super.buildTree();
        //wenn dieses Panel mit einem Pfad der Länge 1 initialisiert wurde, dann gibt es hier nichts zu tun,
        //da es keine Zwischenelemente gibt, die nicht angezeigt werden sollen
        if (edgeClasses.length == 1) {
            return leafNodes;
        }
        if (nodeToUserObjectPath == null) {
            nodeToUserObjectPath = Maps.newHashMap();
        } else {
            nodeToUserObjectPath.clear();
        }
        if (!leafNodes.isEmpty()) {
            //vor dem Umhängen der Blätter an den root für jedes Blatt das echte Vorgängerelement auf dem Pfad merken
            for (LGMTreeNode leaf : leafNodes) {
                LGMTreeNode leafParent = (LGMTreeNode) leaf.getParent();
                ModelElement parentMe = getNodeModelElement(leafParent);
                nodeToUserObjectPath.put(leaf, parentMe);
                root.add(leaf);
            }
            //alle Elemente vom root abhängen
            root.removeAllChildren();
            //alle Blätter direkt an den root hängen
            for (LGMTreeNode leaf : leafNodes) {
                root.add(leaf);
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
                for (int i = 0; i < path2disconnect.length; i++) {
                    LGMTreeNode node = (LGMTreeNode) path2disconnect[i].getLastPathComponent();
                    ModelElement element2Disconnect = getNodeModelElement(node);
                    //immer nur die letzte Kante im Pfad entfernen
                    //das ist der Index der Kante im Pfad, ab der entfernt werden soll
                    int treePathEdgeIndex = edgeClasses.length - 1;
                    ModelElement parentOfElement2Disconnect = nodeToUserObjectPath.get(node);
                    disconnect(parentOfElement2Disconnect, element2Disconnect, treePathEdgeIndex);
                }
            }
        };
    }

}

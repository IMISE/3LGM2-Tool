package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.graphtools.elements.Doppelkante.FORWARD;

import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.tools.LGMTreeNode;

/**
 * Panel für alle einfachen Verbindungen zwischen 2 Elementen, also der Kantenpfad ist genau eine Kante lang.
 *
 * @author AXS
 * @created 24.04.2017
 */
public abstract class AbstractPathOfOneEdgePanel extends AbstractExpandablePanel {

    /** Die Kantenklasse zum anderen Element */
    protected final Class<? extends Kante> edgeClass;

    protected final boolean edgeIsForward;

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param searchElementClass
     * @param edgeClass
     */
    public AbstractPathOfOneEdgePanel(final ElementPropertyDialog dialog, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante> edgeClass) {
        this(dialog, false, searchElementClass, edgeClass);
    }

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param labelEdgeName
     * @param searchElementClass
     * @param edgeClass
     */
    public AbstractPathOfOneEdgePanel(final ElementPropertyDialog dialog, final boolean labelEdgeName, final Class<? extends ModelElement> searchElementClass, final Class<? extends Kante> edgeClass) {
        super(dialog, labelEdgeName, searchElementClass, edgeClass);
        this.edgeClass = edgeClass;
        edgeIsForward = directions[0] == FORWARD;
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "addButtons" der Panels angefügt werden.
     *
     * @param srcTree
     * @param targetTree
     * @param connectForward
     */
    protected final LGMAction getConnectAction(final JTree srcTree, final JTree targetTree, final boolean connectForward) {
        final GraphDocument doc = getGraphDocument();
        final GDCollection gdcoll = doc.getCollection();
        final int pid = getTransactionID();
        return new LGMAction("", Tool3lgmConstants.getIcon("arrow_left2.gif")) {
            @Override
            public void execute(final EventObject e) {
                TreePath[] selpaths = srcTree.getSelectionPaths();
                if (selpaths != null) {
                    for (int n = 0; n < selpaths.length; n++) {
                        LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                        ElementContainer ec = (ElementContainer) node.getUserObject();
                        ModelElement me = ec.getElement();
                        ModelElement topLevelMe = getTopLevelModelElement(targetTree);
                        if (connectForward) {
                            gdcoll.link(edgeClass, topLevelMe, me, pid);
                        } else {
                            gdcoll.link(edgeClass, me, topLevelMe, pid);
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
     * @param disconnectForward
     */
    protected final LGMAction getDisconnectAction(final JTree srcTree, final JTree targetTree, final boolean disconnectForward) {
        final GraphDocument doc = getGraphDocument();
        final GDCollection gdcoll = doc.getCollection();
        final int pid = getTransactionID();
        return new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {
            @Override
            public void execute(final EventObject e) {
                TreePath[] selpaths = srcTree.getSelectionPaths();
                if (selpaths != null) {
                    for (int n = 0; n < selpaths.length; n++) {
                        LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                        ElementContainer ec = (ElementContainer) node.getUserObject();
                        ModelElement me = ec.getElement();
                        ModelElement topLevelModelElement = getTopLevelModelElement(targetTree == null ? srcTree : targetTree);
                        if (disconnectForward) {
                            gdcoll.unlink(topLevelModelElement, me, edgeClass, pid);
                        } else {
                            gdcoll.unlink(me, topLevelModelElement, edgeClass, pid);
                        }
                    }
                }
            }
        };
    }

}

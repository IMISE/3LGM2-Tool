package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;

import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.element.DialogActionCommands;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

/**
 * Panel für alle einfachen Verbindungen zwischen 2 Elementen, also der
 * Kantenpfad ist genau eine Edge lang.
 *
 * @author AXS
 * @created 24.04.2017
 */
public abstract class AbstractPathOfOneEdgePanel extends AbstractExpandablePanel {

    protected final ElementaryMetaPath metaPath;

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param searchElementClass
     * @param edgeClass
     */
    public AbstractPathOfOneEdgePanel(final AbstractElementPropertyDialog dialog, final Class<? extends ModelElement> searchElementClass, final Class<? extends Edge> edgeClass) {
        this(dialog, LABEL_END_ELEMENT_TYPE, LABEL_END_ELEMENT_TYPE, searchElementClass, edgeClass);
    }

    /**
     * Panel für eine einfache Assoziation
     *
     * @param dialog
     * @param titleLabelOption
     * @param westLabelOption
     * @param searchElementClass
     * @param edgeClass
     */
    public AbstractPathOfOneEdgePanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final Class<? extends ModelElement> searchElementClass,
            final Class<? extends Edge> edgeClass) {
        super(dialog, titleLabelOption, westLabelOption, dialog.createSequenceMetaPath(searchElementClass, edgeClass));
        metaPath = (ElementaryMetaPath) super.metaPath;
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben
     * von Elementen aus dem <code>srcTree</code> in den <code>targetTree</code>
     * realisiert. Diese <code>LGMAction</code> sollte an die "addButtons" der
     * Panels angefügt werden.
     *
     * @param srcTree
     * @param targetTree
     * @param connectForward
     */
    protected final LGMAction getConnectAction(final JTree srcTree, final JTree targetTree, final Direction direction) {
        final int pid = getTransactionID();
        return new LGMAction(DialogActionCommands.ACTION_DIALOG_CONNECT_ELEMENT) {
            @Override
            public void execute(final EventObject e) {
                TreePath[] selpaths = srcTree.getSelectionPaths();
                if (selpaths != null) {
                    for (int n = 0; n < selpaths.length; n++) {
                        LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                        ElementContainer ec = (ElementContainer) node.getUserObject();
                        GDCollection gdcoll = ec.getCollection();
                        ModelElement me = ec.getElement();
                        ModelElement panelModelElement = getModelElement();
                        Class<? extends Edge> edgeClass = metaPath.getEdgeClass();
                        gdcoll.link(panelModelElement, me, edgeClass, direction, pid);
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
     * @param disconnectForward
     */
    protected final LGMAction getDisconnectAction(final JTree srcTree, final JTree targetTree, final Direction direction) {
        final int pid = getTransactionID();
        return new LGMAction(DialogActionCommands.ACTION_DIALOG_DISCONNECT_ELEMENT) {
            @Override
            public void execute(final EventObject e) {
                TreePath[] selpaths = srcTree.getSelectionPaths();
                if (selpaths != null) {
                    for (int n = 0; n < selpaths.length; n++) {
                        LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                        ElementContainer ec = (ElementContainer) node.getUserObject();
                        GDCollection gdcoll = ec.getCollection();
                        ModelElement me = ec.getElement();
                        ModelElement panelModelElement = getModelElement();
                        Class<? extends Edge> edgeClass = metaPath.getEdgeClass();
                        gdcoll.unlink(panelModelElement, me, edgeClass, direction, pid);
                    }
                }
            }
        };
    }

}

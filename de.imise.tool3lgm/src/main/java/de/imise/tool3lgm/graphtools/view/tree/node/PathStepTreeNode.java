/**
 *
 */
package de.imise.tool3lgm.graphtools.view.tree.node;

import javax.swing.ImageIcon;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * @author AXS (16.11.2020)
 */
public class PathStepTreeNode extends ElementContainerTreeNode {

    /**
     *
     */
    private final MetaPath metaPath;

    /**
     * @param ec
     * @param metaPath
     * @param setTreeNode
     * @param sort
     */
    public PathStepTreeNode(final ElementContainer ec, final MetaPath metaPath, final boolean setTreeNode, final boolean sort) {
        this(ec, metaPath, setTreeNode, sort, null);
    }

    /**
     * @param ec
     * @param metaPath
     * @param setTreeNode
     * @param sort
     * @param icon
     */
    public PathStepTreeNode(final ElementContainer ec, final MetaPath metaPath, final boolean setTreeNode, final boolean sort, final ImageIcon icon) {
        super(ec, setTreeNode, getVisibleText(ec, metaPath), sort, icon);
        this.metaPath = metaPath;
    }

    /**
     * @param ec
     * @param metaPath
     * @return
     */
    private static final String getVisibleText(final ElementContainer ec, final MetaPath metaPath) {
        ModelElement me = ec.getElement();
        String metaPathName = metaPath.getName();
        String visibleText = me.toString() + " (" + metaPathName + ")";
        return visibleText;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (metaPath == null ? 0 : metaPath.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PathStepTreeNode other = (PathStepTreeNode) obj;
        if (metaPath == null) {
            if (other.metaPath != null) {
                return false;
            }
        } else if (!metaPath.equals(other.metaPath)) {
            return false;
        }
        return true;
    }

}

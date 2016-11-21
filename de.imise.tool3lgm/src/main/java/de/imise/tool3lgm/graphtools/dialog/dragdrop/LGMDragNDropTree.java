/*
 * Created on 25.10.2007
 */
package de.imise.tool3lgm.graphtools.dialog.dragdrop;

import javax.swing.tree.DefaultTreeModel;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.tools.LGMTree;

/**
 * @author fstephan
 */
public class LGMDragNDropTree extends LGMTree {

    public LGMDragNDropTree(final DefaultTreeModel treeModel, final GraphDocument doc) {
        super(treeModel, doc);

    }

    public LGMDragNDropTree(final DefaultTreeModel treeModel) {
        super(treeModel);

    }

}

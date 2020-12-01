package de.imise.util.swing.dnd.tree.demo;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;

/**
 * @author helloworld922
 *         <p>
 * @version 1.0
 *          <p>
 *          copyright 2010 <br>
 *          You are welcome to use/modify this code for any purposes you want so
 *          long as credit is given to me.
 */
public class DnDJTree extends JTree {
    /**
     *
     */
    private static final long serialVersionUID = -4260543969175732269L;

    /**
     * Constructs a DnDJTree with root as the main node.
     *
     * @param root
     */
    public DnDJTree(final TreeModel model) {
        super(model);
        // turn on the JComponent dnd interface
        setDragEnabled(true);
        // setup our transfer handler
        setTransferHandler(new JTreeTransferHandler(this));
    }

}
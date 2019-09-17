package de.imise.tool3lgm.graphtools.view.template;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author AXS (23.08.2019)
 */
public class TemplateBrowserPanel extends JPanel {

    /** */
    private final TemplateBrowserTree tree;

    /**
     *
     */
    public TemplateBrowserPanel() {
        setLayout(new BorderLayout());
        tree = new TemplateBrowserTree();
        JScrollPane treeScrollPane = new JScrollPane(tree);
        add(treeScrollPane, BorderLayout.CENTER);
    }

}

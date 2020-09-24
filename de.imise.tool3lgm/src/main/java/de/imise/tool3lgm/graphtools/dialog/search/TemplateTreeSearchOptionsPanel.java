/**
 *
 */
package de.imise.tool3lgm.graphtools.dialog.search;

import java.awt.BorderLayout;

import de.imise.tool3lgm.graphtools.view.template.TemplateBrowserTree;

/**
 * @author AXS (31.07.2020)
 */
public class TemplateTreeSearchOptionsPanel extends BasicSearchOptionsPanel {

    /**
     * @param tree
     */
    public TemplateTreeSearchOptionsPanel(final TemplateBrowserTree tree) {
        super(tree, new BorderLayout());

        add(searchButton, BorderLayout.WEST);
        add(elementName, BorderLayout.CENTER);
    }

}

/**
 *
 */
package de.imise.tool3lgm.graphtools.view.template;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.imise.util.swing.component.HistoryComboBox;

/**
 * @author Ich (31.07.2020)
 */
public class TemplateTreeSearchPanel extends JPanel {

    /**
     *
     */
    private final TemplateBrowserTree tree;

    /**
     *
     */
    private final HistoryComboBox comboBox;

    /**
     * @param tree
     */
    public TemplateTreeSearchPanel(final TemplateBrowserTree tree) {
        super(new BorderLayout());
        this.tree = tree;
        comboBox = new HistoryComboBox(100);
        add(comboBox, BorderLayout.CENTER);
        String searchLabelText = getResString("TEMPLATE_BROWSER_LABEL_SEARCH");
        JLabel searchLabel = new JLabel(searchLabelText);
        searchLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(searchLabel, BorderLayout.WEST);
    }

}

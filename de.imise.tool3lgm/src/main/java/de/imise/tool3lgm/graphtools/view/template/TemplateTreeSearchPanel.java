/**
 *
 */
package de.imise.tool3lgm.graphtools.view.template;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.imise.tool3lgm.graphtools.dialog.SearchDialog;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
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
    private final HistoryComboBox searchComboBox;

    /**
     *
     */
    private final JButton searchButton;

    /**
     * @param tree
     */
    public TemplateTreeSearchPanel(final TemplateBrowserTree tree) {
        super(new BorderLayout());
        this.tree = tree;

        String searchLabelText = getResString("TEMPLATE_BROWSER_LABEL_SEARCH");
        AbstractAction searchAction = new AbstractAction(searchLabelText) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                selectMatchesInTree();
            }
        };

        searchComboBox = new HistoryComboBox(50, searchAction);
        searchButton = new JButton(searchAction);

        add(searchButton, BorderLayout.WEST);
        add(searchComboBox, BorderLayout.CENTER);
    }

    /**
     *
     */
    private void selectMatchesInTree() {
        tree.clearSelection();
        for (GDCollection template : tree.getDisplayedTemplates()) {
            template.deselectAll();
            GraphDocument selectedTemplateDoc = template.getSelectedDoc();
            List<ElementContainer> result = SearchDialog.getResult(selectedTemplateDoc, searchComboBox);
            template.addToSelection(result);
        }
        tree.addSelection();
    }

}

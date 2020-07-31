/**
 *
 */
package de.imise.tool3lgm.graphtools.view.template;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import de.imise.util.swing.component.HistoryComboBox;

/**
 * @author Ich (31.07.2020)
 */
public class TemplateTreeSearchPanel extends JPanel implements ItemListener {

    /**
     *
     */
    private final TemplateBrowserTree tree;

    /**
     *
     */
    private final HistoryComboBox searchComboBox;

    /**
     * @param tree
     */
    public TemplateTreeSearchPanel(final TemplateBrowserTree tree) {
        super(new BorderLayout());
        this.tree = tree;
        searchComboBox = new HistoryComboBox(100);
        add(searchComboBox, BorderLayout.CENTER);
        String searchLabelText = getResString("TEMPLATE_BROWSER_LABEL_SEARCH");
        JLabel searchLabel = new JLabel(searchLabelText);
        searchLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(searchLabel, BorderLayout.WEST);
        searchComboBox.addItemListener(this);
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        int stateChange = e.getStateChange();
        if (stateChange == ItemEvent.DESELECTED) {
            deselectMatchesInTree();
        } else if (stateChange == ItemEvent.SELECTED) {
            JPopupMenu componentPopupMenu = searchComboBox.getComponentPopupMenu();
            System.err.println(componentPopupMenu != null && componentPopupMenu.isVisible());

            HistoryComboBox.addToHistory(searchComboBox);
            selectMatchesInTree();
            System.err.println(e);
        }
    }

    /**
     *
     */
    private void deselectMatchesInTree() {

    }

    /**
     *
     */
    private void selectMatchesInTree() {
        Object selectedItem = searchComboBox.getSelectedItem();
        String selectedItemString = selectedItem.toString();
        System.err.println(selectedItemString);
    }

}

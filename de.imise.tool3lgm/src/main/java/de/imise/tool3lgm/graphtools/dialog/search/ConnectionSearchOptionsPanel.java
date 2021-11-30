package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import de.imise.tool3lgm.graphtools.view.tree.ElementDialogPanelTree;
import de.imise.util.swing.component.HistoryComboBox;

public class ConnectionSearchOptionsPanel extends BasicSearchOptionsPanel {

    /**  */
    private final JButton expandButton;

    /** stores information if the panel has been expanded or not */
    private static boolean expanded;

    /**
     * Stores the SearchOptions to restore them if the panel was removed and
     * then added again.
     */
    private static SearchOptions lastSearchOptions;

    /**
     * @param tree
     */
    public ConnectionSearchOptionsPanel(final ElementDialogPanelTree tree) {

        super(tree, new GridBagLayout());
        expanded = false;
        expandButton = new JButton("|");
        expandButton.setToolTipText(getResString("SEARCH_TEMPLATE_TOOLTIP_more_options"));

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 4;
        constraints.weightx = 0;
        constraints.ipadx = -10; //Causes the button to display only 3 points
        add(expandButton, constraints);
        expandButton.addActionListener(arg0 -> showFullPanel(expanded));

        addListenerToRestoreSearchOptions();
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public HistoryComboBox getElementName() {
        return elementName;
    }

    /**
     * Adds an {@link AncestorListener} to restore the {@link SearchOptions}
     */
    private void addListenerToRestoreSearchOptions() {

        addAncestorListener(new AncestorListener() {

            @Override
            public void ancestorAdded(final AncestorEvent event) {
                if (lastSearchOptions != null) {
                    restoreSearchOptions(lastSearchOptions);
                    lastSearchOptions = null;
                }
            }

            @Override
            public void ancestorRemoved(final AncestorEvent event) {
                lastSearchOptions = getSearchOptions(true);
            }

            @Override
            public void ancestorMoved(final AncestorEvent event) {
            }

        });
    }

    /**
     * @param full
     */
    public final void showFullPanel(final boolean full) {
        if (expanded) {
            showPartlyPanel();
            expandButton.setToolTipText(getResString("SEARCH_TEMPLATE_TOOLTIP_more_options"));
        } else {
            showFullPanel();
            expandButton.setToolTipText(getResString("SEARCH_TEMPLATE_TOOLTIP_less_options"));
        }
        revalidate();
        repaint();
    }

    /**
     * expands the panel with more filter options
     */
    protected void showFullPanel() {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 0;
        add(searchButton, constraints);

        constraints.gridx = 1;
        add(elementName, constraints);

        constraints.gridy = 1;
        constraints.insets.top = -3;
        add(checkNameCaseSensitive, constraints);

        expanded = true;
    }

    /**
     * removes the added options
     */
    protected void showPartlyPanel() {
        remove(searchButton);
        remove(elementName);
        remove(labelName);
        remove(checkNameCaseSensitive);
        remove(labelDescription);
        remove(elementDescription);
        remove(checkDescriptionCaseSensitive);
        remove(labelElementType);
        remove(elementClassBox);

        Border emptyBorder = BorderFactory.createEmptyBorder();
        setBorder(emptyBorder);

        expanded = false;
    }

}

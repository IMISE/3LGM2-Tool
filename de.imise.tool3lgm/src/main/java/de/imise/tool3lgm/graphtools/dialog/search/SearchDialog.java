package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;

public class SearchDialog extends JDialog {

    /**
     * Stores the searchOptions of the last opended SearchDialog
     * to restore them if a new dialog is opened.
     */
    private static SearchOptions lastSearchOptions;

    /**
     *
     */
    private final SearchDialogOptionsPanel searchOptionsPanel;

    /**
     * Konstruiert auf dem Frame den Dialog.
     *
     * @param owner
     */
    public SearchDialog(final Frame owner) {
        super(owner);

        searchOptionsPanel = new SearchDialogOptionsPanel();

        // um überblenden zu verhindern
        setMinimumSize(new Dimension(600, 400));

        setTitle(getResString("SEARCH_DIALOG_TITLE"));

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(searchOptionsPanel, BorderLayout.NORTH);
        Component resultTablePanel = searchOptionsPanel.getResultViewComponent();
        contentPane.add(resultTablePanel, BorderLayout.CENTER);

        pack();
    }

    /**
     *
     */
    public void showDialog() {
        setVisible(true);
    }

    @Override
    public void setVisible(final boolean b) {
        if (true && lastSearchOptions != null) {
            searchOptionsPanel.setSearchOptions(lastSearchOptions);
        }
        super.setVisible(b);
    }

    @Override
    public void dispose() {
        lastSearchOptions = searchOptionsPanel.getSearchOptions();
        super.dispose();
    }

}

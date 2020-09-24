package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

        addWindowListenerToRestoreOldStates();
    }

    /**
     * Store/restore the old values of all input
     * components if the dialog is closed/opened
     */
    private void addWindowListenerToRestoreOldStates() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                lastSearchOptions = searchOptionsPanel.getSearchOptions(true);
                super.windowClosing(e);
            }

            @Override
            public void windowOpened(final WindowEvent e) {
                if (lastSearchOptions != null) {
                    searchOptionsPanel.restoreSearchOptions(lastSearchOptions);
                    lastSearchOptions = null;
                }
                super.windowOpened(e);
            }
        });
    }

    /**
     *
     */
    public void showDialog() {
        setVisible(true);
    }

}

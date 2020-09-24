/**
 *
 */
package de.imise.tool3lgm.graphtools.dialog.search;

import java.awt.BorderLayout;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import de.imise.tool3lgm.graphtools.view.template.TemplateBrowserTree;

/**
 * @author AXS (31.07.2020)
 */
public class TemplateTreeSearchOptionsPanel extends BasicSearchOptionsPanel {

    /**
     * Stores the SearchOptions to restore them if
     * the panel was removed and then added again.
     */
    private static SearchOptions lastSearchOptions;

    /**
     * @param tree
     */
    public TemplateTreeSearchOptionsPanel(final TemplateBrowserTree tree) {
        super(tree, new BorderLayout());

        add(searchButton, BorderLayout.WEST);
        add(elementName, BorderLayout.CENTER);

        addListenerToRestoreSearchOptions();
    }

    /**
     * Adds an {@link AncestorListener} to restore the
     * {@link SearchOptions}
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

}

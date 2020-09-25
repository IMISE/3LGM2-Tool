package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeDefinition;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeModel;
import de.imise.tool3lgm.graphtools.view.template.TemplateBrowserTree;
import de.imise.util.Sys;

/**
 * @author AXS (31.07.2020)
 */
public class TemplateTreeSearchOptionsPanel extends BasicSearchOptionsPanel {

    private final JPanel filterOptionPanel = new JPanel(new GridLayout(0, 2));
    private final JButton expandOptions;
    private static boolean expanded;
    GridBagConstraints c = new GridBagConstraints();

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
        expanded = false;
        expandOptions = new JButton(">>");

        add(searchButton, BorderLayout.WEST);
        add(elementName, BorderLayout.CENTER);
        add(expandOptions, BorderLayout.EAST);
        add(filterOptionPanel, BorderLayout.PAGE_END);
        expandOptions.addActionListener(arg0 -> showFullPanel(expanded));

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

    /**
     *
     */
    public void addExpandOptionsButtonListener() {

    }

    /**
     * @param full
     */
    public final void showFullPanel(final boolean full) {
        if (expanded) {
            showPartlyPanel();

        } else {
            showFullPanel();
        }
        revalidate();
        repaint();
    }

    /**
     *
     */
    protected void showFullPanel() {
        Sys.out1("pushed");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 0;
        filterOptionPanel.add(checkNameCaseSensitive);
        filterOptionPanel.add(elementDescription);
        filterOptionPanel.add(checkDescriptionCaseSensitive);
        filterOptionPanel.add(elementClassBox);

        expanded = true;
    }

    /**
     *
     */
    protected void showPartlyPanel() {
        filterOptionPanel.remove(checkNameCaseSensitive);
        filterOptionPanel.remove(elementDescription);
        filterOptionPanel.remove(checkDescriptionCaseSensitive);
        filterOptionPanel.remove(elementClassBox);

        expanded = false;
    }

    /**
     * Befüllt die elementClassBox
     */
    @Override
    protected void fillElementClassBox() {

        elementClassBox.removeAllItems();
        elementClassBox.addItem(ModelElement.class, getResString("SEARCH_DIALOG_USERFIELD_AlleElementeArten"));
        elementClassBox.addSeparator(true);

        PathTreeModel model = ((TemplateBrowserTree) resultTargetView).getModel();
        PathTreeDefinition pathTreeDefinition = model.getPathTreeDefinition();
        ElementsNameBuilder elementsNameBuilder = pathTreeDefinition.getElementsNameBuilder();
        for (Class<? extends ModelElement> elementClass : pathTreeDefinition.getVisibleElementTypes()) {
            elementClassBox.addItem(elementClass, elementsNameBuilder.getDisplayableFullName(elementClass));
        }
        elementClassBox.setSelectedObject(ModelElement.class);
    }

}

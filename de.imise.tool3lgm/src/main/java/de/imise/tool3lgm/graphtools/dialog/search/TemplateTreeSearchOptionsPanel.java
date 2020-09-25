package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeDefinition;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeModel;
import de.imise.tool3lgm.graphtools.view.template.TemplateBrowserTree;

/**
 * @author AXS (31.07.2020)
 */
public class TemplateTreeSearchOptionsPanel extends BasicSearchOptionsPanel {

    /**  */
    private final JButton expandOptions;

    /** stores information if the panel has been expanded or not */
    private static boolean expanded;

    /**
     * Stores the SearchOptions to restore them if
     * the panel was removed and then added again.
     */
    private static SearchOptions lastSearchOptions;

    /**
     * @param tree
     */
    public TemplateTreeSearchOptionsPanel(final TemplateBrowserTree tree) {
        super(tree, new GridBagLayout());
        expanded = false;
        expandOptions = new JButton(">>");
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 0;
        add(searchButton, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.weightx = 1;
        add(elementName, constraints);

        constraints.gridx = 4;
        constraints.weightx = 0;
        add(expandOptions, constraints);
        //        add(filterOptionPanel, constraints);
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
     * expands the panel with more filter options
     */
    protected void showFullPanel() {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0;
        add(labelName, constraints);

        constraints.gridx = 2;
        constraints.gridy++;
        add(checkNameCaseSensitive, constraints);

        constraints.gridx = 1;
        constraints.gridy++;
        constraints.weightx = 0;
        add(labelDescription, constraints);

        constraints.gridx = 2;
        constraints.weightx = 1;
        add(elementDescription, constraints);

        constraints.gridx = 2;
        constraints.gridy++;
        constraints.weightx = 0;
        add(checkDescriptionCaseSensitive, constraints);

        constraints.gridx = 1;
        constraints.gridy++;
        constraints.weightx = 0;
        add(labelElementType, constraints);

        constraints.gridx = 2;
        constraints.weightx = 1;
        add(elementClassBox, constraints);

        expanded = true;
    }

    /**
     * removes the added options
     */
    protected void showPartlyPanel() {
        remove(labelName);
        remove(checkNameCaseSensitive);
        remove(labelDescription);
        remove(elementDescription);
        remove(checkDescriptionCaseSensitive);
        remove(labelElementType);
        remove(elementClassBox);

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

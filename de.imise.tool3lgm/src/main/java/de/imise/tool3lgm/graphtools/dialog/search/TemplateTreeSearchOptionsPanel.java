package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;
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
    private final JButton expandButton;

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
        expandButton = new JButton("|");
        expandButton.setToolTipText(getResString("SEARCH_TEMPLATE_TOOLTIP_more_options"));

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
        constraints.ipadx = -10; //Causes the button to display only 3 points
        add(expandButton, constraints);
        expandButton.addActionListener(arg0 -> showFullPanel(expanded));

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
            expandButton.setToolTipText(getResString("SEARCH_TEMPLATE_TOOLTIP_more_options"));
        } else {
            showFullPanel();
            expandButton.setToolTipText(getResString("SEARCH_TEMPLATE_TOOLTIP_less_options"));
        }
        revalidate();
        repaint();
    }

    /**
     * @param constraints
     * @param insets
     */
    private static void setLefRightInsets(final GridBagConstraints constraints, final int insets) {
        constraints.insets.left = insets;
        constraints.insets.right = insets;
    }

    /**
     * @param constraints
     * @param top
     * @param bottom
     */
    private static void setTopBottomInsest(final GridBagConstraints constraints, final int top, final int bottom) {
        constraints.insets.top = top;
        constraints.insets.bottom = bottom;
    }

    /**
     * expands the panel with more filter options
     */
    protected void showFullPanel() {
        GridBagConstraints constraints = new GridBagConstraints();

        int labelInsets = 5;
        int checkboxBottomInsets = 7;
        int checkboxTopInsets = -3;

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 0;
        setLefRightInsets(constraints, labelInsets);
        add(labelName, constraints);
        setLefRightInsets(constraints, 0);

        constraints.gridx = 2;
        constraints.gridy++;
        setTopBottomInsest(constraints, checkboxTopInsets, checkboxBottomInsets);
        add(checkNameCaseSensitive, constraints);
        setTopBottomInsest(constraints, 0, 0);

        constraints.gridx = 1;
        constraints.gridy++;
        constraints.weightx = 0;
        setLefRightInsets(constraints, labelInsets);
        add(labelDescription, constraints);
        setLefRightInsets(constraints, 0);

        constraints.gridx = 2;
        constraints.weightx = 1;
        add(elementDescription, constraints);

        constraints.gridx = 2;
        constraints.gridy++;
        constraints.weightx = 0;
        setTopBottomInsest(constraints, checkboxTopInsets, checkboxBottomInsets);
        add(checkDescriptionCaseSensitive, constraints);
        setTopBottomInsest(constraints, 0, 0);

        constraints.gridx = 1;
        constraints.gridy++;
        constraints.weightx = 0;
        setLefRightInsets(constraints, labelInsets);
        add(labelElementType, constraints);
        setLefRightInsets(constraints, 0);

        constraints.gridx = 2;
        constraints.weightx = 1;
        add(elementClassBox, constraints);

        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 8, 0);
        setBorder(emptyBorder);

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

        Border emptyBorder = BorderFactory.createEmptyBorder();
        setBorder(emptyBorder);

        expanded = false;
    }

    /**
     * Befüllt die elementClassBox
     */
    @Override
    protected void fillElementClassBox() {
        elementClassBox.removeAllItems();
        elementClassBox.addItem(ModelElement.class, getResString("SEARCH_DIALOG_all_element_types"));
        elementClassBox.addSeparator(true);

        PathTreeModel model = ((TemplateBrowserTree) resultTargetView).getModel();
        PathTreeDefinition pathTreeDefinition = model.getPathTreeDefinition();
        ElementsNameBuilder elementsNameBuilder = pathTreeDefinition.getElementsNameBuilder();
        Set<Class<? extends ModelElement>> visibleElementTypes = pathTreeDefinition.getVisibleElementTypes();
        for (Class<? extends ModelElement> elementClass : visibleElementTypes) {
            String displayableFullName = elementsNameBuilder.getDisplayableFullName(elementClass);
            elementClassBox.addItem(elementClass, displayableFullName);
        }
        elementClassBox.setSelectedObject(ModelElement.class);
    }

}

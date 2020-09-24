/**
 *
 */
package de.imise.tool3lgm.graphtools.dialog.search;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;

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

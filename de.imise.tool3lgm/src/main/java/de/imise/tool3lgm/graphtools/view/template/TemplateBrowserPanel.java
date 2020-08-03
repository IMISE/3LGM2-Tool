package de.imise.tool3lgm.graphtools.view.template;

import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static de.imise.tool3lgm.graphtools.view.template.TemplateBrowserTree.PropertyChangeEventType.CONTENT_CHANGED;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_TEMPLATE_ELEMENTS_IN_MODEL_BROWSER;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * @author AXS (23.08.2019)
 */
public class TemplateBrowserPanel extends JPanel implements PropertyChangeListener {

    /** */
    private final TemplateBrowserTree tree;

    /**
     *
     */
    private final TemplateTreeSearchPanel searchPanel;

    /**
     *
     */
    public TemplateBrowserPanel() {
        setLayout(new BorderLayout());
        tree = new TemplateBrowserTree();
        JScrollPane treeScrollPane = new JScrollPane(tree);
        searchPanel = new TemplateTreeSearchPanel(tree);
        add(treeScrollPane, BorderLayout.CENTER);
        tree.addPropertyChangeListener(CONTENT_CHANGED, this);
        checkSearchFieldVisibility();
    }

    /**
     * @param selectionSource
     */
    public void updateSelection(final GraphDocument selectionSource) {
        selectElementsWithSameHashAndInstaniationMasterElements(selectionSource);
    }

    /**
     * Selectes all elements in the templates with the same hashStrings like
     * the selected elements in the given {@link GraphDocument}. Additionally
     * all instanciation master elements will be selected which are the same
     * (same id) like the master element of a selected instanciation instance
     * element in the model. So if you select an {@link InstanciationEdge}
     * instance in the model the instanciation master element will be selected
     * in the {@link TemplateView}.
     *
     * @param source
     */
    public void selectElementsWithSameHashAndInstaniationMasterElements(final GraphDocument source) {
        Collection<GDCollection> displayedTemplateModels = tree.getDisplayedTemplates();
        Collection<GraphDocument> displayedTemplates = new ArrayList<>(displayedTemplateModels.size());
        for (GDCollection template : displayedTemplateModels) {
            LGMGraphDocument activeTemplateDoc = template.getSelectedDoc();
            activeTemplateDoc.deselectAll(false);
            displayedTemplates.add(activeTemplateDoc);
        }
        for (ElementContainer ec : source.getSelectedRealElementContainerIterable()) {
            ModelElement me = ec.getElement();
            //select all elements with the same id in the templates, but
            //exclude pure template elements from selection because some actions
            //like instanciation expands the selection in an unexpected way
            if (!me.isPureTemplateElement() || OPTION_ENABLE_EXPERT_MODE.is() || OPTION_SHOW_TEMPLATE_ELEMENTS_IN_MODEL_BROWSER.is()) {
                selectSameElementInTemplates(displayedTemplates, me);
            }
            List<InstanciationEdge> instanciationEdges = me.getTypedEdges(InstanciationEdge.class);
            //if the element is an isnatnciation instance -> select the master element in the templates
            for (InstanciationEdge instanciationEdge : instanciationEdges) {
                ModelElement instanceElement = instanciationEdge.getInstanceElement();
                if (me == instanceElement) {
                    ModelElement masterElement = instanciationEdge.getMasterElement();
                    selectSameElementInTemplates(displayedTemplates, masterElement);
                }
            }
        }
        tree.addSelection();
    }

    /**
     * @param me
     * @param elementHash
     */
    private void selectSameElementInTemplates(final Collection<GraphDocument> templates, final ModelElement me) {
        String elementHash = me.getHashString();
        for (GraphDocument template : templates) {
            template.addToSelection(elementHash, STANDARD_PID);
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        checkSearchFieldVisibility();
    }

    /**
     *
     */
    private void checkSearchFieldVisibility() {
        boolean showSearchPanel = tree.hasContent();
        if (showSearchPanel) {
            if (searchPanel.getParent() == null) {
                add(searchPanel, BorderLayout.NORTH);
            }
        } else {
            if (searchPanel.getParent() != null) {
                remove(searchPanel);
            }
        }
    }

}

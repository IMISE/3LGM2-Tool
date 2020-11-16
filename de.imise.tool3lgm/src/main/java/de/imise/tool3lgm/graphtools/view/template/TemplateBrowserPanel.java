package de.imise.tool3lgm.graphtools.view.template;

import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static de.imise.tool3lgm.graphtools.view.template.TemplateBrowserTree.PropertyChangeEventType.CONTENT_CHANGED;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_EXPERT_MODE;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_TEMPLATE_ELEMENTS_IN_MODEL_BROWSER;

import java.awt.BorderLayout;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.dialog.search.TemplateTreeSearchOptionsPanel;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;

/**
 * @author AXS (23.08.2019)
 */
public class TemplateBrowserPanel extends JPanel implements PropertyChangeListener {

    /** */
    private final TemplateBrowserTree tree;

    /**
     *
     */
    private TemplateTreeSearchOptionsPanel searchPanel;

    /**
     *
     */
    private final JComponent treeScrollPane;

    /**
     *
     */
    public TemplateBrowserPanel() {
        setLayout(new BorderLayout());
        tree = new TemplateBrowserTree(BooleanProperty.OPTION_ENABLE_EXPERT_MODE);
        treeScrollPane = new JScrollPane(tree);
        tree.addPropertyChangeListener(CONTENT_CHANGED, this);
    }

    /**
     * @param selectionSource
     */
    public void updateSelection(final GraphDocument selectionSource) {
        selectElementsWithSameHashAndInstaniationMasterElements(selectionSource);
    }

    /**
     * Selectes all elements in the templates with the same hashStrings like the
     * selected elements in the given {@link GraphDocument}. Additionally all
     * instanciation master elements will be selected which are the same (same
     * id) like the master element of a selected instanciation instance element
     * in the model. So if you select an {@link InstanciationEdge} instance in
     * the model the instanciation master element will be selected in the
     * {@link TemplateView}.
     *
     * @param source
     */
    private void selectElementsWithSameHashAndInstaniationMasterElements(final GraphDocument source) {
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
        tree.setSelection();
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
        refresh();
    }

    /**
     *
     */
    public void refresh() {
        checkSearchFieldVisibility();
        if (searchPanel != null) {
            searchPanel.refresh();
        }
    }

    /**
     *
     */
    private void checkSearchFieldVisibility() {
        boolean showSearchPanel = tree.hasContent();
        if (showSearchPanel) {
            if (searchPanel == null) {
                searchPanel = new TemplateTreeSearchOptionsPanel(tree);
                add(searchPanel, BorderLayout.NORTH);
            }
        } else {
            if (searchPanel != null) {
                remove(searchPanel);
                searchPanel = null;
            }
        }
    }

    /**
     *
     */
    public void updateComponents() {
        checkSearchFieldVisibility();
        Container scrolPaneParent = treeScrollPane.getParent();
        if (Static.getSelectedDoc() == null) {
            if (scrolPaneParent != null) {
                remove(treeScrollPane);
            }
        } else {
            if (scrolPaneParent == null) {
                add(treeScrollPane, BorderLayout.CENTER);
            }
        }

    }

}

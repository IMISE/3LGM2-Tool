package de.imise.tool3lgm.gui.menu;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.event.action.SelectedElementsAction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.template.TemplateBrowserTree;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.GDCollectionTreeNode;

/**
 * @author AXS (23.09.2019)
 */
public class TemplateContextGenerator extends ElementSelectionContextGenerator {

    /**
     *
     */
    private GraphDocument selectedTemplate = null;

    /**
     * COMMENTME
     */
    private final JMenuItem properties;

    /**
     *
     */
    public TemplateContextGenerator() {
        properties = getItem(getShowElementPropertyDialogAction());
    }

    @Override
    public JPopupMenu getLayerContextMenu() {
        return null;
    }

    @Override
    public JPopupMenu getNodeContextMenu(final Component source) {
        TemplateBrowserTree tree = (TemplateBrowserTree) source;
        TreePath clickedTreePath = tree.getClickedTreePath();
        if (clickedTreePath == null) {
            return null;
        }
        Set<GraphDocument> selectedTemplates = tree.getSelectedGraphDocuments();
        int selectedTemplatesCount = selectedTemplates.size();
        if (selectedTemplatesCount == 0 || selectedTemplatesCount > 1) { //no template or multiple templates selected -> at the moment do nothing
            return null;
        }
        GraphDocument selectedTemplateDoc = selectedTemplates.iterator().next();
        JPopupMenu menu = createUpdatingPopupMenu();
        Object lastPathComponent = clickedTreePath.getLastPathComponent();
        if (lastPathComponent instanceof ElementContainerTreeNode) {
            selectedTemplate = tree.getGraphDocument(clickedTreePath);
            if (selectedTemplate != null) {
                Static.setSelectedTemplate(selectedTemplate);
                if (!selectedTemplate.isSelectedOnlyBendpoints()) {
                    if (selectedTemplate.isSingleSelection()) {
                        menu = getSingleNodeContextMenu(tree, selectedTemplate);
                    } else if (selectedTemplate.isMultipleSelection()) {
                        menu = getMultiNodeContextMenu(tree, selectedTemplate);
                    }
                }
            }
        } else if (lastPathComponent instanceof GDCollectionTreeNode) {
            GDCollection selectedGDcoll = Static.getSelectedGDCollection();
            GDCollection selectedTemplate = selectedTemplateDoc.getCollection();
            if (selectedGDcoll != null && selectedGDcoll != selectedTemplate) {
                //TODO: hier das Kontextmenü für einen einzelnes Template-Modell zusammenbauen
            }
        }

        return menu;
    }

    @Override
    protected GraphDocument getDoc() {
        return selectedTemplate;
    }

    /**
     *
     */
    private final Action getShowElementPropertyDialogAction() {
        return new SelectedElementsAction(ActionIdentifier.ACTION_SHOW_ELEMENTS_PROPERTY_DIALOG, true) {
            @Override
            public void actionPerformed() {
                GraphDocument activeTemplateDoc = getActiveDoc();
                ElementContainer lastSelectedTemplateElementContainer = activeTemplateDoc.getLastSelected();
                //If the same element exists in the current model, this call opens the
                //property dialog of the element in the model and not in the template.
                //If there is no cops in the model this call shows the dialog of the
                //template element.
                Static.showPropertyDialog(lastSelectedTemplateElementContainer);
            }
        };
    }

    /**
     * @return
     */
    private final JMenuItem createCopyToModelItem() {
        JMenuItem item = new JMenuItem(getResString("inmodel"));
        GraphDocument template = getDoc();
        LGMGraphDocument selectedDoc = Static.getSelectedDoc();
        item.addActionListener(e -> LGMGraphDocument.copySelectedToModel(template, selectedDoc));
        return item;
    }

    /**
     * Kontextmenü eines Einzelknotens
     *
     * @param contextSource
     * @param template
     * @return
     */
    private JPopupMenu getSingleNodeContextMenu(final Component contextSource, final GraphDocument template) {
        //      System.err.println("ContextGenerator.getSingleNodeContextMenu()");
        JPopupMenu menu = createUpdatingPopupMenu();
        ElementContainer ec = template.getLastSelected();
        if (!(ec instanceof BendpointContainer)) {
            addMenuItem(menu, properties);
            ModelElement me = ec.getElement();
            if (!me.isPureTemplateElement()) {
                addMenuItem(menu, createCopyToModelItem());
            }
            //Anlegbare Pfade zu anderen Elementen anbieten
            menu.addSeparator();
            addConnectMenuItems(menu, me);
            addNewInstanciationInstanceMenuItem(menu, me);
        }
        return menu;
    }

    /**
     * @param contextSource
     * @return
     */
    private JPopupMenu getMultiNodeContextMenu(final Component contextSource, final GraphDocument template) {
        //      System.err.println("ContextGenerator.getSingleNodeContextMenu()");
        JPopupMenu menu = createUpdatingPopupMenu();
        ElementContainer ec = template.getLastSelected();
        //        if (!(ec instanceof BendpointContainer)) {
        //            addMenuItem(menu, createCopyToModelItem());
        //        }
        return menu;
    }

}

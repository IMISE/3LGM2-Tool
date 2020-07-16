package de.imise.tool3lgm.gui.menu;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.event.action.SelectedElementsAction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.template.TemplateBrowserTree;

/**
 * @author AXS (23.09.2019)
 */
public class TemplateContextGenerator extends ElementSelectionContextGenerator {

    /**
     *
     */
    private GraphDocumentOwner graphDocumentOwner = null;

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
        JPopupMenu menu = createUpdatingPopupMenu();
        if (source instanceof TemplateBrowserTree) {
            graphDocumentOwner = (TemplateBrowserTree) source;
            GraphDocument template = graphDocumentOwner.getGraphDocument();
            Static.setSelectedTemplate(template);
            if (!template.isSelectedOnlyBendpoints()) {
                if (template.isSingleSelection()) {
                    menu = getSingleNodeContextMenu(source, template);
                } else if (template.isMultipleSelection()) {
                    menu = getMultiNodeContextMenu(source, template);
                }
            }
        }
        return menu;
    }

    @Override
    protected LGMGraphDocument getDoc() {
        return graphDocumentOwner != null ? (LGMGraphDocument) graphDocumentOwner.getGraphDocument() : null;
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

    private final JMenuItem createCopyToModelItem() {
        JMenuItem item = new JMenuItem(getResString("inmodel"));
        LGMGraphDocument template = getDoc();
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
        //        ModelElement me = ec.getElement();
        if (!(ec instanceof BendpointContainer)) {
            addMenuItem(menu, properties);
            addMenuItem(menu, createCopyToModelItem());
            //Anlegbare Pfade zu anderen Elementen anbieten
            ModelElement me = ec.getElement();
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
        //        ModelElement me = ec.getElement();
        if (!(ec instanceof BendpointContainer)) {
            addMenuItem(menu, createCopyToModelItem());
        }
        return menu;
    }

}

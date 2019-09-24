package de.imise.tool3lgm.gui.menu;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.event.action.SelectedElementsAction;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentOwner;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.template.TemplateBrowserTree;

/**
 * @author AXS (23.09.2019)
 */
public class TemplateContextGenerator extends ContextGenerator {

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
        JPopupMenu menu = new JPopupMenu();
        if (source instanceof TemplateBrowserTree) {
            graphDocumentOwner = (TemplateBrowserTree) source;
            GraphDocument template = graphDocumentOwner.getGraphDocument();
            Static.setActiveTemplate(template);
            if (!template.isSelectedOnlyBendpoints()) {
                if (template.isSingleSelection()) {
                    menu = getSingleNodeContextMenu(source, template);
                } else if (template.isMultipleSelection()) {
                    menu = getMultiNodeContextMenu(source);
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
                activeTemplateDoc.showPropertyDialog(false);
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
        JPopupMenu menu = new JPopupMenu();
        ElementContainer ec = template.getLastSelected();
        //        ModelElement me = ec.getElement();
        if (!(ec instanceof BendpointContainer)) {
            addMenuItem(menu, properties);
            addMenuItem(menu, createCopyToModelItem());

            //            Class<? extends ModelElement> meClass = me.getClass();
            //
            //            //Anlegbare Pfade zu anderen Elementen anbieten
            //            JLabel connectLabel = null;
            //            MetaModel metaModel = me.getMetaModel();
            //            for (SimpleMetaPath metaPath : metaModel.getCreatableMetaPaths(meClass)) {
            //                if (connectLabel == null) {
            //                    connectLabel = new JLabel(getResString("LABEL_CONNECT"));
            //                    menu.add(connectLabel);
            //                }
            //                Class<? extends ModelElement> endClass = metaPath.getEndClass();
            //                JMenu pathConnectableElements = new JMenu(metaPath.getName(false, true));
            //                pathConnectableElements.setIcon(link_icon);
            //                GraphDocument doc = getDoc();
            //                List<ModelElement> endElements = doc.getModelItems(endClass, true, true);
            //                pathConnectableElements.setEnabled(!endElements.isEmpty());
            //                menu.add(pathConnectableElements);
            //                for (ModelElement endMe : endElements) {
            //                    Action createPathAction = createPathAction(metaPath, endMe);
            //                    JMenuItem createPathItem = getItem(createPathAction);
            //                    pathConnectableElements.add(createPathItem);
            //                }
            //            }
            //
            //            //InstaciationEdges -> "Neue Instanz" der verbundenen Klasse erzeugen anbieten
            //            JLabel newInstanceLabel = null;
            //            if (!metaModel.isSlaveType(meClass)) {
            //                ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
            //                for (Class<? extends Edge> edgeClass : metaModel.getEdgeTypes(meClass)) {
            //                    if (metaModel.isInstanciationMaster(edgeClass, meClass)) {
            //                        if (newInstanceLabel == null) {
            //                            newInstanceLabel = new JLabel(getResString(MODEL_ACTION_CREATE_INSTANCIATION.name()));
            //                            menu.add(newInstanceLabel);
            //                        }
            //                        String toolTip = elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass);
            //                        Class<? extends ModelElement> endClass = Edge.getEndClass(edgeClass);
            //                        String label = elementsNameBuilder.getDisplayableName(endClass);
            //                        JMenuItem item = getItem(label, MODEL_ACTION_CREATE_INSTANCIATION, edgeClass.getSimpleName(), link_icon, true, toolTip);
            //                        menu.add(item);
            //                    }
            //                }
            //            }
            //
            //            if (newInstanceLabel != null || connectLabel != null) {
            //                menu.addSeparator();
            //            }
            //
            //            JMenu subElems = getSubElemMenu();
            //            if (subElems.getItemCount() > 0) {
            //                menu.add(subElems);
            //                menu.addSeparator();
            //            }
            //
            //            if (!me.isUnique()) {
            //                menu.add(getAddToSzenarioMenu());
            //            }
            //            JMenuItem addToModelMenu = getAddToModelMenu();
            //            if (addToModelMenu != null) {
            //                menu.add(addToModelMenu);
            //            }
            //
            //            if (me.getAssociatedDoc() != null) {
            //                menu.add(selectLinkedSzenario);
            //            }
            //            menu.add(getLinkToSzenarioMenu());
            //            if (me.getAssociatedDoc() != null) {
            //                menu.add(unlinkToSzenario);
            //            }
            //            GraphDocument doc = getDoc();
            //            if (doc instanceof Szenario) {
            //                if (ec instanceof InterLayerConnectedNodeContainer && contextSource instanceof InputGraphArea) {
            //                    menu.addSeparator();
            //                    addMenuItem(menu, show_configs);
            //                    addMenuItem(menu, hide_configs);
            //                }
            //                if (metaModel.hasLayout(me.getClass())) {
            //                    menu.addSeparator();
            //                    if (!ec.isVisible()) {
            //                        menu.add(set_visible);
            //                    } else {
            //                        menu.add(set_invisible);
            //                    }
            //                }
            //                if (contextSource instanceof InputGraphArea) {
            //                    if (me.canHaveParts()) {
            //                        // menu.addSeparator();
            //                        if (me.hasDirectPartContainer(doc)) {
            //                            expand.setEnabled(true);
            //                            collapse.setEnabled(true);
            //                        } else {
            //                            expand.setEnabled(false);
            //                            collapse.setEnabled(false);
            //                        }
            //                        if (!ec.isExpanded()) {
            //                            menu.add(expand);
            //                        } else {
            //                            menu.add(collapse);
            //                        }
            //                    }
            //                    menu.addSeparator();
            //                    menu.add(MenuCollection.LayoutSubMenus.ELEMENT_LAYOUT_MENU);
            //                    menu.add(MenuCollection.LayoutSubMenus.ELEMENT_LEVEL_MENU);
            //                    menu.add(getLayerMenu());
            //                }
            //            }
            //
            //            menu.addSeparator();
            //
            //            // Analysemenü anfügen
            //            menu.add(getAnalysisMenu());
            //
            //            JMenuItem joinMenu = getJoinMenu();
            //            if (joinMenu != null) {
            //                menu.addSeparator();
            //                menu.add(joinMenu);
            //            }
            //
            //            menu.addSeparator();
        }

        return menu;
    }

    /**
     * @param contextSource
     * @return
     */
    private JPopupMenu getMultiNodeContextMenu(final Component contextSource) {
        return null;
    }

}

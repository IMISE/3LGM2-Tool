package de.imise.tool3lgm.graphtools.view.template;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeDefinition;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeModel;
import de.imise.tool3lgm.graphtools.view.tree.DynamicTree;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.gui.menu.ContextGenerator;

/**
 * @author AXS (05.09.2019)
 */
public class TemplateBrowserTree extends DynamicTree implements PropertyChangeListener, AncestorListener {

    /**
     *
     */
    private final PathTreeModel pathTreeModel;

    /**
     *
     */
    private TemplateLibrariesManager templateLibrariesManager;

    /**
     *
     */
    public TemplateBrowserTree() {
        super((TreeModel) null);
        setRootVisible(false);
        setShowsRootHandles(true);
        addAncestorListener(this);
        pathTreeModel = new PathTreeModel(Tool3lgmConstants.getResString("TEMPLATE_BROWSER_NO_TEMPLATES_AVAILABLE"), true);
        setModel(pathTreeModel);
    }

    @Override
    public GraphDocument getGraphDocument() {
        GraphDocument doc = null;
        TreePath leadSelectionPath = selectionModel.getLeadSelectionPath();
        if (leadSelectionPath != null) {
            Object lastPathComponent = leadSelectionPath.getLastPathComponent();
            if (lastPathComponent instanceof ElementContainerTreeNode) {
                ElementContainerTreeNode elementContainerNode = (ElementContainerTreeNode) lastPathComponent;
                doc = elementContainerNode.getGraphDocument();
            }
        }
        return doc;
    }

    @Override
    public ContextGenerator getContextGenerator() {
        return Static.templateContextGenerator;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        MetaModelContext currentMetaModelContext = pathTreeModel.getMetaModelContext();
        MetaModelContext newMetaModelContext = Static.getSelectedMetaModelContext();
        if (Objects.equals(currentMetaModelContext, newMetaModelContext)) {
            return;
        }
        TemplateLibrariesManager templateLibrariesManager = Static.getTemplateLibrariesManager();
        PathTreeDefinition templateTreeDefinition = templateLibrariesManager.getTemplateTreeDefintion(newMetaModelContext);
        setRootVisible(true);
        setSelectionListenerActive(false);
        pathTreeModel.setTreeDefinition(templateTreeDefinition);
        setSelectionListenerActive(true);
        try {
            expandRow(0);
            expandRow(1);
        } catch (Exception e) {
        }
        setRootVisible(false);
    }

    @Override
    public void ancestorAdded(final AncestorEvent event) {
        templateLibrariesManager = Static.getTemplateLibrariesManager();
        templateLibrariesManager.addPropertyChangeListener(this);
    }

    @Override
    public void ancestorRemoved(final AncestorEvent event) {
        templateLibrariesManager.addPropertyChangeListener(this);
    }

    @Override
    public void ancestorMoved(final AncestorEvent event) {
        //do nothing
    }

}

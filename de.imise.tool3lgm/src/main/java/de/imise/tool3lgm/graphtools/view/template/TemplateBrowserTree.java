package de.imise.tool3lgm.graphtools.view.template;

import java.util.Objects;

import javax.swing.JTree;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.tree.TreeModel;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmChangeListener;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeDefinition;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeModel;

/**
 * @author AXS (05.09.2019)
 */
public class TemplateBrowserTree extends JTree implements Tool3lgmChangeListener, AncestorListener {

    /**
     *
     */
    private final PathTreeModel pathTreeModel;

    /**
     *
     */
    public TemplateBrowserTree() {
        super((TreeModel) null);
        setRootVisible(false);
        setShowsRootHandles(true);
        addAncestorListener(this);
        pathTreeModel = new PathTreeModel();
        setModel(pathTreeModel);
    }

    @Override
    public void model_change_selected_szenario_changed(final GraphDocument source) {
        MetaModelContext currentMetaModelContext = pathTreeModel.getMetaModelContext();
        MetaModelContext newMetaModelContext = Static.getSelectedMetaModelContext();
        if (Objects.equals(currentMetaModelContext, newMetaModelContext)) {
            return;
        }
        TemplateLibrariesManager templateLibrariesManager = Static.getTemplateLibrariesManager();
        PathTreeDefinition templateTreeDefinition = templateLibrariesManager.getTemplateTreeDefintion(newMetaModelContext);
        setRootVisible(true);
        pathTreeModel.setTreeDefinition(templateTreeDefinition);
        expandRow(0);
        setRootVisible(false);
    }

    @Override
    public void ancestorAdded(final AncestorEvent event) {
        addAsToolChangeListener();
    }

    @Override
    public void ancestorRemoved(final AncestorEvent event) {
        removeAsToolChangeListener();
    }

    @Override
    public void ancestorMoved(final AncestorEvent event) {
        //do nothing
    }

}

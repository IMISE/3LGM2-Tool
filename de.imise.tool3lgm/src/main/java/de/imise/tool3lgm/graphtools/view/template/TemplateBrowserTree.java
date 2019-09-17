package de.imise.tool3lgm.graphtools.view.template;

import java.util.Objects;

import javax.swing.JTree;
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
public class TemplateBrowserTree extends JTree implements Tool3lgmChangeListener {

    /**
     *
     */
    private final PathTreeModel pathTreeModel;

    /**
     *
     */
    public TemplateBrowserTree() {
        super((TreeModel) null);
        //setRootVisible(false);
        setShowsRootHandles(true);
        addAsToolChangeListener();
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
        pathTreeModel.setTreeDefinition(templateTreeDefinition);
        expandRow(0);
    }

}

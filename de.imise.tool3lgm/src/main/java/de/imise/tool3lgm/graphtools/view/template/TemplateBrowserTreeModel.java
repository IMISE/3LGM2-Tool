package de.imise.tool3lgm.graphtools.view.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibrariesManager;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeModel;
import de.imise.util.BooleanOption;

/**
 * @author AXS (10.12.2020)
 */
public class TemplateBrowserTreeModel extends PathTreeModel {

    /**
     * @param emptyModelInfo
     * @param showElementNamesWithSubmodels
     * @param showAllElements
     */
    public TemplateBrowserTreeModel(final String emptyModelInfo, final boolean showElementNamesWithSubmodels, final BooleanOption showAllElements) {
        super(emptyModelInfo, showElementNamesWithSubmodels, showAllElements);
    }

    @Override
    protected Iterable<GraphDocument> getSourceModels() {
        MetaModelContext metaModelContext = getMetaModelContext();
        TemplateLibrariesManager templateLibrariesManager = Static.getTemplateLibrariesManager();
        Collection<GDCollection> templates = templateLibrariesManager.getTemplates(metaModelContext);
        List<GraphDocument> templateMainModels = new ArrayList<>();
        for (GDCollection template : templates) {
            LGMGraphDocument templateMainDoc = template.getMainDoc();
            templateMainModels.add(templateMainDoc);
        }
        return templateMainModels;
    }

}

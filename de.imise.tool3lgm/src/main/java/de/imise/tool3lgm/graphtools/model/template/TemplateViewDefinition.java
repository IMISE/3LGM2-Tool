package de.imise.tool3lgm.graphtools.model.template;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.path.metapaths.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeBranchDefinition;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeModel;
import de.imise.util.resource.SimpleResourceSource;

/**
 * Definition of the structure of the template browser.
 *
 * @author AXS (27.08.2019)
 */
public abstract class TemplateViewDefinition {

    /**
     * The resource handler to get the localized strings and load icons
     */
    private final SimpleResourceSource resourceHandler;

    /**
     * @param resourceHandler
     */
    public TemplateViewDefinition(final SimpleResourceSource resourceHandler) {
        this.resourceHandler = resourceHandler;
    }

    /**
     * @return Name of the main category of this template library e.g "IHE" or
     *         "HL7". More than one template library can be in the same
     *         category. If <code>null</code> it will be ignored.
     */
    protected String getMainCategoryResStringAndIconKey() {
        return null;
    }

    /**
     * @return the metaPaths with the visible elements for the template browser
     */
    protected abstract List<SequenceMetaPath> getViewMetaPaths();

    /**
     * Converts this view definition with all its view metapaths in a list of
     * the {@link PathTreeBranchDefinition} to show this template in a tree with
     * a {@link PathTreeModel}.
     *
     * @return List of the resulting PathTreeBranchDefinitions
     */
    public final List<PathTreeBranchDefinition> getTreeBranchDefinition() {
        List<PathTreeBranchDefinition> treeBranchDefinitions = new ArrayList<>();
        String mainCategoryResStringAndIconKey = getMainCategoryResStringAndIconKey();
        for (SequenceMetaPath viewMetaPath : getViewMetaPaths()) {
            PathTreeBranchDefinition pathTreeBranchDefinition = new PathTreeBranchDefinition(resourceHandler, viewMetaPath, mainCategoryResStringAndIconKey);
            treeBranchDefinitions.add(pathTreeBranchDefinition);
        }
        return treeBranchDefinitions;
    }

}

package de.imise.tool3lgm.graphtools.model.template;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeBranchDefinition;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeModel;

/**
 * Definition of the structure of the template browser.
 *
 * @author AXS (27.08.2019)
 */
public abstract class TemplateViewDefinition {

    /**
     * @return Name of the main category of this template library e.g "IHE" or "HL7". More than one template library can be in the same category. If
     *         <code>null</code> it will be ignored.
     */
    protected String getMainCategoryName() {
        return null;
    }

    /**
     * @return the metaPaths with the visible elements for the template browser
     */
    protected abstract List<SimpleMetaPath> getViewMetaPaths();

    /**
     * Converts this view definition with all its view metapaths in a list of the
     * {@link PathTreeBranchDefinition} to show this template in a tree with a
     * {@link PathTreeModel}.
     *
     * @return List of the resulting PathTreeBranchDefinitions
     */
    public final List<PathTreeBranchDefinition> getTreeBranchDefinition() {
        List<PathTreeBranchDefinition> treeBranchDefinitions = new ArrayList<>();
        String mainCategoryName = getMainCategoryName();
        for (SimpleMetaPath viewMetaPath : getViewMetaPaths()) {
            PathTreeBranchDefinition pathTreeBranchDefinition = new PathTreeBranchDefinition(viewMetaPath, mainCategoryName);
            treeBranchDefinitions.add(pathTreeBranchDefinition);
        }
        return treeBranchDefinitions;
    }

}

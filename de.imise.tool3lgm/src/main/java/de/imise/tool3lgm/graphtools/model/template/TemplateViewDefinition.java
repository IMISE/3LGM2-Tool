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
     * If this value is not <code>null</code> then only models with a name with
     * this prefix will be displayed with this view definition.
     */
    private final String templatesNamePrefix;

    /**
     * @param resourceHandler
     * @param templatesNamePrefix If this value is not <code>null</code> then
     *            only models with a name with this prefix will be displayed
     *            with this view definition.
     */
    public TemplateViewDefinition(final SimpleResourceSource resourceHandler, String templatesNamePrefix) {
        this.resourceHandler = resourceHandler;
        this.templatesNamePrefix = templatesNamePrefix;
    }

    /**
     * @return Name of the main category of this template library e.g "IHE" or
     *         "HL7". More than one template library can be in the same
     *         category. If <code>null</code> it will be ignored.
     */
    protected Object[] getMainCategoryResStringAndIconKeys() {
        return new Object[0];
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
        Object[] mainCategoryResStringAndIconKey = getMainCategoryResStringAndIconKeys();
        List<SequenceMetaPath> viewMetaPaths = getViewMetaPaths();
        if (viewMetaPaths != null && !viewMetaPaths.isEmpty()) {
            for (SequenceMetaPath viewMetaPath : getViewMetaPaths()) {
                PathTreeBranchDefinition pathTreeBranchDefinition = new PathTreeBranchDefinition(resourceHandler, templatesNamePrefix, viewMetaPath, mainCategoryResStringAndIconKey);
                treeBranchDefinitions.add(pathTreeBranchDefinition);
            }
        } else {
            PathTreeBranchDefinition pathTreeBranchDefinition = new PathTreeBranchDefinition(resourceHandler, templatesNamePrefix, mainCategoryResStringAndIconKey);
            treeBranchDefinitions.add(pathTreeBranchDefinition);
        }
        return treeBranchDefinitions;
    }

}

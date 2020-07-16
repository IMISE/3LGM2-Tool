package de.imise.tool3lgm.graphtools.model.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionPrinter;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeBranchDefinition;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeDefinition;

/**
 * Context for the templates view. It stores a set of templates for every metamodel.
 *
 * @author AXS (09.09.2019)
 */
public class TemplateLibrariesContext {

    /**
     * All metaModelContexts whose templates are loaded. If there are no templates
     * for a metaModelContext the metaModelContext is added nevertheless to indicate
     * that there is nothing to load.
     */
    private final Set<MetaModelContext> metaModelContextsWithLoadedTemplates;

    /** Map with all branches for the template browser tree which should be visible if such an template will be displayed */
    private final Map<GDCollection, PathTreeDefinition> templateToTreeDefinition;

    /**
     *
     */
    public TemplateLibrariesContext() {
        metaModelContextsWithLoadedTemplates = new HashSet<>();
        templateToTreeDefinition = new HashMap<>();
    }

    /**
     * @param template
     * @param treeBranchDefinitions
     */
    public void addTemlate(final GDCollection template, final List<PathTreeBranchDefinition> treeBranchDefinitions) {
        MetaModelContext metaModelContext = template.getMetaModelContext();
        PathTreeDefinition treeDefinition = new PathTreeDefinition(metaModelContext);
        treeDefinition.addBranches(treeBranchDefinitions);
        templateToTreeDefinition.put(template, treeDefinition);
        metaModelContextsWithLoadedTemplates.add(metaModelContext);
    }

    /**
     * Registers this metaModelContext with no available templates. Use this
     * to store the information that for this metaModelContext
     *
     * @param metaModelContext
     */
    public void setNoTemplatesAvailable(final MetaModelContext metaModelContext) {
        metaModelContextsWithLoadedTemplates.add(metaModelContext);
    }

    /**
     * @param metaModelContext
     */
    public void remove(final MetaModelContext metaModelContext) {
        Set<GDCollection> templates = templateToTreeDefinition.keySet();
        for (GDCollection template : templates) {
            if (template.hasMetaModelContext(metaModelContext)) {
                templateToTreeDefinition.remove(template);
            }
        }
        metaModelContextsWithLoadedTemplates.remove(metaModelContext);
    }

    /**
     * @return Iterable f all templates loaded template <code>GDCollection</code>s
     */
    public Iterable<GDCollection> iterableTemplates() {
        return templateToTreeDefinition.keySet();
    }

    /**
     * Returns <code>true</code> if the given metaModelContext is contained in this template context
     *
     * @param metaModelContext
     * @return
     */
    public boolean contains(final MetaModelContext metaModelContext) {
        return metaModelContextsWithLoadedTemplates.contains(metaModelContext);
    }

    /**
     * Clears all templates in the context
     */
    public void clear() {
        metaModelContextsWithLoadedTemplates.clear();
        templateToTreeDefinition.clear();
    }

    /**
     * @return <code>true</code> if the context is empty (no templates added or all removed)
     */
    public boolean isEmpty() {
        return metaModelContextsWithLoadedTemplates.isEmpty();
    }

    /**
     *
     */
    public void print() {
        for (GDCollection template : templateToTreeDefinition.keySet()) {
            GDCollectionPrinter.print(template);
        }
    }

    /**
     * @param metaModelContext
     * @return all templates with the given metaModelContext
     */
    public Collection<GDCollection> getTemplates(final MetaModelContext metaModelContext) {
        ArrayList<GDCollection> templates = new ArrayList<>();
        for (GDCollection template : templateToTreeDefinition.keySet()) {
            if (template.hasMetaModelContext(metaModelContext)) {
                templates.add(template);
            }
        }
        return templates;
    }

    /**
     * @param metaModelContext
     * @return
     */
    public PathTreeDefinition getTemplateTreeDefinition(final MetaModelContext metaModelContext) {
        if (Static.isDummyMetaModelContext(metaModelContext)) {
            return null;
        }
        PathTreeDefinition fullPathTreeDefinition = new PathTreeDefinition(metaModelContext);
        for (GDCollection template : templateToTreeDefinition.keySet()) {
            if (template.hasMetaModelContext(metaModelContext)) {
                PathTreeDefinition pathTreeDefinition = templateToTreeDefinition.get(template);
                fullPathTreeDefinition.addBranches(pathTreeDefinition);
            }
        }
        return fullPathTreeDefinition;
    }

}

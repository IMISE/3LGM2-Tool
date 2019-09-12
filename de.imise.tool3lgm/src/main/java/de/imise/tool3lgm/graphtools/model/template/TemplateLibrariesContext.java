package de.imise.tool3lgm.graphtools.model.template;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionPrinter;

/**
 * Context for the templates view. It stores a set of templates for every metamodel.
 *
 * @author AXS (09.09.2019)
 */
public class TemplateLibrariesContext {

    /** Map with the all templates for a metamodel context */
    private final Multimap<MetaModelContext, GDCollection> metaModelContextToTemplates;

    /**
     *
     */
    public TemplateLibrariesContext() {
        metaModelContextToTemplates = HashMultimap.create(3, 3);
    }

    /**
     * @param template
     */
    public void addTemlate(final GDCollection template) {
        MetaModelContext metaModelContext = template.getMetaModelContext();
        metaModelContextToTemplates.put(metaModelContext, template);
    }

    /**
     * Registers this metaModelContext with no available templates. Use this
     * to store the information that for this metaModelContext
     *
     * @param metaModelContext
     */
    public void setNoTemplatesAvailable(final MetaModelContext metaModelContext) {
        metaModelContextToTemplates.put(metaModelContext, null);
    }

    /**
     * @param metaModelContext
     */
    public void removeTemplates(final MetaModelContext metaModelContext) {
        metaModelContextToTemplates.removeAll(metaModelContext);
    }

    /**
     * Returns <code>true</code> if the given metaModelContext is contained in this template context
     *
     * @param metaModelContext
     * @return
     */
    public boolean contains(final MetaModelContext metaModelContext) {
        Set<MetaModelContext> metaModelContexts = metaModelContextToTemplates.keySet();
        return metaModelContexts.contains(metaModelContext);
    }

    /**
     * Clears all templates in the context
     */
    public void clear() {
        metaModelContextToTemplates.clear();
    }

    /**
     * @return <code>true</code> if the context is empty (no templates added or all removed)
     */
    public boolean isEmpty() {
        return metaModelContextToTemplates.isEmpty();
    }

    public void print() {
        for (MetaModelContext metaModelContext : metaModelContextToTemplates.keys()) {
            for (GDCollection template : metaModelContextToTemplates.get(metaModelContext)) {
                GDCollectionPrinter.print(template);
            }
        }
    }

}

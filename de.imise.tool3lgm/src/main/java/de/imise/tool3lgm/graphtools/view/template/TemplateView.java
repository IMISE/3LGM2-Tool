package de.imise.tool3lgm.graphtools.view.template;

import java.util.Collection;

import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * Interface for all views of templates
 *
 * @author Ich (22.07.2020)
 */
public interface TemplateView {

    /**
     * @return all template models which are visible in this template view
     */
    public Collection<GDCollection> getDisplayedTemplates();

    /**
     * Displays the selection from the templates in the view.
     *
     * @param selectionSource
     */
    public void setSelection();

}

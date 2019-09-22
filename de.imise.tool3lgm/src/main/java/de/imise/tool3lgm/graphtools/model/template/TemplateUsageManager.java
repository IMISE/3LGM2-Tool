package de.imise.tool3lgm.graphtools.model.template;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author AXS (19.09.2019)
 */
public class TemplateUsageManager {

    /**
     *
     */
    private final TemplateUsageDefinition templateUsageDefinition;

    /**
     *
     */
    public TemplateUsageManager(final TemplateUsageDefinition templateUsageDefinition) {
        this.templateUsageDefinition = templateUsageDefinition;
    }

    public List<? extends ModelElement> retainAppliableElements(final List<? extends ModelElement> elements) {

        return elements;

    }

}

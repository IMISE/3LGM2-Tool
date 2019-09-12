package de.imise.tool3lgm.graphtools.model.template;

import java.util.List;

import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;

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
    public String getMainCategoryName() {
        return null;
    }

    /**
     * @return the metaPaths with the visible elements for the template browser
     */
    public abstract List<AbstractMetaPath> getViewMetaPaths();

}

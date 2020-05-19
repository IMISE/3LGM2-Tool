package de.imise.tool3lgm.graphtools.model.template;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.util.SimpleResourceBundleHandler;
import de.imise.util.SimpleResourceBundleSource;

/**
 * Plugin base class for all handlers which load a model as a template library.
 *
 * @author AXS (24.08.2019)
 */
public abstract class TemplateLibraryProvider extends MetaModelSpecificAdapter implements SimpleResourceBundleSource {

    private final SimpleResourceBundleHandler resourceHandler;

    /**
     *
     */
    public TemplateLibraryProvider(final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        super(metaModelDefinitionClass);
        resourceHandler = new SimpleResourceBundleHandler(getClass());
    }

    /**
     * @return display name of this library e.g "IHE (full)" or if the library contains only the ITI part "IHE ITI Domain".
     */
    public String getDisplayName() {
        return null;
    }

    /**
     * @return the model that represents the template
     */
    public abstract GDCollection getTemplateLibrary();

    /**
     * @return the view definition for the template browser.
     */
    public abstract TemplateViewDefinition getViewDefinition();

    /**
     * @return the usage definion for this template
     */
    public abstract TemplateUsageDefinition getUsageDefinition();

    @Override
    public String getResString(final String resKey) {
        try {
            return resourceHandler.getResString(resKey);
        } catch (Exception e) {
            //ignore -> return super.getResString(String)
        }
        return super.getResString(resKey);
    }

}

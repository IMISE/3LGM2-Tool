package de.imise.tool3lgm.graphtools.model.template;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.util.SimpleResourceHandler;
import de.imise.util.SimpleResourceSource;

/**
 * Plugin base class for all handlers which load a model as a template library.
 *
 * @author AXS (24.08.2019)
 */
public abstract class TemplateLibraryProvider extends MetaModelSpecificAdapter implements SimpleResourceSource {

    private final SimpleResourceHandler resourceHandler;

    /**
     *
     */
    public TemplateLibraryProvider(final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        super(metaModelDefinitionClass);
        resourceHandler = new SimpleResourceHandler(getClass());
    }

    /**
     * @return display name of this library e.g "IHE (full)" or if the library contains only the ITI part "IHE ITI Domain".
     */
    public String getDisplayName() {
        return null;
    }

    /**
     * @return the class of the MetaModelDefinition of the template and the target models
     */
    @Override
    public abstract Class<? extends MetaModelDefinition> getMetaModelDefinitionClass();

    /**
     * @return the model that represents the template
     */
    public abstract GDCollection getTemplateLibrary();

    /**
     * @return the view definition for the template browser.
     */
    public abstract TemplateViewDefinition getViewDefinition();

    /**
     * @param metaModelDefinitionClass
     * @return <code>true</code> if the given class is the same or a superclass of of
     *         the metamodel definition class of this server
     */
    public final boolean hasMetaModelDefinitionClass(final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        return metaModelDefinitionClass.isAssignableFrom(getMetaModelDefinitionClass());
    }

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

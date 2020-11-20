package de.imise.tool3lgm.graphtools.model.template;

import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.util.resource.SimpleResourceBundleSourceAdapter;
import de.imise.util.resource.SimpleResourceIconSource;
import de.imise.util.resource.SimpleResourceSource;

/**
 * Plugin base class for all handlers which load a model as a template library.
 *
 * @author AXS (24.08.2019)
 */
public abstract class TemplateLibraryProvider extends MetaModelSpecificAdapter {

    /**
     * Default icon path in the resources to the tree icons for the nodes.
     */
    public static final String TREE_ICONS_PATH_PREFIX = "icon/TREE_ICON_";

    /**
     * Resource handler to get all strings and icons.
     */
    protected final SimpleResourceSource resourceHandler;

    /**
     * @param metaModelDefinitionClass
     */
    public TemplateLibraryProvider(final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        super(metaModelDefinitionClass);
        resourceHandler = new TemplateLibraryProviderResourceHandler(metaModelDefinitionClass, getClass());
    }

    /**
     * @return display name of this library e.g "IHE (full)" or if the library
     *         contains only the ITI part "IHE ITI Domain".
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

    /**
     * Resource handler to get all strings and icons
     *
     * @author AXS (17.06.2020)
     */
    private class TemplateLibraryProviderResourceHandler extends MetaModelSpecificAdapter implements SimpleResourceSource {

        /**
         * Resource handler wit the text for the nodes in the template browser
         */
        private final SimpleResourceBundleSourceAdapter resourceHandler;

        /**
         * @param metaModelDefinitionClass
         * @param resourceNameClass the class the
         *            {@link SimpleResourceBundleSourceAdapter} is initialized
         *            with
         */
        public TemplateLibraryProviderResourceHandler(final Class<? extends MetaModelDefinition> metaModelDefinitionClass, final Class<? extends TemplateLibraryProvider> resourceNameClass) {
            super(metaModelDefinitionClass);
            resourceHandler = new SimpleResourceBundleSourceAdapter(resourceNameClass);

        }

        @Override
        public ImageIcon getIcon(final String name) {
            String iconFileName = TREE_ICONS_PATH_PREFIX + name;
            ImageIcon icon = SimpleResourceIconSource.getImageIcon(iconFileName);
            return icon;
        }

        @Override
        public String getResString(final String resKey) {
            try {
                return resourceHandler.getResString(resKey);
            } catch (Exception e) {
                return super.getResString(resKey);
            }
        }

        @Override
        public ResourceBundle getResourceBundle() {
            return resourceHandler.getResourceBundle();
        }

    }

}

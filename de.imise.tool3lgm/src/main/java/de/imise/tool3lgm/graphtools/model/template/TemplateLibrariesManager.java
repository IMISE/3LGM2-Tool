package de.imise.tool3lgm.graphtools.model.template;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_TEMPLATE_BROWSER;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmChangeListener;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeBranchDefinition;
import de.imise.tool3lgm.graphtools.view.pathtree.PathTreeDefinition;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.event.PropertyChangeHandler;

/**
 * Manager for the loaded templates.
 *
 * @author AXS (11.08.2019)
 */
public class TemplateLibrariesManager extends PropertyChangeHandler implements PropertyChangeListener, Tool3lgmChangeListener {

    /** Data model to store the loaded template libraries */
    private final TemplateLibrariesContext templateLibrariesContext = new TemplateLibrariesContext();

    /**
     *
     */
    private GraphDocument activeTemplate;

    public TemplateLibrariesManager() {
        UserProperties.addPropertyChangeListener(this);
        addAsToolChangeListener();
        loadOrUnloadTemplates();
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (OPTION_SHOW_TEMPLATE_BROWSER.isChanged(evt)) {
            loadOrUnloadTemplates();
        }
    }

    @Override
    public void model_change_selected_szenario_changed(final GraphDocument source) {
        loadOrUnloadTemplates();
    }

    @Override
    public void model_change_model_closed(final GraphDocument source) {
        loadOrUnloadTemplates();
    }

    /**
     * @return the activeTemplate
     */
    public GraphDocument getActiveTemplate() {
        return activeTemplate;
    }

    /**
     * @param activeTemplate the activeTemplate to set
     */
    public void setActiveTemplate(final GraphDocument activeTemplate) {
        this.activeTemplate = activeTemplate;
    }

    /**
     *
     */
    private void loadOrUnloadTemplates() {
        if (!OPTION_SHOW_TEMPLATE_BROWSER.is()) { //unload
            templateLibrariesContext.clear();
        } else { //load
            MetaModelContext selectedMetaModelContext = Static.getSelectedMetaModelContext();
            if (Static.isDummyMetaModelContext(selectedMetaModelContext)) {
                templateLibrariesContext.clear();
                setActiveTemplate(null);
            } else if (!templateLibrariesContext.contains(selectedMetaModelContext)) { //templates already loaded?
                Class<? extends MetaModelDefinition> metaModelDefinitionClass = selectedMetaModelContext.getMetaModelDefinitionClass();
                List<TemplateLibraryProvider> templateLibraryProviders = Static.loadPlugins(TemplateLibraryProvider.class);
                removeUnfittingTemplateLibraryServers(templateLibraryProviders, metaModelDefinitionClass);
                addTemplateLibraries(templateLibraryProviders);

            }
        }
        firePropertyChange();
    }

    /**
     * Removes all template library servers which have a different metamodel definition class
     * than the given class. Same means that the given metamodel definition class must be the
     * same class or a superclass.
     *
     * @param templateLibraryProviders
     * @param metaModelDefinitionClass
     */
    private void removeUnfittingTemplateLibraryServers(final List<TemplateLibraryProvider> templateLibraryProviders, final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        for (int i = templateLibraryProviders.size() - 1; i >= 0; i--) {
            TemplateLibraryProvider templateLibraryServer = templateLibraryProviders.get(i);
            if (!templateLibraryServer.hasMetaModelDefinitionClass(metaModelDefinitionClass)) {
                templateLibraryProviders.remove(i);
            }
        }
    }

    /**
     * @param templateLibraryProviders
     */
    private void addTemplateLibraries(final Iterable<TemplateLibraryProvider> templateLibraryProviders) {
        for (TemplateLibraryProvider templateLibraryServer : templateLibraryProviders) {
            GDCollection templateModel = templateLibraryServer.getTemplateLibrary();
            TemplateViewDefinition templateViewDefinition = templateLibraryServer.getViewDefinition();
            List<PathTreeBranchDefinition> treeBranchDefinition = templateViewDefinition.getTreeBranchDefinition();
            templateLibrariesContext.addTemlate(templateModel, treeBranchDefinition);
        }
    }

    /**
     * @param metaModelContext
     * @return
     */
    public Collection<GDCollection> getTemplates(final MetaModelContext metaModelContext) {
        return templateLibrariesContext.getTemplates(metaModelContext);
    }

    /**
     * @param metaModelContext
     * @return
     */
    public PathTreeDefinition getTemplateTreeDefintion(final MetaModelContext metaModelContext) {
        return templateLibrariesContext.getTemplateTreeDefinition(metaModelContext);
    }

}

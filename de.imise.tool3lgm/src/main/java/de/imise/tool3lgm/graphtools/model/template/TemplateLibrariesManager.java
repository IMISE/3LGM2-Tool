package de.imise.tool3lgm.graphtools.model.template;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_TEMPLATE_BROWSER;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
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
 * @author Ich (03.08.2020)
 */
public class TemplateLibrariesManager extends PropertyChangeHandler implements PropertyChangeListener, Tool3lgmChangeListener {

    /** Data model to store the loaded template libraries */
    private final TemplateLibrariesContext templateLibrariesContext = new TemplateLibrariesContext();

    /**
     *
     */
    private GraphDocument activeTemplate;

    /**
     *
     */
    private MetaModelContext activeMetaModelContext;

    /**
     *
     */
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
        activeMetaModelContext = source == null ? null : source.getMetaModelContext();
    }

    @Override
    public void model_change_model_closed(final GraphDocument source) {
        loadOrUnloadTemplates(); //do not remove because model_change_selected_szenario_changed(...) is never called if the last model was closed!
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
     * @return
     */
    public Collection<GDCollection> getAllActiveTemplates() {
        if (activeMetaModelContext == null) {
            return new ArrayList<>(0);
        }
        return getTemplates(activeMetaModelContext);
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
                List<TemplateLibraryProvider> templateLibraryProviders = getTemplateLibraryProviders();
                addTemplateLibraries(templateLibraryProviders);
            }
        }
        removeSuperfluousTemplates();
        firePropertyChange();
    }

    /**
     * When all models of a certain type are closed, the templates
     * for that model type can be removed.
     */
    private void removeSuperfluousTemplates() {
        for (MetaModelContext templateMetaModelContext : templateLibrariesContext.getModelTypesWithTemplates()) {
            boolean isModelOpenWithSameMetaModel = false;
            for (GDCollection gdcoll : Static.iterableCollections()) {
                MetaModelContext metaModelContext = gdcoll.getMetaModelContext();
                if (templateMetaModelContext == metaModelContext) {
                    isModelOpenWithSameMetaModel = true;
                    break;
                }
            }
            if (!isModelOpenWithSameMetaModel) {
                templateLibrariesContext.remove(templateMetaModelContext);
            }
        }
    }

    /**
     * @return all {@link TemplateLibraryProvider} which fitting the currently selectd model type
     */
    private List<TemplateLibraryProvider> getTemplateLibraryProviders() {
        MetaModelContext selectedMetaModelContext = Static.getSelectedMetaModelContext();
        Class<? extends MetaModelDefinition> metaModelDefinitionClass = selectedMetaModelContext.getMetaModelDefinitionClass();
        List<TemplateLibraryProvider> templateLibraryProviders = Static.loadPlugins(TemplateLibraryProvider.class);
        removeUnfittingTemplateLibraryProviders(templateLibraryProviders, metaModelDefinitionClass);
        return templateLibraryProviders;
    }

    /**
     * @return <code>true</code> if there are available templates for the currently selected model type
     */
    public final boolean hasAvailableTemplatesForCurrentModelType() {
        MetaModelContext selectedMetaModelContext = Static.getSelectedMetaModelContext();
        if (templateLibrariesContext.contains(selectedMetaModelContext)) {
            return true;
        }
        return !getTemplateLibraryProviders().isEmpty();
    }

    /**
     * Removes all template library providers which have a different metamodel definition class
     * than the given class. Same means that the given metamodel definition class must be the
     * same class or a superclass.
     *
     * @param templateLibraryProviders
     * @param metaModelDefinitionClass
     */
    private void removeUnfittingTemplateLibraryProviders(final List<TemplateLibraryProvider> templateLibraryProviders, final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        for (int i = templateLibraryProviders.size() - 1; i >= 0; i--) {
            TemplateLibraryProvider templateLibraryProvider = templateLibraryProviders.get(i);
            if (!templateLibraryProvider.hasMetaModelDefinitionClass(metaModelDefinitionClass)) {
                templateLibraryProviders.remove(i);
            }
        }
    }

    /**
     * @param templateLibraryProviders
     */
    private void addTemplateLibraries(final Iterable<TemplateLibraryProvider> templateLibraryProviders) {
        for (TemplateLibraryProvider templateLibraryProvider : templateLibraryProviders) {
            GDCollection templateModel = templateLibraryProvider.getTemplateLibrary();
            TemplateViewDefinition templateViewDefinition = templateLibraryProvider.getViewDefinition();
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

    /**
     * @return Iterable of all loaded template <code>GDCollection</code>s
     */
    public Iterable<GDCollection> iterableTemplates() {
        return templateLibrariesContext.iterableTemplates();
    }

}

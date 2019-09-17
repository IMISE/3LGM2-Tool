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

/**
 * Manager for the loaded templates.
 *
 * @author AXS (11.08.2019)
 */
public class TemplateLibrariesManager implements PropertyChangeListener, Tool3lgmChangeListener {

    /** Data model to store the loaded template libraries */
    private final TemplateLibrariesContext templateLibrariesContext = new TemplateLibrariesContext();

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

    /**
    *
    */
    private void loadOrUnloadTemplates() {
        //unload
        if (!OPTION_SHOW_TEMPLATE_BROWSER.is()) {
            templateLibrariesContext.clear();
            return;
        }
        //load
        MetaModelContext selectedMetaModelContext = Static.getSelectedMetaModelContext();
        //tamplates already loaded?
        if (templateLibrariesContext.contains(selectedMetaModelContext)) {
            return;
        }
        Class<? extends MetaModelDefinition> metaModelDefinitionClass = selectedMetaModelContext.getMetaModelDefinitionClass();
        List<TemplateLibraryProvider> templateLibraryServers = Static.loadPlugins(TemplateLibraryProvider.class);
        removeUnfittingTemplateLibraryServers(templateLibraryServers, metaModelDefinitionClass);
        addTemplateLibraries(templateLibraryServers);
    }

    /**
     * Removes all template library servers which have a different metamodel definition class
     * than the given class. Same means that the given metamodel definition class must be the
     * same class or a superclass.
     *
     * @param templateLibraryServers
     * @param metaModelDefinitionClass
     */
    private void removeUnfittingTemplateLibraryServers(final List<TemplateLibraryProvider> templateLibraryServers, final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        for (int i = templateLibraryServers.size() - 1; i >= 0; i--) {
            TemplateLibraryProvider templateLibraryServer = templateLibraryServers.get(i);
            if (!templateLibraryServer.hasMetaModelDefinitionClass(metaModelDefinitionClass)) {
                templateLibraryServers.remove(i);
            }
        }
    }

    /**
     * @param templateLibraryServers
     */
    private void addTemplateLibraries(final Iterable<TemplateLibraryProvider> templateLibraryServers) {
        for (TemplateLibraryProvider templateLibraryServer : templateLibraryServers) {
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

package de.imise.tool3lgm.graphtools.model.template;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_TEMPLATE_BROWSER_SHOW;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmChangeListener;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
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
        if (OPTION_TEMPLATE_BROWSER_SHOW.isChanged(evt)) {
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
        //        Sys.err1("BEFORE");
        //        templateLibrariesContext.print();
        //unload
        if (!OPTION_TEMPLATE_BROWSER_SHOW.is()) {
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
        List<TemplateLibraryServer> templateLibraryServers = Static.loadPlugins(TemplateLibraryServer.class);
        removeUnfittingTemplateLibraryServers(templateLibraryServers, metaModelDefinitionClass);
        addTemplateLibraries(templateLibraryServers);
        //        Sys.err1("AFTER");
        //        templateLibrariesContext.print();
    }

    /**
     * @param templateLibraryServers
     */
    private void removeUnfittingTemplateLibraryServers(final List<TemplateLibraryServer> templateLibraryServers, final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        for (int i = templateLibraryServers.size() - 1; i >= 0; i--) {
            TemplateLibraryServer templateLibraryServer = templateLibraryServers.get(i);
            if (!templateLibraryServer.hasMetaModelDefinitionClass(metaModelDefinitionClass)) {
                templateLibraryServers.remove(i);
            }
        }
    }

    private void addTemplateLibraries(final Iterable<TemplateLibraryServer> templateLibraryServers) {
        for (TemplateLibraryServer templateLibraryServer : templateLibraryServers) {
            GDCollection templateModel = templateLibraryServer.getTemplateLibrary();
            templateLibrariesContext.addTemlate(templateModel);
        }
    }

}

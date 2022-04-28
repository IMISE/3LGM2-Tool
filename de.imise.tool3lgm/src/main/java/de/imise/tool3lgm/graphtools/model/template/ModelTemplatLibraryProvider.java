package de.imise.tool3lgm.graphtools.model.template;

import java.io.File;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.model.GDCollection;

public class ModelTemplatLibraryProvider extends TemplateLibraryProvider {

    private final File file;

    /**
     * @param metaModelContext
     * @param templateFile
     */
    public ModelTemplatLibraryProvider(MetaModelContext metaModelContext, File templateFile) {
        super(metaModelContext.getMetaModelDefinitionClass());
        file = templateFile;
    }

    @Override
    public GDCollection getTemplateLibrary() {
        return null;
    }

    @Override
    public TemplateViewDefinition getViewDefinition() {
        return null;
    }

    @Override
    public TemplateUsageDefinition getUsageDefinition() {
        return null;
    }

    @Override
    public String toString() {
        return file.getPath();
    }

}

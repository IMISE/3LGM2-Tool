package de.imise.tool3lgm.graphtools.model.template;

import static de.imise.tool3lgm.Tool3lgmModelType.ModelCategory.TEMPLATE;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionFileHandler;
import de.imise.tool3lgm.graphtools.path.metapaths.SequenceMetaPath;

/**
 * @author AXS (28.04.2022)
 */
public class ModelTemplatLibraryProvider extends TemplateLibraryProvider {

    /**
     *
     */
    private final File templateFile;

    /**
     *
     */
    private final GDCollection gdcoll;

    /**
     *
     */
    private boolean couldBeLoaded = false;

    /**
     * @param metaModelContext
     * @param templateFile
     */
    public ModelTemplatLibraryProvider(MetaModelContext metaModelContext, File templateFile) {
        super(metaModelContext.getMetaModelDefinitionClass());
        this.templateFile = templateFile;
        gdcoll = loadTemplateFile();
    }

    /**
     *
     */
    private GDCollection loadTemplateFile() {
        GDCollection gdcoll = new GDCollection();
        GDCollectionFileHandler fileHandler = gdcoll.getFileHandler();
        try {
            fileHandler.setFile(templateFile);
            couldBeLoaded = fileHandler.loadFromRAF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        gdcoll.setModelCategory(TEMPLATE);
        gdcoll.finishInit();
        return gdcoll;
    }

    @Override
    public GDCollection getTemplateLibrary() {
        return gdcoll;
    }

    @Override
    public TemplateViewDefinition getViewDefinition() {
        return new TemplateViewDefinition(null, null) {
            @Override
            protected List<?> getMainCategoryResStringAndIconKeys() {
                List<File> templateDirectories = TemplateLibrariesManager.getTemplateDirectories();
                String templateFilePath = templateFile.getPath();
                for (File templateDirectory : templateDirectories) {
                    String templateDirectoryPath = templateDirectory.getPath();
                    if (!templateDirectoryPath.endsWith(File.separator)) {
                        templateDirectoryPath += File.separator;
                    }
                    if (templateFilePath.startsWith(templateDirectoryPath)) {
                        templateFilePath = templateFilePath.substring(templateDirectoryPath.length());
                        break;
                    }
                }
                String[] modelTemplateFileHieraryNames = templateFilePath.split("\\" + File.separator);
                List<Object> modelTemplateFileHierary = new ArrayList<>(Arrays.asList(modelTemplateFileHieraryNames));
                modelTemplateFileHierary.set(modelTemplateFileHierary.size() - 1, gdcoll);
                return modelTemplateFileHierary;
            }

            @Override
            protected List<SequenceMetaPath> getViewMetaPaths() {
                return ImmutableList.of();
            }

        };
    }

    @Override
    public TemplateUsageDefinition getUsageDefinition() {
        return null;
    }

    @Override
    public String toString() {
        return templateFile.getPath();
    }

    /**
     * @return
     */
    public boolean couldBeLoaded() {
        return couldBeLoaded;
    }

    /**
     * @return
     */
    public boolean isValid() {
        return couldBeLoaded(); //maybe in future there are some more criterias for validity such as hasValidUsageDefinition
    }

}

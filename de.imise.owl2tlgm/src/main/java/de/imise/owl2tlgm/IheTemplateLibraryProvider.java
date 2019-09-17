package de.imise.owl2tlgm;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmMetaModelContext;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverter;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.template.TemplateLibraryProvider;
import de.imise.tool3lgm.graphtools.model.template.TemplateViewDefinition;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.service.TLGMServiceMetaModel;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheDomain;

/**
 * Function to load a {@link TLGMServiceMetaModel} template and the definition of the corresponding view of the template browser.
 *
 * @author AXS (30.08.2019)
 */
public class IheTemplateLibraryProvider extends TemplateLibraryProvider {

    @Override
    public String getDisplayName() {
        return super.getDisplayName();
    }

    @Override
    public TemplateViewDefinition getViewDefinition() {
        return new TemplateViewDefinition() {

            @Override
            public String getMainCategoryName() {
                return "IHE (auslagern!!!)";
            }

            @Override
            public List<SimpleMetaPath> getViewMetaPaths() {
                MetaModelContext metaModelContext = Tool3lgmMetaModelContext.getMetaModelContextForDefinitionClass(getMetaModelDefinitionClass());
                MetaModel metaModel = metaModelContext.getMetaModel();
                SimpleMetaPath submodelMetaPath = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, IheDomain.class, IheActor.class, IheIntegrationProfile_IheDomain_Edge.class, IheIntegrationProfile_IheActor_Edge.class);
                return ImmutableList.of(submodelMetaPath);
            }

        };
    }

    @Override
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return TLGMServiceMetaModel.class;
    }

    @Override
    public GDCollection getTemplateLibrary() {
        IheRDFDataImporter dataImporter = Static.loadPlugin(IheRDFDataImporter.class);
        IheModelConverterDefinition modelConverterDefinition = Static.loadPlugin(IheModelConverterDefinition.class);
        if (dataImporter.startImport(ModelCategory.TEMPLATE)) {
            GDCollection gdcoll = dataImporter.getCollection();
            if (modelConverterDefinition != null) {
                gdcoll = ModelConverter.convert(modelConverterDefinition, gdcoll);
            }
            gdcoll.setModelCategory(ModelCategory.TEMPLATE);
            return gdcoll;
        }
        return null;
    }

}

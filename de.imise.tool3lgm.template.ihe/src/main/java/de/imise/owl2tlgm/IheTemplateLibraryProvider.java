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
import de.imise.tool3lgm.graphtools.model.template.TemplateUsageDefinition;
import de.imise.tool3lgm.graphtools.model.template.TemplateViewDefinition;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.service.TLGMServiceMetaModel;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheDomain;
import de.imise.tool3lgm.metamodel.service.node.IheIntegrationProfile;
import de.imise.tool3lgm.metamodel.service.node.IheInvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheProvidingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheTransaction;

/**
 * Function to load a {@link TLGMServiceMetaModel} template and the definition of the corresponding view of the template browser.
 *
 * @author AXS (30.08.2019)
 */
public class IheTemplateLibraryProvider extends TemplateLibraryProvider {

    /**
     *
     */
    public IheTemplateLibraryProvider() {
        super(TLGMServiceMetaModel.class);
    }

    @Override
    public String getDisplayName() {
        return super.getDisplayName();
    }

    @Override
    public TemplateViewDefinition getViewDefinition() {
        return new TemplateViewDefinition() {

            @Override
            public String getMainCategoryName() {
                return getResString("MAIN_CATEGORY_NAME");
            }

            @Override
            public List<SimpleMetaPath> getViewMetaPaths() {
                MetaModelContext metaModelContext = Tool3lgmMetaModelContext.getMetaModelContextForDefinitionClass(getMetaModelDefinitionClass());
                MetaModel metaModel = metaModelContext.getMetaModel();
                SimpleMetaPath submodelMetaPath = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, IheDomain.class, IheTransaction.class, IheIntegrationProfile_IheDomain_Edge.class, IheIntegrationProfile_IheActor_Edge.class,
                        IheActor_IheInterface_Edge.class, IheInterface_IheTransaction_Edge.class);
                return ImmutableList.of(submodelMetaPath);
            }

        };
    }

    @Override
    public TemplateUsageDefinition getUsageDefinition() {
        return new TemplateUsageDefinition() {
            @Override
            public void init() {
                addAppliableElementAndCopyDependencies(IheDomain.class, IheIntegrationProfile_IheDomain_Edge.class);
                addAppliableElementAndCopyDependencies(IheIntegrationProfile.class, IheIntegrationProfile_IheActor_Edge.class);
                addAppliableElementAndCopyDependencies(IheActor.class, IheActor_IheInterface_Edge.class);
                addAppliableElementAndCopyDependencies(IheInvokingInterface.class, IheInvokingInterface_IheTransaction_Edge.class);
                addAppliableElementAndCopyDependencies(IheProvidingInterface.class, IheProvidingInterface_IheTransaction_Edge.class);
                addAppliableElements(IheTransaction.class);
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

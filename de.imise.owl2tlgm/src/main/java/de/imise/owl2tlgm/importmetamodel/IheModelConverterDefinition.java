package de.imise.owl2tlgm.importmetamodel;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import de.imise.owl2tlgm.importmetamodel.edge.IheDomain_Edge;
import de.imise.owl2tlgm.importmetamodel.edge.IheIntegrationProfile_Edge;
import de.imise.owl2tlgm.importmetamodel.edge.IheTransaction_Edge;
import de.imise.owl2tlgm.importmetamodel.node.Actor;
import de.imise.owl2tlgm.importmetamodel.node.Domain;
import de.imise.owl2tlgm.importmetamodel.node.IntegrationProfile;
import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.service.TLGMServiceMetaModel;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheDomain;
import de.imise.tool3lgm.metamodel.service.node.IheIntegrationProfile;

/**
 * @author AXS (10 Jun 2019)
 */
public class IheModelConverterDefinition extends ModelConverterDefinition {

    /**
     *
     */
    public IheModelConverterDefinition() {
        super(IheImportMetaModelDefinition.class, TLGMServiceMetaModel.class);
    }

    @Override
    public Map<Class<? extends Node>, Class<? extends Node>> getDirectMappingNodeClasses() {
        //Actor -> IheActor   Domain -> IheDomain    IntegrationProfile -> IheIntegrationProfile
        return ImmutableMap.of(Actor.class, IheActor.class, Domain.class, IheDomain.class, IntegrationProfile.class, IheIntegrationProfile.class);
    }

    @Override
    public Map<Class<? extends Edge>, Class<? extends Edge>> getDirectMappingEdgeClasses() {
        //IntegrationProfile_Edge -> IheIntegrationProfile_IheActor_Edge
        return ImmutableMap.of(IheIntegrationProfile_Edge.class, IheIntegrationProfile_IheActor_Edge.class);
    }

    @Override
    public Map<Class<? extends Edge>, Class<? extends Edge>> getDirectMappingSwitchedEdgeClasses() {
        //IheDomain_Edge -> IheIntegrationProfile_IheDomain_Edge
        return ImmutableMap.of(IheDomain_Edge.class, IheIntegrationProfile_IheDomain_Edge.class);
    }

    @Override
    public Map<Class<? extends Edge>, SimpleMetaPath> getEdgesMappingMetaPaths() {
        MetaModelContext serviceMetaModelContext = new MetaModelContext(getTargetMetaModelDefinitionClass());
        MetaModel serviceMetaModel = serviceMetaModelContext.getMetaModel();
        //IHE Actor besitzt IHE Schnittstelle + IHE Schnittstelle (aufrufend) ruft auf ( <- ) IHE Transaction + IHE Transaction wird bereitsgestellt durch ( <- ) IHE Schnittstelle (bereitstellend) + IHE Schnittstelle gehört zu IHE Actor
        SimpleMetaPath actorTransactionActorMetaPath = SimpleMetaPathCreator.createSimpleMetaPath(serviceMetaModel, IheActor.class, IheActor.class, IheActor_IheInterface_Edge.class, IheInvokingInterface_IheTransaction_Edge.class,
                IheProvidingInterface_IheTransaction_Edge.class, IheActor_IheInterface_Edge.class);
        return ImmutableMap.of(IheTransaction_Edge.class, actorTransactionActorMetaPath);
    }

}

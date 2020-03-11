package de.imise.tool3lgm.metamodel.service;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InferenceEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.meta.SectionMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstance_IheActorInstanceInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheCommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheActorInstanceInvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_IheActorInstanceProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheTransaction_IheCommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_InvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_ProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.node.ApplicationSystem;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstance;
import de.imise.tool3lgm.metamodel.service.node.IheInvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheProvidingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheTransaction;
import de.imise.tool3lgm.metamodel.service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.ProvidingInterface;
import de.imise.tool3lgm.metamodel.service.node.Service;
import de.imise.tool3lgm.metamodel.service.node.SoftwareProduct;

/**
 * In this definition all pathes of the metamodel are defined. The {@link MetaModelDefinition}
 * should/can not contain any metapath definitions because itself is needed to created any metapath.
 * This definition here is loaded lazy by the {@link MetaPathDefinition} so the basic metamodel
 * object exists while creating the metapaths.
 *
 * @author AXS (27 May 2019)
 */
public class TLGMServiceMetaPathsDefinition extends MetaPathDefinition {

    /**
     * @param metaModel
     */
    public TLGMServiceMetaPathsDefinition(final MetaModel metaModel) {
        super(metaModel);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Bedingungspfade für Kanten (siehe Beschreibung getConditionMetaPath() //
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public final Map<Class<? extends Edge>, SimpleMetaPath> getConditionMetaPaths() {
        ImmutableMap.Builder<Class<? extends Edge>, SimpleMetaPath> builder = ImmutableMap.builder();
        //IheInvokingInterface_InvokingInterface_Edge
        builder.put(IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class, smp(Edge.getStartClass(IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class),
                Edge.getEndClass(IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class), IheActor_IheInterface_Edge.class, IheActor_IheActorInstance_Edge.class, IheActorInstance_IheActorInstanceInterface_Edge.class));
        //IheProvidingInterface_ProvidingInterface_Edge
        builder.put(IheProvidingInterface_IheActorInstanceProvidingInterface_Edge.class, smp(Edge.getStartClass(IheProvidingInterface_IheActorInstanceProvidingInterface_Edge.class),
                Edge.getEndClass(IheProvidingInterface_IheActorInstanceProvidingInterface_Edge.class), IheActor_IheInterface_Edge.class, IheActor_IheActorInstance_Edge.class, IheActorInstance_IheActorInstanceInterface_Edge.class));
        return builder.build();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    // Ebenfalls mitzuinstanziierende Pfade bei der Instanziierung über eine InstanciationEdge //
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Multimap<Class<? extends InstanciationEdge>, SimpleMetaPath> getInstanciationEdgeToAdditionalInstanciationMetaPaths() {
        ImmutableListMultimap.Builder<Class<? extends InstanciationEdge>, SimpleMetaPath> builder = ImmutableListMultimap.builder();
        //IheActor_IheActorInstance_Edge
        builder.put(IheActor_IheActorInstance_Edge.class,
                smp(IheActor.class, IheActorInstance.class, IheActor_IheInterface_Edge.class, IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class, IheActorInstance_IheActorInstanceInterface_Edge.class));
        builder.put(IheActor_IheActorInstance_Edge.class,
                smp(IheActor.class, IheActorInstance.class, IheActor_IheInterface_Edge.class, IheProvidingInterface_IheActorInstanceProvidingInterface_Edge.class, IheActorInstance_IheActorInstanceInterface_Edge.class));
        return builder.build();
    }
    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    @Override
    public Collection<SimpleMetaPath> getCreatablePaths() {
        SimpleMetaPath path1 = smp(ApplicationSystem.class, IheActor.class, "PATH_ApplicationSystem_IheActor", ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
        return ImmutableList.of(path1);
    }

    ////////////////////////////////////////////////////////////////////////
    // Map auf die in der Grafik anzuzeigenden Namen verbundener Elemente //
    ////////////////////////////////////////////////////////////////////////

    @Override
    public Map<Class<? extends ModelElement>, AbstractMetaPath> getElementClassToNameExtensionPath() {
        SimpleMetaPath applicationsSystemNameExtensionPath = smp(ApplicationSystem.class, SoftwareProduct.class, ApplicationSystem_SoftwareProduct_Edge.class);
        SimpleMetaPath iheActorInstanceNameExtensionPath = smp(IheActorInstance.class, IheActor.class, IheActor_IheActorInstance_Edge.class);
        return ImmutableMap.of(ApplicationSystem.class, applicationsSystemNameExtensionPath, IheActorInstance.class, iheActorInstanceNameExtensionPath);
    }

    @Override
    public Map<Class<? extends Edge>, AbstractMetaPath> getEdgeClassToInitialCreatedNameSourcePath() {
        MetaModel metaModel = getMetaModel();
        ElementaryMetaPathHandler emph = metaModel.getElementaryMetaPathHandler();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // IheCommunicationLink_Edge -> bekommt Name der Transaktion, die über ihre Schnittstellen verbunden ist //
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Path: IheCommunicationLink_Edge -> IheCommunicationLink_Edge-StartElement = IheInvokingInterface ->  IheTransaction_IheInvokingInterface_Edge -> IheTransaction
        ElementaryMetaPath iheCommunicationLink_nameMetaPath1_pathStep1 = emph.getEdgeToStartElementMetaPath(IheCommunicationLink_Edge.class, IheInvokingInterface.class);
        ElementaryMetaPath iheCommunicationLink_nameMetaPath1_pathStep2 = emph.getMetaPath(IheInvokingInterface.class, IheInvokingInterface_IheTransaction_Edge.class, Direction.FORWARD, IheTransaction.class);
        SimpleMetaPath iheCommunicationLink_nameMetaPath1 = new SimpleMetaPath(iheCommunicationLink_nameMetaPath1_pathStep1, iheCommunicationLink_nameMetaPath1_pathStep2);

        //Path: IheCommunicationLink_Edge -> IheCommunicationLink_Edge-EndElement = IheProvidingInterface ->  IheTransaction_IheProvidingInterface_Edge -> IheTransaction
        ElementaryMetaPath iheCommunicationLink_nameMetaPath2_pathStep1 = emph.getEdgeToEndElementMetaPath(IheCommunicationLink_Edge.class, IheProvidingInterface.class);
        ElementaryMetaPath iheCommunicationLink_nameMetaPath2_pathStep2 = emph.getMetaPath(IheProvidingInterface.class, IheProvidingInterface_IheTransaction_Edge.class, Direction.FORWARD, IheTransaction.class);
        SimpleMetaPath iheCommunicationLink_nameMetaPath2 = new SimpleMetaPath(iheCommunicationLink_nameMetaPath2_pathStep1, iheCommunicationLink_nameMetaPath2_pathStep2);

        AbstractMetaPath iheCommunicationLink_nameMetaPath = new SectionMetaPath(iheCommunicationLink_nameMetaPath1, iheCommunicationLink_nameMetaPath2);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // CommunicationLink_Edge -> bekommt Name der Transaktion, die über ihre Schnittstellen verbunden ist //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Path: CommunicationLink_Edge -> CommunicationLink_Edge-StartElement = InvokingInterface ->  IheInvokingInterface_InvokingInterface_Edge -> IheInvokingInterface -> IheTransaction_IheInvokingInterface_Edge -> IheTransaction
        ElementaryMetaPath communicationLink_nameMetaPath1_pathStep1 = emph.getEdgeToStartElementMetaPath(CommunicationLink_Edge.class, InvokingInterface.class);
        ElementaryMetaPath communicationLink_nameMetaPath1_pathStep2 = emph.getMetaPath(InvokingInterface.class, IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class, Direction.BACKWARD, IheInvokingInterface.class);
        ElementaryMetaPath communicationLink_nameMetaPath1_pathStep3 = emph.getMetaPath(IheInvokingInterface.class, IheInvokingInterface_IheTransaction_Edge.class, Direction.FORWARD, IheTransaction.class);
        SimpleMetaPath communicationLink_nameMetaPath1 = new SimpleMetaPath(communicationLink_nameMetaPath1_pathStep1, communicationLink_nameMetaPath1_pathStep2, communicationLink_nameMetaPath1_pathStep3);

        //Path: CommunicationLink_Edge -> CommunicationLink_Edge-EndElement = ProvidingInterface ->  IheProvidingInterface_ProvidingInterface_Edge -> IheProvidingInterface -> IheTransaction_IheProvidingInterface_Edge -> IheTransaction
        ElementaryMetaPath communicationLink_nameMetaPath2_pathStep1 = emph.getEdgeToEndElementMetaPath(CommunicationLink_Edge.class, ProvidingInterface.class);
        ElementaryMetaPath communicationLink_nameMetaPath2_pathStep2 = emph.getMetaPath(ProvidingInterface.class, IheProvidingInterface_IheActorInstanceProvidingInterface_Edge.class, Direction.BACKWARD, IheProvidingInterface.class);
        ElementaryMetaPath communicationLink_nameMetaPath2_pathStep3 = emph.getMetaPath(IheProvidingInterface.class, IheProvidingInterface_IheTransaction_Edge.class, Direction.FORWARD, IheTransaction.class);
        SimpleMetaPath communicationLink_nameMetaPath2 = new SimpleMetaPath(communicationLink_nameMetaPath2_pathStep1, communicationLink_nameMetaPath2_pathStep2, communicationLink_nameMetaPath2_pathStep3);

        AbstractMetaPath communicationLink_nameMetaPath = new SectionMetaPath(communicationLink_nameMetaPath1, communicationLink_nameMetaPath2);
        return ImmutableMap.of(IheCommunicationLink_Edge.class, iheCommunicationLink_nameMetaPath, CommunicationLink_Edge.class, communicationLink_nameMetaPath);
    }

    ////////////////////
    // InferenceEgdes //
    ////////////////////

    /**
     * @return
     */
    @Override
    public final Map<Class<? extends InferenceEdge>, AbstractMetaPath> getInferenceEdgeToConditionMetaPath() {
        //        SimpleMetaPath service_CommunicationLink_InferenceMetaPath_part1 = simpleMetaPathCreator.createSimpleMetaPath(CommunicationLink_Edge.class, Service.class, Service_ProvidingInterface_Edge.class);
        //        SimpleMetaPath service_CommunicationLink_InferenceMetaPath_part2 = simpleMetaPathCreator.createSimpleMetaPath(CommunicationLink_Edge.class, Service.class, Service_InvokingInterface_Edge.class);
        MetaModel metaModel = getMetaModel();
        ElementaryMetaPathHandler emph = metaModel.getElementaryMetaPathHandler();

        //Path: CommunicationLink_Edge -> CommunicationLink_Edge-StartElement = InvokingInterface ->  Service_InvokingInterface_Edge -> Service
        ElementaryMetaPath service_CommunicationLink_InferenceMetaPath1_pathStep1 = emph.getEdgeToStartElementMetaPath(CommunicationLink_Edge.class, InvokingInterface.class);
        ElementaryMetaPath service_CommunicationLink_InferenceMetaPath1_pathStep2 = emph.getMetaPath(InvokingInterface.class, Service_InvokingInterface_Edge.class, Direction.BACKWARD, Service.class);
        SimpleMetaPath service_CommunicationLink_InferenceMetaPath1 = new SimpleMetaPath(service_CommunicationLink_InferenceMetaPath1_pathStep1, service_CommunicationLink_InferenceMetaPath1_pathStep2);

        //Path: CommunicationLink_Edge -> CommunicationLink_Edge-EndElement = ProvidingInterface ->  Service_InvokingInterface_Edge -> Service
        ElementaryMetaPath service_CommunicationLink_InferenceMetaPath2_pathStep1 = emph.getEdgeToEndElementMetaPath(CommunicationLink_Edge.class, ProvidingInterface.class);
        ElementaryMetaPath service_CommunicationLink_InferenceMetaPath2_pathStep2 = emph.getMetaPath(InvokingInterface.class, Service_ProvidingInterface_Edge.class, Direction.BACKWARD, Service.class);
        SimpleMetaPath service_CommunicationLink_InferenceMetaPath2 = new SimpleMetaPath(service_CommunicationLink_InferenceMetaPath2_pathStep1, service_CommunicationLink_InferenceMetaPath2_pathStep2);

        AbstractMetaPath service_CommunicationLink_InferenceMetaPath = new SectionMetaPath(service_CommunicationLink_InferenceMetaPath1, service_CommunicationLink_InferenceMetaPath2);

        //Path: IheCommunicationLink_Edge -> IheCommunicationLink_Edge-StartElement = IheInvokingInterface ->  IheTransaction_IheInvokingInterface_Edge -> IheTransaction
        ElementaryMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath1_pathStep1 = emph.getEdgeToStartElementMetaPath(IheCommunicationLink_Edge.class, IheInvokingInterface.class);
        ElementaryMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath1_pathStep2 = emph.getMetaPath(IheInvokingInterface.class, IheInvokingInterface_IheTransaction_Edge.class, Direction.FORWARD, IheTransaction.class);
        SimpleMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath1 = new SimpleMetaPath(iheTransaction_IheCommunicationLink_InferenceMetaPath1_pathStep1, iheTransaction_IheCommunicationLink_InferenceMetaPath1_pathStep2);

        //Path: IheCommunicationLink_Edge -> IheCommunicationLink_Edge-EndElement = IheProvidingInterface ->  IheTransaction_IheProvidingInterface_Edge -> IheTransaction
        ElementaryMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath2_pathStep1 = emph.getEdgeToEndElementMetaPath(IheCommunicationLink_Edge.class, IheProvidingInterface.class);
        ElementaryMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath2_pathStep2 = emph.getMetaPath(IheProvidingInterface.class, IheProvidingInterface_IheTransaction_Edge.class, Direction.FORWARD, IheTransaction.class);
        SimpleMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath2 = new SimpleMetaPath(iheTransaction_IheCommunicationLink_InferenceMetaPath2_pathStep1, iheTransaction_IheCommunicationLink_InferenceMetaPath2_pathStep2);

        AbstractMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath = new SectionMetaPath(iheTransaction_IheCommunicationLink_InferenceMetaPath1, iheTransaction_IheCommunicationLink_InferenceMetaPath2);

        return ImmutableMap.of(Service_CommunicationLink_Edge.class, service_CommunicationLink_InferenceMetaPath, IheTransaction_IheCommunicationLink_Edge.class, iheTransaction_IheCommunicationLink_InferenceMetaPath);
    }

}

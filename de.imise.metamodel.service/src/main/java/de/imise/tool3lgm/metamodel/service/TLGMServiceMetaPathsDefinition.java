package de.imise.tool3lgm.metamodel.service;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_CommunicationInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_InvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_ProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.node.ApplicationSystem;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstance;
import de.imise.tool3lgm.metamodel.service.node.SoftwareProduct;

/**
 * @author AXS (27 May 2019)
 */
public class TLGMServiceMetaPathsDefinition extends MetaPathDefinition {

    /**
     * @param metaModel
     */
    public TLGMServiceMetaPathsDefinition(final MetaModel metaModel) {
        super(metaModel);
    }

    ///////////////////////////////////////////////////////////////////////
    // Bedingungspfade für Kanten (siehe Beschreibung getConditionPath() //
    ///////////////////////////////////////////////////////////////////////

    @Override
    public final Map<Class<? extends Edge>, SimpleMetaPath> getConditionPaths() {
        ImmutableMap.Builder<Class<? extends Edge>, SimpleMetaPath> builder = ImmutableMap.builder();
        //IheInvokingInterface_InvokingInterface_Edge
        builder.put(IheInvokingInterface_InvokingInterface_Edge.class, simpleMetaPathCreator.createSimpleMetaPath(Edge.getStartClass(IheInvokingInterface_InvokingInterface_Edge.class), Edge.getEndClass(IheInvokingInterface_InvokingInterface_Edge.class),
                IheActor_IheInterface_Edge.class, IheActor_IheActorInstance_Edge.class, ApplicationComponent_CommunicationInterface_Edge.class));
        //IheProvidingInterface_ProvidingInterface_Edge
        builder.put(IheProvidingInterface_ProvidingInterface_Edge.class, simpleMetaPathCreator.createSimpleMetaPath(Edge.getStartClass(IheProvidingInterface_ProvidingInterface_Edge.class),
                Edge.getEndClass(IheProvidingInterface_ProvidingInterface_Edge.class), IheActor_IheInterface_Edge.class, IheActor_IheActorInstance_Edge.class, ApplicationComponent_CommunicationInterface_Edge.class));
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
                simpleMetaPathCreator.createSimpleMetaPath(IheActor.class, IheActorInstance.class, IheActor_IheInterface_Edge.class, IheInvokingInterface_InvokingInterface_Edge.class, ApplicationComponent_CommunicationInterface_Edge.class));
        builder.put(IheActor_IheActorInstance_Edge.class,
                simpleMetaPathCreator.createSimpleMetaPath(IheActor.class, IheActorInstance.class, IheActor_IheInterface_Edge.class, IheProvidingInterface_ProvidingInterface_Edge.class, ApplicationComponent_CommunicationInterface_Edge.class));
        return builder.build();
    }
    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    @Override
    public Collection<SimpleMetaPath> getCreatablePaths() {
        SimpleMetaPath path1 = simpleMetaPathCreator.createSimpleMetaPath(ApplicationSystem.class, IheActor.class, "PATH_ApplicationSystem_IheActor", ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
        return ImmutableList.of(path1);
    }

    ////////////////////////////////////////////////////////////////////////
    // Map auf die in der Grafik anzuzeigenden Namen verbundener Elemente //
    ////////////////////////////////////////////////////////////////////////

    @Override
    public Map<Class<? extends ModelElement>, AbstractMetaPath> getElementClassToNameExtensionPath() {
        SimpleMetaPath applicationsSystemNameExtensionPath = simpleMetaPathCreator.createSimpleMetaPath(ApplicationSystem.class, SoftwareProduct.class, ApplicationSystem_SoftwareProduct_Edge.class);
        SimpleMetaPath iheActorInstanceNameExtensionPath = simpleMetaPathCreator.createSimpleMetaPath(IheActorInstance.class, IheActor.class, IheActor_IheActorInstance_Edge.class);
        return ImmutableMap.of(ApplicationSystem.class, applicationsSystemNameExtensionPath, IheActorInstance.class, iheActorInstanceNameExtensionPath);
    }

}

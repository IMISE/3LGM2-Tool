package de.imise.tool3lgm.metamodel.tlgm_service;

import static de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator.createSimpleMetaPath;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.SHAPE;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_PhysicalDataProcessingComponent_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.SupportLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ApplicationComponent;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ApplicationSystem;
import de.imise.tool3lgm.metamodel.tlgm_service.node.CommunicationInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.Function;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheActorInstance;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheInvokingInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheProvidingInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ObjectType;
import de.imise.tool3lgm.metamodel.tlgm_service.node.OrganisationSystem;
import de.imise.tool3lgm.metamodel.tlgm_service.node.PhysicalDataProcessingComponent;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ProvidingInterface;
import de.imise.util.pair.Pair;

public class TLGMServiceGraphViewDefinion extends GraphViewDefinition {

    @SuppressWarnings("unchecked")
    @Override
    protected final Class[] getPaintableNodes() {
        //diese Funtkion wird nur ein einziges Mal aufgerufen, daher ist es ok,
        //dass das Array hier in der Funktion immer wieder neu angelegt wird
        Class[] graphViewVisibleNodes = {
                Function.class,
                ObjectType.class,
                ApplicationSystem.class,
                OrganisationSystem.class,
                InvokingInterface.class,
                ProvidingInterface.class,
                PhysicalDataProcessingComponent.class,
                //                IheActor.class,
                IheActorInstance.class,
                //                IheInvokingInterface.class,
                //                IheProvidingInterface.class,
        };
        return graphViewVisibleNodes;
    }

    @Override
    protected final SimpleMetaPath[] getConfigurationPaths() {
        SimpleMetaPath[] configurationPaths = {
                //Testpfad über alle Ebenen hinweg
                //new MetaPath(Aufgabe.class, PhysischerDVBaustein.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class),
                createSimpleMetaPath(Function.class, ApplicationComponent.class, Function_Use_Edge.class, SupportLink_Edge.class),
                createSimpleMetaPath(ApplicationComponent.class, PhysicalDataProcessingComponent.class, ApplicationComponent_PhysicalDataProcessingComponent_Edge.class),
        };
        return configurationPaths;
    }

    @Override
    public List<Pair<Class<? extends ModelElement>, Class<? extends Edge>>> getHidableIfNotConnected() {
        return ImmutableList.of(new Pair<>(CommunicationInterface.class, CommunicationLink_Edge.class));
    }

    @Override
    protected void initDefaultElementLayout() {
        setDefaultLayout(Function.class, SHAPE.rechteck, GraphElementLayout.COLORS[GraphElementLayout.RED]);
        setDefaultLayout(ObjectType.class, GraphElementLayout.SHAPE.oval, GraphElementLayout.COLORS[GraphElementLayout.BLUE]);
        setDefaultLayout(ApplicationSystem.class, GraphElementLayout.SHAPE.rundeck, GraphElementLayout.COLORS[GraphElementLayout.LIGHTRED]);
        setDefaultLayout(OrganisationSystem.class, GraphElementLayout.SHAPE.rundeck, GraphElementLayout.COLORS[GraphElementLayout.BLUE]);
        setDefaultLayout(InvokingInterface.class, GraphElementLayout.SHAPE.oval, GraphElementLayout.COLORS[GraphElementLayout.LIGHTGREEN], 15, 15);
        setDefaultLayout(ProvidingInterface.class, GraphElementLayout.SHAPE.dreieck, GraphElementLayout.COLORS[GraphElementLayout.ORANGE], 20, 20);
        setDefaultLayout(PhysicalDataProcessingComponent.class, GraphElementLayout.SHAPE.rechteck, GraphElementLayout.COLORS[GraphElementLayout.ORANGE]);
        //setDefaultLayout(IheActor.class, GraphElementLayout.SHAPE.wabe, GraphElementLayout.COLORS[/* GraphElementLayout.LIGHTPURPLE */GraphElementLayout.YELLOW]);
        setDefaultLayout(IheActorInstance.class, GraphElementLayout.SHAPE.wabe, GraphElementLayout.COLORS[GraphElementLayout.LIGHTBLUE]);
        setDefaultLayout(IheInvokingInterface.class, GraphElementLayout.SHAPE.oval, GraphElementLayout.COLORS[GraphElementLayout.RED], 15, 15);
        setDefaultLayout(IheProvidingInterface.class, GraphElementLayout.SHAPE.dreieck, GraphElementLayout.COLORS[GraphElementLayout.RED], 20, 20);
    }

}

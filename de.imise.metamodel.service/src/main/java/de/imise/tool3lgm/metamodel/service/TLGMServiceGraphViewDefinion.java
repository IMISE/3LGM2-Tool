package de.imise.tool3lgm.metamodel.service;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.SHAPE;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_PhysicalDataProcessingComponent_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.service.node.ApplicationComponent;
import de.imise.tool3lgm.metamodel.service.node.ApplicationSystem;
import de.imise.tool3lgm.metamodel.service.node.CommunicationInterface;
import de.imise.tool3lgm.metamodel.service.node.Function;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstance;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstanceInvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstanceProvidingInterface;
import de.imise.tool3lgm.metamodel.service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.ObjectType;
import de.imise.tool3lgm.metamodel.service.node.OrganisationSystem;
import de.imise.tool3lgm.metamodel.service.node.PhysicalDataProcessingComponent;
import de.imise.tool3lgm.metamodel.service.node.ProvidingInterface;
import de.imise.util.pair.Pair;

public class TLGMServiceGraphViewDefinion extends GraphViewDefinition {

    /**
     * @param metaModel
     */
    public TLGMServiceGraphViewDefinion(final MetaModel metaModel) {
        super(metaModel);
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
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
                IheActorInstance.class,
                IheActorInstanceInvokingInterface.class,
                IheActorInstanceProvidingInterface.class,
        };
        return graphViewVisibleNodes;
    }

    @Override
    protected final SimpleMetaPath[] getConfigurationPaths() {
        SimpleMetaPath[] configurationPaths = {
                //Testpfad über alle Ebenen hinweg
                //new MetaPath(Aufgabe.class, PhysischerDVBaustein.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class),
                SimpleMetaPathCreator.createSimpleMetaPath(metaModel, Function.class, ApplicationComponent.class, Function_Use_Edge.class, ApplicationComponent_Use_Edge.class),
                SimpleMetaPathCreator.createSimpleMetaPath(metaModel, ApplicationComponent.class, PhysicalDataProcessingComponent.class, ApplicationComponent_PhysicalDataProcessingComponent_Edge.class),
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
        setDefaultLayout(IheActorInstance.class, GraphElementLayout.SHAPE.rechteck, GraphElementLayout.COLORS[GraphElementLayout.LIGHTBLUE]);
        setDefaultLayout(IheActorInstanceInvokingInterface.class, GraphElementLayout.SHAPE.oval, GraphElementLayout.COLORS[GraphElementLayout.RED], 15, 15);
        setDefaultLayout(IheActorInstanceProvidingInterface.class, GraphElementLayout.SHAPE.dreieck, GraphElementLayout.COLORS[GraphElementLayout.GRAY], 20, 20);
    }

    /*
     * in order to generate additional model shapes automatically, this function is created.
     * through definition of certain paths, it is able to return the path and the corresponding model component
     */
    @Override
    protected List<AdditionalGraphShapeData> getAdditionalGraphShapeData() {
        //Application Sytsems get a Database on its shape if they are connected to an ObjectType
        SimpleMetaPath mp1 = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, ApplicationSystem.class, ObjectType.class, ApplicationComponent_ObjectType_Edge.class);
        SimpleMetaPath mp2 = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, OrganisationSystem.class, ObjectType.class, ApplicationComponent_ObjectType_Edge.class);
        return ImmutableList.of(new AdditionalGraphShapeData(mp1, SHAPE.tonne), new AdditionalGraphShapeData(mp1, SHAPE.ordner));
    }

}

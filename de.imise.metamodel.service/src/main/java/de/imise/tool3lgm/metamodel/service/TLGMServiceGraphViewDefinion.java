package de.imise.tool3lgm.metamodel.service;

import static de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition.InterLayerLineRenderType.LINE_TYPE_DASHED;
import static de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition.InterLayerLineRenderType.LINE_TYPE_SOLID;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.BLUE;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.GRAY;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.LIGHTBLUE;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.LIGHTGREEN;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.LIGHTRED;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.ORANGE;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.RED;
import static de.imise.tool3lgm.graphtools.view.graph.Shape.dreieck;
import static de.imise.tool3lgm.graphtools.view.graph.Shape.oval;
import static de.imise.tool3lgm.graphtools.view.graph.Shape.rechteck;
import static de.imise.tool3lgm.graphtools.view.graph.Shape.rundeck;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
import de.imise.tool3lgm.graphtools.view.graph.Shape;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_PhysicalDataProcessingComponent_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_RepresentationForm_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ObjectType_RepresentationForm_Edge;
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

    @Override
    protected final SimpleMetaPath[] getInterLayerMetaPaths() {
        SimpleMetaPath[] configurationPaths = {
                //Testpfad über alle Ebenen hinweg
                //new MetaPath(Aufgabe.class, PhysischerDVBaustein.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class),
                SimpleMetaPathCreator.createSimpleMetaPath(metaModel, Function.class, ApplicationComponent.class, Function_Use_Edge.class, ApplicationComponent_Use_Edge.class),
                SimpleMetaPathCreator.createSimpleMetaPath(metaModel, ObjectType.class, ApplicationComponent.class, ObjectType_RepresentationForm_Edge.class, ApplicationComponent_RepresentationForm_Edge.class),
                SimpleMetaPathCreator.createSimpleMetaPath(metaModel, ApplicationComponent.class, PhysicalDataProcessingComponent.class, ApplicationComponent_PhysicalDataProcessingComponent_Edge.class),
        };
        return configurationPaths;
    }

    @Override
    protected InterLayerLineRenderType[] getInterLayerLineRenderTypes() {
        return new InterLayerLineRenderType[] {
                LINE_TYPE_SOLID, LINE_TYPE_DASHED, LINE_TYPE_SOLID
        };
    }

    @Override
    public List<Pair<Class<? extends ModelElement>, Class<? extends Edge>>> getHidableIfNotConnected() {
        return ImmutableList.of(new Pair<>(CommunicationInterface.class, CommunicationLink_Edge.class));
    }

    @Override
    protected void initDefaultElementLayout() {
        setDefaultLayout(Function.class, rechteck, RED);
        setDefaultLayout(ObjectType.class, oval, BLUE);
        setDefaultLayout(ApplicationSystem.class, rundeck, LIGHTRED);
        setDefaultLayout(OrganisationSystem.class, rundeck, BLUE);
        setDefaultLayout(InvokingInterface.class, oval, LIGHTGREEN, 16, 16);
        setDefaultLayout(ProvidingInterface.class, dreieck, ORANGE, 16, 16);
        setDefaultLayout(PhysicalDataProcessingComponent.class, rechteck, ORANGE);
        setDefaultLayout(IheActorInstance.class, rechteck, LIGHTBLUE);
        setDefaultLayout(IheActorInstanceInvokingInterface.class, oval, RED, 16, 16);
        setDefaultLayout(IheActorInstanceProvidingInterface.class, dreieck, GRAY, 16, 16);
    }

    /*
     * in order to generate additional model shapes automatically, this function
     * is created. through definition of certain paths, it is able to return the
     * path and the corresponding model component
     */
    @Override
    protected List<AdditionalGraphShapeData> getAdditionalGraphShapeData() {
        //Application Sytsems get a Database on its shape if they are connected to an ObjectType
        SimpleMetaPath mp1 = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, ApplicationSystem.class, ObjectType.class, ApplicationComponent_RepresentationForm_Edge.class, ObjectType_RepresentationForm_Edge.class);
        SimpleMetaPath mp2 = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, OrganisationSystem.class, ObjectType.class, ApplicationComponent_RepresentationForm_Edge.class, ObjectType_RepresentationForm_Edge.class);
        return ImmutableList.of(new AdditionalGraphShapeData(mp1, Shape.tonne), new AdditionalGraphShapeData(mp2, Shape.ordner));
    }

}

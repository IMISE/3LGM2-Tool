package de.imise.tool3lgm.metamodel.service;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.graphtools.consistency.metapath.ConsistencyCheckSectionMetaPath;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.InferenceEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.graphtools.path.metapaths.DifferenceMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SectionMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstanceCommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstance_IheActorInstanceInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActor_MustBeGroupedWith_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheCommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheCommunicationLink_IheActorInstanceCommunicationLink_Edge;
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
import de.imise.tool3lgm.metamodel.service.node.IheActorInstanceInvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstanceProvidingInterface;
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
        //hier ist im Moment nichts sinnvolles drin. Es gab mal einen Anwendungsfall als Anwendungssysteme ncoh die Oberklasse
        //von IheActorInstances waren und sich beide Klassen dieselben Schnittstellen geteilt haben mit derselben Verbindung
        //dahin. Trotzdem sollten die Schnittstellen von IheActors nicht mit den Schnittstellen von Anwendungssystemen sondern
        //nur mit den Schnittstellen von IhcActorInstances verbunden werden können. Seit der Trennung von Anwendungssystemen
        //und IhcActorInstances wird das hier nicht mehr gebraucht.
        return builder.build();
    }

    @Override
    public final Map<Class<? extends Edge>, MetaPath> getSoftConditionMetaPaths() {
        //IheActorInstanceCommunicationLink_Edge (best connectable ActorInstanceInterfaces should be connected with IheInterfaces which are connected via an IheCommunicationInterface)
        SimpleMetaPath includeCondition1 = smp(IheActorInstanceInvokingInterface.class, IheActorInstanceProvidingInterface.class, IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class, IheCommunicationLink_Edge.class,
                IheProvidingInterface_IheActorInstanceProvidingInterface_Edge.class);
        SimpleMetaPath excludeCondition1 = smp(IheActorInstanceInvokingInterface.class, IheActorInstanceProvidingInterface.class, IheActorInstance_IheActorInstanceInterface_Edge.class, IheActorInstance_IheActorInstanceInterface_Edge.class);
        DifferenceMetaPath differenceMetaPath1 = new DifferenceMetaPath(includeCondition1, excludeCondition1);

        return ImmutableMap.of(IheActorInstanceCommunicationLink_Edge.class, differenceMetaPath1);
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
        SimpleMetaPath metaPath1 = smp(ApplicationSystem.class, IheActor.class, "PATH_ApplicationSystem_IheActor", ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
        //        SimpleMetaPath metaPath2 = smp(IheActorInstance.class, IheActor.class, "PATH_IheActorInstance_ApplicationSystem_IheActor", ApplicationSystem_IheActorInstance_Edge.class, ApplicationSystem_IheActorInstance_Edge.class,
        //                IheActor_IheActorInstance_Edge.class);
        //        return ImmutableList.of(metaPath1, metaPath2);
        //das folgende muss in die Resourcen, sollte man den Pfad oben mal schrf schalten (bisher nur deutsch)
        //        PATH_IheActorInstance_ApplicationSystem_IheActor_f      muss gruppiert werden mit
        //        PATH_IheActorInstance_ApplicationSystem_IheActor_b      wird zur Gruppierung benötigt von
        return ImmutableList.of(metaPath1);
    }

    ////////////////////////////////////////////////////////////////////////
    // Map auf die in der Grafik anzuzeigenden Namen verbundener Elemente //
    ////////////////////////////////////////////////////////////////////////

    @Override
    public Map<Class<? extends ModelElement>, MetaPath> getElementClassToNameExtensionPath() {
        SimpleMetaPath applicationsSystemNameExtensionPath = smp(ApplicationSystem.class, SoftwareProduct.class, ApplicationSystem_SoftwareProduct_Edge.class);
        SimpleMetaPath iheActorInstanceNameExtensionPath = smp(IheActorInstance.class, IheActor.class, IheActor_IheActorInstance_Edge.class);
        return ImmutableMap.of(ApplicationSystem.class, applicationsSystemNameExtensionPath, IheActorInstance.class, iheActorInstanceNameExtensionPath);
    }

    @Override
    public Map<Class<? extends Edge>, MetaPath> getEdgeClassToInitialCreatedNameSourcePath() {
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

        MetaPath iheCommunicationLink_nameMetaPath = new SectionMetaPath(iheCommunicationLink_nameMetaPath1, iheCommunicationLink_nameMetaPath2);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // IheActorInstanceCommunicationLink_Edge -> bekommt Name der Transaktion, die über ihre Schnittstellen verbunden ist //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Path: IheActorInstanceCommunicationLink_Edge -> IheActorInstanceCommunicationLink_Edge-StartElement = IheActorInstanceInvokingInterface
        //      -> IheInvokingInterface_IheActorInstanceInvokingInterface_Edge -> IheInvokingInterface
        //      -> IheTransaction_IheInvokingInterface_Edge -> IheTransaction
        ElementaryMetaPath iheActorInstanceCommunicationLink_nameMetaPath1_pathStep1 = emph.getEdgeToStartElementMetaPath(IheActorInstanceCommunicationLink_Edge.class, IheActorInstanceInvokingInterface.class);
        ElementaryMetaPath iheActorInstanceCommunicationLink_nameMetaPath1_pathStep2 = emph.getMetaPath(IheActorInstanceInvokingInterface.class, IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class, Direction.BACKWARD,
                IheInvokingInterface.class);
        ElementaryMetaPath iheActorInstanceCommunicationLink_nameMetaPath1_pathStep3 = emph.getMetaPath(IheInvokingInterface.class, IheInvokingInterface_IheTransaction_Edge.class, Direction.FORWARD, IheTransaction.class);
        SimpleMetaPath iheActorInstanceCommunicationLink_nameMetaPath1 = new SimpleMetaPath(iheActorInstanceCommunicationLink_nameMetaPath1_pathStep1, iheActorInstanceCommunicationLink_nameMetaPath1_pathStep2,
                iheActorInstanceCommunicationLink_nameMetaPath1_pathStep3);

        //Path: IheActorInstanceCommunicationLink_Edge -> IheActorInstanceCommunicationLink_Edge-EndElement = IheActorInstanceProvidingInterface
        //      -> IheProvidingInterface_ProvidingInterface_Edge -> IheProvidingInterface
        //      -> IheTransaction_IheProvidingInterface_Edge -> IheTransaction
        ElementaryMetaPath iheActorInstanceCommunicationLink_nameMetaPath2_pathStep1 = emph.getEdgeToEndElementMetaPath(IheActorInstanceCommunicationLink_Edge.class, IheActorInstanceProvidingInterface.class);
        ElementaryMetaPath iheActorInstanceCommunicationLink_nameMetaPath2_pathStep2 = emph.getMetaPath(IheActorInstanceProvidingInterface.class, IheProvidingInterface_IheActorInstanceProvidingInterface_Edge.class, Direction.BACKWARD,
                IheProvidingInterface.class);
        ElementaryMetaPath iheActorInstanceCommunicationLink_nameMetaPath2_pathStep3 = emph.getMetaPath(IheProvidingInterface.class, IheProvidingInterface_IheTransaction_Edge.class, Direction.FORWARD, IheTransaction.class);
        SimpleMetaPath iheActorInstanceCommunicationLink_nameMetaPath2 = new SimpleMetaPath(iheActorInstanceCommunicationLink_nameMetaPath2_pathStep1, iheActorInstanceCommunicationLink_nameMetaPath2_pathStep2,
                iheActorInstanceCommunicationLink_nameMetaPath2_pathStep3);

        MetaPath iheActorInstanceCommunicationLink_nameMetaPath = new SectionMetaPath(iheActorInstanceCommunicationLink_nameMetaPath1, iheActorInstanceCommunicationLink_nameMetaPath2);
        return ImmutableMap.of(IheCommunicationLink_Edge.class, iheCommunicationLink_nameMetaPath, IheActorInstanceCommunicationLink_Edge.class, iheActorInstanceCommunicationLink_nameMetaPath);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // InferenceEdges Condition-MetaPaths: (see also yEd-Modell *A1, *A2 und *A3) //
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public final Map<Class<? extends InferenceEdge>, MetaPath> getInferenceEdgeToConditionMetaPath() {

        MetaModel metaModel = getMetaModel();
        ElementaryMetaPathHandler emph = metaModel.getElementaryMetaPathHandler();

        // Edge between Service and CommunicationLink_Edge = Service_CommunicationLink_Edge
        //   (*A1) Service_CommunicationLink_Edge is inferred by Section-MetaPath of MetaPaths 1 and 2:
        //     1.) CommunicationLink_Edge:InvokingInterface ( = CommunicationLink_Edge-StartElement)
        //      -> Service_InvokingInterface_Edge -> Servide
        ElementaryMetaPath service_CommunicationLink_InferenceMetaPath1_pathStep1 = emph.getEdgeToStartElementMetaPath(CommunicationLink_Edge.class, InvokingInterface.class);
        ElementaryMetaPath service_CommunicationLink_InferenceMetaPath1_pathStep2 = emph.getMetaPath(InvokingInterface.class, Service_InvokingInterface_Edge.class, Direction.BACKWARD, Service.class);
        SimpleMetaPath service_CommunicationLink_InferenceMetaPath1 = new SimpleMetaPath(service_CommunicationLink_InferenceMetaPath1_pathStep1, service_CommunicationLink_InferenceMetaPath1_pathStep2);
        //   (*A1) Service_CommunicationLink_Edge is inferred by Section-MetaPath of MetaPaths 1 and 2:
        //     2.) CommunicationLink_Edge:ProvidingInterface ( = CommunicationLink_Edge-EndElement)
        //      -> Service_ProvidingInterface_Edge -> Servide
        ElementaryMetaPath service_CommunicationLink_InferenceMetaPath2_pathStep1 = emph.getEdgeToEndElementMetaPath(CommunicationLink_Edge.class, ProvidingInterface.class);
        ElementaryMetaPath service_CommunicationLink_InferenceMetaPath2_pathStep2 = emph.getMetaPath(InvokingInterface.class, Service_ProvidingInterface_Edge.class, Direction.BACKWARD, Service.class);
        SimpleMetaPath service_CommunicationLink_InferenceMetaPath2 = new SimpleMetaPath(service_CommunicationLink_InferenceMetaPath2_pathStep1, service_CommunicationLink_InferenceMetaPath2_pathStep2);
        //   (*A1) Service_CommunicationLink_Edge is inferred by Section-MetaPath of MetaPaths 1 and 2:
        MetaPath service_CommunicationLink_InferenceMetaPath = new SectionMetaPath(service_CommunicationLink_InferenceMetaPath1, service_CommunicationLink_InferenceMetaPath2);

        // Edge between IheTransaction and IheCommunicationLink_Edge = IheTransaction_IheCommunicationLink_Edge
        //   (*A2) IheTransaction_IheCommunicationLink_Edge is inferred by Section-MetaPath of MetaPaths 1 and 2:
        //     1.) IheCommunicationLink_Edge:IheInvokingInterface ( = IheCommunicationLink_Edge-StartElement)
        //      -> IheInvokingInterface_IheTransaction_Edge -> IheTransaction
        ElementaryMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath1_pathStep1 = emph.getEdgeToStartElementMetaPath(IheCommunicationLink_Edge.class, IheInvokingInterface.class);
        ElementaryMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath1_pathStep2 = emph.getMetaPath(IheInvokingInterface.class, IheInvokingInterface_IheTransaction_Edge.class, Direction.FORWARD, IheTransaction.class);
        SimpleMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath1 = new SimpleMetaPath(iheTransaction_IheCommunicationLink_InferenceMetaPath1_pathStep1, iheTransaction_IheCommunicationLink_InferenceMetaPath1_pathStep2);
        //   (*A2) IheTransaction_IheCommunicationLink_Edge is inferred by Section-MetaPath of MetaPaths 1 and 2:
        //     2.) IheCommunicationLink_Edge:IheProvidingInterface ( = IheCommunicationLink_Edge-EndElement)
        //      -> IheProvidingInterface_IheTransaction_Edge -> IheTransaction
        ElementaryMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath2_pathStep1 = emph.getEdgeToEndElementMetaPath(IheCommunicationLink_Edge.class, IheProvidingInterface.class);
        ElementaryMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath2_pathStep2 = emph.getMetaPath(IheProvidingInterface.class, IheProvidingInterface_IheTransaction_Edge.class, Direction.FORWARD, IheTransaction.class);
        SimpleMetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath2 = new SimpleMetaPath(iheTransaction_IheCommunicationLink_InferenceMetaPath2_pathStep1, iheTransaction_IheCommunicationLink_InferenceMetaPath2_pathStep2);
        //   (*A2) IheTransaction_IheCommunicationLink_Edge is inferred by Section-MetaPath of MetaPaths 1 and 2:
        MetaPath iheTransaction_IheCommunicationLink_InferenceMetaPath = new SectionMetaPath(iheTransaction_IheCommunicationLink_InferenceMetaPath1, iheTransaction_IheCommunicationLink_InferenceMetaPath2);

        // Edge between IheCommunicationLink_Edge and IheActorInstanceCommunicationLink_Edge= IheCommunicationLink_IheActorInstanceCommunicationLink_Edge
        //    (*A3) IheCommunicationLink_IheActorInstanceCommunicationLink_Edge InferenceEdge is inferred by Section-MetaPath of MetaPaths 1 and 2:
        //      1.) IheActorInstanceCommunicationLink_Edge:IheActorInstanceInvokingInterface ( = IheActorInstanceCommunicationLink_Edge-StartElement)
        //       -> IheInvokingInterface_IheActorInstanceInvokingInterface_Edge -> IheInvokingInterface
        //       -> IheInvokingInterface:IheCommunicationLink_Edge (= IheCommunicationLink_Edge-StartElement)
        ElementaryMetaPath iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath1_pathStep1 = emph.getEdgeToStartElementMetaPath(IheActorInstanceCommunicationLink_Edge.class, IheActorInstanceInvokingInterface.class);
        ElementaryMetaPath iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath1_pathStep2 = emph.getMetaPath(IheActorInstanceInvokingInterface.class, IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class,
                Direction.BACKWARD, IheInvokingInterface.class);
        ElementaryMetaPath iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath1_pathStep3 = emph.getStartElementToEdgeMetaPath(IheInvokingInterface.class, IheCommunicationLink_Edge.class);
        SimpleMetaPath iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath1 = new SimpleMetaPath(iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath1_pathStep1,
                iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath1_pathStep2, iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath1_pathStep3);

        // Edge between IheCommunicationLink_Edge and IheActorInstanceCommunicationLink_Edge= IheCommunicationLink_IheActorInstanceCommunicationLink_Edge
        //    (*A3) IheCommunicationLink_IheActorInstanceCommunicationLink_Edge InferenceEdge is inferred by Section-MetaPath of MetaPaths 1 and 2:
        //      2.) IheActorInstanceCommunicationLink_Edge:IheActorInstanceProvidingInterface ( = IheActorInstanceCommunicationLink_Edge-EndElement)
        //       -> IheProvidingInterface_IheActorInstanceProvidingInterface_Edge -> IheProvidingInterface
        //       -> IheProvidingInterface:IheCommunicationLink_Edge (= IheCommunicationLink_Edge-EndElement)
        ElementaryMetaPath iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath2_pathStep1 = emph.getEdgeToEndElementMetaPath(IheActorInstanceCommunicationLink_Edge.class, IheActorInstanceProvidingInterface.class);
        ElementaryMetaPath iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath2_pathStep2 = emph.getMetaPath(IheActorInstanceProvidingInterface.class, IheProvidingInterface_IheActorInstanceProvidingInterface_Edge.class,
                Direction.BACKWARD, IheProvidingInterface.class);
        ElementaryMetaPath iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath2_pathStep3 = emph.getEndElementToEdgeMetaPath(IheProvidingInterface.class, IheCommunicationLink_Edge.class);
        SimpleMetaPath iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath2 = new SimpleMetaPath(iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath2_pathStep1,
                iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath2_pathStep2, iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath2_pathStep3);

        //    (*A3) IheCommunicationLink_IheActorInstanceCommunicationLink_Edge InferenceEdge is inferred by Section-MetaPath of MetaPaths 1 and 2:
        MetaPath iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath = new SectionMetaPath(iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath1,
                iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath2);

        return ImmutableMap.of(Service_CommunicationLink_Edge.class, service_CommunicationLink_InferenceMetaPath, IheTransaction_IheCommunicationLink_Edge.class, iheTransaction_IheCommunicationLink_InferenceMetaPath,
                IheCommunicationLink_IheActorInstanceCommunicationLink_Edge.class, iheCommunicationLink_IheActorInstanceCommunicationLink_InferenceMetaPath);
    }

    /////////////////////////////////////
    //  ConsistencyConditionMetaPaths  //
    /////////////////////////////////////

    @Override
    public Map<ConsistencyCheckSectionMetaPath, Class<? extends Edge>> getConsistencyConditionMissingConnectedElementsMetaPaths() {
        //AXS am 08.09.20220:
        //Dieser erste MetaPath beschreibt den Fehler aus Sicht der IheActorInstance. Fehlt für sie der muss-gruppiert-werden-mit-Partner, dann kommt der
        //Fehler. Problem: der Fehler sagt aus, dass dem Anwendungssystem, dieser IheActorInstance ein weiteres Anwendungssystem zugeordnet werden muss.
        //Das kommt auch, wenn die IheActorInstance gar keinem Anwendungssystem zugeordnet ist. Dadurch aber gibt es das sogenannte Element zur Fehlerbehebung
        //nicht, dessen Eigenschaftsdialog man öffnen könnte, um den Fehler zu beheben (denn das geht nur durch Öffnen des Dialoes für das zugehörige
        //Anwendungssystem, was ja nicht da ist). Dadurch gilt dieser Fehler automatisch als nicht behebbar. Dadurch würde die IheActorInsance beim Einlesen
        //eines solchen fehlerhaften Modells automatisch gelöscht werden (in #clearUnfixableErrors()), was nur dadurch verhindert wird, dass alle diese
        //MissingPathErrors in #clearUnfixableErrors() ignoriert werden. Das ist auch ok so, weil bei beliebig langen Pfaden nie weiß, warum der Fehler
        //aufgetreten ist und das Element somit nicht einfach löschen sollte.
        //Es gab mehrere Möglichkeiten, das nicht erwünschte Löschen zu umgehen:
        // 1.) den Fehler nicht aus Sicht der IheActorInsance sondern für das Anwenundungssystem generieren. Dann kommt der Fehler nur, wenn auch tatsächlich
        //ein Anwendungssystem vorhanden ist. ABER (siehe unten): die Kaskade der Abhängigkeiten funktioniert dann nciht mehr richtig
        //2.) Generell festlegen, dass MissingPathErrors niemals als unfixable gelten und somit die betreffenden Elemente nicht gelöscht werden. Die normalen
        //MIN-MAX-Errors sind davon nicht betroffen, da sie genau für eine einzelne Kante gelten.
        //3.) nicht wirklich praktikabel aber möglich: Man definiert noch eine Bedingung, die zutreffen muss, damit der Fehler anwendbar ist. In dem Fall hier,
        //müsste man den ersten Pfadschritt irgendwie als Bedingung angeben, dass es ihn geben muss, damit der zweite Pfadschritt als fehlerhaft angesehen werden
        //kann.
        //Fazit: Ich habe mich für 2.) entschieden, also MissingPathError-Elemente werden niemals gelöscht und es wird weiterhin der MetaPfad mit der
        //funktionierenden Abhängigkeits-Kaskade ausgeführt. Weiterhin werde ich in der Fehlertabelle der Konsistenzprüfung verhindern, dass Fehler angezeigt werden,
        //die man gar nicht beheben kann. Denn eigentlich müsste für die IheActorInstance noch ein Fehler oder eine Warnung erzeugt werden, dass sie mit keinem
        //Anwendungssystem verbunden ist.
        SimpleMetaPath consistencyConditionSubMetaPath1 = smp(IheActorInstance.class, IheActor.class, IheActor_IheActorInstance_Edge.class, IheActor_IheActor_MustBeGroupedWith_Edge.class);
        SimpleMetaPath consistencyConditionSubMetaPath2 = smp(IheActorInstance.class, IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
        ConsistencyCheckSectionMetaPath consistencyConditionMetaPathActorInstanceMustBeGroupedWith = new ConsistencyCheckSectionMetaPath("PATH_IheActorInstance_mustBeGroupedWith_IheActor", consistencyConditionSubMetaPath1,
                consistencyConditionSubMetaPath2);

        //Der folgende MetaPfad beschreibt die nicht erfüllte must-be-grouped-with-Beziehung ausgehend vom Anwendungssystem. Fehlt eine must-be-grouped-with-Beziehung,
        //dann kommt der Fehler, dass diesem Anwendungssystem eine weitere IheActorInsatnce zugeordnet werden muss. Hat irgendeine zu gruppierende IheActorInstance gar
        //kein Anwendungssystem, dann kommt kein Fehler.
        //Problem: hiermit funktioniert die Kaskade von Anhängigkeiten  nicht richtig. Also eine Bedingung gilt schon als erfüllt, sobald nur eine einzige, aber
        //nicht alle benötigten IheActorInstances an dem Anwendungssystem hängen. Warum das so ist, müsste mal nachvollzogen werden, weil eigentlich halte ich das
        //hier für den besseren Pfad, da kein Fehler entsteht, wenn die Ihe ActorInstance kein Anwendungssystem hat. Bsp.-Kaskade: XDS-Doc-Consumer mal mit allen
        //Varianten des Hinzufügens benötigter Anhängigkeiten ausprobieren.
        SimpleMetaPath consistencyConditionSubMetaPath3 = smp(ApplicationSystem.class, IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class, IheActor_IheActor_MustBeGroupedWith_Edge.class);
        SimpleMetaPath consistencyConditionSubMetaPath4 = smp(ApplicationSystem.class, IheActor.class, ApplicationSystem_IheActorInstance_Edge.class, IheActor_IheActorInstance_Edge.class);
        ConsistencyCheckSectionMetaPath consistencyConditionApplicationSystemNeedsGroupingOfIheActorInstances = new ConsistencyCheckSectionMetaPath("PATH_ApplicationSystem_needsGroupingOf_IheActorInstances", consistencyConditionSubMetaPath3,
                consistencyConditionSubMetaPath4);

        //the identifier for the corresponding ErrorSolution is the IheActor_IheActor_MustBeGroupedWith_Edge.class
        return ImmutableMap.of(consistencyConditionMetaPathActorInstanceMustBeGroupedWith, IheActor_IheActor_MustBeGroupedWith_Edge.class);//, consistencyConditionApplicationSystemNeedsGroupingOfIheActorInstances,IheActor_IheActor_MustBeGroupedWith_Edge.class);
    }

}

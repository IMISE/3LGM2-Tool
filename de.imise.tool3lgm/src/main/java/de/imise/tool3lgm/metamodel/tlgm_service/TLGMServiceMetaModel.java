package de.imise.tool3lgm.metamodel.tlgm_service;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.AnalysisDefinition;
import de.imise.tool3lgm.graphtools.metamodel.CopyDependencies;
import de.imise.tool3lgm.graphtools.metamodel.ExtrasActionsDefinition;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.PathsDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_CommunicationInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_IsPartOfEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_PhysicalDataProcessingComponent_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_PhysicalDataProcessingComponent_RequiresForFunctionality_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_PhysicalDataProcessingComponent_RequiresForStorage_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_SupportLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationSystem_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.DataTransmissionLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.DeviceClass_IsPartOfEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Function_IsPartOfEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Function_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Function_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheActorOfIntegrationProfile_ApplicationSystem_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheConcept_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheActorOfIntegrationProfile_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheIntegrationProfile_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheTransactionLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheTransactionLink_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.IheTransaction_Service_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Location_IsPartOfEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ObjectType_IsPartOfEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.OrganisationalUnit_IsPartOfEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.OrganisationalUnit_SupportLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.OrganisationalUnit_Use_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.PhysicalDataProcessingComponentVirtualises_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.PhysicalDataProcessingComponent_DeviceClass_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.PhysicalDataProcessingComponent_IsPartOfEdge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.PhysicalDataProcessingComponent_Location_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Process_Function_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ServiceUses_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Service_CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Service_Function_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Service_InvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Service_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Service_ProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Service_ServiceClass_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.StorageLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.SupportLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.TransmissionMedium_DataTransmissionLink_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ApplicationComponent;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ApplicationSystem;
import de.imise.tool3lgm.metamodel.tlgm_service.node.CommunicationInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.DeviceClass;
import de.imise.tool3lgm.metamodel.tlgm_service.node.Function;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheActorOfIntegrationProfile;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheConcept;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheDomain;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheIntegrationProfile;
import de.imise.tool3lgm.metamodel.tlgm_service.node.IheTransaction;
import de.imise.tool3lgm.metamodel.tlgm_service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.Location;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ObjectType;
import de.imise.tool3lgm.metamodel.tlgm_service.node.OrganisationSystem;
import de.imise.tool3lgm.metamodel.tlgm_service.node.OrganisationalUnit;
import de.imise.tool3lgm.metamodel.tlgm_service.node.PhysicalDataProcessingComponent;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ProvidingInterface;
import de.imise.tool3lgm.metamodel.tlgm_service.node.Service;
import de.imise.tool3lgm.metamodel.tlgm_service.node.ServiceClass;
import de.imise.tool3lgm.metamodel.tlgm_service.node.SoftwareProduct;
import de.imise.tool3lgm.metamodel.tlgm_service.node.SystemOfConcepts;
import de.imise.tool3lgm.metamodel.tlgm_service.node.TransmissionMedium;
import de.imise.tool3lgm.metamodel.tlgm_service.node.Use;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbVirtualPdvbVerbindung;

@SuppressWarnings({
        "unchecked",
        "rawtypes"
})
public class TLGMServiceMetaModel extends MetaModel {

    /////////////////////
    // PathsDefinition //
    /////////////////////

    @Override
    public PathsDefinition createPathsDefinition() {
        //im Moment hat dieses Metamodell keine eigene Pfaddefinition. Man könnte diese Funktion auch weglassen.
        return super.createPathsDefinition();
    }

    /////////////////////////
    // GraphViewDefinition //
    /////////////////////////

    @Override
    public final GraphViewDefinition createGraphViewDefinition() {
        return new TLGMServiceGraphViewDefinion();
    }

    //////////////////////
    // CopyDependencies //
    //////////////////////

    @Override
    public final CopyDependencies createCopyDependencies() {
        return new CopyDependencies();
    }

    ////////////////////////
    // AnalysisDefinition //
    ////////////////////////

    @Override
    protected Class<? extends AnalysisDefinition> getAnalysisDefinitionClass() {
        return null;
    }

    /////////////////////////////
    // ExtrasActionsDefinition //
    /////////////////////////////

    @Override
    protected Class<? extends ExtrasActionsDefinition> getExtrasActionsDefinitionClass() {
        return null;
    }

    ////////////
    // Node //
    ////////////

    /** Alle Node der FE als Array */
    private static final Class[] ALL_DOMAIN_LAYER_NODES = {
            Function.class,
            ObjectType.class,
            OrganisationalUnit.class,
            Use.class,
            SystemOfConcepts.class,
            de.imise.tool3lgm.metamodel.tlgm_service.node.Process.class, // es gibt im lang-package ebenfalls eine Klasse Process
    };

    /** Alle Node zw. FE und LWE als Array */
    private final Class[] ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES = {
            //auch die Assoziationsklasse hier eintagen
            SupportLink_Edge.class, StorageLink_Edge.class,
    };

    /** Alle Node der LWE als Array */
    private final Class[] ALL_LOGICAL_LAYER_NODES = {
            ApplicationSystem.class,
            OrganisationSystem.class,
            SoftwareProduct.class,
            InvokingInterface.class,
            ProvidingInterface.class,
            Service.class,
            ServiceClass.class,

            //auch die Assoziationsklasse hier eintagen
            CommunicationLink_Edge.class,

            //abstracte Node müssen hier auch eingetragen werden
            ApplicationComponent.class,
            CommunicationInterface.class,

            //IHE-Konzepte
            IheActorOfIntegrationProfile.class,
            IheConcept.class,
            IheDomain.class,
            IheIntegrationProfile.class,
            IheTransaction.class,
            //IHE-Assoziationsklassen
            IheTransactionLink_Edge.class,
    };

    /** Alle Node zw. LWE und PWE als Array */
    private final Class[] ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES = {};

    /** Alle Node der PWE als Array */
    private final Class[] ALL_PHYSICAL_LAYER_NODES = {
            PhysicalDataProcessingComponent.class,
            DeviceClass.class,
            Location.class,
            DataTransmissionLink_Edge.class,
            TransmissionMedium.class,
    };

    @Override
    public final Class[] getAllDomainLayerNodes() {
        return ALL_DOMAIN_LAYER_NODES;
    }

    @Override
    public final Class[] getAllInterDomainLogicalLayerNodes() {
        return ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES;
    }

    @Override
    public final Class[] getAllLogicalLayerNodes() {
        return ALL_LOGICAL_LAYER_NODES;
    }

    @Override
    public final Class[] getAllInterLogicalPhysicalLayerNodes() {
        return ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES;
    }

    @Override
    public final Class[] getAllPhysicalLayerNodes() {
        return ALL_PHYSICAL_LAYER_NODES;
    }

    @Override
    public Class[] getTreeLogicalLayerVisibleAbstractNodes() {
        return new Class[] {
                //nur bei Anwendungsbausteinen soll die abstrakte Oberklasse im Baum angezeigt werden
                ApplicationComponent.class, IheConcept.class,
        };
    }

    ////////////
    // Kanten //
    ////////////

    @Override
    public Class[] getAllEdges() {
        return new Class[] {
                //FE
                Function_IsPartOfEdge.class,
                Function_ObjectType_Edge.class,
                Function_Use_Edge.class,
                ObjectType_IsPartOfEdge.class,
                OrganisationalUnit_IsPartOfEdge.class,
                OrganisationalUnit_Use_Edge.class,
                Process_Function_Edge.class,
                //FE - LWE
                ApplicationComponent_SupportLink_Edge.class,
                Function_SoftwareProduct_Edge.class,
                OrganisationalUnit_SupportLink_Edge.class,
                Service_Function_Edge.class,
                Service_ObjectType_Edge.class,
                StorageLink_Edge.class,
                SupportLink_Edge.class,
                //LWE
                ApplicationComponent_CommunicationInterface_Edge.class,
                ApplicationComponent_CommunicationLink_Edge.class,
                ApplicationComponent_IsPartOfEdge.class,
                ApplicationSystem_SoftwareProduct_Edge.class,
                CommunicationLink_Edge.class,
                Service_CommunicationLink_Edge.class,
                Service_InvokingInterface_Edge.class,
                Service_ProvidingInterface_Edge.class,
                Service_ServiceClass_Edge.class,
                ServiceUses_Edge.class,
                //IHE-Kanten
                IheActorOfIntegrationProfile_ApplicationSystem_Edge.class,
                IheConcept_IheDomain_Edge.class,
                IheIntegrationProfile_IheActorOfIntegrationProfile_Edge.class,
                IheIntegrationProfile_IheTransaction_Edge.class,
                IheTransaction_Service_Edge.class,
                IheTransactionLink_Edge.class,
                IheTransactionLink_IheTransaction_Edge.class,
                //LWE - PWE
                ApplicationComponent_PhysicalDataProcessingComponent_Edge.class,
                ApplicationComponent_PhysicalDataProcessingComponent_RequiresForFunctionality_Edge.class,
                ApplicationComponent_PhysicalDataProcessingComponent_RequiresForStorage_Edge.class,
                //PWE
                DataTransmissionLink_Edge.class,
                DeviceClass_IsPartOfEdge.class,
                Location_IsPartOfEdge.class,
                PhysicalDataProcessingComponent_DeviceClass_Edge.class,
                PhysicalDataProcessingComponent_IsPartOfEdge.class,
                PdvbVirtualPdvbVerbindung.class,
                PhysicalDataProcessingComponent_Location_Edge.class,
                PhysicalDataProcessingComponentVirtualises_Edge.class,
                TransmissionMedium_DataTransmissionLink_Edge.class,
        };
    }

    ///////////////////////////////////
    // spezielle Knoteneigenschaften //
    ///////////////////////////////////

    private final Class[] IMPORTABLE_NODES = {
            //            Aufgabe.class,
            //            Bausteintyp.class,
            //            DBVerwaltungssystem.class,
            //            Dokumententyp.class,
            //            Ereignistyp.class,
            //            KommBeziehung.class,
            //            Kommunikationsstandard.class,
            //            Nachrichtentyp.class,
            //            Netzprotokoll.class,
            //            Netztyp.class,
            //            Objekttyp.class,
            //            Organisationseinheit.class,
            //            Organisationsplan.class,
            //            KonAnwendungsbaustein.class,
            //            PhysischerDVBaustein.class,
            //            RechAnwendungsbaustein.class,
            //            Softwareprodukt.class,
            //            Standort.class,
            //            Subnetz.class,
    };

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    @Override
    public final Class<? extends ModelElement>[] getImportableNodes() {
        return IMPORTABLE_NODES;
    }

    ///////////////////////////////////
    // spezielle Kanteneigenschaften //
    ///////////////////////////////////

    /**
     * Liste aller Kantenklassen, die auch 2 gerichtete Assoziationen im Metamodell sein könnten, aber in eine Assoziation verpackt wurden,
     * bei denen die Richtung der Edge (Doppelkante.FORWARD, Doppelkante.BACKWARD, Doppelkante.DOUBLE) die Bedeutung angibt. Wegen dieser
     * Kanten braucht man den ganzen Kanten-Richtungsmechanismus. Wenn sie grafisch dargestellt werden, dann werden sie als eine Edge dargestellt,die
     * je nach Bedeutung eine der Richtungen oder beide als Pfeile darstellt.
     */
    @Override
    public final Set<Class<? extends Edge>> getDoubleMeaningEdgeClasses() {
        return ImmutableSet.<Class<? extends Edge>> of(
                //vorwärts1: bearbeitet; rückwärts1: wird bearbeitet von
                //vorwärts2: interpretiert; rückwärts2: wird interpretiert von
                Function_ObjectType_Edge.class,
                //vorwärts1: hat als Ergebnis; rückwärts1: ist Ergebnis von
                //vorwärts2: hat als Parameter; rückwärts2: ist Parameter von
                Service_ObjectType_Edge.class);
    }

    ///////////////////////////////////////////////////////////////////
    // Maps von Elementklassen auf Sets von Elementklassen (und mehr)//
    ///////////////////////////////////////////////////////////////////

    private final Set<Class<? extends ModelElement>> GENERATE_NAME_CLASSES = ImmutableSet.<Class<? extends ModelElement>> of();

    @Override
    public Set<Class<? extends ModelElement>> getGenerateNameClasses() {
        return GENERATE_NAME_CLASSES;
    }

}

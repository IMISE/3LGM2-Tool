package de.imise.tool3lgm.metamodel.service;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.consistency.ModelValidatorDefinition;
import de.imise.tool3lgm.graphtools.metamodel.AnalysesDefinition;
import de.imise.tool3lgm.graphtools.metamodel.CopyDependencies;
import de.imise.tool3lgm.graphtools.metamodel.ExtrasActionsDefinition;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.RegularMetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_PhysicalDataProcessingComponent_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_PhysicalDataProcessingComponent_RequiresForFunctionality_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_PhysicalDataProcessingComponent_RequiresForStorage_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.DataTransmissionLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.DeviceClass_HasPartEdge;
import de.imise.tool3lgm.metamodel.service.edge.Function_HasPartEdge;
import de.imise.tool3lgm.metamodel.service.edge.Function_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstanceCommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstance_IheActorInstanceInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstance_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActor_MustBeGroupedWith_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheCommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheCommunicationLink_IheActorInstanceCommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheActorInstanceInvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_IheActorInstanceProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheTransaction_IheCommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Location_HasPartEdge;
import de.imise.tool3lgm.metamodel.service.edge.ObjectType_HasPartEdge;
import de.imise.tool3lgm.metamodel.service.edge.OrganisationalUnit_HasPartEdge;
import de.imise.tool3lgm.metamodel.service.edge.OrganisationalUnit_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PartableApplicationComponent_CommunicationInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PartableApplicationComponent_HasPartEdge;
import de.imise.tool3lgm.metamodel.service.edge.PhysicalDataProcessingComponentVirtualises_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PhysicalDataProcessingComponent_DeviceClass_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PhysicalDataProcessingComponent_HasPartEdge;
import de.imise.tool3lgm.metamodel.service.edge.PhysicalDataProcessingComponent_Location_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Process_Function_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ServiceUses_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_CommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_Function_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_InvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_ProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_ServiceClass_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationComponent_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.service.edge.TransmissionMedium_DataTransmissionLink_Edge;
import de.imise.tool3lgm.metamodel.service.node.ApplicationComponent;
import de.imise.tool3lgm.metamodel.service.node.ApplicationSystem;
import de.imise.tool3lgm.metamodel.service.node.CommunicationInterface;
import de.imise.tool3lgm.metamodel.service.node.DeviceClass;
import de.imise.tool3lgm.metamodel.service.node.Function;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstance;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstanceInterface;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstanceInvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstanceProvidingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheDomain;
import de.imise.tool3lgm.metamodel.service.node.IheIntegrationProfile;
import de.imise.tool3lgm.metamodel.service.node.IheInterface;
import de.imise.tool3lgm.metamodel.service.node.IheInvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheProvidingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheTransaction;
import de.imise.tool3lgm.metamodel.service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.Location;
import de.imise.tool3lgm.metamodel.service.node.ObjectType;
import de.imise.tool3lgm.metamodel.service.node.OrganisationSystem;
import de.imise.tool3lgm.metamodel.service.node.OrganisationalUnit;
import de.imise.tool3lgm.metamodel.service.node.PhysicalDataProcessingComponent;
import de.imise.tool3lgm.metamodel.service.node.Process;
import de.imise.tool3lgm.metamodel.service.node.ProvidingInterface;
import de.imise.tool3lgm.metamodel.service.node.Service;
import de.imise.tool3lgm.metamodel.service.node.ServiceClass;
import de.imise.tool3lgm.metamodel.service.node.SoftwareProduct;
import de.imise.tool3lgm.metamodel.service.node.SystemOfConcepts;
import de.imise.tool3lgm.metamodel.service.node.TransmissionMedium;
import de.imise.tool3lgm.metamodel.service.node.Use;

@SuppressWarnings({
        "unchecked", "rawtypes"
})
public class TLGMServiceMetaModel extends MetaModelDefinition implements RegularMetaModelDefinition {

    /**
     * ID des Metamodells für die Serialisierung.
     */
    public static final long serialVersionUID = 2388259974838049670L;

    @Override
    protected final void putOldToNewClassNames() {
        putOldToNewClassName("Aufgabe", "Function");
        putOldToNewClassName("Objekttyp", "ObjectType");
        putOldToNewClassName("Organisationseinheit", "OrganisationalUnit");
        putOldToNewClassName("AufOrgKombination", "Use");
        putOldToNewClassName("RechAnwendungsbaustein", "ApplicationSystem");
        putOldToNewClassName("KonAnwendungsbaustein", "OrganisationSystem");
        putOldToNewClassName("Standort", "Location");
        putOldToNewClassName("PhysischerDVBaustein", "PhysicalDataProcessingComponent");
        putOldToNewClassName("Prozess", "Process");
        putOldToNewClassName("Softwareprodukt", "SoftwareProduct");

        putOldToNewClassName("AufAufOrgVerbindung", "Function_Use_Edge");
        putOldToNewClassName("AufAufVerbindung", "Function_HasPartEdge");
        putOldToNewClassName("AufObjVerbindung", "Function_ObjectType_Edge");
        putOldToNewClassName("KawbAwbVerbindung", "OrganisationSystem_ApplicationSystem_HasPartEdge"); //das haut nur hin ohne Fehler, wenn die KawVerbindung auch ein KonAnwendungsbaustein mit einem RechAnwendungsbaustein verbunden hat
        putOldToNewClassName("RawbRawbVerbindung", "ApplicationSystem_HasPartEdge");
        putOldToNewClassName("ObjObjVerbindung", "ObjectType_HasPartEdge");
        putOldToNewClassName("", "");
        putOldToNewClassName("", "");
        putOldToNewClassName("", "");
        putOldToNewClassName("", "");
        putOldToNewClassName("", "");

        //        putOldToNewClassName("RawbAwbVerbindung", "RawbRawbVerbindung");
        //        putOldToNewClassName("EtntKombination", "EreignisNachrichtenTyp");
        //        putOldToNewClassName("EtdtKombination", "EreignisDokumentenTyp");
        //        putOldToNewClassName("ETNTKombination", "EreignisNachrichtenTyp");
        //        putOldToNewClassName("ETDTKombination", "EreignisDokumentenTyp");
        //        putOldToNewClassName("AwbKawbVerbindung", "KawbAwbVerbindung");
    }

    /////////////////////
    // PathsDefinition //
    /////////////////////

    @Override
    public final Class<? extends MetaPathDefinition> getMetaPathsDefinitionClass() {
        return TLGMServiceMetaPathsDefinition.class;
    }

    /////////////////////////
    // GraphViewDefinition //
    /////////////////////////

    @Override
    public final Class<? extends GraphViewDefinition> getGraphViewDefinitionClass() {
        return TLGMServiceGraphViewDefinion.class;
    }

    //////////////////////
    // CopyDependencies //
    //////////////////////

    @Override
    public final Class<? extends CopyDependencies> getCopyDependenciesClass() {
        return TLGMServiceCopyDependencies.class;
    }

    ////////////////////////
    // AnalysisDefinition //
    ////////////////////////

    @Override
    public final Class<? extends AnalysesDefinition> getAnalysesDefinitionClass() {
        //im Moment hat dieses Metamodell keine eigene AnalysesDefinition. Man könnte diese Funktion auch weglassen.
        return super.getAnalysesDefinitionClass();
    }

    /////////////////////////////
    // ExtrasActionsDefinition //
    /////////////////////////////

    @Override
    public Class<? extends ExtrasActionsDefinition> getExtrasActionsDefinitionClass() {
        //im Moment hat dieses Metamodell keine eigene Pfaddefinition. Man könnte diese Funktion auch weglassen.
        return super.getExtrasActionsDefinitionClass();
    }

    //////////////////////////////
    // ModelValidatorDefinition //
    //////////////////////////////

    @Override
    public Class<? extends ModelValidatorDefinition> getModelValidatorDefinitionClass() {
        return TLGMServiceModelValidatorDefinition.class;
    }

    ////////////
    // Node //
    ////////////

    /** Alle Node der FE als Array */
    private static final Class[] ALL_DOMAIN_LAYER_NODES = {
            Function.class, ObjectType.class, OrganisationalUnit.class, Use.class, SystemOfConcepts.class, Process.class, // es gibt im lang-package ebenfalls eine Klasse Process
    };

    /** Alle Node zw. FE und LWE als Array */
    private final Class[] ALL_INTER_DOMAIN_LOGICAL_LAYER_NODES = {
            //auch die Assoziationsklasse hier eintagen
            ApplicationComponent_ObjectType_Edge.class,
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
            DataTransmissionLink_Edge.class,

            //abstracte Node müssen hier auch eingetragen werden
            ApplicationComponent.class,
            CommunicationInterface.class,

            //IHE-Konzepte
            IheActor.class,
            IheActorInstance.class,
            IheDomain.class,
            IheIntegrationProfile.class,
            IheTransaction.class,
            IheInterface.class,
            IheInvokingInterface.class,
            IheProvidingInterface.class,
            IheActorInstanceInterface.class,
            IheActorInstanceInvokingInterface.class,
            IheActorInstanceProvidingInterface.class,

            //IHE-Assoziationsklassen
            IheCommunicationLink_Edge.class,
            IheActorInstanceCommunicationLink_Edge.class,
    };

    /** Alle Node zw. LWE und PWE als Array */
    private final Class[] ALL_INTER_LOGICAL_PHYSICAL_LAYER_NODES = {};

    /** Alle Node der PWE als Array */
    private final Class[] ALL_PHYSICAL_LAYER_NODES = {
            PhysicalDataProcessingComponent.class, DeviceClass.class, Location.class, DataTransmissionLink_Edge.class, TransmissionMedium.class,
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
                //ApplicationComponent.class, //im Moment deaktiviert
        };
    }

    @Override
    public Set<Class<? extends ModelElement>> getOnlyExpertModeVisibleNodes() {
        //hier wird nur mit contains(class) gerpüft -> immer auch die Oberklassen, die versteckt werden sollen reinschreiben
        return ImmutableSet.of(Use.class, IheInterface.class, IheInvokingInterface.class, IheProvidingInterface.class);
    }

    @Override
    public Set<Class<? extends ModelElement>> getPureTemplateSourceNodes() {
        return ImmutableSet.of(IheActor.class, IheDomain.class, IheIntegrationProfile.class, IheInterface.class, IheInvokingInterface.class, IheProvidingInterface.class, IheTransaction.class);
    }

    ////////////
    // Kanten //
    ////////////

    @Override
    public Class[] getAllEdges() {
        return new Class[] {
                //FE
                Function_HasPartEdge.class,
                Function_ObjectType_Edge.class,
                Function_Use_Edge.class,
                ObjectType_HasPartEdge.class,
                OrganisationalUnit_HasPartEdge.class,
                OrganisationalUnit_Use_Edge.class,
                Process_Function_Edge.class,
                //FE - LWE
                Function_SoftwareProduct_Edge.class,
                Service_Function_Edge.class,
                Service_ObjectType_Edge.class,
                ApplicationComponent_ObjectType_Edge.class,
                ApplicationComponent_Use_Edge.class,
                //LWE
                PartableApplicationComponent_HasPartEdge.class,
                PartableApplicationComponent_CommunicationInterface_Edge.class,
                ApplicationSystem_SoftwareProduct_Edge.class,
                CommunicationLink_Edge.class,
                Service_CommunicationLink_Edge.class,
                Service_InvokingInterface_Edge.class,
                Service_ProvidingInterface_Edge.class,
                Service_ServiceClass_Edge.class,
                ServiceUses_Edge.class,
                //IHE-Kanten
                IheActor_IheActor_MustBeGroupedWith_Edge.class,
                IheActor_IheActorInstance_Edge.class,
                IheActor_IheInterface_Edge.class,
                ApplicationSystem_IheActorInstance_Edge.class,
                IheCommunicationLink_IheActorInstanceCommunicationLink_Edge.class,
                IheIntegrationProfile_IheDomain_Edge.class,
                IheIntegrationProfile_IheActor_Edge.class,
                IheInvokingInterface_IheTransaction_Edge.class,
                IheProvidingInterface_IheTransaction_Edge.class,
                IheInvokingInterface_IheActorInstanceInvokingInterface_Edge.class,
                IheProvidingInterface_IheActorInstanceProvidingInterface_Edge.class,
                IheCommunicationLink_Edge.class,
                IheTransaction_IheCommunicationLink_Edge.class,
                IheActorInstance_IheActorInstanceInterface_Edge.class,
                IheActorInstanceCommunicationLink_Edge.class,
                IheActorInstance_SoftwareProduct_Edge.class,

                //LWE - PWE
                ApplicationComponent_PhysicalDataProcessingComponent_Edge.class,
                ApplicationComponent_PhysicalDataProcessingComponent_RequiresForFunctionality_Edge.class,
                ApplicationComponent_PhysicalDataProcessingComponent_RequiresForStorage_Edge.class,
                //PWE
                DataTransmissionLink_Edge.class,
                DeviceClass_HasPartEdge.class,
                Location_HasPartEdge.class,
                PhysicalDataProcessingComponent_DeviceClass_Edge.class,
                PhysicalDataProcessingComponent_HasPartEdge.class,
                PhysicalDataProcessingComponent_Location_Edge.class,
                PhysicalDataProcessingComponentVirtualises_Edge.class,
                TransmissionMedium_DataTransmissionLink_Edge.class,
        };
    }

    ///////////////////////////////////
    // spezielle Knoteneigenschaften //
    ///////////////////////////////////

    private final Set<Class<? extends ModelElement>> IMPORTABLE_NODES = ImmutableSet.of(
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
    );

    /** Alle Klassen, die man über den Datenimport einlesen kann */
    @Override
    public final Set<Class<? extends ModelElement>> getImportableNodes() {
        return IMPORTABLE_NODES;
    }

    private final Set<Class<? extends ModelElement>> GENERATE_NAME_CLASSES = ImmutableSet.<Class<? extends ModelElement>> of(Use.class);

    @Override
    public Set<Class<? extends ModelElement>> getGenerateNameClasses() {
        return GENERATE_NAME_CLASSES;
    }

}

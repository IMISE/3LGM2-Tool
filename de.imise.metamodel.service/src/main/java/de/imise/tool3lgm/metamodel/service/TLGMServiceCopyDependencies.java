package de.imise.tool3lgm.metamodel.service;

import de.imise.tool3lgm.graphtools.metamodel.CopyDependencies;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstance_IheActorInstanceInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActorInstance_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActor_MustBeGroupedWith_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PartableApplicationComponent_CommunicationInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PhysicalDataProcessingComponent_DeviceClass_Edge;
import de.imise.tool3lgm.metamodel.service.edge.PhysicalDataProcessingComponent_Location_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Process_Function_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_InvokingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_ProvidingInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Service_ServiceClass_Edge;
import de.imise.tool3lgm.metamodel.service.node.ApplicationSystem;
import de.imise.tool3lgm.metamodel.service.node.DeviceClass;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstance;
import de.imise.tool3lgm.metamodel.service.node.IheDomain;
import de.imise.tool3lgm.metamodel.service.node.IheIntegrationProfile;
import de.imise.tool3lgm.metamodel.service.node.IheInvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheProvidingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheTransaction;
import de.imise.tool3lgm.metamodel.service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.Location;
import de.imise.tool3lgm.metamodel.service.node.OrganisationSystem;
import de.imise.tool3lgm.metamodel.service.node.PhysicalDataProcessingComponent;
import de.imise.tool3lgm.metamodel.service.node.Process;
import de.imise.tool3lgm.metamodel.service.node.ProvidingInterface;
import de.imise.tool3lgm.metamodel.service.node.Service;
import de.imise.tool3lgm.metamodel.service.node.ServiceClass;
import de.imise.tool3lgm.metamodel.service.node.SoftwareProduct;

/**
 * @author AXS (24.09.2019)
 */
public class TLGMServiceCopyDependencies extends CopyDependencies {

    /**
     * @param metaModel
     */
    public TLGMServiceCopyDependencies(final MetaModel metaModel) {
        super(metaModel);
        initCopyDepenencies();
        initAvoidDuplicates();
    }

    @SuppressWarnings("unchecked")
    private void initCopyDepenencies() {
        //Process -> Function
        set(Process.class, Process_Function_Edge.class);
        //ApplicationSystem -> SoftwareProduct, CommunicationInterface, IheActorInstance
        set(ApplicationSystem.class, ApplicationSystem_SoftwareProduct_Edge.class, PartableApplicationComponent_CommunicationInterface_Edge.class, ApplicationSystem_IheActorInstance_Edge.class);
        //(OrganisationSystem -> CommunicationInterface
        set(OrganisationSystem.class, PartableApplicationComponent_CommunicationInterface_Edge.class);
        //PhysicalDataProcessingComponent -> Location, DeviceClass
        set(PhysicalDataProcessingComponent.class, PhysicalDataProcessingComponent_Location_Edge.class, PhysicalDataProcessingComponent_DeviceClass_Edge.class);
        //Service -> ServiceClass
        set(Service.class, Service_ServiceClass_Edge.class);
        //IheActorInstance -> IheActorInstanceInterface, IheActor, SoftwareProduct
        set(IheActorInstance.class, IheActorInstance_IheActorInstanceInterface_Edge.class, IheActor_IheActorInstance_Edge.class, IheActorInstance_SoftwareProduct_Edge.class);
        //InvokingInterface -> Service
        set(InvokingInterface.class, Service_InvokingInterface_Edge.class);
        //ProvidingInterface -> Service
        set(ProvidingInterface.class, Service_ProvidingInterface_Edge.class);

        //IheDomain.class -> IheIntegrationProfile.class
        set(IheDomain.class, IheIntegrationProfile_IheDomain_Edge.class);
        //IheIntegrationProfile.class -> IheDomain.class, IheActor.class
        set(IheIntegrationProfile.class, IheIntegrationProfile_IheDomain_Edge.class, IheIntegrationProfile_IheActor_Edge.class);
        //IheActor.class -> IheIntegrationProfile.class, IheInvokingInterface.class, IheActor.class, IheProvidingInterface.class
        set(IheActor.class, IheIntegrationProfile_IheActor_Edge.class, IheActor_IheInterface_Edge.class, IheActor_IheActor_MustBeGroupedWith_Edge.class);
        //IheInvokingInterface.class -> IheActor.class, IheTransaction.class
        set(IheInvokingInterface.class, IheActor_IheInterface_Edge.class, IheInterface_IheTransaction_Edge.class);
        //IheProvidingInterface.class -> IheActor.class, IheTransaction.class
        set(IheProvidingInterface.class, IheActor_IheInterface_Edge.class, IheInterface_IheTransaction_Edge.class);
    }

    private void initAvoidDuplicates() {
        addToAvoidDuplicates(IheDomain.class);
        addToAvoidDuplicates(IheIntegrationProfile.class);
        addToAvoidDuplicates(IheActor.class);
        addToAvoidDuplicates(IheInvokingInterface.class);
        addToAvoidDuplicates(IheProvidingInterface.class);
        addToAvoidDuplicates(IheTransaction.class);
        addToAvoidDuplicates(Service.class);
        addToAvoidDuplicates(SoftwareProduct.class);
        addToAvoidDuplicates(IheActor.class);
        addToAvoidDuplicates(ServiceClass.class);
        addToAvoidDuplicates(DeviceClass.class);
        addToAvoidDuplicates(Location.class);
    }

}

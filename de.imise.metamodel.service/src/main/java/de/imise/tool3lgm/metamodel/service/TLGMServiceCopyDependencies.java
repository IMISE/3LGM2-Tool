package de.imise.tool3lgm.metamodel.service;

import de.imise.tool3lgm.graphtools.metamodel.CopyDependencies;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActor_MustBeGroupedWith_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheDomain;
import de.imise.tool3lgm.metamodel.service.node.IheIntegrationProfile;
import de.imise.tool3lgm.metamodel.service.node.IheInvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheProvidingInterface;

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
        //        set(IheDomain.class, IheIntegrationProfile.class);
        //        set(IheIntegrationProfile.class, IheDomain.class, IheActor.class);
        //        set(IheActor.class, IheIntegrationProfile.class, IheInvokingInterface.class, IheActor.class, IheProvidingInterface.class);
        //        set(IheInvokingInterface.class, IheActor.class, IheTransaction.class);
        //        set(IheProvidingInterface.class, IheActor.class, IheTransaction.class);
        set(IheDomain.class, IheIntegrationProfile_IheDomain_Edge.class);
        set(IheIntegrationProfile.class, IheIntegrationProfile_IheDomain_Edge.class, IheIntegrationProfile_IheActor_Edge.class);
        set(IheActor.class, IheIntegrationProfile_IheActor_Edge.class, IheActor_IheInterface_Edge.class, IheActor_IheActor_MustBeGroupedWith_Edge.class);
        set(IheInvokingInterface.class, IheActor_IheInterface_Edge.class, IheInterface_IheTransaction_Edge.class);
        set(IheProvidingInterface.class, IheActor_IheInterface_Edge.class, IheInterface_IheTransaction_Edge.class);
    }

    private void initAvoidDuplicates() {
        //        addToAvoidDuplicates(Bausteintyp.class);
        //        addToAvoidDuplicates(DBVerwaltungssystem.class);
        //        addToAvoidDuplicates(Dokumententyp.class);
        //        addToAvoidDuplicates(Ereignistyp.class);
        //        addToAvoidDuplicates(EreignisDokumentenTyp.class);
        //        addToAvoidDuplicates(EreignisNachrichtenTyp.class);
        //        addToAvoidDuplicates(Kommunikationsstandard.class);
        //        addToAvoidDuplicates(Nachrichtentyp.class);
        //        addToAvoidDuplicates(Netzprotokoll.class);
        //        addToAvoidDuplicates(Netztyp.class);
        //        addToAvoidDuplicates(Softwareprodukt.class);
        //        addToAvoidDuplicates(Standort.class);
        //        addToAvoidDuplicates(Subnetz.class);
    }

}

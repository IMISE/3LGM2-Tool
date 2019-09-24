package de.imise.tool3lgm.metamodel.service;

import de.imise.tool3lgm.graphtools.metamodel.CopyDependencies;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheDomain;
import de.imise.tool3lgm.metamodel.service.node.IheIntegrationProfile;
import de.imise.tool3lgm.metamodel.service.node.IheInvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheProvidingInterface;
import de.imise.tool3lgm.metamodel.service.node.IheTransaction;

/**
 * @author AXS (24.09.2019)
 */
public class TLGMServiceCopyDependencies extends CopyDependencies {

    /**
     *
     */
    public TLGMServiceCopyDependencies() {
        initCopyDepenencies();
        initAvoidDuplicates();
    }

    @SuppressWarnings("unchecked")
    private void initCopyDepenencies() {
        set(IheDomain.class, IheIntegrationProfile.class);
        set(IheIntegrationProfile.class, IheDomain.class, IheActor.class);
        set(IheActor.class, IheIntegrationProfile.class, IheInvokingInterface.class, IheProvidingInterface.class);
        set(IheInvokingInterface.class, IheActor.class, IheTransaction.class);
        set(IheProvidingInterface.class, IheActor.class, IheTransaction.class);
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

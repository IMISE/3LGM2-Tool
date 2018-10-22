package de.imise.tool3lgm.metamodel.tlgm_v3_0.action;

import java.util.List;

import javax.swing.Action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.action.GraphDocumentAction;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.PathConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionChangeType;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KommBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KommbezEtntVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Bausteinschnittstelle;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EtntEtdtKombination;
import de.imise.util.SimpleResourceHandler;

public class ExtrasActions {

    private final SimpleResourceHandler resHandler = new SimpleResourceHandler(getClass());

    public Action[] getActions() {
        return new Action[] {
                getRelinkETNTAction()
        };
    }

    private Action getRelinkETNTAction() {
        GraphDocumentAction relinkETNT = new GraphDocumentAction(getClass(), null, resHandler.getString("ACTION_NAME_RELINK_ETNT"), null) {
            @Override
            public void actionPerformed() {
                LGMGraphDocument doc = Static.getSelectedDoc();
                GDCollection gdcoll = doc.getCollection();
                doc.start_transaction(TransactionManager.STANDARD_PID);
                List<ModelElement> all = doc.getModelItems(KommBeziehung.class);
                for (int i = 0; i < all.size(); i++) {
                    KommBeziehung kz = (KommBeziehung) all.get(i);
                    Bausteinschnittstelle bs1 = (Bausteinschnittstelle) kz.getStart();
                    Bausteinschnittstelle bs2 = (Bausteinschnittstelle) kz.getEnd();
                    //hin
                    List<ElementContainer> empf = bs2.getConnectedContainer(EtntEtdtKombination.class, doc, null, PathConnectionState.FROM_ELEMENT);
                    for (ElementContainer kc : bs1.getConnectedContainer(EtntEtdtKombination.class, doc, null, PathConnectionState.TO_ELEMENT)) {
                        if (empf.contains(kc)) {
                            gdcoll.link(KommbezEtntVerbindung.class, kc.getElement(), kz, TransactionManager.STANDARD_PID);
                        }
                    }
                    //zurück
                    empf = bs1.getConnectedContainer(EtntEtdtKombination.class, doc, null, PathConnectionState.FROM_ELEMENT);
                    for (ElementContainer kc : bs2.getConnectedContainer(EtntEtdtKombination.class, doc, null, PathConnectionState.TO_ELEMENT)) {
                        if (empf.contains(kc)) {
                            gdcoll.link(KommbezEtntVerbindung.class, kz, kc.getElement(), TransactionManager.STANDARD_PID);
                        }
                    }
                }
                doc.finish_transaction(TransactionManager.STANDARD_PID);
                doc.distributeEvent(GDCollectionChangeType.DATA_CHANGED);
            }
        };
        relinkETNT.setShortDescription(resHandler.getString("ACTION_TOOLTIP_RELINK_ETNT"));
        return relinkETNT;
    }

}

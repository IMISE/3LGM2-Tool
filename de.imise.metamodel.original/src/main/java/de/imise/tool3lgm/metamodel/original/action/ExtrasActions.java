package de.imise.tool3lgm.metamodel.original.action;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction.FORWARD;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.util.List;

import javax.swing.Action;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.event.action.GraphDocumentAction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.metamodel.original.edge.KommBeziehung;
import de.imise.tool3lgm.metamodel.original.edge.KommbezEtntVerbindung;
import de.imise.tool3lgm.metamodel.original.node.Bausteinschnittstelle;
import de.imise.tool3lgm.metamodel.original.node.EtntEtdtKombination;
import de.imise.util.resource.SimpleResourceBundleSource;
import de.imise.util.resource.SimpleResourceBundleSourceAdapter;

public class ExtrasActions {

    private final SimpleResourceBundleSource resHandler = new SimpleResourceBundleSourceAdapter(getClass());

    public Action[] getActions() {
        return new Action[] {
                getRelinkETNTAction()
        };
    }

    @SuppressWarnings("serial")
    private Action getRelinkETNTAction() {
        GraphDocumentAction relinkETNT = new GraphDocumentAction(getClass(), null, resHandler.getResString("ACTION_NAME_RELINK_ETNT"), null) {
            @Override
            public void actionPerformed() {
                LGMGraphDocument doc = Static.getSelectedDoc();
                GDCollection gdcoll = doc.getCollection();
                doc.start_transaction(STANDARD_PID);
                List<ModelElement> all = doc.getModelItems(KommBeziehung.class);
                for (int i = 0; i < all.size(); i++) {
                    KommBeziehung kz = (KommBeziehung) all.get(i);
                    Bausteinschnittstelle bs1 = (Bausteinschnittstelle) kz.getStart();
                    Bausteinschnittstelle bs2 = (Bausteinschnittstelle) kz.getEnd();
                    //hin
                    List<ElementContainer> empf = bs2.getConnectedContainers(EtntEtdtKombination.class, doc, null, FORWARD);
                    for (ElementContainer kc : bs1.getConnectedContainers(EtntEtdtKombination.class, doc, null, BACKWARD)) {
                        if (empf.contains(kc)) {
                            gdcoll.link(KommbezEtntVerbindung.class, kc.getElement(), kz, STANDARD_PID);
                        }
                    }
                    //zurück
                    empf = bs1.getConnectedContainers(EtntEtdtKombination.class, doc, null, FORWARD);
                    for (ElementContainer kc : bs2.getConnectedContainers(EtntEtdtKombination.class, doc, null, BACKWARD)) {
                        if (empf.contains(kc)) {
                            gdcoll.link(KommbezEtntVerbindung.class, kz, kc.getElement(), STANDARD_PID);
                        }
                    }
                }
                doc.finish_transaction(STANDARD_PID);
                doc.distributeEvent(DATA_CHANGED);
            }
        };
        relinkETNT.setShortDescription(resHandler.getResString("ACTION_TOOLTIP_RELINK_ETNT"));
        return relinkETNT;
    }

}

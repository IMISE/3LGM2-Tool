package de.imise.tool3lgm.metamodel.tlgm_v3_0.analyse;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.FORWARD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.analyse.context.AbstractAnalyse;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KommBeziehung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.KommbezEtntVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Bausteinschnittstelle;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.EtntEtdtKombination;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Repraesentationsform;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Schnittstelle;
import de.imise.util.SimpleResourceHandler;
import de.imise.util.collections.CollectionUtils;
import de.imise.util.swing.component.list.AlphabeticalJList;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * @author AXS
 * @create 14.10.2011
 */
public class InterfaceCanSendOTAnalysis extends AbstractAnalyse {

    /**
     *
     */
    public InterfaceCanSendOTAnalysis() {
        super();
        startknoten.add(Bausteinschnittstelle.class);
        SimpleResourceHandler resHandler = new SimpleResourceHandler(getClass());
        name = resHandler.getString("ANALYSIS_NAME");
    }

    @Override
    public List<ElementContainer> getResult(final GraphDocument doc) {
        Set<ModelElement> result = new HashSet<>();
        List<ModelElement> selectedInterfaces = new ArrayList<>();
        for (ModelElement me : doc.getSelectedElements()) {
            if (me instanceof Bausteinschnittstelle) {
                selectedInterfaces.add(me);
            }
        }
        Set<ModelElement> connectedObjectTypes = new HashSet<>();
        for (ModelElement bs : selectedInterfaces) {
            connectedObjectTypes.addAll(getSendableObjectTypes(bs));
        }

        SimpleResourceHandler resHandler = new SimpleResourceHandler(getClass());

        // die Schnittstelle versendet nichts
        if (connectedObjectTypes.size() == 0) {
            MultipleOptionPane.showInformationMessageDialog(Static.getMainFrame(), resHandler.getString("NOT_SENDING_INTERFACE_DIALOG_TITLE"), resHandler.getString("NOT_SENDING_INTERFACE_DIALOG_MESSAGE"));
            return null;
        }
        // Dialog zur Auswahl der interessierenden OT anbieten
        AlphabeticalJList objectTypeList = new AlphabeticalJList(connectedObjectTypes);
        objectTypeList.setBorder(BorderFactory.createEtchedBorder());
        if (new MultipleOptionPane().showComponentDialog(Static.getMainFrame(), resHandler.getString("CHOOSE_OBJECT_TYPE_DIALOG_TITLE"), resHandler.getString("CHOOSE_OBJECT_TYPE_DIALOG_TITLE"), objectTypeList) != MultipleOptionPane.OK_OPTION) {
            return null;
        }
        List<ModelElement> objectTypes = new ArrayList<>();
        for (Object o : objectTypeList.getSelectedValues()) {
            objectTypes.add((ModelElement) o);
        }
        // nichts ausgewählt
        if (objectTypes.size() == 0) {
            return null;
        }
        result.addAll(objectTypes);

        Set<ModelElement> testedBS = new HashSet<>();
        // Alle Schnittstellen des
        for (int i = 0; i < selectedInterfaces.size(); i++) {
            ModelElement bs = selectedInterfaces.get(i);
            if (testedBS.contains(bs)) {
                continue;
            }
            testedBS.add(bs);
            for (ModelElement commLink : getSendingCommunicationLinks(bs, connectedObjectTypes)) {
                // die Ausgangsschnittstelle nur zum Result hinzufügen, wenn sie wenigestens eine
                // SendeBezihung zu einer
                // Empfangsschnittstelle hat (dass sie in jedem Durchlauf hier nochmal hinzugefügt
                // wird ist nicht schlimm,
                // da result ein Set ist und sie somit nur einmal darin vorkommt
                result.add(bs);
                ModelElement otherBS = ((Edge) commLink).getOther(bs);
                result.add(commLink);
                if (testedBS.contains(otherBS)) {
                    continue;
                }
                testedBS.add(otherBS);
                result.add(otherBS);
                // AWB der Empfangsschnittstelle holen
                List<ModelElement> allAwbOfBs = otherBS.getConnectedElements(Anwendungsbaustein.class, AwbKommssVerbindung.class);
                // Liste um alle AWB erweitern, die zum selben Baustein gehören (alle Parts, Parents
                // und Geschwister des AWB)
                for (int j = allAwbOfBs.size() - 1; j >= 0; j--) {
                    allAwbOfBs.addAll(allAwbOfBs.get(j).getPartAndParentElements());
                }
                // alle Schnittstellen des Gesamt-AWB zu den ursprünglich selektierten hinzufügen
                for (ModelElement awb : allAwbOfBs) {
                    CollectionUtils.addNonMultiples(selectedInterfaces, awb.getConnectedElements(Schnittstelle.class, AwbKommssVerbindung.class));
                }
            }
        }
        List<ElementContainer> resultList = new ArrayList<>(result.size());
        for (ModelElement me : result) {
            ElementContainer ec = me.getContainer(doc);
            if (ec != null) {
                resultList.add(ec);
            }
        }
        return resultList;
    }

    /**
     * Liefert alle Objekttypen, die über Kommunikationsbeziehungen, die von der übergebenen
     * Schnittstelle ausgehen, versendet werden können.
     *
     * @param bs
     * @return
     */
    private static final List<ModelElement> getSendableObjectTypes(final ModelElement bs) {
        List<ModelElement> returnList = new ArrayList<>();
        List<Edge> kommBeziehungen = bs.getEdges(KommBeziehung.class);
        for (ModelElement kommBez : kommBeziehungen) {
            returnList.addAll(getSendableObjectTypes(bs, (Edge) kommBez));
        }
        return returnList;
    }

    /**
     * Liefert für die übergebene Kommunikationsbeziehung alle Objekttypen, die die übergebene
     * Schnittstelle über sie versendet.
     *
     * @param bs
     * @param communicationLink
     * @return
     */
    private static final List<ModelElement> getSendableObjectTypes(final ModelElement bs, final Edge communicationLink) {
        List<ModelElement> returnList = new ArrayList<>();
        int direction = communicationLink.getStart() == bs ? BACKWARD : FORWARD;
        for (ModelElement etnt : communicationLink.getConnectedElements(EtntEtdtKombination.class, KommbezEtntVerbindung.class, direction)) {
            for (ModelElement ntdt : etnt.getConnectedElements(Repraesentationsform.class)) {
                returnList.addAll(ntdt.getConnectedElements(Objekttyp.class));
            }
        }
        return returnList;
    }

    /**
     * Liefert für eine übergebene Schnittstelle alle Kommunikatinsbezeihungen, die die übergebenen
     * Objekttypen versenden können.
     *
     * @param bs
     * @param objectTypes
     * @return
     */
    private static final List<ModelElement> getSendingCommunicationLinks(final ModelElement bs, final Collection<ModelElement> objectTypes) {
        List<ModelElement> returnList = new ArrayList<>();
        for (ModelElement commLink : bs.getEdges(KommBeziehung.class)) {
            List<ModelElement> sendableObjectTypes = getSendableObjectTypes(bs, (Edge) commLink);
            sendableObjectTypes.retainAll(objectTypes);
            if (sendableObjectTypes.size() > 0) {
                returnList.add(commLink);
            }
        }
        return returnList;
    }

}

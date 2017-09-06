package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.EtAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.OrgAufOrgVerbindung;

public final class Aufgabe extends Node {

    public Aufgabe() {
        super();
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(AufObjVerbindung.class);
        dialog.addPathConnectionLeafPanel(AufAufOrgVerbindung.class, OrgAufOrgVerbindung.class);
        dialog.addPathConnectionPanel(EtAufVerbindung.class);
        dialog.addPathConnectionPanel(AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class);
        return dialog;
    }

    /**
     * Gibt eine Liste aller ABKonfigurationConatiner zurueck, die mit dieser Aufgabe, bzw. je nach uebergebener Option mit ihren Parents oder Parts
     * verknüpft sind.
     *
     * @param doc Graphdocument
     * @return ArrayList aller im uebergebenen Graphdocument existierenden Konfigurationen der Aufgabe
     */
    public List<ElementContainer> getAllKonfigs(final GraphDocument doc, List<ElementContainer> allPartsAndParents) {
        List<ElementContainer> returnList = new ArrayList<>();
        if (allPartsAndParents == null) {
            allPartsAndParents = new ArrayList<>();
        }

        List<ElementContainer> aufOrg = getConnectedContainer(AufOrgKombination.class, doc);
        for (int i = 0; i < aufOrg.size(); i++) {
            List<ElementContainer> konfigs = ((NodeContainer) aufOrg.get(i)).getKnoten().getConnectedContainer(ABKonfiguration.class, doc);
            returnList.addAll(konfigs);
        }
        //Liste in die die NodeContainer der Aufgaben kommen, von denen diese
        // Aufgabe Konfigurationen erbt
        List<ElementContainer> partsAndParents = new ArrayList<>();
        for (ElementContainer parent : getParentContainer(doc, false)) {
            if (!allPartsAndParents.contains(parent)) {
                partsAndParents.add(parent);
                allPartsAndParents.add(parent);
            }
        }
        //für jeden Container von dem geerbt wird
        for (ElementContainer ppc : partsAndParents) {
            Aufgabe ppAuf = (Aufgabe) ppc.getElement();
            //alle Konfigurationen holen
            List<ElementContainer> konfigs = ppAuf.getAllKonfigs(doc, allPartsAndParents);
            returnList.addAll(konfigs);
        }
        return returnList;
    }

    /**
     * Gibt eine Liste aller ABKonfigurationConatiner zurueck, die mit dieser Aufgabe, bzw. je nach uebergebener Option mit ihren Parents oder Parts
     * verknüpft sind. Es sind nur die zuerst gefundenen Konfigurationen enthalten, die sich bezueglich ihrer
     * Liste der Anwendungsbausteine unterscheiden.
     *
     * @param doc Graphdocument
     * @return ArrayList aller im uebergebenen Graphdocument existierenden Konfigurationen der Aufgabe
     */
    //    private List<ElementContainer> getAllDifferentKonfigs(final GraphDocument doc) {
    //        List<ElementContainer> returnList = getAllKonfigs(doc, null);
    //        int size = returnList.size();
    //        ABKonfiguration[] konfigs = new ABKonfiguration[size];
    //        for (int i = 0; i < size; i++) {
    //            konfigs[i] = (ABKonfiguration) returnList.get(i).getElement();
    //        }
    //        for (int i = 0; i < size; i++) {
    //            if (konfigs[i] == null) {
    //                continue;
    //            }
    //            for (int j = i + 1; j < size; j++) {
    //                if (konfigs[j] == null) {
    //                    continue;
    //                }
    //                if (konfigs[i].hasSameServer(konfigs[j], doc)) {
    //                    konfigs[j] = null;
    //                }
    //            }
    //        }
    //        //hier braucht man nur bis zum 1. Element gehen, da das 0. nach dem
    //        // oberen Verfahren nie null sein kann
    //        for (int i = size - 1; i >= 0; i--) {
    //            if (konfigs[i] == null) {
    //                returnList.remove(i);
    //            }
    //        }
    //        return returnList;
    //    }
    //
    //    @Override
    //    public List<ElementContainer> getRedundanceTypes(final GraphDocument doc) {
    //        return getAllDifferentKonfigs(doc);
    //    }
    //
    //    @Override
    //    public String getRedundanceString(final float redundance, final float saturation) {
    //        StringBuilder sb = new StringBuilder(40);
    //        sb.append(Tool3lgmConstants.getResString("redundancy_factor"));
    //        sb.append(": ");
    //        sb.append(new Float(redundance));
    //        sb.append("   ");
    //        sb.append(Tool3lgmConstants.getResString("saturation_factor"));
    //        sb.append(": ");
    //        sb.append(new Float(saturation));
    //        return sb.toString();
    //    }

}

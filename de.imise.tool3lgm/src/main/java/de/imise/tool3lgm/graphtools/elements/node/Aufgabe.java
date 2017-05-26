package de.imise.tool3lgm.graphtools.elements.node;

import java.util.ArrayList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.AufOrgPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.DoubleMeaningEdgePanel;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AufObjVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtAufVerbindung;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.log.Log;

public final class Aufgabe extends Knoten {

    /**
     * COMMENTME
     */
    @SuppressWarnings({
            "rawtypes"
    })
    public static final Class[] COPY_DEPENDENCY = {
            AufOrgKombination.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ModelElement>[] getCopyDependencies() {
        return COPY_DEPENDENCY;
    }

    /**
     *
     */
    public Aufgabe() {
        super();
    }

    @Override
    public int layerFor() {
        return ModelConstants.DOMAIN_LAYER;
    }

    @Override
    public Object clone() {
        Aufgabe retVal;
        try {
            retVal = (Aufgabe) super.clone();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }
        return retVal;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addTab(new DoubleMeaningEdgePanel(AufObjVerbindung.class, dialog));
        dialog.addTab(getResString("Organisationseinheit"), new AufOrgPanel(Organisationseinheit.class, dialog, true));
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
    public ArrayList<ElementContainer> getAllKonfigs(final GraphDocument doc, ArrayList<ElementContainer> allPartsAndParents) {
        ArrayList<ElementContainer> returnList = new ArrayList<ElementContainer>();
        if (allPartsAndParents == null) {
            allPartsAndParents = new ArrayList<ElementContainer>();
        }

        ArrayList<ElementContainer> aufOrg = getConnectedContainer(AufOrgKombination.class, doc);
        for (int i = 0; i < aufOrg.size(); i++) {
            ArrayList<ElementContainer> konfigs = ((NodeContainer) aufOrg.get(i)).getKnoten().getConnectedContainer(ABKonfiguration.class, doc);
            returnList.addAll(konfigs);
        }
        //Liste in die die NodeContainer der Aufgaben kommen, von denen diese
        // Aufgabe Konfigurationen erbt
        ArrayList<ElementContainer> partsAndParents = new ArrayList<ElementContainer>();
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
            ArrayList<ElementContainer> konfigs = ppAuf.getAllKonfigs(doc, allPartsAndParents);
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
    public ArrayList<ElementContainer> getAllDifferentKonfigs(final GraphDocument doc) {
        ArrayList<ElementContainer> returnList = getAllKonfigs(doc, null);
        int size = returnList.size();
        ABKonfiguration[] konfigs = new ABKonfiguration[size];
        for (int i = 0; i < size; i++) {
            konfigs[i] = (ABKonfiguration) returnList.get(i).getElement();
        }
        for (int i = 0; i < size; i++) {
            if (konfigs[i] == null) {
                continue;
            }
            for (int j = i + 1; j < size; j++) {
                if (konfigs[j] == null) {
                    continue;
                }
                if (konfigs[i].hasSameServer(konfigs[j], doc)) {
                    konfigs[j] = null;
                }
            }
        }
        //hier braucht man nur bis zum 1. Element gehen, da das 0. nach dem
        // oberen Verfahren nie null sein kann
        for (int i = size - 1; i >= 0; i--) {
            if (konfigs[i] == null) {
                returnList.remove(i);
            }
        }
        return returnList;
    }

    @Override
    public ArrayList<ElementContainer> getRedundanceTypes(final GraphDocument doc) {
        return getAllDifferentKonfigs(doc);
    }

    @Override
    public String getRedundanceString(final float redundance, final float saturation) {
        StringBuilder sb = new StringBuilder(40);
        sb.append(Tool3lgmConstants.getResString("redundancy_factor"));
        sb.append(": ");
        sb.append(new Float(redundance));
        sb.append("   ");
        sb.append(Tool3lgmConstants.getResString("saturation_factor"));
        sb.append(": ");
        sb.append(new Float(saturation));
        return sb.toString();
    }

    @Override
    public boolean hasLayout() {
        return true;
    }

    @Override
    public boolean hasSortedKanten() {
        return false;
    }
}

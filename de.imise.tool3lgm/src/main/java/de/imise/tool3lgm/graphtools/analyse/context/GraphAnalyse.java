package de.imise.tool3lgm.graphtools.analyse.context;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public class GraphAnalyse {

    /**
     * Prüft, ob ein Element(Container) den angegebenen Kriterien entspricht
     * 
     * @param doc
     * @param ec
     * @param verbundenState
     * @param connectedNames
     * @return
     */
    private static boolean matchesCriteria(final GraphDocument doc, final ElementContainer ec, final boolean verbundenState, final List<String> connectedNames) {
        if (connectedNames == null || connectedNames.size() == 0) {
            return true;
        }
        boolean tmpmatch = false;
        // Ist eine Klasse aus "Verbundene" gewählt? Wenn nein, dann okay!
        ModelElement kn = ec.getElement();
        for (int i = 0; i < connectedNames.size() && !tmpmatch; i++) {
            for (int j = 0; j < kn.getEdgesCount() && !tmpmatch; j++) {
                if (kn.getConnectedElement(j).getClass() == ModelConstants.getClassForName(connectedNames.get(i))) {
                    tmpmatch = true;
                }
            }
        }
        // Das Ganze am Ende umdrehen, wenn die RadioButtons es wünschen...
        if (!verbundenState) {
            tmpmatch = !tmpmatch;
        }
        return tmpmatch;
    }

    /**
     * Erste Suche nach passenden Elementen
     * 
     * @param doc
     * @param typ
     * @param getVerbundenState
     * @param connectedNames
     * @return
     */
    public static List<ElementContainer> performSearch(final GraphDocument doc, final List<String> typ, final boolean getVerbundenState, final List<String> connectedNames) {
        List<ElementContainer> ergebnis = new ArrayList<ElementContainer>(50);
        for (String t : typ) {
            for (ElementContainer ec : doc.getElementContainer(ModelConstants.getClassForName(t))) {
                if (matchesCriteria(doc, ec, getVerbundenState, connectedNames)) {
                    if (!ergebnis.contains(ec)) {
                        ergebnis.add(ec);
                    }
                }
            }
        }
        return ergebnis;
    }

    /**
     * Sucht nach passenden Elementen. Diesmal müssen sie aber bereits mit im übergebenen Vektor
     * "origin" enthaltenen Elementen verbunden sein.
     * 
     * @param doc
     * @param origin
     * @param exclude
     * @param typ
     * @param getVerbundenState
     * @param connectedNames
     * @param searchParts
     * @param searchParents
     * @return
     */
    public static List<ElementContainer> searchWithinConnected(final GraphDocument doc, final List<ElementContainer> origin, final List<ElementContainer> exclude, final List<String> typ, final boolean getVerbundenState, final List<String> connectedNames,
            final boolean searchParts, final boolean searchParents) {
        ArrayList<ElementContainer> ergebnis = new ArrayList<ElementContainer>(50);

        for (ElementContainer orgC : origin) {
            ModelElement kn = orgC.getElement();
            for (String t : typ) {
                for (ElementContainer conC : kn.getConnectedContainer(ModelConstants.getClassForName(t), doc)) {
                    if (matchesCriteria(doc, conC, getVerbundenState, connectedNames)) {
                        if (!ergebnis.contains(conC) && !origin.contains(conC) && !exclude.contains(conC)) {
                            ergebnis.add(conC);
                        }
                    }
                }
                exclude.addAll(ergebnis);
                if (searchParts) {
                    for (ElementContainer kc : kn.getDirectPartContainer(doc)) {
                        if (ergebnis.contains(kc) || origin.contains(kc) || exclude.contains(kc)) {
                            continue;
                        }
                        List<ElementContainer> tmpL = new ArrayList<ElementContainer>(1);
                        tmpL.add(kc);
                        List<ElementContainer> a = searchWithinConnected(doc, tmpL, exclude, typ, true, null, true, false);
                        if (a.size() > 0) {
                            ergebnis.add(kc);
                            exclude.add(kc);
                            ergebnis.addAll(a);
                        }
                    }
                }
                if (searchParents) {
                    for (ElementContainer kc : kn.getDirectParentContainer(doc)) {
                        if (ergebnis.contains(kc) || origin.contains(kc) || exclude.contains(kc)) {
                            continue;
                        }
                        List<ElementContainer> tmpL = new ArrayList<ElementContainer>(1);
                        tmpL.add(kc);
                        List<ElementContainer> a = searchWithinConnected(doc, tmpL, exclude, typ, true, null, false, true);
                        if (a.size() > 0) {
                            ergebnis.add(kc);
                            exclude.add(kc);
                            ergebnis.addAll(a);
                        }
                    }
                }
            }
        }

        return ergebnis;
    }

}
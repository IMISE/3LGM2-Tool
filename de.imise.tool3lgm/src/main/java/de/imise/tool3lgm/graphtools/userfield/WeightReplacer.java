package de.imise.tool3lgm.graphtools.userfield;

import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;

public class WeightReplacer {

    private static final String UNIFORMLY_DISTRIBUTED = "UNIFORMLY_DISTRIBUTED";

    /**
     * Mappt für eine ID eines ModelElements und ein dazu gehöriges UserField
     * auf eine ID eines anderen UserFields, das das erste in allen Rechnungen
     * ersetzen soll.
     */
    private Table<String, String, String> replacer;

    /**
     * Mappt für eine ID eines ModelElements und eine Kantenklasse, die zu dem
     * ModelElement passen sollte, auf eine ID eines UserFields, das die
     * Gleichverteilung als Kantengewicht bei Rechnungen über die Knatenklasse
     * ersetzen soll.
     */
    private Table<String, Class<? extends Edge>, String> standardWeigthReplacer;

    public WeightReplacer() {
    }

    /**
     * Setzt die Gleichvertilung als Ersetzung für das UserField mit der
     * übergebenen ID
     *
     * @param modelElementID
     * @param userFieldIDToReplace
     * @return
     */
    public String setUniformDistribution(final String modelElementID, final String userFieldIDToReplace) {
        return setReplacement(modelElementID, userFieldIDToReplace, UNIFORMLY_DISTRIBUTED);
    }

    /**
     * Setzt für ein(e) ModelElement(-ID) für eine UserField-ID eine
     * Erstzungs-UserField-ID
     *
     * @param modelElementID
     * @param userFieldIDToReplace
     * @param userFieldIDReplacement
     */
    public String setReplacement(final String modelElementID, final String userFieldIDToReplace, final String userFieldIDReplacement) {
        //wenn keine gültige ID angegeben ist oder die IDs gleich sind -> entferne die evtl. vorhandene Ersetzung
        if (Strings.isNullOrEmpty(userFieldIDReplacement) || userFieldIDReplacement.equals(userFieldIDToReplace)) {
            return removeReplacement(modelElementID, userFieldIDToReplace);
        }
        //der Replacer ist noch nicht initialisiert
        if (replacer == null) {
            replacer = HashBasedTable.create();
        }
        //füge das Replacement hinzu
        return replacer.put(modelElementID, userFieldIDToReplace, userFieldIDReplacement);
    }

    public boolean isEmpty() {
        return isEmptyReplacer() && isEmptyStandardReplacer();
    }

    public boolean isEmptyReplacer() {
        return replacer == null || replacer.isEmpty();
    }

    public boolean isEmptyStandardReplacer() {
        return standardWeigthReplacer == null || standardWeigthReplacer.isEmpty();
    }

    public Iterable<Cell<String, String, String>> getReplacerContent() {
        return replacer == null ? ImmutableSet.of() : replacer.cellSet();
    }

    public Set<Cell<String, Class<? extends Edge>, String>> getStandardReplacerContent() {
        return standardWeigthReplacer == null ? ImmutableSet.of() : standardWeigthReplacer.cellSet();
    }

    /**
     * Entfernt für ein(e) ModelElement(-ID) für eine UserField-ID eine
     * Erstzungs-UserField-ID
     *
     * @param modelElementID
     * @param userFieldIDToReplace
     */
    public String removeReplacement(final String modelElementID, final String userFieldIDToReplace) {
        //es kann gar nichts entfernt werden -> raus
        if (replacer == null) {
            return null;
        }
        //entferne den Wert
        String returnValue = replacer.remove(modelElementID, userFieldIDToReplace);
        //wenn nichts mehr im Table steht -> lösche ihn
        if (replacer.isEmpty()) {
            replacer = null;
        }
        return returnValue;
    }

    /**
     * Gibt für ein(e) ModelElement(-ID) für eine UserField-ID eine
     * Erstzungs-UserField-ID zurück. Gibt es keine, kommt <code>null</code>
     * zurück.
     *
     * @param modelElementID
     * @param userFieldIDToReplace
     * @return
     */
    public String getReplacement(final String modelElementID, final String userFieldIDToReplace) {
        return replacer == null ? null : replacer.get(modelElementID, userFieldIDToReplace);
    }

    /**
     * Setzt für ein(e) ModelElement(-ID) eine Erstzungs-UserField-ID für die
     * Gleichverteilung der Kantenart.
     *
     * @param modelElementID
     * @param edgeClass
     * @param userFieldIDReplacement
     * @return
     */
    public String setUniformDistributionReplacement(final String modelElementID, final Class<? extends Edge> edgeClass, final String userFieldIDReplacement) {
        //wenn keine gültige ID angegeben ist oder die IDs gleich sind -> entferne die evtl. vorhandene Ersetzung
        if (Strings.isNullOrEmpty(userFieldIDReplacement)) {
            return removeUniformDistributionReplacement(modelElementID, edgeClass);
        }
        //der Replacer ist noch nicht initialisiert
        if (standardWeigthReplacer == null) {
            standardWeigthReplacer = HashBasedTable.create();
        }
        //füge das Replacement hinzu
        return standardWeigthReplacer.put(modelElementID, edgeClass, userFieldIDReplacement);
    }

    /**
     * Entfernt für ein(e) ModelElement(-ID) für eine Kantenart, die ID des
     * UserFields, das in Rechunungen die Gleichverteilung als
     * Verteilungsgewicht ersetzen soll.
     *
     * @param modelElementID
     * @param userFieldIDToReplace
     */
    public String removeUniformDistributionReplacement(final String modelElementID, final Class<? extends Edge> edgeClass) {
        //es kann gar nichts entfernt werden -> raus
        if (standardWeigthReplacer == null) {
            return null;
        }
        //entferne den Wert
        String returnValue = standardWeigthReplacer.remove(modelElementID, edgeClass);
        //wenn nichts mehr im Table steht -> lösche ihn
        if (standardWeigthReplacer.isEmpty()) {
            standardWeigthReplacer = null;
        }
        return returnValue;
    }

    /**
     * Gibt für ein(e) ModelElement(-ID) für eine Kantenklasse eine
     * Erstzungs-UserField-ID, die bei Rechnungen mit Gleichverteilung über die
     * angegebene Kantenklasse genutzt werden soll, zurück. Gibt es keine, kommt
     * <code>null</code> zurück.
     *
     * @param modelElementID
     * @param edgeClass
     * @return
     */
    public String getUniformDistributionReplacement(final String modelElementID, final Class<? extends Edge> edgeClass) {
        return standardWeigthReplacer == null ? null : standardWeigthReplacer.get(modelElementID, edgeClass);
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene ID für die
     * Gleichverteilung steht.
     *
     * @param userFieldIDReplacement
     * @return
     */
    public boolean isUniformDistribution(final String userFieldIDReplacement) {
        return UNIFORMLY_DISTRIBUTED.equals(userFieldIDReplacement);
    }

}

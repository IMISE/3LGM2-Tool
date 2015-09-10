package de.imise.tool3lgm.graphtools.userfield;

import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import de.imise.tool3lgm.graphtools.elements.Kante;

public class WeightReplacer {

    private static final String UNIFORMLY_DISTRIBUTED = "UNIFORMLY_DISTRIBUTED";

    /**
     * Mappt für einen HashString eines ModelElements und ein dazu gehöriges UserField
     * auf einen Hash eines anderen UserFields, das das erste in allen Rechnungen ersetzen soll.
     */
    private Table<String, String, String> replacer;

    /**
     * Mappt für einen HashString eines ModelElements und eine Kantenklasse, die zu dem ModelElement passen sollte,
     * auf einen Hash eines UserFields, das die Gleichverteilung als Kantengewicht bei Rechnungen über die Knatenklasse
     * ersetzen soll.
     */
    private Table<String, Class<? extends Kante>, String> standardWeigthReplacer;

    public WeightReplacer() {
    }

    /**
     * Setzt die Gleichvertilung als Ersetzung für das UserField mit dem übergebenen Hash
     * 
     * @param modelElementHash
     * @param userFieldHashToReplace
     * @return
     */
    public String setUniformDistribution(final String modelElementHash, final String userFieldHashToReplace) {
        return setReplacement(modelElementHash, userFieldHashToReplace, UNIFORMLY_DISTRIBUTED);
    }

    /**
     * Setzt für ein(en) ModelElement(-Hash) für einen UserField-Hash einen Erstzungs-UserField-Hash
     * 
     * @param modelElementHash
     * @param userFieldHashToReplace
     * @param userFieldHashReplacement
     */
    public String setReplacement(final String modelElementHash, final String userFieldHashToReplace, final String userFieldHashReplacement) {
        //wenn kein gültiger HashWert angegeben ist oder die hashes gleich sind -> entferne die evtl. vorhandene Ersetzung
        if (Strings.isNullOrEmpty(userFieldHashReplacement) || userFieldHashReplacement.equals(userFieldHashToReplace)) {
            return removeReplacement(modelElementHash, userFieldHashToReplace);
        }
        //der Replacer ist noch nicht initialisiert
        if (replacer == null) {
            replacer = HashBasedTable.create();
        }
        //füge das Replacement hinzu
        return replacer.put(modelElementHash, userFieldHashToReplace, userFieldHashReplacement);
    }

    /**
     * Entfernt für ein(en) ModelElement(-Hash) für einen UserField-Hash einen Erstzungs-UserField-Hash
     * 
     * @param modelElementHash
     * @param userFieldHashToReplace
     */
    public String removeReplacement(final String modelElementHash, final String userFieldHashToReplace) {
        //es kann gar nichts entfernt werden -> raus
        if (replacer == null) {
            return null;
        }
        //entferne den Wert
        String returnValue = replacer.remove(modelElementHash, userFieldHashToReplace);
        //wenn nichts mehr im Table steht -> lösche ihn
        if (replacer.isEmpty()) {
            replacer = null;
        }
        return returnValue;
    }

    /**
     * Gibt für ein(en) ModelElement(-Hash) für einen UserField-Hash einen Erstzungs-UserField-Hash zurück.
     * Gibt es keinen, kommt <code>null</code> zurück.
     * 
     * @param modelElementHash
     * @param userFieldHashToReplace
     * @return
     */
    public String getReplacement(final String modelElementHash, final String userFieldHashToReplace) {
        return replacer == null ? null : replacer.get(modelElementHash, userFieldHashToReplace);
    }

    /**
     * Setzt für ein(en) ModelElement(-Hash) einen Erstzungs-UserField-Hash für die Gleichverteilung der Kantenart.
     * 
     * @param modelElementHash
     * @param edgeClass
     * @param userFieldHashReplacement
     * @return
     */
    public String setUniformDistributionReplacement(final String modelElementHash, final Class<? extends Kante> edgeClass, final String userFieldHashReplacement) {
        //wenn kein gültiger HashWert angegeben ist oder die hashes gleich sind -> entferne die evtl. vorhandene Ersetzung
        if (Strings.isNullOrEmpty(userFieldHashReplacement)) {
            return removeUniformDistributionReplacement(modelElementHash, edgeClass);
        }
        //der Replacer ist noch nicht initialisiert
        if (standardWeigthReplacer == null) {
            standardWeigthReplacer = HashBasedTable.create();
        }
        //füge das Replacement hinzu
        return standardWeigthReplacer.put(modelElementHash, edgeClass, userFieldHashReplacement);
    }

    /**
     * Entfernt für ein(en) ModelElement(-Hash) für eine Kantenart, den Has des USerFields, was in Rechunungen
     * die Gleichverteilung als Verteilungsgewicht ersetzen soll.
     * 
     * @param modelElementHash
     * @param userFieldHashToReplace
     */
    public String removeUniformDistributionReplacement(final String modelElementHash, final Class<? extends Kante> edgeClass) {
        //es kann gar nichts entfernt werden -> raus
        if (standardWeigthReplacer == null) {
            return null;
        }
        //entferne den Wert
        String returnValue = standardWeigthReplacer.remove(modelElementHash, edgeClass);
        //wenn nichts mehr im Table steht -> lösche ihn
        if (standardWeigthReplacer.isEmpty()) {
            standardWeigthReplacer = null;
        }
        return returnValue;
    }

    /**
     * Gibt für ein(en) ModelElement(-Hash) für für eine Kantenklasse einen Erstzungs-UserField-Hash,
     * der bei Rechnungen mit Gleichverteilung über die angegebene Kantenklasse genutzt werden soll, zurück.
     * Gibt es keinen, kommt <code>null</code> zurück.
     * 
     * @param modelElementHash
     * @param edgeClass
     * @return
     */
    public String getUniformDistributionReplacement(final String modelElementHash, final Class<? extends Kante> edgeClass) {
        return standardWeigthReplacer == null ? null : standardWeigthReplacer.get(modelElementHash, edgeClass);
    }

    /**
     * Liefert <code>true</code>, wenn der übergebene Hash für die Gleichverteilung steht.
     * 
     * @param userFieldHashReplacement
     * @return
     */
    public boolean isUniformDistribution(final String userFieldHashReplacement) {
        return UNIFORMLY_DISTRIBUTED.equals(userFieldHashReplacement);
    }

    /**
     * @return
     */
    public String toXMLString() {
        if (replacer == null && standardWeigthReplacer == null) {
            return "";
        }
        StringBuilder retVal = new StringBuilder("<weightReplacer>");
        if (replacer != null) {
            retVal.append("<replacer>");
            for (String modelElementHash : replacer.rowKeySet()) {
                Map<String, String> userFieldHashToReplacementMap = replacer.row(modelElementHash);
                for (String userFieldHash : userFieldHashToReplacementMap.keySet()) {
                    final String replaceUserFieldHash = userFieldHashToReplacementMap.get(userFieldHash);
                    retVal.append("<entry elementHash=\"");
                    retVal.append(modelElementHash);
                    retVal.append("\" userFieldHash=\"");
                    retVal.append(userFieldHash);
                    retVal.append("\" replaceUserFieldHash=\"");
                    retVal.append(replaceUserFieldHash);
                    retVal.append("\"/>");
                }
            }
            retVal.append("</replacer>");
        }
        if (standardWeigthReplacer != null) {
            retVal.append("<standardWeigthReplacer>");
            for (String modelElementHash : standardWeigthReplacer.rowKeySet()) {
                Map<Class<? extends Kante>, String> edgeClassToReplacementMap = standardWeigthReplacer.row(modelElementHash);
                for (Class<? extends Kante> edgeClass : edgeClassToReplacementMap.keySet()) {
                    final String replaceUserFieldHash = edgeClassToReplacementMap.get(edgeClass);
                    retVal.append("<entry elementHash=\"");
                    retVal.append(modelElementHash);
                    retVal.append("\" edgeClass=\"");
                    retVal.append(edgeClass.getSimpleName());
                    retVal.append("\" replaceUserFieldHash=\"");
                    retVal.append(replaceUserFieldHash);
                    retVal.append("\"/>");
                }
            }
            retVal.append("</standardWeigthReplacer>");
        }
        retVal.append("</weightReplacer>");
        return retVal.toString();
    }
}

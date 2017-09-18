package de.imise.tool3lgm.graphtools.consistency;

import java.util.HashMap;
import java.util.Map;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;

/**
 * Klasse, über die man von den originalen Kardinalitäten der Kanten abweichende Kardinalitäten definieren kann,
 * die beim Ausführen der Konsistenzprüfung genutzt werden.
 * Das braucht man z.B., wenn bestimmte Analysen (z.B. die Redundanzanalsysen nur funktionieren bzw. ein interpretierbares
 * Ergebnis liefern, wenn nicht die originalen Kardinalitäten genommen werden, sondern eingeschränkte. Z.B. braucht man
 * bei der Redundanzanalyse für Anwendungsbausteine bezüglich Aufgaben eine maximale Verbindungsanzahl von 1. Das originale
 * Metamodell lässt aber beliebig viele Verbindungen zu.
 *
 * @author AXS (18.09.2017)
 */
public class CardinalityDefinition {

    /**
     * Wenn <code>true</code>, dann werden für alle Kanten, bei denen keine neue Kardinalität definiert ist immer
     * {@link EdgeCardinality#ZERO_UNIMITED}
     * zurück gegeben. Bei Kanten mit neuer Kardinalität wird dann diese neue zurück gegeben.
     * Wenn <code>false</code>, dann werden entweder die neu definierten Kardinalitäten zurück gegeben oder, wenn keine neuen definiert wurden, dann
     * die originalen Kardinalitäten aus dem Metamodell.
     */
    public boolean filterNewCardinalities = false;

    private Map<Class<? extends Edge>, EdgeCardinality> edgeClassToNewStartToEndCardinality;

    private Map<Class<? extends Edge>, EdgeCardinality> edgeClassToNewEndToStartCardinality;

    /**
     * Löscht alle neuen Kardinalitäten und deaktiviert den Filter.
     */
    public void reset() {
        filterNewCardinalities = false;
        edgeClassToNewStartToEndCardinality.clear();
        edgeClassToNewEndToStartCardinality.clear();
    }

    /**
     * Setzt die übergebenen Kardinalitäten für die Vorwärtsrichtung derübergebenen Kantenart. Damit werden die originalen Kardinalitäten bei der
     * Konsistenzprüfung überschrieben.
     * Dieser Mechanismus ist dafür gedacht, für die Redundanzanalyse andere Kardinalitäten vorzugeben, als das Metamodell definiert.
     * Z.B. funktioniert die Redundanzanalyse auf dem originalen 3LGM-Metamodell nur, wenn Anwendungsbausteinkonfigurationen immer nur mit genau
     * einem Anwednungsbaustein verbunden sind. Das originale Metamodell lässt aber beliebig viele dieser Verbindungen zu. Hierüber kann man die
     * "richtige" Anzahl der Verbindungen nur für die Analyse einschränken, so dass sie ein interpretiertbares Ergebnis liefert.
     *
     * @param edgeClass
     *            Kantenklasse für die vom Metamodell abweichende Cardinalitäten angegeben werden
     * @param edgeCardinality
     *            neue Kardinalitäten für die übergebene Kantenklasse
     */
    public void setNewStartToEndCardinality(final Class<? extends Edge> edgeClass, final EdgeCardinality edgeCardinality) {
        if (edgeClassToNewStartToEndCardinality == null) {
            edgeClassToNewStartToEndCardinality = new HashMap<>();
        }
        edgeClassToNewStartToEndCardinality.put(edgeClass, edgeCardinality);
    }

    /**
     * Setzt die übergebenen Kardinalitäten für die Rückwärtsrichtung derübergebenen Kantenart. Damit werden die originalen Kardinalitäten bei der
     * Konsistenzprüfung überschrieben.
     * Dieser Mechanismus ist dafür gedacht, für die Redundanzanalyse andere Kardinalitäten vorzugeben, als das Metamodell definiert.
     * Z.B. funktioniert die Redundanzanalyse auf dem originalen 3LGM-Metamodell nur, wenn Anwendungsbausteinkonfigurationen immer nur mit genau
     * einem Anwednungsbaustein verbunden sind. Das originale Metamodell lässt aber beliebig viele dieser Verbindungen zu. Hierüber kann man die
     * "richtige" Anzahl der Verbindungen nur für die Analyse einschränken, so dass sie ein interpretiertbares Ergebnis liefert.
     *
     * @param edgeClass
     *            Kantenklasse für die vom Metamodell abweichende Kardinalitäten angegeben werden
     * @param edgeCardinality
     *            neue Kardinalitäten für die übergebene Kantenklasse
     */
    public void setNewEndToStartCardinality(final Class<? extends Edge> edgeClass, final EdgeCardinality edgeCardinality) {
        if (edgeClassToNewEndToStartCardinality == null) {
            edgeClassToNewEndToStartCardinality = new HashMap<>();
        }
        edgeClassToNewEndToStartCardinality.put(edgeClass, edgeCardinality);
    }

    /**
     * @param edgeClass
     * @return
     */
    private final EdgeCardinality getNewStartToEndCardinality(final Class<? extends Edge> edgeClass) {
        if (edgeClassToNewStartToEndCardinality != null) {
            EdgeCardinality edgeCardinality = edgeClassToNewStartToEndCardinality.get(edgeClass);
            if (edgeCardinality != null) {
                return edgeCardinality;
            }
        }
        return null;
    }

    /**
     * @param edgeClass
     * @return
     */
    private final EdgeCardinality getNewEndToStartCardinality(final Class<? extends Edge> edgeClass) {
        if (edgeClassToNewEndToStartCardinality != null) {
            EdgeCardinality edgeCardinality = edgeClassToNewEndToStartCardinality.get(edgeClass);
            if (edgeCardinality != null) {
                return edgeCardinality;
            }
        }
        return null;
    }

    /**
     * @param edgeClass
     * @return
     */
    public final EdgeCardinality getStartToEndCardinality(final Class<? extends Edge> edgeClass) {
        EdgeCardinality newStartToEndCardinality = getNewStartToEndCardinality(edgeClass);
        //Wenn für die Kante neue Kardinalitäten angegeben wurden, gib diese zurück. Wenn keien neuen da sind und gefltert werden soll (= nur neue
        //Kardinalitäten sollen zu Konsistenzfehlern führen), dann gibt für alle Kanten ohne neue Kardinalitäten ZERO_UNLIMITED zurück (-> keine Fehler bei diesen Kanten).
        //Wenn aber nicht gefiltert werden soll und keine neuen Kardinalitäten definiert wurden, dann gib die originalen Kantenkardinalitäten zurück.
        return newStartToEndCardinality != null ? newStartToEndCardinality : filterNewCardinalities ? EdgeCardinality.ZERO_UNIMITED : Edge.getStartToEndCardinality(edgeClass);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final EdgeCardinality getEndToStartCardinality(final Class<? extends Edge> edgeClass) {
        EdgeCardinality newEndToStartCardinality = getNewEndToStartCardinality(edgeClass);
        return newEndToStartCardinality != null ? newEndToStartCardinality : Edge.getEndToStartCardinality(edgeClass);
    }

}

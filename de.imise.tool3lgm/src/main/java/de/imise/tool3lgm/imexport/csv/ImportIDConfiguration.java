package de.imise.tool3lgm.imexport.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Speichert die IDs aller Elemente während des Imports. Damit können
 * Widersprüche bei der Verwendung der IDs erkannt werden.
 *
 * @author AXS
 * @create 06.10.2014
 */
public class ImportIDConfiguration {

    public static final String DEFAULT_ID = null;

    /**
     * Mappt von einer ID auf die Elementklasse des Elementes mit dieser ID
     */
    private final Map<String, Class<? extends ModelElement>> idToElementClass = new HashMap<>();

    /**
     * Liste aller bisher verwendeten Element-IDs. Beim Import wird für jede
     * Zeile erst einmal eine Default-ID eingetragen und dann durch eine
     * eventuell gefundenen ID ersetzt.
     */
    private final List<String> ids = new ArrayList<>();

    /**
     * Mapping from an edge ID to the ID of the element where the edge starts.
     */
    private final Map<String, String> edgeIDToStartElementID = new HashMap<>();

    /**
     * Mapping from an edge ID to the ID of the element where the edge ends.
     */
    private final Map<String, String> edgeIDToEndElementID = new HashMap<>();

    public ImportIDConfiguration() {
    }

    /**
     * Fügt die DEFAULT_ID in die Liste aller IDs ein
     */
    public void addDefaultID() {
        ids.add(DEFAULT_ID);
    }

    /**
     * Ersetzt de letzte ID durch die übergebene.
     *
     * @param id
     */
    public void setLastID(final String id) {
        ids.set(ids.size() - 1, id);
    }

    /**
     * Speichert für eine ID die zugehöroge Elementklasse
     *
     * @param id
     * @param elementClass
     */
    public void put(final String id, final Class<? extends ModelElement> elementClass) {
        idToElementClass.put(id, elementClass);
    }

    /**
     * Liefert für eine ID die zugehörige Elementklasse
     *
     * @param id
     * @return
     */
    public Class<? extends ModelElement> get(final String id) {
        return idToElementClass.get(id);
    }

    /**
     * Gibt den Index zurück, an dem sich eine ID in der Liste aller IDs
     * befindet.
     *
     * @param id
     * @return
     */
    public int indexOf(final String id) {
        return ids.indexOf(id);
    }

    /**
     * Speichert für eine gegebene Edge-ID die ID des Startelementes und des
     * Endelmentes
     *
     * @param edgeID
     * @param startElementID
     * @param endElementID
     */
    public void addEdgeIDs(final String edgeID, final String startElementID, final String endElementID) {
        edgeIDToStartElementID.put(edgeID, startElementID);
        edgeIDToEndElementID.put(edgeID, endElementID);
    }

}

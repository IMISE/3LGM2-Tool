package de.imise.tool3lgm.imexport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.ModelElement;

/**
 * Speichert die Hashes aller Elemente während des Imports. Damit können Widersprüche bei der Verwendung der Hashes erkannt werden.
 * 
 * @author AXS
 * @create 06.10.2014
 */
public class ImportHashConfiguration {

    public static final String DEFAULT_HASH = null;

    /**
     * Mappt von einem Hash auf die Elementklasse des Elementes mit diesem Hash
     */
    private final HashMap<String, Class<? extends ModelElement>> hashToElementClass = new HashMap<String, Class<? extends ModelElement>>();

    /**
     * Liste aller bisher verwendeten Element-Hashes. Beim Import wird für jede Zeile erst einmal ein Default-Hash eingetragen und
     * dann durch einen eventuell gefundenen Hash ersetzt.
     */
    private final List<String> hashes = new ArrayList<String>();

    /**
     * Mappt von einem Hash einer Edge auf den Hash des Elementes, bei dem die Edge startet
     */
    private final HashMap<String, String> edgeHashToStartElementHash = new HashMap<String, String>();

    /**
     * Mappt von einem Hash einer Edge auf den Hash des Elementes, bei dem die Edge endet
     */
    private final HashMap<String, String> edgeHashToEndElementHash = new HashMap<String, String>();

    public ImportHashConfiguration() {
    }

    /**
     * Fügt den DEFAULT_HASH in die Liste aller Hashes ein
     */
    public void addDefaultHash() {
        hashes.add(DEFAULT_HASH);
    }

    /**
     * Ersetzt den letzten Hash durch den übergebenen
     * 
     * @param hash
     */
    public void setLastHash(final String hash) {
        hashes.set(hashes.size() - 1, hash);
    }

    /**
     * Speichert für einen Hash die zugehöroge Elementklasse
     * 
     * @param hash
     * @param elementClass
     */
    public void put(final String hash, final Class<? extends ModelElement> elementClass) {
        hashToElementClass.put(hash, elementClass);
    }

    /**
     * Liefert für einen Hash die zugehörige Elementklasse
     * 
     * @param hash
     * @return
     */
    public Class<? extends ModelElement> get(final String hash) {
        return hashToElementClass.get(hash);
    }

    /**
     * Gibt den Index zurück, an dem sich ein Hash in der Liste aller Hashes befindet.
     * 
     * @param hash
     * @return
     */
    public int indexOf(final String hash) {
        return hashes.indexOf(hash);
    }

    /**
     * Speichert für einen gegebenen Kantenhash den Hash des Startelementes und des Endelmentes
     * 
     * @param edgeHash
     * @param startElementHash
     * @param endElementHash
     */
    public void addEdgeHashes(final String edgeHash, final String startElementHash, final String endElementHash) {
        edgeHashToStartElementHash.put(edgeHash, startElementHash);
        edgeHashToEndElementHash.put(edgeHash, endElementHash);
    }

}

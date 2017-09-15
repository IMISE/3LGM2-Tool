package de.imise.tool3lgm.graphtools.consistency;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.PartOfBeziehung;
import de.imise.util.Pair;

/**
 * Regeln, die statt der im Metamodell vorgegebenen Regeln angegeben werden können, was bei der
 * Konsistenzprüfung als Inkonsistenz angesehen werden soll. Hierrüber kann man die im Metamodell
 * vorgegebenen Kardinalitäten für den Konsistenzcheck ändern bzw. filtern.
 *
 * @author AXS
 * @create 17.02.2011
 */
public class ConsistencyDefinition {

    /** Anzeigename dieser Definition */
    private String name = getClass().getSimpleName();

    /** Beschreibung dieser Definition */
    private String description = "";

    /**
     * Mappt von einer Kantenklasse auf ihre Kardinalitäten.
     */
    private final HashMap<Class<? extends Edge>, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> edgeClassToCardinalities = new HashMap<>();

    /**
     * Explizite Instanz des Keysets von {@link #edgeClassToCardinalities}, damit es nicht bei jedem
     * contains()-Aufruf neu gebildet werden muss.
     */
    private Set<Class<? extends Edge>> edgeClassCardinalitiesKeySet = null;

    /**
     * Mappt von einer Elemnentklasse auf alle Kanten, die bei Elementen dieser Klasse nur an
     * solchen Elementen ohne Kindelemente vorkommen dürfen. Also alle Elemente der Keyklasse dürfen
     * keine Edge der in der Liste befindlichen Art haben, wenn ihnen über eine Teil-Von-Beziehung
     * ein anderes Element untergeordnet ist.
     */
    private final HashMap<Class<? extends ModelElement>, Collection<Class<? extends Edge>>> elementToOnlyLeafAllowedEdgeClasses = new HashMap<>();

    /**
     *
     */
    public ConsistencyDefinition() {
        super();
    }

    /**
     * @param name
     */
    public ConsistencyDefinition(final String name) {
        super();
    }

    /**
     * @param name
     * @param description
     */
    public ConsistencyDefinition(final String name, final String description) {
        super();
    }

    /**
     * @param edgeClass
     * @param minStartToEndCardinality
     * @param maxStartToEndCardinality
     * @param minEndToStartCardinality
     * @param maxEndToStartCardinality
     */
    public void add(final Class<? extends Edge> edgeClass, Integer minStartToEndCardinality, Integer maxStartToEndCardinality, Integer minEndToStartCardinality, Integer maxEndToStartCardinality) {
        if (edgeClass == null) {
            return;
        }
        if (minStartToEndCardinality == null) {
            minStartToEndCardinality = getMinStartToEndCardinality(edgeClass);
        }
        if (maxStartToEndCardinality == null) {
            maxStartToEndCardinality = getMaxStartToEndCardinality(edgeClass);
        }
        if (minEndToStartCardinality == null) {
            minEndToStartCardinality = getMinEndToStartCardinality(edgeClass);
        }
        if (maxEndToStartCardinality == null) {
            maxEndToStartCardinality = getMaxEndToStartCardinality(edgeClass);
        }
        edgeClassToCardinalities.put(edgeClass, new Pair<>(new Pair<>(minStartToEndCardinality, maxStartToEndCardinality), new Pair<>(minEndToStartCardinality, maxEndToStartCardinality)));
        // die Variable keySet wieder null setzen, um sie als geändert zu markieren
        edgeClassCardinalitiesKeySet = null;
    }

    /**
     * Fügt die übergebene Kantenklase mit den ursprünglichen Kardinalitäten der Edge in die Liste
     * ein
     *
     * @param edgeClass
     */
    public void add(final Class<? extends Edge> edgeClass) {
        add(edgeClass, null, null, null, null);
    }

    /**
     * @param elementClass
     * @param edgeClass
     */
    public void addOnlyLeafAllowedEdgeClass(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        // Teil-Von-Beziehungen dürfen hier nicht enthalten sein, da das ein Widerspruch wäre
        if (PartOfBeziehung.class.isAssignableFrom(edgeClass)) {
            return;
        }
        // nur bei Elementen, die auch Teil-Von-Beziehungen haben können, ist es sinnvoll, sie sich
        // zu merken
        Class<? extends PartOfBeziehung>[] edges2parts = ModelConstants.getHasPartsEdgeClasses(elementClass);
        if (edges2parts.length == 0) {
            return;
        }
        Collection<Class<? extends Edge>> leafAllowedEdgeClasses = elementToOnlyLeafAllowedEdgeClasses.get(elementClass);
        if (leafAllowedEdgeClasses == null) {
            leafAllowedEdgeClasses = new HashSet<>();
            elementToOnlyLeafAllowedEdgeClasses.put(elementClass, leafAllowedEdgeClasses);
        }
        leafAllowedEdgeClasses.add(edgeClass);
    }

    /**
     * Setzt für diese Elementart, dass alle Kanten nur an Blättern im Teil-Von-Baum dieser Elemente
     * hängen dürfen.
     *
     * @param elementClass
     */
    public void addOnlyLeafAllowedEdges(final Class<? extends ModelElement> elementClass) {
        for (Class<? extends Edge> edgeClass : ModelConstants.getEdgeTypes(elementClass)) {
            addOnlyLeafAllowedEdgeClass(elementClass, edgeClass);
        }
    }

    /**
     * Liefert die minimale Kardinalität in Vorwärtsrichtung.
     *
     * @param edgeClass
     * @return
     */
    public final int getMinStartToEndCardinality(final Class<? extends Edge> edgeClass) {
        if (edgeClassToCardinalities == null || edgeClassToCardinalities.size() == 0) {
            return getMinStartToEndCardinality(edgeClass);
        }
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> cards = edgeClassToCardinalities.get(edgeClass);
        if (cards == null) {
            return getMinStartToEndCardinality(edgeClass);
        }
        return cards.getFirstItem().getFirstItem();
    }

    /**
     * Liefert die maximale Kardinalität in Vorwärtsrichtung.
     *
     * @param edgeClass
     * @return
     */
    public final int getMaxStartToEndCardinality(final Class<? extends Edge> edgeClass) {
        if (edgeClassToCardinalities == null || edgeClassToCardinalities.size() == 0) {
            return getMaxStartToEndCardinality(edgeClass);
        }
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> cards = edgeClassToCardinalities.get(edgeClass);
        if (cards == null) {
            return getMaxStartToEndCardinality(edgeClass);
        }
        return cards.getFirstItem().getSecondItem();
    }

    /**
     * Liefert die minimale Kardinalität in Rückwärtsrichtung.
     *
     * @param edgeClass
     * @return
     */
    public final int getMinEndToStartCardinality(final Class<? extends Edge> edgeClass) {
        if (edgeClassToCardinalities == null || edgeClassToCardinalities.size() == 0) {
            return getMinEndToStartCardinality(edgeClass);
        }
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> cards = edgeClassToCardinalities.get(edgeClass);
        if (cards == null) {
            return getMinEndToStartCardinality(edgeClass);
        }
        return cards.getSecondItem().getFirstItem();
    }

    /**
     * Liefert die maximale Kardinalität in Rückwärtsrichtung.
     *
     * @param edgeClass
     * @return
     */
    public final int getMaxEndToStartCardinality(final Class<? extends Edge> edgeClass) {
        if (edgeClassToCardinalities == null || edgeClassToCardinalities.size() == 0) {
            return getMaxEndToStartCardinality(edgeClass);
        }
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> cards = edgeClassToCardinalities.get(edgeClass);
        if (cards == null) {
            return getMaxEndToStartCardinality(edgeClass);
        }
        return cards.getSecondItem().getSecondItem();
    }

    /**
     * Liefert <code>true</code>, wenn die Kanteklasse im Key-Set der Map auf die Kardinalitäten
     * enthalten ist oder wenn diese Map leer ist (uns somit alle Kanten als enthalten gelten).
     *
     * @param egdeClass
     * @return
     */
    public final boolean contains(final Class<? extends Edge> egdeClass) {
        if (edgeClassToCardinalities == null) {
            return false;
        }
        if (edgeClassCardinalitiesKeySet == null) {
            edgeClassCardinalitiesKeySet = edgeClassToCardinalities.keySet();
        }
        if (edgeClassCardinalitiesKeySet.size() == 0 || edgeClassCardinalitiesKeySet.contains(egdeClass)) {
            return true;
        }
        return false;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public final void setDescription(final String description) {
        this.description = description;
    }

}

package de.imise.tool3lgm.graphtools.consistency;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
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
    private final HashMap<Class<? extends Kante>, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> edgeClassToCardinalities = new HashMap<Class<? extends Kante>, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>();

    /**
     * Explizite Instanz des Keysets von {@link #edgeClassToCardinalities}, damit es nicht bei jedem
     * contains()-Aufruf neu gebildet werden muss.
     */
    private Set<Class<? extends Kante>> edgeClassCardinalitiesKeySet = null;

    /**
     * Mappt von einer Elemnentklasse auf alle Kanten, die bei Elementen dieser Klasse nur an
     * solchen Elementen ohne Kindelemente vorkommen dürfen. Also alle Elemente der Keyklasse dürfen
     * keine Kante der in der Liste befindlichen Art haben, wenn ihnen über eine Teil-Von-Beziehung
     * ein anderes Element untergeordnet ist.
     */
    private final HashMap<Class<? extends ModelElement>, Collection<Class<? extends Kante>>> elementToOnlyLeafAllowedEdgeClasses = new HashMap<Class<? extends ModelElement>, Collection<Class<? extends Kante>>>();

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
    public void add(final Class<? extends Kante> edgeClass, Integer minStartToEndCardinality, Integer maxStartToEndCardinality, Integer minEndToStartCardinality, Integer maxEndToStartCardinality) {
        if (edgeClass == null) {
            return;
        }
        if (minStartToEndCardinality == null) {
            minStartToEndCardinality = Kante.getMinStartToEndCardinality(edgeClass);
        }
        if (maxStartToEndCardinality == null) {
            maxStartToEndCardinality = Kante.getMaxStartToEndCardinality(edgeClass);
        }
        if (minEndToStartCardinality == null) {
            minEndToStartCardinality = Kante.getMinEndToStartCardinality(edgeClass);
        }
        if (maxEndToStartCardinality == null) {
            maxEndToStartCardinality = Kante.getMaxEndToStartCardinality(edgeClass);
        }
        edgeClassToCardinalities.put(edgeClass, new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(new Pair<Integer, Integer>(minStartToEndCardinality, maxStartToEndCardinality), new Pair<Integer, Integer>(minEndToStartCardinality,
                maxEndToStartCardinality)));
        // die Variable keySet wieder null setzen, um sie als geändert zu markieren
        edgeClassCardinalitiesKeySet = null;
    }

    /**
     * Fügt die übergebene Kantenklase mit den ursprünglichen Kardinalitäten der Kante in die Liste
     * ein
     * 
     * @param edgeClass
     */
    public void add(final Class<? extends Kante> edgeClass) {
        add(edgeClass, null, null, null, null);
    }

    /**
     * @param elementClass
     * @param edgeClass
     */
    public void addOnlyLeafAllowedEdgeClass(final Class<? extends ModelElement> elementClass, final Class<? extends Kante> edgeClass) {
        // Teil-Von-Beziehungen dürfen hier nicht enthalten sein, da das ein Widerspruch wäre
        if (PartOfBeziehung.class.isAssignableFrom(edgeClass)) {
            return;
        }
        // nur bei Elementen, die auch Teil-Von-Beziehungen haben können, ist es sinnvoll, sie sich
        // zu merken
        Class<? extends PartOfBeziehung>[] edges2parts = ModelConstants.getHasPartsEdgeClasses(elementClass);
        if (edges2parts == null || edges2parts.length == 0) {
            return;
        }
        Collection<Class<? extends Kante>> leafAllowedEdgeClasses = elementToOnlyLeafAllowedEdgeClasses.get(elementClass);
        if (leafAllowedEdgeClasses == null) {
            leafAllowedEdgeClasses = new HashSet<Class<? extends Kante>>();
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
        for (Class<? extends Kante> edgeClass : ModelConstants.getEdgeTypes(elementClass)) {
            addOnlyLeafAllowedEdgeClass(elementClass, edgeClass);
        }
    }

    /**
     * Liefert die minimale Kardinalität in Vorwärtsrichtung.
     * 
     * @param edgeClass
     * @return
     */
    public final int getMinStartToEndCardinality(final Class<? extends Kante> edgeClass) {
        if (edgeClassToCardinalities == null || edgeClassToCardinalities.size() == 0) {
            return Kante.getMinStartToEndCardinality(edgeClass);
        }
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> cards = edgeClassToCardinalities.get(edgeClass);
        if (cards == null) {
            return Kante.getMinStartToEndCardinality(edgeClass);
        }
        return cards.getFirstItem().getFirstItem();
    }

    /**
     * Liefert die maximale Kardinalität in Vorwärtsrichtung.
     * 
     * @param edgeClass
     * @return
     */
    public final int getMaxStartToEndCardinality(final Class<? extends Kante> edgeClass) {
        if (edgeClassToCardinalities == null || edgeClassToCardinalities.size() == 0) {
            return Kante.getMaxStartToEndCardinality(edgeClass);
        }
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> cards = edgeClassToCardinalities.get(edgeClass);
        if (cards == null) {
            return Kante.getMaxStartToEndCardinality(edgeClass);
        }
        return cards.getFirstItem().getSecondItem();
    }

    /**
     * Liefert die minimale Kardinalität in Rückwärtsrichtung.
     * 
     * @param edgeClass
     * @return
     */
    public final int getMinEndToStartCardinality(final Class<? extends Kante> edgeClass) {
        if (edgeClassToCardinalities == null || edgeClassToCardinalities.size() == 0) {
            return Kante.getMinEndToStartCardinality(edgeClass);
        }
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> cards = edgeClassToCardinalities.get(edgeClass);
        if (cards == null) {
            return Kante.getMinEndToStartCardinality(edgeClass);
        }
        return cards.getSecondItem().getFirstItem();
    }

    /**
     * Liefert die maximale Kardinalität in Rückwärtsrichtung.
     * 
     * @param edgeClass
     * @return
     */
    public final int getMaxEndToStartCardinality(final Class<? extends Kante> edgeClass) {
        if (edgeClassToCardinalities == null || edgeClassToCardinalities.size() == 0) {
            return Kante.getMaxEndToStartCardinality(edgeClass);
        }
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> cards = edgeClassToCardinalities.get(edgeClass);
        if (cards == null) {
            return Kante.getMaxEndToStartCardinality(edgeClass);
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
    public final boolean contains(final Class<? extends Kante> egdeClass) {
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

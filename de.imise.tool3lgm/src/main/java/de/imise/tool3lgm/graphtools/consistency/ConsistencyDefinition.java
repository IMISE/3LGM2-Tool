package de.imise.tool3lgm.graphtools.consistency;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.PartOfBeziehung;

/**
 * Regeln, die statt der im Metamodell vorgegebenen Regeln angegeben werden können, was bei der
 * Konsistenzprüfung als Inkonsistenz angesehen werden soll. Hierrüber kann man die im Metamodell
 * vorgegebenen Kardinalitäten für den Konsistenzcheck ändern bzw. filtern.
 *
 * @author AXS
 * @create 17.02.2011
 */
public class ConsistencyDefinition {

    /**
     * Definition der Kardinalitäten, die bei der Konsistenzprüfung eingealten werden sollen.
     */
    private CardinalityDefinition cardinalityDefinition = new CardinalityDefinition();

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
    }

    public void setCardinalityDefinition(final CardinalityDefinition cardinalityDefinition) {
        this.cardinalityDefinition = cardinalityDefinition;
    }

    public void reset() {
        cardinalityDefinition.reset();
        elementToOnlyLeafAllowedEdgeClasses.clear();
    }

    public CardinalityDefinition getCardinalityDefinition() {
        return cardinalityDefinition;
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
     * Liefert die Kardinalität in Vorwärtsrichtung.
     *
     * @param edgeClass
     * @return
     */
    public EdgeCardinality getStartToEndCardinality(final Class<? extends Edge> edgeClass) {
        return cardinalityDefinition.getStartToEndCardinality(edgeClass);
    }

    /**
     * Liefert die Kardinalität in Rückwärtsrichtung.
     *
     * @param edgeClass
     * @return
     */
    public EdgeCardinality getEndToStartCardinality(final Class<? extends Edge> edgeClass) {
        return cardinalityDefinition.getEndToStartCardinality(edgeClass);
    }

}

package de.imise.tool3lgm.graphtools.consistency;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Regeln, die statt der im Metamodell vorgegebenen Regeln angegeben werden können, was bei der
 * Konsistenzprüfung als Inkonsistenz angesehen werden soll. Hierrüber kann man die im Metamodell
 * vorgegebenen Kardinalitäten für den Konsistenzcheck ändern bzw. filtern.
 *
 * @author AXS
 * @create 17.02.2011
 */
public class ConsistencyDefinition {

    /** Metamodel, für das die Konsistenz definiert wird */
    private final MetaModel metaModel;

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
     * @param metaModel
     */
    public ConsistencyDefinition(final MetaModel metaModel) {
        this.metaModel = metaModel;
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
        if (HasPartEdge.class.isAssignableFrom(edgeClass)) {
            return;
        }
        // nur bei Elementen, die auch Teil-Von-Beziehungen haben können, ist es sinnvoll, sie sich zu merken
        if (!metaModel.canHaveParts(elementClass)) {
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
        for (Class<? extends Edge> edgeClass : metaModel.getEdgeTypes(elementClass)) {
            addOnlyLeafAllowedEdgeClass(elementClass, edgeClass);
        }
    }

    /**
     * Liefert die Kardinalität in Vorwärtsrichtung.
     *
     * @param edgeClass
     * @return
     */
    public EdgeCardinality getForwardCardinality(final Class<? extends Edge> edgeClass) {
        return cardinalityDefinition.getForwardCardinality(edgeClass);
    }

    /**
     * Liefert die Kardinalität in Rückwärtsrichtung.
     *
     * @param edgeClass
     * @return
     */
    public EdgeCardinality getBackwardCardinality(final Class<? extends Edge> edgeClass) {
        return cardinalityDefinition.getBackwardCardinality(edgeClass);
    }

}

package de.imise.tool3lgm.graphtools.consistency.error.checker;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.imise.tool3lgm.graphtools.consistency.ConsistencyDefinition;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.type.MaxCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.type.MinCardinalityError;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPathHandler;

/**
 * @author AXS (22.03.2020)
 */
public class EdgeCardinalityChecker implements ConsistencyErrorChecker {

    /**
     *
     */
    private ConsistencyDefinition consistencyDefinition;

    @Override
    public Class<? extends AbstractConsistencyError> getErrorType() {
        return AbstractCardinalityError.class;
    }

    /**
     * @param consistencyDefinition
     */
    public void setConsistencyDefinition(final ConsistencyDefinition consistencyDefinition) {
        this.consistencyDefinition = consistencyDefinition;
    }

    /**
     * @return <code>true</code>, wenn diese Instanz eine
     *         {@link ConsistencyDefinition} besitzt
     */
    private boolean isValid() {
        return consistencyDefinition != null;
    }

    @Override
    public Collection<AbstractConsistencyError> getErrors(final GDCollection gdcoll, final boolean checkOnly) {
        GraphDocument doc = gdcoll.getMainDoc();
        Collection<AbstractConsistencyError> errors = new ArrayList<>();
        for (ModelElement me : doc.getModelItems(ModelElement.class, true)) {
            addCardinalityErrors(gdcoll, me, errors, checkOnly);
        }
        return errors;
    }

    /**
     * Fügt der übergebenen Error-Liste alle Kardinalitätsfehler des übergebenen
     * Elementes hinzu.
     *
     * @param gdcoll
     * @param me
     * @param returnList
     * @param checkOnly if <code>true</code> not all but only the first error
     *            will be added to the return list
     */
    private void addCardinalityErrors(final GDCollection gdcoll, final ModelElement me, final Collection<AbstractConsistencyError> returnList, final boolean checkOnly) {
        if (!isValid()) {
            return;
        }
        MetaModel metaModel = gdcoll.getMetaModel();
        Class<? extends ModelElement> meClass = me.getClass();
        ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
        Class<? extends Edge>[] edgeTypes = metaModel.getEdgeTypes(meClass);
        // nur Elementarten beachten, die wenigstens eine Edge besitzen können
        for (Class<? extends Edge> edgeClass : edgeTypes) {
            EdgeCardinality forwardCardinality = consistencyDefinition.getForwardCardinality(edgeClass);
            EdgeCardinality backwardCardinality = consistencyDefinition.getBackwardCardinality(edgeClass);
            //wenn es keine Min-Max-Fehler geben kann -> weiter
            if (forwardCardinality == ZERO_UNLIMITED && backwardCardinality == ZERO_UNLIMITED) {
                continue;
            }

            List<Edge> connections = me.getEdges(edgeClass);
            List<Edge> meIsStartConnections = new ArrayList<>();
            List<Edge> meIsEndConnections = new ArrayList<>();
            for (Edge edge : connections) {
                if (edge.isStart(me)) {
                    meIsStartConnections.add(edge);
                } else {
                    meIsEndConnections.add(edge);
                }
            }

            // entweder für die aktuelle Kantenklasse die neu gesetzten Kardinalitäten holen
            // oder die Standardwaerte laden, wenn keine neuen gesetzt wurden
            int minStartCard = forwardCardinality.min();
            int maxStartCard = forwardCardinality.max();
            int minEndCard = backwardCardinality.min();
            int maxEndCard = backwardCardinality.max();
            boolean meHasStartClass = MetaModel.isStartClass(edgeClass, meClass);
            boolean meHasEndClass = MetaModel.isEndClass(edgeClass, meClass);

            ElementaryMetaPath forwardElementaryMetaPath = elementaryMetaPathHandler.getForwardMetaPath(edgeClass);
            // Bei Teil-Von-Beziehungen oder Beziehungen bei denen meClass
            // sowohl Start- als auch Endklasse sein können
            if (HasPartEdge.class.isAssignableFrom(edgeClass)) {
                if (meHasStartClass) {
                    if (meIsStartConnections.size() < minStartCard) {
                        returnList.add(new MinCardinalityError(me, forwardElementaryMetaPath, minEndCard));
                    }
                    if (meIsStartConnections.size() > maxStartCard) {
                        returnList.add(new MaxCardinalityError(me, forwardElementaryMetaPath, meIsStartConnections, maxEndCard));
                    }
                }
                if (meHasEndClass) {
                    if (meIsEndConnections.size() < minEndCard) {
                        returnList.add(new MinCardinalityError(me, forwardElementaryMetaPath.getOtherDirection(), minStartCard));
                    }
                    if (meIsEndConnections.size() > maxEndCard) {
                        returnList.add(new MaxCardinalityError(me, forwardElementaryMetaPath.getOtherDirection(), meIsEndConnections, maxStartCard));
                    }
                }
            } else if (meHasStartClass && meHasEndClass) {
                int card = minStartCard < minEndCard ? minEndCard : minStartCard;
                if (connections.size() < card) {
                    returnList.add(new MinCardinalityError(me, forwardElementaryMetaPath, card));
                }
                card = maxStartCard < maxEndCard ? maxStartCard : maxEndCard;
                if (connections.size() > card) {
                    returnList.add(new MaxCardinalityError(me, forwardElementaryMetaPath, connections, card));
                }
            } else if (meHasStartClass) {
                if (connections.size() < minStartCard) {
                    returnList.add(new MinCardinalityError(me, forwardElementaryMetaPath, minStartCard));
                }
                if (connections.size() > maxStartCard) {
                    returnList.add(new MaxCardinalityError(me, forwardElementaryMetaPath, connections, maxStartCard));
                }
            } else if (meHasEndClass) {
                if (connections.size() < minEndCard) {
                    returnList.add(new MinCardinalityError(me, forwardElementaryMetaPath.getOtherDirection(), minEndCard));
                }
                if (connections.size() > maxEndCard) {
                    returnList.add(new MaxCardinalityError(me, forwardElementaryMetaPath.getOtherDirection(), connections, maxEndCard));
                }
            } else {
                System.err.println("Die Edge darf gar nicht für dieses Element existieren!");
            }
            if (checkOnly && !returnList.isEmpty()) {
                return;
            }
        }
    }

}

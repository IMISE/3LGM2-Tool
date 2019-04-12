/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency.error;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;

/**
 * @author AXS
 * @created 13.09.2008
 */
public class MaxCardinalityError extends AbstractCardinalityError {

    /**
     * Liste der Kanten, von denen es zuviele gibt
     */
    private final List<Edge> connections;

    /**
     * @param me
     * @param elementaryMetaPath
     * @param connections
     * @param cardValue
     * @param gdcoll
     */
    public MaxCardinalityError(final ModelElement me, final ElementaryMetaPath elementaryMetaPath, final List<Edge> connections, final GDCollection gdcoll, final int cardValue) {
        super(me, elementaryMetaPath, gdcoll, cardValue);
        this.connections = connections;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof MaxCardinalityError)) {
            return false;
        }
        MaxCardinalityError ce = (MaxCardinalityError) obj;
        return connections.equals(ce.connections);
    }

}

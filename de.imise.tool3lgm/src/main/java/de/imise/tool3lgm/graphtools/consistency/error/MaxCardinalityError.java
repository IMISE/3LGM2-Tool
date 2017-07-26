/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency.error;

import java.util.List;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * @author AXS
 * @created 13.09.2008
 */
public class MaxCardinalityError extends AbstractCardinalityError {

    /**
     * Liste der Kanten, von denen es zuviele gibt
     */
    private final List<Kante> connections;

    /**
     * @param me
     * @param edgeClass
     * @param connections
     * @param cardValue
     * @param gdcoll
     */
    public MaxCardinalityError(final ModelElement me, final Class<? extends Kante> edgeClass, final List<Kante> connections, final GDCollection gdcoll, final int cardValue) {
        super(me, edgeClass, gdcoll, cardValue);
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

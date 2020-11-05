/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency.error.type;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;

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
     */
    public MaxCardinalityError(final ModelElement me, final ElementaryMetaPath elementaryMetaPath, final List<Edge> connections, final int cardValue) {
        super(me, elementaryMetaPath, cardValue);
        this.connections = connections;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (connections == null ? 0 : connections.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MaxCardinalityError other = (MaxCardinalityError) obj;
        if (connections == null) {
            if (other.connections != null) {
                return false;
            }
        } else if (!connections.equals(other.connections)) {
            return false;
        }
        return true;
    }

}

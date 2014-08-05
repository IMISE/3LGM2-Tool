/**
 * 
 */
package de.imise.tool3lgm.graphtools.consistency.error;

import java.util.ArrayList;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.util.Alphabetical;

/**
 * @author AXS
 * @created 13.09.2008
 */
public class MaxCardinalityError extends CardinalityError {

    /**
     * Liste der Kanten, von denen es zuviele gibt
     */
    private final ArrayList<Kante> connections;

    /**
     * @param me
     * @param edgeClass
     * @param connections
     * @param cardValue
     * @param gdcoll
     */
    public MaxCardinalityError(final ModelElement me, final Class<? extends Kante> edgeClass, final ArrayList<Kante> connections, final int cardValue, final GDCollection gdcoll) {
        super(me, edgeClass, cardValue, gdcoll);
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

    @Override
    public String getMessage() {
        ArrayList<ModelElement> al = new ArrayList<ModelElement>();
        for (Kante k : connections) {
            al.add(k.isStart(me) ? k.getEnd() : k.getStart());
        }
        Alphabetical.sort(al);
        StringBuilder sb = getMessageBuilder();
        sb.append("\n\t");
        sb.append(al.toString().replace('\n', ' '));
        return sb.toString();
    }

}

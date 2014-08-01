/**
 * 
 */
package de.imise.tool3lgm.graphtools.consistency.error;

import java.util.ArrayList;

import de.imise.util.Alphabetical;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

/**
 * @author AXS
 * @created 13.09.2008
 */
public class MaxCardinalityError extends CardinalityError {

	/**
	 * Liste der Kanten, von denen es zuviele gibt
	 */
	private ArrayList<Kante> connections;
	

	/**
	 * @param me
	 * @param edgeClass
	 * @param connections
	 * @param cardValue
	 * @param gdcoll
	 */
	public MaxCardinalityError(ModelElement me, Class<? extends Kante> edgeClass, ArrayList<Kante> connections, int cardValue, GDCollection gdcoll) {
		super(me, edgeClass, cardValue, gdcoll);
		this.connections = connections;
	}


	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.consistency.error.CardinalityError#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof MaxCardinalityError))
			return false;
		MaxCardinalityError ce = (MaxCardinalityError)obj;
	    return connections.equals(ce.connections);
    }


	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.consistency.error.CardinalityError#getMessage()
	 */
	@Override
    public String getMessage() {
		ArrayList<ModelElement> al = new ArrayList<ModelElement>();
		for (Kante k : connections)
			al.add(k.isStart(me)?k.getEnd():k.getStart());
		Alphabetical.sort(al);
		StringBuilder sb = getMessageBuilder();
		sb.append("\n\t");
		sb.append(al.toString().replace('\n', ' '));
	    return sb.toString();
    }

}

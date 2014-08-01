/*
 * Created on 01.11.2004
 *
 */
package de.imise.tool3lgm.graphtools.elements;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;

/**
 * @author imi0wendt
 *
 */
public class Knickpunkt extends Knoten {

	/**
	 * COMMENTME
	 */
	private String kantenHash = "";
	/**
	 * COMMENTME
	 */
	private EdgeContainer kc = null;
	/**
	 * COMMENTME
	 */
	private int index = 0;
	
	/**
	 * 
	 */
	public Knickpunkt() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone () {
		Knickpunkt retVal;
		try {
			retVal = (Knickpunkt) super.clone();
		} catch (Exception e) {
			Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
			return null;
		}
		retVal.kantenHash = kantenHash;
		retVal.kc = kc;
		retVal.index = index;
		return retVal;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasLayout()
	 */
	@Override
	public boolean hasLayout() {
		return true;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#hasSortedKanten()
	 */
	@Override
	public boolean hasSortedKanten() {
		return false;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#createContainer(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public ElementContainer createContainer(GraphDocument doc) {
		return new BendpointContainer(this, doc);
	}
	
	/**
	 * @return
	 */
	public String getKantenHash() {
		return kantenHash;
	}
	
	/**
	 * @param kantenHash
	 */
	public void setKantenHash(String kantenHash) {
		this.kantenHash = (kantenHash == null ? "" : kantenHash);
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#getXMLEntities()
	 */
	@Override
	protected StringBuilder getXMLEntities() {
		if (kc == null)
			return super.getXMLEntities();
		return super.getXMLEntities()
				.append("<field name=\"kantenHash\">" + kc.getHashString() +"</field>")
				.append("<field name=\"index\">" + kc.getIndexOfKnickpunkt(this) +"</field>");
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#putXMLFieldString(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean putXMLFieldString(String field, String value) {
		if (field.equals("kantenHash")) {
			setKantenHash(value);
			return true;
		}
		if (field.equals("index")) {
			index = Integer.parseInt(value);
			return true;
		}
		
		return super.putXMLFieldString(field, value);
	}
	
	/**
	 * @return
	 */
	public EdgeContainer getOwner() {
		return kc;
	}
	
	/**
	 * @param kc
	 */
	public void setOwner(EdgeContainer kc) {
		this.kc = kc;
		this.kantenHash = kc.getHashString();
	}
	
	/**
	 * @return
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * @return
	 * 		GraphDocument, in dem sich der Knickpunkt befindet.
	 */
	public final GraphDocument getGraphDocument(){
		return kc.getGraphDocument();
	}
	
	/**
	 * @return
	 * 		Den Container in dem Szenario, in dem der Knickpunkt dargestellt wird
	 */
	public final BendpointContainer getBendpointContainer(){
		return (BendpointContainer)getContainer(getGraphDocument());
	}
	
}

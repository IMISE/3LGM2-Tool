package de.imise.tool3lgm.graphtools.elements;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.log.Log;

/**
 *	Die PartOf-Beziehung ist eine Bezihung zwischen zwei Elementen, die von der selben Elementklasse sind.
 *	Start der Kante ist immer das Kindelement, das Ende der Kante ist immer das Elternelement.
 *	Dir Richtung der Kante ist immer forward.
 */
public abstract class PartOfBeziehung extends Doppelkante {
	
	public PartOfBeziehung() {
		super();
	}

	public PartOfBeziehung(ModelElement part, ModelElement parent) {
		super(part, parent);
	}

	public PartOfBeziehung(ModelElement part, ModelElement parent, boolean registerInKnots) {
		super(part, parent, registerInKnots);
	}

	/**
	 * Gibt das Element zurück, welches durch diese Kante Teil des anderen Elementes ist.
	 * 
	 * @return
	 * 		Partelement der Kante
	 */
	public ModelElement getPart(){
		return getStart();
	}
	
	/**
	 * Gibt das Element zurück, welches durch diese Kante das Oberelement des anderen Elementes ist.
	 * 
	 * @return
	 * 		Parentelement der Kante
	 */
	public ModelElement getParent(){
		return getEnd();
	}

	/**
	 * Gibt die Teilelementklasse der Teil-Von-Beziehung zurück
	 *
	 * @param poClass
	 * @return
	 */
	public static Class<? extends ModelElement> getPartClass(Class<? extends PartOfBeziehung> poClass){
		return Kante.getStartClass(poClass);
	}
	
	/**
	 * Gibt die Elementklasse der Teil-Von-Beziehung zurück, die nicht die Teilelementklasse ist
	 *
	 * @param poClass
	 * @return
	 */
	public static Class<? extends ModelElement> getParentClass(Class<? extends PartOfBeziehung> poClass){
		return Kante.getEndClass(poClass);
	}

	/**
	 * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Teilelementklasse der übergebenen
	 * Kante zuweisungskompatibel ist.
	 *
	 * @param poClass
	 * @param meClass
	 * @return
	 */
	public static boolean isPartClass(Class<? extends PartOfBeziehung> poClass, Class<? extends ModelElement> meClass){
		return getPartClass(poClass).isAssignableFrom(meClass);
	}
	
	/**
	 * Liefert <code>true</code>, wenn die übergebene Elementklasse mit der Elementklasse der übergebenen
	 * Kante zuweisungskompatibel ist, ide nicht die Teilelementklasse ist.
	 *
	 * @param poClass
	 * @param meClass
	 * @return
	 */
	public static boolean isParentClass(Class<? extends PartOfBeziehung> poClass, Class<? extends ModelElement> meClass){
		return getParentClass(poClass).isAssignableFrom(meClass);
	}

	
	
	/**
	 * Richtung, in der die Kante vom Part auf den Parent zeigt.
	 */
	public static final int PART_TO_PARENT_DIRECTION = Doppelkante.FORWARD;
	
	/**
	 * Richtung, in der die Kante vom Prent auf den Part zeigt.
	 */
	public static final int PARENT_TO_PART_DIRECTION = Doppelkante.BACKWARD;
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.edge.Doppelkante#getState()
	 * bitte so lassen, wegen XSLT-Scripte (PartOfBeziehung muss immer FORWARD sein)
	 */
	@Override
	public final int getDirection() {
		return PART_TO_PARENT_DIRECTION;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#putXMLFieldString(java.lang.String, java.lang.String)
	 * bitte so lassen, wegen XSLT-Scripte (PartOfBeziehung muss immer FORWARD sein)
	 */
	@Override
	public boolean putXMLFieldString(String field, String value) {
		if (field.equals("state")) {
			for (int i=0; i<DIRECTION_STR.length; i++)
				if (value.equals(DIRECTION_STR[i])) {
					setDirection(i-1);
					return true;
				}
			return false;
		}
		return super.putXMLFieldString(field, value);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.edge.Doppelkante#setDirection(int)
	 * bitte so lassen, wegen XSLT-Scripte (PartOfBeziehung muss immer FORWARD sein)
	 */
	@Override
	public final void setDirection(int _state) {
		ModelElement start = k1;
		ModelElement end = k2;
		
		switch (_state) {
			case Doppelkante.DOUBLE:
				break;
			case Doppelkante.FORWARD:
				super.setDirection(Doppelkante.FORWARD);
				break;
			case Doppelkante.BACKWARD:
				ModelElement temp = k1;
				k1 = k2;
				k2 = temp;
				super.setDirection(Doppelkante.FORWARD);
				break;
		}
		
		if (isInCircle()) {
			k1 = start;
			k2 = end;
		}
		
		return;
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.Doppelkante#setKnots(tool3lgm.graphtools.elements.ModelElement, tool3lgm.graphtools.elements.ModelElement, boolean)
	 */
	@Override
	public void setKnots(ModelElement part, ModelElement parent, boolean registerInKnots) {
		ModelElement start = k1;
		ModelElement end = k2;

		super.setKnots(part, parent, registerInKnots);
		if (isInCircle()) {
			part.removeEdge(this);
			parent.removeEdge(this);
			super.setKnots(start, end, registerInKnots);
		}
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.Kante#setKnotsAndInsert(tool3lgm.graphtools.elements.ModelElement, int, tool3lgm.graphtools.elements.ModelElement, int)
	 */
	@Override
	public void setKnotsAndInsert(ModelElement part, int partEdgePos, ModelElement parent, int parentEdgePos) {
		ModelElement start = k1;
		ModelElement end = k2;

		k1 = part;
		k2 = parent;
		part.insertEdge(this, partEdgePos);
		parent.insertEdge(this, parentEdgePos);

		if (isInCircle()) {
			part.removeEdge(this);
			parent.removeEdge(this);
			super.setKnotsAndInsert(start, partEdgePos, end, parentEdgePos);
		}
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.Kante#setStartAndInsert(tool3lgm.graphtools.elements.ModelElement)
	 */
	@Override
	public void setStartAndInsert(ModelElement part) {
		ModelElement start = k1;
		k1 = part;
		k1.addEdge(this);
		if (isInCircle()) {
			part.removeEdge(this);
			super.setStartAndInsert(start);
		}
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.Kante#setEndAndInsert(tool3lgm.graphtools.elements.ModelElement)
	 */
	@Override
	public void setEndAndInsert(ModelElement parent) {
		ModelElement end = k2;

		k2 = parent;
		k2.addEdge(this);

		if (isInCircle()) {
			parent.removeEdge(this);
			super.setEndAndInsert(end);
		}
	}


	/**
	 * @return
	 */
	public boolean isInCircle () {
		if ((k1 != null) && (k2 != null)) {
			boolean retVal = k2.isPartOf(k1);
			if (retVal)
				Log.show(Log.INFO, Tool3lgmConstants.getErrString("kreis") + 
						"\n" + Tool3lgmConstants.getResString("ModelElement_p") + ":\n" + 
						ModelConstants.getDisplayableName(k1) + ": " + k1.getName() + "\n" +
						ModelConstants.getDisplayableName(k2) + ": " + k2.getName());
			return retVal;
		}
		return false;
	}
	
}

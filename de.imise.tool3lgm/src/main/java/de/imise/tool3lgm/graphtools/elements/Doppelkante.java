package de.imise.tool3lgm.graphtools.elements;


public abstract class Doppelkante extends Kante {
	
	public static final int FORWARD = 1;
	public static final int BACKWARD = 2;
	public static final int DOUBLE = 0;
	public static final int ANY = -2;
	public static final int NOTCONNECTED = -1;
	protected int direction = FORWARD;
	
	public static final int[] DIRECTION = { NOTCONNECTED, DOUBLE, FORWARD, BACKWARD };
	public static final String[] DIRECTION_STR = { "NOTCONNECTED", "DOUBLE", "FORWARD", "BACKWARD" };
	
	public Doppelkante() {
		super();
	}

	public Doppelkante(ModelElement knot1, ModelElement knot2){
		super(knot1,knot2);
	}
		
	/**
	 * @param knot1
	 * @param knot2
	 * @param registerInKnots
	 */
	public Doppelkante(ModelElement knot1, ModelElement knot2, boolean registerInKnots)	{
		super(knot1,knot2,registerInKnots);
	}
		
	/**
	 * @return
	 */
	public int getDirection() {
		return direction;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.Kante#setKnots(tool3lgm.graphtools.elements.ModelElement, tool3lgm.graphtools.elements.ModelElement, boolean)
	 */
	@Override
	public void setKnots(ModelElement _k1, ModelElement _k2, boolean registerInKnots) {
		super.setKnots(_k1, _k2, registerInKnots);
	//	System.err.println(ModelConstants.getMetaAssociationName(this.getClass(), getDirection()));
	}

	/**
	 * @param dir
	 */
	public void setDirection(int dir) {
		direction = dir;
	}

	
	
	/**
	 * 	Teilweise werden Kanten falschherum angelegt, was teilweise auch mal zulässig war. So konnte man
	 * "Aufgabe interpretiert Objekttyp" ausdrücken über eine Kante von der Aufgabe zum Objekttyp
	 * mit der Richtung <code>FORWARD</code> oder über eine Kante vm Objekttyp zur Aufgabe mit der Richtung
	 * <code>BACKWARD</code>. Jetzt ist nur noch eine eindeutige Richtung zulässig. Diese ergibt sich aus
	 * der Startelementklasse und der Endelementklasse, die in einer konkreten Kantenklasse angegeben sind.
	 * 
	 * @see de.imise.tool3lgm.graphtools.elements.Kante#checkValidity()
	 */
	@Override
	public boolean checkValidity() {
		//das Originale Startelement merken
		ModelElement start = k1;
		//die übergeordnete Validitätsprüfung ausführen -> die Elemente wurden evtl. vertauscht.
		if (!super.checkValidity())
			//wenn die übergeordnete Prüfung fehl schlug, ist irgendwas richtig falsch
			return false;
		//wenn die Elemente vertauscht wurden -> wenn nötig auch die Richtung tauschen
		if (k1!=start){
			if (direction==FORWARD)
				direction=BACKWARD;
			else if (direction==BACKWARD)
				direction=FORWARD;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#getXMLEntities()
	 */
	@Override
	public StringBuilder getXMLEntities() {
		return super.getXMLEntities()
		.append("<field name=\"state\">" + DIRECTION_STR[direction + 1] + "</field>");
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.elements.ModelElement#putXMLFieldString(java.lang.String, java.lang.String)
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
	 * @see tool3lgm.graphtools.elements.Kante#isDirecting(tool3lgm.graphtools.elements.ModelElement, tool3lgm.graphtools.elements.ModelElement)
	 */
	@Override
	public boolean isDirecting(ModelElement _k1, ModelElement _k2) {
		switch (direction) {
			case DOUBLE:
				return isConnecting(_k1,_k2);
			case FORWARD: 
				return super.isDirecting(_k1,_k2);
			case BACKWARD:
				return super.isDirecting(_k2,_k1);
			default:
				return false;
		}
	}
	
}

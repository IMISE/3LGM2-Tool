/**
 * 
 */
package de.imise.tool3lgm.graphtools.analyse.redundancy;

import java.util.ArrayList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.log.Log;

/**
 * Diese Klasse führt die Redundnazanalyse, die Dr. Birgit Brigl 1995 in
 * 
 * "Brigl B, Hübner-Bloder G, Wendt T, Haux R, Winter A. (2005). Architectural 
 * Quality Criteria for Hospital Information Systems"
 * 
 * veröffentlicht hat. Es geht um Funktionale Redundanzrate und Funktionale Untersättigungsrate
 * sowie um Datenredundanz von Objekttypen. Das ganze ist eine Art Vorläufer zur "richtigen"
 * Funktionalen Redundanzanalyse.
 * 
 * @author AXS
 */
public class SimpleRedundancyAnalysis {

	//TODO: Diese Werte müssten ein Feld werden, da jedes Element das einen eigenen Redundanzfaktor besitzt, auch einen Gesamtsystemsredunddanzfaktor benötigt.
	//momentan wird der Gesamtfaktor nur für Aufgaben berechnet und für Objekttypen fällt er unter den Tisch
	/**
	 * COMMENTME
	 */
	protected float redundanceFak=0;
	/**
	 * COMMENTME
	 */
	protected float saturationFak=0;

	/**
	 * Das Teilmodell, dessen Redundanz ausgerechnet werden soll
	 */
	private GraphDocument doc;
	
    /**
     * @param doc
     */
    public SimpleRedundancyAnalysis(GraphDocument doc) {
	    super();
	    this.doc = doc;
    }

	/**
	 * @param elementClass
	 * @param show
	 */
	public void computeRedundance(Class<? extends Knoten> elementClass, boolean show) {
		ArrayList<ElementContainer> allElemCont = doc.getElementContainer(elementClass, true);
		int size = allElemCont.size();
		if (size == 0)
			return;
		NodeContainer[] leafs = new NodeContainer[size];
		int leafsSize = 0;
		// in leafs alle Elemente einsammlen, die Blätter sind
		for (int i = 0; i < size; i++) {
			leafs[leafsSize] = (NodeContainer) allElemCont.get(i);
			// den VariablenWert des aktuellen Elements reseten
			leafs[leafsSize].setVariable(0);
			// leafsSize nur erhöhen, wenn aktuelles Element keine Parts hat
			if (!leafs[leafsSize].getElement().hasDirectPartContainer(doc))
				leafsSize++;
		}

		//Aufgabe:	Gesamtanzahl der Konfigurationen an den Blättern (Aufgaben ohne Teilaufgaben)
		//Objekttyp:Gesamtanzahl der Datenbanken "an den Blättern" (Objekttypen ohne Teile)
		//ACHTUNG: haben 2 Aufgaben dieselbe Konfiguration, wird die Konfiguration auch 2x gezählt! 
		//ACHTUNG: haben 2 Objekttypen dasselbe DBS, wird das DBS auch 2x gezählt! 
		int totalRedundanceTypeElemCount=0;

		//Aufgabe:	Anzahl der Aufgaben in leafs, die gar keine Konfigs besitzen
		//Objekttyp:Anzahl der Objekttypen in leafs, die gar nicht gespeichert werden
		int targetTypesWithoutRedundanceType=0;

		//Aufgabe:	Gesamtanzahl der *redundanten* Konfigurationen an den Blättern
		//Objekttyp:Gesamtanzahl der redundanten DBS an den Blättern
		int totalRedundanceCount=0;

		//für jedes Element in leafs
		for (int i=0; i<leafsSize; i++){
			Knoten knoten = (Knoten)leafs[i].getElement();
			//vom Knoten die Liste seiner Elemente holen, die für diesen Knoten redundant sind
			ArrayList<ElementContainer> redundanceTypes = knoten.getRedundanceTypes(doc);
			//Anzahl der Elemente in redundanceTypes holen
			size = redundanceTypes.size();
			//wenn es redundante Elemente besitzt
			if (size > 1) {
				//Gesamtanzahl der redundanten Elemente um die Anzahl des aktuellen Elementes erhöhen
				totalRedundanceCount += size - 1;
			//wenn das aktuelle Element gar Verbindungen zu einem evtl. redundanten Element besitzt (Untersättigung)
			} else if (size == 0) {
				targetTypesWithoutRedundanceType++;
			}
			//Aufgabe:	Gesamtanzahl der Konfigs um Anzahl der Konfigs der aktuellen Aufgabe erhöhen
			//Objekttyp:Gesamtanzahl der DBS um Anzahl der DBS des aktuellen Objekttyps erhöhen
			totalRedundanceTypeElemCount+=size;

			//size auf die Anzahl der redundanten Elemente des aktuellen Elementes setzen
			size--;
			//Aufgaben:	in den Containern die Anzahl ihrer redundanten Konfigs setzen
			//Objekttyp:in den Containern die Anzahl ihrer redundanten DBS setzen
			leafs[i].setVariable(size);
			//Anzahl ihrer redundanten Elemente unten rechts neben den Container schreiben
			leafs[i].setAdditionalTextRightDown((new Integer(size)).toString());
		}
		if (show){
			Knoten knoten;
			try {
				knoten = elementClass.newInstance();
			} catch (Exception ex) {
				Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), ex);
				return;
			}
			//Redundanzfaktor des Gesamtsystems berechen
			redundanceFak = (float) totalRedundanceCount / (float) totalRedundanceTypeElemCount;
			//Untersättigungsfaktor des Gesamtsystems berechen
			saturationFak = (float) targetTypesWithoutRedundanceType / (float) leafsSize;
			String s = knoten.getRedundanceString(redundanceFak, saturationFak);
			doc.getLayer(ModelConstants.DOMAIN_LAYER).setAdditionalTextAbove(Aufgabe.class, s);
		}
	}

	/**
	 * @return the redundanceFak
	 */
	public float getRedundanceFak() {
		return redundanceFak;
	}

	/**
     * @return the saturationFak
     */
    public float getSaturationFak() {
    	return saturationFak;
    }


	
}

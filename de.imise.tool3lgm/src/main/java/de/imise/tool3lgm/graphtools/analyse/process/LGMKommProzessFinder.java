/*
 * Created on 01.03.2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.analyse.process;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.KommBeziehung;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Bausteinschnittstelle;

/**
 * @author AXS
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LGMKommProzessFinder {

	/**
	 * @param steps
	 */
	public LGMKommProzessFinder(GraphDocument doc, List<LGMProzessStep> steps){
		super();
		//alle Analysen finden immer im Gesamtmodell und nicht in einem Teilmodell statt
		getKommProzessForSteps(doc.getCollection().getMainGraphDocument(), steps);
	}
	
	
	/**
	 * Setzt in den übergebenen LGMProzessSteps, den kürzesten Kommunikationspfad, wenn es einen gibt.
	 * (Unendliche Pfade sind an Ende null)
	 * 
	 * @param steps
	 */
	private static final boolean getKommProzessForSteps(GraphDocument doc, List<LGMProzessStep> steps){

		//wenn steps keine Schritte enthält ->return leere Liste
		if (steps.size() == 0)
			return false;

		//ArrayList aller verschiedenen Objekttypen aus den übergebenen steps anlegen 
		List<ModelElement> initialObjekttypes = new ArrayList<ModelElement>();
		for (LGMProzessStep step : steps) {
			//nur vollständige, korrekte Schritte müssen überhaupt betrachtet werden
			if (!step.isCorrect()){
				continue;
			}
			ModelElement ot =  step.getObjektTyp();
			if (!initialObjekttypes.contains(ot))
				initialObjekttypes.add(ot);
		}

		ShortestCommunicationPathFinder sPathFinder = new ShortestCommunicationPathFinder(doc.getCollection());

		//für jeden Schritt
		//1.) Hole alle Anwendungsbausteine seiner Start- und Endkonfiguration
		//2.) Hole alle Bausteinschnittstellen der Anwendungsbausteine seiner Start- und Endkonfiguration
		//3.) Suche den kürzesten Weg von einer der Startschnittstellen zu einer der Endschnittstellen
		//4.) Setze diesen Pfad im Schritt
		
		 
		//für jeden Schritt
		for (LGMProzessStep step : steps) {
			//nur vollständige, korrekte Schritte können einen Pfad haben alle anderen erhalten null
//			System.out.println(stepCount+".]\t"+step.getGraphDocument());
	
	
	
			if (!step.isCorrect()){
				step.setKommProzessKanten(null);
				step.setKommProzessSchnittstellen(null);
				step.setKommProzessLength(-1);
				continue;
			}
			ShortestCommunicationPathFinder.MinimalCommunicationPath minpath = sPathFinder.getShortestPath(step.getStartAufgabeKonfBausteine(), step.getEndAufgabeKonfBausteine(), step.getObjektTyp());
			step.setKommProzessSchnittstellen(minpath.getInterfacePath());
			step.setKommProzessLength(minpath.getPathCosts());
//			System.out.println(step.getKommProzessSchnittstellen());
			
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//Die KantenListe des Schrittes setzen (=Liste der Kanten, die in diesem Schritt durchlaufen werden) und
			//Medienbrüche mitzählen
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			List<Kante> kantenList = new ArrayList<Kante>();
			List<ModelElement> kommProzessSchnittstellen = step.getKommProzessSchnittstellen();
			int count = kommProzessSchnittstellen.size()-1;

			//diese Medeinbruchberechnung ist nicht mehr aktuell
			//->Medienwechsel liegt vor:
			//	- bei Wechsel von einer Kante, die einen NT übterträgt auf eine Kante, die einen DT überträgt und umgekehrt
			//  - immer bei einer Kante die sowohl NT als auch DT überträgt
			//  - immer nach einer Kante, die sowohl einen NT als auch einen DT überträgt

			//speichern, ob die zuletzt übertragene ETNT-Kombination einen Nachrichtentyp und/oder einen Dokumententyp transprotierte
			//->mögliche Werte für lastConnectdType:  
			//  - 0 = initial
			//	- ModelConstants.NACHRICHTENTYP = wenn als letztes ein Nachrichtentyp übertrage wurde
			//	- ModelConstants.DOKUMENTENTYP = wenn als letztes ein Dokumententyp übertrage wurde
			//	- ModelConstants.NACHRICHTENTYP+ModelConstants.DOKUMENTENTYP = wenn als letztes ein Nachrichtentyp und ein Dokumententyp übertragen wurden
//			int lastConnectedType=0;
			
			//für jede Schnittstelle, die in diesem step durchlaufen wird
			for (int i=0; i<count; i++){
				//hole die Schnittstelle
			    ModelElement me1 = kommProzessSchnittstellen.get(i);
				//hole die im step auf sie folgende Schnittstelle
			    ModelElement me2 = kommProzessSchnittstellen.get(i+1);
//				System.out.println(me1 + "\t"+ me1.getConnectionWith(me2,ModelConstants.BSS_BSS_VERBINDUNG, -1).length + "\t" + me2);
				//hole die Kante zw. den beiden Schnittstellen
			    List<Kante> ol = me1.getEdgesWith(me2, KommBeziehung.class);
			    
			    //zw. den Schnittstellen muss es keine Kante geben, wenn sie auf dem selben Baustein liegen
				if (ol.size()<1)
				    continue;
				    
				//zw. 2 Schnittstellen kann es max. eine BSS_BSS_VERBINDUNG geben
				//Kante zur Liste der im Prozessschritt durchlaufenen Kanten hinzufügen
				kantenList.add(ol.get(0));

				//neue Berechnung der Medienbrüche (AXS: 20.10.05)
				//über ModelConstants.ANWENDUNGSBAUSTEIN bekommt man alle Arten von AWBs
				List<ModelElement> bausteinList = me1.getConnectedElements(Anwendungsbaustein.class);
				//hier einfach die 0 nehmen, weil mind. ein Baustein mit der SS verbunden sein muss
				//und weil es nicht das Problem dieses Algorithmus sein kann, wenn eine SS mit mehr 
				//als einem Baustein verbunden ist
				me1 = bausteinList.get(0);
				bausteinList = me2.getConnectedElements(Anwendungsbaustein.class);
				me2 = bausteinList.get(0);
				//bei gemischten AWBs soll das untere Hochzählen der Medienbrüche des Schrittes nicht passieren 
				if (me1.getClass() == Anwendungsbaustein.class || me2.getClass()==Anwendungsbaustein.class)
					me2 = me1;
				if (me1.getClass() != me2.getClass())
					step.setMediumBreaks(step.getMediumBreaks()+1);
			}
				
			step.setKommProzessKanten(kantenList);

			//wenn Kommunikation möglich ist, müssen die tatsächlichen Start- und Endbausteine der Kommunikation gesetzt werden
			if (count>=0){
				//StartKonfiguration durchgehen
				List<ModelElement> realKommunikationBausteine = new ArrayList<ModelElement>();
				Object schnittstelle = kommProzessSchnittstellen.get(0);
				for (ModelElement awb : step.getStartAufgabeKonfBausteine()) {
					boolean found = false;
					List<ModelElement> bausteinSSList = awb.getConnectedElements(Bausteinschnittstelle.class);
					for (ModelElement awbPartParent : awb.getPartAndParentElements())
						bausteinSSList.addAll(awbPartParent.getConnectedElements(Bausteinschnittstelle.class));
					for (Object bausteinSS : bausteinSSList) {
						if (schnittstelle==bausteinSS){
							found = true;
							break;
						}
					}
					if (found)
						realKommunikationBausteine.add(awb);
				}

				step.setRealCommunicationStartKonf(realKommunikationBausteine);

				//EndKonfiguration durchgehen
				realKommunikationBausteine = new ArrayList<ModelElement>();
				schnittstelle = kommProzessSchnittstellen.get(count);
				for (ModelElement awb : step.getEndAufgabeKonfBausteine()) {
					boolean found = false;
					List<ModelElement> bausteinSSList = awb.getConnectedElements(Bausteinschnittstelle.class);
					for (ModelElement awbPartParent : awb.getPartAndParentElements())
						bausteinSSList.addAll(awbPartParent.getConnectedElements(Bausteinschnittstelle.class));
					for (int s=0; s<bausteinSSList.size(); s++){
						if (schnittstelle==bausteinSSList.get(s)){
							found = true;
							break;
						}
					}
					if (found)
						realKommunikationBausteine.add(awb);
				}
				step.setRealCommunicationEndKonf(realKommunikationBausteine);
			}

		}
		return true;
	} 

}
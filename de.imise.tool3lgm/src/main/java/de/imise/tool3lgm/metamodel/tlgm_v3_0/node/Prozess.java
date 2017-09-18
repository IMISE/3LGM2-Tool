/*
 * Created on 26.11.2003
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.FORWARD;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.graphtools.analyse.process.LGMProzessStep;
import de.imise.tool3lgm.graphtools.analyse.process.ProzessStructurePanel;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PrzAufVerbindung;

/**
 * @author AXS Ein Prozess ist ein Node der Kanten zu Aufgaben haelt. Die Reihenfolge der Kanten zu den Aufgaben in der ArrayList connections legt
 *         den Prozessablauf fest.
 */
public final class Prozess extends Node {

    public static Color[] farben = {
            Color.black,
            Color.blue,
            Color.cyan,
            Color.darkGray,
            Color.gray,
            Color.green,
            Color.magenta,
            Color.orange,
            Color.pink,
            Color.red,
            Color.yellow
    };

    public static int colorCounter = 0;

    public int color;

    public Prozess() {
        super();
        // das hier ist insofern problematisch, als dass beim Erstellen (nicht aus Datei) eines Prozeses immer der Konstruktor
        // 2x ausgeführt wird. Das erste mal beim öffnen des NamenDialog und das 2. mal beim Klick auf OK. Bei Abbrechen ists nur
        // 1x. Daher ist die Farbe momentan absolut nicht verhersagbar (nicht weiter schlimm, aber auch nicht wirklich gut!)

        color = colorCounter;
        colorCounter = (colorCounter + 1) % farben.length;
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addTab(getResString("Aufgabe_p"), new ProzessStructurePanel(dialog, PrzAufVerbindung.class, AufObjVerbindung.class));
        //		dialog.addTab(getResString("Kommunikationsprozess_p"),new KommProzessPanel(dialog));
        return dialog;
    }

    //	public ArrayList getAufgabenContainer(GraphDocument doc){
    //		return getConnectedContainer(Aufgabe.class,doc,null,Doppelkante.FORWARD);
    //	}
    //	public static ArrayList getAufgabenContainer(Prozess prozess, GraphDocument doc){
    //		return prozess.getConnectedContainer(Aufgabe.class,doc,null,Doppelkante.FORWARD);
    //	}

    //----------------------------------------------------------------------------------------------------------------------------------
    /**
     * Gibt eine Liste mit Prozessschritten für eine einzelne Aufgabe zurück. In den einzelnen LGMProzessStep dieser Liste sind nur die startAufgabe,
     * endAufgabe und der Objekttyp gesetzt. endAufgabe ist immer identisch mit der Aufgabe an Position pos aus
     * der Liste der Prozessaufgaben. Es ist also eine Liste aller Aufgaben die im Prozess vor aufgabenPos stehen inkl. der von diesen Aufgaben im
     * gegebenen GraphDocument bearbeiteten Objekttypen, die von der Aufgabe an pos gleichzeitig interpretiert
     * werden. Ex. kein Objekttyp den die Aufgabe an pos interpretiert und eine Vorgaengeraufgabe bearbeitet, wird ein LGMProzessStep zurueckgegeben,
     * in dem nur die endAufgabe der Aufgabe an pos entspricht und der Rest null ist. Wird testOnly=true
     * uebergeben, dann werden nicht alle Schritte gesucht sondern nur der erste vollstaendige zurückgegeben oder eine leere Liste, wenn keiner
     * existiert. Pos wird wie immer ab 0 gezaehlt.
     *
     * @param ModelElement
     * @param int
     * @param GraphDocument
     * @param boolean
     */
    public List<LGMProzessStep> getProcessStepsForAufgabe(final List<ModelElement> aufgaben, final int pos, final boolean testOnly) {
        return getProcessStepsForAufgabe(aufgaben, null, pos, testOnly);
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    /**
     * Gibt eine Liste mit Prozessschritten für eine einzelne Aufgabe zurück. In den einzelnen LGMProzessStep dieser Liste sind nur die startAufgabe,
     * endAufgabe und der Objekttyp gesetzt. endAufgabe ist immer identisch mit der uebergebenen Aufgabe. Es ist
     * also eine Liste aller Aufgaben die im Prozess vor aufgabenPos stehen inkl. der von diesen Aufgaben im gegebenen GraphDocument bearbeiteten
     * Objekttypen, die von der übergebenen Aufgabe gleichzeitig interpretiert werden. Ex. kein Objekttyp den die
     * uebergebene Aufgabe interpretiert und eine Vorgaengeraufgabe bearbeitet, wird ein LGMProzessStep zurueckgegeben, in dem nur die endAufgabe der
     * uebergebenen Aufgabe entspricht und der Rest null ist. Wird testOnly=true uebergeben, dann werden nicht
     * alle Schritte gesucht sondern nur der erste vollstaendige zurückgegeben oder eine leere Liste, wenn keiner existiert. Pos wird wie immer ab 0
     * gezaehlt.
     *
     * @param ArrayList Liste mit Elementen der Klasse <code>Aufgabe</code>.
     * @param ModelElement
     * @param int
     * @param GraphDocument
     * @param boolean
     */
    public List<LGMProzessStep> getProcessStepsForAufgabe(final List<ModelElement> aufgaben, ModelElement aufgabe, final int pos, final boolean testOnly) throws IndexOutOfBoundsException, IllegalArgumentException {
        //		System.out.println("########   " + pos + ".) " + aufgabe);
        if (aufgabe != null && !(aufgabe instanceof Aufgabe)) {
            throw new IllegalArgumentException();
        }

        //wenn aufgabe null ist, dann soll aufgabe die Aufgabe werden, die an pos steht
        if (pos < 1 || pos > aufgaben.size() || aufgabe == null && pos == aufgaben.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (aufgabe == null) {
            aufgabe = aufgaben.get(pos);
        }

        List<LGMProzessStep> returnList = new ArrayList<>();
        //alle Objekttypen holen, die aufgabe interpretiert

        //		GraphDocument doc = aufgabe.getGraphDocument();

        //Alle von der Aufgabe interpretierten OT in ein Set legen
        Set<ModelElement> setOfInterpretedObjectTypes = new HashSet<>();
        //Alle von den Parts und Parents der Aufgabe interpretierten OT diesem Set hinzufügen
        for (ModelElement auf : aufgabe.getPartAndParentElements()) {
            setOfInterpretedObjectTypes.addAll(auf.getConnectedElements(Objekttyp.class, AufObjVerbindung.class, BACKWARD));
        }

        //für alle Aufgaben in der ProzessListe vor der übergebenen Position
        for (int i = 0; i < pos; i++) {
            ModelElement startAufgabe = aufgaben.get(i);
            //hole die bearbeiteten Objekttypen der i-ten Aufgabe in der Aufgabenliste des Prozesses
            Set<ModelElement> usedObjekttypenOfAufgabe = new HashSet<>();
            for (ModelElement auf : startAufgabe.getPartAndParentElements()) {
                usedObjekttypenOfAufgabe.addAll(auf.getConnectedElements(Objekttyp.class, AufObjVerbindung.class, FORWARD));
            }
            //für jeden dieser Objekttypen
            for (ModelElement usedObjekttyp : usedObjekttypenOfAufgabe) {
                //prüfe, ob übergebene Aufgabe diesen auch interpretiert; inkl. Teil-Objekttypen
                if (setOfInterpretedObjectTypes.contains(usedObjekttyp)) {
                    //wenn ja -> ProzessStep anlegen
                    returnList.add(new LGMProzessStep(this, startAufgabe, aufgabe, usedObjekttyp, i, pos));
                    //wenn nur getestet werden soll, ob es überhaupt einen gibt -> dann (beim ersten) raus hier
                    if (testOnly) {
                        return returnList;
                    }
                }
            }
        }
        //testOnly==false -> es soll nicht nur irgendein vollständiger Prozessschritt gesucht werden und
        //returnList.size()==0 -> es wurde keiner gefunden
        //=> einen LGMProzessStep anlegen, der nur die uebergebene Aufgabe ale endAufgabe enthaelt
        //		if (!testOnly && returnList.size()==0)
        //			returnList.add(new LGMProzessStep(getContainer(doc), null, aufgabe, null, -1, pos));
        return returnList;
    }

    //----------------------------------------------------------------------------------------------------------------------------------

    public List<LGMProzessStep> getKommProzessStepCombinations() {
        return getKommProzessStepCombinations(getConnectedElements(Aufgabe.class));
    }

    /**
     * Liefert eine Liste aller Prozesschritte und all ihrer Kombinationen, die sich aus unterschiedlichen Konfigurationen der Aufgaben ergeben. Die
     * aufgaben muss eine Liste der ElementContainer von Aufgaben aus dem selben GraphDocument sein. Es wird null
     * zurückgegeben, wenn die ArrayList aufgaben weniger als 2 Elemente hat. Folgende Arten von Schritten koennen in der Rueckgabeliste stehen: 1.)
     * vollstaendige Schritte, also Schritte fuer die nach einem Kommunikationsprozess gesucht werden kann (alle
     * ElementContainer des LGMProzessStep (Startaufgabe, Endaufgabe, Objekttyp, Startkonfiguration, Endkonfiguration) sind nicht null) 2.)
     * LGMProzessStep, in dem nur die endAufgabe gesetzt ist ( = endAufgabe hat keinen Objekttyp interpretiert, den eine
     * der Aufgaben davor im Prozess bearbeitet hat (siehe getBuisnessProcessSteps)) 3.) LGMProzessStep, in dem eine oder beide Konfigurationen null
     * sind ( = mind. eine Aufgabe hat keine Konfiguration)
     *
     * @param ArrayList
     */
    public List<LGMProzessStep> getKommProzessStepCombinations(final List<ModelElement> aufgaben) {
        if (aufgaben.size() < 2) {
            return null;
        }
        //		System.out.println("aufgaben besteht aus " + aufgabenAnzahl + " Aufgaben");

        //Array von ArrayListen der Konfigurationen aller Aufgaben
        @SuppressWarnings("unchecked")
        List<ModelElement>[] konfigs = new ArrayList[aufgaben.size()];

        //steht die gleiche Aufgabe mehrmals in aufgaben, so bekommt sie an jeder Stelle die gleiche
        //Konfigurationsreferenz -> diese braucht dann nur 1x gesetzt werden
        for (int i = 0; i < aufgaben.size(); i++) {
            ModelElement auf = aufgaben.get(i);
            for (ModelElement aufOrgKom : auf.getConnectedElements(AufOrgKombination.class, AufAufOrgVerbindung.class)) {
                konfigs[i] = aufOrgKom.getConnectedElements(ABKonfiguration.class, AwbkAufOrgVerbindung.class);
            }
            for (int j = i + 1; j < aufgaben.size(); j++) {
                if (aufgaben.get(j) == auf) {
                    konfigs[j] = konfigs[i];
                }
            }
        }

        //		System.out.println("######################################################################");
        //		for (int i=0; i<aufgabenAnzahl; i++){
        //			System.out.println(aufgaben.get(i) + " hat folgende Konfigurationen: ");
        //			for (int z=0; z<konfigs[i].size(); z++)
        //				System.out.println("\t" + (z+1) + ".) " + ((Konfiguration)((KonfigurationContainer)konfigs[i].get(z)).getElement()).getServers(doc));
        //			System.out.println();
        //		}

        //Gesamtliste aller in den uebergebenen Aufgaben moeglichen BuisnessProcessSteps zusammenbauen
        List<LGMProzessStep> returnList = new ArrayList<>();
        for (int i = 1; i < aufgaben.size(); i++) {
            returnList.addAll(getProcessStepsForAufgabe(aufgaben, i, false));
        }
        //		System.out.println(returnList.size() + " Geschäftsprozessschritte sind identifiziert worden");
        //jetzt geht es darum, für jeden BuisnessProcessSteps dieser Liste alle seine Varianten unterschiedlicher Konfigurationen zu erzeugen
        List<LGMProzessStep> varianten = new ArrayList<>();
        //für jeden Geschäftsprzessschritt in returnList
        for (int i = 0; i < returnList.size(); i++) {
            //hole den Schritt
            LGMProzessStep step = returnList.get(i);
            //startAufgabe ist nur ungleich null, wenn ein korrekter Geschäftsprozessschritt vorliegt und nur für solche kann es Varianten geben
            if (step.getStartAufgabe() == null) {
                continue;
            }
            if (step.getEndAufgabe() == null) {
                continue;
            }
            //Positionen der Aufgaben im Prozess holen
            int indexOfStartAufgabe = step.getStartPosition();
            int indexOfEndAufgabe = step.getEndPosition();

            //System.out.println (i + " " + konfigs[indexOfStartAufgabe].size() + " " + konfigs[indexOfEndAufgabe].size());
            //alle vollständigen Konfigurationsvarianten hinzufügen (Schritte der Art 1.)
            for (int m = 0; m < konfigs[indexOfEndAufgabe].size(); m++) {
                for (int n = 0; n < konfigs[indexOfStartAufgabe].size(); n++) {
                    varianten.add(LGMProzessStep.cloneAndSetKonfigs(step, konfigs[indexOfStartAufgabe].get(n), konfigs[indexOfEndAufgabe].get(m)));
                }
            }
        }
        return varianten;
    }

    //	/*
    //
    //
    //	public ArrayList _getKommProzessStepCombinations(GraphDocument doc, ArrayList aufgaben){
    //		if (aufgaben.size()<2) return null;
    ////		System.out.println("aufgaben besteht aus " + aufgabenAnzahl + " Aufgaben");
    //
    //		//Array von ArrayListen der Konfigurationen aller Aufgaben
    //		ArrayList[] konfigs = new ArrayList[aufgaben.size()];
    //
    //		//steht die gleiche Aufgabe mehrmals in aufgaben, so bekommt sie an jeder Stelle die gleiche
    //		//Konfigurationsreferenz -> diese braucht dann nur 1x gesetzt werden
    //		for (int i=0; i<aufgaben.size(); i++){
    //			if (konfigs[i]==null){
    //				konfigs[i] = new ArrayList(10);
    //				Object o = aufgaben.get(i);
    //				for (int j=i+1; j<aufgaben.size(); j++)
    //					if (aufgaben.get(j)==o)
    //						konfigs[j] = konfigs[i];
    //			}
    //		}
    //
    //		//hole alle Konfigurationen dieses Szenarios
    //		ArrayList abKonfigurationen = doc.getElementContainer(ABKonfiguration.class);
    //
    //		//in konfigs an die Stelle der jeweiligen Aufgabe die Liste ihrer Konfigurationen schreiben
    //		//hat die i-te Aufgabe keine Konfiguration, ist nach der Schleife konfigs[i]==new ArrayList()
    //		for (int i=0; i<abKonfigurationen.size(); i++){
    //			KonfigurationContainer abkonf = (KonfigurationContainer)abKonfigurationen.get(i);
    //			ArrayList tempList = ((ABKonfiguration)abkonf.getElement()).getClients(doc);
    //			if (tempList.size()<1)
    //				continue;
    //			Object aufgabe = tempList.get(0);
    //			for (int j=0; j<aufgaben.size(); j++)
    //				if (aufgabe==aufgaben.get(j)){
    //					konfigs[j].add(abkonf);
    //					break; //man braucht immer nur das erste konfig zu setzen, weil die anderen bei der selben Aufgabe das selbe Objekt sind
    //				}
    //		}
    //
    ///*
    //		System.out.println("######################################################################");
    //		for (int i=0; i<aufgabenAnzahl; i++){
    //			System.out.println(aufgaben.get(i) + " hat folgende Konfigurationen: ");
    //			for (int z=0; z<konfigs[i].size(); z++)
    //				System.out.println("\t" + (z+1) + ".) " + ((Konfiguration)((KonfigurationContainer)konfigs[i].get(z)).getElement()).getServers(doc));
    //			System.out.println();
    //		}
    //* /
    //		//Gesamtliste aller in den uebergebenen Aufgaben moeglichen BuisnessProcessSteps zusammenbauen
    //		ArrayList returnList = new ArrayList(100);
    //		for (int i=1; i<aufgaben.size(); i++)
    //			returnList.addAll(getProcessStepsForAufgabe(doc, aufgaben, i, false));
    ////		System.out.println(returnList.size() + " Geschäftsprozessschritte sind identifiziert worden");
    //		//jetzt geht es darum, für jeden BuisnessProcessSteps dieser Liste alle seine Varianten unterschiedlicher Konfigurationen zu erzeugen
    //		ArrayList varianten = new ArrayList(100);
    //		//für jeden Geschäftsprzessschritt in returnList
    //		for (int i=0; i<returnList.size(); i++){
    //			//hole den Schritt
    //			LGMProzessStep step = (LGMProzessStep)returnList.get(i);
    //			//startAufgabe ist nur ungleich null, wenn ein korrekter Geschäftsprozessschritt vorliegt und nur für solche kann es Varianten geben
    //			if (step.getStartAufgabe()==null){
    //				continue;
    //			}
    //			if (step.getEndAufgabe()==null){
    //				continue;
    //			}
    //			//Positionen der Aufgaben im Prozess holen
    //			int indexOfStartAufgabe = step.getStartPosition();
    //			int indexOfEndAufgabe = step.getEndPosition();
    //
    //			//System.out.println (i + " " + konfigs[indexOfStartAufgabe].size() + " " + konfigs[indexOfEndAufgabe].size());
    //			//alle vollständigen Konfigurationsvarianten hinzufügen (Schritte der Art 1.)
    //			for (int m=0; m<konfigs[indexOfEndAufgabe].size(); m++){
    //				for (int n=0; n<konfigs[indexOfStartAufgabe].size(); n++){
    //					varianten.add(LGMProzessStep.cloneAndSetKonfigs(step, konfigs[indexOfStartAufgabe].get(n), konfigs[indexOfEndAufgabe].get(m)));
    //				}
    //			}
    //		}
    //		return varianten;
    //	}
    //
    //
    //	*/

}

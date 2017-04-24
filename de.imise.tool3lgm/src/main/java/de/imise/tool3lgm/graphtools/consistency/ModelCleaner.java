package de.imise.tool3lgm.graphtools.consistency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.OrgAufOrgVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.ABKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.AufOrgKombination;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.DBKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Organisationseinheit;
import de.imise.tool3lgm.graphtools.elements.node.PhysischerDVBaustein;
import de.imise.tool3lgm.graphtools.elements.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * Stellt Funktionen, mit denen das Modell bereinigt werden kann.
 *
 * @author AXS created on 03.07.2007
 */
public class ModelCleaner {

    /** Wenn <code>true</code> werden einige berichtigte Fehler ausgegeben */
    private final boolean PRINT_ERRORS = false;

    /**
     * Das Modell, das bereinigt werden soll
     */
    private final GDCollection gdcoll;

    /**
     * Initialisiert einen neuen <code>ModelCleaner</code>.
     *
     * @param gdcoll
     */
    public ModelCleaner(final GDCollection gdcoll) {
        super();
        this.gdcoll = gdcoll;
    }

    /**
     * Führt allgemeine Konsitenz-Bereinigungen auf der gesetzten Collection aus
     */
    public void cleanModel() {
        if (gdcoll == null) {
            return;
        }

        GraphDocument mainDoc = gdcoll.getMainGraphDocument();

        ArrayList<GraphDocument> docs = new ArrayList<GraphDocument>();
        docs.add(mainDoc);
        for (Szenario szen : gdcoll.getSzenarios()) {
            docs.add(szen);
        }

        // Bei allen Elementen den ContainerTable mit den LayerContainern synchronisieren
        // Es kann vorkommen, dass einige Kanten einen Container auf den Layern im Hauptmodell und
        // einem Teilmodell haben, aber in ihrem ContainerTable nur ein Container für das Hauptmodell
        // eingetragen ist, der aber eigentlich der Container aus dem Teilmodell ist. Das ist absoluter
        // Mist und wird hier berichtigt.
        for (GraphDocument doc : docs) {
            for (LayerContainer lc : doc.getLayers()) {
                ArrayList<ElementContainer> allEc = new ArrayList<ElementContainer>(lc.getKnoten());
                allEc.addAll(lc.getKanten());
                allEc.addAll(lc.getKnickpunkte());
                // für alle Container des aktuellen GraphDocuments
                for (ElementContainer layerElemCont : allEc) {
                    ModelElement layerElemMe = layerElemCont.getElement();
                    // wenn der Container aus dem ContainerTable des Modellelements für das doc ein
                    // anderer ist als der auf dem Layer des docs
                    if (layerElemMe.getContainer(doc) != layerElemCont) {
                        if (PRINT_ERRORS) {
                            System.err.println("1: " + layerElemMe.getClass().getSimpleName() + " " + doc + " " + layerElemMe.getClearName() + " " + layerElemMe.getHashString() + " " + layerElemMe.getCreationDate());
                        }
                        // im ContainerTable denselben Container setzen, wie im Layer
                        layerElemMe.setContainer(doc, layerElemCont);
                    }
                    if (doc instanceof Szenario) {
                        // wenn das Element zwar in einem Teilmodell, aber nicht im Gesamtmodell
                        // vorkommt -> Container im Gesamtmodell erzeugen und adden (dieser Fehler
                        // trat noch nie auf, aber sicher ist sicher)
                        if (layerElemMe.getContainer(mainDoc) == null) {
                            if (PRINT_ERRORS) {
                                System.err.println("2: " + layerElemMe.getClass().getSimpleName() + " " + doc + " " + layerElemMe.getClearName() + " " + layerElemMe.getHashString() + " " + layerElemMe.getCreationDate());
                            }
                            layerElemCont.clone(false, mainDoc);
                        }
                        // falls das Element unique ist, aber aus irgendwelchen Gründen auch in
                        // einem Teilmodell einen Container hat -> im Teilmodell löschen
                        // (dieser Fehler sollte eigentlich unkritisch sein und trat im
                        // UKL-KIS-Modell bei PdvbPdvbkVerbindung, OrgAufOrgVerbindung,
                        // PdvbStoVerbindung, PdvbBtypVerbindung und DbsDatVerbindung auf)
                        if (layerElemMe.isUnique()) {
                            if (PRINT_ERRORS) {
                                System.err.println("3: " + layerElemMe.getClass().getSimpleName() + " " + doc + " " + layerElemMe.getClearName() + " " + layerElemMe.getHashString() + " " + layerElemMe.getCreationDate());
                            }
                            layerElemMe.removeContainer(doc);
                            lc.remove(layerElemCont);
                        }
                    }
                }
            }
        }

        // Jetzt ist sicher gestellt, dass alle Container, die bei den Layern eingetragen sind auch
        // bei allen Elementen eingetragen sind.
        // Nun für alle Modellelemente alle ihre Container überprüfen, ob es diese überhaupt geben
        // darf und wenn ja, ob sie auch korrekt auf den Layern eingetragen sind.
        // Dieser Fehler trat bisher noch nicht auf, aber es ist besser, das noch einmal explizit
        // sicher zu stellen!
        for (ModelElement me : mainDoc.getModelItems(ModelElement.class, true)) {
            for (GraphDocument doc : me.getContainerTable().keySet()) {
                // Unique Elemente dürfen keinen Container außerhalb des Hauptmodells haben
                if (me.isUnique() && doc instanceof Szenario) {
                    me.removeContainer(doc);
                    // prüfen, ob alle Container im ContainerTable des Elements auch auf dem Layer liegen
                } else {
                    ElementContainer ec = me.getContainer(doc);
                    LayerContainer lc = doc.getLayer(me.layerFor());
                    if (me instanceof Knickpunkt) {
                        if (!lc.getKnickpunkte().contains(ec)) {
                            if (PRINT_ERRORS) {
                                System.err.println("4: " + me.getClass().getSimpleName() + " " + doc + " " + me.getClearName() + " " + me.getHashString() + " " + me.getCreationDate());
                            }
                            lc.add(ec);
                        }
                    } else if (me instanceof Knoten) {
                        if (!lc.getKnoten().contains(ec)) {
                            if (PRINT_ERRORS) {
                                System.err.println("5: " + me.getClass().getSimpleName() + " " + doc + " " + me.getClearName() + " " + me.getHashString() + " " + me.getCreationDate());
                            }
                            lc.add(ec);
                        }
                    } else if (me instanceof Kante) {
                        if (!lc.getKanten().contains(ec)) {
                            if (PRINT_ERRORS) {
                                System.err.println("6: " + me.getClass().getSimpleName() + " " + doc + " " + me.getClearName() + " " + me.getHashString() + " " + me.getCreationDate());
                            }
                            lc.add(ec);
                        }
                    }
                }
            }
        }

        // Alle ABKonfigurationen löschen, die keiner AufOrgKombination zugeordnet sind. Davon gibt
        // es in alten Modellen aus irgend einem Grund sehr viele removeInconsistentElements(gdcoll,
        // ABKonfiguration.class, AufOrgKombination.class, null);

        int pid = TransactionManager.STANDARD_PID;
        // Alle Knickpunkte löschen, die keiner Kante zugeordnet sind. So etwas trat in alten Modellen
        // auf und sollte gleich am Anfang ausgeschlossen werden
        ArrayList<ElementContainer> al = new ArrayList<ElementContainer>();
        for (Szenario szen : gdcoll.getSzenarios()) {
            for (ElementContainer kc : szen.getElementContainer(Knickpunkt.class)) {
                BendpointContainer bpc = (BendpointContainer) kc;
                Knickpunkt kp = bpc.getKnickpunktKnoten();
                EdgeContainer ec = kp.getOwner();
                // wenn der Owner null ist oder der Knickpunktcontainer nicht richtig in der
                // KnickpunktContainerListe seines Owners steht -> löschen
                if (ec == null) {
                    // System.err.println("nullllll");
                    // System.err.println(gdcoll.getSzenario(i));
                    al.add(kc);
                } else if (ec.getBendpointContainerList().indexOf(bpc) == -1) {
                    // System.err.println("owner kennt den nicht");
                    // System.err.println(gdcoll.getSzenario(i));
                    al.add(kc);
                }

            }
        }

        for (ElementContainer kpc2delete : al) {
            //da die hier zu löschenden Knickpunkte keinen Owner haben, muss man sie einfach aus
            //all ihren GraphDocuments löschen. Da nicht sicher ist, ob layerFor() des Bendpoints
            //den richtigen Layer zurück liefert -> einfach auf allen Layern löschen (wo sie nicht
            //enthalten sind, passiert einfach nichts)
            ModelElement bendpoint = kpc2delete.getElement();
            for (GraphDocument doc : bendpoint.getMySzenarios()) {
                for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
                    doc.getLayer(ModelConstants.LAYERS[i]).remove(kpc2delete);
                }
            }
        }

        // Alle inkonsistenten Kanten löschen = alle Kanten, denen Start- oder Endelement fehlen
        // oder die dieselben Elemente mehrfach verbinden, obwohl sie nur 1 mal verbunden sein sollten
        ArrayList<ModelElement> edges = mainDoc.getModelItems(Kante.class, true);
        for (int i = 0; i < edges.size(); i++) {
            Kante edge = (Kante) edges.get(i);
            // Kanten mit fehlendem Start- und Endelement löschen
            if (edge.getStart() == null || edge.getEnd() == null) {
                gdcoll.deleteElement(edge, mainDoc, pid);
                continue;
            }
            // Kanten löschen, die nicht mehrfach vorkommen dürfen, aber mehrfach vorkommen
            // (alle bis auf eine löschen)
            if (ModelConstants.isMultipleEdgeClass(edge.getClass())) {
                continue;
            }
            for (Kante edge2 : edge.getStart().getEdgesTo(edge.getEnd(), edge.getClass())) {
                if (edge != edge2) {
                    gdcoll.deleteElement(edge2, mainDoc, pid);
                    // edge2 befindet sich auf jeden Fall hinter edge in der Liste edges, sonst wäre
                    // vorher edge2 schon mal edge gewesen und edge wäre dann edge2 gewesen. ALso kann
                    // man edge2 einfach hinten aus der Liste entfernen.
                    edges.remove(edge2);
                }
            }
        }

        // Alle Knoten- und KantenContainer löschen, bei denen das zugehörige ModelElement null ist
        for (Szenario szen : gdcoll.getSzenarios()) {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                LayerContainer lc = szen.getLayer(i);
                if (lc == null) {
                    break;
                }
                for (int j = lc.getKnotenCount() - 1; j >= 0; j--) {
                    NodeContainer kc = lc.getNodeContainer(j);
                    if (kc.getKnoten() == null) {
                        gdcoll.removeContainerFromSubmodel(kc, pid);
                    }
                }
                for (int j = lc.getKantenCount() - 1; j >= 0; j--) {
                    EdgeContainer kc = lc.getEdgeContainer(j);
                    Kante edge = kc.getEdge();
                    if (edge == null || edge.getStart().getContainer(szen) == null || edge.getEnd().getContainer(szen) == null) {
                        gdcoll.removeContainerFromSubmodel(kc, pid);
                        continue;
                    }
                }
            }
        }

        // die ganzen irgendwann beim Zusammenführen mal sinnlos reingekommenen LeerZeichen und
        // Leeerzeilen sowie das "-ZUSAMMENGEFÜHRT-" oder "-JOINED-" löschen
        final String[] superflousStrings = {
                "-ZUSAMMENGEFÜHRT-",
                "-JOINED-"
        };
        for (ModelElement me : gdcoll.getMainGraphDocument().getModelItems(ModelElement.class, true)) {
            // Element-Namen und Beschreibungen bereinigen
            me.setName(getCleanString(me.getName(), Tool3lgmConstants.getResString("joined"), superflousStrings));
            me.setDescription(getCleanString(me.getDescription(), Tool3lgmConstants.getResString("joined"), superflousStrings));

            // alle Radiobuttons, Comboboxes und Kennzahlen, die beim Zusammenführen irgendwelche
            // komischen Werte zusammengeführt bekommen haben, wieder berichtigen
            for (UserField uf : new ArrayList<UserField>(me.getUserFieldInputValueKeys())) {
                if (uf == null) {
                    continue;
                }
                if (uf.getStyle() == Style.RADIO_BUTTON || uf.getStyle() == Style.COMBO_BOX || uf.getStyle() == Style.CLASSIFICATION_NUMBER) {
                    String value = me.getUserFieldInputValue(uf);
                    for (String superFlous : superflousStrings) {
                        int superFlousStart = value.indexOf(superFlous);
                        if (superFlousStart >= 0) {
                            me.setUserFieldInputValue(uf, value.substring(0, superFlousStart).trim());
                        }
                    }
                }
            }

            UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
            for (Class<? extends ModelElement> elementClass : ModelConstants.ALL_ELEMENTS_SET) {
                for (UserField uf : definitions.getUserFields(elementClass)) {
                    // eigentlich haben Checkboxen keine Listenwerte, da sie nur true oder false für
                    // eine einzelne Box darsellen, aber falls aus der einzelnen Checkbox mal eine
                    // ButtonGroup mit mehreren Checkboxes gemacht wird, haut das hier gleich hin.
                    if (uf.getStyle() != Style.RADIO_BUTTON && uf.getStyle() != Style.COMBO_BOX || uf.getStyle() == Style.CHECK_BOX) {
                        continue;
                    }
                    ArrayList<String> listValues = new ArrayList<String>(uf.getListValuesCount());
                    loop: for (int t = 0; t < uf.getListValuesCount(); t++) {
                        String listValue = uf.getListValueAt(t);
                        for (String superFlous : superflousStrings) {
                            if (listValue.indexOf(superFlous) >= 0 || listValue.equals(UserField.EMPTY_STRING)) {
                                continue loop;
                            }
                        }
                        listValues.add(listValue);
                    }
                    uf.removeAllStandardValues();
                    for (String newListValue : listValues) {
                        uf.addListValue(newListValue);
                    }
                }
            }
        }

        // Alle evtl. auch fehlenden Kanten in allen Szenarios nachtragen und die Slave-Elemente
        // an die richtige Stelle bringen
        for (ModelElement me : mainDoc.getModelItems(Knoten.class, true)) {
            for (Szenario szen : gdcoll.getSzenarios()) {
                ElementContainer ec = me.getContainer(szen);
                if (ec == null) {
                    continue;
                }
                szen.createEdgeContainer(ec, szen, false, pid);
                szen.raiseSlaves(ec);
            }
        }

        // Alle initial vorhandenen untergeordneten Elemente erzeugen, die nicht mehr da sind.
        // Diese kann man nicht von Hand neu erzeugen
        for (Class<? extends ModelElement> elementClass : ModelConstants.ALL_NODES_SET) {
            Set<Class<? extends Kante>> subTypeEdges = ModelConstants.getInitialSubtypes(elementClass);
            if (subTypeEdges == null || subTypeEdges.size() == 0) {
                continue;
            }
            for (ModelElement me : gdcoll.getMainGraphDocument().getModelItems(elementClass, false)) {
                gdcoll.createInitialSubtypes(me, TransactionManager.STANDARD_PID);
            }
        }

        // Sicher stellen, dass bei allen berichtigten Kanten und Knickpunkten die Container richtig
        // positioniert sind also einfach nochmal pauzschal alle Kanten-Container initialisieren (das
        // wird und muss bereits einmal nach dem Beenden des Einlesens im ToolContentHanlder getan werden)
        for (GraphDocument doc : docs) {
            doc.initKnotContainers();
            doc.initTraceContainers();
        }
    }

    /**
     * Entfernt in dem übergebenen String alle überflüssigen Leerzeichen am Anfang und Ende und
     * löscht entfernt alle "-ZUSAMMENGEFÜHRT-" Eintragungen, die im Grunde dieselben String
     * trennen. Wenn dieselben Informationen mehrfach durch "-ZUSAMMENGEFÜHRT-" getrennt im
     * übergebenen String stehen, dann kommt die Information nur noch einmal zurück. Wenn
     * verschiedene String durch "-ZUSAMMENGEFÜHRT-" getrennt im Ausgangstring stehen, dann kommt
     * der Ausgangstring zurück. (also das "-ZUSAMMENGEFÜHRT-" bleibt erhalten) @ param sourceString
     * String der bereinigt werden soll
     *
     * @param newDelimiter Yeichenkette, die als neuer Trenner im Rückgabestring eingebaut werden
     *            soll, wenn 2 verschiedene Zeichenketten duruch einen der 'alten' Delimiter aus
     *            delimiter getrennt waren
     * @param delimiter Strings, die eventuell identische Tokens untereinander Aufteilen. Beim
     *            Zusammenführen von Elementen besteht der Name evtl 2 Mal aus derselben
     *            Zeichenkette, die durch "-ZUSAMMENGEFÜHRT-" voneinander getrennt sind.
     * @return
     */
    public static final String getCleanString(String sourceString, final String newDelimiter, final String... delimiter) {
        // alle Leerzeichen und Zeilenumbrüche am Anfang und Ende entfernen
        sourceString = sourceString.trim();
        // Liste mit allen Tokens des Ausgangsstring, die durch "-ZUSAMMENGEFÜHRT-" voneinander
        // getrennt im Ausgangsstring stehen
        ArrayList<String> subStringList = new ArrayList<String>();
        // ArrayList<String> subStringDelimiterStringList = new ArrayList<String>();

        // Index, ab dem nach dem nächsten Auftreten von "-ZUSAMMENGEFÜHRT-" gesucht wird
        int startIndex = 0;
        // Gesamtlänge des Ausgangsstrings
        int length = sourceString.length();
        // man muss immer nach dem deutschen "-ZUSAMMENGEFÜHRT-" und dem englischen "-JOINED-"
        // suchen
        loop: for (int superflousStringIndex = 0; superflousStringIndex < delimiter.length; superflousStringIndex++) {
            // Index an dem "-ZUSAMMENGEFÜHRT-" ab dem Startindex im Ausgangsstring auftaucht
            int endIndex = sourceString.indexOf(delimiter[superflousStringIndex], startIndex);
            // wenn "-ZUSAMMENGEFÜHRT-" im String stand
            if (endIndex != -1) {
                // wenn davor wenigstens ein Inhaltszeichen stand
                if (startIndex < endIndex) {
                    // merke den aktuellen TeilString zwischen startIndex und dem Beginn von
                    // "-ZUSAMMENGEFÜHRT-" in der Tokenliste
                    subStringList.add(sourceString.substring(startIndex, endIndex).trim());
                    // subStringDelimiterStringList.add(superflousStrings[superflousStringIndex]);
                }
                // Startindex auf den Index nach dem aktuellen "-ZUSAMMENGEFÜHRT-" erhöhen
                startIndex = endIndex + delimiter[superflousStringIndex].length();
                // ab da wieder nach dem Auftreten von "-ZUSAMMENGEFÜHRT-" oder "-JOINED-" suchen
                // (solange ein "-ZUSAMMENGEFÜHRT-" oder "-JOINED-" gefunden wurde, wiederholt er
                // diese for -Schleife immer mit aktuellen Zeichenindex im Ausgangsstring)
                superflousStringIndex = -1;
                continue loop;
            }
        }
        // Wenn kein "-ZUSAMMENGEFÜHRT-" oder "-JOINED-" (mehr) gefunden wurde
        if (startIndex < length) {
            // speichere den letzten (und evtl. einzigen) Token in der Tokenliste und trimme ihn
            subStringList.add(sourceString.substring(startIndex).trim());
        }

        // Wenn der String nur aus Zeichen bestanden hat, die beim trim() gelöscht werden oder
        // nur aus "-ZUSAMMENGEFÜHRT-" oder "-JOINED-" und ansonsten wegtrimbaren Zeichen, dann
        // ist die Tokenliste leer -> Leeren String zurückgeben
        if (subStringList.size() == 0) {
            return "";
        }
        // Der Ausgangsstring hatte gar kein "-ZUSAMMENGEFÜHRT-" oder "-JOINED-" in sich oder diese
        // Zeichenketten standen ganz am Anfang oder ganz hinten
        if (subStringList.size() == 1) {
            // gib den einzigen vorhandenen Token zurück
            return subStringList.get(0);
        }

        StringBuilder sb = new StringBuilder();

        // es gibt mind. 2 Token -> vergleichen ob, dieselbe Zeichenkette jedes Tokens am Anfang
        // oder am Ende eines anderen Tokens vorkommt
        loop: for (int i = 0; i < subStringList.size(); i++) {
            String firstToken = subStringList.get(i);
            for (int j = i + 1; j < subStringList.size(); j++) {
                String nextToken = subStringList.get(j);
                // wenn der aktuelle Token einen darausfolgenden Token enthält
                if (firstToken.indexOf(nextToken) != -1) {
                    // lösche den darauffolgenden Token aus der Liste und weiter
                    subStringList.remove(j--);
                    continue;
                }
                // wenn ein Folgetoken den aktuellen Token en
                if (nextToken.indexOf(firstToken) != -1) {
                    // lösche den aktuellen Token aus der Liste und weiter
                    subStringList.remove(i--);
                    continue loop;
                }
            }
            // der aktuelle Token muss dem RückgabeString angefügt werden, wenn davor schon was
            // steht, dann wieder das "-ZUSAMMENGEFÜHRT-" dazwischen schreiben
            if (sb.length() > 0) {
                sb.append("\n\n-");
                sb.append(newDelimiter);
                sb.append("-\n\n");
            }
            sb.append(firstToken);
        }
        return sb.toString();
    }

    // ///////////////////////////////////////////////////////
    // Funktionen, die bestimmte Inkonsistenzen beseitigen //
    // ///////////////////////////////////////////////////////

    // TODO:AXS:showResultDialog wird in den folgenden Funktionen gar nicht beachtet

    /**
     * Löscht alle inkonsitenten Anwendungsbaustein-Konfigurationen aus dem Gesamtmodell.<br>
     * Inkonsitente Anwendungsbaustein-Konfigurationen sind mit keinem Anwendungsbaustein verbunden.
     *
     * @param showResultDialog wenn <code>true</code>, wird ein Dialog mit demErgebnis angezeigt
     */
    @SuppressWarnings("unchecked")
    void removeInconsistentAWBConfigurationsWithoutAWB(final boolean showResultDialog) {
        @SuppressWarnings("rawtypes")
        Class[] connectedElementClasses = {
                Anwendungsbaustein.class,
                RechAnwendungsbaustein.class,
                KonAnwendungsbaustein.class,
        };
        removeInconsistentElements(gdcoll, ABKonfiguration.class, connectedElementClasses, "clean_result_inconsistent_abwconfig");
    }

    /**
     * Löscht alle <code>AufOrgKombination</code>en, die mit keiner <code>Aufgabe</code> oder keiner <code>Organisationseinheit</code> verbunden sind.
     *
     * @param showResultDialog wenn <code>true</code>, wird ein Dialog mit demErgebnis angezeigt
     */
    void removeInconsistentAufOrgKombinations(final boolean showResultDialog) {
        removeInconsistentElements(gdcoll, AufOrgKombination.class, Organisationseinheit.class, "clean_result_inconsistent_auforg");
    }

    /**
     * Löscht alle inkonsitenten Physischen Datenverarbeitungsbaustein-Konfigurationen aus dem
     * Gesamtmodell.<br>
     * Inkonsitente Physischen Datenverarbeitungsbaustein-Konfigurationen sind mit keinem physischen
     * Datenverarbeitungsbaustein verbunden.
     *
     * @param showResultDialog wenn <code>true</code>, wird ein Dialog mit demErgebnis angezeigt
     */
    void removeInconsistentPDVBConfigurations(final boolean showResultDialog) {
        removeInconsistentElements(gdcoll, DBKonfiguration.class, PhysischerDVBaustein.class, "clean_result_inconsistent_pdvbconfig");
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////
    // Konfigurationen von Aufgaben umhängen (hat eigentlich nichts mit dem Model cleanen zu tun) //
    // //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Hängt der Aufgabe eine AufOrgKombination</code> unter mit den gleichen <code>ABKonfiguration</code>en und AWB. Die
     * <code>AufOrgKombination</code> und die <code>ABKonfiguration</code>en werden neu angelegt mit allen Assoziationen, die sie zu
     * anderen Elementen haben.
     *
     * @param gdcoll
     * @param auf
     * @param aufOrg
     */
    private static final void cloneConfig(final GDCollection gdcoll, final Aufgabe auf, final AufOrgKombination aufOrg, final int transactionID) {
        // eine neue AufOrgKombi anlegen, diese mit der Aufgabe verbinden und Name und Beschreibung
        // der alten AufOrgKombi
        // für die neue übernehmen
        GraphDocument mainDoc = gdcoll.getMainGraphDocument();

        mainDoc.createKnotenWithContainer(AufOrgKombination.class, aufOrg.getName(), aufOrg.getDescription(), transactionID);
        ModelElement newAufOrg = mainDoc.getLastCreated().getElement();
        gdcoll.link(AufAufOrgVerbindung.class, auf, newAufOrg, transactionID);
        newAufOrg.setDescription(aufOrg.getDescription());

        // für alle Kanten der aktuellen AufOrgKombination
        for (Kante kante : aufOrg.getEdges()) {
            // bestimme das Element, mit dem die aktuelle AufOrgKombination verbunden ist
            ModelElement elemToConnect;
            if (kante.getStart() == aufOrg) {
                elemToConnect = kante.getEnd();
            } else {
                elemToConnect = kante.getStart();
            }
            // wenn das verbundene Element eine AWB-Konfiguration ist, muss diese auch für
            // jeden AWB neu angelegt werden
            if (elemToConnect instanceof ABKonfiguration) {
                mainDoc.createKnotenWithContainer(ABKonfiguration.class, elemToConnect.getName(), elemToConnect.getDescription(), transactionID);
                ModelElement newKonfig = mainDoc.getLastCreated().getElement();
                gdcoll.link(AwbkAufOrgVerbindung.class, newAufOrg, newKonfig, transactionID);
                newKonfig.setDescription(elemToConnect.getDescription());
                // Alle Verbindungen der Konfiguration auch klonen (außer die zur Originalen
                // AufOrgKombination)
                for (Kante edge : elemToConnect.getEdges()) {
                    if (!(edge instanceof AwbkAufOrgVerbindung)) {
                        ModelElement etc;
                        if (edge.getStart() == elemToConnect) {
                            etc = edge.getEnd();
                        } else {
                            etc = edge.getStart();
                        }
                        gdcoll.link(edge.getClass(), newKonfig, etc, transactionID);
                    }
                }
                // alle anderen Verbindungen der AufOrgKombination zu Organisationseinheiten sein
                // ebenfalls klonen
            } else if (elemToConnect instanceof Organisationseinheit) {
                gdcoll.link(OrgAufOrgVerbindung.class, newAufOrg, elemToConnect, transactionID);
            }
        }

    }

    // /**
    // * Hängt der Aufgabe eine AufOrgKombination</code> unter mit den gleichen
    // <code>ABKonfiguration</code>en
    // * und AWB. Die <code>AufOrgKombination</code> und die <code>ABKonfiguration</code>en werden
    // neu
    // * angelegt mit allen Assoziationen, die sie zu anderen Elementen haben.
    // * @param gdcoll
    // * @param auf
    // * @param aufOrg
    // */
    // private static final void cloneConfig(GDCollection gdcoll, Aufgabe auf, AufOrgKombination
    // aufOrg, int transactionID) {
    // //eine neue AufOrgKombi anlegen, diese mit der Aufgabe verbinden und Name und Beschreibung
    // der alten AufOrgKombi
    // //für die neue übernehmen
    // ModelElement newAufOrg = gdcoll.createKnotenWithContainer(AufOrgKombination.class,
    // aufOrg.getName(), transactionID).getElement();
    // gdcoll.link(AufAufOrgVerbindung.class, auf, newAufOrg, transactionID);
    // newAufOrg.setDescription(aufOrg.getDescription());
    //
    // //für alle Kanten der aktuellen AufOrgKombination
    // for (Kante kante : aufOrg.getEdges()) {
    // //bestimme das Element, mit dem die aktuelle AufOrgKombination verbunden ist
    // ModelElement elemToConnect;
    // if (kante.getStart()==aufOrg)
    // elemToConnect = kante.getEnd();
    // else
    // elemToConnect = kante.getStart();
    // //wenn das verbundene Element eine AWB-Konfiguration ist, muss diese auch für
    // //jeden AWB neu angelegt werden
    // if (elemToConnect instanceof ABKonfiguration) {
    // ModelElement newKonfig = gdcoll.createKnotenWithContainer(ABKonfiguration.class,
    // elemToConnect.getName(), transactionID).getElement();
    // gdcoll.link(AwbkAufOrgVerbindung.class, newAufOrg, newKonfig, transactionID);
    // newKonfig.setDescription(elemToConnect.getDescription());
    // //Alle Verbindungen der Konfiguration auch klonen (außer die zur Originalen
    // AufOrgKombination)
    // for (Kante edge : elemToConnect.getEdges()) {
    // if (!(edge instanceof AwbkAufOrgVerbindung)) {
    // ModelElement etc;
    // if (edge.getStart()==elemToConnect)
    // etc = edge.getEnd();
    // else
    // etc = edge.getStart();
    // gdcoll.link(edge.getClass(), newKonfig, etc, transactionID);
    // }
    // }
    // //alle anderen Verbindungen der AufOrgKombination zu Organisationseinheiten sein ebenfalls
    // klonen
    // }else if (elemToConnect instanceof Organisationseinheit) {
    // gdcoll.link(OrgAufOrgVerbindung.class, newAufOrg, elemToConnect, transactionID);
    // }
    // }
    //
    // }

    /**
     * Prüft, ob die übergebene <code>Aufgabe</code> Konfigurationen besitzt, die sie an vorhandene
     * Teilaufgaben weitergeben kann.
     *
     * @param auf
     * @return
     */
    public boolean hasCloneableConfigs(final Aufgabe auf) {
        // hole alle AufOrgKombinationen der Aufgabe
        ArrayList<ModelElement> aufOrgs = auf.getConnectedElementsByEdge(AufAufOrgVerbindung.class);
        // wenn die Aufgabe nichts zu vererben hat -> nächste Aufgabe
        if (aufOrgs.size() == 0) {
            return false;
        }
        // hole alle Blattaufgaben, die der aktuellen Aufgabe untergeordnet sind
        // wenn sie keine Blattaufgaben hat, kann sie an niemanden etwas vererben -> nächste Aufgabe
        ArrayList<ElementContainer> absParts = auf.getAbsolutePartContainer(gdcoll.getMainGraphDocument());
        if (absParts.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * @param auf
     * @see #cloneConfigsToParts(Aufgabe, int)
     */
    public final void cloneConfigsToParts(final Aufgabe auf) {
        int transactionId = GraphDocument.createTransactionId();
        GraphDocument doc = gdcoll.getMainGraphDocument();
        doc.start_transaction(transactionId);
        cloneConfigsToParts(auf, transactionId);
        doc.finish_transaction(transactionId, false);
        doc.distributeEvent(GraphDocument.DATA_CHANGED, transactionId);
    }

    /**
     * Gibt alle Konfigurationen der übergebenen Aufgabe an ihre Teilaufgaben weiter.<br>
     * Im Einzelnen werden für jede Teilaufgabe alle Konfigurationen mit allen
     *
     * @param auf
     * @param transactionId
     */
    private boolean cloneConfigsToParts(final Aufgabe auf, final int transactionId) {
        // hole alle AufOrgKombinationen der Aufgabe
        ArrayList<ModelElement> aufOrgs = auf.getConnectedElementsByEdge(AufAufOrgVerbindung.class);
        // wenn die Aufgabe nichts zu vererben hat -> nächste Aufgabe
        if (aufOrgs.size() == 0) {
            return false;
        }

        GraphDocument mainDoc = gdcoll.getMainGraphDocument();

        // hole alle Blattaufgaben, die der aktuellen Aufgabe untergeordnet sind
        // wenn sie keine Blattaufgaben hat, kann sie an niemanden etwas vererben -> nächste Aufgabe
        ArrayList<ElementContainer> absParts = auf.getAbsolutePartContainer(mainDoc);
        if (absParts.size() == 0) {
            return false;
        }
        // für alle absoluten Teilaufgaben
        for (Iterator<ElementContainer> absPartsIt = absParts.iterator(); absPartsIt.hasNext();) {
            Aufgabe aufPart = (Aufgabe) absPartsIt.next().getElement();
            // für alle AufOrgKombinationen, die für jede Teilaufgabe neu angelegt werden muss
            for (Iterator<ModelElement> aufOrgsIt = aufOrgs.iterator(); aufOrgsIt.hasNext();) {
                AufOrgKombination aufOrg = (AufOrgKombination) aufOrgsIt.next();
                // alle AufOrgKombinationen und ihre Konfigurationen klonen
                cloneConfig(gdcoll, aufPart, aufOrg, transactionId);
            }
        }
        // jetzt die originale AufOrgKombination und alle ihre Konfigurationen löschen
        for (Iterator<ModelElement> aufOrgsIt = aufOrgs.iterator(); aufOrgsIt.hasNext();) {
            AufOrgKombination aufOrg = (AufOrgKombination) aufOrgsIt.next();
            ArrayList<ModelElement> abKonfigs = aufOrg.getConnectedElements(ABKonfiguration.class);

            String[] hashesToDelete = new String[abKonfigs.size() + 1];
            hashesToDelete[0] = aufOrg.getHashString();
            for (int i = 0; i < abKonfigs.size(); i++) {
                hashesToDelete[i + 1] = abKonfigs.get(i).getHashString();
            }
            gdcoll.deleteElements(hashesToDelete, transactionId);
            // mainDoc.removeElements(hashesToDelete, transactionId);
        }
        return true;
    }

    /**
     * Klont von Aufgaben sämtliche AufOrgKombinationen mit sämtlichen AWB-Konfigurationen an die
     * Teilaufgaben und entfernt die Originale von der Oberaufgabe.
     *
     * @param showResultDialog wenn <code>true</code>, wird ein Dialog mit demErgebnis angezeigt
     */
    public final void copyAufOrgKombinationsToAufLeafs(final boolean showResultDialog) {
        int transactionId = GraphDocument.createTransactionId();

        int aufCount = 0;

        GraphDocument mainDoc = gdcoll.getMainGraphDocument();
        mainDoc.start_transaction(transactionId);

        // alle Aufgaben holen
        ArrayList<ModelElement> aufgaben = mainDoc.getModelItems(Aufgabe.class);
        // für jede Aufgabe
        for (Iterator<ModelElement> aufIt = aufgaben.iterator(); aufIt.hasNext();) {
            Aufgabe auf = (Aufgabe) aufIt.next();
            if (hasCloneableConfigs(auf)) {
                System.err.println(auf);
            }
            if (cloneConfigsToParts(auf, transactionId)) {
                aufCount++;
            }
        }
        mainDoc.finish_transaction(transactionId, false);
        mainDoc.distributeEvent(GraphDocument.DATA_CHANGED, transactionId);
        if (showResultDialog) {
            String resultString = "Es wurde von " + aufCount + " Aufgaben Konfigurationen an Teilaufgaben übertragen.";
            JOptionPane.showMessageDialog(Static.getMainFrame(), resultString, Tool3lgmConstants.getResString("clean_model"), JOptionPane.INFORMATION_MESSAGE);
        }

    }

    // /////////////////////////
    // Allgemeine Funktionen //
    // /////////////////////////

    /**
     * @param gdcoll
     * @param searchElementClass
     * @param connectedElementClass
     * @param resultStringKey
     * @see #removeInconsistentElements(GraphDocument, int, int[], String)
     */
    @SuppressWarnings("unchecked")
    public static final void removeInconsistentElements(final GDCollection gdcoll, final Class<? extends ModelElement> searchElementClass, final Class<? extends ModelElement> connectedElementClass, final String resultStringKey) {
        @SuppressWarnings("rawtypes")
        Class[] connectedElementClasses = {
                connectedElementClass
        };
        removeInconsistentElements(gdcoll, searchElementClass, connectedElementClasses, resultStringKey);
    }

    /**
     * Löscht alle Elemente der Art <code>searchType</code> aus dem Modell <code>doc</code>, das
     * keine Verbindung zu einem der in <code>connectedTypes</code> angegebenen Elementarten
     * besitzt.<br>
     * Das Ergebnis wird ausgegeben, wenn ein nicht leerer <code>resultStringKey</code> angegeben
     * wird, der aus den Resourcen einen String lädt, wlcher beschreibt, was gelöscht wurde. Die
     * Anzahl der gelöschten Elemente wird diesem String in der Ausgabe vorangestellt.
     *
     * @param doc
     * @param searchElementClass
     * @param connectedElementClasses
     * @param resultStringKey
     */
    public static final void removeInconsistentElements(final GDCollection gdcoll, final Class<? extends ModelElement> searchElementClass, final Class<? extends ModelElement>[] connectedElementClasses, final String resultStringKey) {
        HashSet<ModelElement> elems = getNotConnected(gdcoll.getMainGraphDocument(), searchElementClass, connectedElementClasses);
        if (elems.size() > 0) {
            String[] hashesToDelete = new String[elems.size()];
            int i = 0;
            for (ModelElement me : elems) {
                hashesToDelete[i++] = me.getHashString();
            }
            gdcoll.deleteElements(hashesToDelete, GraphDocument.createTransactionId());
        }
        String resultString = null;
        if (resultStringKey != null && !resultStringKey.trim().equals("")) {
            resultString = elems.size() + " " + Tool3lgmConstants.getResString(resultStringKey);
            JOptionPane.showMessageDialog(Static.getMainFrame(), resultString, Tool3lgmConstants.getResString("clean_model"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Liefert alle Elemente der Art <code>searchType</code>, die im angegebenen Modell nicht mit
     * Elementen der Art <code>connectedTypes</code> verbunden sind.
     *
     * @param doc Modell in dem gesucht wird
     * @param searchElementClass Art der Elemente, die gesucht werden sollen
     * @param connectedTypes Arten der Elemente, mit denen die gesuchten Elemente nicht verbunden
     *            sein dürfen
     * @return Liste aller gefundenen Elemente
     */
    private static HashSet<ModelElement> getNotConnected(final GraphDocument doc, final Class<? extends ModelElement> searchElementClass, final Class<? extends ModelElement>[] connectedElementClasses) {
        ArrayList<ModelElement> searchElems = doc.getModelItems(searchElementClass, true);
        HashSet<ModelElement> returnList = new HashSet<ModelElement>();
        // zählt die Anzahl der zu zu löschenden Konfigurationen
        for (ModelElement elem : searchElems) {
            ArrayList<ModelElement> connectedElems = elem.getConnectedElements(connectedElementClasses[0]);
            for (int i = 1; i < connectedElementClasses.length && connectedElems.size() == 0; i++) {
                connectedElems.addAll(elem.getConnectedElements(connectedElementClasses[i]));
            }
            if (connectedElems.size() == 0) {
                returnList.add(elem);
            }
        }
        return returnList;
    }

}

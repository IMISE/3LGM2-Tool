package de.imise.tool3lgm.graphtools.consistency;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;
import static de.imise.tool3lgm.graphtools.userfield.UserField.EMPTY_STRING;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CHECK_BOX;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.CLASSIFICATION_NUMBER;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.COMBO_BOX;
import static de.imise.tool3lgm.graphtools.userfield.UserField.Style.RADIO_BUTTON;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.xml.ToolXMLParser;
import de.imise.util.HashStringGenerator;

/**
 * Stellt Funktionen, mit denen das Modell bereinigt werden kann.
 *
 * @author AXS created on 03.07.2007
 */
public class ModelCleaner {

    /** Wenn <code>true</code> werden einige berichtigte Fehler ausgegeben */
    private final boolean PRINT_ERRORS = false;

    /** Das Modell, das bereinigt werden soll */
    private final GDCollection gdcoll;

    /** Das MetaModel der zu bereinigen GDCollection */
    private final MetaModel metaModel;

    /**
     * Initialisiert einen neuen <code>ModelCleaner</code>.
     *
     * @param gdcoll
     */
    public ModelCleaner(final GDCollection gdcoll) {
        super();
        this.gdcoll = gdcoll;
        metaModel = gdcoll.getMetaModel();
    }

    /**
     * Führt allgemeine Konsitenz-Bereinigungen auf der gesetzten Collection aus
     */
    public void cleanModel() {
        if (gdcoll == null) {
            return;
        }

        boolean bulkMode = gdcoll.isBulkMode();
        gdcoll.setBulkMode(true);

        GraphDocument mainDoc = gdcoll.getMainDoc();

        List<GraphDocument> docs = new ArrayList<>();
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
                List<ElementContainer> allEc = new ArrayList<>(lc.getNodeContainerCount() + lc.getEdgeContainerCount() + lc.getBendpointContainerCount());
                lc.addAllContainers(allEc);
                // für alle Container des aktuellen GraphDocuments
                for (ElementContainer layerElemCont : allEc) {
                    ModelElement layerElemMe = layerElemCont.getElement();
                    // wenn der Container aus dem ContainerTable des Modellelements für das doc ein
                    // anderer ist als der auf dem Layer des docs
                    if (layerElemMe.getContainer(doc) != layerElemCont) {
                        if (PRINT_ERRORS) {
                            print(layerElemMe, doc, 1);
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
                                print(layerElemMe, doc, 2);
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
                                print(layerElemMe, doc, 3);
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
            for (GraphDocument doc : me.getMySzenarios()) {
                // Unique Elemente dürfen keinen Container außerhalb des Hauptmodells haben
                if (me.isUnique() && doc instanceof Szenario) {
                    me.removeContainer(doc);
                    // prüfen, ob alle Container im ContainerTable des Elements auch auf dem Layer liegen
                } else {
                    ElementContainer ec = me.getContainer(doc);
                    LayerContainer lc = doc.getLayer(me.layerFor());
                    if (!lc.isMyElement(ec)) {
                        if (PRINT_ERRORS) {
                            print(me, doc, 4);
                        }
                        lc.add(ec);
                    }
                }
            }
        }

        // Alle Knickpunkte löschen, die keiner Edge zugeordnet sind. So etwas trat in alten Modellen
        // auf und sollte gleich am Anfang ausgeschlossen werden
        List<GraphDocument> allDocs = Lists.newArrayList(gdcoll.getSzenarios());
        allDocs.add(mainDoc);
        for (GraphDocument doc : allDocs) {
            for (LayerContainer lc : doc.getLayers()) {
                for (int i = lc.getBendpointContainerCount() - 1; i >= 0; i--) {
                    boolean ok = true;
                    BendpointContainer bpc = lc.getBendpointContainer(i);
                    Bendpoint bp = bpc.getBendpoint(); //das hier ist der Container aus dem Hauptdokument
                    if (bp.getContainerCount() != 2) {
                        ok = false;
                    } else {
                        EdgeContainer ec = bp.getOwner(); //das hier ist der Container aus dem (einzigen) Szenario, in dem der Knickpunkt vorkommt
                        // wenn der Owner null ist oder der Knickpunktcontainer nicht richtig in der
                        // KnickpunktContainerListe seines Owners steht -> löschen
                        if (ec == null) {
                            // System.err.println("nullllll");
                            // System.err.println(gdcoll.getSzenario(i));
                            ok = false;
                        } else {
                            GraphDocument ecDoc = ec.getGraphDocument(); // das hier muss ein Szeario sein, weil der Owner des Knickpunktes nur in einem Szenario sein kann
                            if (ecDoc == null || !(ecDoc instanceof Szenario)) {
                                ok = false;
                            } else {
                                ElementContainer szenBpc = bp.getContainer(ecDoc);
                                if (ec.indexOfBendpointContainer((BendpointContainer) szenBpc) == -1) {
                                    // System.err.println("owner kennt den nicht");
                                    // System.err.println(gdcoll.getSzenario(i));
                                    ok = false;
                                }
                            }
                        }
                    }

                    if (!ok) {
                        lc.remove(bpc);
                    }
                }
            }
        }

        // Alle inkonsistenten Kanten löschen = alle Kanten, denen Start- oder Endelement fehlen
        // oder die dieselben Elemente mehrfach verbinden, obwohl sie nur 1 mal verbunden sein sollten
        List<ModelElement> edges = mainDoc.getModelItems(Edge.class, true);
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = (Edge) edges.get(i);
            // Kanten mit fehlendem Start- und Endelement löschen
            if (edge.getStart() == null || edge.getEnd() == null) {
                gdcoll.deleteElement(edge, mainDoc, STANDARD_PID);
                continue;
            }
            // Kanten löschen, die nicht mehrfach vorkommen dürfen, aber mehrfach vorkommen
            // (alle bis auf eine löschen)
            if (MetaModel.isMultipleEdgeClass(edge.getClass())) {
                continue;
            }
            for (Edge edge2 : edge.getStart().getEdgesTo(edge.getEnd(), edge.getClass())) {
                if (edge != edge2) {
                    gdcoll.deleteElement(edge2, mainDoc, STANDARD_PID);
                    // edge2 befindet sich auf jeden Fall hinter edge in der Liste edges, sonst wäre
                    // vorher edge2 schon mal edge gewesen und edge wäre dann edge2 gewesen. ALso kann
                    // man edge2 einfach hinten aus der Liste entfernen.
                    edges.remove(edge2);
                }
            }
        }

        // Alle Node- und KantenContainer löschen, bei denen das zugehörige ModelElement null ist
        for (Szenario szen : gdcoll.getSzenarios()) {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                LayerContainer lc = szen.getLayer(i);
                if (lc == null) {
                    break;
                }
                for (int j = lc.getNodeContainerCount() - 1; j >= 0; j--) {
                    NodeContainer kc = lc.getNodeContainer(j);
                    if (kc.getNode() == null) {
                        gdcoll.removeContainerFromSubmodel(kc, STANDARD_PID);
                    }
                }
                for (int j = lc.getEdgeContainerCount() - 1; j >= 0; j--) {
                    EdgeContainer kc = lc.getEdgeContainer(j);
                    Edge edge = kc.getEdge();
                    if (edge == null || edge.getStart() == null || edge.getEnd() == null || edge.getStart().getContainer(szen) == null || edge.getEnd().getContainer(szen) == null) {
                        gdcoll.removeContainerFromSubmodel(kc, STANDARD_PID);
                        continue;
                    }
                }
            }
        }

        // die ganzen irgendwann beim Zusammenführen mal sinnlos reingekommenen LeerZeichen und
        // Leeerzeilen sowie das "-ZUSAMMENGEFÜHRT-" oder "-JOINED-" löschen
        final String[] superflousStrings = {
                "-ZUSAMMENGEFÜHRT-", "-JOINED-"
        };
        for (ModelElement me : gdcoll.getMainDoc().getModelItems(ModelElement.class, true)) {
            // Element-Namen und Beschreibungen bereinigen
            me.setName(getCleanString(me.getName(), getResString("joined"), superflousStrings));
            me.setDescription(getCleanString(me.getDescription(), getResString("joined"), superflousStrings));

            // alle Radiobuttons, Comboboxes und Kennzahlen, die beim Zusammenführen irgendwelche
            // komischen Werte zusammengeführt bekommen haben, wieder berichtigen
            for (UserField uf : new ArrayList<>(me.getUserFieldInputValueKeys())) {
                if (uf == null) {
                    continue;
                }
                if (uf.getStyle() == RADIO_BUTTON || uf.getStyle() == COMBO_BOX || uf.getStyle() == CLASSIFICATION_NUMBER) {
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
            for (Class<? extends ModelElement> elementClass : metaModel.allElementsSet) {
                for (UserField uf : definitions.getUserFields(elementClass)) {
                    // eigentlich haben Checkboxen keine Listenwerte, da sie nur true oder false für
                    // eine einzelne Box darsellen, aber falls aus der einzelnen Checkbox mal eine
                    // ButtonGroup mit mehreren Checkboxes gemacht wird, haut das hier gleich hin.
                    if (uf.getStyle() != RADIO_BUTTON && uf.getStyle() != COMBO_BOX || uf.getStyle() == CHECK_BOX) {
                        continue;
                    }
                    List<String> listValues = new ArrayList<>(uf.getListValuesCount());
                    loop: for (int t = 0; t < uf.getListValuesCount(); t++) {
                        String listValue = uf.getListValueAt(t);
                        for (String superFlous : superflousStrings) {
                            if (listValue.indexOf(superFlous) >= 0 || listValue.equals(EMPTY_STRING)) {
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
        for (ModelElement me : mainDoc.getModelItems(Node.class, true)) {
            for (Szenario szen : gdcoll.getSzenarios()) {
                ElementContainer ec = me.getContainer(szen);
                if (ec == null) {
                    continue;
                }
                szen.createEdgeContainer(ec, szen, false, STANDARD_PID);
                szen.raiseSlaves(ec);
            }
        }

        // Alle initial vorhandenen untergeordneten Elemente erzeugen, die nicht mehr da sind.
        // Diese kann man nicht von Hand neu erzeugen
        for (Class<? extends ModelElement> elementClass : metaModel.allNodesSet) {
            Set<Class<? extends Edge>> subTypeEdges = metaModel.getInitialSubtypes(elementClass);
            if (subTypeEdges == null || subTypeEdges.size() == 0) {
                continue;
            }
            for (ModelElement me : gdcoll.getMainDoc().getModelItems(elementClass, false)) {
                gdcoll.createInitialSubtypes(me, STANDARD_PID);
            }
        }

        //Alle InferenceEdges erzeugen, die fehlen
        gdcoll.updateInferenceEdges(STANDARD_PID);

        // Sicher stellen, dass bei allen berichtigten Kanten und Knickpunkten die Container richtig
        // positioniert sind also einfach nochmal pauschal alle Kanten-Container initialisieren (das
        // wird und muss bereits einmal nach dem Beenden des Einlesens im ToolContentHandler getan werden)
        for (GraphDocument doc : docs) {
            doc.initNodeContainers();
            //doc.initEdgeContainers(); Das hier darf auf keinen Fall gemacht werden, weil die Variable BendpointContainer.index einfach nicht richtig gesetzt ist, sondern nur beim Einlesen und nur da keine Probleme macht.
            //wenn man das initEdgeContainers() hier nochmal aufruft, haben Knickpunktcontainer einen Falschen Wert bei Index (nämlich 0) und sie werden in der Knickpunktliste der Kante einfach nach vorne geschrieben und
            //stehen dann mehrfach drin und der eigentliche Knickpunkt an position 0 ist weg
        }

        gdcoll.setBulkMode(bulkMode);
    }

    /**
     * @param me
     * @param doc
     * @param id
     */
    private void print(final ModelElement me, final GraphDocument doc, final int id) {
        if (PRINT_ERRORS) {
            String hashString = me.getHashString();
            System.err.println(id + ": " + me.getClass().getSimpleName() + " " + doc + " " + me.getClearName() + " " + hashString + " " + HashStringGenerator.getCreationTimeMedium(hashString));
        }
    }

    /**
     * Entfernt in dem übergebenen String alle überflüssigen Leerzeichen am
     * Anfang und Ende und löscht entfernt alle "-ZUSAMMENGEFÜHRT-"
     * Eintragungen, die im Grunde dieselben String trennen. Wenn dieselben
     * Informationen mehrfach durch "-ZUSAMMENGEFÜHRT-" getrennt im übergebenen
     * String stehen, dann kommt die Information nur noch einmal zurück. Wenn
     * verschiedene String durch "-ZUSAMMENGEFÜHRT-" getrennt im Ausgangstring
     * stehen, dann kommt der Ausgangstring zurück. (also das
     * "-ZUSAMMENGEFÜHRT-" bleibt erhalten) @ param sourceString String der
     * bereinigt werden soll
     *
     * @param newDelimiter Yeichenkette, die als neuer Trenner im Rückgabestring
     *            eingebaut werden soll, wenn 2 verschiedene Zeichenketten
     *            duruch einen der 'alten' Delimiter aus delimiter getrennt
     *            waren
     * @param delimiter Strings, die eventuell identische Tokens untereinander
     *            Aufteilen. Beim Zusammenführen von Elementen besteht der Name
     *            evtl 2 Mal aus derselben Zeichenkette, die durch
     *            "-ZUSAMMENGEFÜHRT-" voneinander getrennt sind.
     * @return
     */
    public static final String getCleanString(String sourceString, final String newDelimiter, final String... delimiter) {
        // alle Leerzeichen und Zeilenumbrüche am Anfang und Ende entfernen
        sourceString = sourceString.trim();
        // Liste mit allen Tokens des Ausgangsstring, die durch "-ZUSAMMENGEFÜHRT-" voneinander
        // getrennt im Ausgangsstring stehen
        List<String> subStringList = new ArrayList<>();
        // ArrayList<String> subStringDelimiterStringList = new ArrayList<>();

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

    // /////////////////////////
    // Allgemeine Funktionen //
    // /////////////////////////

    /**
     * Bis Datei-Version 3.4 (siehe {@link ToolXMLParser}) gab es
     * IsPartOfBeziehungen, bei denen das Teil-Element StartElement und das
     * Oberelement EndElement der Kante war. Bei den anderen existierenden
     * Unterordnungsbeziehungen, den Compositions, war das genau andersrum, also
     * das Oberelement war Start und das Unterelement EndElement.<br>
     * Das wurde dahingehend vereinheitlicht, dass die IsPartOfEdges zu
     * HasPartEdges geändert wurden, bei denen genau wie bei den Compositions
     * das Oberelement nun das Startelement ist und beide Klasse dieselbe
     * Oberklasse SubordinationEdge haben können, die jetzt dafür verantwortlich
     * ist, ob verbundene Elemente in der Grafik mitbewegt werden können und die
     * eventuelle Kreise in diesen Unterordnungen checkt, die unzulässig sind.
     *
     * @param gdcoll
     */
    public static final void switchIsEdgesToHasPartEdges(final GDCollection gdcoll) {
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        List<ModelElement> hasPartEdges = mainDoc.getModelItems(HasPartEdge.class, true);
        for (ModelElement edge : hasPartEdges) {
            HasPartEdge hasPartEdge = (HasPartEdge) edge;
            ModelElement part = hasPartEdge.getStart(); // ist ja noch falsch herum
            ModelElement parent = hasPartEdge.getEnd(); // ist ja noch falsch herum
            hasPartEdge.setNodes(parent, part, false);
        }

    }

}

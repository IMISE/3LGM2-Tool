package de.imise.tool3lgm.plugin;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.ABKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.AufOrgKombination;
import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.DBKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.Organisationseinheit;
import de.imise.tool3lgm.graphtools.elements.node.PhysischerDVBaustein;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.Alphabetical;
import de.imise.util.io.FileHandler;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * Exportiert eine Tabelle mit den Spalten
 * "Aufgabe_ID", "Aufgabe", "Teilaufgabe_ID", "Teilaufgabe", "Beschreibung", "Ort der Durchführung", "Verantwortliche Rolle", "Anwendungssystem",
 * "Physischer DV-Baustein", "Benötiger Speicherplatz"
 *
 * @author AXS
 * @create 02.07.2012
 */
public class B1ExportPlugin implements Plugin {

    /** Datei, in die als letztes exportiert wurde */
    private File lastSelectedFile = null;

    @Override
    public Action getAction() {
        return new AbstractAction("Bereich 1 Tabellenexport") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                GraphDocument doc = Static.getSelectedDoc();
                if (doc == null) {
                    return;
                }
                ArrayList<ElementContainer> functions = doc.getElementContainer(Aufgabe.class);
                //maximale Tiefe der Aufgabenhierarchie feststellen
                int maxHierarchyDepth = 0;
                ArrayList<ElementContainer> absolutePartContainer = new ArrayList<ElementContainer>();
                for (ElementContainer funcEc : functions) {
                    ModelElement func = funcEc.getElement();
                    if (func.hasDirectPartContainer(doc)) {
                        continue;
                    }
                    absolutePartContainer.add(funcEc);
                    int currentHierarchyDepth = getHierarchyDepth(funcEc);
                    if (currentHierarchyDepth > maxHierarchyDepth) {
                        maxHierarchyDepth = currentHierarchyDepth;
                    }
                }

                ExtendedFileChooser fileChooser = new ExtendedFileChooser(B1ExportPlugin.class);
                FileFilter filter = new FileNameExtensionFilter("CSV-Dateien (*.csv, *.txt, *.prn)", "csv", "txt", "prn");
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setFileFilter(filter);
                fileChooser.setSelectedFile(lastSelectedFile);
                if (fileChooser.showSaveDialog(Static.getMainFrame()) != ExtendedFileChooser.APPROVE_OPTION) {
                    return;
                }
                lastSelectedFile = fileChooser.getSelectedFile();

                StringBuilder fullTextBuilder = new StringBuilder();
                fullTextBuilder.append(getTableHead(maxHierarchyDepth));
                //für alle Blätter im Aufgabenbaum
                for (ElementContainer leafFuncEc : absolutePartContainer) {
                    fullTextBuilder.append(getLine(leafFuncEc, maxHierarchyDepth));
                }

                InputStream is = new ByteArrayInputStream(fullTextBuilder.toString().getBytes());
                try {
                    FileHandler.writeFile(lastSelectedFile, is);
                } catch (IOException e1) {
                    ExtendedFileChooser.showSaveErrorMessage(Static.getMainFrame());
                }

            }

            @Override
            public boolean isEnabled() {
                return Static.getSelectedDoc() != null;
            }
        };
    }

    /**
     * Schreibt die Zeile für die übergebene Aufgabe, die eine Blattaufgabe im Hierarchiebaum sein sollte
     *
     * @param functionLeafEc
     */
    private static final String getLine(final ElementContainer leafFuncEc, final int maxHierarchyDepth) {
        ModelElement func = leafFuncEc.getElement();
        GraphDocument doc = leafFuncEc.getGraphDocument();
        //Liste von Listen mit den Parents der übergebenen Funktion. In der ersten Liste steht die übergebenen
        //Funktion, in der zweiten ihre direkten Parents, in der dritten deren Parents usw.
        ArrayList<ArrayList<ElementContainer>> parentLists = new ArrayList<ArrayList<ElementContainer>>();
        ArrayList<ElementContainer> parents = new ArrayList<ElementContainer>();
        parents.add(leafFuncEc);
        parentLists.add(parents);
        parents = func.getDirectParentContainer(doc);
        while (parents.size() > 0) {
            parentLists.add(parents);
            ArrayList<ElementContainer> newParents = new ArrayList<ElementContainer>();
            for (ElementContainer parent : parents) {
                newParents.addAll(parent.getElement().getDirectParentContainer(doc));
            }
            parents = newParents;
        }

        //einrücken
        StringBuilder linePartBuilder = new StringBuilder();
        appendEmptyColumns(linePartBuilder, (maxHierarchyDepth - parentLists.size()) * 2, false);

        //Alle Parents und die ebenflass in der Liste stehende Blattaufgaben jeweils in eine Spalte schreiben. Falls
        //ein Element mehrere Parents hat, dann werden diese durch Komma getrennt in dieselbe Spalte geschrieben.
        for (int i = parentLists.size() - 1; i >= 0; i--) {
            parents = parentLists.get(i);
            Alphabetical.sort(parents);
            linePartBuilder.append(GraphDocument.getParseSaveString(getHashListColumn(parents), true));
            linePartBuilder.append("\t");
            linePartBuilder.append(GraphDocument.getParseSaveString(getNamesListColumn(parents), true));
            linePartBuilder.append("\t");
        }
        //Beschreibung der übergebenen Funktion anhängen
        linePartBuilder.append(GraphDocument.getParseSaveString(func.getDescription(), true));
        linePartBuilder.append("\t");

        //Ort der Durchführung des Prozesses ist ein UserField namens 'Räume'
        for (UserField uf : func.getUserFieldInputValueKeys()) {
            if (uf.getName().equals("Raum")) {
                linePartBuilder.append(GraphDocument.getParseSaveString(func.getUserFieldInputValue(uf), true));
                break;
            }
        }
        //wenn kein solches Userfield gefunden wurde, steht immer noch der TAB aus der letzten Spalte vorne -> ein Leerzeichen anhängen
        if (linePartBuilder.charAt(linePartBuilder.length() - 1) == '\t') {
            linePartBuilder.append(GraphDocument.getParseSaveString(""));
        }
        linePartBuilder.append("\t");

        //für jede AufOrgKombination bzw. damit verknüpfe OEs muss eine eigene Zeile generiert werden
        ArrayList<ElementContainer> aufOrgCombis = func.getConnectedContainer(AufOrgKombination.class, doc);
        //wenn gar keine OEs verknüpft sind -> einfach nur die Aufgabenhierarchie zurück geben.
        if (aufOrgCombis.size() == 0) {
            appendNoOeConnected(linePartBuilder);
            return linePartBuilder.toString();
        }

        //das ist der Anfang jeder Zeile (also alle Aufgaben bis hin zur Blattaufgabe mit deren Beschreibung und dem Ort der Prozessdurchführung)
        String lineStart = linePartBuilder.toString();

        //falls mehrere OEs mit mehreren AWBs für die Ausgangsaufgabe verknüpft sind, müssen mehrere Zeilen erzeugt werden,
        //die alle mit dem lineStart beginnen. Am Ende stehen diese gleich beginnenden Zeilen in fullLineBuilder
        StringBuilder fullLinesBuilder = new StringBuilder();

        for (ElementContainer aufOrgComb : aufOrgCombis) {
            ArrayList<ElementContainer> oes = aufOrgComb.getElement().getConnectedContainer(Organisationseinheit.class, doc);
            //an der AufOrgKombi hängen keine OEs -> Zeile ist zu Ende
            if (oes.size() == 0) {
                fullLinesBuilder.append(lineStart);
                //aktuelle Zeile mit Leerspalten auffüllen und abschließen
                appendNoOeConnected(fullLinesBuilder);
                continue;
            }

            //jede einzelne Zeile beginnt mit dem lineStart
            linePartBuilder.setLength(0);
            linePartBuilder.append(lineStart);

            Alphabetical.sort(oes);
            linePartBuilder.append(GraphDocument.getParseSaveString(getNamesListColumn(oes), true));
            linePartBuilder.append("\t");

            //ACHTUNG: es werden einfach alle Konfigurationen ausgeblendet, also einfach nur alle verknüpften AWBs eingesammelt,
            //egal ob sie in einer oder mehreren Konfigurationen stecken. Sonst müsste man für jede Konfig eine weitere Zeile anlegen
            ArrayList<ElementContainer> awbs = new ArrayList<ElementContainer>();
            for (ElementContainer awbKonf : aufOrgComb.getElement().getConnectedContainer(ABKonfiguration.class, doc)) {
                awbs.addAll(awbKonf.getElement().getConnectedContainer(Anwendungsbaustein.class, doc));
            }

            //Kein AWB vorhanden -> aktuelle Zeile für diese OEs abschließen
            if (awbs.size() == 0) {
                linePartBuilder.append(GraphDocument.getParseSaveString("Kein Anwendungssystem verknüpft"));
                linePartBuilder.append("\t");
                //aktuelle Zeile mit Leerspalten auffüllen und abschließen
                appendEmptyColumns(linePartBuilder, 2, true);
                fullLinesBuilder.append(linePartBuilder);
                continue;
            }

            //jetzt die AWBnamen kommasepariert eimntragen
            linePartBuilder.append(GraphDocument.getParseSaveString(getNamesListColumn(awbs)));
            linePartBuilder.append("\t");

            //Physische DV-Baustein-Konfigs holen. ACHTUNG: Auch hier werden einfach alle Konfigurationen in einen
            //Topf geworfen, also alle PDVB
            ArrayList<ElementContainer> pdvbKonfigs = new ArrayList<ElementContainer>();
            for (ElementContainer awb : awbs) {
                pdvbKonfigs.addAll(awb.getElement().getConnectedContainer(DBKonfiguration.class, doc));
            }

            ArrayList<ElementContainer> pdvbs = new ArrayList<ElementContainer>();
            for (ElementContainer pdvbKonf : pdvbKonfigs) {
                pdvbs.addAll(pdvbKonf.getElement().getConnectedContainer(PhysischerDVBaustein.class, doc));
            }

            if (pdvbs.size() == 0) {
                linePartBuilder.append(GraphDocument.getParseSaveString("Keine Physischen DV-Bausteine verknüpft"));
                linePartBuilder.append("\t");
                //aktuelle Zeile mit Leerspalten auffüllen und abschließen
                appendEmptyColumns(linePartBuilder, 1, true);
                fullLinesBuilder.append(linePartBuilder);
                continue;
            }

            //jetzt die PDVBnamen kommasepariert eimntragen
            linePartBuilder.append(GraphDocument.getParseSaveString(getNamesListColumn(pdvbs)));
            linePartBuilder.append("\t");

            //Speicherplatz ist noch nicht gekärt -> einfach Leer setzen
            appendEmptyColumns(linePartBuilder, 1, true);
            fullLinesBuilder.append(linePartBuilder);

        }

        return fullLinesBuilder.toString();
    }

    /**
     * @param sb
     * @param count
     */
    private static final void appendEmptyColumns(final StringBuilder sb, final int count, final boolean lineEnd) {
        for (int i = 0; i < count; i++) {
            sb.append(GraphDocument.getParseSaveString(""));
            sb.append("\t");
        }
        if (lineEnd) {
            sb.append("\r\n");
        }
    }

    /**
     * Fügt dem übergebenen {@link StringBuilder} die Info an, dass keine Organisationseinheit
     * mit der Aufgabe aus dieser Zeile verknüpft ist und füllt die hinteren Spalten mit Leerspalten
     * und schließt die Gesamtzeile mit einem Zeilenumbruch ab.
     *
     * @param sb
     */
    private static final void appendNoOeConnected(final StringBuilder sb) {
        sb.append(GraphDocument.getParseSaveString("Keine Organisationseinheit verknüpft"));
        sb.append("\t");
        appendEmptyColumns(sb, 3, true);
    }

    /**
     * @param containerList
     * @return
     */
    private static final String getNamesListColumn(final ArrayList<ElementContainer> containerList) {
        return getStringListColumn(containerList, false);
    }

    /**
     * @param containerList
     * @return
     */
    private static final String getHashListColumn(final ArrayList<ElementContainer> containerList) {
        return getStringListColumn(containerList, true);
    }

    /**
     * @param containerList
     * @param hash
     * @return
     */
    private static final String getStringListColumn(final ArrayList<ElementContainer> containerList, final boolean hash) {
        StringBuilder sb = new StringBuilder();
        for (ElementContainer ec : containerList) {
            if (hash) {
                sb.append(ec.getHashString());
            } else {
                sb.append(ec.getElement().getClearName());
            }
            sb.append(", ");
        }
        //letztes Komma löschen
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    /**
     * @param sb
     */
    private static final void appendNewLine(final StringBuilder sb) {
        sb.append("\r\n");
    }

    /**
     * Gibt den Tabellenkopf aus
     *
     * @param hierarchyDepth
     *            Tiefe der Aufgabenhierarchie
     */
    private static final String getTableHead(final int hierarchyDepth) {
        StringBuilder sb = new StringBuilder();
        for (int i = hierarchyDepth - 1; i > 0; i--) {
            sb.append("Aufgabe_ID (");
            sb.append(i);
            sb.append(")\t");
            sb.append("Aufgabe (");
            sb.append(i);
            sb.append(")\t");
        }
        sb.append("Teilaufgabe_ID\t");
        sb.append("Teilaufgabe\t");
        sb.append("Beschreibung\t");
        sb.append("Ort der Durchführung\t");
        sb.append("Verantwortliche Rolle\t");
        sb.append("Anwendungssystem\t");
        sb.append("Physischer DV-Baustein\t");
        sb.append("Benötiger Speicherplatz\t");
        appendNewLine(sb);
        return sb.toString();
    }

    /**
     * Liefert die Anzahl an mit Teil-Von-Beziehungen übergeordneten Elementen im selben {@link GraphDocument} des
     * übergebenen Containers.
     *
     * @param ec
     * @return
     */
    public int getHierarchyDepth(final ElementContainer ec) {
        GraphDocument doc = ec.getGraphDocument();
        HashSet<ElementContainer> nextStepStartContainer = new HashSet<ElementContainer>();
        HashSet<ElementContainer> resultContainer = new HashSet<ElementContainer>();
        nextStepStartContainer.add(ec);
        int hierarchy = 1;
        while (true) {
            resultContainer.clear();
            for (ElementContainer startEc : nextStepStartContainer) {
                resultContainer.addAll(startEc.getElement().getDirectParentContainer(doc));
            }
            if (resultContainer.size() == 0) {
                break;
            }
            hierarchy++;
            nextStepStartContainer.clear();
            nextStepStartContainer.addAll(resultContainer);
        }
        return hierarchy;
    }

}

package de.imise.tool3lgm.plugin;

import static de.imise.tool3lgm.graphtools.elements.Edge.ANY;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.DBKonfiguration;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;
import de.imise.util.Alphabetical;
import de.imise.util.io.FileHandler;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * Exportiert eine Tabelle mit den Spalten
 * "Anwendungssystem", "Physische Datenverabeitungsbausteine", "Anzahl"
 * Für jedes Anwendungssystem steht in der ersten Spalte der Name. Alle mit diesem Anwendungssystem verknüpften physischen
 * Datenverabeitungsbausteine stehen dann untereinander in der 2 Spalte. Nach der letzten Zeile in der physischen
 * Datenverabeitungssystemspalte steht in der ersten Spalte das nächste Anwendungssystem usw. Die letzte Spalte enthält immer
 * die Anzahl der mit dem jeweiligen Anwendungssystem verknüpften phys. Datenverarbeitungsbausteine.
 * Die dargstellten Anwendungssysteme kommen aus dem aktuell selketierten Teilmodell, die verbundenen Phys.
 * Datenverabeitungsbausteine werden im Gesamtmodell gesucht.
 *
 * @author AXS
 * @create 07.09.2012
 */
public class ExportPdvb4AwbPlugin implements Plugin {

    /** Datei, in die als letztes exportiert wurde */
    private File lastSelectedFile = null;

    /**
     * COMMENTME
     */
    private final boolean german = Locale.getDefault().getCountry() == Locale.GERMAN.getCountry();

    @Override
    public Action getAction() {
        return new AbstractAction(german ? "CSV-Export Anwendungssysteme -> Phys. Datenverabeitungsbausteine" : "CSV Export Application Systems -> Phys. Data Processing Components") {

            @Override
            public void actionPerformed(final ActionEvent e) {
                GraphDocument doc = Static.getSelectedDoc();
                if (doc == null) {
                    return;
                }

                ExtendedFileChooser fileChooser = new ExtendedFileChooser(ExportPdvb4AwbPlugin.class);
                FileFilter filter = new FileNameExtensionFilter("CSV-Dateien (*.csv, *.txt, *.prn)", "csv", "txt", "prn");
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setFileFilter(filter);
                fileChooser.setSelectedFile(lastSelectedFile);
                if (fileChooser.showSaveDialog(Static.getMainFrame()) != ExtendedFileChooser.APPROVE_OPTION) {
                    return;
                }
                lastSelectedFile = fileChooser.getSelectedFile();

                StringBuilder fullTextBuilder = new StringBuilder();
                fullTextBuilder.append(getTableHead());
                //für alle Anwendungssysteme
                for (ModelElement applicationSystem : doc.getModelItems(Anwendungsbaustein.class, true, true)) {
                    addEntry(applicationSystem, fullTextBuilder);
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
     * Erzeugt den gesamten Eintrag für ein einzelnes Anwendungssystem.
     *
     * @param applicationSystem
     *            Anwendungssystem, für das alle Datenverabeitungsbausteine angehängt werden sollen.
     * @param entryBuilder
     * @return
     */
    private static final String addEntry(final ModelElement applicationSystem, final StringBuilder entryBuilder) {
        for (ModelElement pdvbKonf : applicationSystem.getConnectedElements(DBKonfiguration.class, PdvbkAwbVerbindung.class, ANY, true)) {

            //Alle verbundenen Phys. Datenbverarbeitungsbausteine holen und davon alle absoluten Parts
            List<ModelElement> pdvbList = pdvbKonf.getConnectedElements(PhysischerDVBaustein.class, PdvbPdvbkVerbindung.class, ANY, true);
            Set<ModelElement> absolutePartPdvbs = new HashSet<>();
            for (int i = 0; i < pdvbList.size(); i++) {
                ModelElement pdvb = pdvbList.get(i);
                Set<ModelElement> absoluteParts = pdvb.getAbsolutePartElements();
                if (absoluteParts.size() > 0) {
                    absolutePartPdvbs.addAll(absoluteParts);
                } else {
                    absolutePartPdvbs.add(pdvb);
                }
            }
            pdvbList.clear();
            pdvbList.addAll(absolutePartPdvbs);
            Alphabetical.sort(pdvbList);

            //erste Zeile jeweils den Namen des AWB, den ersten Pdvb (wenn vorhanden) und die Anzahl der vebundenen Pdvb
            entryBuilder.append(GraphDocument.getParseSaveString(applicationSystem.getClearName(), true));
            entryBuilder.append("\t");
            entryBuilder.append(GraphDocument.getParseSaveString(pdvbList.size() > 0 ? pdvbList.get(0).getClearName() : "", true));
            entryBuilder.append("\t");
            entryBuilder.append(GraphDocument.getParseSaveString(new Integer(pdvbList.size()).toString()));
            entryBuilder.append("\t");
            appendNewLine(entryBuilder);
            if (pdvbList.size() > 0) {
                pdvbList.remove(0);
            }
            //alle anderen Spalten nur den aktuellen Pdvb eintragen
            for (ModelElement pdvb : pdvbList) {
                entryBuilder.append(GraphDocument.getParseSaveString(""));
                entryBuilder.append("\t");
                entryBuilder.append(GraphDocument.getParseSaveString(pdvb.getClearName(), true));
                entryBuilder.append("\t");
                entryBuilder.append(GraphDocument.getParseSaveString(""));
                entryBuilder.append("\t");
                appendNewLine(entryBuilder);
            }
        }
        return entryBuilder.toString();
    }

    /**
     * @param sb
     */
    private static final void appendNewLine(final StringBuilder sb) {
        sb.append("\r\n");
    }

    /**
     * Gibt den Tabellenkopf aus
     */
    private String getTableHead() {
        StringBuilder sb = new StringBuilder();
        sb.append("Anwendungsbaustein\t");
        sb.append("Phys. Datenverabeitungsbaustein\t");
        sb.append(german ? "Anzahl\t" : "Count\t");
        appendNewLine(sb);
        return sb.toString();
    }

}

package de.imise.tool3lgm.metamodel.original.action;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.metamodel.original.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.metamodel.original.node.Anwendungsprogramm;
import de.imise.tool3lgm.metamodel.original.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Softwareprodukt;
import de.imise.util.io.FileHandler;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * Exportiert eine Tabelle mit den Spalten "Anwendungssystem", "Physische
 * Datenverabeitungsbausteine", "Anzahl" Für jedes Anwendungssystem steht in der
 * ersten Spalte der Name. Alle mit diesem Anwendungssystem verknüpften
 * physischen Datenverabeitungsbausteine stehen dann untereinander in der 2
 * Spalte. Nach der letzten Zeile in der physischen
 * Datenverabeitungssystemspalte steht in der ersten Spalte das nächste
 * Anwendungssystem usw. Die letzte Spalte enthält immer die Anzahl der mit dem
 * jeweiligen Anwendungssystem verknüpften phys. Datenverarbeitungsbausteine.
 * Die dargstellten Anwendungssysteme kommen aus dem aktuell selketierten
 * Teilmodell, die verbundenen Phys. Datenverabeitungsbausteine werden im
 * Gesamtmodell gesucht.
 *
 * @author AXS
 * @create 07.09.2012
 */
public class ExportApplicationSystems {

    /** Datei, in die als letztes exportiert wurde */
    private File lastSelectedFile = null;

    private static final JCheckBox skipApplicationSystemsWithoutSoftwareProduct = new JCheckBox();

    /**
     * COMMENTME
     */
    private final boolean german = Locale.getDefault().getCountry() == Locale.GERMAN.getCountry();

    @SuppressWarnings("serial")
    public Action getAction() {
        return new AbstractAction(german ? "CSV-Export Anwendungssysteme -> Softwareprodukte" : "CSV Export Application Systems -> Software Products") {

            @Override
            public void actionPerformed(final ActionEvent e) {
                GDCollection gdcoll = Static.getSelectedGDCollection();
                if (gdcoll == null) {
                    return;
                }

                ExtendedFileChooser fileChooser = new ExtendedFileChooser(ExportApplicationSystems.class);
                FileFilter filter = new FileNameExtensionFilter("CSV-Dateien (*.csv, *.txt, *.prn)", "csv", "txt", "prn");
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setFileFilter(filter);
                fileChooser.setSelectedFile(lastSelectedFile);
                skipApplicationSystemsWithoutSoftwareProduct.setText(german ? "Überspringe Anwendungssysteme ohne Software Produkt" : "Skip Application Systems without Software Product");
                fileChooser.setAccessory(skipApplicationSystemsWithoutSoftwareProduct);

                if (fileChooser.showSaveDialog(Static.getMainFrame()) != ExtendedFileChooser.APPROVE_OPTION) {
                    return;
                }
                lastSelectedFile = fileChooser.getSelectedFile();

                StringBuilder fullTextBuilder = new StringBuilder();
                fullTextBuilder.append(getTableHead());
                for (GraphDocument doc : gdcoll.getSzenarios()) {
                    addDocEntry(doc, fullTextBuilder);
                    //für alle Anwendungssysteme
                    for (ModelElement applicationSystem : doc.getModelItems(RechAnwendungsbaustein.class, true, true)) {
                        addEntry(applicationSystem, fullTextBuilder);
                    }
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

    private static final void addDocEntry(GraphDocument doc, final StringBuilder entryBuilder) {
        entryBuilder.append(doc.getName().trim());
        entryBuilder.append("\t");
        entryBuilder.append("");
        entryBuilder.append("\t");
        entryBuilder.append("");
        appendNewLine(entryBuilder);
    }

    /**
     * Erzeugt den gesamten Eintrag für ein einzelnes Anwendungssystem.
     *
     * @param applicationSystem Anwendungssystem, für das alle
     *            Datenverabeitungsbausteine angehängt werden sollen.
     * @param entryBuilder
     * @return
     */
    private static final void addEntry(final ModelElement applicationSystem, final StringBuilder entryBuilder) {
        for (ModelElement applicationSystemProgram : applicationSystem.getConnectedElements(Anwendungsprogramm.class, RawbAwpVerbindung.class, null, true)) {
            List<ModelElement> softwareProducts = applicationSystemProgram.getConnectedElements(Softwareprodukt.class, AwpSwpVerbindung.class, null, true);
            if (softwareProducts.isEmpty()) {
                if (!skipApplicationSystemsWithoutSoftwareProduct.isSelected()) {
                    entryBuilder.append("");
                    entryBuilder.append("\t");
                    entryBuilder.append(applicationSystem.getClearName().trim());
                    entryBuilder.append("\t");
                    appendNewLine(entryBuilder);
                }
            } else {
                for (ModelElement softwareProduct : softwareProducts) {
                    entryBuilder.append("");
                    entryBuilder.append("\t");
                    entryBuilder.append(applicationSystem.getClearName().trim());
                    entryBuilder.append("\t");
                    entryBuilder.append(softwareProduct.getClearName().trim());
                    appendNewLine(entryBuilder);
                }
            }
        }
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
        sb.append(german ? "Teilmodell" : "Submodel");
        sb.append("\t");
        sb.append(german ? "Anwendungsbaustein" : "Application System");
        sb.append("\t");
        sb.append(german ? "Softwareprodukt" : "Softwareproduct");
        appendNewLine(sb);
        return sb.toString();
    }

}

/*
 * Created on 21.04.2005
 */
package de.imise.tool3lgm.imexport.csv;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFileChooser;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.log.Log;
import de.imise.util.Alphabetical;
import de.imise.util.swing.dialog.ExtendedFileChooser;

/**
 * @author thomas
 */
public class DataExportModule {

    //@SuppressWarnings("unchecked")
    public static final SimpleMetaPath[] metaPaths2Export = {
            //Über diese Angaben kann man abweichend vom Standardexport verbundene Elemente ebenfalls exportieren.
            //Das macht aber Probleme in dem Fall, dass man denselben Datensatz wieder importieren möchte, da man nun anhand des Namens
            //eines über einen Pfad verbundenen Elementes auch noch die Zwischenelemente rekonstruieren müsste. Daher einfach alle
            //Elemente ohne diesen Schnickschnack exportieren, dann kann man das über einen sehr simplen Import auch wieder rein bekommen.
            //Diese Art des Exports und dazugehörigen Imports beachtet keinerlei Kanten! D.h. im Export stecken überhaupt keine Verbindungsinformationen!

            //        new SimpleMetaPath(RechAnwendungsbaustein.class, Softwareprodukt.class, RawbAwpVerbindung.class, AwpSwpVerbindung.class)

    };

    public static void exportData(final GraphDocument doc) {
        GDCollection gdcoll = doc.getCollection();
        File docFile = gdcoll.getFile();
        File docDir = docFile == null ? null : docFile.getParentFile();
        String exportFileName = gdcoll.getTitle() + "_-_" + doc.getTitle().replace(' ', '_') + ".csv";
        ExtendedFileChooser saveDialog = new ExtendedFileChooser(FileFilterType.CSV, docDir, exportFileName);
        saveDialog.setMultiSelectionEnabled(false);
        saveDialog.setFileFilters(true, Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.CSV));
        if (saveDialog.showSaveDialog(Static.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            exportData(doc, saveDialog.getSelectedFile());
        }

    }

    private static void exportData(final GraphDocument doc, final File exportFile) {
        Class<? extends ModelElement> classElement = null;
        try {
            FileOutputStream ostream = new FileOutputStream(exportFile, false);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ostream));
            String caption = "";

            UserFieldDefinitions ufDef = doc.getCollection().getUserFieldDefinitions();
            //Elemente selektiert -> nur die selekierten exportieren
            List<ModelElement> elements = doc.getSelectedElements();
            //wenn nichts selektiert war -> alles exportieren
            if (elements.size() == 0) {
                elements = doc.getModelItems(ModelElement.class, true, true);
            }

            MetaModel metaModel = doc.getMetaModel();

            //Alle Elementklassen in der Reihenfolge zusammen sammeln, in der sie exportiert werden sollen (erst
            //alphabetisch alle Knotenklassen, dann alle Kantenklassen
            ArrayList<Class<? extends ModelElement>> elementClasses = new ArrayList<>();
            elementClasses.addAll(metaModel.allNodesSet);
            Alphabetical.sort(elementClasses);
            ArrayList<Class<? extends ModelElement>> edgeClasses = new ArrayList<>();
            edgeClasses.addAll(metaModel.allEdgesSet);
            //Kommunikationsbeziehungen sind Node und Kanten -> einfach alle Kanten von den Node abziehen, damit die nicht 2 mal drin sind
            elementClasses.removeAll(edgeClasses);
            Alphabetical.sort(edgeClasses);
            elementClasses.addAll(edgeClasses);

            //alle diese Elementklassen durchlaufen und die jeweilgen Elemente dieser Klasse exportieren
            for (Class<? extends ModelElement> elementClass : elementClasses) {
                for (ModelElement me : elements) {
                    //aktuelles Element ist nicht von der aktuellen Klasse -> nächstes Element
                    if (me.getClass() != elementClass) {
                        continue;
                    }
                    if (elementClass != classElement) {
                        classElement = elementClass;
                        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
                        String displayableClassName = CoreMetaModel.isEdgeType(elementClass) ? elementsNameBuilder.getFullForwardMetaAssociationName(elementClass.asSubclass(Edge.class)) : elementsNameBuilder.getDisplayableName(elementClass);
                        caption = displayableClassName + "\tName\tDescription\tHashString";
                        for (SimpleMetaPath metaPath : metaPaths2Export) {
                            if (metaPath.getStartClass().isAssignableFrom(elementClass)) {
                                caption += "\t" + elementsNameBuilder.getDisplayableName(metaPath.getEndClass());
                            }
                        }
                        for (UserField uf : ufDef.getUserFields(classElement)) {
                            caption += "\t" + uf.getName().replaceAll("\t", "\\\\t");
                        }
                        writer.write(caption);
                        writer.newLine();
                    }
                    StringBuilder lineBuf = new StringBuilder("\t");
                    String v = me.getName();
                    if (v == null || v.equals("")) {
                        v = "\"\"";
                    }
                    lineBuf.append(v.replaceAll("\t", "\\\\t"));
                    v = me.getDescription();
                    if (v == null || v.equals("")) {
                        v = "\"\"";
                    }
                    lineBuf.append("\t" + v.replaceAll("\t", "\\\\t"));
                    v = me.getHashString();
                    if (v == null || v.equals("")) {
                        v = "\"\"";
                    }
                    lineBuf.append("\t" + v.replaceAll("\t", "\\\\t"));

                    v = "";
                    for (SimpleMetaPath metaPath : metaPaths2Export) {
                        if (metaPath.getStartClass().isAssignableFrom(elementClass)) {
                            Collection<ModelElement> connected = metaPath.getConnectedElements(me);
                            for (ModelElement con : connected) {
                                v += con.getName() + ", ";
                            }
                            if (connected.size() > 0) {
                                v = v.substring(0, v.length() - 2);
                            }
                            lineBuf.append("\t" + v.replaceAll("\t", "\\\\t"));
                        }
                    }

                    for (UserField uf : ufDef.getUserFields(classElement)) {
                        String value = "";
                        v = uf.getFormattedValue(me);
                        if (v != null && !v.equals("") && !v.equals(UserField.EMPTY_STRING)) {
                            value += v;
                        } else {
                            value = "\"\"";
                        }
                        lineBuf.append("\t" + value.replaceAll("\t", "\\\\t"));
                    }
                    String line = lineBuf.toString().replaceAll("\n", "\\\\n");
                    writer.write(line);
                    writer.newLine();
                }
            }

            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), ex);
            return;
        }

    }

}

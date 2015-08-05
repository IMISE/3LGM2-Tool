/*
 * Created on 21.04.2005
 */
package de.imise.tool3lgm.imexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.path.PathFinder;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.log.Log;
import de.imise.util.Alphabetical;

/**
 * @author thomas
 */
public class DataExportModule {

    //@SuppressWarnings("unchecked")
    public static final MetaPath[] metaPaths2Export = {
    //Über diese Angaben kann man abweichend vom Standardexport verbundene Elemente ebenfalls exportieren.
    //Das macht aber Probleme in dem Fall, dass man denselben Datensatz wieder importieren möchte, da man nun anhand des Namens
    //eines über einen Pfad verbundenen Elementes auch noch die Zwischenelemente rekonrtuieren müsste. Daher einfach alle
    //Elemente ohne diesen Schnickschnack exportieren, dann kann man das über einen sehr simplen Import auch wieder rein bekommen.
    //Diese Art des Exports und dazugehörigen Imports beachtet keinerlei Kanten! D.h. im Export stecken überhaupt keine Verbindungsinformationen!

    //        new MetaPath(RechAnwendungsbaustein.class, Softwareprodukt.class, RawbAwpVerbindung.class, AwpSwpVerbindung.class)

    };

    public static void exportData(final LGMGraphDocument doc, final File exportFile) {
        Class<? extends ModelElement> classElement = null;
        try {
            FileOutputStream ostream = new FileOutputStream(exportFile, false);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ostream));
            String caption = "";

            UserFieldDefinitions ufDef = doc.getCollection().getUserFieldDefinitions();
            //Elemente selektiert -> nur die selekierten exportieren
            ArrayList<ModelElement> elements = doc.getSelectedElements();
            //wenn nichts selektiert war -> alles exportieren
            if (elements.size() == 0) {
                elements = doc.getModelItems(ModelElement.class, true, true);
            }

            //Alle Elementklassen in der Reihenfolge zusammen sammeln, in der sie exportiert werden sollen (erst
            //alphabetisch alle Knotenklassen, dann alle Kantenklassen
            ArrayList<Class<? extends ModelElement>> elementClasses = new ArrayList<Class<? extends ModelElement>>();
            elementClasses.addAll(ModelConstants.ALL_NODES_SET);
            Alphabetical.sort(elementClasses);
            ArrayList<Class<? extends ModelElement>> edgeClasses = new ArrayList<Class<? extends ModelElement>>();
            edgeClasses.addAll(ModelConstants.ALL_EDGES_SET);
            //Kommunikationsbeziehungen sind Knoten und Kanten -> einfach alle Kanten von den Knoten abziehen, damit die nicht 2 mal drin sind
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
                        String displayableClassName = ModelConstants.isEdgeType(elementClass) ? ModelConstants.getFullForwardMetaAssociationName(elementClass.asSubclass(Kante.class)) : ModelConstants.getDisplayableName(elementClass);
                        caption = displayableClassName + "\tName\tDescription\tHashString";
                        for (MetaPath metaPath : metaPaths2Export) {
                            if (metaPath.getStartClass().isAssignableFrom(elementClass)) {
                                caption += "\t" + ModelConstants.getDisplayableName(metaPath.getEndClass());
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
                    for (MetaPath metaPath : metaPaths2Export) {
                        if (metaPath.getStartClass().isAssignableFrom(elementClass)) {
                            HashSet<ModelElement> connected = PathFinder.getDirectConnectedElements(me, metaPath, doc.getCollection());
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
                        v = uf.getFormatedValue(me);
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
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), ex);
            return;
        }

    }

}

package de.imise.tool3lgm.graphtools.analyse.context;

import static de.imise.tool3lgm.Tool3lgmConstants.ANALYSEN_FILE_NAME;
import static de.imise.tool3lgm.Tool3lgmConstants.USER_HOME_3LGM_DIR;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import org.xml.sax.SAXException;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.AnalysesDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.log.Log;
import de.imise.util.io.FileHandler;

/**
 * Diese Klasse stellt Methoden zum speichern und laden der Analysen bereit.
 * Hier werden alle Analysen die im 3LGM-Baukasten verwendet werden, verwaltet.
 * Achtung: Es existiert noch kein Listener-Mechanismus, der Klassen, welche das
 * AnalysesRepository verwenden, über Änderungen im Repository informiert.
 *
 * @author Thomas Wendt, Sebastian Weber, AXS
 */
public class AnalysesRepository {

    /**
     * Datei, aus der zuletzt Analysen geladen oder in die zuletzt Analysen
     * gespeichert wurden
     */
    private static File file;

    /**
     * Enthält alle Analysen, die im Modell audf Elemente angewendet werden
     * können.
     */
    private static List<XMLAnalysis> xmlAnalyses;

    /**
     * Fügt eine neue XMLAnalyse ins Repository ein, wenn sie noch nicht
     * enthalten ist.
     *
     * @param xmlAnalyses Liste zu der die übergebene XMLAnalyse hinzugefügt
     *            werden soll
     * @param toadd die XMLAnalyse, die hinzugefügt werden soll
     */
    public static boolean addAnalysis(final XMLAnalysis toadd) {
        if (toadd == null) {
            return false;
        }
        if (xmlAnalyses == null) {
            xmlAnalyses = new ArrayList<>();
        }
        if (xmlAnalyses.contains(toadd)) {
            return false;
        }
        if (toadd.startClasses == null || toadd.startClasses.isEmpty()) {
            return false;
        }
        xmlAnalyses.add(toadd);
        return true;
    }

    /**
     * Prüft, ob der übergebene <code>name</code> bei irgend einer anderen als
     * der einzeln übergebenen XMLAnalyse vorkommt.
     *
     * @param xmlAnalyses eine ArrayList der Analysen.
     * @param xMLAnalysis die XMLAnalyse, der ein neuer Name gegeben werden
     *            soll.
     * @param name der Name für die übergebene XMLAnalyse.
     * @return false, wenn der Name in der Liste der Analysen gar nicht
     *         enthalten ist oder nur die einzeln übergebnen XMLAnalyse diesen
     *         Namen besitzt.
     */
    public static boolean containsName(final List<XMLAnalysis> analyses, final XMLAnalysis xMLAnalysis, final String name) {
        if (analyses == null) {
            return false;
        }
        for (XMLAnalysis ana : analyses) {
            if (ana.getName().equals(name)) {
                if (xMLAnalysis != null && xMLAnalysis == ana) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt alle Analysen zurück, deren Startknoten dem übergebenen Node
     * entspricht.
     *
     * @param metaModel
     * @param elementClass
     * @return List, in der jeder Eintrag eine XMLAnalyse ist. Ist keine
     *         vorhanden, kommt eine leere Liste zurück, aber niemals
     *         <code>null</code>.
     */
    public static List<AbstractAnalysis> getAnalyses(final MetaModel metaModel, final Class<? extends ModelElement> elementClass) {
        List<AbstractAnalysis> analyses = new ArrayList<>();
        List<AbstractAnalysis> allAnalyses = new ArrayList<>(getXMLAnalyses());
        AnalysesDefinition analysesDefinition = metaModel.getAnalysesDefinition();
        List<AbstractAnalysis> nodeAnalyses = analysesDefinition.getNodeAnalyses();
        allAnalyses.addAll(nodeAnalyses);
        for (AbstractAnalysis ana : allAnalyses) {
            List<Class<? extends ModelElement>> startClasses = ana.getStartClasses();
            for (Class<? extends ModelElement> startClass : startClasses) {
                if (startClass != null && startClass.isAssignableFrom(elementClass)) {
                    analyses.add(ana);
                }
            }
        }
        return analyses;
    }

    /**
     * Liefert eine Analysedatei für den Benutzer. <br>
     * Ist bereits eine Datei geladen oder gespeichert worden, kommt diese
     * zurück. Sonst wird der Reihe nach folgendes geprüft:<br>
     * Hat der Benutzer in seinem Anwendungsdatenverzeichnis eine Analysedatei
     * mit dem Namen "Tool3lgm_Analys.res3" wird diese zurückgegeben. Sie sollte
     * immer beschreibbar sein.<br>
     * Ex. diese Datei nicht, dann wird im Installationsverzeichnis des
     * Bauskastens nach einer Datei mit dem Namen "Tool3lgm.analysis" gesucht.
     * Wenn diese beschreibbar ist, wird sie zurückgeliefert. Wenn sie nicht
     * beschreibbar ist, dann wird diese Datei ins Anwendungsdatenverzeichnis
     * des Benutzers mit den Namen "Tool3lgm_Analys.res3" kopiert und
     * zurückgegeben.<br>
     * Wenn auch im Installationsverzeichnis keine Analysendatei gefunden wurde,
     * wird aus den Resourcen die Standarddatei geladen. Zuerst wird versucht,
     * sie unter dem Namen "Tool3lgm.analysis" ins Installationsverzeichnis des
     * Baukastens zu kopieren. Geht das gut, wird sie zurückgegeben. Geht das
     * nicht gut, wird die Standarddatei ins Benutzerverzeichnis mit dem Namen
     * "Tool3lgm_LOCALECODE.analysis" kopiert und diese neue Datei
     * zurückgegeben.<br>
     * Geht das auch nicht gut, wird die Original Resourcendatei zurückgegeben.
     * Da sie in einem jar-Paket liegt, ist sie dann sicher nicht beschreibbar.
     * Der ganze Aufwand hängt damit zusammen, dass der Benutzer immer auf einer
     * Datei arbeitet, die er auch möglichst beschreiben kann. Das Speichern der
     * Analysendatei im Installationsverzeichnis hat den Vorteil, dass die
     * Analysen allen Benutzern dieser Installation zu Verfügung stehen.
     *
     * @return
     */
    public static File getRepositoryFile() {
        if (file != null) {
            return file;
        }
        file = new File(USER_HOME_3LGM_DIR, ANALYSEN_FILE_NAME);
        if (file.exists()) {
            if (FileHandler.getLine(file, "Model-Type: TLGMServiceMetaModel@2388259974838049670", true) == null) {
                List<XMLAnalysis> concatenatedAnalysis = concatXMLAnalyses();
                saveAnalyseFile(file, concatenatedAnalysis);
            }
            return file;
        } else {
            try {
                // wenn es die Datei nicht im Installpfad gibt und man sie auch nicht dahin
                // schreiben kann
                if (!file.createNewFile()) {
                    // lege eine Datei im Benutzerverzeichnis an
                    file = new File(USER_HOME_3LGM_DIR, ANALYSEN_FILE_NAME);
                }
            } catch (IOException e) {
                // das hier tritt ein, wenn es bei file.createNewFile() ne Exception gab
                file = new File(USER_HOME_3LGM_DIR, ANALYSEN_FILE_NAME);
            }

            // schreibe den Inhalt der Resourcendatei in file
            List<XMLAnalysis> concatenatedAnalysis = concatXMLAnalyses();
            saveAnalyseFile(file, concatenatedAnalysis);
        }
        return file;
    }

    /**
     * merges the analysis from the message based and service based meta models
     *
     * @return
     */
    public static List<XMLAnalysis> concatXMLAnalyses() {
        List<XMLAnalysis> originalAnalysis = loadAnalyseFile(Tool3lgmConstants.DEFAULT_ORIGINAL_ANALYSEN_RESSOURCE_URL);
        List<XMLAnalysis> serviceAnalysis = loadAnalyseFile(Tool3lgmConstants.DEFAULT_SERVICE_ANALYSEN_RESSOURCE_URL);
        List<XMLAnalysis> concatenatedAnalysis = new ArrayList<>(originalAnalysis);
        concatenatedAnalysis.addAll(serviceAnalysis);
        return concatenatedAnalysis;
    }
    /**
     * Gibt alle Abfragen zurück, die sich im Analyserepository befinden.
     *
     * @return eine ArrayList der Abfragen. Jeder Eintrag des ArrayList ist eine
     *         XMLAnalyse.
     */
    public static List<XMLAnalysis> getXMLAnalyses() {
        // Analyses
        File repositoryFile = getRepositoryFile();
        xmlAnalyses = loadAnalyseFile(repositoryFile);
        if (xmlAnalyses == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(xmlAnalyses);
    }

    // ////////////////////////////////////////////////////
    // Speichern, Laden und Import von XMLAnalyse-Dateien //
    // ////////////////////////////////////////////////////

    /**
     * Gibt eine Liste aller Analysen zurück, die sich in dem übergebenen File
     * befinden.
     *
     * @param file
     * @return
     */
    public static List<XMLAnalysis> loadAnalyseFile(final File file) {
        try {
            return loadAnalyseFile(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Gibt eine Liste aller Analysen zurück, die sich in der Datei mit der
     * übergebenen URL befinden.
     *
     * @param f
     * @return
     */
    public static List<XMLAnalysis> loadAnalyseFile(final URL url) {
        String line = "";
        List<XMLAnalysis> analysen = null;
        try {
            BufferedReader dataStream = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.ISO_8859_1));
            line = dataStream.readLine();
            if (!line.equals("Content-Type: multipart/related; boundary=--multipart_3lgm_query_separator;")) {
                throw new DataFormatException();
            }
            line = dataStream.readLine();
            if (!line.equals("--multipart_3lgm_query_separator")) {
                throw new DataFormatException();
            }
            analysen = new ArrayList<>();
            while (true) {
                line = dataStream.readLine();
                if (line == null) {
                    break;
                }
                if (!line.equals("Content-Type: text/xml")) {
                    throw new DataFormatException();
                }
                line = dataStream.readLine();
                //Model-Type ist die Metamodel-ID, die erst nach Version 3.4.0.4 eingeführt wurde und in den Modelldateien sowie den Analysen gespeichert wird
                String metaModelIDLinePrefix = "Model-Type: ";
                String metaModelId = null;
                MetaModel currentMetaModel = Static.getSelectedMetaModel();
                if (currentMetaModel == null) {
                    break;
                }
                MetaModelContext currentMetaModelContext = currentMetaModel.getMetaModelContext();
                String currentMetaModelID = currentMetaModel.getMetaModelID();
                if (line.startsWith(metaModelIDLinePrefix)) {
                    metaModelId = line.substring(metaModelIDLinePrefix.length()).trim();
                    line = dataStream.readLine();
                }
                String ananame = line.substring("Content-ID: ".length());
                StringBuilder strbuf = new StringBuilder();
                for (line = dataStream.readLine(); !line.equals("--multipart_3lgm_query_separator"); line = dataStream.readLine()) {
                    strbuf.append(line + "\n");
                }
                // this if statement makes sure, that only the relevant analyses are shown for the selected Meta-Model
                if (metaModelId != null && metaModelId.equals(currentMetaModelID)) {
                    XMLAnalysis toadd = null;
                    try {
                        toadd = XMLAnalysis.createAnalysis(currentMetaModelContext, ananame, strbuf.toString());
                    } catch (SAXException ex) {
                        Log.show(Log.ERROR, getResString("ANALYSIS_CANT_CREATE") + "\n" + ex.getMessage(), ex);
                    }
                    analysen.add(toadd);
                }
            }
            dataStream.close();
        } catch (Exception e) {
            Log.show(Log.ERROR, getResString("fehler"), e);
        }
        return analysen;

    }

    /**
     * Speichert die Analysen in die übergebene Analysedatei. Die übergebene
     * Datei muss nicht existieren, aber erzeugbar und beschreibbar sein.
     *
     * @param f die Datei, in die die Analysen gespeichert werden sollen.
     */
    public static void saveAnalyseFile(final File f, final List<XMLAnalysis> analysen) {
        if (file == null || analysen == null) {
            return;
        }
        try {
            if (f.exists()) {
                f.delete();
            }
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            raf.writeBytes("Content-Type: multipart/related; boundary=--multipart_3lgm_query_separator;\n");
            String line = "";
            for (int i = 0; i < analysen.size(); i++) {
                XMLAnalysis sp = analysen.get(i);
                line = null;
                if (sp != null) {
                    line = sp.getXMLText();
                }
                if (line != null) {
                    raf.writeBytes("--multipart_3lgm_query_separator\nContent-Type: text/xml\n");
                    raf.writeBytes("Model-Type: " + sp.getMetaModelID() + "\n");
                    raf.writeBytes("Content-ID: " + sp.getName() + "\n");
                    raf.writeBytes(line);
                    if (!line.endsWith("\n")) {
                        raf.writeBytes("\n");
                    }
                }
            }
            raf.writeBytes("--multipart_3lgm_query_separator");
            raf.close();
            file = f;
        } catch (Exception e) {
            Log.show(Log.INFO, getResString("analyse_speicherfehler"), e);
        }
    }

    /**
     * Speichert die aktuelle Liste <code>xmlAnalysen</code> als Repository.
     */
    public static final void saveRepository() {
        saveAnalyseFile(getRepositoryFile(), xmlAnalyses);
    }

    /**
     * Übernimmt die übergebenen Abfragen in das Repository. Der aktuelle Inhalt
     * des Repository wird dadurch überschrieben.
     *
     * @param newAbfragen eine ArrayList der neuen Abfragen. Jeder Eintrag des
     *            ArrayList ist eine XMLAnalyse.
     */
    public static boolean setXMLAnalysen(final List<XMLAnalysis> newXMLAnalysen) {
        if (newXMLAnalysen == null) {
            return false;
        }
        xmlAnalyses = newXMLAnalysen;
        return true;
    }

}
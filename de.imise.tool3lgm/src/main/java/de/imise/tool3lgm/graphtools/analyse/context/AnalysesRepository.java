package de.imise.tool3lgm.graphtools.analyse.context;

import static de.imise.tool3lgm.Tool3lgmConstants.USER_HOME_ANALYSES_FILE;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.Tool3lgmMetaModelContext.getDefaultMetaModelContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.DataFormatException;

import org.xml.sax.SAXException;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmMetaModelContext;
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
     * @return
     */
    private static List<XMLAnalysis> getAnalyses() {
        if (xmlAnalyses == null) {
            loadAnalysesFromUserHomeFile();
        }
        return xmlAnalyses;
    }

    /**
     * @param id
     * @return the analysis with the given id or <code>null</code> if no
     *         analysis exists
     */
    public static XMLAnalysis getAnalysis(final String id) {
        for (XMLAnalysis ana : getAnalyses()) {
            if (ana.hasID(id)) {
                return ana;
            }
        }
        return null;
    }

    /**
     * @param metaModelContext
     * @return the analyses for the given metamodel
     */
    public static List<XMLAnalysis> getAnalyses(final MetaModelContext metaModelContext) {
        List<XMLAnalysis> analyses = new ArrayList<>();
        for (XMLAnalysis ana : getAnalyses()) {
            if (ana.hasMetaModelContext(metaModelContext)) {
                analyses.add(ana);
            }
        }
        return analyses;
    }

    /**
     * Gibt alle Analysen zurück, deren Startknoten dem übergebenen Node
     * entspricht.
     *
     * @param metaModelContext
     * @param elementClass
     * @return List, in der jeder Eintrag eine XMLAnalyse ist. Ist keine
     *         vorhanden, kommt eine leere Liste zurück, aber niemals
     *         <code>null</code>.
     */
    public static List<AbstractAnalysis> getAnalyses(final MetaModelContext metaModelContext, final Class<? extends ModelElement> elementClass) {
        List<AbstractAnalysis> analyses = new ArrayList<>();
        List<AbstractAnalysis> allAnalyses = new ArrayList<>(getAnalyses(metaModelContext));
        MetaModel metaModel = metaModelContext.getMetaModel();
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
     * Liefert eine Analysedatei für den Benutzer aus seinem USER_HOME. <br>
     *
     * @return
     */
    public static File getRepositoryFile() {
        updateDefaultAnalysis(false); //ensure it exists
        return USER_HOME_ANALYSES_FILE;
    }

    /**
     * Gibt alle Abfragen zurück, die sich im Analyserepository befinden.
     *
     * @param metaModel
     * @return eine ArrayList der Abfragen. Jeder Eintrag des ArrayList ist eine
     *         XMLAnalyse.
     */
    private static List<XMLAnalysis> loadAnalysesFromUserHomeFile() {
        // Analyses
        File repositoryFile = getRepositoryFile();
        xmlAnalyses = loadAnalysesFile(repositoryFile);
        if (xmlAnalyses == null) { //an error occured while reading the existing file
            try {
                //make a backup of the unparseble old analysis file and create a new one with the default analyses
                if (repositoryFile.exists()) {
                    String backupFileName = repositoryFile.getAbsolutePath() + ".bak";
                    File backupFile = new File(backupFileName);
                    FileHandler.copyFile(repositoryFile, backupFile);
                }
            } catch (Exception ex) {
                //ignore
            }
            setOrReplaceAnlysesByDefaults();
        }
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
    public static List<XMLAnalysis> loadAnalysesFile(final File file) {
        try {
            return loadAnalysesFile(file.toURI().toURL());
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
    private static List<XMLAnalysis> loadAnalysesFile(final URL url) {
        String line = "";
        List<XMLAnalysis> analysen = null;
        try {
            BufferedReader dataStream = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
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
                if (line.startsWith(metaModelIDLinePrefix)) {
                    int idStartIndex = metaModelIDLinePrefix.length();
                    metaModelId = line.substring(idStartIndex).trim();
                    line = dataStream.readLine();
                } else {
                    MetaModelContext defaultMetaModelContext = getDefaultMetaModelContext();
                    metaModelId = defaultMetaModelContext.getMetaModelID();
                }
                String analyseID = line.substring("Content-ID: ".length());
                StringBuilder strbuf = new StringBuilder();
                for (line = dataStream.readLine(); !line.equals("--multipart_3lgm_query_separator"); line = dataStream.readLine()) {
                    strbuf.append(line + "\n");
                }
                XMLAnalysis toadd = null;
                MetaModelContext currentMetaModelContext = Tool3lgmMetaModelContext.getMetaModelContextForID(metaModelId);
                try {
                    toadd = XMLAnalysis.createAnalysis(currentMetaModelContext, strbuf.toString(), analyseID);
                } catch (SAXException ex) {
                    Log.show(Log.ERROR, getResString("ANALYSIS_CANT_CREATE") + "\n" + ex.getMessage(), ex);
                }
                analysen.add(toadd);
            }
            dataStream.close();
        } catch (Exception e) {
            analysen = null;
        }
        return analysen;

    }

    /**
     * Speichert die Analysen in die übergebene Analysedatei. Die übergebene
     * Datei muss nicht existieren, aber erzeugbar und beschreibbar sein.
     *
     * @param file die Datei, in die die Analysen gespeichert werden sollen.
     */
    public static void saveAnalyseFile(final File file, final List<XMLAnalysis> analysen) {
        if (file == null || analysen == null) {
            return;
        }
        try {
            if (file.exists()) {
                file.delete();
            }
            try (FileOutputStream fos = new FileOutputStream(file); OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8); BufferedWriter writer = new BufferedWriter(osw)) {
                writer.append("Content-Type: multipart/related; boundary=--multipart_3lgm_query_separator;\n");
                String line = "";
                for (int i = 0; i < analysen.size(); i++) {
                    XMLAnalysis sp = analysen.get(i);
                    line = null;
                    if (sp != null) {
                        line = sp.getXMLText();
                    }
                    if (line != null) {
                        writer.append("--multipart_3lgm_query_separator\nContent-Type: text/xml\n");
                        writer.append("Model-Type: " + sp.getMetaModelID() + "\n");
                        writer.append("Content-ID: " + sp.getID() + "\n");
                        writer.append(line);
                        if (!line.endsWith("\n")) {
                            writer.append("\n");
                        }
                    }
                }
                writer.append("--multipart_3lgm_query_separator");
            } catch (IOException e) {
                throw e;
            }
        } catch (Exception e) {
            Log.show(Log.INFO, getResString("analyse_speicherfehler"), e);
        }
    }

    /**
     * Speichert die aktuelle Liste <code>xmlAnalysen</code> als Repository.
     */
    public static final void saveRepository() {
        File repositoryFile = getRepositoryFile();
        saveAnalyseFile(repositoryFile, xmlAnalyses);
    }

    /**
     * Übernimmt die übergebenen Abfragen in das Repository. Der aktuelle Inhalt
     * des Repository wird dadurch überschrieben.
     *
     * @param newAbfragen eine ArrayList der neuen Abfragen. Jeder Eintrag des
     *            ArrayList ist eine XMLAnalyse.
     */
    public static boolean setXMLAnalysen(final List<XMLAnalysis> newXMLAnalyses) {
        if (newXMLAnalyses == null) {
            return false;
        }
        //usually this should be a set with only 1 element
        Set<MetaModelContext> metaModelContextsToChange = new HashSet<>();
        for (XMLAnalysis analysis : newXMLAnalyses) {
            MetaModelContext metaModelContext = analysis.getMetaModelContext();
            metaModelContextsToChange.add(metaModelContext);
        }

        List<XMLAnalysis> analyses = getAnalyses();
        int i = analyses.size() - 1;
        for (; i >= 0; i--) {
            XMLAnalysis xmlAnalysis = analyses.get(i);
            MetaModelContext metaModelContext = xmlAnalysis.getMetaModelContext();
            if (metaModelContextsToChange.contains(metaModelContext)) {
                xmlAnalyses.remove(i);
            }
        }
        //if we insert the newXMLAnalyses at the same position where the
        //old (and now removed) analyses were we ensure that the full list
        //has only minimal changes if the newAnalyses is an only minor
        //changed list of the oldAnalyses for this metamodel.
        xmlAnalyses.addAll(i < 0 ? 0 : i, newXMLAnalyses);
        return true;
    }

    /**
     * Updated das Repository. Das bedeutet, dass zuerst geprüft wird, ob im
     * USER_HOME bereits eine Datei mit den Analsysen vorkommen. Wenn nicht,
     * dann werden alle Standardanalysen aller Metamodelle in eine solche Datei
     * kopiert. Wenn
     *
     * @param checkNewerVersions
     */
    public static void updateDefaultAnalysis(final boolean checkNewerVersions) {
        if (!USER_HOME_ANALYSES_FILE.exists()) {
            setOrReplaceAnlysesByDefaults();
        } else if (checkNewerVersions) {
            //replace old analyses by potencial updated or add them if not exists
            List<XMLAnalysis> allDefaultAnalyses = loadAllDefaultAnalyses();
            List<XMLAnalysis> currentAnalyses = loadAnalysesFromUserHomeFile();
            for (XMLAnalysis defaultAnalysis : allDefaultAnalyses) {
                boolean exists = false;
                for (int i = 0; i < currentAnalyses.size(); i++) {
                    XMLAnalysis currentAnalysis = currentAnalyses.get(i);
                    if (currentAnalysis.hasEqualsID(defaultAnalysis)) {
                        currentAnalyses.set(i, defaultAnalysis);
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    currentAnalyses.add(defaultAnalysis);
                }
            }
            xmlAnalyses = currentAnalyses;
            // schreibe den Inhalt der Resourcendatei in file
            saveAnalyseFile(USER_HOME_ANALYSES_FILE, xmlAnalyses);
        }
    }

    /**
     * Replaces the file with all analyses in the USER_HOME directory by the
     * default analyses.
     */
    private static void setOrReplaceAnlysesByDefaults() {
        xmlAnalyses = loadAllDefaultAnalyses();
        // schreibe den Inhalt der Resourcendatei in file
        saveAnalyseFile(USER_HOME_ANALYSES_FILE, xmlAnalyses);
    }

    /**
     * @return a list of all analyses defined in the plugins of all metamodels
     */
    private static List<XMLAnalysis> loadAllDefaultAnalyses() {
        List<XMLAnalysis> xmlAnalyses = new ArrayList<>();
        for (MetaModelContext metaModelContext : Tool3lgmMetaModelContext.getRegularMetaModelContexts()) {
            try {
                MetaModel metaModel = metaModelContext.getMetaModel();
                AnalysesDefinition analysesDefinition = metaModel.getAnalysesDefinition();
                String xmlAnalysisRepositoryFileName = analysesDefinition.getXMLAnalysisRepositoryFileName();

                URL analysesFileURL = ClassLoader.getSystemResource(xmlAnalysisRepositoryFileName);
                List<XMLAnalysis> analyses = loadAnalysesFile(analysesFileURL);
                xmlAnalyses.addAll(analyses);
            } catch (Exception e) {
                continue;
            }
        }
        return xmlAnalyses;
    }

}
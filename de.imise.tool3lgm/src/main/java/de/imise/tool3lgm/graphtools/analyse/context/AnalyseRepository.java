package de.imise.tool3lgm.graphtools.analyse.context;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import org.xml.sax.SAXException;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.analyse.special.InterfaceCanSendOTAnalysis;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.log.Log;

/**
 * Diese Klasse stellt Methoden zum speichern und laden der Analysen bereit. Hier werden alle
 * Analysen die im 3LGM-Baukasten verwendet werden, verwaltet. Achtung: Es existiert noch kein
 * Listener-Mechanismus, der Klassen, welche das AnalyseRepository verwenden, über Änderungen im
 * Repository informiert.
 *
 * @author Thomas Wendt, Sebastian Weber, AXS
 */
public class AnalyseRepository {

    /** Datei, aus der zuletzt Analysen geladen oder in die zuletzt Analysen gespeichert wurden */
    private static File file;

    /** Enthält alle Analysen, die im Modell audf Elemente angewendet werden können. */
    private static List<XMLAnalyse> xmlAnalysen;

    private static List<AbstractAnalyse> specialAnalysis;

    /**
     * Fügt eine neue XMLAnalyse ins Repository ein, wenn sie noch nicht enthalten ist.
     *
     * @param xmlAnalysen Liste zu der die übergebene XMLAnalyse hinzugefügt werden soll
     * @param toadd die XMLAnalyse, die hinzugefügt werden soll
     */
    public static boolean addAnalyse(final XMLAnalyse toadd) {
        if (toadd == null) {
            return false;
        }
        if (xmlAnalysen == null) {
            xmlAnalysen = new ArrayList<>();
        }
        if (xmlAnalysen.contains(toadd)) {
            return false;
        }
        if (toadd.startknoten == null || toadd.startknoten.isEmpty()) {
            return false;
        }
        xmlAnalysen.add(toadd);
        return true;
    }

    /**
     * Prüft, ob der übergebene <code>name</code> bei irgend einer anderen als der einzeln
     * übergebenen XMLAnalyse vorkommt.
     *
     * @param xmlAnalysen eine ArrayList der Analysen.
     * @param xMLAnalyse die XMLAnalyse, der ein neuer Name gegeben werden soll.
     * @param name der Name für die übergebene XMLAnalyse.
     * @return false, wenn der Name in der Liste der Analysen gar nicht enthalten ist oder nur die
     *         einzeln übergebnen XMLAnalyse diesen Namen besitzt.
     */
    public static boolean containsName(final List<XMLAnalyse> analysen, final XMLAnalyse xMLAnalyse, final String name) {
        if (analysen == null) {
            return false;
        }
        for (XMLAnalyse ana : analysen) {
            if (ana.getName().equals(name)) {
                if (xMLAnalyse != null && xMLAnalyse == ana) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt alle Analysen zurück, deren Startknoten dem übergebenen Node entspricht.
     *
     * @param elementClassName
     * @return ArrayList, in der jeder Eintrag eine XMLAnalyse ist.
     */
    public static List<AbstractAnalyse> getAnalysenFuerKnoten(final String elementClassName) {
        Class<? extends ModelElement> elementClass = ModelConstants.getClassForName(elementClassName);
        List<AbstractAnalyse> analysenFuerKnoten = new ArrayList<>();
        List<AbstractAnalyse> allAnalysis = new ArrayList<>(getXMLAnalysen());
        allAnalysis.addAll(getSpecialAnalysis());
        for (AbstractAnalyse ana : allAnalysis) {
            ArrayList<Class<? extends ModelElement>> startknoten = ana.getStartknoten();
            for (Class<? extends ModelElement> startKnotenClass : startknoten) {
                if (startKnotenClass != null && startKnotenClass.isAssignableFrom(elementClass)) {
                    analysenFuerKnoten.add(ana);
                }
            }
        }
        return analysenFuerKnoten;
    }

    /**
     * Liefert eine Analysedatei für den Benutzer. <br>
     * Ist bereits eine Datei geladen oder gespeichert worden, kommt diese zurück. Sonst wird der
     * Reihe nach folgendes geprüft:<br>
     * Hat der Benutzer in seinem Anwendungsdatenverzeichnis eine Analysedatei mit dem Namen
     * "Tool3lgm_Analys.res3" wird diese zurückgegeben. Sie sollte immer beschreibbar sein.<br>
     * Ex. diese Datei nicht, dann wird im Installationsverzeichnis des Bauskastens nach einer Datei
     * mit dem Namen "Tool3lgm.analysis" gesucht. Wenn diese beschreibbar ist, wird sie
     * zurückgeliefert. Wenn sie nicht beschreibbar ist, dann wird diese Datei ins
     * Anwendungsdatenverzeichnis des Benutzers mit den Namen "Tool3lgm_Analys.res3" kopiert und
     * zurückgegeben.<br>
     * Wenn auch im Installationsverzeichnis keine Analysendatei gefunden wurde, wird aus den
     * Resourcen die Standarddatei geladen. Zuerst wird versucht, sie unter dem Namen
     * "Tool3lgm.analysis" ins Installationsverzeichnis des Baukastens zu kopieren. Geht das gut,
     * wird sie zurückgegeben. Geht das nicht gut, wird die Standarddatei ins Benutzerverzeichnis
     * mit dem Namen "Tool3lgm_LOCALECODE.analysis" kopiert und diese neue Datei zurückgegeben.<br>
     * Geht das auch nicht gut, wird die Original Resourcendatei zurückgegeben. Da sie in einem
     * jar-Paket liegt, ist sie dann sicher nicht beschreibbar. Der ganze Aufwand hängt damit
     * zusammen, dass der Benutzer immer auf einer Datei arbeitet, die er auch möglichst beschreiben
     * kann. Das Speichern der Analysendatei im Installationsverzeichnis hat den Vorteil, dass die
     * Analysen allen Benutzern dieser Installation zu Verfügung stehen.
     *
     * @return
     */
    public static File getRepositoryFile() {
        if (file != null) {
            return file;
        }
        file = new File(Tool3lgmConstants.USER_HOME_DIR_NAME, Tool3lgmConstants.ANALYSEN_FILE_NAME);
        if (file.exists()) {
            return file;
        }
        file = new File(Tool3lgmConstants.APPLICATION_DIR, Tool3lgmConstants.ANALYSEN_FILE_NAME);
        // es ex. eine Analysendatei im Installationsverzeichnis
        if (file.exists()) {
            // die Datei aus dem Installationsverzeichnis ins Benutzerverzeichnis kopieren (lesen
            // sollte
            // man file können, sonst könnte man den Baukasten im gleichen Verzeichnis auch nicht
            // ausführen)
            if (!file.canWrite()) {
                File userHomeFile = new File(Tool3lgmConstants.USER_HOME_DIR_NAME, Tool3lgmConstants.ANALYSEN_FILE_NAME);
                saveAnalyseFile(userHomeFile, loadAnalyseFile(file));
                file = userHomeFile;
            }
            // es ex. keine Analysendatei im Installationsverzeichnis -> Standardresourcendatei
            // dahin oder, wenn
            // das nicht geht, ins Benutzerverzeichnis kopieren
        } else {
            try {
                // wenn es die Datei nicht im Installpfad gibt und man sie auch nicht dahin
                // schreiben kann
                if (!file.createNewFile()) {
                    // lege eine Datei im Benutzerverzeichnis an
                    file = new File(Tool3lgmConstants.USER_HOME_DIR_NAME, Tool3lgmConstants.ANALYSEN_FILE_NAME);
                }
            } catch (IOException e) {
                // das hier tritt ein, wenn es bei file.createNewFile() ne Exception gab
                file = new File(Tool3lgmConstants.USER_HOME_DIR_NAME, Tool3lgmConstants.ANALYSEN_FILE_NAME);
            }

            // schreibe den Inhalt der Resourcendatei in file
            saveAnalyseFile(file, loadAnalyseFile(Tool3lgmConstants.DEFAULT_ANALYSEN_RESOURCE_URL));
        }
        return file;
    }

    /**
     * @return
     */
    public static List<AbstractAnalyse> getSpecialAnalysis() {
        if (specialAnalysis == null) {
            specialAnalysis = new ArrayList<>();
            specialAnalysis.add(new InterfaceCanSendOTAnalysis());
        }
        return specialAnalysis;
    }

    /**
     * Gibt alle Abfragen zurück, die sich im Analyserepository befinden.
     *
     * @return eine ArrayList der Abfragen. Jeder Eintrag des ArrayList ist eine XMLAnalyse.
     */
    public static List<XMLAnalyse> getXMLAnalysen() {
        if (xmlAnalysen == null) {
            xmlAnalysen = loadAnalyseFile(getRepositoryFile());
        }
        if (xmlAnalysen == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(xmlAnalysen);
    }

    // ////////////////////////////////////////////////////
    // Speichern, Laden und Import von XMLAnalyse-Dateien //
    // ////////////////////////////////////////////////////

    /**
     * Gibt eine Liste aller Analysen zurück, die sich in dem übergebenen File befinden.
     *
     * @param file
     * @return
     */
    public static List<XMLAnalyse> loadAnalyseFile(final File file) {
        try {
            return loadAnalyseFile(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Gibt eine Liste aller Analysen zurück, die sich in der Datei mit der übergebenen URL
     * befinden.
     *
     * @param f
     * @return
     */
    public static List<XMLAnalyse> loadAnalyseFile(final URL url) {
        String line = "";
        List<XMLAnalyse> analysen = null;
        try {
            BufferedReader dataStream = new BufferedReader(new InputStreamReader(url.openStream()));
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
                String ananame = line.substring(12);
                StringBuilder strbuf = new StringBuilder();
                for (line = dataStream.readLine(); !line.equals("--multipart_3lgm_query_separator"); line = dataStream.readLine()) {
                    strbuf.append(line + "\n");
                }
                XMLAnalyse toadd = null;
                try {
                    toadd = XMLAnalyse.createAnalyse(ananame, strbuf.toString());
                } catch (SAXException ex) {
                    Log.show(Log.ERROR, getResString("AnalyseNichtErstellt") + "\n" + ex.getMessage(), ex);
                }
                analysen.add(toadd);
            }
            dataStream.close();
        } catch (Exception e) {
            Log.show(Log.ERROR, getResString("fehler"), e);
        }
        return analysen;

    }

    /**
     * Speichert die Analysen in die übergebene Analysedatei. Die übergebene Datei muss nicht
     * existieren, aber erzeugbar und beschreibbar sein.
     *
     * @param f die Datei, in die die Analysen gespeichert werden sollen.
     */
    public static void saveAnalyseFile(final File f, final List<XMLAnalyse> analysen) {
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
                XMLAnalyse sp = analysen.get(i);
                line = null;
                if (sp != null) {
                    line = sp.getXMLText();
                }
                if (line != null) {
                    raf.writeBytes("--multipart_3lgm_query_separator\nContent-Type: text/xml\nContent-ID: " + sp.getName() + "\n");
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
        saveAnalyseFile(getRepositoryFile(), xmlAnalysen);
    }

    /**
     * Übernimmt die übergebenen Abfragen in das Repository. Der aktuelle Inhalt des Repository wird
     * dadurch überschrieben.
     *
     * @param newAbfragen eine ArrayList der neuen Abfragen. Jeder Eintrag des ArrayList ist eine
     *            XMLAnalyse.
     */
    public static boolean setXMLAnalysen(final List<XMLAnalyse> newXMLAnalysen) {
        if (newXMLAnalysen == null) {
            return false;
        }
        xmlAnalysen = newXMLAnalysen;
        return true;
    }

}
package de.imise.tool3lgm.xslt;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.log.Log;
import de.imise.util.StringUtils;
import de.imise.util.io.FileNameExtensionFilterAndFileFilter;

/**
 * @author AXS
 * @create 06.07.2012
 */
public class XSLTFileHandler {

    /**
     * {@link javax.swing.filechooser.FileFilter} für XSL-Dateien
     */
    public static final FileNameExtensionFilterAndFileFilter XSLT_FILE_FILTER = new FileNameExtensionFilterAndFileFilter(Tool3lgmConstants.getResString("FILE_FILTER_XSL"), StringUtils.tokenize(Tool3lgmConstants.getResString("FILE_FILTER_XSL_EXT"), " ",
            false));

    /**
     * fuegt alle XSLT-Files eines Verzeichnisses zur Tabelle hinzu
     * 
     * @param path
     */
    public static ArrayList<XSLTScript> getXSLTScripts(final ArrayList<File> searchPath) {
        ArrayList<XSLTScript> list = new ArrayList<XSLTScript>();
        for (File f : searchPath) {
            if (!f.isDirectory()) {
                continue;
            }
            File[] files = f.listFiles(XSLT_FILE_FILTER);
            if (files == null) {
                continue;
            }
            for (File ff : files) {
                String[] attr = checkContent(ff);
                if (attr != null && attr[2].indexOf("html") >= 0) {
                    list.add(new XSLTScript(ff));
                }
            }
        }
        return list;
    }

    /**
     * gibt UserField einer XSLT-Datei zurueck, wenn die angegebene Datei den
     * Spezifikationen entspricht, ansonsten null
     * 
     * @param arg0 File, die zu ueberpruefende Datei
     * @return String[] mit den gefunden Eigenschaften, oder null
     *         String[] = { dateiname mit pfad, bezeichnung, beschreibung, autor }
     */
    public static String[] checkContent(final File arg0) {
        if (!arg0.isFile()) {
            return null;
        }
        if (!XSLT_FILE_FILTER.accept(arg0)) {
            return null;
        }
        try {
            RandomAccessFile file = new RandomAccessFile(arg0, "r");
            String[] attr = check(file);
            file.close();
            if (attr != null) {
                attr[0] = arg0.toString();
            }
            return attr;
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }
    }

    /**
     * @param file
     * @return
     */
    private static String[] check(final RandomAccessFile file) {
        try {
            String[] attr = {
                    "", "", "", "", ""
            };
            String line;
            int endIndex;

            // Leerzeilen überspringen
            while ((line = file.readLine()).equals("")) {
                continue;
            }

            if (!line.equals("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>")) {
                return null;
            }

            // Leerzeilen überspringen
            while ((line = file.readLine()).equals("")) {
                continue;
            }

            if (line.indexOf("<!--name: ") != 0) {
                return null;
            }
            if ((endIndex = line.indexOf(" -->")) < 9) {
                return null;
            }
            attr[1] = line.substring(10, endIndex);

            // Leerzeilen überspringen
            while ((line = file.readLine()).equals("")) {
                continue;
            }

            if (line.indexOf("<!--type: ") != 0) {
                return null;
            }
            if ((endIndex = line.indexOf(" -->")) < 9) {
                return null;
            }
            attr[2] = line.substring(10, endIndex);

            // Leerzeilen überspringen
            while ((line = file.readLine()).equals("")) {
                continue;
            }

            if (line.indexOf("<!--description: ") != 0) {
                return null;
            }
            if ((endIndex = line.indexOf(" -->")) < 16) {
                return null;
            }
            attr[3] = line.substring(17, endIndex);

            // Leerzeilen überspringen
            while ((line = file.readLine()).equals("")) {
                continue;
            }

            if (line.indexOf("<!--author: ") != 0) {
                return null;
            }
            if ((endIndex = line.indexOf(" -->")) < 11) {
                return null;
            }
            attr[4] = line.substring(12, endIndex);

            return attr;
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }
    }

}

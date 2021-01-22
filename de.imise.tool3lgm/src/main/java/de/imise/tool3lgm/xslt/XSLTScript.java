/*
 * Created on 19.07.2004 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.xslt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import de.imise.util.Sys;

/**
 * @author Thomas Rudert
 */
public class XSLTScript {

    /**
     *
     */
    private final String source;

    /**
     *
     */
    private String name = null;

    /**
     *
     */
    private String description = null;

    /**
     *
     */
    private String type = null;

    /**
     *
     */
    private String author = null;

    /**
     *
     */
    private URL url = null;

    /**
     *
     */
    private File file = null;

    /**
     * @param file
     */
    public XSLTScript(final File file) {
        Sys.err(file);
        this.file = file;
        source = file.toString();
        String[] content = XSLTFileHandler.checkContent(file);
        name = content[1];
        type = content[2];
        description = content[3];
        author = content[4];
    }

    /**
     * @param url
     * @throws IOException
     */
    public XSLTScript(final URL url) throws IOException {
        this.url = url;
        source = extractUrlSourceString();
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "ISO-8859-1"));
        String line;
        while ((line = reader.readLine()) != null) {
            int indexOfContentEnd = line.indexOf("-->");
            if (indexOfContentEnd < 0) {
                indexOfContentEnd = line.length();
            }
            if (line.startsWith("<!--name: ")) {
                name = line.substring("<!--name: ".length(), indexOfContentEnd);
            } else if (line.startsWith("<!--description: ")) {
                description = line.substring("<!--description: ".length(), indexOfContentEnd);
            } else if (line.startsWith("<!--type: ")) {
                type = line.substring("<!--type: ".length(), indexOfContentEnd);
            } else if (line.startsWith("<!--author: ")) {
                author = line.substring("<!--author: ".length(), indexOfContentEnd);
            }
            if (name != null && description != null && type != null && author != null) {
                break;
            }
        }
        reader.close();
    }

    /**
    *
    */
    private String extractUrlSourceString() {
        //TODO:AXS: testen, ob der Kommentar unten noch stimmt
        //Aus irgendeinem Grund kommt die URL mit "%5c" und "/" als Dateitrenner gemischt rein
        //und das obwohl File.separator einen Backslash ("\") liefert. Deswegen wird hier einfach
        //jede Möglichkeit getestet, um an den letzten Namen innerhalb des Dateipfades zu kommen
        String urlName = url.toString();
        int index = urlName.lastIndexOf("%5c");
        int offset = 0;
        if (index < 0) {
            index = urlName.lastIndexOf(File.separator);
        } else {
            offset = "%5c".length();
        }
        if (offset == 0) {
            if (index < 0) {
                index = urlName.lastIndexOf("/");
            } else {
                offset = 1;
            }
        }
        if (offset == 0 && index >= 0) {
            offset = 1;
        }
        String source = urlName.substring(index < 0 ? 0 : index + offset);
        return source;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getAuthor() {
        return author;
    }

    public InputStream openStream() throws IOException {
        if (file != null) {
            return new FileInputStream(file);
        } else if (url != null) {
            return url.openStream();
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        //	    assert false : "hashCode not designed";
        return 42; // any arbitrary constant will do
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof XSLTScript && ((XSLTScript) obj).getSource().equals(getSource());
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * @return
     */
    public boolean isReadOnly() {
        return file == null && url != null;
    }

}
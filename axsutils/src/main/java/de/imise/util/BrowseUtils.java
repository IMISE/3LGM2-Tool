package de.imise.util;

import java.awt.Desktop;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import javax.swing.event.HyperlinkEvent;

/**
 * Stellt Funktionen zum Öffnen von Dateien, Verzeichnissen oder Webseiten
 * bereit.
 *
 * @author AXS (9 Apr 2010)
 */
public class BrowseUtils {

    /**
     * Öffnet die Datei oder das Verzeichnis mit dem zugehörigen
     * Standard-Systemprogramm.
     *
     * @param file Pfad zur Datei oder dem Verzeichnis
     */
    public static final boolean browse(final File file) {
        URI uri = file.toURI();
        return browse(uri);
    }

    /**
     * Öffnet die Datei, deren absoluter Pfad sich aus dem Pfad der Application
     * und dem übergebenen relativen Pfad ergibt.
     *
     * @param relativePath
     */
    public static final boolean browseApplicationPathRelativeFile(final String relativePath) {
        File file = new File(ApplicationManager.getApplicationDir(), relativePath);
        return browse(file);
    }

    /**
     * Converts the passed object into a string (via String.valueOf(Object)). If
     * this string describes a file, the file will be loaded. Relative paths are
     * converted to absolute paths. If the file cannot be opened, the string is
     * interpreted as URI and opened. If this does not work either, the string
     * is preceded by "http://" and then again as an attempt to open it as a web
     * page. If this does not work either, <code>false</code> returns, otherwise
     * always <code>true</code>.
     *
     * @param urlOrPathObject the object intereted as file string or uri
     */
    public static final boolean browse(final Object urlOrPathObject) {
        String urlOrPath = null;
        if (urlOrPathObject instanceof File) {
            File file = (File) urlOrPathObject;
            if (browse((File) urlOrPathObject)) {
                return true;
            }
            urlOrPath = file.getPath();
        }
        if (urlOrPath == null) {
            urlOrPath = String.valueOf(urlOrPathObject);
        }
        try {
            File file = new File(urlOrPath);
            String fullPath = file.getAbsolutePath();
            file = new File(fullPath);
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            String query = null;
            int indexOfQueryStart = urlOrPath.indexOf('?') + 1;
            if (indexOfQueryStart > 0) {
                query = urlOrPath.substring(indexOfQueryStart);
                query = encodeQuery(query);
                urlOrPath = urlOrPath.substring(0, indexOfQueryStart);
            }
            try {
                String fullUri = urlOrPath.contains("://") ? urlOrPath : "https://" + urlOrPath; // if no protocel -> https
                if (query != null) {
                    fullUri += query;
                }
                URI uri = new URI(fullUri);
                browse(uri);
            } catch (Exception ex) {
                ex.printStackTrace();
                int protocolStartIndex = urlOrPath.indexOf("://") + 3;
                String url = protocolStartIndex >= 0 ? urlOrPath.substring(protocolStartIndex) : urlOrPath;
                try {
                    String fullUri = "http://" + url;
                    if (query != null) {
                        fullUri += query;
                    }
                    URI uri = new URI(fullUri);
                    browse(uri);
                } catch (Exception exx) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param unencodedQuery
     * @return
     */
    private static String encodeQuery(final String unencodedQuery) {
        StringBuilder fullEncodedString = new StringBuilder();
        StringTokenizer st = new StringTokenizer(unencodedQuery, "&", true);
        while (st.hasMoreTokens()) {
            String nextToken = st.nextToken();
            if (nextToken.equals("&")) {
                fullEncodedString.append(nextToken);
                continue;
            }
            int indexOfEqualsSign = nextToken.indexOf('=') + 1;
            String parameterName = "";
            String parameterValue = "";
            if (indexOfEqualsSign > 0) {
                parameterName = nextToken.substring(0, indexOfEqualsSign);
                parameterValue = nextToken.substring(indexOfEqualsSign);
            } else {
                parameterValue = nextToken;
            }
            parameterValue = encodeValue(parameterValue);
            fullEncodedString.append(parameterName);
            fullEncodedString.append(parameterValue);
        }
        return fullEncodedString.toString();
    }

    /**
     * Method to encode a string value using `UTF-8` encoding scheme
     *
     * @param value
     * @return
     */
    private static String encodeValue(final String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    /**
     * Öffnet die übergebene URI, je nachdem was es ist als Datei, als
     * Verzeichnis oder als Webseite.
     *
     * @param uri
     */
    public static final boolean browse(final URI uri) {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Öffnet die übergebene URI, je nachdem was es ist als Datei, als
     * Verzeichnis oder als Webseite.
     *
     * @param uri
     */
    public static final boolean browse(final HyperlinkEvent event) {
        URL url = event.getURL();
        try {
            URI uri = url.toURI();
            return browse(uri);
        } catch (Exception e) {
            return browse(url);
        }
    }

}
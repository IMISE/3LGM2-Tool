package de.imise.tool3lgm.xslt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Thomas Rudert Klasse zum Ausführen von Transformationen auf
 *         XML-Dokumenten mit Hilfe von XSLT-Dateien
 */
public class XMLTransformer {

    /**
     * führt eine Transformation aus
     *
     * @param xsltSource Quelle mit den Transformationsvorschriften
     * @param xmlSource Quelle der XML-Modell-Datei
     * @param destination Ziel für die transformierte Datei
     * @return boolean with true if execution was successful
     */
    public static void transform(final InputStream xsltSource, final String xsltName, final File xmlSource, final String destination) throws TransformerException, IOException {
        TransformerFactory tFactory = TransformerFactory.newInstance();

        Transformer transformer = tFactory.newTransformer(new StreamSource(xsltSource));

        FileOutputStream result = new FileOutputStream(destination);

        transformer.transform(new StreamSource(xmlSource), new StreamResult(result));

        result.close();
    }
}
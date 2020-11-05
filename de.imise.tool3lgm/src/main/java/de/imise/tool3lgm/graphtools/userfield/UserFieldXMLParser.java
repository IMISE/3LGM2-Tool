package de.imise.tool3lgm.graphtools.userfield;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.ToolXMLWriter;

/**
 * @author Thomas Rudert seit Version Beta 2 P26, AXS mit Inputstreams für die
 *         Version zum aus den JARs laden
 */
public class UserFieldXMLParser {

    private final SAXParser parser;

    private final InputStream userFieldFileStream;

    /**
     * @param userFieldFileStream
     * @param def
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    private UserFieldXMLParser(final InputStream userFieldFileStream, final UserFieldDefinitions def) throws SAXException, ParserConfigurationException, IOException {
        this.userFieldFileStream = userFieldFileStream;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        parser = factory.newSAXParser();
        parser.getXMLReader().setContentHandler(new UserFieldXMLContentHandler(def));
    }

    private void parseDocument() throws SAXException, IOException {
        XMLReader reader = parser.getXMLReader();
        reader.parse(new InputSource(userFieldFileStream));
        userFieldFileStream.close();
    }

    private static final boolean importDefinitions(final URL url, final File file, final UserFieldDefinitions definitions) {
        try {
            InputStream inStream = file != null ? new FileInputStream(file) : url.openStream();
            UserFieldXMLParser parser = new UserFieldXMLParser(inStream, definitions);
            parser.parseDocument();
        } catch (Exception exp) {
            Log.show(Log.ERROR, "Exception while parsing UserFieldFile", exp);
            return false;
        }
        return true;
    }

    public static final boolean importDefinitions(final URL url, final UserFieldDefinitions definitions) {
        return importDefinitions(url, null, definitions);
    }

    public static final boolean importDefinitions(final File file, final UserFieldDefinitions definitions) {
        return importDefinitions(null, file, definitions);
    }

    public static final boolean exportDefinitions(final File file, final UserFieldDefinitions definitions) {
        return ToolXMLWriter.writeUserFieldDefinitions(definitions, file);
    }

}
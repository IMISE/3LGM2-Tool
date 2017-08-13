package de.imise.tool3lgm.graphtools.userfield;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.ToolXMLWriter;

/**
 * @author Thomas Rudert seit Version Beta 2 P26
 */
public class UserFieldXMLParser {

    private final SAXParser parser;

    private final File userFieldFile;

    /**
     * @param _userFieldFile
     * @param def
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    private UserFieldXMLParser(final File _userFieldFile, final UserFieldDefinitions def) throws SAXException, ParserConfigurationException, IOException {
        if (!_userFieldFile.canRead()) {
            throw new IOException("Can not read file: " + _userFieldFile.toString());
        }
        userFieldFile = _userFieldFile;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        parser = factory.newSAXParser();
        parser.getXMLReader().setContentHandler(new UserFieldXMLContentHandler(def));
    }

    private void parseDocument() throws SAXException, IOException {
        FileInputStream stream = new FileInputStream(userFieldFile);
        XMLReader reader = parser.getXMLReader();
        reader.parse(new InputSource(stream));
        stream.close();
    }

    public static final boolean importDefinitions(final File file, final UserFieldDefinitions definitions) {
        try {
            UserFieldXMLParser parser = new UserFieldXMLParser(file, definitions);
            parser.parseDocument();
        } catch (Exception exp) {
            Log.show(Log.ERROR, "IOException while parsing UserFieldFile", exp);
            return false;
        }
        return true;
    }

    public static final boolean exportDefinitions(final File file, final UserFieldDefinitions definitions) {
        return ToolXMLWriter.writeUserFieldDefinitions(definitions, file);
    }

}
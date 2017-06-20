package de.imise.tool3lgm.userproperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.imise.tool3lgm.xml.ToolDTDHandler;
import de.imise.tool3lgm.xml.XMLVersionException;

/**
 * @author Thomas Rudert
 *         seit Version Beta 2 P26
 */
public class UserPropertiesParser {

    /** unterstützte XML und Datei Versionen (aktuellste Version steht im Array ganz hinten, also mit Index = length-1) */
    private static String[] supportedXMLVersions = {
            "<?xml version='1.0' encoding='utf-8'?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    };

    private final SAXParser parser;

    private final File userInfoFile;

    /**
	 * 
	 */
    public UserPropertiesParser(final File _userInfoFile) throws SAXException, ParserConfigurationException, IOException, XMLVersionException {

        if (!_userInfoFile.canRead()) {
            throw new IOException("Can not read file: " + _userInfoFile.toString());
        }

        userInfoFile = _userInfoFile;

        SAXParserFactory factory = SAXParserFactory.newInstance();

        parser = factory.newSAXParser();
        parser.getXMLReader().setDTDHandler(new ToolDTDHandler());

        if (!isParseAbleFileVersion(userInfoFile)) {
            throw new XMLVersionException("Unable to read content of file: " + userInfoFile);
        }

        parser.getXMLReader().setContentHandler(new UserPropertiesContentHandler());
    }

    public void parseDocument() throws SAXException, IOException {
        FileInputStream stream = new FileInputStream(userInfoFile);

        XMLReader reader = parser.getXMLReader();
        reader.parse(new InputSource(stream));

        stream.close();
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean isParseAbleFileVersion(final File file) throws IOException {
        boolean parseAble = false;

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line = reader.readLine();

        for (int i = 0; i < supportedXMLVersions.length; i++) {
            if (line.toLowerCase().equals(supportedXMLVersions[i].toLowerCase())) {
                parseAble = true;
                break;
            }
        }

        reader.close();
        return parseAble;
    }
}
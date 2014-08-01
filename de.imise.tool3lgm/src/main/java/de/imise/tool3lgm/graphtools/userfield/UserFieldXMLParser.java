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

/**
 * @author Thomas Rudert seit Version Beta 2 P26
 */
public class UserFieldXMLParser {

	/**
	 * Comment for <code>parser</code>
	 */
	private SAXParser parser;

	/**
	 * Comment for <code>userFieldFile</code>
	 */
	private File userFieldFile;

	/**
	 * 
	 */
	public UserFieldXMLParser(File _userFieldFile, UserFieldDefinitions def) throws SAXException, ParserConfigurationException, IOException {

		if (!_userFieldFile.canRead())
			throw new IOException("Can not read file: " + _userFieldFile.toString());

		userFieldFile = _userFieldFile;

		SAXParserFactory factory = SAXParserFactory.newInstance();

		parser = factory.newSAXParser();

		parser.getXMLReader().setContentHandler(new UserFieldXMLContentHandler(def));
	}

	public void parseDocument() throws SAXException, IOException {
		FileInputStream stream = new FileInputStream(userFieldFile);

		XMLReader reader = parser.getXMLReader();
		reader.parse(new InputSource(stream));

		stream.close();
	}

}
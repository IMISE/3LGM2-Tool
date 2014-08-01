/*
 * Created on 25.11.2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.userfield;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.imise.tool3lgm.graphtools.elements.ModelConstants;

/**
 * Die Variablen sind auf protected Gesetzt, damit man einen neuen ContentHandler
 * von dieser Klasse ableiten kann aber trotzdem noch Zugriff auf alle nötigen
 * Werte hat. Ich denke, bei kleinen Änderungen (hinzukommen oder wegfallen einzelnener
 * Felder im Dokument) muß man keinen ganz neuen ContentHandler schreiben sondern muß
 * nur einen abgeleiteten von diesem bilden. Ich würde aber empfehlen von Zeit zu Zeit
 * einen völlig neuen ContentHandler zu schreiben.
 * 
 * @author Thomas Rudert
 * 
 */
public class UserFieldXMLContentHandler implements ContentHandler {

	private UserFieldDefinitions definitions;
	private UserField field;
	
	/** String der in der characters Methode ausgelesen wird (Werte eines Tags) */
	private StringBuilder elementValue = new StringBuilder();

	/**
	 * 
	 */
	public UserFieldXMLContentHandler(UserFieldDefinitions def) {
		super();
		definitions = def;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	@Override
	public void setDocumentLocator(Locator arg0) {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
	 */
	@Override
	public void startPrefixMapping(String arg0, String arg1) throws SAXException {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		elementValue.setLength(0);		
		
		if (qName.equals("userFieldDefinitions")) {
			
		} else 	if (qName.equals("userFieldDef")) {
			String elementClass = atts.getValue("elementClass");
			//bei Modellvariablen ist die Elementclass null
			if (elementClass==null)
				field = new UserField(atts.getValue("hash"), definitions);
			else
				field = new UserField(ModelConstants.getClassForName(elementClass), atts.getValue("hash"), definitions);
		} else if (qName.equals("userFieldName")) {
		
		} else if (qName.equals("userFieldDescription")) {
		
		} else if (qName.equals("userFieldStyle")) {
			
		} else if (qName.equals("userFieldStandardValue")) {

		} else if (qName.equals("userFieldTreeVis")) {
		
		} else if (qName.equals("userFieldFormula")) {
		
		} else if (qName.equals("userFieldInternalAccounting")) {

		} else if (qName.equals("userFieldInternalAccountingWeightUserFieldHash")) {
		
		} else if (qName.equals("userFieldFormatHash")) {		
		
		} else if (qName.equals("userFieldFormatString")) {
		
		} else {
			throw new SAXException("Unknown xml-tag: " + qName);
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
	    if (qName.equals("userFieldDef")) {
			definitions.add(field);
		} else if (qName.equals("userFieldDefinitions")) {
			
		} else if (qName.equals("userFieldName") || qName.equals("userFieldDescription") || qName.equals("userFieldStyle")
				|| qName.equals("userFieldTreeVis") || qName.equals("userFieldStandardValue")
				|| qName.equals("userFieldInternalAccounting") || qName.equals("userFieldInternalAccountingWeightUserFieldHash")
				|| qName.equals("userFieldFormula") || qName.equals("userFieldFormatString")|| qName.equals("userFieldFormatHash")){
			if (field == null)
				throw new SAXException("Error while parsing definition of userFields: userFiel shouldn't not be equals to null");
			field.putXMLFieldString(qName, elementValue.toString());
		} else {
			throw new SAXException("Unknown xml-tag: " + qName);
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		elementValue.append(String.valueOf(arg0, arg1, arg2));
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
	 */
	@Override
	public void processingInstruction(String arg0, String arg1) throws SAXException {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	@Override
	public void skippedEntity(String arg0) throws SAXException {
	}	
}

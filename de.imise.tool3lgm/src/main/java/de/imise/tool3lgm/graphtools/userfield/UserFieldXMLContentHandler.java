/*
 * Created on 25.11.2003 To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.userfield;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.imise.tool3lgm.graphtools.elements.ModelConstants;

/**
 * Die Variablen sind auf protected Gesetzt, damit man einen neuen ContentHandler von dieser Klasse ableiten kann aber trotzdem noch Zugriff auf alle
 * nötigen Werte hat. Ich denke, bei kleinen Änderungen (hinzukommen oder wegfallen einzelnener Felder im Dokument) muß man keinen ganz neuen
 * ContentHandler schreiben sondern muß nur einen abgeleiteten von diesem bilden. Ich würde aber empfehlen von Zeit zu Zeit einen völlig neuen
 * ContentHandler zu schreiben.
 * 
 * @author Thomas Rudert
 */
public class UserFieldXMLContentHandler implements ContentHandler {

    private final UserFieldDefinitions definitions;
    private UserField field;

    /** String der in der characters Methode ausgelesen wird (Werte eines Tags) */
    private final StringBuilder elementValue = new StringBuilder();

    /**
	 * 
	 */
    public UserFieldXMLContentHandler(final UserFieldDefinitions def) {
        super();
        definitions = def;
    }

    @Override
    public void setDocumentLocator(final Locator arg0) {
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startPrefixMapping(final String arg0, final String arg1) throws SAXException {
    }

    @Override
    public void endPrefixMapping(final String arg0) throws SAXException {
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        elementValue.setLength(0);

        if (qName.equals("userFieldDefinitions")) {

        } else if (qName.equals("userFieldDef")) {
            String elementClass = atts.getValue("elementClass");
            //bei Modellvariablen ist die Elementclass null
            if (elementClass == null) {
                field = new UserField(atts.getValue("hash"), definitions);
            } else {
                field = new UserField(ModelConstants.getClassForName(elementClass), atts.getValue("hash"), definitions);
            }
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

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        if (qName.equals("userFieldDef")) {
            definitions.add(field);
        } else if (qName.equals("userFieldDefinitions")) {

        } else if (qName.equals("userFieldName") || qName.equals("userFieldDescription") || qName.equals("userFieldStyle") || qName.equals("userFieldTreeVis") || qName.equals("userFieldStandardValue") || qName.equals("userFieldInternalAccounting")
                || qName.equals("userFieldInternalAccountingWeightUserFieldHash") || qName.equals("userFieldFormula") || qName.equals("userFieldFormatString") || qName.equals("userFieldFormatHash")) {
            if (field == null) {
                throw new SAXException("Error while parsing definition of userFields: userFiel shouldn't not be equals to null");
            }
            field.putXMLFieldString(qName, elementValue.toString());
        } else {
            throw new SAXException("Unknown xml-tag: " + qName);
        }
    }

    @Override
    public void characters(final char[] arg0, final int arg1, final int arg2) throws SAXException {
        elementValue.append(String.valueOf(arg0, arg1, arg2));
    }

    @Override
    public void ignorableWhitespace(final char[] arg0, final int arg1, final int arg2) throws SAXException {
    }

    @Override
    public void processingInstruction(final String arg0, final String arg1) throws SAXException {
    }

    @Override
    public void skippedEntity(final String arg0) throws SAXException {
    }
}

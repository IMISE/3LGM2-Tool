/*
 * Created on 25.11.2003 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.userfield;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldNumberFormat;

/**
 * Die Variablen sind auf protected Gesetzt, damit man einen neuen
 * ContentHandler von dieser Klasse ableiten kann aber trotzdem noch Zugriff auf
 * alle nötigen Werte hat. Ich denke, bei kleinen Änderungen (hinzukommen oder
 * wegfallen einzelnener Felder im Dokument) muß man keinen ganz neuen
 * ContentHandler schreiben sondern muß nur einen abgeleiteten von diesem
 * bilden. Ich würde aber empfehlen von Zeit zu Zeit einen völlig neuen
 * ContentHandler zu schreiben.
 *
 * @author Thomas Rudert
 */
public class UserFieldXMLContentHandler implements ContentHandler {

    /**
     *
     */
    private final UserFieldDefinitions userFieldDefinitions;

    /**
     *
     */
    private UserField userField;

    /**
     *
     */
    private UserFieldNumberFormat userFieldNumberFormat;

    /**
     * String der in der characters Methode ausgelesen wird (Werte eines Tags)
     */
    private final StringBuilder elementValue = new StringBuilder();

    /**
     *
     */
    public UserFieldXMLContentHandler(final UserFieldDefinitions def) {
        userFieldDefinitions = def;
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

        } else if (qName.equals("userFieldFormat")) {
            String id = atts.getValue("hash");
            userFieldNumberFormat = new UserFieldNumberFormat(id);

        } else if (qName.equals("userFieldDef")) {
            String elementClass = atts.getValue("elementClass");
            String userFieldID = atts.getValue("hash");
            MetaModel metaModel = userFieldDefinitions.getMetaModel();
            Class<? extends ModelElement> userFieldTargetClass = metaModel.getClassForName(elementClass);
            //bei Modellvariablen ist die Elementclass null
            userField = new UserField(userFieldTargetClass, userFieldID);
        } else if (qName.equals("userFieldName")) {

        } else if (qName.equals("userFieldDescription")) {

        } else if (qName.equals("userFieldStyle")) {

        } else if (qName.equals("userFieldStandardValue")) {

        } else if (qName.equals("userFieldTreeVis")) {

        } else if (qName.equals("userFieldShowDescriptionInDialog")) {

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
        if (qName.equals("userFieldDefinitions")) {

        } else if (qName.equals("userFieldFormat")) {
            if (userFieldNumberFormat == null) {
                throw new SAXException("Error while parsing definition of userFields: userFieldNumberFormat shouldn't not be null");
            } else {
                userFieldDefinitions.add(userFieldNumberFormat);
            }
            userFieldNumberFormat = null;

        } else if (qName.equals("userFieldDef")) {
            if (userField != null) {
                if (userField.getStyle() != null) { //File Version 3.9 UserField.Style.FORMAT is removed as UserField style -> ignore such UserFields
                    userFieldDefinitions.add(userField);
                }
            } else if (userFieldNumberFormat != null) {
                userFieldDefinitions.add(userFieldNumberFormat);
            } else {
                throw new SAXException("Error while parsing definition of userFields: userField shouldn't not be null");
            }
            userField = null;
            userFieldNumberFormat = null;

        } else if (qName.equals("userFieldName") || qName.equals("userFieldDescription") || qName.equals("userFieldStyle") || qName.equals("userFieldTreeVis") || qName.equals("userFieldShowDescriptionInDialog") || qName.equals("userFieldStandardValue")
                || qName.equals("userFieldInternalAccounting") || qName.equals("userFieldInternalAccountingWeightUserFieldHash") || qName.equals("userFieldFormula") || qName.equals("userFieldFormatHash")) {
            if (userField == null) {
                throw new SAXException("Error while parsing definition of userFields: userFielsshouldn't not be equals to null");
            }
            String value = elementValue.toString();
            if (qName.equals("userFieldStyle")) {
                //Style.NUMER was Style.CLASSIFICATION_NUMBER and
                //Style.FORMULA was Style.CLASSIFICATION_NUMBER_FORMULA
                if (value.equals("CLASSIFICATION_NUMBER")) {
                    value = Style.NUMBER.name();
                } else if (value.equals("CLASSIFICATION_NUMBER_FORMULA")) {
                    value = Style.FORMULA.name();
                } else if (value.equals("FORMAT")) {
                    //formats are no longer UserFields
                    value = null;
                }
            }
            if (value != null) {
                userField.putXMLFieldString(qName, value, userFieldDefinitions);
            }

        } else if (qName.equals("userFieldFormatString")) {
            if (userField != null) { // File Version 3.8
                String id = userField.getID();
                userFieldNumberFormat = new UserFieldNumberFormat(id);
                userField = null;
            }
            //file Version 3.9
            if (userFieldNumberFormat == null) {
                throw new SAXException("Error while parsing definition of userFields: userFieldNumberFormat shouldn't not be null");
            }
            String value = elementValue.toString();
            userFieldNumberFormat.putXMLFieldString(qName, value);

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

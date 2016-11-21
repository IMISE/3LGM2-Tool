/*
 * Created on 02.02.2004
 */
package de.imise.tool3lgm.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;

/**
 * @author thomas
 */
public class ToolContentHandlerV1_2 extends ToolContentHandlerV1_1 {

    /**
     * @param coll
     */
    public ToolContentHandlerV1_2(final GDCollection coll) {
        super(coll);
    }

    /**
     * setzt doc, container, collection, elementValue auf null;
     * 
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    @SuppressWarnings("deprecation")
    @Override
    public void endDocument() throws SAXException {
        Tool3lgm.tool.setProgressDialogStatusLabel(Tool3lgmConstants.getResString("label_convert"));
        doc._createNewEdgeClasses();
        doc._removeMultipleTraces();
        for (int i = 0; i < collection.getNumberOfSzenarios(); i++) {
            collection.getSzenario(i).initKnotContainers();
            collection.getSzenario(i).initTraceContainers();
            //			collection.getSzenario(i).refreshSpecialInfoTargets();
        }
        doc._refreshSubordinatedElementsInSzenarios();

        doc = null;
        containerWithIcon = null;
        collection = null;
        elementValue = null;
    }

    /**
     * es muss gewaehrleistet werden, dass elementValue.setLength(0) aufgerufen wird
     * 
     * @see de.imise.tool3lgm.xml.ToolContentHandlerV1_0#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (qName.equals("valign")) {
            elementValue.setLength(0);

        } else if (qName.equals("halign")) {
            elementValue.setLength(0);

        } else {
            super.startElement(namespaceURI, localName, qName, atts);
        }
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {

        if (qName.equals("valign")) {
            if (layout != null) {
                layout.valign = Integer.parseInt(elementValue.toString());
            }

        } else if (qName.equals("halign")) {
            if (layout != null) {
                layout.halign = Integer.parseInt(elementValue.toString());
            }

        } else {
            super.endElement(namespaceURI, localName, qName);
        }
    }
}

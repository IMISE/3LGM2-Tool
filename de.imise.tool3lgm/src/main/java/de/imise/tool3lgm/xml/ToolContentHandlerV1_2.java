/*
 * Created on 02.02.2004
 */
package de.imise.tool3lgm.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.TextPositionHorizontal;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.TextPositionVertical;

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
    @Override
    public void endDocument() throws SAXException {
        Static.setProgressDialogStatusLabel("label_convert");
        for (Szenario szen : collection.getSzenarios()) {
            szen.initNodeContainers();
            szen.initEdgeContainers();
            //szen.refreshSpecialInfoTargets();
        }
        doc = null;
        containerWithIcon = null;
        collection = null;
        elementValue = null;
    }

    /**
     * es muss gewaehrleistet werden, dass elementValue.setLength(0) aufgerufen
     * wird
     *
     * @see de.imise.tool3lgm.xml.ToolContentHandlerV1_0#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
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
                String elementValueString = elementValue.toString();
                //alte Dateien -> SwingContstants
                try {
                    int swingConstantValue = Integer.parseInt(elementValueString);
                    TextPositionVertical textPositionVertical = TextPositionVertical.getValueForSwingConstant(swingConstantValue);
                    layout.textPositionVertical = textPositionVertical;
                } catch (Exception ex) {
                    // ignore -> default alignment
                }
            }

        } else if (qName.equals("halign")) {
            String elementValueString = elementValue.toString();
            if (layout != null) {
                //alte Dateien -> SwingContstants
                try {
                    int swingConstantValue = Integer.parseInt(elementValueString);
                    TextPositionHorizontal textPositionHorizontal = TextPositionHorizontal.getValueForSwingConstant(swingConstantValue);
                    layout.textPositionHorizontal = textPositionHorizontal;
                } catch (Exception ex) {
                    // ignore -> default alignment
                }
            }

        } else {
            super.endElement(namespaceURI, localName, qName);
        }
    }
}

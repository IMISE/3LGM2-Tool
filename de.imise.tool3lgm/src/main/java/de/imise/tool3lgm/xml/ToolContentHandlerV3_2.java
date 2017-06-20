/*
 * Created on 06.02.2005
 */
package de.imise.tool3lgm.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.log.Log;

/**
 * für Dateiversion mit Knickpunkten
 *
 * @author Thomas Wendt
 */
public class ToolContentHandlerV3_2 extends ToolContentHandlerV3_1 {

    private boolean paste = false;
    private ArrayList<ElementContainer> pastedElements;

    /**
     * @param coll
     */
    public ToolContentHandlerV3_2(final GDCollection coll, final boolean paste) {
        super(coll);
        this.paste = paste;
        if (paste) {
            pastedElements = new ArrayList<ElementContainer>(5000);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            GraphDocument tmpDoc = doc;
            super.endDocument();
            tmpDoc.deselectAll(true);
            if (paste) {
                for (ElementContainer ec : pastedElements) {
                    tmpDoc.addToSelection(ec, 0);
                }
            }
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        elementValue.setLength(0);
        super.startElement(namespaceURI, localName, qName, atts);
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        if (qName.equals("element")) {
            if (element != null) {
                try {
                    if (!avoidDuplicates || element.getContainer(doc) == null) {
                        container = element.createContainer(doc);
                        int layer = element.layerFor();
                        if (layer < 0 || layer >= ModelConstants.LAYERS.length) {
                            throw new SAXException("ModelElement hat ungueltige Ebenenangabe! hash=" + element.getHashString() + "layerFor=" + element.layerFor());
                        }
                        doc.getLayer(element.layerFor()).add(container);
                    }
                    if (paste) {
                        pastedElements.add(container);
                    }
                    element = null;
                    container = null;
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    e.printStackTrace();
                }
            }
        } else {
            super.endElement(namespaceURI, localName, qName);
        }
    }
}

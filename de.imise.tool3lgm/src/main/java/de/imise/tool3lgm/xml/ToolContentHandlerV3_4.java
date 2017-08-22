/**
 *
 */
package de.imise.tool3lgm.xml;

import org.xml.sax.SAXException;

import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.log.Log;

/**
 * @author AXS
 */
public class ToolContentHandlerV3_4 extends ToolContentHandlerV3_2 {

    /**
     * @param coll
     * @param paste
     */
    public ToolContentHandlerV3_4(final GDCollection coll, final boolean paste) {
        super(coll, paste);
    }

    // kann man weglassen, solange hier nichts passiert
    //    @Override
    //    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
    //            elementValue.setLength(0);
    //
    //            if (qName.equals("layerHeight")) {
    //
    //            } else if (qName.equals("layerWidth")) {
    //
    //            } else if (qName.equals("fixlayersize")) {
    //
    //                //"degree" umbenannt in "angle"
    //            } else if (qName.equals("angle")) {
    //
    //            } else {
    //                super.startElement(namespaceURI, localName, qName, atts);
    //            }
    //    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        try {
            if (qName.equals("layerHeight")) {
                double pageSizeFactor = (double) Integer.parseInt(elementValue.toString()) / (double) GraphDocument.INITIAL_PAGE_HEIGHT;
                System.err.println(pageSizeFactor + " " + elementValue + " " + GraphDocument.INITIAL_PAGE_HEIGHT);
                if (pageSizeFactor > viewParameter.pageSizeFactor) {
                    viewParameter.pageSizeFactor = pageSizeFactor;
                }
                System.err.println("pageSizeFactor " + elementValue + " -> " + pageSizeFactor);
            } else if (qName.equals("layerWidth")) {
                double pageSizeFactor = (double) Integer.parseInt(elementValue.toString()) / (double) GraphDocument.INITIAL_PAGE_WIDTH;
                System.err.println(pageSizeFactor + " " + elementValue + " " + GraphDocument.INITIAL_PAGE_HEIGHT);
                if (pageSizeFactor > viewParameter.pageSizeFactor) {
                    viewParameter.pageSizeFactor = pageSizeFactor;
                }

            } else if (qName.equals("fixlayersize")) {
                //			viewParameter.fixLayerSize = Boolean.valueOf(elementValue.toString()).booleanValue();

            } else if (qName.equals("angle")) {
                String s = elementValue.toString();
                int i = Integer.parseInt(s);
                viewParameter.degree = i;

            } else {
                super.endElement(namespaceURI, localName, qName);
            }
        } catch (Exception e) {
            Log.show(Log.ERROR, e);
        }
    }

}

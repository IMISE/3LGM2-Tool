/*
 * Created on 06.02.2005
 */
package de.imise.tool3lgm.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.log.Log;

/**
 * @author Thomas Rudert
 *         new xml-tags: view with childs x, y, zoom, degree, shift and multiView
 */
public class ToolContentHandlerV3_1 extends ToolContentHandlerV3_0 {

    protected GraphViewParameter graphViewParameter = null;

    /**
     * @param coll
     */
    public ToolContentHandlerV3_1(final GDCollection coll) {
        super(coll);
    }

    /**
     * @param coll
     */
    public ToolContentHandlerV3_1(final GDCollection coll, final boolean paste) {
        super(coll, paste);
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        //        elementValue.setLength(0);
        //        if (qName.equals("x")) {
        //
        //        } else if (qName.equals("y")) {
        //
        //        } else if (qName.equals("zoom")) {
        //
        //        } else if (qName.equals("shift")) {
        //
        //        } else if (qName.equals("degree")) {
        //
        //        } else if (qName.equals("pageSizeFactor")) {
        //
        //        } else if (qName.equals("multiView")) {
        //
        //        } else if (qName.equals("selected")) {
        //
        //        } else if (qName.equals("activeLayer")) {
        //
        //        } else

        if (qName.equals("view")) {
            if (szenario instanceof Szenario) {
                graphViewParameter = ((Szenario) szenario).getGraphViewParameter();
            }
        } else {
            super.startElement(namespaceURI, localName, qName, atts);
        }
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        try {
            if (qName.equals("x") && graphViewParameter != null) {
                graphViewParameter.viewPositionX = Integer.parseInt(elementValue.toString());

            } else if (qName.equals("y") && graphViewParameter != null) {
                graphViewParameter.viewPositionY = Integer.parseInt(elementValue.toString());

            } else if (qName.equals("zoom")) {
                if (graphViewParameter == null) {
                    return;
                }
                graphViewParameter.zoom = Double.parseDouble(elementValue.toString());

            } else if (qName.equals("shift")) {
                graphViewParameter.layerGap = Integer.parseInt(elementValue.toString());

            } else if (qName.equals("degree")) {
                graphViewParameter.layerAngle = Integer.parseInt(elementValue.toString());

            } else if (qName.equals("pageSizeFactor")) {
                szenario.setPageSizeFactor(Double.parseDouble(elementValue.toString()));

            } else if (qName.equals("activeLayer")) {
                graphViewParameter.activeLayer = Integer.parseInt(elementValue.toString());

            } else if (qName.equals("multiView")) {
                graphViewParameter.multiView = Boolean.valueOf(elementValue.toString()).booleanValue();

            } else if (qName.equals("selected")) {
                graphViewParameter.selected = Boolean.valueOf(elementValue.toString()).booleanValue();

            } else if (qName.equals("view")) {
                graphViewParameter = null;

            } else {
                super.endElement(namespaceURI, localName, qName);
            }
        } catch (Exception e) {
            Log.show(Log.ERROR, e);
        }
    }

}

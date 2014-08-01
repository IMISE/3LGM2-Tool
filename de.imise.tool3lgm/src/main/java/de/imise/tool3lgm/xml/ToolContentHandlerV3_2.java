/*
 * Created on 06.02.2005
 *
 */
package de.imise.tool3lgm.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.node.ABKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.DBKonfiguration;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.KonfigurationContainer;
import de.imise.tool3lgm.log.Log;

/**
 * @author Thomas Wendt
 * 
 *         für Dateiversion mit Knickpunkten
 * 
 */
public class ToolContentHandlerV3_2 extends ToolContentHandlerV3_1 {

	private boolean paste = false;
	private ArrayList<ElementContainer> pastedElements;

	/**
	 * @param coll
	 */
	public ToolContentHandlerV3_2(GDCollection coll, boolean paste) {
		super(coll);
		this.paste = paste;
		if (paste)
			pastedElements = new ArrayList<ElementContainer>(5000);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.xml.ToolContentHandlerV3_0#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
		try {
			GraphDocument tmpDoc = doc;
			super.endDocument();
			tmpDoc.deselectAll(true);
			if (paste)
				for (ElementContainer ec : pastedElements)
					tmpDoc.addToSelection(ec, 0);
		} catch (Exception e) {
			Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see tool3lgm.xml.ToolContentHandlerV3_1#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		elementValue.setLength(0);
		super.startElement(namespaceURI, localName, qName, atts);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.xml.ToolContentHandlerV3_1#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equals("element")) {
			if (element != null)
				try {
					if (!avoidDuplicates || element.getContainer(doc) == null) {
						container = element.createContainer(doc);
						int layer = element.layerFor();
						if (layer < 0 || layer >= ModelConstants.LAYERS.length)
							throw new SAXException("ModelElement hat ungueltige Ebenenangabe! hash=" + element.getHashString() + "layerFor=" + element.layerFor());
						if (doc.getLayer(element.layerFor()).add(container) != null) {
							if (element instanceof ABKonfiguration)
								collection.addABKonf((KonfigurationContainer) container);
							else if (element instanceof DBKonfiguration)
								collection.addDBKonf((KonfigurationContainer) container);
						}
					}
					if (paste)
						pastedElements.add(container);
					element = null;
					container = null;
				} catch (Exception e) {
					Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
					e.printStackTrace();
				}
		} else
			super.endElement(namespaceURI, localName, qName);
	}
}

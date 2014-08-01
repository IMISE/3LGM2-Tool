/*
 * Created on 27.11.2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.xml;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

/**
 * @author Thomas Rudert
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ToolDTDHandler implements DTDHandler {

	/**
	 * 
	 */
	public ToolDTDHandler() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.DTDHandler#notationDecl(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void notationDecl(String arg0, String arg1, String arg2)
		throws SAXException {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.DTDHandler#unparsedEntityDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void unparsedEntityDecl(
		String arg0,
		String arg1,
		String arg2,
		String arg3)
		throws SAXException {
	}

}

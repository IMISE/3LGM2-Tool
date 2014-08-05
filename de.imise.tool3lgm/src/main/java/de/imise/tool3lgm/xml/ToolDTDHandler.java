/*
 * Created on 27.11.2003
 */
package de.imise.tool3lgm.xml;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

/**
 * @author Thomas Rudert
 */
public class ToolDTDHandler implements DTDHandler {

    /**
	 * 
	 */
    public ToolDTDHandler() {
        super();
    }

    @Override
    public void notationDecl(final String arg0, final String arg1, final String arg2) throws SAXException {
    }

    @Override
    public void unparsedEntityDecl(final String arg0, final String arg1, final String arg2, final String arg3) throws SAXException {
    }

}

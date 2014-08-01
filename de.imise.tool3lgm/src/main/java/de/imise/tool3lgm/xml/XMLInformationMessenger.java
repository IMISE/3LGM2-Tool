/*
 * Created on 06.02.2008
 *
 */
package de.imise.tool3lgm.xml;

import java.util.HashSet;
import java.util.Set;

import de.imise.util.swing.dialog.MultipleOptionPane;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;


/**
 * Stellt bei globalen Änderungen eine einheitliche Schnittstelle für alle ParserVersionen
 * zur Verfügung, über die Fehlermeldungen u. ä. ausgegeben werden kann.
 * 
 * @author astruebi
 */
public class XMLInformationMessenger {

	/**
	 * Merkt sich alle Modelle, für die die Warnung, dass Anwendungsbausteine nicht
	 * mehr instanziiert werden können bereits angezeigt wurde. 
	 */
	private static Set<GDCollection> AWBNotSupportedAlreadyShownModels = null;
	
	
	/**
	 * @param gdcol
	 * @param elementClass
	 * @param checkAlreadyShown
	 */
	public static void showElementNoLongerSupportedMessage(GDCollection gdcol, Class<? extends ModelElement> elementClass, boolean checkAlreadyShown){
		if (elementClass==Anwendungsbaustein.class)
			showAWBNotLongerSupportedMessage(gdcol, checkAlreadyShown);
	}
	
	/**
	 * @param gdcol
	 * @param checkAlreadyShown
	 */
	private static final void showAWBNotLongerSupportedMessage(GDCollection gdcol, boolean checkAlreadyShown){
		if (AWBNotSupportedAlreadyShownModels == null)
			AWBNotSupportedAlreadyShownModels = new HashSet<GDCollection>();
		if (checkAlreadyShown && AWBNotSupportedAlreadyShownModels.contains(gdcol))
			return;
		AWBNotSupportedAlreadyShownModels.add(gdcol);
		MultipleOptionPane.showInformationMessageDialog(Tool3lgm.tool, Tool3lgmConstants.getErrString("awb_no_longer_supported_title"), Tool3lgmConstants.getErrString("awb_no_longer_supported_message")); 
	}
	
	
}

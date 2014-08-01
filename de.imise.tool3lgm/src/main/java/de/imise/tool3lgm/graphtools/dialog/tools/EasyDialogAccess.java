package de.imise.tool3lgm.graphtools.dialog.tools;

import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;

import de.imise.util.swing.dialog.FontChooser;

import de.imise.tool3lgm.Tool3lgmConstants;

/**
 * Stellt einfachen Zugriff auf Dilaloge breit.<br>
 * Diese sind:<br>
 * <ul>
 * <li>FontChooser</li>
 * </ul>
 * 
 * 
 * @author AXS
 * created on 16.08.2007
 */
public class EasyDialogAccess {

	/**
	 * Zeigt einen <code>FontChooser</code> an und gibt den gewählten Font zurück.
	 * @param parent Parent des <code>FontChooser</code>s
	 * @param initialFont Beim Anzeigen des <code>FontChooser</code>s zurerst ausgewählter Font
	 * @return
	 */
	public static final Font getFontByChooser(JDialog parent, Font initialFont) {
		String previewText = "<center>3LGM²</center>"+ Tool3lgmConstants.getResString("schrift_warnung");
		return FontChooser.chooseFont(parent, initialFont, previewText);
	}

	/**
	 * Zeigt einen <code>FontChooser</code> an und gibt den gewählten Font zurück.
	 * @param parent Parent des <code>FontChooser</code>s
	 * @param initialFont Beim Anzeigen des <code>FontChooser</code>s zurerst ausgewählter Font
	 * @return
	 */
	public static final Font getFontByChooser(JFrame parent, Font initialFont) {
		String previewText = "<center>3LGM²</center>"+ Tool3lgmConstants.getResString("schrift_warnung");
		return FontChooser.chooseFont(parent, initialFont, previewText);
	}

	
}

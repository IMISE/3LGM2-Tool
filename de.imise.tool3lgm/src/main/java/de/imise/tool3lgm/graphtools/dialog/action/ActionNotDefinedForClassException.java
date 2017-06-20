/*
 * Created on 18.12.2007
 */
package de.imise.tool3lgm.graphtools.dialog.action;

/**
 * @author fstephan
 */
public class ActionNotDefinedForClassException extends Exception {

    /**
	 * 
	 */
    public ActionNotDefinedForClassException(final String panelClassName) {
        super("Die Aktion ist für " + panelClassName + " nicht definiert!");
    }

}

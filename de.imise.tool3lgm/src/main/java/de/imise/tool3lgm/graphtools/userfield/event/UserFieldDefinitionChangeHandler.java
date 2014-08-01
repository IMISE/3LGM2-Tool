/*
 * Created on 21.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.event;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.GraphDocumentAdapter;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.PropertyDialogUserFieldPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * @author AXS
 */
public abstract class UserFieldDefinitionChangeHandler extends GraphDocumentAdapter{

	/**
	 * Wenn diese Variable <code>true</code> ist, wird bei der Abfrage irgendeines Kennzahlformelwertes
	 * alles neu berechnet. Sie wird bei allen Änderungen am Modell <code>true</code>.
	 */
	private boolean reset = true;

	/** Das Modell für welches die UserField definiert sind */
	protected GDCollection gdcoll = null;

	/**
	 * @param gdcoll
	 */
	public UserFieldDefinitionChangeHandler(GDCollection gdcoll) {
		super();
		this.gdcoll = gdcoll;
	}

	/**
	 * @return Returns the gdcoll.
	 */
	public GDCollection getCollection() {
		return gdcoll;
	}
	
	/**
	 * Löscht alle Berechneten Werte, d.h. alle berechneten Werte werden inkonsitstent (null) gesetzt.
	 */
	protected abstract void clearCalculatedUserFieldValues();
	
	/**
	 * Über diese Funktion kann dem Calculator mitgeteilt werden, dass sich mind. eine Kennzahl 
	 * geändert hat. Nachdem alle Kennzahlen geändert wurden, kann dann die Funktion reset() 
	 * aufgerufen werden, in der alle Kennzahlen neu berechnet werden.
	 * Der Calculator ist selbst kein <code>GraphDocumentListener</code>, der auf <code>DATA_CHANGED</code>
	 *  hört, weil er sonst bei jeder Änderung immer alles neu berechen würde.
	 * Das <code>GraphDocument</code> setzt den <code>boolen reset</code> auf <code>true</code>,
	 * wenn das Kommando <code>SET_USER_FIELD_VALUE</code> ausgeführt wurde.
	 * Ein Aufruf der Funktion <code>reset()</code> nach dem <code>true</code>-setzen, führt dann
	 * tatsächlich zu der Neuberechnung. Das macht aber nicht das <code>GraphDocument</code>, sondern
	 * das muss man selber machen, nachdem man alle Kennzahlen geändert hat.
	 * 
	 * @see PropertyDialogUserFieldPanel#commit()
	 */
	public final void initReset(){
		reset = true;
	}
	
	/**
	 * Setzt alle berechneten Werte zurück
	 */
	public final void reset(){
		//wenn sich irgendwas geändert hatte, ist reset auf true
		if (reset){
			//alle berechneten Werte auf null zurücksetzen
			clearCalculatedUserFieldValues();
			reset = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#dataChanged(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void dataChanged(GraphDocument source) {
		initReset();
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#elementAdded(tool3lgm.graphtools.GraphDocument, tool3lgm.graphtools.view.container.ElementContainer)
	 */
	@Override
	public void elementAdded(GraphDocument source, ElementContainer element) {
		initReset();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.GraphDocumentListener#elementDeleted(tool3lgm.graphtools.GraphDocument, tool3lgm.graphtools.view.container.ElementContainer)
	 */
	@Override
	public void elementDeleted(GraphDocument source, ElementContainer element) {
		initReset();
	}
}

package de.imise.tool3lgm.event;

import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import de.imise.util.swing.event.ExtendedAction;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GDCommands;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.matrixview.TableInternalFrame;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.gui.AbstractInternalFrame;

/**
 * Oberklasse aller Actions in diesem Package. Ist die einzige nach außen hin sichtbare Klasse.
 * @author fstephan
 *
 */
public abstract class AbstractLGMAction extends ExtendedAction {
	
	/** Schlüssel für das Attribut <em>Prüfen ob ein {@link GraphDocument} ausgewählt ist</em> */
	public static final String ENABLED_WHEN_SELECTED_DOC_NOT_NULL_KEY = "DocNotNullKey";
	
	/** Schlüssel für den {@link ActionIdentifier} dieser Action */
	public static final String IDENTIFIER_KEY = "IdentifierKey";
	
	/** Key, für das auszuführende Kommando  */
	public static final String COMMAND_KEY = "CommandKey";
	
	/** Key, für das Argument des auszuführenden Kommandos */ 
	public static final String ARGUMENT_KEY = "ArgumentKey";
	
	/** Key für die {@link ModelElement}-Klasse die, durch diese Action erzeugt wird */
	public static final String ELEMENT_CLASS_KEY = "ElementClassKey";
	
	/** Liste aller {@link Action}s, die einen {@link KeyStroke} besitzen */
	protected static final ArrayList<StaticAction> KEYSTROKE_ACTIONS = new ArrayList<StaticAction>();
	
	AbstractLGMAction(String text, Icon smallIcon, Icon largeIcon, KeyStroke keyStroke, String shortDescription, String longDescription, String actionCommand, Boolean initialSelectionState) {
	    super(text, smallIcon, largeIcon, keyStroke, shortDescription, longDescription, actionCommand, initialSelectionState);
    }

	AbstractLGMAction(String text, Icon icon) {
	    this(text,icon,icon,null,null,null,null,null);
    }

	AbstractLGMAction(String text) {
	    this(text,null);
    }

	/** Gibt die gerade laufende Instanz von 3lgm wieder */
	Tool3lgm getTool() {
		return Tool3lgm.tool;
	}
	
	//TODO:+++ prüfen, ob das mit selectedDoc hier wirlich ok ist oder ob das richtige doc übergeben werden muss
	
	
	/** Gibt das momentan ausgewählte {@link GraphDocument} zurück */
	LGMGraphDocument getSelectedDoc() {
		Tool3lgm tool = getTool();
		//kommt beim Initialisieren des Tools und der Actions vor
		if (tool == null)
			return null;
		return tool.getSelectedDoc();
	}

	/** Gibt die momentan ausgewählte {@link GDCollection} zurück */
	GDCollection getSelectedCollection() {
		return getSelectedDoc().getCollection();
	}

	
	/** Gibt zurück, ob interne Frames existieren */
	boolean hasInternalFrames() {
		return getTool().getAllFrames().length > 0;
	}
	
	/** Gibt zurück, ob ein gültiges aktives Frame existiert */
	boolean hasActiveFrame() {
		AbstractInternalFrame f = getTool().getActiveFrame();
		return f != null && !(f instanceof TableInternalFrame);
	}
	
	/** Gibt das gerade aktivierte Frame zurück */
	AbstractInternalFrame getActiveFrame() {
		return getTool().getActiveFrame();
	}
	
	/** Benachrichtigt das Tool über eine Ändeung der Daten */
	void distributeDataChanged() {
		distributeOptionChange(GraphDocument.DATA_CHANGED);
	}
	
	/** Benachrichtigt das Tool über eine Ändeung der grafischen Darstellung der Elemente */
	void distributeElementGraphicsChanged() {
		distributeOptionChange(GraphDocument.ELEMENT_GRAPHICS_CHANGED);
	}
	
	/** Teilt dem Tool einen Wechsel der Ebenenansicht mit */
	void distributeViewChanged() {
		getTool().activeLayerChanged(getSelectedDoc());
	}
	
	/** Löst ein Neuzeichnen des Tools aus */
	void repaintTool() {
		getTool().repaint();
	}
	
	/** Erzeugt ein Element der spezifizierten Klasse */
	void createNode(Class<? extends ModelElement> elementClass) {
		getSelectedDoc().createKnotenWithContainer(elementClass,TransactionManager.STANDARD_PID);
	}
	
	/** Führt das spezifizierte Kommando im momentan selektierten {@link GraphDocument} aus */
	void exec(GDCommands command) {
		exec(command.name());
	}
	
	/** 
	 * Führt das spezifizierte Kommando mit dem spezifizierten Argument 
	 * im momentan selektierten {@link GraphDocument} aus 
	 */
	void exec(GDCommands command, Object argument) {
		exec(command.name() + ((argument != null) ? " " + argument.toString() : ""));
	}
	
	/** Führt den spezifizierten Kommando-<code>String</code> im momentan selektierten {@link GraphDocument} aus */
	private void exec(String commandString) {
		getSelectedDoc().exec(commandString, TransactionManager.STANDARD_PID);
	}
	
	/** Benachrichtigt das Tool über das Eintreten des spezifizierten Ereignisses */
	private void distributeOptionChange(int eventCode) {
		getTool().distributeOptionChange(eventCode);
	}

	/** Gibt die Liste aller {@link Action}s wieder, die einen {@link KeyStroke} besitzen */
    public static ArrayList<StaticAction> getKeyStrokeActions() {
    	return KEYSTROKE_ACTIONS;
    }
}

package de.imise.tool3lgm.event;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.imise.tool3lgm.graphtools.GDCommands;
import de.imise.tool3lgm.graphtools.GraphDocument;

/**
 * Von {@link StaticAction} abgeleitet Klasse zur Änderung von Layer- und Element-Layout.
 * <p>
 * Das im Konstruktor spezifizierte Kommando wird inklusive eventueller Argumente beim Auslösen
 * dieser {@link Action} im aktuellen {@link GraphDocument} ausgeführt.
 * 
 * @author fstephan
 */
class LayoutAction extends StaticAction {

	/** Das beim Auslösen dieser {@link Action} im {@link GraphDocument} ausgeführte Kommando */
	private GDCommands command;
	
	/** Argument für das auszuführende Kommando */
	private Integer argument;

	
	/**
	 * Konstruktor
	 * 
	 * @param identifier
	 * 			eindeutiger {@link ActionIdentifier} für diese Action
	 * @param command
	 * 			das beim Auslösen dieser {@link Action} im {@link GraphDocument} ausgeführte Kommando
	 * @param argument
	 * 			Argument für das auszuführende Kommando
	 */
	private LayoutAction(ActionIdentifier identifier, GDCommands command, Integer argument) {
		this(identifier,"",command,argument);
	}
	
	/**
	 * Konstruktor
	 * 
	 * @param identifier
	 * 			eindeutiger {@link ActionIdentifier} für diese Action
	 * @param command
	 * 			das beim Auslösen dieser {@link Action} im {@link GraphDocument} ausgeführte Kommando
	 * @param argument
	 * 			Argument für das auszuführende Kommando
	 * @param textSuffix
	 * 			Suffix für den Text der Action (z.B. "...")
	 */
	private LayoutAction(ActionIdentifier identifier, String textSuffix, GDCommands command, Integer argument) {
		super(identifier,textSuffix);
		this.command = command;
		this.argument = argument;
		putValue(COMMAND_KEY, command);
		putValue(ARGUMENT_KEY, argument);
	}

	/**
	 * Konstruktor
	 * 
	 * @param identifier
	 * 			eindeutiger {@link ActionIdentifier} für diese Action
	 * @param command
	 * 			das beim Auslösen dieser {@link Action} im {@link GraphDocument} ausgeführte Kommando
	 */
	private LayoutAction(ActionIdentifier identifier, GDCommands command) {
		this(identifier,command,null);
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(!isEnabled())
			return;
		exec(command,argument);
	}

	
	/* ************************************ Start: Unterklassen *************************************** */
	
	// ///////////////////////////////////////////////
	// Aller Unterklassen funktionieren genauso wie //
	// LayoutAction. Sie unterscheiden sich nur in  //
	// ihren isEnabled() Methoden.					//
	//////////////////////////////////////////////////
	
	/**
	 * Von {@link LayoutAction} abgeleitet Klasse speziell für das Ändern des Ebenen-Layouts
	 * @author fstephan
	 */
	public static class LayerLayoutAction extends LayoutAction {

		public LayerLayoutAction(ActionIdentifier identifier, GDCommands command) {
			this(identifier, command, null);
		}
		
		public LayerLayoutAction(ActionIdentifier identifier, GDCommands command, Integer arguments) {
			this(identifier, "", command, arguments);
		}
		
		public LayerLayoutAction(ActionIdentifier identifier, String textSuffix, GDCommands command) {
			this(identifier, textSuffix,command, null);
		}
		
		public LayerLayoutAction(ActionIdentifier identifier, String textSuffix, GDCommands command, Integer arguments) {
			super(identifier, textSuffix, command, arguments);
		}

		@Override
		public boolean isEnabled() {
			return hasActiveFrame();
		}
	}

	/**
	 * Von {@link LayoutAction} abgeleitet Klasse speziell für das Ändern des Element-Layouts
	 * @author fstephan
	 */
	public static class ElementLayoutAction extends LayoutAction {

		public ElementLayoutAction(ActionIdentifier identifier, GDCommands command) {
			this(identifier, command, null);
		}
		
		public ElementLayoutAction(ActionIdentifier identifier, GDCommands command, Integer arguments) {
			this(identifier,"", command, arguments);
		}
		
		public ElementLayoutAction(ActionIdentifier identifier, String textSuffix, GDCommands command) {
			this(identifier, textSuffix, command, null);
		}
		
		public ElementLayoutAction(ActionIdentifier identifier, String textSuffix, GDCommands command, Integer arguments) {
			super(identifier, textSuffix, command, arguments);
			checkDoc(true);
		}

		@Override
		public boolean isEnabled() {
			return super.isEnabled() && getSelectedDoc().isSelectedAtLeastOneRealNode();
		}
	}
	
	/**
	 * Von {@link LayoutAction} abgeleitet Klasse speziell für das Ändern der Element-Ausrichtung
	 * @author fstephan
	 */
	public static class ElementAlignmentAction extends LayoutAction {
		
		public ElementAlignmentAction(ActionIdentifier identifier, GDCommands command) {
			super(identifier, command);
			checkDoc(true);
		}
		
		@Override
		public boolean isEnabled() {
			return super.isEnabled() && getSelectedDoc().isAlignable();
		}
	}
	
	/* ************************************ Ende: Unterklassen *************************************** */
}

package de.imise.tool3lgm.event;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;

import de.imise.tool3lgm.graphtools.GDCommands;
import de.imise.tool3lgm.graphtools.GraphDocument;

/**
 * Von {@link AbstractLGMAction} abgeleitete Klasse zu Ausführung von {@link GDCommands}
 * im aktiven {@link GraphDocument}.
 * @author fstephan
 *
 */
class CommandAction extends AbstractLGMAction {
	
	/** Das beim Auslösen dieser {@link Action} im {@link GraphDocument} ausgeführte Kommando */
	private GDCommands command;

	/** Argument für das auszuführende Kommando */
	protected String arguments;
	
	/**
	 * 
	 * @param command
	 */
	public CommandAction(GDCommands command) {
		this(command.name(), null,  command);
	}
	
	public CommandAction(String text, GDCommands command) {
		this(text, null,  command);
	}
	public CommandAction(String text, Icon icon, GDCommands command) {
		this(text, icon,  command, null, true);
	}
	
	public CommandAction(String text, Icon icon, GDCommands command, String argumentString, boolean isEnabled) {
		super(text,icon);
		this.command = command;
		this.arguments = argumentString;
		putValue(COMMAND_KEY, command);
		putValue(ARGUMENT_KEY, arguments);
		setEnabled(isEnabled);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		exec(command, arguments);	
    }
}

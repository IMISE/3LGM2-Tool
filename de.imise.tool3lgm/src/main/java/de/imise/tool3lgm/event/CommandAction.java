package de.imise.tool3lgm.event;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;

import de.imise.tool3lgm.graphtools.GDCommands;
import de.imise.tool3lgm.graphtools.GraphDocument;

/**
 * Von {@link AbstractLGMAction} abgeleitete Klasse zu Ausführung von {@link GDCommands} im aktiven
 * {@link GraphDocument}.
 * 
 * @author fstephan
 */
class CommandAction extends AbstractLGMAction {

    /** Das beim Auslösen dieser {@link Action} im {@link GraphDocument} ausgeführte Kommando */
    private final GDCommands command;

    /** Argument für das auszuführende Kommando */
    protected String arguments;

    /**
     * @param command
     */
    public CommandAction(final GDCommands command) {
        this(command.name(), null, command);
    }

    public CommandAction(final String text, final GDCommands command) {
        this(text, null, command);
    }

    public CommandAction(final String text, final Icon icon, final GDCommands command) {
        this(text, icon, command, null, true);
    }

    public CommandAction(final String text, final Icon icon, final GDCommands command, final String argumentString, final boolean isEnabled) {
        super(text, icon);
        this.command = command;
        arguments = argumentString;
        putValue(COMMAND_KEY, command);
        putValue(ARGUMENT_KEY, arguments);
        setEnabled(isEnabled);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        exec(command, arguments);
    }
}

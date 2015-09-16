package de.imise.tool3lgm.event;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.imise.tool3lgm.graphtools.GDCommands;
import de.imise.tool3lgm.graphtools.GraphDocument;

/**
 * Von {@link StaticAction} abgeleitet Klasse zur Änderung von Layer- und Element-Layout.
 * <p>
 * Das im Konstruktor spezifizierte Kommando wird inklusive eventueller Argumente beim Auslösen dieser {@link Action} im aktuellen
 * {@link GraphDocument} ausgeführt.
 * 
 * @author fstephan
 */
class LayoutAction extends StaticAction {

    /**
     * Von {@link LayoutAction} abgeleitet Klasse speziell für das Ändern der Element-Ausrichtung
     * 
     * @author fstephan
     */
    public static class ElementAlignmentAction extends LayoutAction {

        public ElementAlignmentAction(final ActionIdentifier identifier, final GDCommands command) {
            super(identifier, command);
            checkDoc(true);
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && getSelectedDoc().isAlignable();
        }
    }

    /**
     * Von {@link LayoutAction} abgeleitet Klasse speziell für das Ändern des Element-Layouts
     * 
     * @author fstephan
     */
    public static class ElementLayoutAction extends LayoutAction {

        public ElementLayoutAction(final ActionIdentifier identifier, final GDCommands command) {
            this(identifier, command, null);
        }

        public ElementLayoutAction(final ActionIdentifier identifier, final GDCommands command, final Integer arguments) {
            this(identifier, "", command, arguments);
        }

        public ElementLayoutAction(final ActionIdentifier identifier, final String textSuffix, final GDCommands command) {
            this(identifier, textSuffix, command, null);
        }

        public ElementLayoutAction(final ActionIdentifier identifier, final String textSuffix, final GDCommands command, final Integer arguments) {
            super(identifier, textSuffix, command, arguments);
            checkDoc(true);
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && getSelectedDoc().isSelectedAtLeastOneRealNode();
        }
    }

    /**
     * Von {@link LayoutAction} abgeleitet Klasse speziell für das Ändern des Ebenen-Layouts
     * 
     * @author fstephan
     */
    public static class LayerLayoutAction extends LayoutAction {

        public LayerLayoutAction(final ActionIdentifier identifier, final GDCommands command) {
            this(identifier, command, null);
        }

        public LayerLayoutAction(final ActionIdentifier identifier, final GDCommands command, final Integer arguments) {
            this(identifier, "", command, arguments);
        }

        public LayerLayoutAction(final ActionIdentifier identifier, final String textSuffix, final GDCommands command) {
            this(identifier, textSuffix, command, null);
        }

        public LayerLayoutAction(final ActionIdentifier identifier, final String textSuffix, final GDCommands command, final Integer arguments) {
            super(identifier, textSuffix, command, arguments);
        }

        @Override
        public boolean isEnabled() {
            return hasActiveFrame();
        }
    }

    /** Das beim Auslösen dieser {@link Action} im {@link GraphDocument} ausgeführte Kommando */
    private final GDCommands command;

    /** Argument für das auszuführende Kommando */
    private final Integer argument;

    /**
     * Konstruktor
     * 
     * @param identifier eindeutiger {@link ActionIdentifier} für diese Action
     * @param command das beim Auslösen dieser {@link Action} im {@link GraphDocument} ausgeführte
     *            Kommando
     */
    private LayoutAction(final ActionIdentifier identifier, final GDCommands command) {
        this(identifier, command, null);
    }

    /*
     * ************************************ Start: Unterklassen
     * ***************************************
     */

    // ///////////////////////////////////////////////
    // Aller Unterklassen funktionieren genauso wie //
    // LayoutAction. Sie unterscheiden sich nur in //
    // ihren isEnabled() Methoden. //
    // ////////////////////////////////////////////////

    /**
     * Konstruktor
     * 
     * @param identifier eindeutiger {@link ActionIdentifier} für diese Action
     * @param command das beim Auslösen dieser {@link Action} im {@link GraphDocument} ausgeführte
     *            Kommando
     * @param argument Argument für das auszuführende Kommando
     */
    private LayoutAction(final ActionIdentifier identifier, final GDCommands command, final Integer argument) {
        this(identifier, "", command, argument);
    }

    /**
     * Konstruktor
     * 
     * @param identifier eindeutiger {@link ActionIdentifier} für diese Action
     * @param command das beim Auslösen dieser {@link Action} im {@link GraphDocument} ausgeführte
     *            Kommando
     * @param argument Argument für das auszuführende Kommando
     * @param textSuffix Suffix für den Text der Action (z.B. "...")
     */
    private LayoutAction(final ActionIdentifier identifier, final String textSuffix, final GDCommands command, final Integer argument) {
        super(identifier, textSuffix);
        this.command = command;
        this.argument = argument;
        putValue(COMMAND_KEY, command);
        putValue(ARGUMENT_KEY, argument);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (!isEnabled()) {
            return;
        }
        exec(command, argument);
    }

    /*
     * ************************************ Ende: Unterklassen
     * ***************************************
     */
}

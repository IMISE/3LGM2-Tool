package de.imise.tool3lgm.event.action;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.util.MissingResourceException;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.util.swing.event.ExtendedAction;

/**
 * Von {@link ExtendedAction} abgeleitete Klasse, die eine global einsetzbare Action repräsentiert.
 * <p>
 * Jede Instanz dieser Klasse wird durch einen {@link ActionIdentifier} identifiziert über den alle Attribute, wie Name, Icon, etc. gesetzt werden.
 * <p>
 * Außerdem ist es möglich, ein Überprüfen des gerade ausgewählten {@link GraphDocument}s zu aktivieren, sowie einen initialen Selektionszustand für
 * die Anwendung bei {@link JCheckBoxMenuItem}s bzw. {@link JRadioButtonMenuItem}s zu setzen.<br>
 * Der Zugriff auf diese Attribute wird durch die Schlüssel {@link StaticAction#ENABLED_WHEN_SELECTED_DOC_NOT_NULL_KEY} bzw.
 * {@link Action#SELECTED_KEY} über {@link #getValue(String)} ermöglicht.
 *
 * @see ActionIdentifier
 * @see ExtendedAction
 * @author fstephan, AXS
 */
public abstract class StaticAction extends ExtendedAction {

    /** "..."-Suffix Actions */
    public static final String PPP = "...";

    /** Schlüssel für den {@link ActionIdentifier} oder das {@link GDCommands} dieser Action */
    public static final String IDENTIFIER_KEY = "IdentifierKey";

    /** Key, für das Argument des auszuführenden Kommandos */
    public static final String ARGUMENT_KEY = "ArgumentKey";

    public static final String CONFIRM_QUESTION_RESSOURCE_PREFIX = "CONFIRM_";
    public static final String TOOLTIP_RESSOURCE_PREFIX = "TOOLTIP_";
    public static final String ICON_LARGE_PREFIX = "ICON_LARGE_";
    public static final String ICON_SMALL_PREFIX = "ICON_SMALL_";

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser Klasse mit den dazugehörigen Attributen.
     *
     * @param identifier
     *            eindeutiger Identifier für diese Action
     */
    public StaticAction(final Object identifier) {
        this(identifier, null, null, null);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser Klasse mit den dazugehörigen Attributen.
     * <p>
     * Der Rückgabewert von {@link #isEnabled()} hängt von <code>enabledWhenSelectedDocNotNull</code>.
     *
     * @param identifier
     *            eindeutiger Identifier für diese Action
     * @param textSuffix
     *            Suffix für die Text-Property (in der Regel werden 3 Punkte angehängt, wenn die Aktion einen Dialog öffnet)
     */
    public StaticAction(final Object identifier, final String textSuffix) {
        this(identifier, null, null, textSuffix);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser Klasse mit den dazugehörigen Attributen.
     * <p>
     * Der initiale Selektionszustand wird auf den spezifizierten Wert gesetz und kann bei {@link JCheckBoxMenuItem}s bzw.
     * {@link JRadioButtonMenuItem}s genutzt werden. Die Action wird
     *
     * @param identifier
     *            eindeutiger Identifier für diese Action
     * @param arguments
     *            Argumente des Kommandos
     * @param text
     *            Anzeigetext der Action, wenn sie auf einem Button oder MenuItem liegt
     */
    public StaticAction(final Object identifier, final String arguments, final String text) {
        this(identifier, arguments, text, null);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser Klasse mit den dazugehörigen Attributen.
     * <p>
     * Der initiale Selektionszustand wird auf den spezifizierten Wert gesetz und kann bei {@link JCheckBoxMenuItem}s bzw.
     * {@link JRadioButtonMenuItem}s genutzt werden. Die Action wird
     *
     * @param identifier
     *            eindeutiger Identifier für diese Action
     * @param arguments
     *            Argumente des Kommandos
     * @param text
     *            Anzeigetext der Action, wenn sie auf einem Button oder MenuItem liegt
     * @param textSuffix
     *            Suffix für die Text-Property
     * @see de.imise.util.swing.event.ExtendedAction#isOptionAction()
     */
    public StaticAction(final Object identifier, final String arguments, final String text, final String textSuffix) {
        //wenn darunter das try-catch schief geht, dann ist der Text = dem übergebenen identifier.toString()
        super((text == null ? identifier.toString() : text) + (textSuffix != null ? textSuffix : ""));
        putValue(IDENTIFIER_KEY, identifier);

        String command = setActionCommand(identifier, arguments);
        setText(identifier, command, text, textSuffix);
        setIcons(command);
        setToolTip(command);
    }

    private static String getIdentifierName(final Object identifier) {
        return identifier instanceof Enum<?> ? ((Enum<?>) identifier).name() : identifier.toString();
    }

    private String setActionCommand(final Object identifier, final String arguments) {
        //GDCommands überschreiben die toString() so, dass sie ordinal()
        //zurück liefern (damit die UNDO-REDO-Commands nicht so lang werden).
        //Deshalb muss man hier explizit die name()-Methode abfragen.
        String command = getIdentifierName(identifier);
        if (!Strings.isNullOrEmpty(arguments)) {
            putValue(ARGUMENT_KEY, arguments);
            setActionCommand(command + " " + arguments);
        } else {
            setActionCommand(command);
        }
        return command;
    }

    private void setText(final Object identifier, final String command, final String text, final String textSuffix) {
        //Text auf RessourcenString lesen, wenn keiner übergeben wurde und eine Ressource existiert (wenn keine da ist, bleibts
        //bei dem, was im super-Constructor gesetzt wurde)
        if (text == null) {
            String actionText;
            try {
                actionText = getResString(command);
            } catch (MissingResourceException e) {
                try {
                    String simpleIdentifierClassName = ((Class<?>) identifier).getSimpleName();
                    actionText = getResString(simpleIdentifierClassName);
                } catch (Exception ex) {
                    actionText = command;
                }
            }
            setText(actionText + (textSuffix != null ? textSuffix : ""));
        }
    }

    private void setIcons(final String command) {
        //LargeIcon laden (wenn vorhanden)
        String iconName = StaticAction.ICON_LARGE_PREFIX + command + ".gif";
        //erst localized suchen
        Icon icon = Tool3lgmConstants.getLocalizedIcon(iconName);
        if (icon != null) {
            setLargeIcon(icon);
        } else {
            setLargeIcon(Tool3lgmConstants.getIcon(iconName));
        }
        //SmallIcon laden (wenn vorhanden)
        iconName = StaticAction.ICON_SMALL_PREFIX + command + ".gif";
        icon = Tool3lgmConstants.getLocalizedIcon(iconName);
        if (icon != null) {
            setSmallIcon(icon);
        } else {
            setSmallIcon(Tool3lgmConstants.getIcon(iconName));
        }
    }

    /**
     * Ersetzt den originalText der Action mit Replacement-Markern durch die übergebenen Replacements.
     *
     * @param replacements
     * @see Tool3lgmConstants#getReplacedString(String, String...)
     */
    public final void setReplacedText(final String... replacements) {
        String text = getText();
        text = Tool3lgmConstants.getReplacedString(text, replacements);
        setText(text);
    }

    private void setToolTip(final String command) {
        //ToolTip laden (wenn vorhanden)
        try {
            setShortDescription(Tool3lgmConstants.getResString(TOOLTIP_RESSOURCE_PREFIX + command));
        } catch (MissingResourceException e) {
        }
    }

    @Override
    public boolean isEnabled() {
        //Das müssen alle statischen Unter-Actions abfragen, da es sonst zu NullPointern beim Init des Tools kommt
        return Static.getTool() != null;
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        if (!isEnabled()) {
            return;
        }
        Object identifier = getValue(IDENTIFIER_KEY);
        String confirmQuestionResKey = CONFIRM_QUESTION_RESSOURCE_PREFIX + getIdentifierName(identifier);
        boolean perform = false;
        try {
            String confirmQuestion = getResString(confirmQuestionResKey);
            //wenn es eine confirm-Question in den Resourcen gibt -> Confirm-Frage stellen
            int answer = JOptionPane.showConfirmDialog(Static.getTool(), confirmQuestion, getResString("confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                //wenn ja gedrückt wurde -> Action ausführen
                perform = true;
            }
        } catch (MissingResourceException ex) {
            //wenn es keine confirm-Question in den Resourcen gibt -> Action immer ausführen
            perform = true;
        }
        if (perform) {
            if (identifier instanceof GDCommands) {
                Object arguments = getValue(ARGUMENT_KEY);
                Static.getSelectedDoc().exec(identifier.toString() + (arguments != null ? " " + arguments : ""), TransactionManager.STANDARD_PID);
            }
            actionPerformed();
            actionPerformedWithEvent(e);
        }
    }

    protected void actionPerformed() {
        //diese Funktion können Unterklassen überschreiben und müssen das enabled nie wieder testen.
        //Da bei all diesen Action die ActionEvent-Souce egal ist, wird das Event auch nicht durchgereicht
    }

    protected void actionPerformedWithEvent(final ActionEvent e) {
        //diese Funktion können Unterklassen überschreiben und müssen das enabled nie wieder testen.
        //Diese Funktion sollte von allen Unterklassen überschrieben werden, die das Event brauchen
    }

}

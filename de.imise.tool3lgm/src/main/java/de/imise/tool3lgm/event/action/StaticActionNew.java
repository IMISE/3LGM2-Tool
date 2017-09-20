package de.imise.tool3lgm.event.action;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.util.MissingResourceException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.event.StaticAction;
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
public abstract class StaticActionNew extends ExtendedAction {

    /** "..."-Suffix Actions */
    private static final String PPP = "...";

    /** Schlüssel für den {@link ActionIdentifier} oder das {@link GDCommands} dieser Action */
    public static final String IDENTIFIER_KEY = "IdentifierKey";

    /** Key, für das Argument des auszuführenden Kommandos */
    public static final String ARGUMENT_KEY = "ArgumentKey";

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
    public StaticActionNew(final ActionIdentifier identifier) {
        this(identifier, false, true);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser Klasse mit den dazugehörigen Attributen.
     *
     * @param identifier
     *            eindeutiger Identifier für diese Action
     */
    public StaticActionNew(final GDCommands identifier) {
        this(identifier, null, null, false, true);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser Klasse mit den dazugehörigen Attributen.
     *
     * @param identifier
     *            eindeutiger Identifier für diese Action
     * @param arguments
     *            Arguente für das Ausführen dieser Action
     */
    public StaticActionNew(final GDCommands identifier, final String arguments, final String text) {
        this(identifier, arguments, text, false, true);
    }

    @Override
    public boolean isEnabled() {
        //Das müssen alle statischen Unter-Actions abfragen, da es sonst zu NullPointern beim Init des Tools kommt
        return Static.getTool() != null;
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
     * @param appendThreePoints
     *            Suffix für die Text-Property <br>
     *            = <code>true</code>: {@link #isEnabled()} gibt <code>true</code> zurück, wenn ein selektiertes {@link GraphDocument} existiert,
     *            sonst <code>false</code> <br>
     *            = <code>false</code>: {@link #isEnabled()} gibt Standardwert zurück (durch {@link AbstractAction} bestimmt)
     */
    public StaticActionNew(final ActionIdentifier identifier, final Boolean appendThreePoints) {
        this(identifier, appendThreePoints, null);
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
     * @param appendThreePoints
     *            Suffix für die Text-Property <br>
     *            = <code>true</code>: {@link #isEnabled()} gibt <code>true</code> zurück, wenn ein selektiertes {@link GraphDocument} existiert,
     *            sonst <code>false</code> <br>
     *            = <code>false</code>: {@link #isEnabled()} gibt Standardwert zurück (durch {@link AbstractAction} bestimmt)
     */
    public StaticActionNew(final GDCommands identifier, final Boolean appendThreePoints) {
        this(identifier, null, null, appendThreePoints, null);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser Klasse mit den dazugehörigen Attributen.
     * <p>
     * Der Rückgabewert von {@link #isEnabled()} hängt von <code>enabledWhenSelectedDocNotNull</code>.
     * <p>
     * Der initiale Selektionszustand wird auf den spezifizierten Wert gesetz und kann bei {@link JCheckBoxMenuItem}s bzw.
     * {@link JRadioButtonMenuItem}s genutzt werden.
     *
     * @param identifier
     *            eindeutiger Identifier für diese Action
     * @param textSuffix
     *            Suffix für die Text-Property
     * @param enabledWhenSelectedDocNotNull
     *            <br>
     *            = <code>true</code>: {@link #isEnabled()} gibt <code>true</code> zurück, wenn ein selektiertes {@link GraphDocument} existiert,
     *            sonst <code>false</code> <br>
     *            = <code>false</code>: {@link #isEnabled()} gibt Standardwert zurück (durch {@link AbstractAction} bestimmt)
     * @param initialSelectionState
     *            initialer Selektionszustand
     */
    public StaticActionNew(final ActionIdentifier identifier, final Boolean appendThreePoints, final Boolean initialSelectionState) {
        this(identifier, null, null, appendThreePoints, initialSelectionState);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser Klasse mit den dazugehörigen Attributen.
     * <p>
     * Der Rückgabewert von {@link #isEnabled()} hängt von <code>enabledWhenSelectedDocNotNull</code>.
     * <p>
     * Der initiale Selektionszustand wird auf den spezifizierten Wert gesetz und kann bei {@link JCheckBoxMenuItem}s bzw.
     * {@link JRadioButtonMenuItem}s genutzt werden.
     *
     * @param identifier
     *            eindeutiger Identifier für diese Action
     * @param textSuffix
     *            Suffix für die Text-Property
     * @param enabledWhenSelectedDocNotNull
     *            <br>
     *            = <code>true</code>: {@link #isEnabled()} gibt <code>true</code> zurück, wenn ein selektiertes {@link GraphDocument} existiert,
     *            sonst <code>false</code> <br>
     *            = <code>false</code>: {@link #isEnabled()} gibt Standardwert zurück (durch {@link AbstractAction} bestimmt)
     * @param initialSelectionState
     *            initialer Selektionszustand
     */
    private StaticActionNew(final Object identifier, final String arguments, final String text, final Boolean appendThreePoints, final Boolean initialSelectionState) {
        //wenn darunter das try-catch schief geht, dann ist der Text = dem übergebenen identifier.toString()
        super(text == null ? identifier.toString() : text);
        putValue(IDENTIFIER_KEY, identifier);

        String command = identifier.toString();
        if (!Strings.isNullOrEmpty(arguments)) {
            putValue(ARGUMENT_KEY, arguments);
            setActionCommand(command + " " + arguments);
        } else {
            setActionCommand(command);
        }

        setSelected(initialSelectionState);

        //Text auf RessourcenString legen, wenn keiner übergeben wurde udn eine Ressource existiert (wenn keine da ist blebts bei dem, was in der
        //ersten Zeile gesetzt wurde)
        if (text == null) {
            try {
                String resString = getResString(command);
                setText(resString + (appendThreePoints ? PPP : ""));
            } catch (MissingResourceException e) {
                try {
                    String simpleIdentifierClassName = ((Class<?>) identifier).getSimpleName();
                    String resString = getResString(simpleIdentifierClassName);
                    setText(resString + (appendThreePoints ? PPP : ""));
                } catch (Exception ex) {
                    setText(command);
                }
            }
        }

        //LargeIcon laden (wenn vorhanden)
        Icon icon = Tool3lgmConstants.getLocalizedIcon(StaticActionNew.ICON_LARGE_PREFIX + identifier + ".gif");
        if (icon != null) {
            setLargeIcon(icon);
        } else {
            setLargeIcon(Tool3lgmConstants.getIcon(StaticActionNew.ICON_LARGE_PREFIX + identifier + ".gif"));
        }
        //SmallIcon laden (wenn vorhanden)
        icon = Tool3lgmConstants.getLocalizedIcon(StaticActionNew.ICON_SMALL_PREFIX + identifier + ".gif");
        if (icon != null) {
            setSmallIcon(icon);
        } else {
            setSmallIcon(Tool3lgmConstants.getIcon(StaticActionNew.ICON_SMALL_PREFIX + identifier + ".gif"));
        }
        //ToolTip laden (wenn vorhanden)
        try {
            setShortDescription(Tool3lgmConstants.getResString(TOOLTIP_RESSOURCE_PREFIX + identifier));
        } catch (MissingResourceException e) {
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (!isEnabled()) {
            return;
        }
        Object identifier = getValue(IDENTIFIER_KEY);
        if (identifier instanceof GDCommands) {
            Object arguments = getValue(ARGUMENT_KEY);
            Static.getSelectedDoc().exec(identifier.toString() + (arguments != null ? " " + arguments : ""), TransactionManager.STANDARD_PID);
        }
    }

}

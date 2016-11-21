package de.imise.tool3lgm.event;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;

import de.imise.tool3lgm.graphtools.GraphDocument;

/**
 * Von {@link AbstractLGMAction} abgeleitete Klasse, die eine global einsetzbare Action
 * repräsentiert.
 * <p>
 * Jede Instanz dieser Klasse wird durch einen {@link ActionIdentifier} identifiziert über den alle
 * Attribute, wie Name, Icon, etc. gesetzt werden.
 * <p>
 * Außerdem ist es möglich, ein Überprüfen des gerade ausgewählten {@link GraphDocument}s zu
 * aktivieren, sowie einen initialen Selektionszustand für die Anwendung bei
 * {@link JCheckBoxMenuItem}s bzw. {@link JRadioButtonMenuItem}s zu setzen.<br>
 * Der Zugriff auf diese Attribute wird durch die Schlüssel
 * {@link StaticAction#ENABLED_WHEN_SELECTED_DOC_NOT_NULL_KEY} bzw. {@link Action#SELECTED_KEY} über
 * {@link #getValue(String)} ermöglicht.
 * 
 * @see ActionIdentifier
 * @see AbstractLGMAction
 * @author fstephan
 */
public abstract class StaticAction extends AbstractLGMAction {

    /**
     * Gibt an, ob für {@link #isEnabled()} geprüft werden soll, ob ein selektiertes
     * {@link GraphDocument} existiert.
     */
    private boolean enabledWhenSelectedDocNotNull;

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser
     * Klasse mit den dazugehörigen Attributen.
     * 
     * @param identifier eindeutiger Identifier für diese Action
     */
    public StaticAction(final ActionIdentifier identifier) {
        this(identifier, false);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser
     * Klasse mit den dazugehörigen Attributen.
     * <p>
     * Der Rückgabewert von {@link #isEnabled()} hängt von
     * <code>enabledWhenSelectedDocNotNull</code>.
     * 
     * @param identifier eindeutiger Identifier für diese Action
     * @param enabledWhenSelectedDocNotNull <br>
     *            = <code>true</code>: {@link #isEnabled()} gibt <code>true</code> zurück, wenn ein
     *            selektiertes {@link GraphDocument} existiert, sonst <code>false</code> <br>
     *            = <code>false</code>: {@link #isEnabled()} gibt Standardwert zurück (durch
     *            {@link AbstractAction} bestimmt)
     */
    public StaticAction(final ActionIdentifier identifier, final boolean enabledWhenSelectedDocNotNull) {
        this(identifier, enabledWhenSelectedDocNotNull, (Boolean) null);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser
     * Klasse mit den dazugehörigen Attributen.
     * <p>
     * Der Rückgabewert von {@link #isEnabled()} hängt von
     * <code>enabledWhenSelectedDocNotNull</code>.
     * <p>
     * Der initiale Selektionszustand wird auf den spezifizierten Wert gesetz und kann bei
     * {@link JCheckBoxMenuItem}s bzw. {@link JRadioButtonMenuItem}s genutzt werden.
     * 
     * @param identifier eindeutiger Identifier für diese Action
     * @param enabledWhenSelectedDocNotNull <br>
     *            = <code>true</code>: {@link #isEnabled()} gibt <code>true</code> zurück, wenn ein
     *            selektiertes {@link GraphDocument} existiert, sonst <code>false</code> <br>
     *            = <code>false</code>: {@link #isEnabled()} gibt Standardwert zurück (durch
     *            {@link AbstractAction} bestimmt)
     * @param initialSelectionState initialer Selektionszustand
     */
    public StaticAction(final ActionIdentifier identifier, final boolean enabledWhenSelectedDocNotNull, final Boolean initialSelectionState) {
        this(identifier, "", enabledWhenSelectedDocNotNull, initialSelectionState);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser
     * Klasse mit den dazugehörigen Attributen.
     * <p>
     * Der initiale Selektionszustand wird auf den spezifizierten Wert gesetz und kann bei
     * {@link JCheckBoxMenuItem}s bzw. {@link JRadioButtonMenuItem}s genutzt werden.
     * 
     * @param identifier eindeutiger Identifier für diese Action
     * @param initialSelectionState initialer Selektionszustand
     */
    public StaticAction(final ActionIdentifier identifier, final Boolean initialSelectionState) {
        this(identifier, false, initialSelectionState);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser
     * Klasse mit den dazugehörigen Attributen.
     * 
     * @param identifier eindeutiger Identifier für diese Action
     * @param textSuffix Suffix für die Text-Property
     */
    public StaticAction(final ActionIdentifier identifier, final String textSuffix) {
        this(identifier, textSuffix, false);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser
     * Klasse mit den dazugehörigen Attributen.
     * <p>
     * Der Rückgabewert von {@link #isEnabled()} hängt von
     * <code>enabledWhenSelectedDocNotNull</code>.
     * 
     * @param identifier eindeutiger Identifier für diese Action
     * @param textSuffix Suffix für die Text-Property
     * @param enabledWhenSelectedDocNotNull <br>
     *            = <code>true</code>: {@link #isEnabled()} gibt <code>true</code> zurück, wenn ein
     *            selektiertes {@link GraphDocument} existiert, sonst <code>false</code> <br>
     *            = <code>false</code>: {@link #isEnabled()} gibt Standardwert zurück (durch
     *            {@link AbstractAction} bestimmt)
     */
    public StaticAction(final ActionIdentifier identifier, final String textSuffix, final boolean enabledWhenSelectedDocNotNull) {
        this(identifier, textSuffix, enabledWhenSelectedDocNotNull, (Boolean) null);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser
     * Klasse mit den dazugehörigen Attributen.
     * <p>
     * Der Rückgabewert von {@link #isEnabled()} hängt von
     * <code>enabledWhenSelectedDocNotNull</code>.
     * <p>
     * Der initiale Selektionszustand wird auf den spezifizierten Wert gesetz und kann bei
     * {@link JCheckBoxMenuItem}s bzw. {@link JRadioButtonMenuItem}s genutzt werden.
     * 
     * @param identifier eindeutiger Identifier für diese Action
     * @param textSuffix Suffix für die Text-Property
     * @param enabledWhenSelectedDocNotNull <br>
     *            = <code>true</code>: {@link #isEnabled()} gibt <code>true</code> zurück, wenn ein
     *            selektiertes {@link GraphDocument} existiert, sonst <code>false</code> <br>
     *            = <code>false</code>: {@link #isEnabled()} gibt Standardwert zurück (durch
     *            {@link AbstractAction} bestimmt)
     * @param initialSelectionState initialer Selektionszustand
     */
    public StaticAction(final ActionIdentifier identifier, final String textSuffix, final boolean enabledWhenSelectedDocNotNull, final Boolean initialSelectionState) {
        super(identifier.getText().concat(textSuffix), identifier.getSmallIcon(), identifier.getLargeIcon(), identifier.getKeyStroke(), identifier.getShortDescription(), identifier.getLongDescription(), identifier.getActionCommand(),
                initialSelectionState);

        if (identifier.getKeyStroke() != null) {
            KEYSTROKE_ACTIONS.add(this);
        }

        putValue(IDENTIFIER_KEY, identifier);
        checkDoc(enabledWhenSelectedDocNotNull);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine durch den spezifizierten {@link ActionIdentifier} identifizierte Instanz dieser
     * Klasse mit den dazugehörigen Attributen.
     * <p>
     * Der initiale Selektionszustand wird auf den spezifizierten Wert gesetz und kann bei
     * {@link JCheckBoxMenuItem}s bzw. {@link JRadioButtonMenuItem}s genutzt werden.
     * 
     * @param identifier eindeutiger Identifier für diese Action
     * @param textSuffix Suffix für die Text-Property
     * @param initialSelectionState initialer Selektionszustand
     */
    public StaticAction(final ActionIdentifier identifier, final String textSuffix, final Boolean initialSelectionState) {
        this(identifier, textSuffix, false, initialSelectionState);
    }

    /**
     * (De-)Aktiviert das Überwachen des selektierten {@link GraphDocument}s.
     * 
     * @see {@link #enabledWhenSelectedDocNotNull}
     */
    public void checkDoc(final boolean enabledWhenSelectedDocNotNull) {
        this.enabledWhenSelectedDocNotNull = enabledWhenSelectedDocNotNull;
        putValue(ENABLED_WHEN_SELECTED_DOC_NOT_NULL_KEY, enabledWhenSelectedDocNotNull);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.AbstractAction#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        if (enabledWhenSelectedDocNotNull) {
            return getSelectedDoc() != null;
        }
        return super.isEnabled();
    }

}

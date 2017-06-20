package de.imise.util.swing.event;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import de.imise.util.Pair;

/**
 * Erweiterungsklasse zu {@link AbstractAction}
 * <p>
 * Ermöglich den Zugriff auf bzw. das Setzen der durch {@link Action} gegebenen Properties über get- bzw. set-Methoden. <br>
 * 
 * @author fstephan, AXS
 */
public abstract class ExtendedAction extends AbstractAction {

    /** Source für das Ausführen der Aktion über ein neu generiertes {@link ActionEvent} */
    private static JButton button = new JButton();

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine Instanz dieser Klasse mit den spezifizierten Property-Werten ({@link Action}). <br>
     * Die Mnemonic-Property wird hier ausgeschlossen, da sie üblicherweise innerhalb von {@link AbstractButton}-Gruppen gemeinsam gesetzt wird und
     * beim Erzeugen der Action noch nicht bekannt ist.
     * </p>
     * 
     * @param text
     * @param smallIcon
     * @param largeIcon
     * @param keyStroke
     * @param shortDescription
     * @param longDescription
     * @param actionCommand
     * @param initialSelectionState
     *            Initialer Selektionszustand für die Verwendung bei {@link JCheckBoxMenuItem}s und {@link JRadioButtonMenuItem}s
     */
    public ExtendedAction(final String text, final Icon smallIcon, final Icon largeIcon, final KeyStroke keyStroke, final String shortDescription, final String longDescription, final String actionCommand, final Boolean initialSelectionState) {
        this(text, smallIcon);
        setLargeIcon(largeIcon);
        setKeyStroke(keyStroke);
        setShortDescription(shortDescription);
        setLongDescription(longDescription);
        setActionCommand(actionCommand);
        setSelected(initialSelectionState != null ? initialSelectionState : false);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine Instanz dieser Klasse mit den spezifizierten Property-Werten ({@link Action}).
     * </p>
     * 
     * @param text
     * @param smallIcon
     */
    public ExtendedAction(final String text, final Icon smallIcon) {
        super(text, smallIcon);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine Instanz dieser Klasse mit dem spezifizierten Property-Wert ({@link Action}).
     * </p>
     * 
     * @param text
     */
    public ExtendedAction(final String text) {
        super(text);
    }

    /**
     * Konstruktor
     */
    public ExtendedAction() {
        super();
    }

    /**
     * Setzt den anzuzeigenden Text
     * 
     * @param
     */
    public void setText(final String text) {
        if (text != null) {
            putValue(NAME, text);
        }
    }

    /**
     * Setzt das kleine Icon
     * 
     * @param smallIcon
     */
    public void setSmallIcon(final Icon smallIcon) {
        if (smallIcon != null) {
            putValue(SMALL_ICON, smallIcon);
        }
    }

    /**
     * Setzt das große Icon
     * 
     * @param largeIcon
     */
    public void setLargeIcon(final Icon largeIcon) {
        if (largeIcon != null) {
            putValue(LARGE_ICON_KEY, largeIcon);
        }
    }

    /**
     * Setzt den {@link KeyStroke}, der diese Action auslöst
     * 
     * @param keyStroke
     */
    public void setKeyStroke(final KeyStroke keyStroke) {
        if (keyStroke != null) {
            putValue(ACCELERATOR_KEY, keyStroke);
        }
    }

    /**
     * Setzt den Index des Mnemonic im Text
     * 
     * @see #setText(String)
     * @param mnemonicIndex
     */
    public void setMnemonicIndex(final int mnemonicIndex) {
        putValue(DISPLAYED_MNEMONIC_INDEX_KEY, new Integer(mnemonicIndex));
    }

    /**
     * Setzt kurze Beschreibung der Action. (Zur Verwendung als Tooltip)
     * 
     * @param description
     */
    public void setShortDescription(final String description) {
        if (description != null) {
            putValue(SHORT_DESCRIPTION, description);
        }
    }

    /**
     * Setzt ausführliche Beschreibung der Action. (Zur Verwendung bei Hilfe)
     * 
     * @param description
     */
    public void setLongDescription(final String description) {
        if (description != null) {
            putValue(LONG_DESCRIPTION, description);
        }
    }

    /**
     * Setzt den command-<code>String</code> für das {@link ActionEvent}, das beim Auslösen dieser Action entsteht.
     * 
     * @param actionCommand
     */
    public void setActionCommand(final String actionCommand) {
        putValue(ACTION_COMMAND_KEY, actionCommand);
    }

    /**
     * Setzt den Selektionszustand dieser Action. (Zur Verwendung bei {@link JRadioButtonMenuItem} und {@link JCheckBoxMenuItem})
     * 
     * @param b
     */
    public void setSelected(final boolean b) {
        putValue(SELECTED_KEY, b);
    }

    /**
     * Setzt den Selektionszustand dieser Action. (Zur Verwendung bei {@link JRadioButtonMenuItem} und {@link JCheckBoxMenuItem}). Bei
     * <code>null</code> wird <code>false</code> gesetzt.
     * 
     * @param b
     */
    public void setSelected(final Boolean b) {
        setSelected(b != null ? b : false);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.AbstractAction#putValue(java.lang.String, java.lang.Object)
     */
    @Override
    public void putValue(final String key, final Object value) {
        super.putValue(key, value);
    }

    /**
     * Liefert für den key {@link Action#SELECTED_KEY} den Wert von {@link ExtendedAction#isSelected()} zurück, verhält
     * sich sonst aber wie {@link AbstractAction#getValue(String)}. <br>
     * Durch Überschreiben von {@link ExtendedAction#isSelected()} kann der Selektionzustand dynamisch festgelegt
     * werden, sodass ein manuelles Setzen mittels {@link ExtendedAction#putValue(String, Object)} entfällt.
     * 
     * @see javax.swing.AbstractAction#getValue(java.lang.String)
     */
    @Override
    public Object getValue(final String key) {
        // Ermöglicht das Überschreiben von isSelected() sodass beim Überprüfen
        // des Selektionszustandes dieser Action durch eine Component,
        // der Wert der isSelected()-Methode genommen wird, statt
        // dem Wert im ArrayTable.
        // Wird isSelected() nicht überschrieben, bleibt das Standardverhalten erhalten,
        // d.h. es wird der Wert aus dem ArrayTable zurückgegeben.
        if (key == SELECTED_KEY) {
            return isSelected();
        }
        return super.getValue(key);
    }

    /**
     * Gibt den anzuzeigenden Text wieder
     * 
     * @param
     */
    public String getText() {
        return (String) getValue(NAME);
    }

    /**
     * Gibt das kleine Icon wieder
     * 
     * @return
     */
    public Icon getSmallIcon() {
        return (Icon) getValue(SMALL_ICON);
    }

    /**
     * Gibt das große Icon wieder
     * 
     * @return
     */
    public Icon getLargeIcon() {
        return (Icon) getValue(LARGE_ICON_KEY);
    }

    /**
     * Gibt den {@link KeyStroke} zurück, der diese Action auslöst
     * 
     * @return
     */
    public KeyStroke getKeyStroke() {
        return (KeyStroke) getValue(ACCELERATOR_KEY);
    }

    /**
     * Gibt den Index des Mnemonics im Text wieder, falls er gesetz wurde. Sonst wird <code>-1</code> zurückgegeben.
     * 
     * @see #getText()
     * @return
     */
    public int getMnemonicIndex() {
        Integer i = (Integer) getValue(DISPLAYED_MNEMONIC_INDEX_KEY);
        return i != null ? i : -1;
    }

    /**
     * Gibt kurze Beschreibung der Action zurück. (Zur Verwendung als Tooltip)
     * 
     * @param description
     */
    public String getShortDescription() {
        return (String) getValue(SHORT_DESCRIPTION);
    }

    /**
     * Gibt ausführliche Beschreibung der Action zurück. (Zur Verwendung bei Hilfe)
     * 
     * @param description
     */
    public String getLongDescription() {
        return (String) getValue(LONG_DESCRIPTION);
    }

    /**
     * Gibt den command-<code>String</code> des {@link ActionEvent}s wieder, das beim Auslösen dieser Action entsteht.
     * 
     * @param actionCommand
     */
    public String getActionCommand() {
        return (String) getValue(ACTION_COMMAND_KEY);
    }

    /**
     * Gibt den Selektionszustand dieser Action wieder. (Zur Verwendung bei {@link JRadioButtonMenuItem} und {@link JCheckBoxMenuItem}) <br>
     * Falls kein Selektionszustand gesetzt wurde, wird <code>false</code> zurückgegeben.
     * <p>
     * Durch Überschreiben dieser Methode kann der Selektionszustand dynamisch festgelegt werden. Allerdings ist dann eine Beeinflussung durch
     * {@link #putValue(String, Object)} nicht mehr möglich.
     * 
     * @param b
     */
    public boolean isSelected() {
        Boolean b = (Boolean) super.getValue(SELECTED_KEY);
        return b != null && b;
    }

    /**
     * Legt die spezifizierten {@link Pair}s auf die Attribute-Map von {@link AbstractAction}. <br>
     * 
     * @see AbstractAction#putValue(String, Object)
     * @param keysAndValues
     *            - erstes Item: key - zweites Item: value
     */
    public void putValues(final Pair<String, Object>... keysAndValues) {
        for (Pair<String, Object> keyAndValue : keysAndValues) {
            putValue(keyAndValue);
        }
    }

    /**
     * Legt das spezifiziert {@link Pair} auf die Attribute-Map von {@link AbstractAction}. <br>
     * 
     * @see AbstractAction#putValue(String, Object)
     * @param keysAndValues
     *            - erstes Item: key - zweites Item: value
     */
    public void putValue(final Pair<String, Object> keyAndValue) {
        putValue(keyAndValue.getFirstItem(), keyAndValue.getSecondItem());
    }

    /**
     * Entfernt alle Attributwerte dieser Action und setzt sie auf die Werte der spezifizierten Action. <br>
     * Achtung: Die Attributwerte werden hierbei nicht geclont!
     */
    public void setAllAttributes(final ExtendedAction source) {
        clearAttributes();
        addAttributesFrom(source);
    }

    /**
     * Setzt alle Attributwerte dieser Action auf die Attributwerte der spezifizierten Action.<br>
     * Achtung: Die Attributwerte werden hierbei nicht geclont!
     */
    public void addAttributesFrom(final ExtendedAction source) {
        Object[] keys = source.getKeys();
        for (Object key : keys) {
            putValue((String) key, source.getValue((String) key));
        }
    }

    /** Entfernt alle Attribute aus der Attribute-Map von {@link AbstractAction} */
    public void clearAttributes() {
        Object[] keys = getKeys();
        for (Object key : keys) {
            putValue((String) key, null);
        }
    }

    /** Führt die Aktion aus */
    public final void perform() {
        actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, getText()));
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getText() + "[");
        Object[] keys = getKeys();
        for (Object key : keys) {
            sb.append(key + "=\"" + getValue(key.toString()) + "\", ");
        }
        sb.append("]");
        sb.append("\n" + super.toString());
        return sb.toString();
    }
}

package de.imise.util.swing.event;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import com.google.common.base.Strings;

import de.imise.util.pair.Pair;

/**
 * Erweiterungsklasse zu {@link AbstractAction}
 * <p>
 * Ermöglich den Zugriff auf bzw. das Setzen der durch {@link Action} gegebenen
 * Properties über get- bzw. set-Methoden. <br>
 *
 * @author fstephan, AXS
 */
public abstract class ExtendedAction extends AbstractAction {

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine Instanz dieser Klasse mit den spezifizierten Property-Werten
     * ({@link Action}). <br>
     * Die Mnemonic-Property wird hier ausgeschlossen, da sie üblicherweise
     * innerhalb von {@link AbstractButton}-Gruppen gemeinsam gesetzt wird und
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
     */
    public ExtendedAction(final String text, final Icon smallIcon, final Icon largeIcon, final KeyStroke keyStroke, final String shortDescription, final String longDescription, final String actionCommand) {
        super(text, smallIcon);
        setLargeIcon(largeIcon);
        setKeyStroke(keyStroke);
        setShortDescription(shortDescription);
        setLongDescription(longDescription);
        setActionCommand(actionCommand);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine Instanz dieser Klasse mit den spezifizierten Property-Werten
     * ({@link Action}).
     * </p>
     *
     * @param text
     * @param smallIcon
     */
    public ExtendedAction(final String text, final Icon smallIcon) {
        this(text, smallIcon, null, null, null, null, null);
    }

    /**
     * Konstruktor
     * <p>
     * Erzeugt eine Instanz dieser Klasse mit dem spezifizierten Property-Wert
     * ({@link Action}).
     * </p>
     *
     * @param text
     */
    public ExtendedAction(final String text) {
        this(text, null, null, null, null, null, null);
    }

    /**
     * Konstruktor
     */
    public ExtendedAction() {
        this(null, null, null, null, null, null, null);
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
        putValue(DISPLAYED_MNEMONIC_INDEX_KEY, mnemonicIndex);
    }

    /**
     * Setzt kurze Beschreibung der Action. (Zur Verwendung als ToolTip)
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
     * Setzt den command-<code>String</code> für das {@link ActionEvent}, das
     * beim Auslösen dieser Action entsteht.
     *
     * @param actionCommand
     */
    public void setActionCommand(final String actionCommand) {
        putValue(ACTION_COMMAND_KEY, actionCommand);
    }

    @Override
    public void putValue(final String key, final Object value) {
        super.putValue(key, value);
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
     * Gibt den Index des Mnemonics im Text wieder, falls er gesetz wurde. Sonst
     * wird <code>-1</code> zurückgegeben.
     *
     * @see #getText()
     * @return
     */
    public int getMnemonicIndex() {
        Integer i = (Integer) getValue(DISPLAYED_MNEMONIC_INDEX_KEY);
        return i != null ? i : -1;
    }

    /**
     * Gibt kurze Beschreibung der Action zurück. (Zur Verwendung als ToolTip)
     *
     * @param description
     */
    public String getShortDescription() {
        return (String) getValue(SHORT_DESCRIPTION);
    }

    /**
     * Gibt ausführliche Beschreibung der Action zurück. (Zur Verwendung bei
     * Hilfe)
     *
     * @param description
     */
    public String getLongDescription() {
        return (String) getValue(LONG_DESCRIPTION);
    }

    /**
     * Gibt den command-<code>String</code> des {@link ActionEvent}s wieder, das
     * beim Auslösen dieser Action entsteht.
     *
     * @param actionCommand
     */
    public String getActionCommand() {
        return (String) getValue(ACTION_COMMAND_KEY);
    }

    /**
     * Legt die spezifizierten {@link Pair}s auf die Attribute-Map von
     * {@link AbstractAction}. <br>
     *
     * @see AbstractAction#putValue(String, Object)
     * @param keysAndValues - erstes Item: key - zweites Item: value
     */
    public void putValues(@SuppressWarnings("unchecked") final Pair<String, Object>... keysAndValues) {
        for (Pair<String, Object> keyAndValue : keysAndValues) {
            putValue(keyAndValue);
        }
    }

    /**
     * Legt das spezifiziert {@link Pair} auf die Attribute-Map von
     * {@link AbstractAction}. <br>
     *
     * @see AbstractAction#putValue(String, Object)
     * @param keysAndValues - erstes Item: key - zweites Item: value
     */
    public void putValue(final Pair<String, Object> keyAndValue) {
        putValue(keyAndValue.getFirstItem(), keyAndValue.getSecondItem());
    }

    /**
     * Entfernt alle Attributwerte dieser Action und setzt sie auf die Werte der
     * spezifizierten Action. <br>
     * Achtung: Die Attributwerte werden hierbei nicht geclont!
     */
    public void setAllAttributes(final ExtendedAction source) {
        clearAttributes();
        addAttributesFrom(source);
    }

    /**
     * Setzt alle Attributwerte dieser Action auf die Attributwerte der
     * spezifizierten Action.<br>
     * Achtung: Die Attributwerte werden hierbei nicht geclont!
     */
    public void addAttributesFrom(final ExtendedAction source) {
        Object[] keys = source.getKeys();
        for (Object key : keys) {
            putValue((String) key, source.getValue((String) key));
        }
    }

    /**
     * Entfernt alle Attribute aus der Attribute-Map von {@link AbstractAction}
     */
    public void clearAttributes() {
        Object[] keys = getKeys();
        for (Object key : keys) {
            putValue((String) key, null);
        }
    }

    /** Führt die Aktion aus */
    public final void perform() {
        actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getText()));
    }

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

    /**
     * Unterklassen können diese Funktion überschreiben und gleich ein passendes
     * MenuItem erzeugen
     */
    public JMenuItem createMenuItem() {
        JMenuItem item = new JMenuItem(this);
        item.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorRemoved(final AncestorEvent event) {
            }
            @Override
            public void ancestorMoved(final AncestorEvent event) {
            }
            @Override
            public void ancestorAdded(final AncestorEvent event) {
                //diese Funktion wird beim Anzeigen des MenuItems ausgelöst. Dabei muss der Selektionszustand
                //des Items noch einmal geprüft werden, falls die zu grunde liegende Property woanders als über
                //dieses Item geändert wurde
                //updateSelection();
                item.setEnabled(isEnabled());
            }
        });
        String toolTip = getShortDescription();
        if (!Strings.isNullOrEmpty(toolTip)) {
            item.setToolTipText(toolTip);
        }
        return item;
    }

}

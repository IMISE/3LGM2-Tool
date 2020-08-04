package de.imise.util.swing.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Action;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import de.imise.util.swing.component.text.TextComponentStandardPopup;

/**
 * Editierbare JComboBox, die sich in der Liste die lestzetn Eingaben merken kann.
 *
 * @author AXS, STKR
 */
public class HistoryComboBox extends JComboBox<String> implements KeyListener {

    /** Konstante für die Maximalzahl der Historyeinträge */
    private static final int MAX_HISTORY_ENTRIES = Integer.MAX_VALUE;

    /*** Maximale Anzahl von Einträgen in der Histotry-Liste */
    private int maxHistoryLength = MAX_HISTORY_ENTRIES;

    /** Die Aktion, die beim Drücken von Enter ausgeführt werden soll */
    private Action enterAction = null;

    /**
     *
     */
    public HistoryComboBox() {
        setEditable(true);
        Component editorComp = getEditor().getEditorComponent();
        if (editorComp instanceof JTextComponent) {
            TextComponentStandardPopup.addPopupMenuTo((JTextComponent) editorComp, false);
        }
        addKeyListener(this);
    }

    @Override
    public synchronized void addKeyListener(final KeyListener l) {
        super.addKeyListener(l);
        getEditor().getEditorComponent().addKeyListener(this);
    }

    /**
     * @param documentListener
     */
    public synchronized void addDocumentListener(final DocumentListener documentListener) {
        ((JTextComponent) getEditor().getEditorComponent()).getDocument().addDocumentListener(documentListener);
    }

    /**
     * @param historyLength
     *            Maximale Anzahl der Einträge in der History-Liste
     * @param initialListValues
     *            Am Anfang vorhandene Listenwerte
     */

    public HistoryComboBox(final int historyLength, final String... initialListValues) {
        this();
        maxHistoryLength = historyLength;
        if (initialListValues != null) {
            for (int i = 0; i < initialListValues.length; i++) {
                insertItemAt(initialListValues[i], i);
            }
        }
    }

    /**
     * @param initialListValues
     *            Am Anfang vorhandene Listenwerte
     */
    public HistoryComboBox(final String... initialListValues) {
        this(MAX_HISTORY_ENTRIES, initialListValues);
    }

    /**
     * @param historyLength
     *            Maximale Anzahl der Einträge in der History-Liste
     */
    public HistoryComboBox(final int historyLength) {
        this(historyLength, (String[]) null);
    }

    /**
     * Hinzufügen des selektierten Eintrages zur History
     *
     * @param historyComboBox
     */
    public static void addToHistory(final HistoryComboBox historyComboBox) {
        // Bei Suchen über Enter ist das selektierte Objekt noch nicht als selectedObject verfügbar, dann muss über getEditor gegangen werden
        ComboBoxEditor comboBoxEditor = historyComboBox.getEditor();
        JTextComponent textComponent = (JTextComponent) comboBoxEditor.getEditorComponent();
        String text = textComponent.getText();

        int itemIndex = getItemIndex(historyComboBox, text);
        if (itemIndex == 0) {
            return;
        }
        ItemListener[] itemListeners = historyComboBox.getItemListeners();
        for (ItemListener itemListener : itemListeners) {
            historyComboBox.removeItemListener(itemListener);
        }
        if (itemIndex > 0) {
            historyComboBox.removeItemAt(itemIndex);
        }
        historyComboBox.insertItemAt(text, 0);
        for (ItemListener itemListener : itemListeners) {
            historyComboBox.addItemListener(itemListener);
        }
        historyComboBox.setSelectedIndex(0);

        int itemCount = historyComboBox.getItemCount();
        if (itemCount > historyComboBox.maxHistoryLength) {
            historyComboBox.removeItemAt(itemCount - 1);
        }

    }

    /**
     * @param comboBox
     * @param item
     * @return
     */
    private static int getItemIndex(final HistoryComboBox comboBox, final String item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Object itemAtIndex = comboBox.getItemAt(i);
            if (String.valueOf(item).equals(itemAtIndex)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param enterAction the enterAction to set
     */
    public final void setEnterAction(final Action enterAction) {
        this.enterAction = enterAction;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        boolean enterPressed = e.getKeyCode() == KeyEvent.VK_ENTER;
        if (enterAction == null) {
            return;
        }
        if (enterPressed) {
            ActionEvent event = new ActionEvent(this, 0, "");
            enterAction.actionPerformed(event);
            addToHistory(this);
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

}

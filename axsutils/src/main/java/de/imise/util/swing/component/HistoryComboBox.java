package de.imise.util.swing.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

import de.imise.util.swing.component.text.TextComponentStandardPopup;

/**
 * Editierbare JComboBox, die sich in der Liste die lestzetn Eingaben merken kann.
 *
 * @author AXS, STKR
 */
public class HistoryComboBox extends JComboBox implements KeyListener/* , DocumentListener */ {

    /** Konstante für die Maximalzahl der Historyeinträge */
    private static final int MAX_HISTORY_ENTRIES = Integer.MAX_VALUE;

    /*** Maximale Anzahl von Einträgen in der Histotry-Liste */
    private int historyLength = MAX_HISTORY_ENTRIES;

    /** Die Aktion, die beim Drücken von Enrter ausgeführt werden soll */
    private Action enterAction = null;

    private ActionEvent enterActionEvent;

    /**
     * 
     */
    public HistoryComboBox() {
        super();
        setEditable(true);
        Component editorComp = getEditor().getEditorComponent();
        if (editorComp instanceof JTextComponent) {
            TextComponentStandardPopup.addPopupMenuTo((JTextComponent) editorComp, false);
        }
        addKeyListener(this);
        getEditor().getEditorComponent().addKeyListener(this);
        //((JTextComponent)getEditor().getEditorComponent()).getDocument().addDocumentListener(this);
    }

    /**
     * @param historyLength
     *            Maximale Anzahl der Einträge in der History-Liste
     * @param initialListValues
     *            Am Anfang vorhandene Listenwerte
     */

    public HistoryComboBox(final int historyLength, final String... initialListValues) {
        this();
        this.historyLength = historyLength;
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
     * @param jbc
     */
    public static void addToHistory(final HistoryComboBox jbc) {
        // Bei Suchen über Enter ist das selektierte Objekt noch nicht als selectedObject verfügbar, dann muss über getEditor gegangen werden
        jbc.setSelectedItem(((JTextComponent) jbc.getEditor().getEditorComponent()).getText());
        if (jbc.getItemCount() > 0 || jbc.getSelectedObjects().length > 0) {
            boolean found = false;
            for (int i = 0; i < jbc.getItemCount(); i++) {
                if (jbc.getSelectedItem().equals(jbc.getItemAt(i))) {
                    found = true;
                }
            }
            if (!found) {
                //wenn historylänge überschritten, letztes löschen
                if (jbc.historyLength < jbc.getItemCount() + 1) {
                    jbc.removeItemAt(jbc.historyLength - 1);
                }
                jbc.insertItemAt(jbc.getSelectedItem(), 0);
            }
        }
    }

    /**
     * Lösung funktionierte nicht
     */
    @Deprecated
    //	public void addToHistory() {
    //		String selectedItem = (String) getSelectedItem();
    //		if (selectedItem == null)
    //			return;
    //		for (int i = 0; i < getItemCount(); i++) {
    //			if (selectedItem.equals(getItemAt(i))) {
    //				removeItemAt(i);
    //				break;
    //			}
    //		}
    //		insertItemAt(selectedItem, 0);
    //		while (getItemCount() > historyLength)
    //			removeItemAt(getItemCount() - 1);
    //		setSelectedIndex(0);
    //			
    //	}

    /**
     * @param enterAction the enterAction to set
     */
    public final void setEnterAction(final Action enterAction) {
        this.enterAction = enterAction;
    }

    /**
     * @param enterAction the enterAction to set
     */
    public final void setActionEvent(final ActionEvent enterActionEvent) {
        this.enterActionEvent = enterActionEvent;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        if (enterAction == null || enterActionEvent == null) {
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            enterAction.actionPerformed(enterActionEvent);
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

    //	muss erstmal raus, lagt sonst in großen Modellen	
    //	TODO: später debuggen -> Lösung?
    //	@Override
    //	public void insertUpdate(DocumentEvent e) {
    //		fireActionEvent();
    //	}
    //
    //	@Override
    //	public void removeUpdate(DocumentEvent e) {
    //		fireActionEvent();
    //	}
    //
    //	@Override
    //	public void changedUpdate(DocumentEvent e) {
    //		fireActionEvent();
    //	}

}
